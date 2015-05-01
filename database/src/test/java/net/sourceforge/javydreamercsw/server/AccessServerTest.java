package net.sourceforge.javydreamercsw.server;

import java.util.logging.Level;
import java.util.logging.Logger;
import net.sourceforge.javydreamercsw.msm.controller.AccessJpaController;
import net.sourceforge.javydreamercsw.msm.db.manager.DataBaseManager;
import net.sourceforge.javydreamercsw.msm.server.AccessServer;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class AccessServerTest extends AbstractServerTest {

    private static final Logger LOG
            = Logger.getLogger(AccessServerTest.class.getName());

    /**
     * Test of getEntity method, of class AccessServer.
     */
    @Test
    public void testGetEntity() {
        System.out.println("getEntity");
        AccessServer instance = new AccessServer("test");
        try {
            assertNull(instance.getEntity());
            fail("Should throw an IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            //Expected
        }
        try {
            assertTrue(instance.write2DB() >= 1000);
            assertNotNull(instance.getEntity());
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
            fail();
        }
    }

    @Test
    public void testDefaultValues() {
        System.out.println("Default Values");
        //Database should have some values already
        AccessJpaController c
                = new AccessJpaController(DataBaseManager.getEntityManagerFactory());
        assertEquals(3, c.getAccessCount());
    }
}
