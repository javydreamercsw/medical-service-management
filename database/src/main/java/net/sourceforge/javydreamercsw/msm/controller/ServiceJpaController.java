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
import net.sourceforge.javydreamercsw.msm.db.ServiceHasField;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import net.sourceforge.javydreamercsw.msm.controller.exceptions.IllegalOrphanException;
import net.sourceforge.javydreamercsw.msm.controller.exceptions.NonexistentEntityException;
import net.sourceforge.javydreamercsw.msm.db.Service;
import net.sourceforge.javydreamercsw.msm.db.ServiceInstance;

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
        if (service.getServiceHasFieldList() == null) {
            service.setServiceHasFieldList(new ArrayList<ServiceHasField>());
        }
        if (service.getServiceInstanceList() == null) {
            service.setServiceInstanceList(new ArrayList<ServiceInstance>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<ServiceHasField> attachedServiceHasFieldList = new ArrayList<ServiceHasField>();
            for (ServiceHasField serviceHasFieldListServiceHasFieldToAttach : service.getServiceHasFieldList()) {
                serviceHasFieldListServiceHasFieldToAttach = em.getReference(serviceHasFieldListServiceHasFieldToAttach.getClass(), serviceHasFieldListServiceHasFieldToAttach.getServiceHasFieldPK());
                attachedServiceHasFieldList.add(serviceHasFieldListServiceHasFieldToAttach);
            }
            service.setServiceHasFieldList(attachedServiceHasFieldList);
            List<ServiceInstance> attachedServiceInstanceList = new ArrayList<ServiceInstance>();
            for (ServiceInstance serviceInstanceListServiceInstanceToAttach : service.getServiceInstanceList()) {
                serviceInstanceListServiceInstanceToAttach = em.getReference(serviceInstanceListServiceInstanceToAttach.getClass(), serviceInstanceListServiceInstanceToAttach.getId());
                attachedServiceInstanceList.add(serviceInstanceListServiceInstanceToAttach);
            }
            service.setServiceInstanceList(attachedServiceInstanceList);
            em.persist(service);
            for (ServiceHasField serviceHasFieldListServiceHasField : service.getServiceHasFieldList()) {
                Service oldServiceOfServiceHasFieldListServiceHasField = serviceHasFieldListServiceHasField.getService();
                serviceHasFieldListServiceHasField.setService(service);
                serviceHasFieldListServiceHasField = em.merge(serviceHasFieldListServiceHasField);
                if (oldServiceOfServiceHasFieldListServiceHasField != null) {
                    oldServiceOfServiceHasFieldListServiceHasField.getServiceHasFieldList().remove(serviceHasFieldListServiceHasField);
                    oldServiceOfServiceHasFieldListServiceHasField = em.merge(oldServiceOfServiceHasFieldListServiceHasField);
                }
            }
            for (ServiceInstance serviceInstanceListServiceInstance : service.getServiceInstanceList()) {
                Service oldServiceIdOfServiceInstanceListServiceInstance = serviceInstanceListServiceInstance.getServiceId();
                serviceInstanceListServiceInstance.setServiceId(service);
                serviceInstanceListServiceInstance = em.merge(serviceInstanceListServiceInstance);
                if (oldServiceIdOfServiceInstanceListServiceInstance != null) {
                    oldServiceIdOfServiceInstanceListServiceInstance.getServiceInstanceList().remove(serviceInstanceListServiceInstance);
                    oldServiceIdOfServiceInstanceListServiceInstance = em.merge(oldServiceIdOfServiceInstanceListServiceInstance);
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
            List<ServiceHasField> serviceHasFieldListOld = persistentService.getServiceHasFieldList();
            List<ServiceHasField> serviceHasFieldListNew = service.getServiceHasFieldList();
            List<ServiceInstance> serviceInstanceListOld = persistentService.getServiceInstanceList();
            List<ServiceInstance> serviceInstanceListNew = service.getServiceInstanceList();
            List<String> illegalOrphanMessages = null;
            for (ServiceHasField serviceHasFieldListOldServiceHasField : serviceHasFieldListOld) {
                if (!serviceHasFieldListNew.contains(serviceHasFieldListOldServiceHasField)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain ServiceHasField " + serviceHasFieldListOldServiceHasField + " since its service field is not nullable.");
                }
            }
            for (ServiceInstance serviceInstanceListOldServiceInstance : serviceInstanceListOld) {
                if (!serviceInstanceListNew.contains(serviceInstanceListOldServiceInstance)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain ServiceInstance " + serviceInstanceListOldServiceInstance + " since its serviceId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<ServiceHasField> attachedServiceHasFieldListNew = new ArrayList<ServiceHasField>();
            for (ServiceHasField serviceHasFieldListNewServiceHasFieldToAttach : serviceHasFieldListNew) {
                serviceHasFieldListNewServiceHasFieldToAttach = em.getReference(serviceHasFieldListNewServiceHasFieldToAttach.getClass(), serviceHasFieldListNewServiceHasFieldToAttach.getServiceHasFieldPK());
                attachedServiceHasFieldListNew.add(serviceHasFieldListNewServiceHasFieldToAttach);
            }
            serviceHasFieldListNew = attachedServiceHasFieldListNew;
            service.setServiceHasFieldList(serviceHasFieldListNew);
            List<ServiceInstance> attachedServiceInstanceListNew = new ArrayList<ServiceInstance>();
            for (ServiceInstance serviceInstanceListNewServiceInstanceToAttach : serviceInstanceListNew) {
                serviceInstanceListNewServiceInstanceToAttach = em.getReference(serviceInstanceListNewServiceInstanceToAttach.getClass(), serviceInstanceListNewServiceInstanceToAttach.getId());
                attachedServiceInstanceListNew.add(serviceInstanceListNewServiceInstanceToAttach);
            }
            serviceInstanceListNew = attachedServiceInstanceListNew;
            service.setServiceInstanceList(serviceInstanceListNew);
            service = em.merge(service);
            for (ServiceHasField serviceHasFieldListNewServiceHasField : serviceHasFieldListNew) {
                if (!serviceHasFieldListOld.contains(serviceHasFieldListNewServiceHasField)) {
                    Service oldServiceOfServiceHasFieldListNewServiceHasField = serviceHasFieldListNewServiceHasField.getService();
                    serviceHasFieldListNewServiceHasField.setService(service);
                    serviceHasFieldListNewServiceHasField = em.merge(serviceHasFieldListNewServiceHasField);
                    if (oldServiceOfServiceHasFieldListNewServiceHasField != null && !oldServiceOfServiceHasFieldListNewServiceHasField.equals(service)) {
                        oldServiceOfServiceHasFieldListNewServiceHasField.getServiceHasFieldList().remove(serviceHasFieldListNewServiceHasField);
                        oldServiceOfServiceHasFieldListNewServiceHasField = em.merge(oldServiceOfServiceHasFieldListNewServiceHasField);
                    }
                }
            }
            for (ServiceInstance serviceInstanceListNewServiceInstance : serviceInstanceListNew) {
                if (!serviceInstanceListOld.contains(serviceInstanceListNewServiceInstance)) {
                    Service oldServiceIdOfServiceInstanceListNewServiceInstance = serviceInstanceListNewServiceInstance.getServiceId();
                    serviceInstanceListNewServiceInstance.setServiceId(service);
                    serviceInstanceListNewServiceInstance = em.merge(serviceInstanceListNewServiceInstance);
                    if (oldServiceIdOfServiceInstanceListNewServiceInstance != null && !oldServiceIdOfServiceInstanceListNewServiceInstance.equals(service)) {
                        oldServiceIdOfServiceInstanceListNewServiceInstance.getServiceInstanceList().remove(serviceInstanceListNewServiceInstance);
                        oldServiceIdOfServiceInstanceListNewServiceInstance = em.merge(oldServiceIdOfServiceInstanceListNewServiceInstance);
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
            List<ServiceHasField> serviceHasFieldListOrphanCheck = service.getServiceHasFieldList();
            for (ServiceHasField serviceHasFieldListOrphanCheckServiceHasField : serviceHasFieldListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Service (" + service + ") cannot be destroyed since the ServiceHasField " + serviceHasFieldListOrphanCheckServiceHasField + " in its serviceHasFieldList field has a non-nullable service field.");
            }
            List<ServiceInstance> serviceInstanceListOrphanCheck = service.getServiceInstanceList();
            for (ServiceInstance serviceInstanceListOrphanCheckServiceInstance : serviceInstanceListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Service (" + service + ") cannot be destroyed since the ServiceInstance " + serviceInstanceListOrphanCheckServiceInstance + " in its serviceInstanceList field has a non-nullable serviceId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
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
