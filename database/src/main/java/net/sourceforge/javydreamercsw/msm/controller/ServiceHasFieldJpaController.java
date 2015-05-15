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
import net.sourceforge.javydreamercsw.msm.db.Field;
import net.sourceforge.javydreamercsw.msm.db.Service;
import net.sourceforge.javydreamercsw.msm.db.ServiceHasField;
import net.sourceforge.javydreamercsw.msm.db.ServiceHasFieldPK;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class ServiceHasFieldJpaController implements Serializable {

    public ServiceHasFieldJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(ServiceHasField serviceHasField) throws PreexistingEntityException, Exception {
        if (serviceHasField.getServiceHasFieldPK() == null) {
            serviceHasField.setServiceHasFieldPK(new ServiceHasFieldPK());
        }
        serviceHasField.getServiceHasFieldPK().setFieldId(serviceHasField.getTmfield().getId());
        serviceHasField.getServiceHasFieldPK().setServiceId(serviceHasField.getService().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Field tmfield = serviceHasField.getTmfield();
            if (tmfield != null) {
                tmfield = em.getReference(tmfield.getClass(), tmfield.getId());
                serviceHasField.setTmfield(tmfield);
            }
            Service service = serviceHasField.getService();
            if (service != null) {
                service = em.getReference(service.getClass(), service.getId());
                serviceHasField.setService(service);
            }
            em.persist(serviceHasField);
            if (tmfield != null) {
                tmfield.getServiceHasFieldList().add(serviceHasField);
                tmfield = em.merge(tmfield);
            }
            if (service != null) {
                service.getServiceHasFieldList().add(serviceHasField);
                service = em.merge(service);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findServiceHasField(serviceHasField.getServiceHasFieldPK()) != null) {
                throw new PreexistingEntityException("ServiceHasField " + serviceHasField + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(ServiceHasField serviceHasField) throws NonexistentEntityException, Exception {
        serviceHasField.getServiceHasFieldPK().setFieldId(serviceHasField.getTmfield().getId());
        serviceHasField.getServiceHasFieldPK().setServiceId(serviceHasField.getService().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            ServiceHasField persistentServiceHasField = em.find(ServiceHasField.class, serviceHasField.getServiceHasFieldPK());
            Field tmfieldOld = persistentServiceHasField.getTmfield();
            Field tmfieldNew = serviceHasField.getTmfield();
            Service serviceOld = persistentServiceHasField.getService();
            Service serviceNew = serviceHasField.getService();
            if (tmfieldNew != null) {
                tmfieldNew = em.getReference(tmfieldNew.getClass(), tmfieldNew.getId());
                serviceHasField.setTmfield(tmfieldNew);
            }
            if (serviceNew != null) {
                serviceNew = em.getReference(serviceNew.getClass(), serviceNew.getId());
                serviceHasField.setService(serviceNew);
            }
            serviceHasField = em.merge(serviceHasField);
            if (tmfieldOld != null && !tmfieldOld.equals(tmfieldNew)) {
                tmfieldOld.getServiceHasFieldList().remove(serviceHasField);
                tmfieldOld = em.merge(tmfieldOld);
            }
            if (tmfieldNew != null && !tmfieldNew.equals(tmfieldOld)) {
                tmfieldNew.getServiceHasFieldList().add(serviceHasField);
                tmfieldNew = em.merge(tmfieldNew);
            }
            if (serviceOld != null && !serviceOld.equals(serviceNew)) {
                serviceOld.getServiceHasFieldList().remove(serviceHasField);
                serviceOld = em.merge(serviceOld);
            }
            if (serviceNew != null && !serviceNew.equals(serviceOld)) {
                serviceNew.getServiceHasFieldList().add(serviceHasField);
                serviceNew = em.merge(serviceNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                ServiceHasFieldPK id = serviceHasField.getServiceHasFieldPK();
                if (findServiceHasField(id) == null) {
                    throw new NonexistentEntityException("The serviceHasField with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(ServiceHasFieldPK id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            ServiceHasField serviceHasField;
            try {
                serviceHasField = em.getReference(ServiceHasField.class, id);
                serviceHasField.getServiceHasFieldPK();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The serviceHasField with id " + id + " no longer exists.", enfe);
            }
            Field tmfield = serviceHasField.getTmfield();
            if (tmfield != null) {
                tmfield.getServiceHasFieldList().remove(serviceHasField);
                tmfield = em.merge(tmfield);
            }
            Service service = serviceHasField.getService();
            if (service != null) {
                service.getServiceHasFieldList().remove(serviceHasField);
                service = em.merge(service);
            }
            em.remove(serviceHasField);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<ServiceHasField> findServiceHasFieldEntities() {
        return findServiceHasFieldEntities(true, -1, -1);
    }

    public List<ServiceHasField> findServiceHasFieldEntities(int maxResults, int firstResult) {
        return findServiceHasFieldEntities(false, maxResults, firstResult);
    }

    private List<ServiceHasField> findServiceHasFieldEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(ServiceHasField.class));
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

    public ServiceHasField findServiceHasField(ServiceHasFieldPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(ServiceHasField.class, id);
        } finally {
            em.close();
        }
    }

    public int getServiceHasFieldCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<ServiceHasField> rt = cq.from(ServiceHasField.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
