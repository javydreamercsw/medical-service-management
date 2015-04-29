package net.sourceforge.javydreamercsw.msm.server;

import java.util.ArrayList;
import net.sourceforge.javydreamercsw.msm.db.Person;
import net.sourceforge.javydreamercsw.msm.db.PersonHasService;
import net.sourceforge.javydreamercsw.msm.controller.PersonJpaController;
import net.sourceforge.javydreamercsw.msm.db.manager.DataBaseManager;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class PersonServer extends Person implements EntityServer<Person> {

    public PersonServer(Person a) {
        update(PersonServer.this, a);
    }

    public PersonServer(String name) {
        super();
        setName(name);
        setPersonHasServiceList(new ArrayList<PersonHasService>());
    }

    @Override
    public int write2DB() throws Exception {
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
}
