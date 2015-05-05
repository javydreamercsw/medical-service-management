package net.sourceforge.javydreamercsw.msm.server;

import java.util.ArrayList;
import net.sourceforge.javydreamercsw.msm.db.FieldType;
import net.sourceforge.javydreamercsw.msm.db.TMField;
import net.sourceforge.javydreamercsw.msm.controller.FieldTypeJpaController;
import net.sourceforge.javydreamercsw.msm.db.manager.DataBaseManager;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public final class FieldTypeServer extends FieldType implements EntityServer<FieldType> {
    
    public FieldTypeServer(int id){
        super();
        setId(id);
        update();
    }

    public FieldTypeServer(FieldType ft) {
        update(FieldTypeServer.this, ft);
    }
    
    public FieldTypeServer(String name){
        super();
        setName(name);
        setTmfieldList(new ArrayList<TMField>());
    }

    @Override
    public int write2DB() throws Exception {
        if (getId() != null && getId() > 0) {
            FieldType entity = getEntity();
            update(entity, this);
            new FieldTypeJpaController(DataBaseManager.getEntityManagerFactory()).edit(entity);
            setId(getEntity().getId());
        } else {
            FieldType a = new FieldType();
            update(a, this);
            new FieldTypeJpaController(DataBaseManager.getEntityManagerFactory()).create(a);
            setId(a.getId());
            update();
        }
        return getId();
    }

    @Override
    public FieldType getEntity() throws IllegalArgumentException {
        FieldTypeJpaController c
                = new FieldTypeJpaController(DataBaseManager.getEntityManagerFactory());
        return c.findFieldType(getId());
    }

    @Override
    public void update(FieldType target, FieldType source) {
        target.setId(source.getId());
        target.setName(source.getName());
        if (target.getTmfieldList() == null) {
            target.setTmfieldList(new ArrayList<TMField>());
        } else {
            target.getTmfieldList().clear();
        }
        target.getTmfieldList().addAll(source.getTmfieldList());
    }

    @Override
    public void update() {
        update(this, getEntity());
    }
}
