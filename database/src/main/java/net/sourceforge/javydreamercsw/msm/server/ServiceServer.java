package net.sourceforge.javydreamercsw.msm.server;

import java.util.ArrayList;
import net.sourceforge.javydreamercsw.msm.controller.ServiceHasFieldJpaController;
import net.sourceforge.javydreamercsw.msm.db.Service;
import net.sourceforge.javydreamercsw.msm.db.TMField;
import net.sourceforge.javydreamercsw.msm.controller.ServiceJpaController;
import net.sourceforge.javydreamercsw.msm.db.ServiceHasField;
import net.sourceforge.javydreamercsw.msm.db.ServiceInstance;
import net.sourceforge.javydreamercsw.msm.db.manager.DataBaseManager;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class ServiceServer extends Service implements EntityServer<Service> {

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

    @Override
    public int write2DB() throws Exception {
        if (getId() != null && getId() > 0) {
            update(getEntity(), this);
            new ServiceJpaController(DataBaseManager.getEntityManagerFactory()).edit(getEntity());
            setId(getEntity().getId());
        } else {
            Service a = new Service();
            update(a, this);
            new ServiceJpaController(DataBaseManager.getEntityManagerFactory()).create(a);
            setId(a.getId());
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

    public void addField(TMField field, int index) throws Exception {
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
                    new ServiceHasFieldJpaController(DataBaseManager.getEntityManagerFactory()).edit(s);
                }
            }
        }
        new ServiceHasFieldJpaController(DataBaseManager.getEntityManagerFactory()).create(shf);
        update();
    }
}
