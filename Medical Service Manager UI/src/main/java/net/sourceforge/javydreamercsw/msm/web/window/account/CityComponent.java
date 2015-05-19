package net.sourceforge.javydreamercsw.msm.web.window.account;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.TextField;
import net.sourceforge.javydreamercsw.msm.db.City;
import net.sourceforge.javydreamercsw.msm.web.MSMUI;

/**
 *
 * @author Javier Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class CityComponent extends CustomField<City> {

    private FieldGroup fieldGroup;

    @Override
    protected Component initContent() {
        TextField city = new TextField(MSMUI.getResourceBundle().getString("general.city") + ":");
        fieldGroup = new BeanFieldGroup<>(City.class);
        fieldGroup.bind(city, "city");
        return city;
    }

    @Override
    public Class<? extends City> getType() {
        return City.class;
    }
}
