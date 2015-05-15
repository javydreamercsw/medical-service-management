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
import net.sourceforge.javydreamercsw.msm.db.RangeType;
import net.sourceforge.javydreamercsw.msm.db.Field;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import net.sourceforge.javydreamercsw.msm.controller.exceptions.IllegalOrphanException;
import net.sourceforge.javydreamercsw.msm.controller.exceptions.NonexistentEntityException;
import net.sourceforge.javydreamercsw.msm.controller.exceptions.PreexistingEntityException;
import net.sourceforge.javydreamercsw.msm.db.Range;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class RangeJpaController implements Serializable {

    public RangeJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Range range) throws PreexistingEntityException, Exception {
        if (range.getTmfieldList() == null) {
            range.setTmfieldList(new ArrayList<Field>());
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
            for (Field tmfieldListTMFieldToAttach : range.getTmfieldList()) {
                tmfieldListTMFieldToAttach = em.getReference(tmfieldListTMFieldToAttach.getClass(), tmfieldListTMFieldToAttach.getId());
                attachedTmfieldList.add(tmfieldListTMFieldToAttach);
            }
            range.setTmfieldList(attachedTmfieldList);
            em.persist(range);
            if (rangeTypeId != null) {
                rangeTypeId.getRangeList().add(range);
                rangeTypeId = em.merge(rangeTypeId);
            }
            for (Field tmfieldListTMField : range.getTmfieldList()) {
                Range oldRangeIdOfTmfieldListTMField = tmfieldListTMField.getRangeId();
                tmfieldListTMField.setRangeId(range);
                tmfieldListTMField = em.merge(tmfieldListTMField);
                if (oldRangeIdOfTmfieldListTMField != null) {
                    oldRangeIdOfTmfieldListTMField.getTmfieldList().remove(tmfieldListTMField);
                    oldRangeIdOfTmfieldListTMField = em.merge(oldRangeIdOfTmfieldListTMField);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findRange(range.getId()) != null) {
                throw new PreexistingEntityException("Range " + range + " already exists.", ex);
            }
            throw ex;
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
            List<String> illegalOrphanMessages = null;
            for (Field tmfieldListOldTMField : tmfieldListOld) {
                if (!tmfieldListNew.contains(tmfieldListOldTMField)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain TMField " + tmfieldListOldTMField + " since its rangeId field is not nullable.");
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
            for (Field tmfieldListNewTMFieldToAttach : tmfieldListNew) {
                tmfieldListNewTMFieldToAttach = em.getReference(tmfieldListNewTMFieldToAttach.getClass(), tmfieldListNewTMFieldToAttach.getId());
                attachedTmfieldListNew.add(tmfieldListNewTMFieldToAttach);
            }
            tmfieldListNew = attachedTmfieldListNew;
            range.setTmfieldList(tmfieldListNew);
            range = em.merge(range);
            if (rangeTypeIdOld != null && !rangeTypeIdOld.equals(rangeTypeIdNew)) {
                rangeTypeIdOld.getRangeList().remove(range);
                rangeTypeIdOld = em.merge(rangeTypeIdOld);
            }
            if (rangeTypeIdNew != null && !rangeTypeIdNew.equals(rangeTypeIdOld)) {
                rangeTypeIdNew.getRangeList().add(range);
                rangeTypeIdNew = em.merge(rangeTypeIdNew);
            }
            for (Field tmfieldListNewTMField : tmfieldListNew) {
                if (!tmfieldListOld.contains(tmfieldListNewTMField)) {
                    Range oldRangeIdOfTmfieldListNewTMField = tmfieldListNewTMField.getRangeId();
                    tmfieldListNewTMField.setRangeId(range);
                    tmfieldListNewTMField = em.merge(tmfieldListNewTMField);
                    if (oldRangeIdOfTmfieldListNewTMField != null && !oldRangeIdOfTmfieldListNewTMField.equals(range)) {
                        oldRangeIdOfTmfieldListNewTMField.getTmfieldList().remove(tmfieldListNewTMField);
                        oldRangeIdOfTmfieldListNewTMField = em.merge(oldRangeIdOfTmfieldListNewTMField);
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
            for (Field tmfieldListOrphanCheckTMField : tmfieldListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Range (" + range + ") cannot be destroyed since the TMField " + tmfieldListOrphanCheckTMField + " in its tmfieldList field has a non-nullable rangeId field.");
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
