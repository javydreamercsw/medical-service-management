package net.sourceforge.javydreamercsw.msm.server;

import java.util.ArrayList;
import net.sourceforge.javydreamercsw.msm.controller.ServiceHasFieldJpaController;
import net.sourceforge.javydreamercsw.msm.db.Service;
import net.sourceforge.javydreamercsw.msm.db.TMField;
import net.sourceforge.javydreamercsw.msm.controller.ServiceJpaController;
import net.sourceforge.javydreamercsw.msm.db.ServiceHasField;
import net.sourceforge.javydreamercsw.msm.db.ServiceInstance;
import net.sourceforge.javydreamercsw.msm.db.manager.DataBaseManager;
import net.sourceforge.javydreamercsw.msm.db.manager.MSMException;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public final class ServiceServer extends Service implements EntityServer<Service> {

    public ServiceServer(int id) {
        setId(id);
        update();
    }

    public ServiceServer(Service a) {
        update(ServiceServer.this, a);
    }

    public ServiceServer(String name) {
        super();
        setName(name);
        setServiceHasFieldList(new ArrayList<ServiceHasField>());
        setServiceInstanceList(new ArrayList<ServiceInstance>());
    }

    private void proccessFields() throws Exception {
        ServiceHasFieldJpaController controller
                = new ServiceHasFieldJpaController(DataBaseManager.getEntityManagerFactory());
        for (ServiceHasField shf : getServiceHasFieldList()) {
            if (controller.findServiceHasField(shf.getServiceHasFieldPK()) == null) {
                controller.create(shf);
            } else {
                controller.edit(shf);
            }
        }
    }

    @Override
    public int write2DB() throws Exception {
        if (getId() != null && getId() > 0) {
            Service entity = getEntity();
            update(entity, this);
            proccessFields();
            update();
            new ServiceJpaController(DataBaseManager.getEntityManagerFactory()).edit(entity);
            setId(getEntity().getId());
        } else {
            Service a = new Service();
            update(a, this);
            new ServiceJpaController(DataBaseManager.getEntityManagerFactory()).create(a);
            setId(a.getId());
            proccessFields();
            update();
        }
        return getId();
    }

    @Override
    public Service getEntity() {
        ServiceJpaController c
                = new ServiceJpaController(DataBaseManager.getEntityManagerFactory());
        return c.findService(getId());
    }

    @Override
    public void update(Service target, Service source) {
        target.setId(source.getId());
        target.setName(source.getName());
        if (target.getServiceHasFieldList() == null) {
            target.setServiceHasFieldList(new ArrayList<ServiceHasField>());
        } else {
            target.getServiceHasFieldList().clear();
        }
        target.getServiceHasFieldList().addAll(source.getServiceHasFieldList());
        if (target.getServiceInstanceList() == null) {
            target.setServiceInstanceList(new ArrayList<ServiceInstance>());
        } else {
            target.getServiceInstanceList().clear();
        }
        target.getServiceInstanceList().addAll(source.getServiceInstanceList());
    }

    @Override
    public void update() {
        update(this, getEntity());
    }

    /**
     * Add afield to the service.
     *
     * @param field Field to add.
     * @param index Order on the list of fields. (Field is inserted at the
     * specified spot.
     * @throws Exception
     */
    public void addField(TMField field, int index) throws Exception {
        if (getId() == null) {
            throw new MSMException("Service must exist in DB before adding fields to it!");
        } else {
            ServiceHasField shf = new ServiceHasField(getId(), field.getId());
            shf.setIndex(index);
            shf.setTmfield(field);
            shf.setService(getEntity());
            //Check if there's a field on that spot
            boolean exists = false;
            for (ServiceHasField s : getServiceHasFieldList()) {
                if (s.getIndex() == index) {
                    exists = true;
                    break;
                }
            }
            if (exists) {
                //Insert into specified index and push the others
                for (ServiceHasField s : getServiceHasFieldList()) {
                    if (s.getIndex() >= index) {
                        s.setIndex(s.getIndex() + 1);
                    }
                }
            }
            getServiceHasFieldList().add(shf);
        }
    }
}
