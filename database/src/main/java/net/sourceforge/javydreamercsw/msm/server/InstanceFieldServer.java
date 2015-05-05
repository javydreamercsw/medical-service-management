package net.sourceforge.javydreamercsw.msm.server;

import net.sourceforge.javydreamercsw.msm.controller.InstanceFieldJpaController;
import net.sourceforge.javydreamercsw.msm.db.InstanceField;
import net.sourceforge.javydreamercsw.msm.db.InstanceFieldPK;
import net.sourceforge.javydreamercsw.msm.db.manager.DataBaseManager;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public final class InstanceFieldServer extends InstanceField
        implements EntityServer<InstanceField> {

    public InstanceFieldServer(int id) {
        setInstanceFieldPK(new InstanceFieldPK(id));
    }

    public InstanceFieldServer(InstanceField a) {
        update(InstanceFieldServer.this, a);
    }

    @Override
    public int write2DB() throws Exception {
        if (getInstanceFieldPK()!= null && getInstanceFieldPK().getId() > 0) {
            InstanceField entity = getEntity();
            update(entity, this);
            new InstanceFieldJpaController(DataBaseManager.getEntityManagerFactory()).edit(entity);
            setInstanceFieldPK(getInstanceFieldPK());
        } else {
            InstanceField a = new InstanceField();
            update(a, this);
            new InstanceFieldJpaController(DataBaseManager.getEntityManagerFactory()).create(a);
            setInstanceFieldPK(a.getInstanceFieldPK());
            update();
        }
        return getInstanceFieldPK().getId();
    }

    @Override
    public InstanceField getEntity() throws IllegalArgumentException {
        InstanceFieldJpaController c
                = new InstanceFieldJpaController(DataBaseManager.getEntityManagerFactory());
        return c.findInstanceField(getInstanceFieldPK());
    }

    @Override
    public void update(InstanceField target, InstanceField source) {
        target.setInstanceFieldPK(source.getInstanceFieldPK());
        target.setBoolVal(source.getBoolVal());
        target.setFloatVal(source.getFloatVal());
        target.setIntVal(source.getIntVal());
        target.setServiceInstance(source.getServiceInstance());
        target.setStringVal(source.getStringVal());
    }

    @Override
    public void update() {
        update(this, getEntity());
    }
}
