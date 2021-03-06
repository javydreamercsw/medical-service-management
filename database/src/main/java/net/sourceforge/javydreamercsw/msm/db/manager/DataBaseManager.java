package net.sourceforge.javydreamercsw.msm.db.manager;

import com.googlecode.flyway.core.Flyway;
import com.googlecode.flyway.core.api.FlywayException;
import com.googlecode.flyway.core.api.MigrationInfo;
import com.googlecode.flyway.core.api.MigrationState;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import static java.lang.Class.forName;
import static java.lang.Integer.parseInt;
import static java.lang.Long.valueOf;
import static java.lang.Thread.sleep;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import static java.util.ResourceBundle.getBundle;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.logging.Logger.getLogger;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import static javax.persistence.Persistence.createEntityManagerFactory;
import javax.persistence.Query;
import javax.sql.DataSource;
import org.h2.jdbcx.JdbcDataSource;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class DataBaseManager {

    private static EntityManagerFactory emf;
    private static Map<String, Object> properties;
    //Default. Can be overwritten using setPersistenceUnitName(String aPU)
    private static String PU = "MSMPUJNDI";
    private static EntityManager em;
    private static boolean dbError = false;
    private static final Logger LOG
            = getLogger(DataBaseManager.class.getSimpleName());
    private static DBState state = DBState.START_UP;
    private static final ResourceBundle settings
            = getBundle("net.sourceforge.javydreamercsw.msm.settings");
    private static boolean locked = false;
    private static boolean usingContext;
    private static boolean demo;
    private static Long demoResetPeriod;
    private static DataBaseManager instance;
    private static boolean versioning_enabled = false;

    /**
     * @return the versioning enabled
     */
    public static boolean isVersioningEnabled() {
        return versioning_enabled;
    }

    /**
     * @param aVersioning_enabled the versioning_enabled to set
     */
    public static void setVersioningEnabled(boolean aVersioning_enabled) {
        versioning_enabled = aVersioning_enabled;
    }

    /**
     * @return the demoResetPeriod
     */
    public static Long getDemoResetPeriod() {
        return demoResetPeriod;
    }

    private DataBaseManager() {
    }

    public static DataBaseManager get() throws Exception {
        if (instance == null) {
            instance = new DataBaseManager();
        }
        return instance;
    }

    /**
     * @return the properties
     */
    public static Map<String, Object> getProperties() {
        return properties;
    }

    /**
     * Get the current persistence unit name
     *
     * @return current persistence unit name
     */
    public static String getPersistenceUnitName() {
        return PU;
    }

    /**
     * @param aPU the PU to set
     */
    public static void setPersistenceUnitName(String aPU) {
        PU = aPU;
        LOG.log(Level.FINE, "Changed persistence unit name to: {0}", PU);
        //Set it to null so it's recreated with new Persistence Unit next time is requested.
        emf = null;
        em = null;
        try {
            reload();
        } catch (MSMException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
    }

    public static void setEntityManagerFactory(EntityManagerFactory newEMF) {
        emf = newEMF;
    }

    /**
     * @return the emf
     */
    public static EntityManagerFactory getEntityManagerFactory() {
        if (emf == null && !dbError) {
            try {
                InitialContext ctx = new InitialContext();
                ctx.lookup("java:/comp/env/jdbc/MSMDB");
                //Use the context defined Database connection
                PU = (String) ctx.lookup("java:comp/env/msm/JNDIDB");
                try {
                    demo = (Boolean) ctx.lookup("java:comp/env/msm/demo");
                } catch (NamingException e) {
                    LOG.log(Level.SEVERE, null, e);
                    demo = false;
                }
                if (isDemo()) {
                    try {
                        demoResetPeriod = (Long) ctx.lookup("java:comp/env/msm/demo-period");
                    } catch (NamingException e) {
                        LOG.log(Level.SEVERE, null, e);
                        demoResetPeriod = valueOf(0);
                    }
                    if (getDemoResetPeriod() > 0) {
                        LOG.log(Level.WARNING,
                                "Instance configured as demo, database will reset"
                                + " each {0} milliseconds", getDemoResetPeriod());
                    }
                }
                final String JNDIDB = (String) ctx.lookup("java:comp/env/msm/JNDIDB");
                emf = createEntityManagerFactory(JNDIDB);
                LOG.log(Level.INFO, "Using context defined database connection: {0}", JNDIDB);
                usingContext = true;
            } catch (NamingException e) {
                LOG.log(Level.FINE, null, e);
                if (!usingContext) {
                    LOG.log(Level.WARNING,
                            "Manually specified connection parameters. "
                            + "Using pre-defined persistence unit: {0}", PU);
                    emf = createEntityManagerFactory(PU);
                } else {
                    LOG.log(Level.SEVERE,
                            "Context doesn't exist. Check your configuration.", e);
                    dbError = true;
                }
            }
        }
        return emf;
    }

    /**
     * @return the demo
     */
    public static boolean isDemo() {
        return demo;
    }

    public static EntityManager getEntityManager() {
        if (em == null) {
            em = getEntityManagerFactory().createEntityManager();
            LOG.log(Level.FINE,
                    "Creating EntityManager from: {0}", PU);
            properties = em.getProperties();
        }
        return em;
    }

    public static List<Object> createdQuery(String query) {
        return createdQuery(query, null);
    }

    @SuppressWarnings("unchecked")
    public static List<Object> createdQuery(String query, Map<String, Object> parameters) {
        EntityTransaction transaction = getEntityManager().getTransaction();
        transaction.begin();
        Query q = getEntityManager().createQuery(query);
        if (parameters != null) {
            Iterator<Entry<String, Object>> entries = parameters.entrySet().iterator();
            while (entries.hasNext()) {
                Entry<String, Object> e = entries.next();
                q.setParameter(e.getKey(), e.getValue());
            }
        }
        transaction.commit();
        return q.getResultList();
    }

    /**
     * Named query that will modify the database
     *
     * @param query query to execute
     */
    public static void namedUpdateQuery(String query) {
        namedQuery(query, null, true);
    }

    public static List<Object> nativeQuery(String query) {
        EntityTransaction transaction = getEntityManager().getTransaction();
        transaction.begin();
        List<Object> resultList
                = getEntityManager().createNativeQuery(query).getResultList();
        transaction.commit();
        return resultList;
    }

    public static void nativeUpdateQuery(String query) {
        boolean atomic = false;
        EntityTransaction transaction = getEntityManager().getTransaction();
        if (!getEntityManager().getTransaction().isActive()) {
            transaction.begin();
            atomic = true;
        }
        getEntityManager().createNativeQuery(query).executeUpdate();
        if (atomic) {
            transaction.commit();
        }
    }

    /**
     * Named query (not for updates)
     *
     * @param query query to execute
     * @return query result
     */
    public static List<Object> namedQuery(String query) {
        return namedQuery(query, null, false);
    }

    /**
     * Named query that will modify the database
     *
     * @param query query to execute
     * @param parameters query parameters
     */
    public static void namedUpdateQuery(String query, Map<String, Object> parameters) {
        namedQuery(query, parameters, true);
    }

    /**
     * Named query (not for updates)
     *
     * @param query query to execute
     * @param parameters query parameters
     * @return query result
     */
    public static List<Object> namedQuery(String query,
            Map<String, Object> parameters) {
        return namedQuery(query, parameters, false);
    }

    @SuppressWarnings("unchecked")
    private static List<Object> namedQuery(String query,
            Map<String, Object> parameters, boolean change) {
        EntityTransaction transaction = getEntityManager().getTransaction();
        if (change) {
            transaction.begin();
        }
        Query q = getEntityManager().createNamedQuery(query);
        if (parameters != null) {
            Iterator<Entry<String, Object>> entries
                    = parameters.entrySet().iterator();
            while (entries.hasNext()) {
                Entry<String, Object> e = entries.next();
                q.setParameter(e.getKey(), e.getValue());
            }
        }
        if (change) {
            transaction.commit();
        }
        return q.getResultList();
    }

    public static void close() {
        getEntityManager().close();
        getEntityManagerFactory().close();
        emf = null;
    }

    public static EntityTransaction getTransaction() {
        return getEntityManager().getTransaction();
    }

    /**
     * Change the contents of text file in its entirety, overwriting any
     * existing text.
     *
     * This style of implementation throws all exceptions to the caller.
     *
     * @param aFile is an existing file which can be written to.
     * @param aContents contents to set
     * @throws IllegalArgumentException if param does not comply.
     * @throws FileNotFoundException if the file does not exist.
     * @throws IOException if problem encountered during write.
     */
    static public void setContents(File aFile, ArrayList<String> aContents)
            throws FileNotFoundException, IOException {
        if (aFile == null) {
            throw new IllegalArgumentException("File should not be null.");
        }
        if (!aFile.exists()) {
            throw new FileNotFoundException("File does not exist: " + aFile);
        }
        if (!aFile.isFile()) {
            throw new IllegalArgumentException("Should not be a directory: " + aFile);
        }
        if (!aFile.canWrite()) {
            throw new IllegalArgumentException("File cannot be written: " + aFile);
        }
        FileWriter fw = new FileWriter(aFile);
        try ( //use buffering
                Writer output = new BufferedWriter(fw)) {
            //FileWriter always assumes default encoding is OK!
            for (String line : aContents) {
                output.write(line);
                output.write("\n");
            }
        } finally {
            fw.close();
        }
    }

    public static void reload() throws MSMException {
        reload(false);
    }

    public static void reload(boolean close) throws MSMException {
        if (close) {
            close();
        }
        updateDBState();
        getEntityManager();
    }

    public static void updateDBState() {
        DataSource ds = null;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            ds = (javax.sql.DataSource) new InitialContext().lookup("java:comp/env/jdbc/MSMDB");
            conn = ds.getConnection();
        } catch (NamingException ne) {
            LOG.log(Level.FINE, null, ne);
            if (emf == null) {
                try {
                    //It might be the tests, use an H2 Database
                    ds = new JdbcDataSource();
                    ((JdbcDataSource) ds).setPassword("");
                    ((JdbcDataSource) ds).setUser("msm_user");
                    ((JdbcDataSource) ds).setURL(
                            "jdbc:h2:file:./target/data/test/medical-service-manager-test;AUTO_SERVER=TRUE;MV_STORE=FALSE;MVCC=FALSE");
                    //Load the H2 driver
                    forName("org.h2.Driver");
                    conn = ds.getConnection();
                } catch (ClassNotFoundException | SQLException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                }
            } else {
                EntityTransaction transaction = getEntityManager().getTransaction();
                transaction.begin();
                conn = getEntityManager().unwrap(java.sql.Connection.class);
                transaction.commit();
            }
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
        if (conn != null) {
            try {
                stmt = conn.prepareStatement("select * from access");
                rs = stmt.executeQuery();
                if (!rs.next()) {
                    //Tables there but empty? Not safe to proceed
                    setState(DBState.NEED_MANUAL_UPDATE);
                }
            } catch (SQLException ex) {
                LOG.log(Level.FINE, null, ex);
                //Need INIT, probably nothing there
                setState(DBState.NEED_INIT);
                //Create the database
                getEntityManager();
            } finally {
                try {
                    conn.close();
                } catch (SQLException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                }
                try {
                    if (stmt != null) {
                        stmt.close();
                    }
                } catch (SQLException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                }
                try {
                    if (rs != null) {
                        rs.close();
                    }
                } catch (SQLException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                }
            }
        }
        if (ds != null) {
            try {
                LOG.log(Level.INFO, "Connecting to: {0}", 
                        ds.getConnection().getMetaData().getURL());
            } catch (SQLException ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
            //Initialize flyway
            initializeFlyway(ds);
            updateDatabase(ds);
        } else {
            state = DBState.ERROR;
        }

        if (state != DBState.VALID) {
            waitForDB();
        }
    }

    private static void updateDatabase(DataSource ds) {
        Flyway flyway = new Flyway();
        try {
            flyway.setDataSource(ds);
            flyway.setLocations("db.migration");
            LOG.info("Starting migration...");
            flyway.migrate();
            LOG.info("Done!");
        } catch (FlywayException fe) {
            LOG.log(Level.SEVERE, "Unable to migrate data", fe);
            setState(DBState.ERROR);
        }
        try {
            LOG.info("Validating migration...");
            flyway.validate();
            LOG.info("Done!");
            setState(flyway.info().current().getState()
                    == MigrationState.SUCCESS ? DBState.VALID : DBState.ERROR);
        } catch (FlywayException fe) {
            LOG.log(Level.SEVERE, "Unable to validate", fe);
            setState(DBState.ERROR);
        }
    }

    protected static void setState(DBState newState) {
        state = newState;
    }

    public static void waitForDB() {
        while (getState() != DBState.VALID
                && getState() != DBState.UPDATED
                && getState() != DBState.ERROR) {
            LOG.log(Level.INFO,
                    "Waiting for DB initialization. Current state: {0}",
                    (getState() != null ? getState().name() : null));
            try {
                sleep(100);
            } catch (InterruptedException ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
        }
        LOG.log(Level.INFO, "DB ready, resuming...");
    }

    /**
     * @return the state
     */
    public static DBState getState() {
        return state;
    }

    /**
     * @param lock the lock to set
     */
    public static void setLocked(boolean lock) {
        LOG.log(Level.WARNING, "{0} the database.",
                (lock ? "Locking" : "Unlocking"));
        locked = lock;
    }

    public static String getVersion() {
        return getVersionNumber() + ((settings.getString("version.postfix").isEmpty()
                ? "" : " " + settings.getString("version.postfix")));
    }

    public static String getVersionNumber() {
        StringBuilder version = new StringBuilder();
        version.append(settings.getString("version.high"));
        version.append(".");
        version.append(settings.getString("version.mid"));
        version.append(".");
        version.append(settings.getString("version.low"));
        return version.toString();
    }

    /**
     * Compare two number strings. For example: 2.1.0 == 2.01.00
     *
     * @param first first string to compare
     * @param second second string to compare
     * @return
     */
    public static boolean compareNumberStrings(String first, String second) {
        return compareNumberStrings(first, second, ".");
    }

    /**
     * Compare two number strings. For example: 2.1.0 == 2.01.00
     *
     * @param first first string to compare
     * @param second second string to compare
     * @param separator separator of fields (i.e. for 2.1.0 is '.')
     * @return
     */
    public static boolean compareNumberStrings(String first, String second,
            String separator) {
        boolean result = true;
        StringTokenizer firstST = new StringTokenizer(first, separator);
        StringTokenizer secondST = new StringTokenizer(second, separator);
        if (firstST.countTokens() != secondST.countTokens()) {
            //Different amount of fields, not equal. (i.e. 2.1 and 2.1.1
            result = false;
        } else {
            try {
                while (firstST.hasMoreTokens()) {
                    int firstInt = parseInt(firstST.nextToken());
                    int secondInt = parseInt(secondST.nextToken());
                    //Both numbers let's continue
                    if (firstInt != secondInt) {
                        result = false;
                        break;
                    }
                }
                //Everything the same
            } catch (java.lang.NumberFormatException e) {
                LOG.log(Level.WARNING, null, e);
                //Is not a number
                result = false;
            }
        }
        return result;
    }

    /**
     * @return the locked
     */
    public static boolean isLocked() {
        return locked;
    }

    private static void initializeFlyway(DataSource dataSource) {
        assert dataSource != null;
        setState(DBState.START_UP);
        Flyway flyway = new Flyway();
        flyway.setDataSource(dataSource);
        MigrationInfo status = flyway.info().current();
        if (status == null) {
            setState(DBState.NEED_INIT);
            LOG.info("Initialize the metadata...");
            try {
                flyway.init();
                LOG.info("Done!");
            } catch (FlywayException fe) {
                LOG.log(Level.SEVERE, "Unable to initialize database", fe);
                setState(DBState.ERROR);
            }
        } else {
            LOG.info("Database has Flyway metadata already...");
            displayDBStatus(status);
        }
    }

    private static void displayDBStatus(MigrationInfo status) {
        LOG.log(Level.INFO, "Description: {0}\nState: {1}\nVersion: {2}",
                new Object[]{status.getDescription(), status.getState(), status.getVersion()});
    }
}
