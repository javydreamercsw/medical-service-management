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
import net.sourceforge.javydreamercsw.msm.db.Person;
import net.sourceforge.javydreamercsw.msm.db.PersonHasService;
import net.sourceforge.javydreamercsw.msm.db.PersonHasServicePK;
import net.sourceforge.javydreamercsw.msm.db.Service;
import net.sourceforge.javydreamercsw.msm.controller.exceptions.NonexistentEntityException;
import net.sourceforge.javydreamercsw.msm.controller.exceptions.PreexistingEntityException;

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
        personHasService.getPersonHasServicePK().setServiceid(personHasService.getService().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Person person = personHasService.getPerson();
            if (person != null) {
                person = em.getReference(person.getClass(), person.getId());
                personHasService.setPerson(person);
            }
            Service service = personHasService.getService();
            if (service != null) {
                service = em.getReference(service.getClass(), service.getId());
                personHasService.setService(service);
            }
            em.persist(personHasService);
            if (person != null) {
                person.getPersonHasServiceList().add(personHasService);
                person = em.merge(person);
            }
            if (service != null) {
                service.getPersonHasServiceList().add(personHasService);
                service = em.merge(service);
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
        personHasService.getPersonHasServicePK().setServiceid(personHasService.getService().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            PersonHasService persistentPersonHasService = em.find(PersonHasService.class, personHasService.getPersonHasServicePK());
            Person personOld = persistentPersonHasService.getPerson();
            Person personNew = personHasService.getPerson();
            Service serviceOld = persistentPersonHasService.getService();
            Service serviceNew = personHasService.getService();
            if (personNew != null) {
                personNew = em.getReference(personNew.getClass(), personNew.getId());
                personHasService.setPerson(personNew);
            }
            if (serviceNew != null) {
                serviceNew = em.getReference(serviceNew.getClass(), serviceNew.getId());
                personHasService.setService(serviceNew);
            }
            personHasService = em.merge(personHasService);
            if (personOld != null && !personOld.equals(personNew)) {
                personOld.getPersonHasServiceList().remove(personHasService);
                personOld = em.merge(personOld);
            }
            if (personNew != null && !personNew.equals(personOld)) {
                personNew.getPersonHasServiceList().add(personHasService);
                personNew = em.merge(personNew);
            }
            if (serviceOld != null && !serviceOld.equals(serviceNew)) {
                serviceOld.getPersonHasServiceList().remove(personHasService);
                serviceOld = em.merge(serviceOld);
            }
            if (serviceNew != null && !serviceNew.equals(serviceOld)) {
                serviceNew.getPersonHasServiceList().add(personHasService);
                serviceNew = em.merge(serviceNew);
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
            Person person = personHasService.getPerson();
            if (person != null) {
                person.getPersonHasServiceList().remove(personHasService);
                person = em.merge(person);
            }
            Service service = personHasService.getService();
            if (service != null) {
                service.getPersonHasServiceList().remove(personHasService);
                service = em.merge(service);
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
