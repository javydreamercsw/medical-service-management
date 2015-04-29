package net.sourceforge.javydreamercsw.server;

import java.util.logging.Level;
import java.util.logging.Logger;
import net.sourceforge.javydreamercsw.tm.FieldType;
import net.sourceforge.javydreamercsw.tm.controller.FieldTypeJpaController;
import net.sourceforge.javydreamercsw.tm.controller.exceptions.IllegalOrphanException;
import net.sourceforge.javydreamercsw.tm.controller.exceptions.NonexistentEntityException;
import net.sourceforge.javydreamercsw.tm.db.manager.DataBaseManager;
import net.sourceforge.javydreamercsw.tm.server.FieldTypeServer;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class FieldTypeServerTest extends AbstractServerTest {

    /**
     * Test of getEntity method, of class AccessServer.
     */
    @Test
    public void testGetEntity() {
        System.out.println("getEntity");
        FieldTypeServer instance = new FieldTypeServer("test");
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
            Logger.getLogger(FieldTypeServerTest.class.getName()).log(Level.SEVERE, null, ex);
            fail();
        }
        clean();
    }
    
    @Test
    public void testDefaultValues() {
        System.out.println("Default Values");
        //Database should have some values already
        FieldTypeJpaController c
                = new FieldTypeJpaController(DataBaseManager.getEntityManagerFactory());
        assertEquals(4, c.getFieldTypeCount());
    }
    
    private void clean() {
        FieldTypeJpaController controller = new FieldTypeJpaController(DataBaseManager.getEntityManagerFactory());
        for (FieldType a : controller.findFieldTypeEntities()) {
            try {
                if (a.getId() >= 1000) {
                    new FieldTypeJpaController(DataBaseManager.getEntityManagerFactory()).destroy(a.getId());
                } else {
                    Logger.getLogger(FieldTypeServerTest.class.getName()).log(Level.INFO, "Ignoring id: {0}", a.getId());
                }
            } catch (IllegalOrphanException | NonexistentEntityException ex) {
                Logger.getLogger(FieldTypeServerTest.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
