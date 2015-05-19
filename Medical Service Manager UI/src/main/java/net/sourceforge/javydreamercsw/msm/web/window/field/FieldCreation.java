package net.sourceforge.javydreamercsw.msm.web.window.field;

import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import net.sourceforge.javydreamercsw.msm.web.ByteArrayToStringConverter;
import net.sourceforge.javydreamercsw.msm.web.MSMUI;

/**
 *
 * @author Javier Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class FieldCreation extends Window {

    private final TextField name, desc, range;
    private static FieldGroup fieldGroup;

    public FieldCreation() {
        fieldGroup = new FieldGroup();
        VerticalLayout layout = new VerticalLayout();
        name = new TextField(MSMUI.getResourceBundle()
                .getString("general.name") + ":");
        desc = new TextField(MSMUI.getResourceBundle()
                .getString("general.desc") + ":");
        desc.setConverter(new ByteArrayToStringConverter());
        range = new TextField(MSMUI.getResourceBundle()
                .getString("general.range") + ":");
        fieldGroup.bind(name, "name");
        fieldGroup.bind(desc, "desc");
        fieldGroup.bind(range, "rangeId");

        layout.addComponent(name);
        layout.addComponent(desc);
        layout.addComponent(range);

        setContent(layout);
    }
}
