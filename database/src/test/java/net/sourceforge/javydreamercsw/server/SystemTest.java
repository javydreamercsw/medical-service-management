package net.sourceforge.javydreamercsw.server;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sourceforge.javydreamercsw.tm.Person;
import net.sourceforge.javydreamercsw.tm.PersonHasService;
import net.sourceforge.javydreamercsw.tm.TMField;
import net.sourceforge.javydreamercsw.tm.controller.TmfieldJpaController;
import net.sourceforge.javydreamercsw.tm.controller.exceptions.NonexistentEntityException;
import net.sourceforge.javydreamercsw.tm.db.manager.DataBaseManager;
import net.sourceforge.javydreamercsw.tm.server.AccessServer;
import net.sourceforge.javydreamercsw.tm.server.FieldServer;
import net.sourceforge.javydreamercsw.tm.server.PersonServer;
import net.sourceforge.javydreamercsw.tm.server.ServiceServer;
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
            TMField name = FieldServer.createStringField("Name", "Desc");
            assertNotNull(name);
            assertEquals("Name", name.getName());
            assertEquals("Desc", new String(name.getDesc(), "UTF-8"));
            assertTrue(name.getFieldTypeId().getId() == 1);
            assertNull(name.getRangeId());
            assertTrue(name.getServiceList().isEmpty());
            TMField days = FieldServer.createIntField("Days", "Desc", 0, 365);
            assertNotNull(days);
            assertEquals("Days", days.getName());
            assertEquals("Desc", new String(days.getDesc(), "UTF-8"));
            assertTrue(0 == days.getRangeId().getMin());
            assertTrue(365 == days.getRangeId().getMax());
            assertTrue(days.getFieldTypeId().getId() == 2);
            assertNotNull(days.getRangeId());
            assertTrue(days.getServiceList().isEmpty());
            TMField bp = FieldServer.createFloatField("Blood Pressure", "Desc", 60, 100);
            assertNotNull(bp);
            assertEquals("Blood Pressure", bp.getName());
            assertEquals("Desc", new String(bp.getDesc(), "UTF-8"));
            assertTrue(60 == bp.getRangeId().getMin());
            assertTrue(100 == bp.getRangeId().getMax());
            assertTrue(bp.getFieldTypeId().getId() == 3);
            assertNotNull(bp.getRangeId());
            assertTrue(bp.getServiceList().isEmpty());
            TMField b = FieldServer.createBoolField("Bool", "Desc");
            assertNotNull(b);
            assertEquals("Bool", b.getName());
            assertEquals("Desc", new String(b.getDesc(), "UTF-8"));
            assertNull(name.getRangeId());
            assertTrue(b.getFieldTypeId().getId() == 4);
            assertTrue(b.getServiceList().isEmpty());
            //Add fields to service
            ss.getTmfieldList().add(name);
            ss.getTmfieldList().add(days);
            ss.getTmfieldList().add(bp);
            ss.getTmfieldList().add(b);
            ss.write2DB();
            assertTrue(ss.getTmfieldList().size() > 0);
            
        } catch (UnsupportedEncodingException ex) {
            LOG.log(Level.SEVERE, null, ex);
            fail();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
            fail();
        }
        clean();
    }

    private void clean() {
        TmfieldJpaController controller = new TmfieldJpaController(DataBaseManager.getEntityManagerFactory());
        for (TMField a : controller.findTmfieldEntities()) {
            try {
                if (a.getId() >= 1000) {
                    new TmfieldJpaController(DataBaseManager.getEntityManagerFactory()).destroy(a.getId());
                } else {
                    LOG.log(Level.INFO, "Ignoring id: {0}", a.getId());
                }
            } catch (NonexistentEntityException ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
        }
    }
}
