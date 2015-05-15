package net.sourceforge.javydreamercsw.msm.web.window.service;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.Action;
import com.vaadin.event.Action.Handler;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.MouseEventDetails.MouseButton;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Tree;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sourceforge.javydreamercsw.msm.controller.ServiceHasFieldJpaController;
import net.sourceforge.javydreamercsw.msm.controller.ServiceJpaController;
import net.sourceforge.javydreamercsw.msm.db.Service;
import net.sourceforge.javydreamercsw.msm.db.ServiceHasField;
import net.sourceforge.javydreamercsw.msm.db.TMField;
import net.sourceforge.javydreamercsw.msm.db.manager.DataBaseManager;
import net.sourceforge.javydreamercsw.msm.server.FieldServer;
import net.sourceforge.javydreamercsw.msm.server.FieldTypeServer;
import net.sourceforge.javydreamercsw.msm.server.ServiceServer;
import net.sourceforge.javydreamercsw.msm.web.MSMUI;
import net.sourceforge.javydreamercsw.msm.web.window.field.FieldManagement;

/**
 *
 * @author Javier Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class ServiceManagement extends Window implements Handler,
        ItemClickListener, ValueChangeListener {

    private final HorizontalSplitPanel hsplit = new HorizontalSplitPanel();
    private final Tree tree = new Tree();
    private final static FieldGroup fieldGroup = new FieldGroup();
    private final Table table = new Table(null);
    private static final Logger LOG
            = Logger.getLogger(ServiceManagement.class.getName());
    private final List<Button> buttons = new ArrayList<>();

    public ServiceManagement() {
        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setSpacing(true);
        mainLayout.setMargin(true);
        mainLayout.setSizeFull();
        tree.setCaption(MSMUI.getResourceBundle().getString("window.service.tree"));
        // Cause valueChange immediately when the user selects
        tree.setImmediate(true);
        tree.addActionHandler(ServiceManagement.this);
        tree.addItemClickListener(ServiceManagement.this);

        hsplit.setSizeFull();
        hsplit.setSplitPosition(25, Unit.PERCENTAGE);

        hsplit.setFirstComponent(tree);
        hsplit.setSecondComponent(buildForm());

        mainLayout.addComponent(hsplit);

        setCaption(MSMUI.getResourceBundle().getString("manage.service"));

        update();

        setClosable(true);
        setContent(mainLayout);
        setIcon(new ThemeResource("icons/stethoscope.png"));
        center();
        setModal(true);
        setWidth(100, Unit.PERCENTAGE);
        setHeight(100, Unit.PERCENTAGE);
        setVisible(true);
    }

    private Component buildForm() {
        VerticalLayout form = new VerticalLayout();
        //Build controls
        HorizontalLayout controls = new HorizontalLayout();
        controls.setSpacing(true);
        Button save = new Button(MSMUI.getResourceBundle().getString("general.save"),
                new ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        try {
                            getFieldGroup().commit();
                            Service s = ((BeanItem<Service>) getFieldGroup()
                            .getItemDataSource()).getBean();
                            ServiceServer ss = new ServiceServer(s);
                            for(ServiceHasField shf:ss.getServiceHasFieldList()){
                                new ServiceHasFieldJpaController(DataBaseManager.getEntityManagerFactory()).destroy(shf.getServiceHasFieldPK());
                            }
                            ss.write2DB();
                            for (TMField f : (Collection<TMField>) table.getContainerDataSource().getItemIds()) {
                                boolean present = false;
                                for (ServiceHasField shf : ss.getServiceHasFieldList()) {
                                    if (shf.getTmfield().getId().equals(f.getId())) {
                                        present = true;
                                        break;
                                    }
                                }
                                if (!present) {
                                    ss.addField(f, ss.getServiceHasFieldList().size() + 1);
                                }
                            }
                            ss.write2DB();
                            update();
                        } catch (CommitException ex) {
                            LOG.log(Level.SEVERE, null, ex);
                        } catch (Exception ex) {
                            LOG.log(Level.SEVERE, null, ex);
                        }
                    }
                });
        Button discard = new Button(MSMUI.getResourceBundle().getString("general.discard"),
                new ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        getFieldGroup().discard();
                    }
                });
        Button add = new Button(MSMUI.getResourceBundle().getString("general.add") + " "
                + MSMUI.getResourceBundle().getString("general.field"),
                new ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        showFieldManagement();
                    }
                });
        Button remove = new Button(MSMUI.getResourceBundle().getString("general.remove") + " "
                + MSMUI.getResourceBundle().getString("general.field"),
                new ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        Set<TMField> selectedValues = (Set<TMField>) table.getValue();
                        for (TMField field : selectedValues) {
                            table.removeItem(field);
                        }
                    }
                });
        buttons.add(save);
        buttons.add(add);
        buttons.add(discard);
        buttons.add(remove);
        controls.addComponent(save);
        controls.addComponent(discard);
        controls.addComponent(add);
        controls.addComponent(remove);
        //--------------
        form.setSpacing(true);
        TextField name
                = new TextField(MSMUI.getResourceBundle().getString("general.name") + ":");
        //Field Type
        form.addComponent(controls);
        form.addComponent(name);

        getFieldGroup().bind(name, "name");

        table.setSelectable(true);
        table.setMultiSelect(true);
        table.setWidth(100, Unit.PERCENTAGE);
        table.setHeight(75, Unit.PERCENTAGE);
        table.setSizeFull();
        table.setImmediate(true);
        table.addValueChangeListener(this);

        table.addGeneratedColumn("rangeId", new RangeGenerator());
        table.addGeneratedColumn("desc", new ByteToStringGenerator());
        table.addGeneratedColumn("fieldTypeId", new FieldTypeGenerator());
        table.addGeneratedColumn("sequence", new SequenceGenerator());
        table.sort(new Object[]{"sequence"}, new boolean[]{false});
        form.addComponent(getTable());

        setEnabledButtons(false);

        return form;
    }

    private void setEnabledButtons(boolean enable) {
        for (Button b : buttons) {
            b.setEnabled(enable);
        }
    }

    private void editService(Service service) {
        if (service == null) {
            LOG.fine("Create");
            updateService(null);
        } else {
            LOG.log(Level.FINE, "Edit: {0}", service.getName());
            updateService(service);
        }
    }

    @Override
    public void itemClick(ItemClickEvent event) {
        if (event.getButton() == MouseButton.RIGHT) {
            //Right click
            tree.select(event.getItemId());
        }
        if (!tree.areChildrenAllowed(event.getItemId())) {
            //A service is selected. Show it on the right side
            editService((Service) event.getItemId());
            setEnabledButtons(true);
        } else {
            editService(null);
            setEnabledButtons(false);
        }
    }

    private void updateService(Service service) {
        if (service == null) {
            service = new Service();
        }
        BeanItem<Service> item = new BeanItem<>(service);
        getFieldGroup().setItemDataSource(item);
        List<TMField> fields = new ArrayList<>();
        if (service.getServiceHasFieldList() != null) {
            for (ServiceHasField shf : service.getServiceHasFieldList()) {
                fields.add(shf.getTmfield());
            }
        }
        BeanItemContainer<TMField> container = new BeanItemContainer<>(
                TMField.class, fields);
        getTable().setContainerDataSource(container);
        getTable().setVisibleColumns("sequence", "name", "desc", "fieldTypeId", "rangeId");
        getTable().setColumnHeaders(new String[]{
            MSMUI.getResourceBundle().getString("general.sequence"),
            MSMUI.getResourceBundle().getString("general.name"),
            MSMUI.getResourceBundle().getString("general.desc"),
            MSMUI.getResourceBundle().getString("general.field.type"),
            MSMUI.getResourceBundle().getString("general.range.type")});
    }

    /**
     * @return the fieldGroup
     */
    public static FieldGroup getFieldGroup() {
        return fieldGroup;
    }

    /**
     * @return the table
     */
    public Table getTable() {
        return table;
    }

    @Override
    public void valueChange(Property.ValueChangeEvent event) {
        if (table.getValue() != null && buttons.size() >= 4) {
            buttons.get(3).setEnabled(true);
        } else if (buttons.size() >= 4) {
            buttons.get(3).setEnabled(false);
        }
    }

    private class CreateService extends Action {

        public CreateService() {
            super(MSMUI.getResourceBundle().getString("create.service"),
                    new ThemeResource("icons/add_record.png"));
        }
    }

    private class EditService extends Action {

        public EditService() {
            super(MSMUI.getResourceBundle().getString("edit.service"),
                    new ThemeResource("icons/edit_record.png"));
        }
    }

    private void update() {
        //Clean the tree
        tree.removeAllItems();
        tree.addItem(MSMUI.getResourceBundle().getString("available.services"));
        //Rebuild
        List<Service> services
                = new ServiceJpaController(DataBaseManager.getEntityManagerFactory()).findServiceEntities();
        if (services.isEmpty()) {
            try {
                //Create a dummy one
                ServiceServer ss = new ServiceServer("Dummy");
                ss.write2DB();
                for (int i = 0; i < 10; i++) {
                    FieldServer field = new FieldServer("Field" + i);
                    field.setDesc(("Field" + i
                            + " description.").getBytes("UTF-8"));
                    field.setFieldTypeId(new FieldTypeServer(i % 4 + 1).getEntity());
                    field.write2DB();
                    ss.addField(field.getEntity(), i);
                }
                ss.write2DB();
                services.add(ss.getEntity());
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
        }
        for (Service s : services) {
            tree.addItem(s);
            tree.setItemCaption(s, s.getName());
            tree.setParent(s, MSMUI.getResourceBundle().getString("available.services"));
            tree.setChildrenAllowed(s, false);
        }
        //Expand tree
        tree.expandItemsRecursively(MSMUI.getResourceBundle().getString("available.services"));
    }

    @Override
    public Action[] getActions(Object target, Object sender) {
        if (sender == tree) {
            return tree.areChildrenAllowed(tree.getValue())
                    ? new Action[]{new CreateService()}
                    : new Action[]{new EditService()};
        }
        return new Action[]{};
    }

    @Override
    public void handleAction(Action action, Object sender, Object target) {
        if (action instanceof CreateService) {
            editService(null);
        } else if (action instanceof EditService) {
            editService((Service) tree.getValue());
        }
    }

    private void showFieldManagement() {
        getUI().addWindow(new FieldManagement(this));
    }
}
