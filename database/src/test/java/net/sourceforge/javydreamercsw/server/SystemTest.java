package net.sourceforge.javydreamercsw.server;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sourceforge.javydreamercsw.msm.db.Person;
import net.sourceforge.javydreamercsw.msm.db.PersonHasService;
import net.sourceforge.javydreamercsw.msm.db.Field;
import net.sourceforge.javydreamercsw.msm.db.ServiceInstance;
import net.sourceforge.javydreamercsw.msm.server.AccessServer;
import net.sourceforge.javydreamercsw.msm.server.FieldServer;
import net.sourceforge.javydreamercsw.msm.server.PersonServer;
import net.sourceforge.javydreamercsw.msm.server.ServiceInstanceServer;
import net.sourceforge.javydreamercsw.msm.server.ServiceServer;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class SystemTest extends AbstractServerTest {

    private static final Logger LOG
            = Logger.getLogger(SystemTest.class.getName());

    /**
     * User creation test
     */
    @Test
    public void testCreateUser() {
        Person p = new Person();
        PersonServer ps;
        try {
            ps = new PersonServer(p);
            ps.write2DB();
            fail("Expected an exception!");
        } catch (Exception ex) {
            //Expected
        }
        p.setName("Test");
        p.setLastname("Tester");
        p.setSsn("111-11-1111");
        try {
            ps = new PersonServer(p);
            ps.write2DB();
            fail("Expected an exception!");
        } catch (Exception ex) {
            //Expected
        }
        p.setPersonHasServiceList(new ArrayList<PersonHasService>());
        p.setAccessId(new AccessServer(1).getEntity());
        p.setPassword("test12");
        p.setUsername("test1");
        try {
            ps = new PersonServer(p);
            ps.write2DB();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
            fail();
        }
    }

    /**
     * Service creation test
     */
    @Test
    public void testCreateService() {
        //Create a service
        ServiceServer ss = new ServiceServer("Test Service");
        try {
            ss.write2DB();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
            fail();
        }
        try {
            //Create fields
            Field name = FieldServer.createStringField("Name", "Desc");
            assertNotNull(name);
            assertEquals("Name", name.getName());
            assertEquals("Desc", new String(name.getDesc(), "UTF-8"));
            assertTrue(name.getFieldTypeId().getId() == 1);
            assertNull(name.getRangeId());
            assertTrue(name.getServiceHasFieldList().isEmpty());
            Field days = FieldServer.createIntField("Days", "Desc", 0, 365);
            assertNotNull(days);
            assertEquals("Days", days.getName());
            assertEquals("Desc", new String(days.getDesc(), "UTF-8"));
            assertTrue(0 == days.getRangeId().getMin());
            assertTrue(365 == days.getRangeId().getMax());
            assertTrue(days.getFieldTypeId().getId() == 2);
            assertNotNull(days.getRangeId());
            assertTrue(days.getServiceHasFieldList().isEmpty());
            Field bp = FieldServer.createFloatField("Blood Pressure", "Desc", 60, 100);
            assertNotNull(bp);
            assertEquals("Blood Pressure", bp.getName());
            assertEquals("Desc", new String(bp.getDesc(), "UTF-8"));
            assertTrue(60 == bp.getRangeId().getMin());
            assertTrue(100 == bp.getRangeId().getMax());
            assertTrue(bp.getFieldTypeId().getId() == 3);
            assertNotNull(bp.getRangeId());
            assertTrue(bp.getServiceHasFieldList().isEmpty());
            Field b = FieldServer.createBoolField("Bool", "Desc");
            assertNotNull(b);
            assertEquals("Bool", b.getName());
            assertEquals("Desc", new String(b.getDesc(), "UTF-8"));
            assertNull(name.getRangeId());
            assertTrue(b.getFieldTypeId().getId() == 4);
            assertTrue(b.getServiceHasFieldList().isEmpty());
            //Add fields to service
            int i = 1;
            ss.addField(name, i);
            ss.addField(days, ++i);
            ss.addField(bp, ++i);
            ss.addField(b, ++i);
            ss.write2DB();
            assertTrue(ss.getServiceHasFieldList().size() == 4);
            //Create a service instance
            ServiceInstanceServer sis = new ServiceInstanceServer();
            ServiceInstance si = sis.createServiceInstance(ss.getId());
            assertEquals(4, si.getInstanceFieldList().size());
            assertTrue(si.getPersonHasServiceList().isEmpty());
            assertEquals(ss.getId(), si.getServiceId().getId());
            assertTrue(si.getId() >= 1);
            //Create a person
            PersonServer ps = new PersonServer("Test", "Tester","test1","test");
            ps.setSsn("111-11-1111");
            ps.write2DB();
            //Assign service instance to person
            ps.addServiceInstance(si);
            assertEquals(1, ps.getPersonHasServiceList().size());
        } catch (UnsupportedEncodingException ex) {
            LOG.log(Level.SEVERE, null, ex);
            fail();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
            fail();
        }
    }
}
