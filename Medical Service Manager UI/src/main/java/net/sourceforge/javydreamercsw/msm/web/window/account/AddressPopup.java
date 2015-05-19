package net.sourceforge.javydreamercsw.msm.web.window.account;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sourceforge.javydreamercsw.msm.db.Address;
import net.sourceforge.javydreamercsw.msm.server.AddressServer;
import net.sourceforge.javydreamercsw.msm.web.MSMUI;

/**
 *
 * @author Javier Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class AddressPopup extends CustomField<Address> {
    private FieldGroup fieldGroup;
    private static final Logger LOG
            = Logger.getLogger(AddressPopup.class.getName());

    @Override
    protected Component initContent() {
        FormLayout layout = new FormLayout();
        final Window window = new Window(MSMUI.getResourceBundle().getString("message.edit.address"));
        TextArea street = new TextArea(MSMUI.getResourceBundle().getString("general.street") + ":");
        TextField zip = new TextField(MSMUI.getResourceBundle().getString("general.zip") + ":");
        CityComponent city = new CityComponent();
        layout.addComponent(street);
        layout.addComponent(zip);
        layout.addComponent(city);

        fieldGroup = new BeanFieldGroup<>(Address.class);
        fieldGroup.bind(street, "address");
        fieldGroup.bind(zip, "postalCode");
        fieldGroup.bind(city, "cityId");
        Button button = new Button(MSMUI.getResourceBundle().getString("message.edit.address.editor"),
                new ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        getUI().addWindow(window);
                    }
                });
        window.addCloseListener(new CloseListener() {
            @Override
            public void windowClose(CloseEvent e) {
                try {
                    fieldGroup.commit();
                    Address a = ((BeanItem<Address>) fieldGroup
                            .getItemDataSource()).getBean();
                    AddressServer as = new AddressServer(a);
                    as.write2DB();
                } catch (CommitException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                } catch (Exception ex) {
                    LOG.log(Level.SEVERE, null, ex);
                }
            }
        });

        window.setContent(layout);
        window.center();
        window.setWidth(null);
        layout.setWidth(null);
        layout.setMargin(true);

        return button;
    }

    @Override
    public Class<Address> getType() {
        return Address.class;
    }

    @Override
    protected void setInternalValue(Address address) {
        if (address == null) {
            address = new Address();
        }
        super.setInternalValue(address);
        fieldGroup.setItemDataSource(new BeanItem<>(address));
    }
}
