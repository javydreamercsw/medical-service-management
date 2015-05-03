package net.sourceforge.javydreamercsw.msm.web;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Widgetset;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.util.PropertysetItem;
import com.vaadin.event.MouseEvents;
import com.vaadin.event.MouseEvents.ClickListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.annotation.WebServlet;
import net.sourceforge.javydreamercsw.msm.db.Person;
import net.sourceforge.javydreamercsw.msm.db.manager.DataBaseManager;
import net.sourceforge.javydreamercsw.msm.db.manager.MSMException;
import net.sourceforge.javydreamercsw.msm.server.MD5;
import net.sourceforge.javydreamercsw.msm.server.PersonServer;

/**
 *
 */
@Theme("msmtheme")
@Widgetset("net.sourceforge.javydreamercsw.msm.manager.MSMWidgetset")
public class MSMUI extends UI {

    private PersonServer p = null;
    private Component left;
    private Component right;
    private final ResourceBundle rb
            = ResourceBundle.getBundle(
                    "net.sourceforge.javydreamercsw.msm.web.MSMMessages",
                    Locale.getDefault());
    private final ThemeResource smallIcon
            = new ThemeResource("icons/caduceus.png");
    private Timer timer;
    private Window loginWindow = null;
    private HashMap<String, Object> parameters = new HashMap<>();
    private static final Logger LOG
            = Logger.getLogger(MSMUI.class.getName());

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        DataBaseManager.getEntityManagerFactory();
        addClickListener(new ClickListener() {

            @Override
            public void click(MouseEvents.ClickEvent event) {
                //Reset timer
            }
        });
        // Have a panel to put stuff in
        Panel panel = new Panel("Split Panels Inside This Panel");

        // Have a horizontal split panel as its content
        HorizontalSplitPanel hsplit = new HorizontalSplitPanel();
        hsplit.setSizeFull();

        panel.setContent(hsplit);

        updateScreen();

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
        if (loginWindow != null) {
            loginWindow.close();
        }
        loginWindow = new Window();
        loginWindow.setIcon(smallIcon);
        FormLayout form = new FormLayout();
        loginWindow.setContent(form);
        PropertysetItem user = new PropertysetItem();
        user.addItemProperty("username", new ObjectProperty<>(""));
        user.addItemProperty("password", new ObjectProperty<>(""));
        form.setCaption(getResource().getString("window.connection") + ":");
        final com.vaadin.ui.TextField username
                = new com.vaadin.ui.TextField(getResource().getString("general.username") + ":");
        final PasswordField password
                = new PasswordField(getResource().getString("general.password")
                        + ":");
        form.addComponent(username);
        form.addComponent(password);
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
                        if (timer == null) {
                            timer = new Timer();
                        }
                        timer.schedule(new LogoutTask(), 1 * 1000);
                        //Select root node
                        loginWindow.close();
                    }
                });
        commit.addListener(new com.vaadin.ui.Button.ClickListener() {
            @Override
            public void buttonClick(com.vaadin.ui.Button.ClickEvent event) {
                parameters.clear();
                parameters.put("un", username.getValue());
                //Login into the system
                List<Object> result
                        = DataBaseManager.namedQuery("Person.findByUserName",
                                parameters);
                if (!result.isEmpty()) {
                    try {
                        Person user = (Person) result.get(0);
                        //Now check password
                        if (user.getPassword().equals(MD5.encrypt(password.getValue()))) {
                            p = new PersonServer(user);
                            loginWindow.close();
                            p.setLastLogin(new Date());
                            p.setAttempts(0);
                            p.write2DB();
                        } else {
                            PersonServer person = new PersonServer(user);
                            person.setAttempts(person.getAttempts() + 1);
                            p.write2DB();
                            if (person.getAttempts() >= 3) {
                                Notification.show(getResource().getString("message.account.locked"),
                                        getResource().getString("message.account.locked.desc"),
                                        Notification.Type.ERROR_MESSAGE);
                            } else {
                                Notification.show(getResource().getString("message.missing.password"),
                                        getResource().getString("message.missing.password.desc"),
                                        Notification.Type.WARNING_MESSAGE);
                            }
                        }
                    } catch (MSMException ex) {
                        LOG.log(Level.SEVERE, null, ex);
                    } catch (Exception ex) {
                        LOG.log(Level.SEVERE, null, ex);
                    }
                } else {
                    Notification.show(getResource().getString("message.invalid.username"),
                            getResource().getString("message.invalid.username.desc"),
                            Notification.Type.WARNING_MESSAGE);
                }
            }
        });
        HorizontalLayout hl = new HorizontalLayout();
        hl.addComponent(commit);
        hl.addComponent(cancel);
        form.addComponent(hl);
        form.setSizeFull();
        form.setVisible(true);
        loginWindow.center();
        loginWindow.setModal(true);
        loginWindow.setWidth(300, Unit.PIXELS);
        loginWindow.setHeight(300, Unit.PIXELS);
        loginWindow.setReadOnly(true);
        loginWindow.setVisible(true);
        addWindow(loginWindow);
    }

    /**
     * @return the Resource Bundle
     */
    public ResourceBundle getResource() {
        return rb;
    }

    private void updateMenu() {
        //Do nothing
    }

    private void updateScreen() {
        //Make sure user is loged in
        if (p == null) {
            showLoginScreen();
        } else {
            switch (p.getAccessId().getId()) {
                case 1://Admin
                //Fall thru
                case 2://Staff
                //Fall thru
                case 3://Patient
                    break;
                default:
                    Notification.show(getResource().getString("message.access.invalid"),
                            getResource().getString("message.access.invalid.desc"),
                            Notification.Type.ERROR_MESSAGE);
            }
        }
    }

    @WebServlet(urlPatterns = "/*", name = "MSMUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MSMUI.class, productionMode = false)
    public static class MSMUIServlet extends VaadinServlet {
    }

    private class LogoutTask extends TimerTask {

        @Override
        public void run() {
            System.out.println("Time's up!");
            p = null;
            Notification.show(getResource().getString("message.login.timeout"),
                    getResource().getString("message.login.timeout.desc"),
                    Notification.Type.WARNING_MESSAGE);
            showLoginScreen();
        }
    }
}
