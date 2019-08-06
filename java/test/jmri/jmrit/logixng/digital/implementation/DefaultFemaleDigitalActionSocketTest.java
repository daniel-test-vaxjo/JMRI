package jmri.jmrit.logixng.digital.implementation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import jmri.jmrit.logixng.Base;
import jmri.jmrit.logixng.Category;
import jmri.jmrit.logixng.FemaleSocket;
import jmri.jmrit.logixng.FemaleSocketListener;
import jmri.jmrit.logixng.FemaleSocketTestBase;
import jmri.jmrit.logixng.digital.actions.ActionTurnout;
import jmri.util.JUnitUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Test ExpressionTimer
 * 
 * @author Daniel Bergqvist 2018
 */
public class DefaultFemaleDigitalActionSocketTest extends FemaleSocketTestBase {

    private MyActionTurnout _action;
    
    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Test
    public void testBundleClass() {
        Assert.assertEquals("bundle is correct", "Test Bundle bb aa cc", Bundle.getMessage("TestBundle", "aa", "bb", "cc"));
        Assert.assertEquals("bundle is correct", "Generic", Bundle.getMessage(Locale.US, "SocketTypeGeneric"));
        Assert.assertEquals("bundle is correct", "Test Bundle bb aa cc", Bundle.getMessage(Locale.US, "TestBundle", "aa", "bb", "cc"));
    }
    
    @Test
    public void testGetName() {
        Assert.assertTrue("String matches", "A1".equals(femaleSocket.getName()));
    }
    
    @Test
    public void testGetDescription() {
        Assert.assertTrue("String matches", "!".equals(femaleSocket.getShortDescription()));
        Assert.assertTrue("String matches", "! A1".equals(femaleSocket.getLongDescription()));
    }
    
    @Override
    protected boolean hasSocketBeenSetup() {
        return _action._hasBeenSetup;
    }
    
    @Test
    public void testSystemName() {
        Assert.assertEquals("String matches", "IQDA10", femaleSocket.getExampleSystemName());
        Assert.assertEquals("String matches", "IQDA:0001", femaleSocket.getNewSystemName());
    }
    
    @Test
    public void testSetValue() {
        // Every test method should have an assertion
        Assert.assertNotNull("femaleSocket is not null", femaleSocket);
        Assert.assertFalse("femaleSocket is not connected", femaleSocket.isConnected());
        // Test execute() when not connected
        ((DefaultFemaleDigitalActionSocket)femaleSocket).execute();
    }
    
    @Test
    public void testGetConnectableClasses() {
        Map<Category, List<Class<? extends Base>>> map = new HashMap<>();
        
        List<Class<? extends Base>> classes = new ArrayList<>();
        classes.add(jmri.jmrit.logixng.digital.actions.ActionLight.class);
        classes.add(jmri.jmrit.logixng.digital.actions.ActionTurnout.class);
        classes.add(jmri.jmrit.logixng.digital.actions.ActionSensor.class);
        map.put(Category.ITEM, classes);
        
        classes = new ArrayList<>();
        classes.add(jmri.jmrit.logixng.digital.actions.DoAnalogAction.class);
        classes.add(jmri.jmrit.logixng.digital.actions.Many.class);
        classes.add(jmri.jmrit.logixng.digital.actions.HoldAnything.class);
        classes.add(jmri.jmrit.logixng.digital.actions.DoStringAction.class);
        classes.add(jmri.jmrit.logixng.digital.actions.IfThen.class);
        map.put(Category.COMMON, classes);
        
        classes = new ArrayList<>();
        map.put(Category.OTHER, classes);
        
        classes = new ArrayList<>();
        classes.add(jmri.jmrit.logixng.digital.actions.ShutdownComputer.class);
        map.put(Category.EXRAVAGANZA, classes);
        
        Assert.assertTrue("maps are equal",
                isConnectionClassesEquals(map, femaleSocket.getConnectableClasses()));
    }
    
    @Test
    public void testGetNewObjectBasedOnTemplate() {
        thrown.expect(UnsupportedOperationException.class);
        femaleSocket.getNewObjectBasedOnTemplate(null);
    }
    
    // The minimal setup for log4J
    @Before
    public void setUp() {
        JUnitUtil.setUp();
        JUnitUtil.resetInstanceManager();
        JUnitUtil.initInternalSensorManager();
        JUnitUtil.initInternalTurnoutManager();
        
        flag = new AtomicBoolean();
        errorFlag = new AtomicBoolean();
        _action = new MyActionTurnout("IQDA321");
        ActionTurnout otherAction = new MyActionTurnout("IQDA322");
        maleSocket = new DefaultMaleDigitalActionSocket(_action);
        otherMaleSocket = new DefaultMaleDigitalActionSocket(otherAction);
        femaleSocket = new DefaultFemaleDigitalActionSocket(null, new FemaleSocketListener() {
            @Override
            public void connected(FemaleSocket socket) {
                flag.set(true);
            }

            @Override
            public void disconnected(FemaleSocket socket) {
                flag.set(true);
            }
        }, "A1");
    }

    @After
    public void tearDown() {
        JUnitUtil.tearDown();
    }
    
    
    
    private class MyActionTurnout extends ActionTurnout {
        
        private boolean _hasBeenSetup = false;
        
        public MyActionTurnout(String systemName) {
            super(systemName);
        }
        
        /** {@inheritDoc} */
        @Override
        public void setup() {
            _hasBeenSetup = true;
        }
    }
    
}
