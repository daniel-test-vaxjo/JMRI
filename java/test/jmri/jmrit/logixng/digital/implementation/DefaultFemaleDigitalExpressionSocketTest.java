package jmri.jmrit.logixng.digital.implementation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import jmri.jmrit.logixng.Base;
import jmri.jmrit.logixng.Category;
import jmri.jmrit.logixng.FemaleSocket;
import jmri.jmrit.logixng.FemaleSocketListener;
import jmri.jmrit.logixng.FemaleSocketTestBase;
import jmri.jmrit.logixng.digital.expressions.ExpressionTurnout;
import jmri.util.JUnitUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Test DefaultFemaleDigitalExpressionSocket
 * 
 * @author Daniel Bergqvist 2018
 */
public class DefaultFemaleDigitalExpressionSocketTest extends FemaleSocketTestBase {

    private MyExpressionTurnout _expression;
    
    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Test
    public void testGetName() {
        Assert.assertTrue("String matches", "E1".equals(femaleSocket.getName()));
    }
    
    @Test
    public void testGetDescription() {
        Assert.assertTrue("String matches", "?".equals(femaleSocket.getShortDescription()));
        Assert.assertTrue("String matches", "? E1".equals(femaleSocket.getLongDescription()));
    }
    
    @Override
    protected boolean hasSocketBeenSetup() {
        return _expression._hasBeenSetup;
    }
    
    @Test
    public void testSystemName() {
        Assert.assertEquals("String matches", "IQDE10", femaleSocket.getExampleSystemName());
        Assert.assertEquals("String matches", "IQDE:0001", femaleSocket.getNewSystemName());
    }
    
    @Test
    public void testSetValue() {
        // Every test method should have an assertion
        Assert.assertNotNull("femaleSocket is not null", femaleSocket);
        Assert.assertFalse("femaleSocket is not connected", femaleSocket.isConnected());
        // Test evaluate() when not connected
        Assert.assertFalse("result is false", ((DefaultFemaleDigitalExpressionSocket)femaleSocket).evaluate());
    }
    
    @Test
    public void testGetConnectableClasses() {
        Map<Category, List<Class<? extends Base>>> map = new HashMap<>();
        
        List<Class<? extends Base>> classes = new ArrayList<>();
        classes.add(jmri.jmrit.logixng.digital.expressions.ExpressionLight.class);
        classes.add(jmri.jmrit.logixng.digital.expressions.ExpressionSensor.class);
        classes.add(jmri.jmrit.logixng.digital.expressions.ExpressionTurnout.class);
        map.put(Category.ITEM, classes);
        
        classes = new ArrayList<>();
        classes.add(jmri.jmrit.logixng.digital.expressions.And.class);
        classes.add(jmri.jmrit.logixng.digital.expressions.Antecedent.class);
        classes.add(jmri.jmrit.logixng.digital.expressions.Or.class);
        classes.add(jmri.jmrit.logixng.digital.expressions.Timer.class);
        map.put(Category.COMMON, classes);
        
        classes = new ArrayList<>();
        classes.add(jmri.jmrit.logixng.digital.expressions.False.class);
        classes.add(jmri.jmrit.logixng.digital.expressions.Hold.class);
        classes.add(jmri.jmrit.logixng.digital.expressions.ResetOnTrue.class);
        classes.add(jmri.jmrit.logixng.digital.expressions.TriggerOnce.class);
        classes.add(jmri.jmrit.logixng.digital.expressions.True.class);
        map.put(Category.OTHER, classes);
        
        classes = new ArrayList<>();
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
        _expression = new MyExpressionTurnout("IQDE321");
        ExpressionTurnout otherExpression = new ExpressionTurnout("IQDE322");
        maleSocket = new DefaultMaleDigitalExpressionSocket(_expression);
        otherMaleSocket = new DefaultMaleDigitalExpressionSocket(otherExpression);
        femaleSocket = new DefaultFemaleDigitalExpressionSocket(null, new FemaleSocketListener() {
            @Override
            public void connected(FemaleSocket socket) {
                flag.set(true);
            }

            @Override
            public void disconnected(FemaleSocket socket) {
                flag.set(true);
            }
        }, "E1");
    }

    @After
    public void tearDown() {
        JUnitUtil.tearDown();
    }
    
    
    
    private class MyExpressionTurnout extends ExpressionTurnout {
        
        private boolean _hasBeenSetup = false;
        
        public MyExpressionTurnout(String systemName) {
            super(systemName);
        }
        
        /** {@inheritDoc} */
        @Override
        public void setup() {
            _hasBeenSetup = true;
        }
    }
    
}
