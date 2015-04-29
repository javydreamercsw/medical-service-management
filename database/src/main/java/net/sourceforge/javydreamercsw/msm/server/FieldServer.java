package net.sourceforge.javydreamercsw.msm.server;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import net.sourceforge.javydreamercsw.msm.db.Service;
import net.sourceforge.javydreamercsw.msm.db.TMField;
import net.sourceforge.javydreamercsw.msm.controller.TmfieldJpaController;
import net.sourceforge.javydreamercsw.msm.db.manager.DataBaseManager;
import net.sourceforge.javydreamercsw.msm.db.manager.TMException;

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
        setServiceList(new ArrayList<Service>());
    }

    @Override
    public int write2DB() throws Exception {
        if (getId() != null && getId() > 0) {
            update(getEntity(), this);
            new TmfieldJpaController(DataBaseManager.getEntityManagerFactory()).edit(getEntity());
            setId(getEntity().getId());
        } else {
            TMField a = new TMField();
            update(a, this);
            new TmfieldJpaController(DataBaseManager.getEntityManagerFactory()).create(a);
            setId(a.getId());
            update();
        }
        return getId();
    }

    @Override
    public TMField getEntity() throws IllegalArgumentException {
        TmfieldJpaController c
                = new TmfieldJpaController(DataBaseManager.getEntityManagerFactory());
        return c.findTmfield(getId());
    }

    @Override
    public void update(TMField target, TMField source) {
        target.setId(source.getId());
        target.setName(source.getName());
        target.setDesc(source.getDesc());
        target.setRangeId(source.getRangeId());
        target.setFieldTypeId(source.getFieldTypeId());
        if (target.getServiceList() == null) {
            target.setServiceList(new ArrayList<Service>());
        } else {
            target.getServiceList().clear();
        }
        target.getServiceList().addAll(source.getServiceList());
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

    public static TMField createStringField(String name, String desc) throws TMException {
        return createField(name, desc, 0, 0, 1);
    }

    private static TMField createField(String name, String desc, int min,
            int max, int type) throws TMException {
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
                throw new TMException(ex);
            } catch (Exception ex) {
                throw new TMException(ex);
            }
            try {
                newField.write2DB();
            } catch (Exception ex) {
                throw new TMException(ex);
            }
            result = newField.getEntity();
        } else {
            throw new TMException("Field '" + name + "' already exists!");
        }
        return result;
    }

    public static TMField createFloatField(String name, String desc, int min,
            int max) throws TMException {
        return createField(name, desc, min, max, 3);
    }

    public static TMField createIntField(String name, String desc, int min,
            int max) throws TMException {
        return createField(name, desc, min, max, 2);
    }

    public static TMField createBoolField(String name, String desc)
            throws TMException {
        return createField(name, desc, 0, 0, 4);
    }
}
