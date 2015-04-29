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
import net.sourceforge.javydreamercsw.msm.db.FieldType;
import net.sourceforge.javydreamercsw.msm.db.Range;
import net.sourceforge.javydreamercsw.msm.db.Service;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import net.sourceforge.javydreamercsw.msm.db.TMField;
import net.sourceforge.javydreamercsw.msm.controller.exceptions.NonexistentEntityException;
import net.sourceforge.javydreamercsw.msm.controller.exceptions.PreexistingEntityException;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class TmfieldJpaController implements Serializable {

    public TmfieldJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(TMField tmfield) throws PreexistingEntityException, Exception {
        if (tmfield.getServiceList() == null) {
            tmfield.setServiceList(new ArrayList<Service>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            FieldType fieldTypeId = tmfield.getFieldTypeId();
            if (fieldTypeId != null) {
                fieldTypeId = em.getReference(fieldTypeId.getClass(), fieldTypeId.getId());
                tmfield.setFieldTypeId(fieldTypeId);
            }
            Range rangeId = tmfield.getRangeId();
            if (rangeId != null) {
                rangeId = em.getReference(rangeId.getClass(), rangeId.getId());
                tmfield.setRangeId(rangeId);
            }
            List<Service> attachedServiceList = new ArrayList<Service>();
            for (Service serviceListServiceToAttach : tmfield.getServiceList()) {
                serviceListServiceToAttach = em.getReference(serviceListServiceToAttach.getClass(), serviceListServiceToAttach.getId());
                attachedServiceList.add(serviceListServiceToAttach);
            }
            tmfield.setServiceList(attachedServiceList);
            em.persist(tmfield);
            if (fieldTypeId != null) {
                fieldTypeId.getTmfieldList().add(tmfield);
                fieldTypeId = em.merge(fieldTypeId);
            }
            if (rangeId != null) {
                rangeId.getTmfieldList().add(tmfield);
                rangeId = em.merge(rangeId);
            }
            for (Service serviceListService : tmfield.getServiceList()) {
                serviceListService.getTmfieldList().add(tmfield);
                serviceListService = em.merge(serviceListService);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findTmfield(tmfield.getId()) != null) {
                throw new PreexistingEntityException("Tmfield " + tmfield + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(TMField tmfield) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            TMField persistentTmfield = em.find(TMField.class, tmfield.getId());
            FieldType fieldTypeIdOld = persistentTmfield.getFieldTypeId();
            FieldType fieldTypeIdNew = tmfield.getFieldTypeId();
            Range rangeIdOld = persistentTmfield.getRangeId();
            Range rangeIdNew = tmfield.getRangeId();
            List<Service> serviceListOld = persistentTmfield.getServiceList();
            List<Service> serviceListNew = tmfield.getServiceList();
            if (fieldTypeIdNew != null) {
                fieldTypeIdNew = em.getReference(fieldTypeIdNew.getClass(), fieldTypeIdNew.getId());
                tmfield.setFieldTypeId(fieldTypeIdNew);
            }
            if (rangeIdNew != null) {
                rangeIdNew = em.getReference(rangeIdNew.getClass(), rangeIdNew.getId());
                tmfield.setRangeId(rangeIdNew);
            }
            List<Service> attachedServiceListNew = new ArrayList<Service>();
            for (Service serviceListNewServiceToAttach : serviceListNew) {
                serviceListNewServiceToAttach = em.getReference(serviceListNewServiceToAttach.getClass(), serviceListNewServiceToAttach.getId());
                attachedServiceListNew.add(serviceListNewServiceToAttach);
            }
            serviceListNew = attachedServiceListNew;
            tmfield.setServiceList(serviceListNew);
            tmfield = em.merge(tmfield);
            if (fieldTypeIdOld != null && !fieldTypeIdOld.equals(fieldTypeIdNew)) {
                fieldTypeIdOld.getTmfieldList().remove(tmfield);
                fieldTypeIdOld = em.merge(fieldTypeIdOld);
            }
            if (fieldTypeIdNew != null && !fieldTypeIdNew.equals(fieldTypeIdOld)) {
                fieldTypeIdNew.getTmfieldList().add(tmfield);
                fieldTypeIdNew = em.merge(fieldTypeIdNew);
            }
            if (rangeIdOld != null && !rangeIdOld.equals(rangeIdNew)) {
                rangeIdOld.getTmfieldList().remove(tmfield);
                rangeIdOld = em.merge(rangeIdOld);
            }
            if (rangeIdNew != null && !rangeIdNew.equals(rangeIdOld)) {
                rangeIdNew.getTmfieldList().add(tmfield);
                rangeIdNew = em.merge(rangeIdNew);
            }
            for (Service serviceListOldService : serviceListOld) {
                if (!serviceListNew.contains(serviceListOldService)) {
                    serviceListOldService.getTmfieldList().remove(tmfield);
                    serviceListOldService = em.merge(serviceListOldService);
                }
            }
            for (Service serviceListNewService : serviceListNew) {
                if (!serviceListOld.contains(serviceListNewService)) {
                    serviceListNewService.getTmfieldList().add(tmfield);
                    serviceListNewService = em.merge(serviceListNewService);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = tmfield.getId();
                if (findTmfield(id) == null) {
                    throw new NonexistentEntityException("The tmfield with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            TMField tmfield;
            try {
                tmfield = em.getReference(TMField.class, id);
                tmfield.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The tmfield with id " + id + " no longer exists.", enfe);
            }
            FieldType fieldTypeId = tmfield.getFieldTypeId();
            if (fieldTypeId != null) {
                fieldTypeId.getTmfieldList().remove(tmfield);
                fieldTypeId = em.merge(fieldTypeId);
            }
            Range rangeId = tmfield.getRangeId();
            if (rangeId != null) {
                rangeId.getTmfieldList().remove(tmfield);
                rangeId = em.merge(rangeId);
            }
            List<Service> serviceList = tmfield.getServiceList();
            for (Service serviceListService : serviceList) {
                serviceListService.getTmfieldList().remove(tmfield);
                serviceListService = em.merge(serviceListService);
            }
            em.remove(tmfield);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<TMField> findTmfieldEntities() {
        return findTmfieldEntities(true, -1, -1);
    }

    public List<TMField> findTmfieldEntities(int maxResults, int firstResult) {
        return findTmfieldEntities(false, maxResults, firstResult);
    }

    private List<TMField> findTmfieldEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(TMField.class));
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

    public TMField findTmfield(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(TMField.class, id);
        } finally {
            em.close();
        }
    }

    public int getTmfieldCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<TMField> rt = cq.from(TMField.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
