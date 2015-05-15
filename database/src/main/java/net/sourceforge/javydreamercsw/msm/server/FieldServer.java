package net.sourceforge.javydreamercsw.msm.server;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import net.sourceforge.javydreamercsw.msm.controller.FieldJpaController;
import net.sourceforge.javydreamercsw.msm.db.Field;
import net.sourceforge.javydreamercsw.msm.db.InstanceField;
import net.sourceforge.javydreamercsw.msm.db.Range;
import net.sourceforge.javydreamercsw.msm.db.ServiceHasField;
import net.sourceforge.javydreamercsw.msm.db.manager.DataBaseManager;
import net.sourceforge.javydreamercsw.msm.db.manager.MSMException;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public final class FieldServer extends Field implements EntityServer<Field> {

    public FieldServer(int id) {
        setId(id);
        update();
    }

    public FieldServer(Field a) {
        update(FieldServer.this, a);
    }

    public FieldServer(String name) {
        super();
        setName(name);
        setInstanceFieldList(new ArrayList<InstanceField>());
        setServiceHasFieldList(new ArrayList<ServiceHasField>());
        setRangeList(new ArrayList<Range>());
    }

    @Override
    public int write2DB() throws Exception {
        if (getId() != null && getId() > 0) {
            Field entity = getEntity();
            update(entity, this);
            new FieldJpaController(DataBaseManager.getEntityManagerFactory()).edit(entity);
            setId(getEntity().getId());
        } else {
            Field a = new Field();
            update(a, this);
            new FieldJpaController(DataBaseManager.getEntityManagerFactory()).create(a);
            setId(a.getId());
            update();
        }
        return getId();
    }

    @Override
    public Field getEntity() throws IllegalArgumentException {
        FieldJpaController c
                = new FieldJpaController(DataBaseManager.getEntityManagerFactory());
        return c.findTMField(getId());
    }

    @Override
    public void update(Field target, Field source) {
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
        if (target.getRangeList() == null) {
            target.setRangeList(new ArrayList<Range>());
        } else {
            target.getRangeList().clear();
        }
        target.getRangeList().addAll(source.getRangeList());
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

    public static Field createStringField(String name, String desc) throws MSMException {
        return createField(name, desc, 0, 0, 1);
    }

    private static Field createField(String name, String desc, int min,
            int max, int type) throws MSMException {
        Field result = null;
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

    public static Field createFloatField(String name, String desc, int min,
            int max) throws MSMException {
        return createField(name, desc, min, max, 3);
    }

    public static Field createIntField(String name, String desc, int min,
            int max) throws MSMException {
        return createField(name, desc, min, max, 2);
    }

    public static Field createBoolField(String name, String desc)
            throws MSMException {
        return createField(name, desc, 0, 0, 4);
    }
}
