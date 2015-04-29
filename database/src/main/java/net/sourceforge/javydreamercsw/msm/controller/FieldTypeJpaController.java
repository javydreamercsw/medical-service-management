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
import net.sourceforge.javydreamercsw.msm.db.TMField;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import net.sourceforge.javydreamercsw.msm.db.FieldType;
import net.sourceforge.javydreamercsw.msm.controller.exceptions.IllegalOrphanException;
import net.sourceforge.javydreamercsw.msm.controller.exceptions.NonexistentEntityException;
import net.sourceforge.javydreamercsw.msm.controller.exceptions.PreexistingEntityException;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class FieldTypeJpaController implements Serializable {

    public FieldTypeJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(FieldType fieldType) throws PreexistingEntityException, Exception {
        if (fieldType.getTmfieldList() == null) {
            fieldType.setTmfieldList(new ArrayList<TMField>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<TMField> attachedTmfieldList = new ArrayList<TMField>();
            for (TMField tmfieldListTmfieldToAttach : fieldType.getTmfieldList()) {
                tmfieldListTmfieldToAttach = em.getReference(tmfieldListTmfieldToAttach.getClass(), tmfieldListTmfieldToAttach.getId());
                attachedTmfieldList.add(tmfieldListTmfieldToAttach);
            }
            fieldType.setTmfieldList(attachedTmfieldList);
            em.persist(fieldType);
            for (TMField tmfieldListTmfield : fieldType.getTmfieldList()) {
                FieldType oldFieldTypeIdOfTmfieldListTmfield = tmfieldListTmfield.getFieldTypeId();
                tmfieldListTmfield.setFieldTypeId(fieldType);
                tmfieldListTmfield = em.merge(tmfieldListTmfield);
                if (oldFieldTypeIdOfTmfieldListTmfield != null) {
                    oldFieldTypeIdOfTmfieldListTmfield.getTmfieldList().remove(tmfieldListTmfield);
                    oldFieldTypeIdOfTmfieldListTmfield = em.merge(oldFieldTypeIdOfTmfieldListTmfield);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findFieldType(fieldType.getId()) != null) {
                throw new PreexistingEntityException("FieldType " + fieldType + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(FieldType fieldType) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            FieldType persistentFieldType = em.find(FieldType.class, fieldType.getId());
            List<TMField> tmfieldListOld = persistentFieldType.getTmfieldList();
            List<TMField> tmfieldListNew = fieldType.getTmfieldList();
            List<String> illegalOrphanMessages = null;
            for (TMField tmfieldListOldTmfield : tmfieldListOld) {
                if (!tmfieldListNew.contains(tmfieldListOldTmfield)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Tmfield " + tmfieldListOldTmfield + " since its fieldTypeId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<TMField> attachedTmfieldListNew = new ArrayList<TMField>();
            for (TMField tmfieldListNewTmfieldToAttach : tmfieldListNew) {
                tmfieldListNewTmfieldToAttach = em.getReference(tmfieldListNewTmfieldToAttach.getClass(), tmfieldListNewTmfieldToAttach.getId());
                attachedTmfieldListNew.add(tmfieldListNewTmfieldToAttach);
            }
            tmfieldListNew = attachedTmfieldListNew;
            fieldType.setTmfieldList(tmfieldListNew);
            fieldType = em.merge(fieldType);
            for (TMField tmfieldListNewTmfield : tmfieldListNew) {
                if (!tmfieldListOld.contains(tmfieldListNewTmfield)) {
                    FieldType oldFieldTypeIdOfTmfieldListNewTmfield = tmfieldListNewTmfield.getFieldTypeId();
                    tmfieldListNewTmfield.setFieldTypeId(fieldType);
                    tmfieldListNewTmfield = em.merge(tmfieldListNewTmfield);
                    if (oldFieldTypeIdOfTmfieldListNewTmfield != null && !oldFieldTypeIdOfTmfieldListNewTmfield.equals(fieldType)) {
                        oldFieldTypeIdOfTmfieldListNewTmfield.getTmfieldList().remove(tmfieldListNewTmfield);
                        oldFieldTypeIdOfTmfieldListNewTmfield = em.merge(oldFieldTypeIdOfTmfieldListNewTmfield);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = fieldType.getId();
                if (findFieldType(id) == null) {
                    throw new NonexistentEntityException("The fieldType with id " + id + " no longer exists.");
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
            FieldType fieldType;
            try {
                fieldType = em.getReference(FieldType.class, id);
                fieldType.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The fieldType with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<TMField> tmfieldListOrphanCheck = fieldType.getTmfieldList();
            for (TMField tmfieldListOrphanCheckTmfield : tmfieldListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This FieldType (" + fieldType + ") cannot be destroyed since the Tmfield " + tmfieldListOrphanCheckTmfield + " in its tmfieldList field has a non-nullable fieldTypeId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(fieldType);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<FieldType> findFieldTypeEntities() {
        return findFieldTypeEntities(true, -1, -1);
    }

    public List<FieldType> findFieldTypeEntities(int maxResults, int firstResult) {
        return findFieldTypeEntities(false, maxResults, firstResult);
    }

    private List<FieldType> findFieldTypeEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(FieldType.class));
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

    public FieldType findFieldType(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(FieldType.class, id);
        } finally {
            em.close();
        }
    }

    public int getFieldTypeCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<FieldType> rt = cq.from(FieldType.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
