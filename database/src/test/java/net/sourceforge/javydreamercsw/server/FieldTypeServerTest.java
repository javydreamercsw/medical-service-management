package net.sourceforge.javydreamercsw.server;

import java.util.logging.Level;
import java.util.logging.Logger;
import net.sourceforge.javydreamercsw.msm.controller.FieldTypeJpaController;
import net.sourceforge.javydreamercsw.msm.db.manager.DataBaseManager;
import net.sourceforge.javydreamercsw.msm.server.FieldTypeServer;
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
    }
    
    @Test
    public void testDefaultValues() {
        System.out.println("Default Values");
        //Database should have some values already
        FieldTypeJpaController c
                = new FieldTypeJpaController(DataBaseManager.getEntityManagerFactory());
        assertEquals(4, c.getFieldTypeCount());
    }
}
