package jmri.jmrit.logixng;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicBoolean;
import jmri.NamedBean;
import jmri.managers.AbstractManager;
import jmri.util.junit.annotations.ToDo;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for testing FemaleStringExpressionSocket classes
 * 
 * @author Daniel Bergqvist 2018
 */
public abstract class FemaleSocketTestBase {

    protected AtomicBoolean flag;
    protected AtomicBoolean errorFlag;
    protected MaleSocket maleSocket;
    protected MaleSocket otherMaleSocket;
    protected FemaleSocket femaleSocket;
    
    
    private SortedSet<String> getClassNames(List<Class<? extends Base>> classes) {
        SortedSet<String> set = new TreeSet<>();
        
        for (Class<? extends Base> clazz : classes) {
            set.add(clazz.getName());
        }
        
        return set;
    }
    
    private boolean isSetsEqual(SortedSet<String> set1, SortedSet<String> set2) {
        if (set1.size() != set2.size()) {
            return false;
        }
        for (String s1 : set1) {
            if (!set2.contains(s1)) {
                return false;
            }
        }
        for (String s2 : set2) {
            if (!set1.contains(s2)) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Asserts that the two maps of connection classes holds the same classes.
     * The reason to why this method returns a boolean is because every test
     * method is expected to have an assertion. And the statistical analysis is
     * not able to see that that assertion is here, so we therefore also need
     * an assertion in the caller method.
     * @param expectedMap the expected result
     * @param actualMap the actual result
     * @return true if assertion is correct
     */
    public final boolean isConnectionClassesEquals(
            Map<Category, List<Class<? extends Base>>> expectedMap,
            Map<Category, List<Class<? extends Base>>> actualMap) {
        
        List<Class<? extends Base>> classes;
/*        
        for (Category category : Category.values()) {
            classes = femaleSocket.getConnectableClasses().get(category);
            for (Class<? extends Base> clazz : classes) {
                System.out.format("FemaleSocket: %s, category: %s, class: %s%n",
                        femaleSocket.getClass().getName(),
                        category.name(),
                        clazz.getName());
            }
        }
*/        
        for (Category category : Category.values()) {
            
            if (!isSetsEqual(
                    getClassNames(expectedMap.get(category)),
                    getClassNames(actualMap.get(category)))) {
                
                classes = femaleSocket.getConnectableClasses().get(category);
                for (Class<? extends Base> clazz : classes) {
                    System.err.format("Set of classes are different:%n");
                    System.err.format("FemaleSocket: %s, category: %s, class: %s%n",
                            femaleSocket.getClass().getName(),
                            category.name(),
                            clazz.getName());
/*
                    log.error("Set of classes are different:");
                    log.error("FemaleSocket: {}, category: {}, class: {}",
                            femaleSocket.getClass().getName(),
                            category.name(),
                            clazz.getName());
*/
                }
                
                return false;
            }
        }
        
        // We will not get here if assertion fails.
        return true;
    }
    
    abstract protected boolean hasSocketBeenSetup();
    
    @Test
    public void testSetup() throws SocketAlreadyConnectedException {
        Assert.assertFalse("not connected", femaleSocket.isConnected());

        // Check that we can call setup() even if the socket is not connected.
        femaleSocket.setup();
        
        femaleSocket.connect(maleSocket);
        Assert.assertTrue("is connected", femaleSocket.isConnected());
        Assert.assertFalse("not setup", hasSocketBeenSetup());
        femaleSocket.setup();
        Assert.assertTrue("is setup", hasSocketBeenSetup());
    }
    
    @Test
    public void testConnectIncompatibleSocket() {
        MaleSocket incompatibleSocket = new IncompatibleMaleSocket();
        Assert.assertFalse("socket not compatible", femaleSocket.isCompatible(incompatibleSocket));
        
        // Test connect incompatible male socket
        errorFlag.set(false);
        try {
            femaleSocket.connect(incompatibleSocket);
        } catch (UnsupportedOperationException ex) {
            errorFlag.set(true);
        } catch (SocketAlreadyConnectedException ex) {
            // We shouldn't be here.
            Assert.fail("socket is already connected");
        }
        
        Assert.assertTrue("socket is not compatible", errorFlag.get());
        
        // Test connect null
        errorFlag.set(false);
        try {
            femaleSocket.connect(null);
        } catch (NullPointerException ex) {
            errorFlag.set(true);
        } catch (SocketAlreadyConnectedException ex) {
            // We shouldn't be here.
            Assert.fail("socket is already connected");
        }
        
        Assert.assertTrue("cannot connect socket that is null", errorFlag.get());
    }
    
    @Test
    public void testConnect() {
        
        // Test connect male socket
        flag.set(false);
        errorFlag.set(false);
        try {
            femaleSocket.connect(maleSocket);
        } catch (SocketAlreadyConnectedException ex) {
            errorFlag.set(true);
        }
        
        Assert.assertTrue("Socket is connected", flag.get());
        Assert.assertFalse("No error", errorFlag.get());
        
        // Test connect male socket when already connected
        flag.set(false);
        errorFlag.set(false);
        try {
            femaleSocket.connect(otherMaleSocket);
        } catch (SocketAlreadyConnectedException ex) {
            errorFlag.set(true);
        }
        
        Assert.assertFalse("Socket was not connected again", flag.get());
        Assert.assertTrue("Socket already connected error", errorFlag.get());
    }
    
    @Test
    public void testDisconnect() throws SocketAlreadyConnectedException {
        
        // Ensure the socket is connected before this test.
        if (!femaleSocket.isConnected()) {
            femaleSocket.connect(maleSocket);
        }
        
        // Test disconnect male socket
        flag.set(false);
        femaleSocket.disconnect();
        
        Assert.assertTrue("Socket is disconnected", flag.get());
        
        // Test connect male socket
        flag.set(false);
        errorFlag.set(false);
        femaleSocket.disconnect();
        
        Assert.assertFalse("Socket is not disconnected again", flag.get());
    }
    
    @Test
    public void testSetParentForAllChildren() throws SocketAlreadyConnectedException {
        Assert.assertFalse("femaleSocket is not connected", femaleSocket.isConnected());
        femaleSocket.setParentForAllChildren();
        Assert.assertNull("malesocket.getParent() is null", maleSocket.getParent());
        femaleSocket.connect(maleSocket);
        femaleSocket.setParentForAllChildren();
        Assert.assertEquals("malesocket.getParent() is femaleSocket", femaleSocket, maleSocket.getParent());
    }
    
    private boolean setName_verifyException(String newName, String expectedExceptionMessage) {
        AtomicBoolean hasThrown = new AtomicBoolean(false);
        try {
            femaleSocket.setName(newName);
        } catch (IllegalArgumentException ex) {
            hasThrown.set(true);
            Assert.assertTrue("Error message is correct", ex.getMessage().equals(expectedExceptionMessage));
        }
        return hasThrown.get();
    }
    
    @Test
    public void testSetName() {
        // Both letters and digits is OK
        femaleSocket.setName("X12");
        Assert.assertTrue("name matches", "X12".equals(femaleSocket.getName()));
        
        // Only letters is OK
        femaleSocket.setName("Xyz");
        Assert.assertTrue("name matches", "Xyz".equals(femaleSocket.getName()));
        
        // Both letters and digits in random order is OK as long as the first
        // character is a letter
        femaleSocket.setName("X1b2c3Y");
        Assert.assertTrue("name matches", "X1b2c3Y".equals(femaleSocket.getName()));
        
        // The name must start with a letter, not a digit
        Assert.assertTrue("exception is thrown", setName_verifyException("123", "the name is not valid: 123"));
        
        // The name must not contain any spaces
        Assert.assertTrue("exception is thrown", setName_verifyException(" A123", "the name is not valid:  A123"));
        Assert.assertTrue("exception is thrown", setName_verifyException("A1 23", "the name is not valid: A1 23"));
        Assert.assertTrue("exception is thrown", setName_verifyException("A123 ", "the name is not valid: A123 "));
        
        // The name must not contain any character that's not a letter nor a digit
        Assert.assertTrue("exception is thrown", setName_verifyException("A12!3", "the name is not valid: A12!3"));
        Assert.assertTrue("exception is thrown", setName_verifyException("A+123", "the name is not valid: A+123"));
        Assert.assertTrue("exception is thrown", setName_verifyException("=A123", "the name is not valid: =A123"));
        Assert.assertTrue("exception is thrown", setName_verifyException("A12*3", "the name is not valid: A12*3"));
        Assert.assertTrue("exception is thrown", setName_verifyException("A123/", "the name is not valid: A123/"));
        Assert.assertTrue("exception is thrown", setName_verifyException("A12(3", "the name is not valid: A12(3"));
        Assert.assertTrue("exception is thrown", setName_verifyException("A12)3", "the name is not valid: A12)3"));
        Assert.assertTrue("exception is thrown", setName_verifyException("A12[3", "the name is not valid: A12[3"));
        Assert.assertTrue("exception is thrown", setName_verifyException("A12]3", "the name is not valid: A12]3"));
        Assert.assertTrue("exception is thrown", setName_verifyException("A12{3", "the name is not valid: A12{3"));
        Assert.assertTrue("exception is thrown", setName_verifyException("A12}3", "the name is not valid: A12}3"));
    }
/*    
    @Test
    public void testLock() {
        femaleSocket.setLock(Base.Lock.NONE);
        Assert.assertEquals("lock matches", Base.Lock.NONE, femaleSocket.getLock());
        femaleSocket.setLock(Base.Lock.USER_LOCK);
        Assert.assertEquals("lock matches", Base.Lock.USER_LOCK, femaleSocket.getLock());
        femaleSocket.setLock(Base.Lock.HARD_LOCK);
        Assert.assertEquals("lock matches", Base.Lock.HARD_LOCK, femaleSocket.getLock());
    }
*/    
    @Test
    public void testDisposeWithoutChild() {
        femaleSocket.dispose();
        Assert.assertFalse("socket not connected", femaleSocket.isConnected());
    }
    
    @Test
    public void testDisposeWithChild() throws SocketAlreadyConnectedException {
        Assert.assertFalse("socket not connected", femaleSocket.isConnected());
        femaleSocket.connect(maleSocket);
        Assert.assertTrue("socket is connected", femaleSocket.isConnected());
        femaleSocket.dispose();
        Assert.assertFalse("socket not connected", femaleSocket.isConnected());
    }
    
    @Test
    public void testMethodsThatAreNotSupported() {
        errorFlag.set(false);
        try {
            femaleSocket.getCategory();
        } catch (UnsupportedOperationException ex) {
            errorFlag.set(true);
        }
        Assert.assertTrue("method not supported", errorFlag.get());
        
        errorFlag.set(false);
        try {
            femaleSocket.isExternal();
        } catch (UnsupportedOperationException ex) {
            errorFlag.set(true);
        }
        Assert.assertTrue("method not supported", errorFlag.get());
        
        errorFlag.set(false);
        try {
            femaleSocket.getChild(0);
        } catch (UnsupportedOperationException ex) {
            errorFlag.set(true);
        }
        Assert.assertTrue("method not supported", errorFlag.get());
        
        errorFlag.set(false);
        try {
            femaleSocket.getChildCount();
        } catch (UnsupportedOperationException ex) {
            errorFlag.set(true);
        }
        Assert.assertTrue("method not supported", errorFlag.get());
        
        errorFlag.set(false);
        try {
            femaleSocket.getUserName();
        } catch (UnsupportedOperationException ex) {
            errorFlag.set(true);
        }
        Assert.assertTrue("method not supported", errorFlag.get());
        
        errorFlag.set(false);
        try {
            femaleSocket.setUserName("aaa");
        } catch (UnsupportedOperationException ex) {
            errorFlag.set(true);
        }
        Assert.assertTrue("method not supported", errorFlag.get());
        
        errorFlag.set(false);
        try {
            femaleSocket.getSystemName();
        } catch (UnsupportedOperationException ex) {
            errorFlag.set(true);
        }
        Assert.assertTrue("method not supported", errorFlag.get());
        
    }
    
    
    
    private class IncompatibleMaleSocket implements MaleSocket {

        @Override
        public void setEnabled(boolean enable) {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public boolean isEnabled() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public Base getObject() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public void setDebugConfig(DebugConfig config) {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public DebugConfig getDebugConfig() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public DebugConfig createDebugConfig() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public String getSystemName() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public String getUserName() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public void setUserName(String s) throws NamedBean.BadUserNameException {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public String getShortDescription() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public String getLongDescription() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public Base getNewObjectBasedOnTemplate(String sys) {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public ConditionalNG getConditionalNG() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public LogixNG getLogixNG() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public Base getParent() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public void setParent(Base parent) {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public void setParentForAllChildren() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public FemaleSocket getChild(int index) throws IllegalArgumentException, UnsupportedOperationException {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public int getChildCount() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public Category getCategory() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public boolean isExternal() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public Lock getLock() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public void setLock(Lock lock) {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public void dispose() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public void registerListeners() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public void unregisterListeners() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public void printTree(PrintWriter writer, String indent) {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public void printTree(PrintWriter writer, String indent, String currentIndent) {
            throw new UnsupportedOperationException("Not supported.");
        }
        
    }
    
//    private final static Logger log = LoggerFactory.getLogger(FemaleSocketTestBase.class);

}
