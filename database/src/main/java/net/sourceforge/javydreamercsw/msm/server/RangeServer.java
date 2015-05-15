package net.sourceforge.javydreamercsw.msm.server;

import java.util.ArrayList;
import net.sourceforge.javydreamercsw.msm.db.Range;
import net.sourceforge.javydreamercsw.msm.db.Field;
import net.sourceforge.javydreamercsw.msm.controller.RangeJpaController;
import net.sourceforge.javydreamercsw.msm.db.manager.DataBaseManager;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public final class RangeServer extends Range implements EntityServer<Range> {

    public RangeServer(Range a) {
        update(RangeServer.this, a);
    }

    public RangeServer(float min, float max) {
        super();
        setMax(max);
        setMin(min);
        setTmfieldList(new ArrayList<Field>());
    }

    @Override
    public int write2DB() throws Exception {
        if (getId() != null && getId() > 0) {
            Range entity = getEntity();
            update(entity, this);
            new RangeJpaController(DataBaseManager.getEntityManagerFactory()).edit(entity);
            setId(getEntity().getId());
        } else {
            Range a = new Range();
            update(a, this);
            new RangeJpaController(DataBaseManager.getEntityManagerFactory()).create(a);
            setId(a.getId());
            update();
        }
        return getId();
    }

    @Override
    public Range getEntity() throws IllegalArgumentException {
        RangeJpaController c
                = new RangeJpaController(DataBaseManager.getEntityManagerFactory());
        return c.findRange(getId());
    }

    @Override
    public void update(Range target, Range source) {
        target.setId(source.getId());
        target.setMax(source.getMax());
        target.setMin(source.getMin());
        target.setRangeTypeId(source.getRangeTypeId());
        if (target.getTmfieldList() == null) {
            target.setTmfieldList(new ArrayList<Field>());
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
