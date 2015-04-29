package net.sourceforge.javydreamercsw.msm.server;

import java.util.ArrayList;
import net.sourceforge.javydreamercsw.msm.controller.ServiceInstanceJpaController;
import net.sourceforge.javydreamercsw.msm.db.InstanceField;
import net.sourceforge.javydreamercsw.msm.db.PersonHasService;
import net.sourceforge.javydreamercsw.msm.db.Service;
import net.sourceforge.javydreamercsw.msm.db.ServiceInstance;
import net.sourceforge.javydreamercsw.msm.db.TMField;
import net.sourceforge.javydreamercsw.msm.db.manager.DataBaseManager;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class ServiceInstanceServer extends ServiceInstance
        implements EntityServer<ServiceInstance> {

    public ServiceInstanceServer(int id) {
        setId(id);
        update();
    }

    public ServiceInstanceServer(ServiceInstance a) {
        update(ServiceInstanceServer.this, a);
    }

    public ServiceInstanceServer() {
        super();
        setInstanceFieldList(new ArrayList<InstanceField>());
        setPersonHasServiceList(new ArrayList<PersonHasService>());
    }

    @Override
    public int write2DB() throws Exception {
        if (getId() != null && getId() > 0) {
            update(getEntity(), this);
            new ServiceInstanceJpaController(DataBaseManager.getEntityManagerFactory()).edit(getEntity());
            setId(getEntity().getId());
        } else {
            ServiceInstance a = new ServiceInstance();
            update(a, this);
            new ServiceInstanceJpaController(DataBaseManager.getEntityManagerFactory()).create(a);
            setId(a.getId());
            update();
        }
        return getId();
    }

    @Override
    public ServiceInstance getEntity() throws IllegalArgumentException {
        ServiceInstanceJpaController c
                = new ServiceInstanceJpaController(DataBaseManager.getEntityManagerFactory());
        return c.findServiceInstance(getId());
    }

    @Override
    public void update(ServiceInstance target, ServiceInstance source) {
        target.setId(source.getId());
        target.setServiceId(source.getServiceId());
        if (target.getInstanceFieldList() == null) {
            target.setInstanceFieldList(new ArrayList<InstanceField>());
        } else {
            target.getInstanceFieldList().clear();
        }
        target.getInstanceFieldList().addAll(source.getInstanceFieldList());
        if (target.getPersonHasServiceList() == null) {
            target.setPersonHasServiceList(new ArrayList<PersonHasService>());
        } else {
            target.getPersonHasServiceList().clear();
        }
        target.getPersonHasServiceList().addAll(source.getPersonHasServiceList());
    }

    @Override
    public void update() {
        update(this, getEntity());
    }

    /**
     * Create a Service Instance based on the service template.
     *
     * @param id
     * @return Service Instance
     */
    public ServiceInstance createServiceInstance(Integer id) {
        ServiceInstance result = null;
        //Look for the template
        ServiceServer ss = new ServiceServer(id);
        Service service = ss.getEntity();
        if (service != null) {
            //Valid template, retrieve fields
            for (TMField f : service.getTmfieldList()) {
                
            }
        }
        return result;
    }
}
