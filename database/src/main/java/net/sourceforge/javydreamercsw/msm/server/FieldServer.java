package net.sourceforge.javydreamercsw.msm.server;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import net.sourceforge.javydreamercsw.msm.controller.TMFieldJpaController;
import net.sourceforge.javydreamercsw.msm.db.TMField;
import net.sourceforge.javydreamercsw.msm.db.InstanceField;
import net.sourceforge.javydreamercsw.msm.db.ServiceHasField;
import net.sourceforge.javydreamercsw.msm.db.manager.DataBaseManager;
import net.sourceforge.javydreamercsw.msm.db.manager.MSMException;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class FieldServer extends TMField implements EntityServer<TMField> {

    public FieldServer(int id) {
        setId(id);
        update();
    }

    public FieldServer(TMField a) {
        update(FieldServer.this, a);
    }

    public FieldServer(String name) {
        super();
        setName(name);
        setInstanceFieldList(new ArrayList<InstanceField>());
        setServiceHasFieldList(new ArrayList<ServiceHasField>());
    }

    @Override
    public int write2DB() throws Exception {
        if (getId() != null && getId() > 0) {
            update(getEntity(), this);
            new TMFieldJpaController(DataBaseManager.getEntityManagerFactory()).edit(getEntity());
            setId(getEntity().getId());
        } else {
            TMField a = new TMField();
            update(a, this);
            new TMFieldJpaController(DataBaseManager.getEntityManagerFactory()).create(a);
            setId(a.getId());
            update();
        }
        return getId();
    }

    @Override
    public TMField getEntity() throws IllegalArgumentException {
        TMFieldJpaController c
                = new TMFieldJpaController(DataBaseManager.getEntityManagerFactory());
        return c.findTMField(getId());
    }

    @Override
    public void update(TMField target, TMField source) {
        target.setId(source.getId());
        target.setName(source.getName());
        target.setDesc(source.getDesc());
        target.setRangeId(source.getRangeId());
        target.setFieldTypeId(source.getFieldTypeId());
        if (target.getInstanceFieldList() == null) {
            target.setInstanceFieldList(new ArrayList<InstanceField>());
        } else {
            target.getInstanceFieldList().clear();
        }
        target.getInstanceFieldList().addAll(source.getInstanceFieldList());
        if (target.getServiceHasFieldList() == null) {
            target.setServiceHasFieldList(new ArrayList<ServiceHasField>());
        } else {
            target.getServiceHasFieldList().clear();
        }
        target.getServiceHasFieldList().addAll(source.getServiceHasFieldList());
    }

    @Override
    public void update() {
        update(this, getEntity());
    }

    private static boolean fieldExist(String name) {
        parameters.clear();
        parameters.put("name", name);
        return !DataBaseManager.namedQuery("TMField.findByName", parameters).isEmpty();
    }

    public static TMField createStringField(String name, String desc) throws MSMException {
        return createField(name, desc, 0, 0, 1);
    }

    private static TMField createField(String name, String desc, int min,
            int max, int type) throws MSMException {
        TMField result = null;
        if (!fieldExist(name)) {
            //Create it
            FieldServer newField = new FieldServer(name);
            newField.setFieldTypeId(new FieldTypeServer(type).getEntity());
            try {
                newField.setDesc(desc.getBytes("UTF-8"));
                if (type == 2 || type == 3) {
                    //Create range
                    RangeTypeServer rts = new RangeTypeServer(name);
                    rts.write2DB();

                    RangeServer rs = new RangeServer(min, max);
                    rs.setRangeTypeId(rts.getEntity());
                    rs.write2DB();

                    newField.setRangeId(rs.getEntity());
                }
            } catch (UnsupportedEncodingException ex) {
                throw new MSMException(ex);
            } catch (Exception ex) {
                throw new MSMException(ex);
            }
            try {
                newField.write2DB();
            } catch (Exception ex) {
                throw new MSMException(ex);
            }
            result = newField.getEntity();
        } else {
            throw new MSMException("Field '" + name + "' already exists!");
        }
        return result;
    }

    public static TMField createFloatField(String name, String desc, int min,
            int max) throws MSMException {
        return createField(name, desc, min, max, 3);
    }

    public static TMField createIntField(String name, String desc, int min,
            int max) throws MSMException {
        return createField(name, desc, min, max, 2);
    }

    public static TMField createBoolField(String name, String desc)
            throws MSMException {
        return createField(name, desc, 0, 0, 4);
    }
}
