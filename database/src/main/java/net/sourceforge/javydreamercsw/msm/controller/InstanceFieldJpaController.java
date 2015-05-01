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
import net.sourceforge.javydreamercsw.msm.db.InstanceField;
import net.sourceforge.javydreamercsw.msm.db.InstanceFieldPK;
import net.sourceforge.javydreamercsw.msm.db.TMField;
import net.sourceforge.javydreamercsw.msm.db.ServiceInstance;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class InstanceFieldJpaController implements Serializable {

    public InstanceFieldJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(InstanceField instanceField) throws PreexistingEntityException, Exception {
        if (instanceField.getInstanceFieldPK() == null) {
            instanceField.setInstanceFieldPK(new InstanceFieldPK());
        }
        instanceField.getInstanceFieldPK().setServiceInstanceId(instanceField.getServiceInstance().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            TMField tmfieldId = instanceField.getTmfieldId();
            if (tmfieldId != null) {
                tmfieldId = em.getReference(tmfieldId.getClass(), tmfieldId.getId());
                instanceField.setTmfieldId(tmfieldId);
            }
            ServiceInstance serviceInstance = instanceField.getServiceInstance();
            if (serviceInstance != null) {
                serviceInstance = em.getReference(serviceInstance.getClass(), serviceInstance.getId());
                instanceField.setServiceInstance(serviceInstance);
            }
            em.persist(instanceField);
            if (tmfieldId != null) {
                tmfieldId.getInstanceFieldList().add(instanceField);
                tmfieldId = em.merge(tmfieldId);
            }
            if (serviceInstance != null) {
                serviceInstance.getInstanceFieldList().add(instanceField);
                serviceInstance = em.merge(serviceInstance);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findInstanceField(instanceField.getInstanceFieldPK()) != null) {
                throw new PreexistingEntityException("InstanceField " + instanceField + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(InstanceField instanceField) throws NonexistentEntityException, Exception {
        instanceField.getInstanceFieldPK().setServiceInstanceId(instanceField.getServiceInstance().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            InstanceField persistentInstanceField = em.find(InstanceField.class, instanceField.getInstanceFieldPK());
            TMField tmfieldIdOld = persistentInstanceField.getTmfieldId();
            TMField tmfieldIdNew = instanceField.getTmfieldId();
            ServiceInstance serviceInstanceOld = persistentInstanceField.getServiceInstance();
            ServiceInstance serviceInstanceNew = instanceField.getServiceInstance();
            if (tmfieldIdNew != null) {
                tmfieldIdNew = em.getReference(tmfieldIdNew.getClass(), tmfieldIdNew.getId());
                instanceField.setTmfieldId(tmfieldIdNew);
            }
            if (serviceInstanceNew != null) {
                serviceInstanceNew = em.getReference(serviceInstanceNew.getClass(), serviceInstanceNew.getId());
                instanceField.setServiceInstance(serviceInstanceNew);
            }
            instanceField = em.merge(instanceField);
            if (tmfieldIdOld != null && !tmfieldIdOld.equals(tmfieldIdNew)) {
                tmfieldIdOld.getInstanceFieldList().remove(instanceField);
                tmfieldIdOld = em.merge(tmfieldIdOld);
            }
            if (tmfieldIdNew != null && !tmfieldIdNew.equals(tmfieldIdOld)) {
                tmfieldIdNew.getInstanceFieldList().add(instanceField);
                tmfieldIdNew = em.merge(tmfieldIdNew);
            }
            if (serviceInstanceOld != null && !serviceInstanceOld.equals(serviceInstanceNew)) {
                serviceInstanceOld.getInstanceFieldList().remove(instanceField);
                serviceInstanceOld = em.merge(serviceInstanceOld);
            }
            if (serviceInstanceNew != null && !serviceInstanceNew.equals(serviceInstanceOld)) {
                serviceInstanceNew.getInstanceFieldList().add(instanceField);
                serviceInstanceNew = em.merge(serviceInstanceNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                InstanceFieldPK id = instanceField.getInstanceFieldPK();
                if (findInstanceField(id) == null) {
                    throw new NonexistentEntityException("The instanceField with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(InstanceFieldPK id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            InstanceField instanceField;
            try {
                instanceField = em.getReference(InstanceField.class, id);
                instanceField.getInstanceFieldPK();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The instanceField with id " + id + " no longer exists.", enfe);
            }
            TMField tmfieldId = instanceField.getTmfieldId();
            if (tmfieldId != null) {
                tmfieldId.getInstanceFieldList().remove(instanceField);
                tmfieldId = em.merge(tmfieldId);
            }
            ServiceInstance serviceInstance = instanceField.getServiceInstance();
            if (serviceInstance != null) {
                serviceInstance.getInstanceFieldList().remove(instanceField);
                serviceInstance = em.merge(serviceInstance);
            }
            em.remove(instanceField);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<InstanceField> findInstanceFieldEntities() {
        return findInstanceFieldEntities(true, -1, -1);
    }

    public List<InstanceField> findInstanceFieldEntities(int maxResults, int firstResult) {
        return findInstanceFieldEntities(false, maxResults, firstResult);
    }

    private List<InstanceField> findInstanceFieldEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(InstanceField.class));
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

    public InstanceField findInstanceField(InstanceFieldPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(InstanceField.class, id);
        } finally {
            em.close();
        }
    }

    public int getInstanceFieldCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<InstanceField> rt = cq.from(InstanceField.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
