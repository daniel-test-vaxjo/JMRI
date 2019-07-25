package jmri.jmrit.logixng.util.parser.functions;

import java.util.ArrayList;
import java.util.Collections;
import jmri.jmrit.logixng.util.parser.expressionnode.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import jmri.jmrit.logixng.util.parser.CalculateException;
import jmri.jmrit.logixng.util.parser.ParserException;
import jmri.jmrit.logixng.util.parser.Token;
import jmri.jmrit.logixng.util.parser.TokenType;
import jmri.jmrit.logixng.util.parser.Variable;
import jmri.jmrit.logixng.util.parser.WrongNumberOfParametersException;
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
public class MathFunctionsTest {

    ExpressionNode expr_boolean_true = new ExpressionNodeTrue();
    ExpressionNode expr_str_HELLO = new ExpressionNodeString(new Token(TokenType.NONE, "hello", 0));
    ExpressionNode expr_str_RAD = new ExpressionNodeString(new Token(TokenType.NONE, "rad", 0));
    ExpressionNode expr_str_DEG = new ExpressionNodeString(new Token(TokenType.NONE, "deg", 0));
    ExpressionNode expr_str_0_34 = new ExpressionNodeString(new Token(TokenType.NONE, "0.34", 0));
    ExpressionNode expr0_34 = new ExpressionNodeFloatingNumber(new Token(TokenType.NONE, "0.34", 0));
    ExpressionNode expr0_95 = new ExpressionNodeFloatingNumber(new Token(TokenType.NONE, "0.95", 0));
    ExpressionNode expr12_34 = new ExpressionNodeFloatingNumber(new Token(TokenType.NONE, "12.34", 0));
    ExpressionNode expr25_46 = new ExpressionNodeFloatingNumber(new Token(TokenType.NONE, "25.46", 0));
    ExpressionNode expr12 = new ExpressionNodeFloatingNumber(new Token(TokenType.NONE, "12", 0));
    ExpressionNode expr23 = new ExpressionNodeFloatingNumber(new Token(TokenType.NONE, "23", 0));
    
    
    private List<ExpressionNode> getParameterList(ExpressionNode... exprNodes) {
        List<ExpressionNode> list = new ArrayList<>();
        Collections.addAll(list, exprNodes);
        return list;
    }
    
    @Test
    public void testIntFunction() throws ParserException {
        MathFunctions.IntFunction intFunction = new MathFunctions.IntFunction();
        Assert.assertEquals("strings matches", "int", intFunction.getName());
        Assert.assertEquals("strings matches", "int", new MathFunctions.IntFunction().getName());
        
        AtomicBoolean hasThrown = new AtomicBoolean(false);
        
        // Test unsupported token type
        hasThrown.set(false);
        try {
            intFunction.calculate(getParameterList());
        } catch (WrongNumberOfParametersException e) {
            hasThrown.set(true);
        }
        Assert.assertTrue("exception is thrown", hasThrown.get());
        
        Assert.assertEquals("numbers are equal", (Integer)12, intFunction.calculate(getParameterList(expr12_34)));
        
        // Test unsupported token type
        hasThrown.set(false);
        try {
            intFunction.calculate(getParameterList(expr12_34, expr25_46));
        } catch (WrongNumberOfParametersException e) {
            hasThrown.set(true);
        }
    }
    
    @Test
    public void testLongFunction() throws ParserException {
        MathFunctions.LongFunction longFunction = new MathFunctions.LongFunction();
        Assert.assertEquals("strings matches", "long", longFunction.getName());
        Assert.assertEquals("strings matches", "long", new MathFunctions.LongFunction().getName());
        
        AtomicBoolean hasThrown = new AtomicBoolean(false);
        
        // Test unsupported token type
        hasThrown.set(false);
        try {
            longFunction.calculate(getParameterList());
        } catch (WrongNumberOfParametersException e) {
            hasThrown.set(true);
        }
        Assert.assertTrue("exception is thrown", hasThrown.get());
        
        Assert.assertEquals("numbers are equal", (Long)12l, longFunction.calculate(getParameterList(expr12_34)));
        
        // Test unsupported token type
        hasThrown.set(false);
        try {
            longFunction.calculate(getParameterList(expr12_34, expr25_46));
        } catch (WrongNumberOfParametersException e) {
            hasThrown.set(true);
        }
    }
    
    @Test
    public void testSinFunction() throws ParserException {
        MathFunctions.SinFunction sinFunction = new MathFunctions.SinFunction();
        Assert.assertEquals("strings matches", "sin", sinFunction.getName());
        
        // Test sin(x)
        Assert.assertEquals("numbers are equal", (Double)0.3334870921408144, (Double)sinFunction.calculate(getParameterList(expr0_34)), 0.0000001d);
        Assert.assertEquals("numbers are equal", (Double)0.8134155047893737, (Double)sinFunction.calculate(getParameterList(expr0_95)), 0.0000001d);
        Assert.assertEquals("numbers are equal", (Double)(-0.22444221895185537), (Double)sinFunction.calculate(getParameterList(expr12_34)), 0.0000001d);
        
        // Test sin(x) with a string as parameter
        Assert.assertEquals("numbers are equal", (Double)0.3334870921408144, (Double)sinFunction.calculate(getParameterList(expr0_34)), 0.0000001d);
        
        AtomicBoolean hasThrown = new AtomicBoolean(false);
        
        // Test sin(x,"rad"), sin(x,"deg"), sin(x,"hello"), sin(x, true)
        Assert.assertEquals("numbers are equal", (Double)0.3334870921408144, (Double)sinFunction.calculate(getParameterList(expr0_34, expr_str_RAD)), 0.0000001d);
        Assert.assertEquals("numbers are equal", (Double)0.21371244079399437, (Double)sinFunction.calculate(getParameterList(expr12_34, expr_str_DEG)), 0.0000001d);
        hasThrown.set(false);
        try {
            sinFunction.calculate(getParameterList(expr12_34, expr_str_HELLO));
        } catch (CalculateException e) {
            hasThrown.set(true);
        }
        Assert.assertTrue("exception is thrown", hasThrown.get());
        hasThrown.set(false);
        try {
            sinFunction.calculate(getParameterList(expr12_34, expr_boolean_true));
        } catch (CalculateException e) {
            hasThrown.set(true);
        }
        Assert.assertTrue("exception is thrown", hasThrown.get());
        
        // Test sin(x,"deg", 12, 23)
        Assert.assertEquals("numbers are equal", (Double)14.350836848733938, (Double)sinFunction.calculate(getParameterList(expr12_34, expr_str_DEG, expr12, expr23)), 0.0000001d);
        
        // Test sin()
        hasThrown.set(false);
        try {
            sinFunction.calculate(getParameterList());
        } catch (WrongNumberOfParametersException e) {
            hasThrown.set(true);
        }
        Assert.assertTrue("exception is thrown", hasThrown.get());
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
