package net.sourceforge.javydreamercsw.msm.server;

import java.util.ArrayList;
import net.sourceforge.javydreamercsw.msm.db.PersonHasService;
import net.sourceforge.javydreamercsw.msm.db.Service;
import net.sourceforge.javydreamercsw.msm.db.TMField;
import net.sourceforge.javydreamercsw.msm.controller.ServiceJpaController;
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
        setPersonHasServiceList(new ArrayList<PersonHasService>());
        setTmfieldList(new ArrayList<TMField>());
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
        if (target.getPersonHasServiceList() == null) {
            target.setPersonHasServiceList(new ArrayList<PersonHasService>());
        } else {
            target.getPersonHasServiceList().clear();
        }
        target.getPersonHasServiceList().addAll(source.getPersonHasServiceList());
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
