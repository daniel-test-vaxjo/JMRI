package jmri.jmrit.logixng.tools;

import java.util.ArrayList;

import jmri.*;
import jmri.implementation.*;
import jmri.util.JUnitUtil;

import org.junit.After;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

/**
 * Test import of Logix to LogixNG
 * 
 * @author Daniel Bergqvist (C) 2020
 */
public class ImportTest {
    
    @Test
    public void testImportLogix() {
/*        
        ConditionalManager conditionalManager = InstanceManager.getDefault(ConditionalManager.class);
        
        Logix logix = new DefaultLogix("IX1");
        
        Conditional conditional = conditionalManager.createNewConditional("IXC1", "First conditional");
        logix.addConditional(conditional.getSystemName(), 0);
        
        conditional.setLogicType(Conditional.AntecedentOperator.ALL_AND, "");
        conditional.setLogicType(Conditional.AntecedentOperator.ALL_OR, "");
        conditional.setLogicType(Conditional.AntecedentOperator.MIXED, "R1 AND R2");
        
        conditional.setTriggerOnChange(false);
        conditional.setTriggerOnChange(true);
        
        ArrayList<ConditionalVariable> variables = new ArrayList<>();
        conditional.setStateVariables(variables);
        
        ArrayList<ConditionalAction> actions = new ArrayList<>();
        conditional.setAction(actions);
*/
    }
    
    // The minimal setup for log4J
    @Before
    public void setUp() {
        JUnitUtil.setUp();
        JUnitUtil.resetInstanceManager();
        JUnitUtil.resetProfileManager();
        JUnitUtil.initConfigureManager();
        JUnitUtil.initInternalSensorManager();
        JUnitUtil.initInternalTurnoutManager();
        JUnitUtil.initLogixManager();
        JUnitUtil.initLogixNGManager();
    }

    @After
    public void tearDown() {
        JUnitUtil.deregisterBlockManagerShutdownTask();
        JUnitUtil.tearDown();
    }
    
}
