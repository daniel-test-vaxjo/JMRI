package jmri.jmrit.logixng.util.parser.expressionnode;

import java.util.concurrent.atomic.AtomicBoolean;
import jmri.jmrit.logixng.util.parser.ParserException;
import jmri.jmrit.logixng.util.parser.Token;
import jmri.jmrit.logixng.util.parser.TokenType;
import jmri.util.JUnitUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test ParsedExpression
 * 
 * @author Daniel Bergqvist 2019
 */
public class ExpressionNodeBooleanOperatorTest {

    @Test
    public void testCtor() throws ParserException {
        
        ExpressionNode exprTrue = new ExpressionNodeTrue();
        ExpressionNode exprFalse = new ExpressionNodeFalse();
        
        Token token = new Token(TokenType.NONE, "1", 0);
        ExpressionNodeFloatingNumber expressionNumber = new ExpressionNodeFloatingNumber(token);
        ExpressionNodeBooleanOperator t = new ExpressionNodeBooleanOperator(TokenType.BOOLEAN_NOT, null, expressionNumber);
        Assert.assertNotNull("exists", t);
        
        AtomicBoolean hasThrown = new AtomicBoolean(false);
        
        // Test right side is null
        try {
            new ExpressionNodeBooleanOperator(TokenType.BOOLEAN_NOT, null, null);
        } catch (IllegalArgumentException e) {
            hasThrown.set(true);
        }
        Assert.assertTrue("exception is thrown", hasThrown.get());
        
        // Test invalid token
        try {
            new ExpressionNodeBooleanOperator(TokenType.BINARY_AND, null, null);
        } catch (IllegalArgumentException e) {
            hasThrown.set(true);
        }
        Assert.assertTrue("exception is thrown", hasThrown.get());
        
        // AND requires two operands
        try {
            new ExpressionNodeBooleanOperator(TokenType.BOOLEAN_AND, null, exprTrue);
        } catch (IllegalArgumentException e) {
            hasThrown.set(true);
        }
        Assert.assertTrue("exception is thrown", hasThrown.get());
        
        // OR requires two operands
        hasThrown.set(false);
        try {
            new ExpressionNodeBooleanOperator(TokenType.BOOLEAN_OR, null, exprTrue);
        } catch (IllegalArgumentException e) {
            hasThrown.set(true);
        }
        Assert.assertTrue("exception is thrown", hasThrown.get());
        
        // NOT requires only one operands
        hasThrown.set(false);
        try {
            new ExpressionNodeBooleanOperator(TokenType.BOOLEAN_NOT, exprFalse, exprTrue);
        } catch (IllegalArgumentException e) {
            hasThrown.set(true);
        }
        Assert.assertTrue("exception is thrown", hasThrown.get());
    }
    
    @Test
    public void testCalculate() throws ParserException {
        
        ExpressionNode exprTrue1 = new ExpressionNodeTrue();
        ExpressionNode exprTrue2 = new ExpressionNodeTrue();
        ExpressionNode exprFalse1 = new ExpressionNodeFalse();
        ExpressionNode exprFalse2 = new ExpressionNodeFalse();
        
        Assert.assertFalse("calculate() gives the correct value",
                (Boolean)new ExpressionNodeBooleanOperator(TokenType.BOOLEAN_NOT, null, exprTrue1).calculate());
        Assert.assertTrue("calculate() gives the correct value",
                (Boolean)new ExpressionNodeBooleanOperator(TokenType.BOOLEAN_NOT, null, exprFalse1).calculate());
        
        Assert.assertTrue("calculate() gives the correct value",
                (Boolean)new ExpressionNodeBooleanOperator(TokenType.BOOLEAN_AND, exprTrue1, exprTrue2).calculate());
        Assert.assertFalse("calculate() gives the correct value",
                (Boolean)new ExpressionNodeBooleanOperator(TokenType.BOOLEAN_AND, exprTrue1, exprFalse1).calculate());
        Assert.assertFalse("calculate() gives the correct value",
                (Boolean)new ExpressionNodeBooleanOperator(TokenType.BOOLEAN_AND, exprFalse1, exprFalse2).calculate());
        Assert.assertFalse("calculate() gives the correct value",
                (Boolean)new ExpressionNodeBooleanOperator(TokenType.BOOLEAN_AND, exprFalse1, exprTrue1).calculate());
        
        Assert.assertTrue("calculate() gives the correct value",
                (Boolean)new ExpressionNodeBooleanOperator(TokenType.BOOLEAN_OR, exprTrue1, exprTrue2).calculate());
        Assert.assertTrue("calculate() gives the correct value",
                (Boolean)new ExpressionNodeBooleanOperator(TokenType.BOOLEAN_OR, exprTrue1, exprFalse1).calculate());
        Assert.assertFalse("calculate() gives the correct value",
                (Boolean)new ExpressionNodeBooleanOperator(TokenType.BOOLEAN_OR, exprFalse1, exprFalse1).calculate());
        Assert.assertTrue("calculate() gives the correct value",
                (Boolean)new ExpressionNodeBooleanOperator(TokenType.BOOLEAN_OR, exprFalse1, exprTrue1).calculate());
    }
    
    @Test
    public void testGetDefinitionString() throws ParserException {
        
        ExpressionNode exprTrue1 = new ExpressionNodeTrue();
        ExpressionNode exprTrue2 = new ExpressionNodeTrue();
        ExpressionNode exprFalse1 = new ExpressionNodeFalse();
        ExpressionNode exprFalse2 = new ExpressionNodeFalse();
        
        Assert.assertEquals("getDefinitionString() gives the correct value",
                "!(true)",
                new ExpressionNodeBooleanOperator(TokenType.BOOLEAN_NOT, null, exprTrue1)
                        .getDefinitionString());
        Assert.assertEquals("calculate gives the correct value",
                "!(false)",
                new ExpressionNodeBooleanOperator(TokenType.BOOLEAN_NOT, null, exprFalse1)
                        .getDefinitionString());
        
        Assert.assertEquals("getDefinitionString() gives the correct value",
                "(true)&&(true)",
                new ExpressionNodeBooleanOperator(TokenType.BOOLEAN_AND, exprTrue1, exprTrue2)
                        .getDefinitionString());
        Assert.assertEquals("getDefinitionString() gives the correct value",
                "(true)&&(false)",
                new ExpressionNodeBooleanOperator(TokenType.BOOLEAN_AND, exprTrue1, exprFalse1)
                        .getDefinitionString());
        Assert.assertEquals("getDefinitionString() gives the correct value",
                "(false)&&(false)",
                new ExpressionNodeBooleanOperator(TokenType.BOOLEAN_AND, exprFalse1, exprFalse2)
                        .getDefinitionString());
        Assert.assertEquals("getDefinitionString() gives the correct value",
                "(false)&&(true)",
                new ExpressionNodeBooleanOperator(TokenType.BOOLEAN_AND, exprFalse1, exprTrue1)
                        .getDefinitionString());
        
        Assert.assertEquals("getDefinitionString() gives the correct value",
                "(true)||(true)",
                new ExpressionNodeBooleanOperator(TokenType.BOOLEAN_OR, exprTrue1, exprTrue2)
                        .getDefinitionString());
        Assert.assertEquals("getDefinitionString() gives the correct value",
                "(true)||(false)",
                new ExpressionNodeBooleanOperator(TokenType.BOOLEAN_OR, exprTrue1, exprFalse1)
                        .getDefinitionString());
        Assert.assertEquals("getDefinitionString() gives the correct value",
                "(false)||(false)",
                new ExpressionNodeBooleanOperator(TokenType.BOOLEAN_OR, exprFalse1, exprFalse2)
                        .getDefinitionString());
        Assert.assertEquals("getDefinitionString() gives the correct value",
                "(false)||(true)",
                new ExpressionNodeBooleanOperator(TokenType.BOOLEAN_OR, exprFalse1, exprTrue1)
                        .getDefinitionString());
    }
    
    // The minimal setup for log4J
    @Before
    public void setUp() {
        JUnitUtil.setUp();
        JUnitUtil.resetInstanceManager();
        JUnitUtil.initInternalSensorManager();
        JUnitUtil.initInternalTurnoutManager();
    }

    @After
    public void tearDown() {
        JUnitUtil.tearDown();
    }
    
}
