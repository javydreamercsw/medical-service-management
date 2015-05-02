package net.sourceforge.javydreamercsw.msm.web;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Widgetset;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.util.PropertysetItem;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Component;
import com.vaadin.ui.Form;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import java.util.Locale;
import java.util.ResourceBundle;
import net.sourceforge.javydreamercsw.msm.db.Person;

/**
 *
 */
@Theme("msmtheme")
@Widgetset("net.sourceforge.javydreamercsw.tuberculosis.manager.MyAppWidgetset")
public class MSMUI extends UI {

    private Person p = null;
    private Component left;
    private Component right;
    private final ResourceBundle rb
            = ResourceBundle.getBundle(
                    "net.sourceforge.javydreamercsw.msm.web.MSMMessages",
                    Locale.getDefault());

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        // Have a panel to put stuff in
        Panel panel = new Panel("Split Panels Inside This Panel");

        // Have a horizontal split panel as its content
        HorizontalSplitPanel hsplit = new HorizontalSplitPanel();
        hsplit.setSizeFull();

        panel.setContent(hsplit);

        if (p == null) {
            showLoginScreen();
        } else {

        }

        // Put a component in the left panel
        hsplit.setFirstComponent(left);

        hsplit.setSecondComponent(right);

        // Set the position of the splitter as percentage
        hsplit.setSplitPosition(25, Unit.PERCENTAGE);

        panel.setWidth(100, Unit.PERCENTAGE);
        panel.setHeight(100, Unit.PERCENTAGE);

        setContent(panel);
    }

    private void showLoginScreen() {
        final Window loginWindow = new Window();
        FormLayout form = new FormLayout();
        loginWindow.setContent(form);
        PropertysetItem user = new PropertysetItem();
        user.addItemProperty("username", new ObjectProperty<>(""));
        user.addItemProperty("password", new ObjectProperty<>(""));
        FieldGroup binder = new FieldGroup(user);
        form.setCaption(getResource().getString("window.connection") + ":");
        com.vaadin.ui.TextField username
                = new com.vaadin.ui.TextField(getResource().getString("general.username") + ":");
        PasswordField password
                = new PasswordField(getResource().getString("general.password") + ":");
        form.addComponent(binder.buildAndBind(getResource().getString("general.username") + ":", "username"));
//        form.addField("username", username);
//        form.addField("password", password);
//        form.getField("username").setRequired(true);
//        form.getField("username").focus();
//        form.getField("username").setRequiredError(getResource().getString("message.missing.username"));
//        form.getField("password").setRequired(true);
//        form.getField("password").setRequiredError(getResource().getString("message.missing.password"));
//        form.setFooter(new HorizontalLayout());
        //Used for validation purposes
        final com.vaadin.ui.Button commit = new com.vaadin.ui.Button(
                getResource().getString("general.login"));
        final com.vaadin.ui.Button cancel = new com.vaadin.ui.Button(
                getResource().getString("general.cancel"),
                new com.vaadin.ui.Button.ClickListener() {
                    @Override
                    public void buttonClick(com.vaadin.ui.Button.ClickEvent event) {
                        //Make sure to log out anyone previously logged in
                        p = null;
                        updateMenu();
                        //Select root node
                        loginWindow.close();
                    }

                });
        commit.addListener(new com.vaadin.ui.Button.ClickListener() {
            @Override
            public void buttonClick(com.vaadin.ui.Button.ClickEvent event) {
                loginWindow.close();
            }
        });
//        form.getFooter().setSizeUndefined();
//        form.getFooter().addComponent(commit);
//        form.getFooter().addComponent(cancel);
        form.setSizeFull();
        form.setVisible(true);
        loginWindow.center();
        loginWindow.setModal(true);
        loginWindow.setWidth(300, Unit.PIXELS);
        loginWindow.setReadOnly(true);
        loginWindow.setVisible(true);
        addWindow(loginWindow);
        System.out.println("Showing login screen!");
    }

    /**
     * @return the Resource Bundle
     */
    public ResourceBundle getResource() {
        return rb;
    }

    private void updateMenu() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @WebServlet(urlPatterns = "/*", name = "MSMUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MSMUI.class, productionMode = false)
    public static class MSMUIServlet extends VaadinServlet {
    }
}
