package net.sourceforge.javydreamercsw.server;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sourceforge.javydreamercsw.msm.controller.AccessJpaController;
import net.sourceforge.javydreamercsw.msm.controller.FieldTypeJpaController;
import net.sourceforge.javydreamercsw.msm.controller.InstanceFieldJpaController;
import net.sourceforge.javydreamercsw.msm.controller.PersonHasServiceJpaController;
import net.sourceforge.javydreamercsw.msm.controller.PersonJpaController;
import net.sourceforge.javydreamercsw.msm.controller.ServiceHasFieldJpaController;
import net.sourceforge.javydreamercsw.msm.controller.ServiceInstanceJpaController;
import net.sourceforge.javydreamercsw.msm.controller.TMFieldJpaController;
import net.sourceforge.javydreamercsw.msm.controller.exceptions.IllegalOrphanException;
import net.sourceforge.javydreamercsw.msm.controller.exceptions.NonexistentEntityException;
import net.sourceforge.javydreamercsw.msm.db.Access;
import net.sourceforge.javydreamercsw.msm.db.FieldType;
import net.sourceforge.javydreamercsw.msm.db.InstanceField;
import net.sourceforge.javydreamercsw.msm.db.Person;
import net.sourceforge.javydreamercsw.msm.db.PersonHasService;
import net.sourceforge.javydreamercsw.msm.db.ServiceHasField;
import net.sourceforge.javydreamercsw.msm.db.ServiceInstance;
import net.sourceforge.javydreamercsw.msm.db.TMField;
import net.sourceforge.javydreamercsw.msm.db.manager.DataBaseManager;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.BeforeClass;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public abstract class AbstractServerTest {

    private static final Logger LOG
            = Logger.getLogger(AbstractServerTest.class.getName());

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
        clean();
    }

    protected void clean() {
        LOG.info("Cleaning after test...");
        TMFieldJpaController tfc = new TMFieldJpaController(DataBaseManager.getEntityManagerFactory());
        for (TMField a : tfc.findTMFieldEntities()) {
            try {
                if (a.getId() >= 1000) {
                    deleteInstanceField(a.getInstanceFieldList());
                    deleteServiceHasField(a.getServiceHasFieldList());
                    tfc.destroy(a.getId());
                } else {
                    LOG.log(Level.FINE, "Ignoring id: {0}", a.getId());
                }
            } catch (NonexistentEntityException | IllegalOrphanException ex) {
                LOG.log(Level.SEVERE, null, ex);
                fail();
            }
        }
        ServiceInstanceJpaController sic = new ServiceInstanceJpaController(DataBaseManager.getEntityManagerFactory());
        for (ServiceInstance si : sic.findServiceInstanceEntities()) {
            try {
                deleteInstanceField(si.getInstanceFieldList());
                deletePersonHasService(si.getPersonHasServiceList());
                sic.destroy(si.getId());
            } catch (NonexistentEntityException | IllegalOrphanException ex) {
                LOG.log(Level.SEVERE, null, ex);
                fail();
            }
        }

        AccessJpaController controller = new AccessJpaController(DataBaseManager.getEntityManagerFactory());
        for (Access a : controller.findAccessEntities()) {
            try {
                if (a.getId() >= 1000) {
                    controller.destroy(a.getId());
                } else {
                    LOG.log(Level.FINE, "Ignoring id: {0}", a.getId());
                }
            } catch (IllegalOrphanException | NonexistentEntityException ex) {
                LOG.log(Level.SEVERE, null, ex);
                fail();
            }
        }

        FieldTypeJpaController ftc = new FieldTypeJpaController(DataBaseManager.getEntityManagerFactory());
        for (FieldType a : ftc.findFieldTypeEntities()) {
            try {
                if (a.getId() >= 1000) {
                    ftc.destroy(a.getId());
                } else {
                    LOG.log(Level.FINE, "Ignoring id: {0}", a.getId());
                }
            } catch (IllegalOrphanException | NonexistentEntityException ex) {
                LOG.log(Level.SEVERE, null, ex);
                fail();
            }
        }

        PersonJpaController pc = new PersonJpaController(DataBaseManager.getEntityManagerFactory());
        for (Person a : pc.findPersonEntities()) {
            try {
                if (a.getId() >= 1000) {
                    pc.destroy(a.getId());
                } else {
                    LOG.log(Level.FINE, "Ignoring id: {0}", a.getId());
                }
            } catch (IllegalOrphanException | NonexistentEntityException ex) {
                LOG.log(Level.SEVERE, null, ex);
                fail();
            }
        }
        LOG.info("Done!");
    }

    private void deleteServiceHasField(List<ServiceHasField> list) throws NonexistentEntityException {
        for (ServiceHasField shf : list) {
            new ServiceHasFieldJpaController(DataBaseManager.getEntityManagerFactory()).destroy(shf.getServiceHasFieldPK());
        }
    }

    private void deleteInstanceField(List<InstanceField> list) throws NonexistentEntityException {
        for (InstanceField i : list) {
            new InstanceFieldJpaController(DataBaseManager.getEntityManagerFactory()).destroy(i.getInstanceFieldPK());
        }
    }

    private void deletePersonHasService(List<PersonHasService> list) throws NonexistentEntityException {
        for (PersonHasService phs : list) {
            new PersonHasServiceJpaController(DataBaseManager.getEntityManagerFactory()).destroy(phs.getPersonHasServicePK());
        }
    }
}
