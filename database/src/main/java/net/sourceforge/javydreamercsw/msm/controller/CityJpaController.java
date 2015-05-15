/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sourceforge.javydreamercsw.msm.controller;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import net.sourceforge.javydreamercsw.msm.db.Country;
import net.sourceforge.javydreamercsw.msm.db.Address;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import net.sourceforge.javydreamercsw.msm.controller.exceptions.IllegalOrphanException;
import net.sourceforge.javydreamercsw.msm.controller.exceptions.NonexistentEntityException;
import net.sourceforge.javydreamercsw.msm.db.City;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class CityJpaController implements Serializable {

    public CityJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(City city) {
        if (city.getAddressList() == null) {
            city.setAddressList(new ArrayList<Address>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Country countryId = city.getCountryId();
            if (countryId != null) {
                countryId = em.getReference(countryId.getClass(), countryId.getId());
                city.setCountryId(countryId);
            }
            List<Address> attachedAddressList = new ArrayList<Address>();
            for (Address addressListAddressToAttach : city.getAddressList()) {
                addressListAddressToAttach = em.getReference(addressListAddressToAttach.getClass(), addressListAddressToAttach.getId());
                attachedAddressList.add(addressListAddressToAttach);
            }
            city.setAddressList(attachedAddressList);
            em.persist(city);
            if (countryId != null) {
                countryId.getCityList().add(city);
                countryId = em.merge(countryId);
            }
            for (Address addressListAddress : city.getAddressList()) {
                City oldCityIdOfAddressListAddress = addressListAddress.getCityId();
                addressListAddress.setCityId(city);
                addressListAddress = em.merge(addressListAddress);
                if (oldCityIdOfAddressListAddress != null) {
                    oldCityIdOfAddressListAddress.getAddressList().remove(addressListAddress);
                    oldCityIdOfAddressListAddress = em.merge(oldCityIdOfAddressListAddress);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(City city) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            City persistentCity = em.find(City.class, city.getId());
            Country countryIdOld = persistentCity.getCountryId();
            Country countryIdNew = city.getCountryId();
            List<Address> addressListOld = persistentCity.getAddressList();
            List<Address> addressListNew = city.getAddressList();
            List<String> illegalOrphanMessages = null;
            for (Address addressListOldAddress : addressListOld) {
                if (!addressListNew.contains(addressListOldAddress)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Address " + addressListOldAddress + " since its cityId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (countryIdNew != null) {
                countryIdNew = em.getReference(countryIdNew.getClass(), countryIdNew.getId());
                city.setCountryId(countryIdNew);
            }
            List<Address> attachedAddressListNew = new ArrayList<Address>();
            for (Address addressListNewAddressToAttach : addressListNew) {
                addressListNewAddressToAttach = em.getReference(addressListNewAddressToAttach.getClass(), addressListNewAddressToAttach.getId());
                attachedAddressListNew.add(addressListNewAddressToAttach);
            }
            addressListNew = attachedAddressListNew;
            city.setAddressList(addressListNew);
            city = em.merge(city);
            if (countryIdOld != null && !countryIdOld.equals(countryIdNew)) {
                countryIdOld.getCityList().remove(city);
                countryIdOld = em.merge(countryIdOld);
            }
            if (countryIdNew != null && !countryIdNew.equals(countryIdOld)) {
                countryIdNew.getCityList().add(city);
                countryIdNew = em.merge(countryIdNew);
            }
            for (Address addressListNewAddress : addressListNew) {
                if (!addressListOld.contains(addressListNewAddress)) {
                    City oldCityIdOfAddressListNewAddress = addressListNewAddress.getCityId();
                    addressListNewAddress.setCityId(city);
                    addressListNewAddress = em.merge(addressListNewAddress);
                    if (oldCityIdOfAddressListNewAddress != null && !oldCityIdOfAddressListNewAddress.equals(city)) {
                        oldCityIdOfAddressListNewAddress.getAddressList().remove(addressListNewAddress);
                        oldCityIdOfAddressListNewAddress = em.merge(oldCityIdOfAddressListNewAddress);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = city.getId();
                if (findCity(id) == null) {
                    throw new NonexistentEntityException("The city with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            City city;
            try {
                city = em.getReference(City.class, id);
                city.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The city with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Address> addressListOrphanCheck = city.getAddressList();
            for (Address addressListOrphanCheckAddress : addressListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This City (" + city + ") cannot be destroyed since the Address " + addressListOrphanCheckAddress + " in its addressList field has a non-nullable cityId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Country countryId = city.getCountryId();
            if (countryId != null) {
                countryId.getCityList().remove(city);
                countryId = em.merge(countryId);
            }
            em.remove(city);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<City> findCityEntities() {
        return findCityEntities(true, -1, -1);
    }

    public List<City> findCityEntities(int maxResults, int firstResult) {
        return findCityEntities(false, maxResults, firstResult);
    }

    private List<City> findCityEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(City.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public City findCity(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(City.class, id);
        } finally {
            em.close();
        }
    }

    public int getCityCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<City> rt = cq.from(City.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
