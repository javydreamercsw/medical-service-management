package net.sourceforge.javydreamercsw.server;

import java.util.logging.Level;
import java.util.logging.Logger;
import net.sourceforge.javydreamercsw.msm.db.Access;
import net.sourceforge.javydreamercsw.msm.controller.AccessJpaController;
import net.sourceforge.javydreamercsw.msm.controller.exceptions.IllegalOrphanException;
import net.sourceforge.javydreamercsw.msm.controller.exceptions.NonexistentEntityException;
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

    private void clean() {
        AccessJpaController controller = new AccessJpaController(DataBaseManager.getEntityManagerFactory());
        for (Access a : controller.findAccessEntities()) {
            try {
                if (a.getId() >= 1000) {
                    new AccessJpaController(DataBaseManager.getEntityManagerFactory()).destroy(a.getId());
                } else {
                    LOG.log(Level.INFO, "Ignoring id: {0}", a.getId());
                }
            } catch (IllegalOrphanException | NonexistentEntityException ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
        }
    }

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
        clean();
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
