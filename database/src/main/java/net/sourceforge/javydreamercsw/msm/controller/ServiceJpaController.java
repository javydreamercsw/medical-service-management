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
import net.sourceforge.javydreamercsw.msm.db.TMField;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import net.sourceforge.javydreamercsw.msm.controller.exceptions.IllegalOrphanException;
import net.sourceforge.javydreamercsw.msm.controller.exceptions.NonexistentEntityException;
import net.sourceforge.javydreamercsw.msm.db.PersonHasService;
import net.sourceforge.javydreamercsw.msm.db.Service;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class ServiceJpaController implements Serializable {

    public ServiceJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Service service) {
        if (service.getTmfieldList() == null) {
            service.setTmfieldList(new ArrayList<TMField>());
        }
        if (service.getPersonHasServiceList() == null) {
            service.setPersonHasServiceList(new ArrayList<PersonHasService>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<TMField> attachedTmfieldList = new ArrayList<TMField>();
            for (TMField tmfieldListTMFieldToAttach : service.getTmfieldList()) {
                tmfieldListTMFieldToAttach = em.getReference(tmfieldListTMFieldToAttach.getClass(), tmfieldListTMFieldToAttach.getId());
                attachedTmfieldList.add(tmfieldListTMFieldToAttach);
            }
            service.setTmfieldList(attachedTmfieldList);
            List<PersonHasService> attachedPersonHasServiceList = new ArrayList<PersonHasService>();
            for (PersonHasService personHasServiceListPersonHasServiceToAttach : service.getPersonHasServiceList()) {
                personHasServiceListPersonHasServiceToAttach = em.getReference(personHasServiceListPersonHasServiceToAttach.getClass(), personHasServiceListPersonHasServiceToAttach.getPersonHasServicePK());
                attachedPersonHasServiceList.add(personHasServiceListPersonHasServiceToAttach);
            }
            service.setPersonHasServiceList(attachedPersonHasServiceList);
            em.persist(service);
            for (TMField tmfieldListTMField : service.getTmfieldList()) {
                tmfieldListTMField.getServiceList().add(service);
                tmfieldListTMField = em.merge(tmfieldListTMField);
            }
            for (PersonHasService personHasServiceListPersonHasService : service.getPersonHasServiceList()) {
                Service oldServiceOfPersonHasServiceListPersonHasService = personHasServiceListPersonHasService.getService();
                personHasServiceListPersonHasService.setService(service);
                personHasServiceListPersonHasService = em.merge(personHasServiceListPersonHasService);
                if (oldServiceOfPersonHasServiceListPersonHasService != null) {
                    oldServiceOfPersonHasServiceListPersonHasService.getPersonHasServiceList().remove(personHasServiceListPersonHasService);
                    oldServiceOfPersonHasServiceListPersonHasService = em.merge(oldServiceOfPersonHasServiceListPersonHasService);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Service service) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Service persistentService = em.find(Service.class, service.getId());
            List<TMField> tmfieldListOld = persistentService.getTmfieldList();
            List<TMField> tmfieldListNew = service.getTmfieldList();
            List<PersonHasService> personHasServiceListOld = persistentService.getPersonHasServiceList();
            List<PersonHasService> personHasServiceListNew = service.getPersonHasServiceList();
            List<String> illegalOrphanMessages = null;
            for (PersonHasService personHasServiceListOldPersonHasService : personHasServiceListOld) {
                if (!personHasServiceListNew.contains(personHasServiceListOldPersonHasService)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain PersonHasService " + personHasServiceListOldPersonHasService + " since its service field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<TMField> attachedTmfieldListNew = new ArrayList<TMField>();
            for (TMField tmfieldListNewTMFieldToAttach : tmfieldListNew) {
                tmfieldListNewTMFieldToAttach = em.getReference(tmfieldListNewTMFieldToAttach.getClass(), tmfieldListNewTMFieldToAttach.getId());
                attachedTmfieldListNew.add(tmfieldListNewTMFieldToAttach);
            }
            tmfieldListNew = attachedTmfieldListNew;
            service.setTmfieldList(tmfieldListNew);
            List<PersonHasService> attachedPersonHasServiceListNew = new ArrayList<PersonHasService>();
            for (PersonHasService personHasServiceListNewPersonHasServiceToAttach : personHasServiceListNew) {
                personHasServiceListNewPersonHasServiceToAttach = em.getReference(personHasServiceListNewPersonHasServiceToAttach.getClass(), personHasServiceListNewPersonHasServiceToAttach.getPersonHasServicePK());
                attachedPersonHasServiceListNew.add(personHasServiceListNewPersonHasServiceToAttach);
            }
            personHasServiceListNew = attachedPersonHasServiceListNew;
            service.setPersonHasServiceList(personHasServiceListNew);
            service = em.merge(service);
            for (TMField tmfieldListOldTMField : tmfieldListOld) {
                if (!tmfieldListNew.contains(tmfieldListOldTMField)) {
                    tmfieldListOldTMField.getServiceList().remove(service);
                    tmfieldListOldTMField = em.merge(tmfieldListOldTMField);
                }
            }
            for (TMField tmfieldListNewTMField : tmfieldListNew) {
                if (!tmfieldListOld.contains(tmfieldListNewTMField)) {
                    tmfieldListNewTMField.getServiceList().add(service);
                    tmfieldListNewTMField = em.merge(tmfieldListNewTMField);
                }
            }
            for (PersonHasService personHasServiceListNewPersonHasService : personHasServiceListNew) {
                if (!personHasServiceListOld.contains(personHasServiceListNewPersonHasService)) {
                    Service oldServiceOfPersonHasServiceListNewPersonHasService = personHasServiceListNewPersonHasService.getService();
                    personHasServiceListNewPersonHasService.setService(service);
                    personHasServiceListNewPersonHasService = em.merge(personHasServiceListNewPersonHasService);
                    if (oldServiceOfPersonHasServiceListNewPersonHasService != null && !oldServiceOfPersonHasServiceListNewPersonHasService.equals(service)) {
                        oldServiceOfPersonHasServiceListNewPersonHasService.getPersonHasServiceList().remove(personHasServiceListNewPersonHasService);
                        oldServiceOfPersonHasServiceListNewPersonHasService = em.merge(oldServiceOfPersonHasServiceListNewPersonHasService);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = service.getId();
                if (findService(id) == null) {
                    throw new NonexistentEntityException("The service with id " + id + " no longer exists.");
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
            Service service;
            try {
                service = em.getReference(Service.class, id);
                service.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The service with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<PersonHasService> personHasServiceListOrphanCheck = service.getPersonHasServiceList();
            for (PersonHasService personHasServiceListOrphanCheckPersonHasService : personHasServiceListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Service (" + service + ") cannot be destroyed since the PersonHasService " + personHasServiceListOrphanCheckPersonHasService + " in its personHasServiceList field has a non-nullable service field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<TMField> tmfieldList = service.getTmfieldList();
            for (TMField tmfieldListTMField : tmfieldList) {
                tmfieldListTMField.getServiceList().remove(service);
                tmfieldListTMField = em.merge(tmfieldListTMField);
            }
            em.remove(service);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Service> findServiceEntities() {
        return findServiceEntities(true, -1, -1);
    }

    public List<Service> findServiceEntities(int maxResults, int firstResult) {
        return findServiceEntities(false, maxResults, firstResult);
    }

    private List<Service> findServiceEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Service.class));
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

    public Service findService(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Service.class, id);
        } finally {
            em.close();
        }
    }

    public int getServiceCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Service> rt = cq.from(Service.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
}
