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
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
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
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.annotation.WebServlet;
import net.sourceforge.javydreamercsw.msm.db.Person;
import net.sourceforge.javydreamercsw.msm.db.manager.DataBaseManager;
import net.sourceforge.javydreamercsw.msm.db.manager.MSMException;
import net.sourceforge.javydreamercsw.msm.server.MD5;
import net.sourceforge.javydreamercsw.msm.server.PersonServer;
import net.sourceforge.javydreamercsw.msm.web.window.account.AccountManagement;
import net.sourceforge.javydreamercsw.msm.web.window.service.ServiceManagement;

/**
 * @author Javier Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@Theme("msmtheme")
@Widgetset("net.sourceforge.javydreamercsw.msm.manager.MSMWidgetset")
public class MSMUI extends UI {

    private static final long serialVersionUID = 4397902634406847971L;

    private PersonServer p = null;
    private Component left;
    private Component right;
    private final static ResourceBundle rb
            = ResourceBundle.getBundle(
                    "net.sourceforge.javydreamercsw.msm.web.MSMMessages",
                    Locale.getDefault());
    private final ThemeResource smallIcon
            = new ThemeResource("icons/caduceus.png");
    private Timer timer;
    private Window loginWindow = null;
    private Window manageAccount = null, manageService = null;
    private final HashMap<String, Object> parameters = new HashMap<>();
    private static final Logger LOG
            = Logger.getLogger(MSMUI.class.getName());
    private final Panel mainPanel = new Panel();
    private static boolean initialized = false;
    private final HorizontalSplitPanel hsplit = new HorizontalSplitPanel();

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        if (!initialized) {
            DataBaseManager.getEntityManagerFactory();
            DataBaseManager.updateDBState();
            initialized = true;
        }
        addClickListener(new ClickListener() {
            private static final long serialVersionUID = -1812183928286849175L;

            @Override
            public void click(MouseEvents.ClickEvent event) {
                //Reset timer
            }
        });

        // Have a horizontal split panel as its content
        hsplit.setSizeFull();

        mainPanel.setContent(hsplit);

        updateScreen();

        // Put a component in the left panel
        hsplit.setFirstComponent(left);

        hsplit.setSecondComponent(right);

        // Set the position of the splitter as percentage
        hsplit.setSplitPosition(25, Unit.PERCENTAGE);

        mainPanel.setWidth(100, Unit.PERCENTAGE);
        mainPanel.setHeight(100, Unit.PERCENTAGE);

        setContent(mainPanel);
    }

    private void showLoginScreen() {
        if (loginWindow != null) {
            loginWindow.close();
        }
        loginWindow = new Window();
        loginWindow.setIcon(smallIcon);
        String facility="Default Facility";
        try {
            InitialContext ctx = new InitialContext();
            facility = (String) ctx.lookup("java:comp/env/msm/facility-name");
        } catch (NamingException ex) {
            LOG.log(Level.WARNING, "No configured facility name, "
                    + "reverting to default.", ex);
        }
        loginWindow.setCaption(facility);
        FormLayout form = new FormLayout();
        form.setSpacing(true);
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
                    private static final long serialVersionUID = 5019806363620874205L;

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
            private static final long serialVersionUID = 5019806363620874205L;

            @Override
            public void buttonClick(com.vaadin.ui.Button.ClickEvent event) {
                parameters.clear();
                parameters.put("un", username.getValue());
                //Login into the system
                List<Object> result
                        = DataBaseManager.namedQuery("Person.findByUserName",
                                parameters);
                if (result.isEmpty()) {
                    Notification.show(getResource().getString("message.invalid.username"),
                            getResource().getString("message.invalid.username.desc"),
                            Notification.Type.WARNING_MESSAGE);
                } else {
                    try {
                        Person user = (Person) result.get(0);
                        //Now check password
                        if (user.getPassword().equals(MD5.encrypt(password.getValue()))) {
                            p = new PersonServer(user);
                            mainPanel.setCaption(getResource().getString("message.welcome")
                                    .replaceAll("%n", p.getName())
                                    .replaceAll("%ln", p.getLastname())
                                    .replaceAll("%d", p.getLogin() == null
                                                    ? getResourceBundle().getString("general.NA") : p.getLogin().toString()));
                            p.setLogin(new Date());
                            p.setAttempts(0);
                            p.write2DB();
                            p.getEntity();
                            loginWindow.close();
                            startLoginTimer();
                            updateScreen();
                        } else {
                            PersonServer person = new PersonServer(user);
                            person.setAttempts(person.getAttempts() + 1);
                            person.write2DB();
                            if (person.getAttempts() >= 3) {
                                Notification.show(getResource().getString("message.account.locked"),
                                        getResource().getString("message.account.locked.desc"),
                                        Notification.Type.ERROR_MESSAGE);
                            } else {
                                Notification.show(getResource().getString("message.invalid.password"),
                                        getResource().getString("message.invalid.password.desc"),
                                        Notification.Type.WARNING_MESSAGE);
                            }
                        }
                    } catch (MSMException ex) {
                        LOG.log(Level.SEVERE, ex.getLocalizedMessage(), ex);
                    } catch (Exception ex) {
                        LOG.log(Level.SEVERE, ex.getLocalizedMessage(), ex);
                    }
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
        loginWindow.setWidth(25, Unit.PERCENTAGE);
        loginWindow.setHeight(30, Unit.PERCENTAGE);
        loginWindow.setReadOnly(true);
        loginWindow.setVisible(true);
        addWindow(loginWindow);
    }

    private void startLoginTimer() {
        if (timer == null) {
            timer = new Timer();
        } else {
            timer.cancel();
            timer = new Timer();
        }
        //timer.schedule(new LogoutTask(), 60 * 1000);
    }

    /**
     * @return the Resource Bundle
     */
    public ResourceBundle getResource() {
        return getResourceBundle();
    }

    private void updateMenu() {
        //Do nothing
    }

    private void updateScreen() {
        //Make sure user is loged in
        if (p == null) {
            showLoginScreen();
        } else {
            left = new TabSheet();
            left.setHeight(100, Unit.PERCENTAGE);
            VerticalLayout adminTab = new VerticalLayout();
            adminTab.setSpacing(true);
            VerticalLayout staffTab = new VerticalLayout();
            staffTab.setSpacing(true);
            VerticalLayout patientTab = new VerticalLayout();
            patientTab.setSpacing(true);
            switch (p.getAccessId().getId()) {
                case 1://Admin
                    //Load Admin content on tab
                    Button manageUser
                            = new Button(getResource().getString("manage.account"),
                                    new com.vaadin.ui.Button.ClickListener() {

                                        @Override
                                        public void buttonClick(Button.ClickEvent event) {
                                            showAccountManagementScreen();
                                        }
                                    });
                    adminTab.addComponent(manageUser);
                    Button manageServices
                            = new Button(getResource().getString("manage.service"),
                                    new com.vaadin.ui.Button.ClickListener() {

                                        @Override
                                        public void buttonClick(Button.ClickEvent event) {
                                            showServiceManagementScreen();
                                        }
                                    });
                    adminTab.addComponent(manageServices);
                    ((TabSheet) left).addTab(adminTab,
                            getResource().getString("access.admin"),
                            new ThemeResource("icons/patient_record.png"));
                case 2://Staff
                    staffTab.addComponent(new Button("Staff 1"));
                    ((TabSheet) left).addTab(staffTab,
                            getResource().getString("access.staff"),
                            new ThemeResource("icons/nurse.png"));
                //Fall thru
                case 3://Patient
                    patientTab.addComponent(new Button("Patient 1"));
                    ((TabSheet) left).addTab(patientTab,
                            getResource().getString("access.person"),
                            new ThemeResource("icons/unhealthy.png"));
                    hsplit.setFirstComponent(left);
                    break;
                default:
                    Notification.show(getResource().getString("message.access.invalid"),
                            getResource().getString("message.access.invalid.desc"),
                            Notification.Type.ERROR_MESSAGE);
            }
        }
    }

    /**
     * @return the rb
     */
    public static ResourceBundle getResourceBundle() {
        return rb;
    }

    @WebServlet(urlPatterns = "/*", name = "MSMUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MSMUI.class, productionMode = false)
    public static class MSMUIServlet extends VaadinServlet {

        private static final long serialVersionUID = 5212284236537166327L;
    }

    private class LogoutTask extends TimerTask {

        @Override
        public void run() {
            System.out.println("Time's up!");
            p = null;
            updateScreen();
            Notification.show(getResource().getString("message.login.timeout"),
                    getResource().getString("message.login.timeout.desc"),
                    Notification.Type.WARNING_MESSAGE);
        }
    }

    private void showAccountManagementScreen() {
        if (manageAccount == null) {
            manageAccount = new AccountManagement();
        } else {
            manageAccount.close();
            removeWindow(manageAccount);
        }
        manageAccount.center();
        manageAccount.setModal(true);
        manageAccount.setWidth(80, Unit.PERCENTAGE);
        manageAccount.setHeight(80, Unit.PERCENTAGE);
        manageAccount.setVisible(true);
        addWindow(manageAccount);
    }

    private void showServiceManagementScreen() {
        if (manageService == null) {
            manageService = new ServiceManagement();
        } else {
            manageService.close();
            removeWindow(manageService);
        }
        manageService.center();
        manageService.setModal(true);
        manageService.setWidth(95, Unit.PERCENTAGE);
        manageService.setHeight(80, Unit.PERCENTAGE);
        manageService.setVisible(true);
        addWindow(manageService);
    }
}
