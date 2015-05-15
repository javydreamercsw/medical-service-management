package net.sourceforge.javydreamercsw.msm.web.window.service;

import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import net.sourceforge.javydreamercsw.msm.db.Field;

/**
 *
 * @author Javier Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class FieldTypeGenerator implements Table.ColumnGenerator {

    public FieldTypeGenerator() {
    }

    @Override
    public Object generateCell(Table source, Object itemId, Object columnId) {
        return new Label(((Field) itemId).getFieldTypeId().getName());
    }

}
