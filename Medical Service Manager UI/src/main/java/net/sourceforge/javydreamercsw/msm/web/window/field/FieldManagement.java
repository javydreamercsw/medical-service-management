package net.sourceforge.javydreamercsw.msm.web.window.field;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.MultiSelectMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import net.sourceforge.javydreamercsw.msm.controller.FieldJpaController;
import net.sourceforge.javydreamercsw.msm.db.Field;
import net.sourceforge.javydreamercsw.msm.db.manager.DataBaseManager;
import net.sourceforge.javydreamercsw.msm.web.MSMUI;
import net.sourceforge.javydreamercsw.msm.web.window.service.ByteToStringGenerator;
import net.sourceforge.javydreamercsw.msm.web.window.service.FieldTypeGenerator;
import net.sourceforge.javydreamercsw.msm.web.window.service.RangeGenerator;
import net.sourceforge.javydreamercsw.msm.web.window.service.SequenceGenerator;
import net.sourceforge.javydreamercsw.msm.web.window.service.ServiceManagement;

/**
 *
 * @author Javier Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class FieldManagement extends Window implements ItemClickListener,
        ValueChangeListener {

    private static final long serialVersionUID = -6862697900642952069L;

    private final FieldGroup fieldGroup = new FieldGroup();
    private final Table available = new Table(null);
    private final Table selected = new Table(null);
    private Button down, up;
    private ServiceManagement sm;

    public FieldManagement(ServiceManagement sm) {
        this.sm = sm;
        available.setCaption(MSMUI.getResourceBundle().getString("general.available")
                + " " + MSMUI.getResourceBundle().getString("general.field"));
        selected.setCaption(MSMUI.getResourceBundle().getString("general.selected")
                + " " + MSMUI.getResourceBundle().getString("general.field"));
        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setSpacing(true);
        mainLayout.setMargin(true);
        mainLayout.setSizeFull();

        mainLayout.addComponent(buildAvailableTable());
        mainLayout.addComponent(buildSelectionControls());
        mainLayout.addComponent(buildSelectedTable());
        mainLayout.addComponent(buildForm());

        update();

        setCaption(MSMUI.getResourceBundle().getString("select.fields"));

        setClosable(true);
        setContent(mainLayout);
        setIcon(new ThemeResource("icons/add_record.png"));
        center();
        setModal(true);
        setWidth(80, Unit.PERCENTAGE);
        setHeight(80, Unit.PERCENTAGE);
        setVisible(true);
    }

    private void update() {

    }

    private Table createTMFieldTable(Table table,
            BeanItemContainer<Field> container) {
        if (container == null) {
            container = new BeanItemContainer<>(Field.class,
                    new ArrayList<Field>());
        }
        table.removeAllItems();
        table.setContainerDataSource(container);
        table.setMultiSelect(true);
        table.setMultiSelectMode(MultiSelectMode.DEFAULT);
        table.addItemClickListener(this);
        table.addValueChangeListener(this);
        table.setSelectable(true);
        table.setWidth(100, Unit.PERCENTAGE);
        table.setHeight(40, Unit.PERCENTAGE);
        table.setSizeFull();
        table.setImmediate(true);
        table.addGeneratedColumn("rangeId",
                new RangeGenerator());
        table.addGeneratedColumn("desc", new ByteToStringGenerator());
        table.addGeneratedColumn("fieldTypeId", new FieldTypeGenerator());
        table.addGeneratedColumn("sequence", new SequenceGenerator());
        table.setVisibleColumns("sequence", "name", "desc",
                "fieldTypeId", "rangeId");
        table.setColumnHeaders(new String[]{
            MSMUI.getResourceBundle().getString("general.sequence"),
            MSMUI.getResourceBundle().getString("general.name"),
            MSMUI.getResourceBundle().getString("general.desc"),
            MSMUI.getResourceBundle().getString("general.field.type"),
            MSMUI.getResourceBundle().getString("general.range.type")});
        return table;
    }

    private Component buildAvailableTable() {
        List<Field> fields
                = new FieldJpaController(DataBaseManager.getEntityManagerFactory()).findFieldEntities();
        createTMFieldTable(available, new BeanItemContainer<>(
                Field.class, fields));
        return available;
    }

    private Component buildSelectedTable() {
        List<Field> fields = new ArrayList<>();
        createTMFieldTable(selected, new BeanItemContainer<>(Field.class, fields));
        return selected;
    }

    private Component buildForm() {
        HorizontalLayout layout = new HorizontalLayout();
        Button save = new Button(MSMUI.getResourceBundle().getString("general.save"),
                new com.vaadin.ui.Button.ClickListener() {
                    private static final long serialVersionUID = 5019806363620874205L;

                    @Override
                    public void buttonClick(Button.ClickEvent event) {
                        //Process the changes
                        for (Field f : (Collection<Field>) selected.getContainerDataSource().getItemIds()) {
                            if (sm.getTable().getItem(f) == null) {
                                sm.getTable().addItem(f);
                            }
                        }
                        getUI().removeWindow(FieldManagement.this);
                    }
                });
        Button discard = new Button(MSMUI.getResourceBundle().getString("general.discard"),
                new com.vaadin.ui.Button.ClickListener() {
                    private static final long serialVersionUID = 5019806363620874205L;

                    @Override
                    public void buttonClick(Button.ClickEvent event) {
                        getUI().removeWindow(FieldManagement.this);
                    }
                });
//        name = new TextField(MSMUI.getResourceBundle().getString("general.name") + ":");
//        name.setEnabled(false);
//        desc = new TextField(MSMUI.getResourceBundle().getString("general.desc") + ":");
//        desc.setEnabled(false);
//        desc.setConverter(new ByteArrayToStringConverter());
//        fieldGroup.bind(name, "name");
//        fieldGroup.bind(desc, "desc");
//
//        layout.addComponent(name);
//        layout.addComponent(desc);
        layout.addComponent(save);
        layout.addComponent(discard);
        return layout;
    }

    private void update(Field field) {
        if (field == null) {
            field = new Field();
        }
        BeanItem<Field> item = new BeanItem<>(field);
        fieldGroup.setItemDataSource(item);
    }

    @Override
    public void itemClick(ItemClickEvent event) {
        update((Field) event.getItemId());
    }

    private Component buildSelectionControls() {
        HorizontalLayout layout = new HorizontalLayout();
        down = new Button("v", new com.vaadin.ui.Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;

            @Override
            public void buttonClick(Button.ClickEvent event) {
                Set<Field> selectedValues = (Set<Field>) available.getValue();
                for (Field field : selectedValues) {
                    if (selected.getItem(field) == null) {
                        //Not removing from available for cases you might need mutiple copies of fields
                        selected.addItem(field);
                    }
                }
            }
        });
        down.setEnabled(false);
        up = new Button("^", new com.vaadin.ui.Button.ClickListener() {
            private static final long serialVersionUID = 5019806363620874205L;

            @Override
            public void buttonClick(Button.ClickEvent event) {
                Set<Field> selectedValues = (Set<Field>) selected.getValue();
                for (Field field : selectedValues) {
                    selected.removeItem(field);
                }
            }
        });
        up.setEnabled(false);

        layout.addComponent(up);
        layout.addComponent(down);

        return layout;
    }

    @Override
    public void valueChange(Property.ValueChangeEvent event) {
        Set<Field> selectedValues = (Set<Field>) available.getValue();
        down.setEnabled(selectedValues != null && selectedValues.size() > 0);
        selectedValues = (Set<Field>) selected.getValue();
        up.setEnabled(selectedValues != null && selectedValues.size() > 0);
    }
}
