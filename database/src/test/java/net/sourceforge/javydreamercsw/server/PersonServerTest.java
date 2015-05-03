package net.sourceforge.javydreamercsw.server;

import java.util.logging.Level;
import java.util.logging.Logger;
import net.sourceforge.javydreamercsw.msm.db.Person;
import net.sourceforge.javydreamercsw.msm.server.MD5;
import net.sourceforge.javydreamercsw.msm.server.PersonServer;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;

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

    @Test
    public void testMD5() {
        System.out.println("Test MD5");
        PersonServer instance = new PersonServer("test");
        try {
            String pass = "test";
            instance.setPassword(pass);
            assertTrue(instance.write2DB() >= 1000);
            assertEquals(MD5.encrypt(pass), instance.getPassword());
            Person person = instance.getEntity();
            assertEquals(MD5.encrypt(pass), person.getPassword());
            instance = new PersonServer(person);
            assertEquals(MD5.encrypt(pass), instance.getPassword());
            instance.write2DB();
            assertEquals(MD5.encrypt(pass), instance.getPassword());
        } catch (Exception ex) {
            Logger.getLogger(PersonServerTest.class.getName()).log(Level.SEVERE, null, ex);
            fail();
        }
    }
}
