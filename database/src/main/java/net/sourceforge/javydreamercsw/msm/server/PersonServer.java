package net.sourceforge.javydreamercsw.msm.server;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import net.sourceforge.javydreamercsw.msm.controller.PersonHasServiceJpaController;
import net.sourceforge.javydreamercsw.msm.controller.PersonJpaController;
import net.sourceforge.javydreamercsw.msm.db.Person;
import net.sourceforge.javydreamercsw.msm.db.PersonHasService;
import net.sourceforge.javydreamercsw.msm.db.ServiceInstance;
import net.sourceforge.javydreamercsw.msm.db.manager.DataBaseManager;
import net.sourceforge.javydreamercsw.msm.db.manager.MSMException;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class PersonServer extends Person implements EntityServer<Person> {

    private boolean encrypted = false;

    public PersonServer(Person a) {
        update(PersonServer.this, a);
        encrypted = true;
    }

    public PersonServer(String name) {
        super();
        setName(name);
        setPersonHasServiceList(new ArrayList<PersonHasService>());
    }

    @Override
    public int write2DB() throws Exception {
        if (!encrypted) {
            setPassword(MD5.encrypt(getPassword()));
            encrypted = true;
        }
        if (getId() != null && getId() > 0) {
            update(getEntity(), this);
            new PersonJpaController(DataBaseManager.getEntityManagerFactory()).edit(getEntity());
            setId(getEntity().getId());
        } else {
            Person a = new Person();
            update(a, this);
            new PersonJpaController(DataBaseManager.getEntityManagerFactory()).create(a);
            setId(a.getId());
            update();
        }
        return getId();
    }

    @Override
    public Person getEntity() throws IllegalArgumentException {
        PersonJpaController c
                = new PersonJpaController(DataBaseManager.getEntityManagerFactory());
        return c.findPerson(getId());
    }

    @Override
    public void update(Person target, Person source) {
        target.setId(source.getId());
        target.setName(source.getName());
        target.setLastname(source.getLastname());
        target.setAccessId(source.getAccessId());
        target.setSsn(source.getSsn());
        target.setLastLogin(source.getLastLogin());
        target.setUsername(source.getUsername());
        target.setPassword(source.getPassword());
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

    public void addServiceInstance(ServiceInstance si) throws MSMException {
        for (PersonHasService phs : getPersonHasServiceList()) {
            if (Objects.equals(phs.getServiceInstance().getId(), si.getId())) {
                throw new MSMException("Person already has this service instance!");
            }
        }
        PersonHasService phs = new PersonHasService(getId(), si.getId());
        phs.setPerson(getEntity());
        phs.setDate(new Date());
        phs.setServiceInstance(si);
        PersonHasServiceJpaController phsc
                = new PersonHasServiceJpaController(DataBaseManager.getEntityManagerFactory());
        try {
            phsc.create(phs);
        } catch (Exception ex) {
            throw new MSMException(ex);
        }
        update();
    }
}
