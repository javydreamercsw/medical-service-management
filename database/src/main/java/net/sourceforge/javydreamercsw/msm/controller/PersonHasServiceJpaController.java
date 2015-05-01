/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sourceforge.javydreamercsw.msm.controller;

import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import net.sourceforge.javydreamercsw.msm.controller.exceptions.NonexistentEntityException;
import net.sourceforge.javydreamercsw.msm.controller.exceptions.PreexistingEntityException;
import net.sourceforge.javydreamercsw.msm.db.ServiceInstance;
import net.sourceforge.javydreamercsw.msm.db.Person;
import net.sourceforge.javydreamercsw.msm.db.PersonHasService;
import net.sourceforge.javydreamercsw.msm.db.PersonHasServicePK;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class PersonHasServiceJpaController implements Serializable {

    public PersonHasServiceJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(PersonHasService personHasService) throws PreexistingEntityException, Exception {
        if (personHasService.getPersonHasServicePK() == null) {
            personHasService.setPersonHasServicePK(new PersonHasServicePK());
        }
        personHasService.getPersonHasServicePK().setPersonId(personHasService.getPerson().getId());
        personHasService.getPersonHasServicePK().setServiceInstanceId(personHasService.getServiceInstance().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            ServiceInstance serviceInstance = personHasService.getServiceInstance();
            if (serviceInstance != null) {
                serviceInstance = em.getReference(serviceInstance.getClass(), serviceInstance.getId());
                personHasService.setServiceInstance(serviceInstance);
            }
            Person person = personHasService.getPerson();
            if (person != null) {
                person = em.getReference(person.getClass(), person.getId());
                personHasService.setPerson(person);
            }
            em.persist(personHasService);
            if (serviceInstance != null) {
                serviceInstance.getPersonHasServiceList().add(personHasService);
                serviceInstance = em.merge(serviceInstance);
            }
            if (person != null) {
                person.getPersonHasServiceList().add(personHasService);
                person = em.merge(person);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findPersonHasService(personHasService.getPersonHasServicePK()) != null) {
                throw new PreexistingEntityException("PersonHasService " + personHasService + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(PersonHasService personHasService) throws NonexistentEntityException, Exception {
        personHasService.getPersonHasServicePK().setPersonId(personHasService.getPerson().getId());
        personHasService.getPersonHasServicePK().setServiceInstanceId(personHasService.getServiceInstance().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            PersonHasService persistentPersonHasService = em.find(PersonHasService.class, personHasService.getPersonHasServicePK());
            ServiceInstance serviceInstanceOld = persistentPersonHasService.getServiceInstance();
            ServiceInstance serviceInstanceNew = personHasService.getServiceInstance();
            Person personOld = persistentPersonHasService.getPerson();
            Person personNew = personHasService.getPerson();
            if (serviceInstanceNew != null) {
                serviceInstanceNew = em.getReference(serviceInstanceNew.getClass(), serviceInstanceNew.getId());
                personHasService.setServiceInstance(serviceInstanceNew);
            }
            if (personNew != null) {
                personNew = em.getReference(personNew.getClass(), personNew.getId());
                personHasService.setPerson(personNew);
            }
            personHasService = em.merge(personHasService);
            if (serviceInstanceOld != null && !serviceInstanceOld.equals(serviceInstanceNew)) {
                serviceInstanceOld.getPersonHasServiceList().remove(personHasService);
                serviceInstanceOld = em.merge(serviceInstanceOld);
            }
            if (serviceInstanceNew != null && !serviceInstanceNew.equals(serviceInstanceOld)) {
                serviceInstanceNew.getPersonHasServiceList().add(personHasService);
                serviceInstanceNew = em.merge(serviceInstanceNew);
            }
            if (personOld != null && !personOld.equals(personNew)) {
                personOld.getPersonHasServiceList().remove(personHasService);
                personOld = em.merge(personOld);
            }
            if (personNew != null && !personNew.equals(personOld)) {
                personNew.getPersonHasServiceList().add(personHasService);
                personNew = em.merge(personNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                PersonHasServicePK id = personHasService.getPersonHasServicePK();
                if (findPersonHasService(id) == null) {
                    throw new NonexistentEntityException("The personHasService with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(PersonHasServicePK id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            PersonHasService personHasService;
            try {
                personHasService = em.getReference(PersonHasService.class, id);
                personHasService.getPersonHasServicePK();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The personHasService with id " + id + " no longer exists.", enfe);
            }
            ServiceInstance serviceInstance = personHasService.getServiceInstance();
            if (serviceInstance != null) {
                serviceInstance.getPersonHasServiceList().remove(personHasService);
                serviceInstance = em.merge(serviceInstance);
            }
            Person person = personHasService.getPerson();
            if (person != null) {
                person.getPersonHasServiceList().remove(personHasService);
                person = em.merge(person);
            }
            em.remove(personHasService);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<PersonHasService> findPersonHasServiceEntities() {
        return findPersonHasServiceEntities(true, -1, -1);
    }

    public List<PersonHasService> findPersonHasServiceEntities(int maxResults, int firstResult) {
        return findPersonHasServiceEntities(false, maxResults, firstResult);
    }

    private List<PersonHasService> findPersonHasServiceEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(PersonHasService.class));
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

    public PersonHasService findPersonHasService(PersonHasServicePK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(PersonHasService.class, id);
        } finally {
            em.close();
        }
    }

    public int getPersonHasServiceCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<PersonHasService> rt = cq.from(PersonHasService.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
