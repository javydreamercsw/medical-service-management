package net.sourceforge.javydreamercsw.msm.web.window.service;

import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import net.sourceforge.javydreamercsw.msm.db.Service;
import net.sourceforge.javydreamercsw.msm.db.ServiceHasField;
import net.sourceforge.javydreamercsw.msm.db.TMField;

/**
 *
 * @author Javier Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class SequenceGenerator implements Table.ColumnGenerator {

    @Override
    public Object generateCell(Table source, Object itemId, Object columnId) {
        Service s = ((BeanItem<Service>) ServiceManagement.getFieldGroup().getItemDataSource()).getBean();
        int seq = -1;
        for (ServiceHasField shf : s.getServiceHasFieldList()) {
            if (shf.getTmfield().getId().equals(((TMField) itemId).getId())) {
                seq = shf.getIndex();
                break;
            }
        }
        return new Label("" + seq);
    }

}
