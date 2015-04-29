package net.sourceforge.javydreamercsw.server;

import net.sourceforge.javydreamercsw.tm.db.manager.DataBaseManager;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public abstract class AbstractServerTest {

    @BeforeClass
    public static void setUpClass() {
        DataBaseManager.setPersistenceUnitName("Test-PU");
    }

    @AfterClass
    public static void tearDownClass() {
        
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }
}
