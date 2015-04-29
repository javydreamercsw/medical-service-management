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
import net.sourceforge.javydreamercsw.msm.db.InstanceField;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import net.sourceforge.javydreamercsw.msm.controller.exceptions.IllegalOrphanException;
import net.sourceforge.javydreamercsw.msm.controller.exceptions.NonexistentEntityException;
import net.sourceforge.javydreamercsw.msm.controller.exceptions.PreexistingEntityException;
import net.sourceforge.javydreamercsw.msm.db.PersonHasService;
import net.sourceforge.javydreamercsw.msm.db.ServiceInstance;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class ServiceInstanceJpaController implements Serializable {

    public ServiceInstanceJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(ServiceInstance serviceInstance) throws PreexistingEntityException, Exception {
        if (serviceInstance.getInstanceFieldList() == null) {
            serviceInstance.setInstanceFieldList(new ArrayList<InstanceField>());
        }
        if (serviceInstance.getPersonHasServiceList() == null) {
            serviceInstance.setPersonHasServiceList(new ArrayList<PersonHasService>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<InstanceField> attachedInstanceFieldList = new ArrayList<InstanceField>();
            for (InstanceField instanceFieldListInstanceFieldToAttach : serviceInstance.getInstanceFieldList()) {
                instanceFieldListInstanceFieldToAttach = em.getReference(instanceFieldListInstanceFieldToAttach.getClass(), instanceFieldListInstanceFieldToAttach.getInstanceFieldPK());
                attachedInstanceFieldList.add(instanceFieldListInstanceFieldToAttach);
            }
            serviceInstance.setInstanceFieldList(attachedInstanceFieldList);
            List<PersonHasService> attachedPersonHasServiceList = new ArrayList<PersonHasService>();
            for (PersonHasService personHasServiceListPersonHasServiceToAttach : serviceInstance.getPersonHasServiceList()) {
                personHasServiceListPersonHasServiceToAttach = em.getReference(personHasServiceListPersonHasServiceToAttach.getClass(), personHasServiceListPersonHasServiceToAttach.getPersonHasServicePK());
                attachedPersonHasServiceList.add(personHasServiceListPersonHasServiceToAttach);
            }
            serviceInstance.setPersonHasServiceList(attachedPersonHasServiceList);
            em.persist(serviceInstance);
            for (InstanceField instanceFieldListInstanceField : serviceInstance.getInstanceFieldList()) {
                ServiceInstance oldServiceInstanceOfInstanceFieldListInstanceField = instanceFieldListInstanceField.getServiceInstance();
                instanceFieldListInstanceField.setServiceInstance(serviceInstance);
                instanceFieldListInstanceField = em.merge(instanceFieldListInstanceField);
                if (oldServiceInstanceOfInstanceFieldListInstanceField != null) {
                    oldServiceInstanceOfInstanceFieldListInstanceField.getInstanceFieldList().remove(instanceFieldListInstanceField);
                    oldServiceInstanceOfInstanceFieldListInstanceField = em.merge(oldServiceInstanceOfInstanceFieldListInstanceField);
                }
            }
            for (PersonHasService personHasServiceListPersonHasService : serviceInstance.getPersonHasServiceList()) {
                ServiceInstance oldServiceInstanceOfPersonHasServiceListPersonHasService = personHasServiceListPersonHasService.getServiceInstance();
                personHasServiceListPersonHasService.setServiceInstance(serviceInstance);
                personHasServiceListPersonHasService = em.merge(personHasServiceListPersonHasService);
                if (oldServiceInstanceOfPersonHasServiceListPersonHasService != null) {
                    oldServiceInstanceOfPersonHasServiceListPersonHasService.getPersonHasServiceList().remove(personHasServiceListPersonHasService);
                    oldServiceInstanceOfPersonHasServiceListPersonHasService = em.merge(oldServiceInstanceOfPersonHasServiceListPersonHasService);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findServiceInstance(serviceInstance.getId()) != null) {
                throw new PreexistingEntityException("ServiceInstance " + serviceInstance + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(ServiceInstance serviceInstance) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            ServiceInstance persistentServiceInstance = em.find(ServiceInstance.class, serviceInstance.getId());
            List<InstanceField> instanceFieldListOld = persistentServiceInstance.getInstanceFieldList();
            List<InstanceField> instanceFieldListNew = serviceInstance.getInstanceFieldList();
            List<PersonHasService> personHasServiceListOld = persistentServiceInstance.getPersonHasServiceList();
            List<PersonHasService> personHasServiceListNew = serviceInstance.getPersonHasServiceList();
            List<String> illegalOrphanMessages = null;
            for (InstanceField instanceFieldListOldInstanceField : instanceFieldListOld) {
                if (!instanceFieldListNew.contains(instanceFieldListOldInstanceField)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain InstanceField " + instanceFieldListOldInstanceField + " since its serviceInstance field is not nullable.");
                }
            }
            for (PersonHasService personHasServiceListOldPersonHasService : personHasServiceListOld) {
                if (!personHasServiceListNew.contains(personHasServiceListOldPersonHasService)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain PersonHasService " + personHasServiceListOldPersonHasService + " since its serviceInstance field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<InstanceField> attachedInstanceFieldListNew = new ArrayList<InstanceField>();
            for (InstanceField instanceFieldListNewInstanceFieldToAttach : instanceFieldListNew) {
                instanceFieldListNewInstanceFieldToAttach = em.getReference(instanceFieldListNewInstanceFieldToAttach.getClass(), instanceFieldListNewInstanceFieldToAttach.getInstanceFieldPK());
                attachedInstanceFieldListNew.add(instanceFieldListNewInstanceFieldToAttach);
            }
            instanceFieldListNew = attachedInstanceFieldListNew;
            serviceInstance.setInstanceFieldList(instanceFieldListNew);
            List<PersonHasService> attachedPersonHasServiceListNew = new ArrayList<PersonHasService>();
            for (PersonHasService personHasServiceListNewPersonHasServiceToAttach : personHasServiceListNew) {
                personHasServiceListNewPersonHasServiceToAttach = em.getReference(personHasServiceListNewPersonHasServiceToAttach.getClass(), personHasServiceListNewPersonHasServiceToAttach.getPersonHasServicePK());
                attachedPersonHasServiceListNew.add(personHasServiceListNewPersonHasServiceToAttach);
            }
            personHasServiceListNew = attachedPersonHasServiceListNew;
            serviceInstance.setPersonHasServiceList(personHasServiceListNew);
            serviceInstance = em.merge(serviceInstance);
            for (InstanceField instanceFieldListNewInstanceField : instanceFieldListNew) {
                if (!instanceFieldListOld.contains(instanceFieldListNewInstanceField)) {
                    ServiceInstance oldServiceInstanceOfInstanceFieldListNewInstanceField = instanceFieldListNewInstanceField.getServiceInstance();
                    instanceFieldListNewInstanceField.setServiceInstance(serviceInstance);
                    instanceFieldListNewInstanceField = em.merge(instanceFieldListNewInstanceField);
                    if (oldServiceInstanceOfInstanceFieldListNewInstanceField != null && !oldServiceInstanceOfInstanceFieldListNewInstanceField.equals(serviceInstance)) {
                        oldServiceInstanceOfInstanceFieldListNewInstanceField.getInstanceFieldList().remove(instanceFieldListNewInstanceField);
                        oldServiceInstanceOfInstanceFieldListNewInstanceField = em.merge(oldServiceInstanceOfInstanceFieldListNewInstanceField);
                    }
                }
            }
            for (PersonHasService personHasServiceListNewPersonHasService : personHasServiceListNew) {
                if (!personHasServiceListOld.contains(personHasServiceListNewPersonHasService)) {
                    ServiceInstance oldServiceInstanceOfPersonHasServiceListNewPersonHasService = personHasServiceListNewPersonHasService.getServiceInstance();
                    personHasServiceListNewPersonHasService.setServiceInstance(serviceInstance);
                    personHasServiceListNewPersonHasService = em.merge(personHasServiceListNewPersonHasService);
                    if (oldServiceInstanceOfPersonHasServiceListNewPersonHasService != null && !oldServiceInstanceOfPersonHasServiceListNewPersonHasService.equals(serviceInstance)) {
                        oldServiceInstanceOfPersonHasServiceListNewPersonHasService.getPersonHasServiceList().remove(personHasServiceListNewPersonHasService);
                        oldServiceInstanceOfPersonHasServiceListNewPersonHasService = em.merge(oldServiceInstanceOfPersonHasServiceListNewPersonHasService);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = serviceInstance.getId();
                if (findServiceInstance(id) == null) {
                    throw new NonexistentEntityException("The serviceInstance with id " + id + " no longer exists.");
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
            ServiceInstance serviceInstance;
            try {
                serviceInstance = em.getReference(ServiceInstance.class, id);
                serviceInstance.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The serviceInstance with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<InstanceField> instanceFieldListOrphanCheck = serviceInstance.getInstanceFieldList();
            for (InstanceField instanceFieldListOrphanCheckInstanceField : instanceFieldListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This ServiceInstance (" + serviceInstance + ") cannot be destroyed since the InstanceField " + instanceFieldListOrphanCheckInstanceField + " in its instanceFieldList field has a non-nullable serviceInstance field.");
            }
            List<PersonHasService> personHasServiceListOrphanCheck = serviceInstance.getPersonHasServiceList();
            for (PersonHasService personHasServiceListOrphanCheckPersonHasService : personHasServiceListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This ServiceInstance (" + serviceInstance + ") cannot be destroyed since the PersonHasService " + personHasServiceListOrphanCheckPersonHasService + " in its personHasServiceList field has a non-nullable serviceInstance field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(serviceInstance);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<ServiceInstance> findServiceInstanceEntities() {
        return findServiceInstanceEntities(true, -1, -1);
    }

    public List<ServiceInstance> findServiceInstanceEntities(int maxResults, int firstResult) {
        return findServiceInstanceEntities(false, maxResults, firstResult);
    }

    private List<ServiceInstance> findServiceInstanceEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(ServiceInstance.class));
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

    public ServiceInstance findServiceInstance(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(ServiceInstance.class, id);
        } finally {
            em.close();
        }
    }

    public int getServiceInstanceCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<ServiceInstance> rt = cq.from(ServiceInstance.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
