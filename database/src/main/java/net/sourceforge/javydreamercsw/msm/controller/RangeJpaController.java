package net.sourceforge.javydreamercsw.msm.controller;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import net.sourceforge.javydreamercsw.msm.db.RangeType;
import net.sourceforge.javydreamercsw.msm.db.Field;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import net.sourceforge.javydreamercsw.msm.controller.exceptions.IllegalOrphanException;
import net.sourceforge.javydreamercsw.msm.controller.exceptions.NonexistentEntityException;
import net.sourceforge.javydreamercsw.msm.db.Range;

/**
 *
 * @author Javier Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class RangeJpaController implements Serializable {

    public RangeJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Range range) {
        if (range.getTmfieldList() == null) {
            range.setTmfieldList(new ArrayList<Field>());
        }
        if (range.getTMFieldList() == null) {
            range.setTMFieldList(new ArrayList<Field>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            RangeType rangeTypeId = range.getRangeTypeId();
            if (rangeTypeId != null) {
                rangeTypeId = em.getReference(rangeTypeId.getClass(), rangeTypeId.getId());
                range.setRangeTypeId(rangeTypeId);
            }
            List<Field> attachedTmfieldList = new ArrayList<Field>();
            for (Field tmfieldListFieldToAttach : range.getTmfieldList()) {
                tmfieldListFieldToAttach = em.getReference(tmfieldListFieldToAttach.getClass(), tmfieldListFieldToAttach.getId());
                attachedTmfieldList.add(tmfieldListFieldToAttach);
            }
            range.setTmfieldList(attachedTmfieldList);
            List<Field> attachedTMFieldList = new ArrayList<Field>();
            for (Field TMFieldListFieldToAttach : range.getTMFieldList()) {
                TMFieldListFieldToAttach = em.getReference(TMFieldListFieldToAttach.getClass(), TMFieldListFieldToAttach.getId());
                attachedTMFieldList.add(TMFieldListFieldToAttach);
            }
            range.setTMFieldList(attachedTMFieldList);
            em.persist(range);
            if (rangeTypeId != null) {
                rangeTypeId.getRangeList().add(range);
                rangeTypeId = em.merge(rangeTypeId);
            }
            for (Field tmfieldListField : range.getTmfieldList()) {
                Range oldRangeIdOfTmfieldListField = tmfieldListField.getRangeId();
                tmfieldListField.setRangeId(range);
                tmfieldListField = em.merge(tmfieldListField);
                if (oldRangeIdOfTmfieldListField != null) {
                    oldRangeIdOfTmfieldListField.getTmfieldList().remove(tmfieldListField);
                    oldRangeIdOfTmfieldListField = em.merge(oldRangeIdOfTmfieldListField);
                }
            }
            for (Field TMFieldListField : range.getTMFieldList()) {
                Range oldRangeIdOfTMFieldListField = TMFieldListField.getRangeId();
                TMFieldListField.setRangeId(range);
                TMFieldListField = em.merge(TMFieldListField);
                if (oldRangeIdOfTMFieldListField != null) {
                    oldRangeIdOfTMFieldListField.getTMFieldList().remove(TMFieldListField);
                    oldRangeIdOfTMFieldListField = em.merge(oldRangeIdOfTMFieldListField);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Range range) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Range persistentRange = em.find(Range.class, range.getId());
            RangeType rangeTypeIdOld = persistentRange.getRangeTypeId();
            RangeType rangeTypeIdNew = range.getRangeTypeId();
            List<Field> tmfieldListOld = persistentRange.getTmfieldList();
            List<Field> tmfieldListNew = range.getTmfieldList();
            List<Field> TMFieldListOld = persistentRange.getTMFieldList();
            List<Field> TMFieldListNew = range.getTMFieldList();
            List<String> illegalOrphanMessages = null;
            for (Field tmfieldListOldField : tmfieldListOld) {
                if (!tmfieldListNew.contains(tmfieldListOldField)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Field " + tmfieldListOldField + " since its rangeId field is not nullable.");
                }
            }
            for (Field TMFieldListOldField : TMFieldListOld) {
                if (!TMFieldListNew.contains(TMFieldListOldField)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Field " + TMFieldListOldField + " since its rangeId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (rangeTypeIdNew != null) {
                rangeTypeIdNew = em.getReference(rangeTypeIdNew.getClass(), rangeTypeIdNew.getId());
                range.setRangeTypeId(rangeTypeIdNew);
            }
            List<Field> attachedTmfieldListNew = new ArrayList<Field>();
            for (Field tmfieldListNewFieldToAttach : tmfieldListNew) {
                tmfieldListNewFieldToAttach = em.getReference(tmfieldListNewFieldToAttach.getClass(), tmfieldListNewFieldToAttach.getId());
                attachedTmfieldListNew.add(tmfieldListNewFieldToAttach);
            }
            tmfieldListNew = attachedTmfieldListNew;
            range.setTmfieldList(tmfieldListNew);
            List<Field> attachedTMFieldListNew = new ArrayList<Field>();
            for (Field TMFieldListNewFieldToAttach : TMFieldListNew) {
                TMFieldListNewFieldToAttach = em.getReference(TMFieldListNewFieldToAttach.getClass(), TMFieldListNewFieldToAttach.getId());
                attachedTMFieldListNew.add(TMFieldListNewFieldToAttach);
            }
            TMFieldListNew = attachedTMFieldListNew;
            range.setTMFieldList(TMFieldListNew);
            range = em.merge(range);
            if (rangeTypeIdOld != null && !rangeTypeIdOld.equals(rangeTypeIdNew)) {
                rangeTypeIdOld.getRangeList().remove(range);
                rangeTypeIdOld = em.merge(rangeTypeIdOld);
            }
            if (rangeTypeIdNew != null && !rangeTypeIdNew.equals(rangeTypeIdOld)) {
                rangeTypeIdNew.getRangeList().add(range);
                rangeTypeIdNew = em.merge(rangeTypeIdNew);
            }
            for (Field tmfieldListNewField : tmfieldListNew) {
                if (!tmfieldListOld.contains(tmfieldListNewField)) {
                    Range oldRangeIdOfTmfieldListNewField = tmfieldListNewField.getRangeId();
                    tmfieldListNewField.setRangeId(range);
                    tmfieldListNewField = em.merge(tmfieldListNewField);
                    if (oldRangeIdOfTmfieldListNewField != null && !oldRangeIdOfTmfieldListNewField.equals(range)) {
                        oldRangeIdOfTmfieldListNewField.getTmfieldList().remove(tmfieldListNewField);
                        oldRangeIdOfTmfieldListNewField = em.merge(oldRangeIdOfTmfieldListNewField);
                    }
                }
            }
            for (Field TMFieldListNewField : TMFieldListNew) {
                if (!TMFieldListOld.contains(TMFieldListNewField)) {
                    Range oldRangeIdOfTMFieldListNewField = TMFieldListNewField.getRangeId();
                    TMFieldListNewField.setRangeId(range);
                    TMFieldListNewField = em.merge(TMFieldListNewField);
                    if (oldRangeIdOfTMFieldListNewField != null && !oldRangeIdOfTMFieldListNewField.equals(range)) {
                        oldRangeIdOfTMFieldListNewField.getTMFieldList().remove(TMFieldListNewField);
                        oldRangeIdOfTMFieldListNewField = em.merge(oldRangeIdOfTMFieldListNewField);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = range.getId();
                if (findRange(id) == null) {
                    throw new NonexistentEntityException("The range with id " + id + " no longer exists.");
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
            Range range;
            try {
                range = em.getReference(Range.class, id);
                range.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The range with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Field> tmfieldListOrphanCheck = range.getTmfieldList();
            for (Field tmfieldListOrphanCheckField : tmfieldListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Range (" + range + ") cannot be destroyed since the Field " + tmfieldListOrphanCheckField + " in its tmfieldList field has a non-nullable rangeId field.");
            }
            List<Field> TMFieldListOrphanCheck = range.getTMFieldList();
            for (Field TMFieldListOrphanCheckField : TMFieldListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Range (" + range + ") cannot be destroyed since the Field " + TMFieldListOrphanCheckField + " in its TMFieldList field has a non-nullable rangeId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            RangeType rangeTypeId = range.getRangeTypeId();
            if (rangeTypeId != null) {
                rangeTypeId.getRangeList().remove(range);
                rangeTypeId = em.merge(rangeTypeId);
            }
            em.remove(range);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Range> findRangeEntities() {
        return findRangeEntities(true, -1, -1);
    }

    public List<Range> findRangeEntities(int maxResults, int firstResult) {
        return findRangeEntities(false, maxResults, firstResult);
    }

    private List<Range> findRangeEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Range.class));
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

    public Range findRange(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Range.class, id);
        } finally {
            em.close();
        }
    }

    public int getRangeCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Range> rt = cq.from(Range.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
