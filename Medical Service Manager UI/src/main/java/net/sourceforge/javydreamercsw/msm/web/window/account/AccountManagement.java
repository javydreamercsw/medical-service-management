package net.sourceforge.javydreamercsw.msm.web.window.account;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sourceforge.javydreamercsw.msm.controller.AccessJpaController;
import net.sourceforge.javydreamercsw.msm.controller.PersonJpaController;
import net.sourceforge.javydreamercsw.msm.controller.exceptions.IllegalOrphanException;
import net.sourceforge.javydreamercsw.msm.controller.exceptions.NonexistentEntityException;
import net.sourceforge.javydreamercsw.msm.db.Access;
import net.sourceforge.javydreamercsw.msm.db.Person;
import net.sourceforge.javydreamercsw.msm.db.manager.DataBaseManager;
import net.sourceforge.javydreamercsw.msm.server.AccessServer;
import net.sourceforge.javydreamercsw.msm.server.PersonServer;

/**
 *
 * @author Javier Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class AccountManagement extends Window {

    private GridLayout form;
    private HorizontalLayout tableControls;
    private Table table;
    private HorizontalLayout formControls;
    private final ResourceBundle rb;
    private final FieldGroup fieldGroup = new FieldGroup();
    private static final Logger LOG
            = Logger.getLogger(AccountManagement.class.getName());
    private final PersonJpaController controller
            = new PersonJpaController(DataBaseManager.getEntityManagerFactory());

    public AccountManagement(ResourceBundle rb) {
        this.rb = rb;
        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setSpacing(true);
        mainLayout.setMargin(true);
        mainLayout.setSizeFull();

        mainLayout.addComponent(buildTableControls());
        mainLayout.addComponent(buildTable());
        mainLayout.addComponent(buildForm());
        mainLayout.addComponent(buildFormControls());

        setCaption(rb.getString("manage.account"));
        setClosable(true);
        setContent(mainLayout);
        setIcon(new ThemeResource("icons/patient_record.png"));
    }

    private Component buildTableControls() {
        tableControls = new HorizontalLayout();
        Button add = new Button(rb.getString("general.add"),
                new ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        editPerson(new Person());
                    }
                });
        Button delete = new Button(rb.getString("general.delete"),
                new ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        Person target = ((Person) table.getValue());
                        if (target.getId() != null && target.getId() >= 1000) {
                            try {
                                controller.destroy(target.getId());
                            } catch (IllegalOrphanException | NonexistentEntityException ex) {
                                LOG.log(Level.SEVERE, null, ex);
                            }
                        } else {
                            Notification.show(rb.getString("general.invalid.operation"),
                                    rb.getString("general.invalid.operation.desc"),
                                    Notification.Type.WARNING_MESSAGE);
                        }
                        updateTableData();
                    }
                });
        Button reset = new Button(rb.getString("general.password.reset"),
                new ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        try {
                            Person target = ((Person) table.getValue());
                            target.setPassword("123456");
                            PersonServer ps = new PersonServer(target);
                            ps.setEncrypted(false);
                            ps.write2DB();
                            Notification.show(rb.getString("general.password.reset"),
                                    rb.getString("general.password.reset.desc")
                                    + " (123456)",
                                    Notification.Type.TRAY_NOTIFICATION);
                            updateTableData();
                        } catch (Exception ex) {
                            LOG.log(Level.SEVERE, null, ex);
                        }
                    }
                });
        tableControls.addComponent(add);
        tableControls.addComponent(delete);
        tableControls.addComponent(reset);
        return tableControls;
    }

    private Component buildFormControls() {
        formControls = new HorizontalLayout();
        Button save = new Button(rb.getString("general.save"),
                new ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        try {
                            fieldGroup.commit();
                            Person p = ((BeanItem<Person>) fieldGroup
                            .getItemDataSource()).getBean();
                            PersonServer ps = new PersonServer(p);
                            if (p.getId() == null || p.getId() == 0) {
                                //New person, set default password
                                p.setPassword("123456");
                                ps.setEncrypted(false);
                            }
                            ps.write2DB();
                            updateTableData();
                            editPerson(null);
                        } catch (CommitException ex) {
                            LOG.log(Level.SEVERE, null, ex);
                        } catch (Exception ex) {
                            LOG.log(Level.SEVERE, null, ex);
                        }
                    }
                });
        Button discard = new Button(rb.getString("general.discard"),
                new ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        fieldGroup.discard();
                    }
                });
        formControls.addComponent(save);
        formControls.addComponent(discard);
        return formControls;
    }

    private Component buildTable() {
        table = new Table(null);
        table.setSelectable(true);
        table.setWidth(100, Unit.PERCENTAGE);
        table.setHeight(75, Unit.PERCENTAGE);
        table.setSizeFull();
        table.setImmediate(true);
        table.addValueChangeListener(new ValueChangeListener() {

            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                editPerson((Person) table.getValue());
            }
        });
        table.addGeneratedColumn("accessId",
                new AccessGenerator());
        updateTableData();
        return table;
    }

    private void editPerson(Person person) {
        if (person == null) {
            person = new Person();
        }
        BeanItem<Person> item = new BeanItem<>(person);
        fieldGroup.setItemDataSource(item);
    }

    private void updateTableData() {
        List<Person> persons
                = new PersonJpaController(DataBaseManager.getEntityManagerFactory()).findPersonEntities();
        BeanItemContainer<Person> container = new BeanItemContainer<>(
                Person.class, persons);
        table.setContainerDataSource(container);

        table.setVisibleColumns("name", "lastname", "username", "ssn",
                "accessId");
        table.setColumnHeaders(new String[]{
            rb.getString("general.first.name"),
            rb.getString("general.last.name"),
            rb.getString("general.username"),
            rb.getString("general.ssn"),
            rb.getString("general.access")});
        table.sort(new Object[]{"name", "lastname"}, new boolean[]{
            true, true});
    }

    public class AccessGenerator implements Table.ColumnGenerator {

        @Override
        public Object generateCell(Table source, Object itemId, Object columnId) {
            Label label = new Label();
            label.setValue(((Person) itemId).getAccessId() == null
                    ? "null" : rb.getString(((Person) itemId).getAccessId().getName()));
            return label;
        }
    }

    private Component buildForm() {
        form = new GridLayout(5, 2);

        TextField firstName
                = new TextField(rb.getString("general.first.name") + ":");
        TextField lastName
                = new TextField(rb.getString("general.last.name") + ":");
        TextField userName
                = new TextField(rb.getString("general.username") + ":");
        TextField ssn = new TextField(rb.getString("general.ssn") + ":");

        List<Access> accesses = new AccessJpaController(DataBaseManager.getEntityManagerFactory()).findAccessEntities();
        IndexedContainer iContainer = new IndexedContainer();
        iContainer.addContainerProperty("name", String.class, "");
        iContainer.addContainerProperty("bean", Access.class,
                new AccessServer(3).getEntity());
        for (Access a : accesses) {
            Item newItem = iContainer.getItem(iContainer.addItem());
            newItem.getItemProperty("name").setValue(rb.getString(a.getName()));
            newItem.getItemProperty("bean").setValue(a);
        }
        final ComboBox access
                = new ComboBox(rb.getString("general.access"), iContainer);
        access.setConverter(new AccessConverter(iContainer));
        access.setItemCaptionPropertyId("name");
        access.setImmediate(true);

        AddressPopup address = new AddressPopup(rb);

        fieldGroup.bind(firstName, "name");
        fieldGroup.bind(lastName, "lastname");
        fieldGroup.bind(userName, "username");
        fieldGroup.bind(ssn, "ssn");
        fieldGroup.bind(access, "accessId");
        fieldGroup.bind(address, "addressId");

        form.addComponent(firstName);
        form.addComponent(lastName);
        form.addComponent(userName);
        form.addComponent(ssn);
        form.addComponent(access);
        form.addComponent(address);
        return form;
    }

    private class AccessConverter implements com.vaadin.data.util.converter.Converter {

        private final IndexedContainer container;

        public AccessConverter(IndexedContainer container) {
            this.container = container;
        }

        @Override
        public Object convertToModel(Object itemID, Class targetType,
                Locale locale) throws ConversionException {
            if (itemID == null) {
                return null;
            }

            Item item = container.getItem(itemID);
            if (item == null) {
                return null;
            }

            Access model = (Access) item.getItemProperty("bean").getValue();
            return model;
        }

        @Override
        public Object convertToPresentation(Object value, Class targetType,
                Locale locale) throws ConversionException {
            for (Object itemId : container.getItemIds()) {
                Item item = container.getItem(itemId);
                if (value != null && value.equals((Access) item.getItemProperty("bean").getValue())) {
                    return (Integer) itemId;
                }
            }
            return null;
        }

        @Override
        public Class getModelType() {
            return Access.class;
        }

        @Override
        public Class getPresentationType() {
            return Object.class;
        }
    }
}
