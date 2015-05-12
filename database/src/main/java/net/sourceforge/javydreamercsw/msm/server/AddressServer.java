package net.sourceforge.javydreamercsw.msm.server;

import java.util.ArrayList;
import net.sourceforge.javydreamercsw.msm.db.Person;
import net.sourceforge.javydreamercsw.msm.controller.AddressJpaController;
import net.sourceforge.javydreamercsw.msm.db.Address;
import net.sourceforge.javydreamercsw.msm.db.manager.DataBaseManager;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public final class AddressServer extends Address implements EntityServer<Address> {

    public AddressServer(int id) {
        setId(id);
        update();
    }

    public AddressServer(Address a) {
        update(AddressServer.this, a);
    }

    public AddressServer(String address, String postalCode) {
        super();
         setAddress(address);
         setPostalCode(postalCode);
        setPersonList(new ArrayList<Person>());
    }

    @Override
    public int write2DB() throws Exception {
        if (getId() != null && getId() > 0) {
            Address entity = getEntity();
            update(entity, this);
            new AddressJpaController(DataBaseManager.getEntityManagerFactory()).edit(entity);
            setId(getEntity().getId());
        } else {
            Address a = new Address();
            update(a, this);
            new AddressJpaController(DataBaseManager.getEntityManagerFactory()).create(a);
            setId(a.getId());
            update();
        }
        return getId();
    }

    @Override
    public Address getEntity() throws IllegalArgumentException {
        AddressJpaController c
                = new AddressJpaController(DataBaseManager.getEntityManagerFactory());
        return c.findAddress(getId());
    }

    @Override
    public void update(Address target, Address source) {
        target.setId(source.getId());
        target.setAddress(source.getAddress());
        target.setAddress2(source.getAddress2());
        target.setCityId(source.getCityId());
        target.setDistrict(source.getDistrict());
        target.setLastUpdate(source.getLastUpdate());
        target.setPhone(source.getPhone());
        target.setPostalCode(source.getPostalCode());
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
