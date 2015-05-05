package net.sourceforge.javydreamercsw.msm.server;

import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import static net.sourceforge.javydreamercsw.msm.db.manager.DataBaseManager.getEntityManager;

/**
 *
 * @author root
 * @param <T>
 */
public class EntityListener<T> {
    private static final Logger LOG =
            Logger.getLogger(EntityListener.class.getSimpleName());

    private boolean constraintViolationsDetected(T entity) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<T>> constraintViolations = validator.validate(entity);
        if (constraintViolations.size() > 0) {
            Iterator<ConstraintViolation<T>> iterator = constraintViolations.iterator();
            while (iterator.hasNext()) {
                ConstraintViolation<T> cv = iterator.next();
                LOG.log(Level.SEVERE, "{0}.{1} {2}", 
                        new Object[]{cv.getRootBeanClass().getName(), 
                            cv.getPropertyPath(), cv.getMessage()});
            }
            return true;
        } else {
            return false;
        }
    }

    @PrePersist
    public void create(T entity) {
        if (!constraintViolationsDetected(entity)) {
            getEntityManager().persist(entity);
        }
    }

    @PreUpdate
    public T edit(T entity) {
        if (!constraintViolationsDetected(entity)) {
            return getEntityManager().merge(entity);
        } else {
            return entity;
        }
    }

    //TODO: It cause StackOverflowError 
    //@PreRemove
    public void remove(T entity) {
        if (!constraintViolationsDetected(entity)) {
            getEntityManager().remove(getEntityManager().merge(entity));
        }
    }
}
