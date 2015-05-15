package net.sourceforge.javydreamercsw.msm.web.window.service;

import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import net.sourceforge.javydreamercsw.msm.db.TMField;

/**
 *
 * @author Javier Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class ByteToStringGenerator implements Table.ColumnGenerator {

    public ByteToStringGenerator() {
    }

    @Override
    public Object generateCell(Table source, Object itemId, Object columnId) {
        return new Label(new String(((TMField) itemId).getDesc()));
    }
}
