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
import net.sourceforge.javydreamercsw.msm.db.City;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import net.sourceforge.javydreamercsw.msm.controller.exceptions.IllegalOrphanException;
import net.sourceforge.javydreamercsw.msm.controller.exceptions.NonexistentEntityException;
import net.sourceforge.javydreamercsw.msm.db.Country;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class CountryJpaController implements Serializable {

    public CountryJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Country country) {
        if (country.getCityList() == null) {
            country.setCityList(new ArrayList<City>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<City> attachedCityList = new ArrayList<City>();
            for (City cityListCityToAttach : country.getCityList()) {
                cityListCityToAttach = em.getReference(cityListCityToAttach.getClass(), cityListCityToAttach.getId());
                attachedCityList.add(cityListCityToAttach);
            }
            country.setCityList(attachedCityList);
            em.persist(country);
            for (City cityListCity : country.getCityList()) {
                Country oldCountryIdOfCityListCity = cityListCity.getCountryId();
                cityListCity.setCountryId(country);
                cityListCity = em.merge(cityListCity);
                if (oldCountryIdOfCityListCity != null) {
                    oldCountryIdOfCityListCity.getCityList().remove(cityListCity);
                    oldCountryIdOfCityListCity = em.merge(oldCountryIdOfCityListCity);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Country country) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Country persistentCountry = em.find(Country.class, country.getId());
            List<City> cityListOld = persistentCountry.getCityList();
            List<City> cityListNew = country.getCityList();
            List<String> illegalOrphanMessages = null;
            for (City cityListOldCity : cityListOld) {
                if (!cityListNew.contains(cityListOldCity)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain City " + cityListOldCity + " since its countryId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<City> attachedCityListNew = new ArrayList<City>();
            for (City cityListNewCityToAttach : cityListNew) {
                cityListNewCityToAttach = em.getReference(cityListNewCityToAttach.getClass(), cityListNewCityToAttach.getId());
                attachedCityListNew.add(cityListNewCityToAttach);
            }
            cityListNew = attachedCityListNew;
            country.setCityList(cityListNew);
            country = em.merge(country);
            for (City cityListNewCity : cityListNew) {
                if (!cityListOld.contains(cityListNewCity)) {
                    Country oldCountryIdOfCityListNewCity = cityListNewCity.getCountryId();
                    cityListNewCity.setCountryId(country);
                    cityListNewCity = em.merge(cityListNewCity);
                    if (oldCountryIdOfCityListNewCity != null && !oldCountryIdOfCityListNewCity.equals(country)) {
                        oldCountryIdOfCityListNewCity.getCityList().remove(cityListNewCity);
                        oldCountryIdOfCityListNewCity = em.merge(oldCountryIdOfCityListNewCity);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = country.getId();
                if (findCountry(id) == null) {
                    throw new NonexistentEntityException("The country with id " + id + " no longer exists.");
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
            Country country;
            try {
                country = em.getReference(Country.class, id);
                country.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The country with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<City> cityListOrphanCheck = country.getCityList();
            for (City cityListOrphanCheckCity : cityListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Country (" + country + ") cannot be destroyed since the City " + cityListOrphanCheckCity + " in its cityList field has a non-nullable countryId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(country);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Country> findCountryEntities() {
        return findCountryEntities(true, -1, -1);
    }

    public List<Country> findCountryEntities(int maxResults, int firstResult) {
        return findCountryEntities(false, maxResults, firstResult);
    }

    private List<Country> findCountryEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Country.class));
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

    public Country findCountry(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Country.class, id);
        } finally {
            em.close();
        }
    }

    public int getCountryCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Country> rt = cq.from(Country.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
