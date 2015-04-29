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
import net.sourceforge.javydreamercsw.msm.db.Access;
import net.sourceforge.javydreamercsw.msm.db.PersonHasService;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import net.sourceforge.javydreamercsw.msm.db.Person;
import net.sourceforge.javydreamercsw.msm.controller.exceptions.IllegalOrphanException;
import net.sourceforge.javydreamercsw.msm.controller.exceptions.NonexistentEntityException;
import net.sourceforge.javydreamercsw.msm.controller.exceptions.PreexistingEntityException;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class PersonJpaController implements Serializable {

    public PersonJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Person person) throws PreexistingEntityException, Exception {
        if (person.getPersonHasServiceList() == null) {
            person.setPersonHasServiceList(new ArrayList<PersonHasService>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Access accessId = person.getAccessId();
            if (accessId != null) {
                accessId = em.getReference(accessId.getClass(), accessId.getId());
                person.setAccessId(accessId);
            }
            List<PersonHasService> attachedPersonHasServiceList = new ArrayList<PersonHasService>();
            for (PersonHasService personHasServiceListPersonHasServiceToAttach : person.getPersonHasServiceList()) {
                personHasServiceListPersonHasServiceToAttach = em.getReference(personHasServiceListPersonHasServiceToAttach.getClass(), personHasServiceListPersonHasServiceToAttach.getPersonHasServicePK());
                attachedPersonHasServiceList.add(personHasServiceListPersonHasServiceToAttach);
            }
            person.setPersonHasServiceList(attachedPersonHasServiceList);
            em.persist(person);
            if (accessId != null) {
                accessId.getPersonList().add(person);
                accessId = em.merge(accessId);
            }
            for (PersonHasService personHasServiceListPersonHasService : person.getPersonHasServiceList()) {
                Person oldPersonOfPersonHasServiceListPersonHasService = personHasServiceListPersonHasService.getPerson();
                personHasServiceListPersonHasService.setPerson(person);
                personHasServiceListPersonHasService = em.merge(personHasServiceListPersonHasService);
                if (oldPersonOfPersonHasServiceListPersonHasService != null) {
                    oldPersonOfPersonHasServiceListPersonHasService.getPersonHasServiceList().remove(personHasServiceListPersonHasService);
                    oldPersonOfPersonHasServiceListPersonHasService = em.merge(oldPersonOfPersonHasServiceListPersonHasService);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findPerson(person.getId()) != null) {
                throw new PreexistingEntityException("Person " + person + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Person person) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Person persistentPerson = em.find(Person.class, person.getId());
            Access accessIdOld = persistentPerson.getAccessId();
            Access accessIdNew = person.getAccessId();
            List<PersonHasService> personHasServiceListOld = persistentPerson.getPersonHasServiceList();
            List<PersonHasService> personHasServiceListNew = person.getPersonHasServiceList();
            List<String> illegalOrphanMessages = null;
            for (PersonHasService personHasServiceListOldPersonHasService : personHasServiceListOld) {
                if (!personHasServiceListNew.contains(personHasServiceListOldPersonHasService)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain PersonHasService " + personHasServiceListOldPersonHasService + " since its person field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (accessIdNew != null) {
                accessIdNew = em.getReference(accessIdNew.getClass(), accessIdNew.getId());
                person.setAccessId(accessIdNew);
            }
            List<PersonHasService> attachedPersonHasServiceListNew = new ArrayList<PersonHasService>();
            for (PersonHasService personHasServiceListNewPersonHasServiceToAttach : personHasServiceListNew) {
                personHasServiceListNewPersonHasServiceToAttach = em.getReference(personHasServiceListNewPersonHasServiceToAttach.getClass(), personHasServiceListNewPersonHasServiceToAttach.getPersonHasServicePK());
                attachedPersonHasServiceListNew.add(personHasServiceListNewPersonHasServiceToAttach);
            }
            personHasServiceListNew = attachedPersonHasServiceListNew;
            person.setPersonHasServiceList(personHasServiceListNew);
            person = em.merge(person);
            if (accessIdOld != null && !accessIdOld.equals(accessIdNew)) {
                accessIdOld.getPersonList().remove(person);
                accessIdOld = em.merge(accessIdOld);
            }
            if (accessIdNew != null && !accessIdNew.equals(accessIdOld)) {
                accessIdNew.getPersonList().add(person);
                accessIdNew = em.merge(accessIdNew);
            }
            for (PersonHasService personHasServiceListNewPersonHasService : personHasServiceListNew) {
                if (!personHasServiceListOld.contains(personHasServiceListNewPersonHasService)) {
                    Person oldPersonOfPersonHasServiceListNewPersonHasService = personHasServiceListNewPersonHasService.getPerson();
                    personHasServiceListNewPersonHasService.setPerson(person);
                    personHasServiceListNewPersonHasService = em.merge(personHasServiceListNewPersonHasService);
                    if (oldPersonOfPersonHasServiceListNewPersonHasService != null && !oldPersonOfPersonHasServiceListNewPersonHasService.equals(person)) {
                        oldPersonOfPersonHasServiceListNewPersonHasService.getPersonHasServiceList().remove(personHasServiceListNewPersonHasService);
                        oldPersonOfPersonHasServiceListNewPersonHasService = em.merge(oldPersonOfPersonHasServiceListNewPersonHasService);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = person.getId();
                if (findPerson(id) == null) {
                    throw new NonexistentEntityException("The person with id " + id + " no longer exists.");
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
            Person person;
            try {
                person = em.getReference(Person.class, id);
                person.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The person with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<PersonHasService> personHasServiceListOrphanCheck = person.getPersonHasServiceList();
            for (PersonHasService personHasServiceListOrphanCheckPersonHasService : personHasServiceListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Person (" + person + ") cannot be destroyed since the PersonHasService " + personHasServiceListOrphanCheckPersonHasService + " in its personHasServiceList field has a non-nullable person field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Access accessId = person.getAccessId();
            if (accessId != null) {
                accessId.getPersonList().remove(person);
                accessId = em.merge(accessId);
            }
            em.remove(person);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Person> findPersonEntities() {
        return findPersonEntities(true, -1, -1);
    }

    public List<Person> findPersonEntities(int maxResults, int firstResult) {
        return findPersonEntities(false, maxResults, firstResult);
    }

    private List<Person> findPersonEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Person.class));
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

    public Person findPerson(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Person.class, id);
        } finally {
            em.close();
        }
    }

    public int getPersonCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Person> rt = cq.from(Person.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
