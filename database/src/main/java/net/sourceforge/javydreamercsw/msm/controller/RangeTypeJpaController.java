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
import net.sourceforge.javydreamercsw.msm.db.Range;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import net.sourceforge.javydreamercsw.msm.controller.exceptions.IllegalOrphanException;
import net.sourceforge.javydreamercsw.msm.controller.exceptions.NonexistentEntityException;
import net.sourceforge.javydreamercsw.msm.controller.exceptions.PreexistingEntityException;
import net.sourceforge.javydreamercsw.msm.db.RangeType;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class RangeTypeJpaController implements Serializable {

    public RangeTypeJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(RangeType rangeType) throws PreexistingEntityException, Exception {
        if (rangeType.getRangeList() == null) {
            rangeType.setRangeList(new ArrayList<Range>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Range> attachedRangeList = new ArrayList<Range>();
            for (Range rangeListRangeToAttach : rangeType.getRangeList()) {
                rangeListRangeToAttach = em.getReference(rangeListRangeToAttach.getClass(), rangeListRangeToAttach.getId());
                attachedRangeList.add(rangeListRangeToAttach);
            }
            rangeType.setRangeList(attachedRangeList);
            em.persist(rangeType);
            for (Range rangeListRange : rangeType.getRangeList()) {
                RangeType oldRangeTypeIdOfRangeListRange = rangeListRange.getRangeTypeId();
                rangeListRange.setRangeTypeId(rangeType);
                rangeListRange = em.merge(rangeListRange);
                if (oldRangeTypeIdOfRangeListRange != null) {
                    oldRangeTypeIdOfRangeListRange.getRangeList().remove(rangeListRange);
                    oldRangeTypeIdOfRangeListRange = em.merge(oldRangeTypeIdOfRangeListRange);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findRangeType(rangeType.getId()) != null) {
                throw new PreexistingEntityException("RangeType " + rangeType + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(RangeType rangeType) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            RangeType persistentRangeType = em.find(RangeType.class, rangeType.getId());
            List<Range> rangeListOld = persistentRangeType.getRangeList();
            List<Range> rangeListNew = rangeType.getRangeList();
            List<String> illegalOrphanMessages = null;
            for (Range rangeListOldRange : rangeListOld) {
                if (!rangeListNew.contains(rangeListOldRange)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Range " + rangeListOldRange + " since its rangeTypeId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Range> attachedRangeListNew = new ArrayList<Range>();
            for (Range rangeListNewRangeToAttach : rangeListNew) {
                rangeListNewRangeToAttach = em.getReference(rangeListNewRangeToAttach.getClass(), rangeListNewRangeToAttach.getId());
                attachedRangeListNew.add(rangeListNewRangeToAttach);
            }
            rangeListNew = attachedRangeListNew;
            rangeType.setRangeList(rangeListNew);
            rangeType = em.merge(rangeType);
            for (Range rangeListNewRange : rangeListNew) {
                if (!rangeListOld.contains(rangeListNewRange)) {
                    RangeType oldRangeTypeIdOfRangeListNewRange = rangeListNewRange.getRangeTypeId();
                    rangeListNewRange.setRangeTypeId(rangeType);
                    rangeListNewRange = em.merge(rangeListNewRange);
                    if (oldRangeTypeIdOfRangeListNewRange != null && !oldRangeTypeIdOfRangeListNewRange.equals(rangeType)) {
                        oldRangeTypeIdOfRangeListNewRange.getRangeList().remove(rangeListNewRange);
                        oldRangeTypeIdOfRangeListNewRange = em.merge(oldRangeTypeIdOfRangeListNewRange);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = rangeType.getId();
                if (findRangeType(id) == null) {
                    throw new NonexistentEntityException("The rangeType with id " + id + " no longer exists.");
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
            RangeType rangeType;
            try {
                rangeType = em.getReference(RangeType.class, id);
                rangeType.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The rangeType with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Range> rangeListOrphanCheck = rangeType.getRangeList();
            for (Range rangeListOrphanCheckRange : rangeListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This RangeType (" + rangeType + ") cannot be destroyed since the Range " + rangeListOrphanCheckRange + " in its rangeList field has a non-nullable rangeTypeId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(rangeType);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<RangeType> findRangeTypeEntities() {
        return findRangeTypeEntities(true, -1, -1);
    }

    public List<RangeType> findRangeTypeEntities(int maxResults, int firstResult) {
        return findRangeTypeEntities(false, maxResults, firstResult);
    }

    private List<RangeType> findRangeTypeEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(RangeType.class));
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

    public RangeType findRangeType(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(RangeType.class, id);
        } finally {
            em.close();
        }
    }

    public int getRangeTypeCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<RangeType> rt = cq.from(RangeType.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
