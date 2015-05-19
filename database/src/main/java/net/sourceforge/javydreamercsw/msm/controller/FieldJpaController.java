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
import net.sourceforge.javydreamercsw.msm.db.Field;
import net.sourceforge.javydreamercsw.msm.db.ServiceHasField;

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

    public void create(Field field) {
        if (field.getInstanceFieldList() == null) {
            field.setInstanceFieldList(new ArrayList<InstanceField>());
        }
        if (field.getServiceHasFieldList() == null) {
            field.setServiceHasFieldList(new ArrayList<ServiceHasField>());
        }
        if (field.getRangeList() == null) {
            field.setRangeList(new ArrayList<Range>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            FieldType fieldTypeId = field.getFieldTypeId();
            if (fieldTypeId != null) {
                fieldTypeId = em.getReference(fieldTypeId.getClass(), fieldTypeId.getId());
                field.setFieldTypeId(fieldTypeId);
            }
            Range rangeId = field.getRangeId();
            if (rangeId != null) {
                rangeId = em.getReference(rangeId.getClass(), rangeId.getId());
                field.setRangeId(rangeId);
            }
            List<InstanceField> attachedInstanceFieldList = new ArrayList<InstanceField>();
            for (InstanceField instanceFieldListInstanceFieldToAttach : field.getInstanceFieldList()) {
                instanceFieldListInstanceFieldToAttach = em.getReference(instanceFieldListInstanceFieldToAttach.getClass(), instanceFieldListInstanceFieldToAttach.getInstanceFieldPK());
                attachedInstanceFieldList.add(instanceFieldListInstanceFieldToAttach);
            }
            field.setInstanceFieldList(attachedInstanceFieldList);
            List<ServiceHasField> attachedServiceHasFieldList = new ArrayList<ServiceHasField>();
            for (ServiceHasField serviceHasFieldListServiceHasFieldToAttach : field.getServiceHasFieldList()) {
                serviceHasFieldListServiceHasFieldToAttach = em.getReference(serviceHasFieldListServiceHasFieldToAttach.getClass(), serviceHasFieldListServiceHasFieldToAttach.getServiceHasFieldPK());
                attachedServiceHasFieldList.add(serviceHasFieldListServiceHasFieldToAttach);
            }
            field.setServiceHasFieldList(attachedServiceHasFieldList);
            List<Range> attachedRangeList = new ArrayList<Range>();
            for (Range rangeListRangeToAttach : field.getRangeList()) {
                rangeListRangeToAttach = em.getReference(rangeListRangeToAttach.getClass(), rangeListRangeToAttach.getId());
                attachedRangeList.add(rangeListRangeToAttach);
            }
            field.setRangeList(attachedRangeList);
            em.persist(field);
            if (fieldTypeId != null) {
                fieldTypeId.getTmfieldList().add(field);
                fieldTypeId = em.merge(fieldTypeId);
            }
            if (rangeId != null) {
                rangeId.getTmfieldList().add(field);
                rangeId = em.merge(rangeId);
            }
            for (InstanceField instanceFieldListInstanceField : field.getInstanceFieldList()) {
                Field oldTmfieldIdOfInstanceFieldListInstanceField = instanceFieldListInstanceField.getTmfieldId();
                instanceFieldListInstanceField.setTmfieldId(field);
                instanceFieldListInstanceField = em.merge(instanceFieldListInstanceField);
                if (oldTmfieldIdOfInstanceFieldListInstanceField != null) {
                    oldTmfieldIdOfInstanceFieldListInstanceField.getInstanceFieldList().remove(instanceFieldListInstanceField);
                    oldTmfieldIdOfInstanceFieldListInstanceField = em.merge(oldTmfieldIdOfInstanceFieldListInstanceField);
                }
            }
            for (ServiceHasField serviceHasFieldListServiceHasField : field.getServiceHasFieldList()) {
                Field oldTmfieldOfServiceHasFieldListServiceHasField = serviceHasFieldListServiceHasField.getTmfield();
                serviceHasFieldListServiceHasField.setTmfield(field);
                serviceHasFieldListServiceHasField = em.merge(serviceHasFieldListServiceHasField);
                if (oldTmfieldOfServiceHasFieldListServiceHasField != null) {
                    oldTmfieldOfServiceHasFieldListServiceHasField.getServiceHasFieldList().remove(serviceHasFieldListServiceHasField);
                    oldTmfieldOfServiceHasFieldListServiceHasField = em.merge(oldTmfieldOfServiceHasFieldListServiceHasField);
                }
            }
            for (Range rangeListRange : field.getRangeList()) {
                rangeListRange.getTMFieldList().add(field);
                rangeListRange = em.merge(rangeListRange);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Field field) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Field persistentField = em.find(Field.class, field.getId());
            FieldType fieldTypeIdOld = persistentField.getFieldTypeId();
            FieldType fieldTypeIdNew = field.getFieldTypeId();
            Range rangeIdOld = persistentField.getRangeId();
            Range rangeIdNew = field.getRangeId();
            List<InstanceField> instanceFieldListOld = persistentField.getInstanceFieldList();
            List<InstanceField> instanceFieldListNew = field.getInstanceFieldList();
            List<ServiceHasField> serviceHasFieldListOld = persistentField.getServiceHasFieldList();
            List<ServiceHasField> serviceHasFieldListNew = field.getServiceHasFieldList();
            List<Range> rangeListOld = persistentField.getRangeList();
            List<Range> rangeListNew = field.getRangeList();
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
                field.setFieldTypeId(fieldTypeIdNew);
            }
            if (rangeIdNew != null) {
                rangeIdNew = em.getReference(rangeIdNew.getClass(), rangeIdNew.getId());
                field.setRangeId(rangeIdNew);
            }
            List<InstanceField> attachedInstanceFieldListNew = new ArrayList<InstanceField>();
            for (InstanceField instanceFieldListNewInstanceFieldToAttach : instanceFieldListNew) {
                instanceFieldListNewInstanceFieldToAttach = em.getReference(instanceFieldListNewInstanceFieldToAttach.getClass(), instanceFieldListNewInstanceFieldToAttach.getInstanceFieldPK());
                attachedInstanceFieldListNew.add(instanceFieldListNewInstanceFieldToAttach);
            }
            instanceFieldListNew = attachedInstanceFieldListNew;
            field.setInstanceFieldList(instanceFieldListNew);
            List<ServiceHasField> attachedServiceHasFieldListNew = new ArrayList<ServiceHasField>();
            for (ServiceHasField serviceHasFieldListNewServiceHasFieldToAttach : serviceHasFieldListNew) {
                serviceHasFieldListNewServiceHasFieldToAttach = em.getReference(serviceHasFieldListNewServiceHasFieldToAttach.getClass(), serviceHasFieldListNewServiceHasFieldToAttach.getServiceHasFieldPK());
                attachedServiceHasFieldListNew.add(serviceHasFieldListNewServiceHasFieldToAttach);
            }
            serviceHasFieldListNew = attachedServiceHasFieldListNew;
            field.setServiceHasFieldList(serviceHasFieldListNew);
            List<Range> attachedRangeListNew = new ArrayList<Range>();
            for (Range rangeListNewRangeToAttach : rangeListNew) {
                rangeListNewRangeToAttach = em.getReference(rangeListNewRangeToAttach.getClass(), rangeListNewRangeToAttach.getId());
                attachedRangeListNew.add(rangeListNewRangeToAttach);
            }
            rangeListNew = attachedRangeListNew;
            field.setRangeList(rangeListNew);
            field = em.merge(field);
            if (fieldTypeIdOld != null && !fieldTypeIdOld.equals(fieldTypeIdNew)) {
                fieldTypeIdOld.getTmfieldList().remove(field);
                fieldTypeIdOld = em.merge(fieldTypeIdOld);
            }
            if (fieldTypeIdNew != null && !fieldTypeIdNew.equals(fieldTypeIdOld)) {
                fieldTypeIdNew.getTmfieldList().add(field);
                fieldTypeIdNew = em.merge(fieldTypeIdNew);
            }
            if (rangeIdOld != null && !rangeIdOld.equals(rangeIdNew)) {
                rangeIdOld.getTmfieldList().remove(field);
                rangeIdOld = em.merge(rangeIdOld);
            }
            if (rangeIdNew != null && !rangeIdNew.equals(rangeIdOld)) {
                rangeIdNew.getTmfieldList().add(field);
                rangeIdNew = em.merge(rangeIdNew);
            }
            for (InstanceField instanceFieldListNewInstanceField : instanceFieldListNew) {
                if (!instanceFieldListOld.contains(instanceFieldListNewInstanceField)) {
                    Field oldTmfieldIdOfInstanceFieldListNewInstanceField = instanceFieldListNewInstanceField.getTmfieldId();
                    instanceFieldListNewInstanceField.setTmfieldId(field);
                    instanceFieldListNewInstanceField = em.merge(instanceFieldListNewInstanceField);
                    if (oldTmfieldIdOfInstanceFieldListNewInstanceField != null && !oldTmfieldIdOfInstanceFieldListNewInstanceField.equals(field)) {
                        oldTmfieldIdOfInstanceFieldListNewInstanceField.getInstanceFieldList().remove(instanceFieldListNewInstanceField);
                        oldTmfieldIdOfInstanceFieldListNewInstanceField = em.merge(oldTmfieldIdOfInstanceFieldListNewInstanceField);
                    }
                }
            }
            for (ServiceHasField serviceHasFieldListNewServiceHasField : serviceHasFieldListNew) {
                if (!serviceHasFieldListOld.contains(serviceHasFieldListNewServiceHasField)) {
                    Field oldTmfieldOfServiceHasFieldListNewServiceHasField = serviceHasFieldListNewServiceHasField.getTmfield();
                    serviceHasFieldListNewServiceHasField.setTmfield(field);
                    serviceHasFieldListNewServiceHasField = em.merge(serviceHasFieldListNewServiceHasField);
                    if (oldTmfieldOfServiceHasFieldListNewServiceHasField != null && !oldTmfieldOfServiceHasFieldListNewServiceHasField.equals(field)) {
                        oldTmfieldOfServiceHasFieldListNewServiceHasField.getServiceHasFieldList().remove(serviceHasFieldListNewServiceHasField);
                        oldTmfieldOfServiceHasFieldListNewServiceHasField = em.merge(oldTmfieldOfServiceHasFieldListNewServiceHasField);
                    }
                }
            }
            for (Range rangeListOldRange : rangeListOld) {
                if (!rangeListNew.contains(rangeListOldRange)) {
                    rangeListOldRange.getTMFieldList().remove(field);
                    rangeListOldRange = em.merge(rangeListOldRange);
                }
            }
            for (Range rangeListNewRange : rangeListNew) {
                if (!rangeListOld.contains(rangeListNewRange)) {
                    rangeListNewRange.getTMFieldList().add(field);
                    rangeListNewRange = em.merge(rangeListNewRange);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = field.getId();
                if (findField(id) == null) {
                    throw new NonexistentEntityException("The field with id " + id + " no longer exists.");
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
            Field field;
            try {
                field = em.getReference(Field.class, id);
                field.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The field with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<InstanceField> instanceFieldListOrphanCheck = field.getInstanceFieldList();
            for (InstanceField instanceFieldListOrphanCheckInstanceField : instanceFieldListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Field (" + field + ") cannot be destroyed since the InstanceField " + instanceFieldListOrphanCheckInstanceField + " in its instanceFieldList field has a non-nullable tmfieldId field.");
            }
            List<ServiceHasField> serviceHasFieldListOrphanCheck = field.getServiceHasFieldList();
            for (ServiceHasField serviceHasFieldListOrphanCheckServiceHasField : serviceHasFieldListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Field (" + field + ") cannot be destroyed since the ServiceHasField " + serviceHasFieldListOrphanCheckServiceHasField + " in its serviceHasFieldList field has a non-nullable tmfield field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            FieldType fieldTypeId = field.getFieldTypeId();
            if (fieldTypeId != null) {
                fieldTypeId.getTmfieldList().remove(field);
                fieldTypeId = em.merge(fieldTypeId);
            }
            Range rangeId = field.getRangeId();
            if (rangeId != null) {
                rangeId.getTmfieldList().remove(field);
                rangeId = em.merge(rangeId);
            }
            List<Range> rangeList = field.getRangeList();
            for (Range rangeListRange : rangeList) {
                rangeListRange.getTMFieldList().remove(field);
                rangeListRange = em.merge(rangeListRange);
            }
            em.remove(field);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Field> findFieldEntities() {
        return findFieldEntities(true, -1, -1);
    }

    public List<Field> findFieldEntities(int maxResults, int firstResult) {
        return findFieldEntities(false, maxResults, firstResult);
    }

    private List<Field> findFieldEntities(boolean all, int maxResults, int firstResult) {
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

    public Field findField(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Field.class, id);
        } finally {
            em.close();
        }
    }

    public int getFieldCount() {
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
