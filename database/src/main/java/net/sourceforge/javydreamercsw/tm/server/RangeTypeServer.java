package net.sourceforge.javydreamercsw.tm.server;

import java.util.ArrayList;
import net.sourceforge.javydreamercsw.tm.Range;
import net.sourceforge.javydreamercsw.tm.RangeType;
import net.sourceforge.javydreamercsw.tm.controller.RangeTypeJpaController;
import net.sourceforge.javydreamercsw.tm.db.EntityServer;
import net.sourceforge.javydreamercsw.tm.db.manager.DataBaseManager;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class RangeTypeServer extends RangeType implements EntityServer<RangeType> {

    public RangeTypeServer(RangeType a) {
        update(RangeTypeServer.this, a);
    }

    public RangeTypeServer(String name) {
        super();
        setName(name);
        setRangeList(new ArrayList<Range>());
    }

    @Override
    public int write2DB() throws Exception {
        if (getId() != null && getId() > 0) {
            update(getEntity(), this);
            new RangeTypeJpaController(DataBaseManager.getEntityManagerFactory()).edit(getEntity());
            setId(getEntity().getId());
        } else {
            RangeType a = new RangeType();
            update(a, this);
            new RangeTypeJpaController(DataBaseManager.getEntityManagerFactory()).create(a);
            setId(a.getId());
            update();
        }
        return getId();
    }

    @Override
    public RangeType getEntity() throws IllegalArgumentException {
        RangeTypeJpaController c
                = new RangeTypeJpaController(DataBaseManager.getEntityManagerFactory());
        return c.findRangeType(getId());
    }

    @Override
    public void update(RangeType target, RangeType source) {
        target.setId(source.getId());
        target.setName(source.getName());
        if (target.getRangeList() == null) {
            target.setRangeList(new ArrayList<Range>());
        } else {
            target.getRangeList().clear();
        }
        target.getRangeList().addAll(source.getRangeList());
    }

    @Override
    public void update() {
        update(this, getEntity());
    }
}
