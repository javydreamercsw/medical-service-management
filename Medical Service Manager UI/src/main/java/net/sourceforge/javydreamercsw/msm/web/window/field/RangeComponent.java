package net.sourceforge.javydreamercsw.msm.web.window.field;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.TextField;
import net.sourceforge.javydreamercsw.msm.db.Range;

/**
 *
 * @author Javier Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class RangeComponent extends CustomField<Range> {

    private FieldGroup fieldGroup;

    @Override
    protected Component initContent() {
        TextField range = new TextField();
        fieldGroup = new BeanFieldGroup<>(Range.class);
        fieldGroup.bind(range, "name");
        return range;
    }

    @Override
    public Class<? extends Range> getType() {
        return Range.class;
    }
}
