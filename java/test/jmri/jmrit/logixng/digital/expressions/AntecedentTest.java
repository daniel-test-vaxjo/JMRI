package jmri.jmrit.logixng.digital.expressions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import jmri.InstanceManager;
import jmri.jmrit.logixng.ConditionalNG;
import jmri.jmrit.logixng.DigitalExpressionManager;
import jmri.jmrit.logixng.FemaleDigitalExpressionSocket;
import jmri.jmrit.logixng.FemaleSocket;
import jmri.jmrit.logixng.FemaleSocketListener;
import jmri.jmrit.logixng.LogixNG;
import jmri.jmrit.logixng.LogixNG_Manager;
import jmri.jmrit.logixng.MaleSocket;
import jmri.jmrit.logixng.SocketAlreadyConnectedException;
import jmri.jmrit.logixng.digital.expressions.Antecedent.ExpressionEntry;
import jmri.jmrit.logixng.implementation.DefaultConditionalNG;
import jmri.util.JUnitUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import jmri.jmrit.logixng.DigitalExpressionBean;

/**
 * Test And
 * 
 * @author Daniel Bergqvist 2018
 */
public class AntecedentTest implements FemaleSocketListener {

    private static final boolean EXPECT_SUCCESS = true;
    private static final boolean EXPECT_FAILURE = false;

    private LogixNG logixNG;
    private ConditionalNG conditionalNG;
    
    
    @Test
    public void testCtor() {
        Antecedent t = new Antecedent("IQA55:E321", null, "R1");
        Assert.assertNotNull("exists",t);
    }
    
    @Test
    public void testDescription() {
        DigitalExpressionBean e1 = new Antecedent("IQA55:E321");
        Assert.assertTrue("Antecedent".equals(e1.getShortDescription()));
        Assert.assertTrue("Antecedent: empty".equals(e1.getLongDescription()));
    }
    
    private void testValidate(boolean expectedResult, String antecedent, List<DigitalExpressionBean> conditionalVariablesList) throws SocketAlreadyConnectedException {
        Antecedent ix1 = new Antecedent("IXIC 1");
        
        int count = 0;
        List<ExpressionEntry> expressionEntryList = new ArrayList<>();
        for (DigitalExpressionBean expression : conditionalVariablesList) {
            String socketName = "E"+Integer.toString(count++);
            FemaleDigitalExpressionSocket socket =
                    InstanceManager.getDefault(DigitalExpressionManager.class)
                            .createFemaleSocket(null, this, socketName);
            socket.connect((MaleSocket) expression);
            expressionEntryList.add(new ExpressionEntry(socket, socketName));
        }
        
        if (expectedResult) {
            Assert.assertTrue("validateAntecedent() returns null for '"+antecedent+"'",
                    ix1.validateAntecedent(antecedent, expressionEntryList) == null);
        } else {
            Assert.assertTrue("validateAntecedent() returns error message for '"+antecedent+"'",
                    ix1.validateAntecedent(antecedent, expressionEntryList) != null);
        }
    }
    
    private void testCalculate(int expectedResult, String antecedent, List<DigitalExpressionBean> conditionalVariablesList, String errorMessage) throws SocketAlreadyConnectedException {
        
        AtomicBoolean isCompleted = new AtomicBoolean(true);
        Antecedent ix1 = new Antecedent("IXIC 1", null, antecedent);
        
//        for (int i=0; i < ix1.getChildCount(); i++) {
//            ix1.getChild(i).disconnect();
//        }
        
        ix1.setChildCount(conditionalVariablesList.size());
        
        for (int i=0; i < conditionalVariablesList.size(); i++) {
            ix1.getChild(i).connect((MaleSocket)conditionalVariablesList.get(i));
        }
        
        switch (expectedResult) {
            case Antecedent.FALSE:
                Assert.assertFalse("validateAntecedent() returns FALSE for '"+antecedent+"'",
                        ix1.evaluate(isCompleted));
                break;
                
            case Antecedent.TRUE:
//                System.err.format("antecedent: %s%n", antecedent);
//                System.err.format("variable: %b%n", conditionalVariablesList.get(0).evaluate(isCompleted));
                Assert.assertTrue("validateAntecedent() returns TRUE for '"+antecedent+"'",
                        ix1.evaluate(isCompleted));
                break;
                
            default:
                throw new RuntimeException(String.format("Unknown expected result: %d", expectedResult));
        }
        
        if (! errorMessage.isEmpty()) {
            jmri.util.JUnitAppender.assertErrorMessageStartsWith(errorMessage);
        }
    }
    
    @Test
    public void testValidate() throws SocketAlreadyConnectedException {
        DigitalExpressionBean[] conditionalVariables_Empty = { };
        List<DigitalExpressionBean> conditionalVariablesList_Empty = Arrays.asList(conditionalVariables_Empty);
        
        DigitalExpressionBean trueExpression = InstanceManager.getDefault(DigitalExpressionManager.class).registerExpression(new True(conditionalNG));
//        DigitalExpressionBean falseExpression = InstanceManager.getDefault(DigitalExpressionManager.class).registerExpression(new False(conditionalNG));
        
        DigitalExpressionBean[] conditionalVariables_True
                = { trueExpression };
        List<DigitalExpressionBean> conditionalVariablesList_True = Arrays.asList(conditionalVariables_True);
        
        DigitalExpressionBean[] conditionalVariables_TrueTrueTrue
                = { trueExpression
                        , trueExpression
                        , trueExpression };
        List<DigitalExpressionBean> conditionalVariablesList_TrueTrueTrue = Arrays.asList(conditionalVariables_TrueTrueTrue);
        
        // Test empty antecedent string
        testValidate(EXPECT_FAILURE, "", conditionalVariablesList_Empty);
        
        testValidate(EXPECT_SUCCESS, "R1", conditionalVariablesList_True);
        testValidate(EXPECT_FAILURE, "R2", conditionalVariablesList_True);
        
        // Test parentheses
        testValidate(EXPECT_SUCCESS, "([{R1)}]", conditionalVariablesList_True);
        testValidate(EXPECT_FAILURE, "(R2", conditionalVariablesList_True);
        testValidate(EXPECT_FAILURE, "R2)", conditionalVariablesList_True);
        
        // Test several items
        testValidate(EXPECT_FAILURE, "R1 and R2 and R3", conditionalVariablesList_True);
        testValidate(EXPECT_FAILURE, "R1", conditionalVariablesList_TrueTrueTrue);
        testValidate(EXPECT_SUCCESS, "R1 and R2 and R3", conditionalVariablesList_TrueTrueTrue);
        
        // Test uppercase and lowercase
        testValidate(EXPECT_SUCCESS, "R2 AND R1 or R3", conditionalVariablesList_TrueTrueTrue);
        
        // Test several items and parenthese
        testValidate(EXPECT_SUCCESS, "(R1 and R3) and not R2", conditionalVariablesList_TrueTrueTrue);
        testValidate(EXPECT_FAILURE, "(R1 and) R3 and not R2", conditionalVariablesList_TrueTrueTrue);
        testValidate(EXPECT_FAILURE, "R1( and R3) and not R2", conditionalVariablesList_TrueTrueTrue);
        testValidate(EXPECT_FAILURE, "R1 (and R3 and) not R2", conditionalVariablesList_TrueTrueTrue);
        testValidate(EXPECT_FAILURE, "(R1 and R3) and not R2)", conditionalVariablesList_TrueTrueTrue);
        testValidate(EXPECT_SUCCESS, "(R1 and (R3) and not R2)", conditionalVariablesList_TrueTrueTrue);
        
        // Test invalid combinations
        testValidate(EXPECT_FAILURE, "R1 and or R3 and R2", conditionalVariablesList_TrueTrueTrue);
        testValidate(EXPECT_FAILURE, "R1 or or R3 and R2", conditionalVariablesList_TrueTrueTrue);
        testValidate(EXPECT_FAILURE, "R1 or and R3 and R2", conditionalVariablesList_TrueTrueTrue);
        testValidate(EXPECT_FAILURE, "R1 not R3 and R2", conditionalVariablesList_TrueTrueTrue);
        testValidate(EXPECT_FAILURE, "and R1 not R3 and R2", conditionalVariablesList_TrueTrueTrue);
        testValidate(EXPECT_FAILURE, "R1 or R3 and R2 or", conditionalVariablesList_TrueTrueTrue);
    }
    
    @Test
    @SuppressWarnings("unused") // test building in progress
    public void testCalculate() throws SocketAlreadyConnectedException {
        DigitalExpressionBean[] conditionalVariables_Empty = { };
        List<DigitalExpressionBean> conditionalVariablesList_Empty = Arrays.asList(conditionalVariables_Empty);
        
        DigitalExpressionBean trueExpression = InstanceManager.getDefault(DigitalExpressionManager.class).registerExpression(new True(conditionalNG));
        DigitalExpressionBean falseExpression = InstanceManager.getDefault(DigitalExpressionManager.class).registerExpression(new False(conditionalNG));
        
        DigitalExpressionBean[] conditionalVariables_True
                = { trueExpression };
        List<DigitalExpressionBean> conditionalVariablesList_True = Arrays.asList(conditionalVariables_True);
        
        DigitalExpressionBean[] conditionalVariables_False
                = { falseExpression };
        List<DigitalExpressionBean> conditionalVariablesList_False = Arrays.asList(conditionalVariables_False);
        
        DigitalExpressionBean[] conditionalVariables_TrueTrueTrue
                = { trueExpression
                        , trueExpression
                        , trueExpression };
        List<DigitalExpressionBean> conditionalVariablesList_TrueTrueTrue = Arrays.asList(conditionalVariables_TrueTrueTrue);
        
        // Test with two digit variable numbers
        DigitalExpressionBean[] conditionalVariables_TrueTrueFalseTrueTrueFalseTrueTrueFalseTrueTrueFalse
                = {trueExpression
                        , trueExpression
                        , falseExpression
                        , trueExpression
                        , trueExpression
                        , falseExpression
                        , trueExpression
                        , trueExpression
                        , falseExpression
                        , trueExpression
                        , trueExpression
                        , falseExpression };
        List<DigitalExpressionBean> conditionalVariablesList_TrueTrueFalseTrueTrueFalseTrueTrueFalseTrueTrueFalse =
                Arrays.asList(conditionalVariables_TrueTrueFalseTrueTrueFalseTrueTrueFalseTrueTrueFalse);
        
        
        // Test empty antecedent string
        testCalculate(Antecedent.FALSE, "", conditionalVariablesList_Empty, "");
//        testCalculate(Antecedent.FALSE, "", conditionalVariablesList_True,
//                "IXIC 1 parseCalculation error antecedent= , ex= java.lang.StringIndexOutOfBoundsException");
        testCalculate(Antecedent.FALSE, "", conditionalVariablesList_True, "");
        
        // Test illegal number
        testCalculate(Antecedent.FALSE, "R#", conditionalVariablesList_True,
                "IXIC 1 parseCalculation error antecedent= R#, ex= java.lang.NumberFormatException");
        testCalculate(Antecedent.FALSE, "R-", conditionalVariablesList_True,
                "IXIC 1 parseCalculation error antecedent= R-, ex= java.lang.NumberFormatException");
        testCalculate(Antecedent.FALSE, "Ra", conditionalVariablesList_True,
                "IXIC 1 parseCalculation error antecedent= Ra, ex= java.lang.NumberFormatException");
        
        // Test single condition
        testCalculate(Antecedent.TRUE, "R1", conditionalVariablesList_True, "");
        testCalculate(Antecedent.FALSE, "R1", conditionalVariablesList_False, "");
        testCalculate(Antecedent.FALSE, "not R1", conditionalVariablesList_True, "");
        testCalculate(Antecedent.TRUE, "not R1", conditionalVariablesList_False, "");
        
        // Test single item but wrong item (R2 instead of R1)
//        testCalculate(Antecedent.FALSE, "R2)", conditionalVariablesList_True,
//                "IXIC 1 parseCalculation error antecedent= R2), ex= java.lang.ArrayIndexOutOfBoundsException");
        
        // Test two digit variable numbers
        testCalculate(Antecedent.TRUE, "R3 and R12 or R5 and R10",
                conditionalVariablesList_TrueTrueFalseTrueTrueFalseTrueTrueFalseTrueTrueFalse, "");
        testCalculate(Antecedent.FALSE, "R3 and (R12 or R5) and R10",
                conditionalVariablesList_TrueTrueFalseTrueTrueFalseTrueTrueFalseTrueTrueFalse, "");
        testCalculate(Antecedent.FALSE, "R12 and R10",
                conditionalVariablesList_TrueTrueFalseTrueTrueFalseTrueTrueFalseTrueTrueFalse, "");
        testCalculate(Antecedent.TRUE, "R12 or R10",
                conditionalVariablesList_TrueTrueFalseTrueTrueFalseTrueTrueFalseTrueTrueFalse, "");
        testCalculate(Antecedent.FALSE, "not (R12 or R10)",
                conditionalVariablesList_TrueTrueFalseTrueTrueFalseTrueTrueFalseTrueTrueFalse, "");
        
        // Test parentheses
        testCalculate(Antecedent.TRUE, "([{R1)}]", conditionalVariablesList_True, "");
//        testCalculate(Antecedent.FALSE, "(R2", conditionalVariablesList_True,
//                "IXIC 1 parseCalculation error antecedent= (R2, ex= java.lang.ArrayIndexOutOfBoundsException");
        
        // Test several items
        testCalculate(Antecedent.FALSE, "R1 and R2 and R3", conditionalVariablesList_True,
                "IXIC 1 parseCalculation error antecedent= R1 and R2 and R3, ex= java.lang.IndexOutOfBoundsException");
        testCalculate(Antecedent.TRUE, "R1", conditionalVariablesList_TrueTrueTrue, "");
        testCalculate(Antecedent.TRUE, "R2", conditionalVariablesList_TrueTrueTrue, "");
        testCalculate(Antecedent.TRUE, "R3", conditionalVariablesList_TrueTrueTrue, "");
        testCalculate(Antecedent.TRUE, "R1 and R2 and R3", conditionalVariablesList_TrueTrueTrue, "");
        testCalculate(Antecedent.TRUE, "R2 AND R1 or R3", conditionalVariablesList_TrueTrueTrue, "");
        
        // Test invalid combinations of and, or, not
        testCalculate(Antecedent.FALSE, "R1 and or R3 and R2", conditionalVariablesList_TrueTrueTrue,
                "IXIC 1 parseCalculation error antecedent= R1 and or R3 and R2, ex= jmri.JmriException: Unexpected operator or characters < ORR3ANDR2 >");
        testCalculate(Antecedent.FALSE, "R1 or or R3 and R2", conditionalVariablesList_TrueTrueTrue,
                "IXIC 1 parseCalculation error antecedent= R1 or or R3 and R2, ex= jmri.JmriException: Unexpected operator or characters < ORR3ANDR2 >");
        testCalculate(Antecedent.FALSE, "R1 or and R3 and R2", conditionalVariablesList_TrueTrueTrue,
                "IXIC 1 parseCalculation error antecedent= R1 or and R3 and R2, ex= jmri.JmriException: Unexpected operator or characters < ANDR3ANDR2 >");
        testCalculate(Antecedent.FALSE, "R1 not R3 and R2", conditionalVariablesList_TrueTrueTrue,
                "IXIC 1 parseCalculation error antecedent= R1 not R3 and R2, ex= jmri.JmriException: Could not find expected operator < NOTR3ANDR2 >");
        testCalculate(Antecedent.FALSE, "and R1 not R3 and R2", conditionalVariablesList_TrueTrueTrue,
                "IXIC 1 parseCalculation error antecedent= and R1 not R3 and R2, ex= jmri.JmriException: Unexpected operator or characters < ANDR1NOTR3ANDR2 >");
        testCalculate(Antecedent.FALSE, "R1 or R3 and R2 or", conditionalVariablesList_TrueTrueTrue,
                "IXIC 1 parseCalculation error antecedent= R1 or R3 and R2 or, ex= java.lang.StringIndexOutOfBoundsException");
        
        // Test several items and parenthese
        testCalculate(Antecedent.TRUE, "(R1 and R3) and R2", conditionalVariablesList_TrueTrueTrue, "");
        testCalculate(Antecedent.FALSE, "(R1 and R3) and not R2", conditionalVariablesList_TrueTrueTrue, "");
        testCalculate(Antecedent.FALSE, "(R1 and) R3 and not R2", conditionalVariablesList_TrueTrueTrue,
                "IXIC 1 parseCalculation error antecedent= (R1 and) R3 and not R2, ex= jmri.JmriException: Unexpected operator or characters < )R3ANDNOTR2 >");
        testCalculate(Antecedent.FALSE, "R1( and R3) and not R2", conditionalVariablesList_TrueTrueTrue,
                "IXIC 1 parseCalculation error antecedent= R1( and R3) and not R2, ex= jmri.JmriException: Could not find expected operator < (ANDR3)ANDNOTR2 >");
        testCalculate(Antecedent.FALSE, "R1 (and R3 and) not R2", conditionalVariablesList_TrueTrueTrue,
                "IXIC 1 parseCalculation error antecedent= R1 (and R3 and) not R2, ex= jmri.JmriException: Could not find expected operator < (ANDR3AND)NOTR2 >");
        testCalculate(Antecedent.FALSE, "(R1 and R3) and not R2)", conditionalVariablesList_TrueTrueTrue, "");
        testCalculate(Antecedent.TRUE, "(R1 and (R3) and R2)", conditionalVariablesList_TrueTrueTrue, "");
        testCalculate(Antecedent.FALSE, "(R1 and (R3) and not R2)", conditionalVariablesList_TrueTrueTrue, "");
    }
    
    
    // The minimal setup for log4J
    @Before
    public void setUp() {
        JUnitUtil.setUp();
        JUnitUtil.resetInstanceManager();
        JUnitUtil.initInternalSensorManager();
        JUnitUtil.initInternalTurnoutManager();
        
        logixNG = InstanceManager.getDefault(LogixNG_Manager.class).createLogixNG("A LogixNG");
        conditionalNG = new DefaultConditionalNG(logixNG.getSystemName()+":1");
        logixNG.addConditionalNG(conditionalNG);
    }

    @After
    public void tearDown() {
        JUnitUtil.tearDown();
    }

    @Override
    public void connected(FemaleSocket socket) {
        // Do nothing
    }

    @Override
    public void disconnected(FemaleSocket socket) {
        // Do nothing
    }
    
}
