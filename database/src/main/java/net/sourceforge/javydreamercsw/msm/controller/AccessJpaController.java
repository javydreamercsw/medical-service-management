package net.sourceforge.javydreamercsw.msm.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import net.sourceforge.javydreamercsw.msm.controller.exceptions.IllegalOrphanException;
import net.sourceforge.javydreamercsw.msm.controller.exceptions.NonexistentEntityException;
import net.sourceforge.javydreamercsw.msm.db.Access;
import net.sourceforge.javydreamercsw.msm.db.Person;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class AccessJpaController implements Serializable {

    public AccessJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Access access) {
        if (access.getPersonList() == null) {
            access.setPersonList(new ArrayList<Person>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Person> attachedPersonList = new ArrayList<Person>();
            for (Person personListPersonToAttach : access.getPersonList()) {
                personListPersonToAttach = em.getReference(personListPersonToAttach.getClass(), personListPersonToAttach.getId());
                attachedPersonList.add(personListPersonToAttach);
            }
            access.setPersonList(attachedPersonList);
            em.persist(access);
            for (Person personListPerson : access.getPersonList()) {
                Access oldAccessIdOfPersonListPerson = personListPerson.getAccessId();
                personListPerson.setAccessId(access);
                personListPerson = em.merge(personListPerson);
                if (oldAccessIdOfPersonListPerson != null) {
                    oldAccessIdOfPersonListPerson.getPersonList().remove(personListPerson);
                    oldAccessIdOfPersonListPerson = em.merge(oldAccessIdOfPersonListPerson);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Access access) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Access persistentAccess = em.find(Access.class, access.getId());
            List<Person> personListOld = persistentAccess.getPersonList();
            List<Person> personListNew = access.getPersonList();
            List<String> illegalOrphanMessages = null;
            for (Person personListOldPerson : personListOld) {
                if (!personListNew.contains(personListOldPerson)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Person " + personListOldPerson + " since its accessId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Person> attachedPersonListNew = new ArrayList<Person>();
            for (Person personListNewPersonToAttach : personListNew) {
                personListNewPersonToAttach = em.getReference(personListNewPersonToAttach.getClass(), personListNewPersonToAttach.getId());
                attachedPersonListNew.add(personListNewPersonToAttach);
            }
            personListNew = attachedPersonListNew;
            access.setPersonList(personListNew);
            access = em.merge(access);
            for (Person personListNewPerson : personListNew) {
                if (!personListOld.contains(personListNewPerson)) {
                    Access oldAccessIdOfPersonListNewPerson = personListNewPerson.getAccessId();
                    personListNewPerson.setAccessId(access);
                    personListNewPerson = em.merge(personListNewPerson);
                    if (oldAccessIdOfPersonListNewPerson != null && !oldAccessIdOfPersonListNewPerson.equals(access)) {
                        oldAccessIdOfPersonListNewPerson.getPersonList().remove(personListNewPerson);
                        oldAccessIdOfPersonListNewPerson = em.merge(oldAccessIdOfPersonListNewPerson);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = access.getId();
                if (findAccess(id) == null) {
                    throw new NonexistentEntityException("The access with id " + id + " no longer exists.");
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
            Access access;
            try {
                access = em.getReference(Access.class, id);
                access.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The access with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Person> personListOrphanCheck = access.getPersonList();
            for (Person personListOrphanCheckPerson : personListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Access (" + access + ") cannot be destroyed since the Person " + personListOrphanCheckPerson + " in its personList field has a non-nullable accessId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(access);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Access> findAccessEntities() {
        return findAccessEntities(true, -1, -1);
    }

    public List<Access> findAccessEntities(int maxResults, int firstResult) {
        return findAccessEntities(false, maxResults, firstResult);
    }

    private List<Access> findAccessEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Access.class));
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

    public Access findAccess(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Access.class, id);
        } finally {
            em.close();
        }
    }

    public int getAccessCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Access> rt = cq.from(Access.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
