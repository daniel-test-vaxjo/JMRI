package jmri.jmrit.logixng.implementation;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import javax.annotation.Nonnull;
import jmri.jmrit.logixng.StringExpressionManager;
import jmri.InstanceManagerAutoDefault;
import jmri.InvokeOnGuiThread;
import jmri.jmrit.logixng.Category;
import jmri.jmrit.logixng.MaleStringExpressionSocket;
import jmri.util.Log4JUtil;
import jmri.util.ThreadingUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jmri.jmrit.logixng.Base;
import jmri.jmrit.logixng.StringExpression;
import jmri.jmrit.logixng.FemaleStringExpressionSocket;
import jmri.jmrit.logixng.FemaleSocketListener;
import jmri.managers.AbstractManager;
import jmri.jmrit.logixng.LogixNG;
import jmri.jmrit.logixng.LogixNGPluginFactory;
import jmri.jmrit.logixng.StringExpressionFactory;

/**
 * Class providing the basic logic of the ExpressionManager interface.
 * 
 * @author Daniel Bergqvist Copyright 2018
 */
public class DefaultStringExpressionManager extends AbstractManager<MaleStringExpressionSocket>
        implements StringExpressionManager, InstanceManagerAutoDefault {

    private final Map<Category, List<Class<? extends StringExpression>>> expressionClassList = new HashMap<>();
    int lastAutoExpressionRef = 0;
    
    // This is for testing only!!!
    // This number needs to be saved and restored.
    DecimalFormat paddedNumber = new DecimalFormat("0000");

    
    public DefaultStringExpressionManager() {
        super();
        
        for (Category category : Category.values()) {
            expressionClassList.put(category, new ArrayList<>());
        }
        
        System.out.format("Read expressions%n");
        for (StringExpressionFactory expressionFactory : ServiceLoader.load(StringExpressionFactory.class)) {
            expressionFactory.getStringExpressionClasses().forEach((entry) -> {
                System.out.format("Add expression: %s, %s%n", entry.getKey().name(), entry.getValue().getName());
                expressionClassList.get(entry.getKey()).add(entry.getValue());
            });
        }
        
//        System.out.format("Read plugin expressions%n");
//        for (LogixNGPluginFactory expressionFactory : ServiceLoader.load(LogixNGPluginFactory.class)) {
//            System.out.format("Read plugin factory: %s%n", expressionFactory.getClass().getName());
//            expressionFactory.getStringExpressionClasses().forEach((entry) -> {
//                System.out.format("Add expression plugin: %s, %s%n", entry.getKey().name(), entry.getValue().getName());
//                expressionClassList.get(entry.getKey()).add(entry.getValue());
//            });
//        }
    }

    protected MaleStringExpressionSocket createMaleStringExpressionSocket(StringExpression expression) {
        MaleStringExpressionSocket socket = new DefaultMaleStringExpressionSocket(expression);
        expression.setParent(socket);
        return socket;
    }
    
    /**
     * Remember a NamedBean Object created outside the manager.
     * This method creates a MaleActionSocket for the action.
     *
     * @param expression the bean
     */
    @Override
    public MaleStringExpressionSocket register(@Nonnull StringExpression expression)
            throws IllegalArgumentException {
        
        // Check if system name is valid
        if (this.validSystemNameFormat(expression.getSystemName()) != NameValidity.VALID) {
            log.warn("SystemName " + expression.getSystemName() + " is not in the correct format");
            throw new IllegalArgumentException("System name is invalid");
        }
        // save in the maps
        MaleStringExpressionSocket maleSocket = createMaleStringExpressionSocket(expression);
        register(maleSocket);
        return maleSocket;
    }
    
    @Override
    public int getXMLOrder() {
        return LOGIXNGS;
    }

    @Override
    public String getBeanTypeHandled() {
        return Bundle.getMessage("BeanNameStringExpression");
    }

    @Override
    public String getSystemPrefix() {
        return "I";
    }

    @Override
    public char typeLetter() {
        return 'Q';
    }

    /**
     * Test if parameter is a properly formatted system name.
     *
     * @param systemName the system name
     * @return enum indicating current validity, which might be just as a prefix
     */
    @Override
    public NameValidity validSystemNameFormat(String systemName) {
        // I - Internal
        // Q - LogixNG
        // :
        // Optional: A: - Automatic (if the system name is created by the software and not by the user
        // \d+ - The LogixNG ID number
        // :
        // Optional: A: - Automatic (if the system name is created by the software and not by the user
        // AE - StringExpression
        // \d+ - The StringExpression ID number
        if (systemName.matches("IQA?\\d+:SEA?\\d+")) {
            return NameValidity.VALID;
        } else {
            return NameValidity.INVALID;
        }
    }

    @Override
    public String getNewSystemName(LogixNG newLogix) {
        int nextAutoLogixNGRef = lastAutoExpressionRef + 1;
        StringBuilder b = new StringBuilder(newLogix.getSystemName());
        b.append(":SEA");
        String nextNumber = paddedNumber.format(nextAutoLogixNGRef);
        b.append(nextNumber);
        return b.toString();
    }

    @Override
    public FemaleStringExpressionSocket createFemaleStringExpressionSocket(
            Base parent, FemaleSocketListener listener, String socketName) {
        return new DefaultFemaleStringExpressionSocket(parent, listener, socketName);
    }

    @Override
    public FemaleStringExpressionSocket createFemaleStringExpressionSocket(
            Base parent,
            FemaleSocketListener listener,
            String socketName,
            MaleStringExpressionSocket maleSocket) {
        
        FemaleStringExpressionSocket socket =
                new DefaultFemaleStringExpressionSocket(parent, listener, socketName, maleSocket);
        
        return socket;
    }
/*
    @Override
    public void addExpression(Expression expression) throws IllegalArgumentException {
        // Check if system name is valid
        if (this.validSystemNameFormat(expression.getSystemName()) != NameValidity.VALID) {
            log.warn("SystemName " + expression.getSystemName() + " is not in the correct format");
            throw new IllegalArgumentException("System name is invalid");
        }
        // save in the maps
        register(expression);
    }

    @Override
    public Expression getExpression(String name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Expression getByUserName(String s) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Expression getBySystemName(String s) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void deleteExpression(Expression x) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
*/    
    static DefaultStringExpressionManager _instance = null;

    @InvokeOnGuiThread  // this method is not thread safe
    static public DefaultStringExpressionManager instance() {
        if (log.isDebugEnabled()) {
            if (!ThreadingUtil.isGUIThread()) {
                Log4JUtil.warnOnce(log, "instance() called on wrong thread");
            }
        }
        
        if (_instance == null) {
            _instance = new DefaultStringExpressionManager();
        }
        return (_instance);
    }

    private final static Logger log = LoggerFactory.getLogger(DefaultStringExpressionManager.class);
}
