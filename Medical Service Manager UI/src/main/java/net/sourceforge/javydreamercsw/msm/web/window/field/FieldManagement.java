package net.sourceforge.javydreamercsw.msm.web.window.field;



import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import java.util.ArrayList;
import java.util.List;
import net.sourceforge.javydreamercsw.msm.controller.TMFieldJpaController;
import net.sourceforge.javydreamercsw.msm.db.TMField;
import net.sourceforge.javydreamercsw.msm.db.manager.DataBaseManager;
import net.sourceforge.javydreamercsw.msm.web.MSMUI;
import net.sourceforge.javydreamercsw.msm.web.window.service.ByteToStringGenerator;
import net.sourceforge.javydreamercsw.msm.web.window.service.FieldTypeGenerator;
import net.sourceforge.javydreamercsw.msm.web.window.service.RangeGenerator;
import net.sourceforge.javydreamercsw.msm.web.window.service.SequenceGenerator;

/**
 *
 * @author Javier Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class FieldManagement extends Window {

    private final FieldGroup fieldGroup = new FieldGroup();
    private final Table available = new Table(null);
    private final Table selected = new Table(null);
    private final List<TMField> selectedFields = new ArrayList<>();

    public FieldManagement() {
        available.setCaption(MSMUI.getResourceBundle().getString("general.available")
                + " " + MSMUI.getResourceBundle().getString("general.field"));
        selected.setCaption(MSMUI.getResourceBundle().getString("general.selected")
                + " " + MSMUI.getResourceBundle().getString("general.field"));
        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setSpacing(true);
        mainLayout.setMargin(true);
        mainLayout.setSizeFull();

        mainLayout.addComponent(buildForm());
        mainLayout.addComponent(buildAvailableTable());
        mainLayout.addComponent(buildSelectedTable());

        update();

        setClosable(true);
        setContent(mainLayout);
        setIcon(new ThemeResource("icons/add_record.png"));
    }

    private void update() {

    }

    private Component buildAvailableTable() {
        available.removeAllItems();
        available.setSelectable(true);
        available.setWidth(100, Unit.PERCENTAGE);
        available.setHeight(35, Unit.PERCENTAGE);
        available.setImmediate(true);
        List<TMField> fields
                = new TMFieldJpaController(DataBaseManager.getEntityManagerFactory()).findTMFieldEntities();
        BeanItemContainer<TMField> container = new BeanItemContainer<>(
                TMField.class, fields);
        available.setContainerDataSource(container);
        available.addGeneratedColumn("rangeId",
                new RangeGenerator());
        available.addGeneratedColumn("desc", new ByteToStringGenerator());
        available.addGeneratedColumn("fieldTypeId", new FieldTypeGenerator());
        available.addGeneratedColumn("sequence", new SequenceGenerator());
        available.setVisibleColumns("sequence", "name", "desc", "fieldTypeId", "rangeId");
        available.setColumnHeaders(new String[]{
            MSMUI.getResourceBundle().getString("general.sequence"),
            MSMUI.getResourceBundle().getString("general.name"),
            MSMUI.getResourceBundle().getString("general.desc"),
            MSMUI.getResourceBundle().getString("general.field.type"),
            MSMUI.getResourceBundle().getString("general.range.type")});
        return available;
    }

    private Component buildSelectedTable() {
        selected.removeAllItems();
        selected.setSelectable(true);
        selected.setWidth(100, Unit.PERCENTAGE);
        selected.setHeight(35, Unit.PERCENTAGE);
        selected.setImmediate(true);
        BeanItemContainer<TMField> container = new BeanItemContainer<>(
                TMField.class, selectedFields);
        selected.setContainerDataSource(container);
        selected.setVisibleColumns("sequence", "name", "desc", "fieldTypeId", "rangeId");
        selected.setColumnHeaders(new String[]{
            MSMUI.getResourceBundle().getString("general.sequence"),
            MSMUI.getResourceBundle().getString("general.name"),
            MSMUI.getResourceBundle().getString("general.desc"),
            MSMUI.getResourceBundle().getString("general.field.type"),
            MSMUI.getResourceBundle().getString("general.range.type")});
        return selected;
    }

    private Component buildForm() {
        return new Label("TODO");
    }
}
