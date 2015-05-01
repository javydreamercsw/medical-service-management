package net.sourceforge.javydreamercsw.tm.server;

import java.util.ArrayList;
import net.sourceforge.javydreamercsw.tm.PersonHasService;
import net.sourceforge.javydreamercsw.tm.Service;
import net.sourceforge.javydreamercsw.tm.TMField;
import net.sourceforge.javydreamercsw.tm.controller.ServiceJpaController;
import net.sourceforge.javydreamercsw.tm.db.EntityServer;
import net.sourceforge.javydreamercsw.tm.db.manager.DataBaseManager;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class ServiceServer extends Service implements EntityServer<Service> {

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
    public Service getEntity() throws IllegalArgumentException {
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
