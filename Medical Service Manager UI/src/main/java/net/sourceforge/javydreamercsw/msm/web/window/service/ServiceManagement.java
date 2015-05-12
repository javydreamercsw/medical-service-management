package net.sourceforge.javydreamercsw.msm.web.window.service;

import com.vaadin.data.Item;
import com.vaadin.event.Action;
import com.vaadin.event.Action.Handler;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Tree;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import java.util.ResourceBundle;
import net.sourceforge.javydreamercsw.msm.controller.ServiceJpaController;
import net.sourceforge.javydreamercsw.msm.db.Service;
import net.sourceforge.javydreamercsw.msm.db.manager.DataBaseManager;

/**
 *
 * @author Javier Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class ServiceManagement extends Window implements Handler {

    private final ResourceBundle rb;
    private final HorizontalSplitPanel hsplit = new HorizontalSplitPanel();
    private final Tree tree = new Tree();

    public ServiceManagement(ResourceBundle bundle) {
        this.rb = bundle;
        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setSpacing(true);
        mainLayout.setMargin(true);
        mainLayout.setSizeFull();
        tree.setCaption(bundle.getString("window.service.tree"));
        // Cause valueChange immediately when the user selects
        tree.setImmediate(true);
        tree.addActionHandler(ServiceManagement.this);

        hsplit.setSizeFull();
        hsplit.setSplitPosition(25, Unit.PERCENTAGE);

        mainLayout.addComponent(hsplit);

        setCaption(bundle.getString("manage.service"));

        update();

        setClosable(true);
        setContent(mainLayout);
        setIcon(new ThemeResource("icons/stethoscope.png"));
    }

    private void showEditService(Service service) {
        if (service == null) {
            System.out.println("Create");
        } else {
            System.out.println("Edit: " + service.getName());
        }
    }

    private class CreateService extends Action {

        public CreateService() {
            super(rb.getString("create.service"),
                    new ThemeResource("icons/add_record.png"));
        }
    }

    private class EditService extends Action {

        public EditService() {
            super(rb.getString("edit.service"),
                    new ThemeResource("icons/edit_record.png"));
        }
    }

    private void update() {
        //Clean the tree
        tree.removeAllItems();
        Item root = tree.addItem(rb.getString("general.root"));
        //Rebuild
        for (Service s : new ServiceJpaController(DataBaseManager.getEntityManagerFactory()).findServiceEntities()) {
            Item newItem = tree.addItem(s);
            newItem.getItemProperty("name").setValue(s.getName());
            tree.setParent(newItem, root);
        }
        hsplit.setFirstComponent(tree);
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
            showEditService(null);
        } else if (action instanceof EditService) {
            showEditService((Service) tree.getValue());
        }
    }
}
