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
import net.sourceforge.javydreamercsw.msm.db.Person;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import net.sourceforge.javydreamercsw.msm.controller.exceptions.IllegalOrphanException;
import net.sourceforge.javydreamercsw.msm.controller.exceptions.NonexistentEntityException;
import net.sourceforge.javydreamercsw.msm.db.Address;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class AddressJpaController implements Serializable {

    public AddressJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Address address) {
        if (address.getPersonList() == null) {
            address.setPersonList(new ArrayList<Person>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            City cityId = address.getCityId();
            if (cityId != null) {
                cityId = em.getReference(cityId.getClass(), cityId.getId());
                address.setCityId(cityId);
            }
            List<Person> attachedPersonList = new ArrayList<Person>();
            for (Person personListPersonToAttach : address.getPersonList()) {
                personListPersonToAttach = em.getReference(personListPersonToAttach.getClass(), personListPersonToAttach.getId());
                attachedPersonList.add(personListPersonToAttach);
            }
            address.setPersonList(attachedPersonList);
            em.persist(address);
            if (cityId != null) {
                cityId.getAddressList().add(address);
                cityId = em.merge(cityId);
            }
            for (Person personListPerson : address.getPersonList()) {
                Address oldAddressIdOfPersonListPerson = personListPerson.getAddressId();
                personListPerson.setAddressId(address);
                personListPerson = em.merge(personListPerson);
                if (oldAddressIdOfPersonListPerson != null) {
                    oldAddressIdOfPersonListPerson.getPersonList().remove(personListPerson);
                    oldAddressIdOfPersonListPerson = em.merge(oldAddressIdOfPersonListPerson);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Address address) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Address persistentAddress = em.find(Address.class, address.getId());
            City cityIdOld = persistentAddress.getCityId();
            City cityIdNew = address.getCityId();
            List<Person> personListOld = persistentAddress.getPersonList();
            List<Person> personListNew = address.getPersonList();
            List<String> illegalOrphanMessages = null;
            for (Person personListOldPerson : personListOld) {
                if (!personListNew.contains(personListOldPerson)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Person " + personListOldPerson + " since its addressId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (cityIdNew != null) {
                cityIdNew = em.getReference(cityIdNew.getClass(), cityIdNew.getId());
                address.setCityId(cityIdNew);
            }
            List<Person> attachedPersonListNew = new ArrayList<Person>();
            for (Person personListNewPersonToAttach : personListNew) {
                personListNewPersonToAttach = em.getReference(personListNewPersonToAttach.getClass(), personListNewPersonToAttach.getId());
                attachedPersonListNew.add(personListNewPersonToAttach);
            }
            personListNew = attachedPersonListNew;
            address.setPersonList(personListNew);
            address = em.merge(address);
            if (cityIdOld != null && !cityIdOld.equals(cityIdNew)) {
                cityIdOld.getAddressList().remove(address);
                cityIdOld = em.merge(cityIdOld);
            }
            if (cityIdNew != null && !cityIdNew.equals(cityIdOld)) {
                cityIdNew.getAddressList().add(address);
                cityIdNew = em.merge(cityIdNew);
            }
            for (Person personListNewPerson : personListNew) {
                if (!personListOld.contains(personListNewPerson)) {
                    Address oldAddressIdOfPersonListNewPerson = personListNewPerson.getAddressId();
                    personListNewPerson.setAddressId(address);
                    personListNewPerson = em.merge(personListNewPerson);
                    if (oldAddressIdOfPersonListNewPerson != null && !oldAddressIdOfPersonListNewPerson.equals(address)) {
                        oldAddressIdOfPersonListNewPerson.getPersonList().remove(personListNewPerson);
                        oldAddressIdOfPersonListNewPerson = em.merge(oldAddressIdOfPersonListNewPerson);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = address.getId();
                if (findAddress(id) == null) {
                    throw new NonexistentEntityException("The address with id " + id + " no longer exists.");
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
            Address address;
            try {
                address = em.getReference(Address.class, id);
                address.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The address with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Person> personListOrphanCheck = address.getPersonList();
            for (Person personListOrphanCheckPerson : personListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Address (" + address + ") cannot be destroyed since the Person " + personListOrphanCheckPerson + " in its personList field has a non-nullable addressId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            City cityId = address.getCityId();
            if (cityId != null) {
                cityId.getAddressList().remove(address);
                cityId = em.merge(cityId);
            }
            em.remove(address);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Address> findAddressEntities() {
        return findAddressEntities(true, -1, -1);
    }

    public List<Address> findAddressEntities(int maxResults, int firstResult) {
        return findAddressEntities(false, maxResults, firstResult);
    }

    private List<Address> findAddressEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Address.class));
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

    public Address findAddress(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Address.class, id);
        } finally {
            em.close();
        }
    }

    public int getAddressCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Address> rt = cq.from(Address.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
