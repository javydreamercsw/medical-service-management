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
import net.sourceforge.javydreamercsw.msm.db.InstanceField;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import net.sourceforge.javydreamercsw.msm.controller.exceptions.IllegalOrphanException;
import net.sourceforge.javydreamercsw.msm.controller.exceptions.NonexistentEntityException;
import net.sourceforge.javydreamercsw.msm.controller.exceptions.PreexistingEntityException;
import net.sourceforge.javydreamercsw.msm.db.ServiceHasField;
import net.sourceforge.javydreamercsw.msm.db.Field;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class FieldJpaController implements Serializable {

    public FieldJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Field TMField) throws PreexistingEntityException, Exception {
        if (TMField.getInstanceFieldList() == null) {
            TMField.setInstanceFieldList(new ArrayList<InstanceField>());
        }
        if (TMField.getServiceHasFieldList() == null) {
            TMField.setServiceHasFieldList(new ArrayList<ServiceHasField>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            FieldType fieldTypeId = TMField.getFieldTypeId();
            if (fieldTypeId != null) {
                fieldTypeId = em.getReference(fieldTypeId.getClass(), fieldTypeId.getId());
                TMField.setFieldTypeId(fieldTypeId);
            }
            Range rangeId = TMField.getRangeId();
            if (rangeId != null) {
                rangeId = em.getReference(rangeId.getClass(), rangeId.getId());
                TMField.setRangeId(rangeId);
            }
            List<InstanceField> attachedInstanceFieldList = new ArrayList<InstanceField>();
            for (InstanceField instanceFieldListInstanceFieldToAttach : TMField.getInstanceFieldList()) {
                instanceFieldListInstanceFieldToAttach = em.getReference(instanceFieldListInstanceFieldToAttach.getClass(), instanceFieldListInstanceFieldToAttach.getInstanceFieldPK());
                attachedInstanceFieldList.add(instanceFieldListInstanceFieldToAttach);
            }
            TMField.setInstanceFieldList(attachedInstanceFieldList);
            List<ServiceHasField> attachedServiceHasFieldList = new ArrayList<ServiceHasField>();
            for (ServiceHasField serviceHasFieldListServiceHasFieldToAttach : TMField.getServiceHasFieldList()) {
                serviceHasFieldListServiceHasFieldToAttach = em.getReference(serviceHasFieldListServiceHasFieldToAttach.getClass(), serviceHasFieldListServiceHasFieldToAttach.getServiceHasFieldPK());
                attachedServiceHasFieldList.add(serviceHasFieldListServiceHasFieldToAttach);
            }
            TMField.setServiceHasFieldList(attachedServiceHasFieldList);
            em.persist(TMField);
            if (fieldTypeId != null) {
                fieldTypeId.getTmfieldList().add(TMField);
                fieldTypeId = em.merge(fieldTypeId);
            }
            if (rangeId != null) {
                rangeId.getTmfieldList().add(TMField);
                rangeId = em.merge(rangeId);
            }
            for (InstanceField instanceFieldListInstanceField : TMField.getInstanceFieldList()) {
                Field oldTmfieldIdOfInstanceFieldListInstanceField = instanceFieldListInstanceField.getTmfieldId();
                instanceFieldListInstanceField.setTmfieldId(TMField);
                instanceFieldListInstanceField = em.merge(instanceFieldListInstanceField);
                if (oldTmfieldIdOfInstanceFieldListInstanceField != null) {
                    oldTmfieldIdOfInstanceFieldListInstanceField.getInstanceFieldList().remove(instanceFieldListInstanceField);
                    oldTmfieldIdOfInstanceFieldListInstanceField = em.merge(oldTmfieldIdOfInstanceFieldListInstanceField);
                }
            }
            for (ServiceHasField serviceHasFieldListServiceHasField : TMField.getServiceHasFieldList()) {
                Field oldTmfieldOfServiceHasFieldListServiceHasField = serviceHasFieldListServiceHasField.getTmfield();
                serviceHasFieldListServiceHasField.setTmfield(TMField);
                serviceHasFieldListServiceHasField = em.merge(serviceHasFieldListServiceHasField);
                if (oldTmfieldOfServiceHasFieldListServiceHasField != null) {
                    oldTmfieldOfServiceHasFieldListServiceHasField.getServiceHasFieldList().remove(serviceHasFieldListServiceHasField);
                    oldTmfieldOfServiceHasFieldListServiceHasField = em.merge(oldTmfieldOfServiceHasFieldListServiceHasField);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findTMField(TMField.getId()) != null) {
                throw new PreexistingEntityException("TMField " + TMField + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Field TMField) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Field persistentTMField = em.find(Field.class, TMField.getId());
            FieldType fieldTypeIdOld = persistentTMField.getFieldTypeId();
            FieldType fieldTypeIdNew = TMField.getFieldTypeId();
            Range rangeIdOld = persistentTMField.getRangeId();
            Range rangeIdNew = TMField.getRangeId();
            List<InstanceField> instanceFieldListOld = persistentTMField.getInstanceFieldList();
            List<InstanceField> instanceFieldListNew = TMField.getInstanceFieldList();
            List<ServiceHasField> serviceHasFieldListOld = persistentTMField.getServiceHasFieldList();
            List<ServiceHasField> serviceHasFieldListNew = TMField.getServiceHasFieldList();
            List<String> illegalOrphanMessages = null;
            for (InstanceField instanceFieldListOldInstanceField : instanceFieldListOld) {
                if (!instanceFieldListNew.contains(instanceFieldListOldInstanceField)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain InstanceField " + instanceFieldListOldInstanceField + " since its tmfieldId field is not nullable.");
                }
            }
            for (ServiceHasField serviceHasFieldListOldServiceHasField : serviceHasFieldListOld) {
                if (!serviceHasFieldListNew.contains(serviceHasFieldListOldServiceHasField)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain ServiceHasField " + serviceHasFieldListOldServiceHasField + " since its tmfield field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (fieldTypeIdNew != null) {
                fieldTypeIdNew = em.getReference(fieldTypeIdNew.getClass(), fieldTypeIdNew.getId());
                TMField.setFieldTypeId(fieldTypeIdNew);
            }
            if (rangeIdNew != null) {
                rangeIdNew = em.getReference(rangeIdNew.getClass(), rangeIdNew.getId());
                TMField.setRangeId(rangeIdNew);
            }
            List<InstanceField> attachedInstanceFieldListNew = new ArrayList<InstanceField>();
            for (InstanceField instanceFieldListNewInstanceFieldToAttach : instanceFieldListNew) {
                instanceFieldListNewInstanceFieldToAttach = em.getReference(instanceFieldListNewInstanceFieldToAttach.getClass(), instanceFieldListNewInstanceFieldToAttach.getInstanceFieldPK());
                attachedInstanceFieldListNew.add(instanceFieldListNewInstanceFieldToAttach);
            }
            instanceFieldListNew = attachedInstanceFieldListNew;
            TMField.setInstanceFieldList(instanceFieldListNew);
            List<ServiceHasField> attachedServiceHasFieldListNew = new ArrayList<ServiceHasField>();
            for (ServiceHasField serviceHasFieldListNewServiceHasFieldToAttach : serviceHasFieldListNew) {
                serviceHasFieldListNewServiceHasFieldToAttach = em.getReference(serviceHasFieldListNewServiceHasFieldToAttach.getClass(), serviceHasFieldListNewServiceHasFieldToAttach.getServiceHasFieldPK());
                attachedServiceHasFieldListNew.add(serviceHasFieldListNewServiceHasFieldToAttach);
            }
            serviceHasFieldListNew = attachedServiceHasFieldListNew;
            TMField.setServiceHasFieldList(serviceHasFieldListNew);
            TMField = em.merge(TMField);
            if (fieldTypeIdOld != null && !fieldTypeIdOld.equals(fieldTypeIdNew)) {
                fieldTypeIdOld.getTmfieldList().remove(TMField);
                fieldTypeIdOld = em.merge(fieldTypeIdOld);
            }
            if (fieldTypeIdNew != null && !fieldTypeIdNew.equals(fieldTypeIdOld)) {
                fieldTypeIdNew.getTmfieldList().add(TMField);
                fieldTypeIdNew = em.merge(fieldTypeIdNew);
            }
            if (rangeIdOld != null && !rangeIdOld.equals(rangeIdNew)) {
                rangeIdOld.getTmfieldList().remove(TMField);
                rangeIdOld = em.merge(rangeIdOld);
            }
            if (rangeIdNew != null && !rangeIdNew.equals(rangeIdOld)) {
                rangeIdNew.getTmfieldList().add(TMField);
                rangeIdNew = em.merge(rangeIdNew);
            }
            for (InstanceField instanceFieldListNewInstanceField : instanceFieldListNew) {
                if (!instanceFieldListOld.contains(instanceFieldListNewInstanceField)) {
                    Field oldTmfieldIdOfInstanceFieldListNewInstanceField = instanceFieldListNewInstanceField.getTmfieldId();
                    instanceFieldListNewInstanceField.setTmfieldId(TMField);
                    instanceFieldListNewInstanceField = em.merge(instanceFieldListNewInstanceField);
                    if (oldTmfieldIdOfInstanceFieldListNewInstanceField != null && !oldTmfieldIdOfInstanceFieldListNewInstanceField.equals(TMField)) {
                        oldTmfieldIdOfInstanceFieldListNewInstanceField.getInstanceFieldList().remove(instanceFieldListNewInstanceField);
                        oldTmfieldIdOfInstanceFieldListNewInstanceField = em.merge(oldTmfieldIdOfInstanceFieldListNewInstanceField);
                    }
                }
            }
            for (ServiceHasField serviceHasFieldListNewServiceHasField : serviceHasFieldListNew) {
                if (!serviceHasFieldListOld.contains(serviceHasFieldListNewServiceHasField)) {
                    Field oldTmfieldOfServiceHasFieldListNewServiceHasField = serviceHasFieldListNewServiceHasField.getTmfield();
                    serviceHasFieldListNewServiceHasField.setTmfield(TMField);
                    serviceHasFieldListNewServiceHasField = em.merge(serviceHasFieldListNewServiceHasField);
                    if (oldTmfieldOfServiceHasFieldListNewServiceHasField != null && !oldTmfieldOfServiceHasFieldListNewServiceHasField.equals(TMField)) {
                        oldTmfieldOfServiceHasFieldListNewServiceHasField.getServiceHasFieldList().remove(serviceHasFieldListNewServiceHasField);
                        oldTmfieldOfServiceHasFieldListNewServiceHasField = em.merge(oldTmfieldOfServiceHasFieldListNewServiceHasField);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = TMField.getId();
                if (findTMField(id) == null) {
                    throw new NonexistentEntityException("The tMField with id " + id + " no longer exists.");
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
            Field TMField;
            try {
                TMField = em.getReference(Field.class, id);
                TMField.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The TMField with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<InstanceField> instanceFieldListOrphanCheck = TMField.getInstanceFieldList();
            for (InstanceField instanceFieldListOrphanCheckInstanceField : instanceFieldListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<>();
                }
                illegalOrphanMessages.add("This TMField (" + TMField + ") cannot be destroyed since the InstanceField " + instanceFieldListOrphanCheckInstanceField + " in its instanceFieldList field has a non-nullable tmfieldId field.");
            }
            List<ServiceHasField> serviceHasFieldListOrphanCheck = TMField.getServiceHasFieldList();
            for (ServiceHasField serviceHasFieldListOrphanCheckServiceHasField : serviceHasFieldListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This TMField (" + TMField + ") cannot be destroyed since the ServiceHasField " + serviceHasFieldListOrphanCheckServiceHasField + " in its serviceHasFieldList field has a non-nullable tmfield field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            FieldType fieldTypeId = TMField.getFieldTypeId();
            if (fieldTypeId != null) {
                fieldTypeId.getTmfieldList().remove(TMField);
                fieldTypeId = em.merge(fieldTypeId);
            }
            Range rangeId = TMField.getRangeId();
            if (rangeId != null) {
                rangeId.getTmfieldList().remove(TMField);
                rangeId = em.merge(rangeId);
            }
            em.remove(TMField);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Field> findTMFieldEntities() {
        return findTMFieldEntities(true, -1, -1);
    }

    public List<Field> findTMFieldEntities(int maxResults, int firstResult) {
        return findTMFieldEntities(false, maxResults, firstResult);
    }

    private List<Field> findTMFieldEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Field.class));
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

    public Field findTMField(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Field.class, id);
        } finally {
            em.close();
        }
    }

    public int getTMFieldCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Field> rt = cq.from(Field.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
