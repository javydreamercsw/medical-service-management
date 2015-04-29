package net.sourceforge.javydreamercsw.server;

import java.util.logging.Level;
import java.util.logging.Logger;
import net.sourceforge.javydreamercsw.msm.server.PersonServer;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class PersonServerTest extends AbstractServerTest {

    /**
     * Test of getEntity method, of class AccessServer.
     */
    @Test
    public void testGetEntity() {
        System.out.println("getEntity");
        PersonServer instance = new PersonServer("test");
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
            Logger.getLogger(PersonServerTest.class.getName()).log(Level.SEVERE, null, ex);
            fail();
        }
    }
}
