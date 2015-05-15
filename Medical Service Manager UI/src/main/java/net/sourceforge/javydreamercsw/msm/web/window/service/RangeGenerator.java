package net.sourceforge.javydreamercsw.msm.web.window.service;

import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import net.sourceforge.javydreamercsw.msm.db.Range;
import net.sourceforge.javydreamercsw.msm.db.Field;
import net.sourceforge.javydreamercsw.msm.web.MSMUI;

/**
 *
 * @author Javier Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class RangeGenerator implements Table.ColumnGenerator {

    @Override
    public Object generateCell(Table source, Object itemId, Object columnId) {
        Range range = ((Field) itemId).getRangeId();
        return range == null ? new Label(MSMUI.getResourceBundle().getString("general.NA"))
                : new Label(((Field) itemId).getRangeId().getRangeTypeId().getName());
    }
}
