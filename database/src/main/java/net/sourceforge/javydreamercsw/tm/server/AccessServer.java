package net.sourceforge.javydreamercsw.tm.server;

import java.util.ArrayList;
import net.sourceforge.javydreamercsw.tm.Access;
import net.sourceforge.javydreamercsw.tm.Person;
import net.sourceforge.javydreamercsw.tm.controller.AccessJpaController;
import net.sourceforge.javydreamercsw.tm.db.EntityServer;
import net.sourceforge.javydreamercsw.tm.db.manager.DataBaseManager;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class AccessServer extends Access implements EntityServer<Access> {

    public AccessServer(int id){
        setId(id);
        update();
    }
    
    public AccessServer(Access a) {
        update(AccessServer.this, a);
    }

    public AccessServer(String name) {
        super();
        setName(name);
        setPersonList(new ArrayList<Person>());
    }

    @Override
    public int write2DB() throws Exception {
        if (getId() != null && getId() > 0) {
            update(getEntity(), this);
            new AccessJpaController(DataBaseManager.getEntityManagerFactory()).edit(getEntity());
            setId(getEntity().getId());
        } else {
            Access a = new Access();
            update(a, this);
            new AccessJpaController(DataBaseManager.getEntityManagerFactory()).create(a);
            setId(a.getId());
            update();
        }
        return getId();
    }

    @Override
    public Access getEntity() throws IllegalArgumentException {
        AccessJpaController c
                = new AccessJpaController(DataBaseManager.getEntityManagerFactory());
        return c.findAccess(getId());
    }

    @Override
    public void update(Access target, Access source) {
        target.setId(source.getId());
        target.setName(source.getName());
        if (target.getPersonList() == null) {
            target.setPersonList(new ArrayList<Person>());
        } else {
            target.getPersonList().clear();
        }
        target.getPersonList().addAll(source.getPersonList());
    }

    @Override
    public void update() {
        update(this, getEntity());
    }
}
