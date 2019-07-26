package jmri.jmrit.logixng.analog.implementation;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import javax.annotation.Nonnull;
import jmri.InstanceManager;
import jmri.InstanceManagerAutoDefault;
import jmri.InvokeOnGuiThread;
import jmri.jmrit.logixng.AnalogExpressionBean;
import jmri.jmrit.logixng.AnalogExpressionFactory;
import jmri.jmrit.logixng.AnalogExpressionManager;
import jmri.jmrit.logixng.Base;
import jmri.jmrit.logixng.Category;
import jmri.jmrit.logixng.FemaleAnalogExpressionSocket;
import jmri.jmrit.logixng.FemaleGenericExpressionSocket;
import jmri.jmrit.logixng.FemaleSocketListener;
import jmri.jmrit.logixng.MaleAnalogExpressionSocket;
import jmri.jmrit.logixng.implementation.DefaultFemaleGenericExpressionSocket;
import jmri.jmrit.logixng.implementation.LogixNGPreferences;
import jmri.managers.AbstractManager;
import jmri.util.Log4JUtil;
import jmri.util.ThreadingUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class providing the basic logic of the ExpressionManager interface.
 * 
 * @author Daniel Bergqvist Copyright 2018
 */
public class DefaultAnalogExpressionManager extends AbstractManager<MaleAnalogExpressionSocket>
        implements AnalogExpressionManager, InstanceManagerAutoDefault {

    private final Map<Category, List<Class<? extends Base>>> expressionClassList = new HashMap<>();
    int lastAutoExpressionRef = 0;
    
    // This is for testing only!!!
    // This number needs to be saved and restored.
    DecimalFormat paddedNumber = new DecimalFormat("0000");

    
    public DefaultAnalogExpressionManager() {
        super();
        
        for (Category category : Category.values()) {
            expressionClassList.put(category, new ArrayList<>());
        }
        
//        System.out.format("Read expressions%n");
        for (AnalogExpressionFactory expressionFactory : ServiceLoader.load(AnalogExpressionFactory.class)) {
            expressionFactory.getAnalogExpressionClasses().forEach((entry) -> {
//                System.out.format("Add expression: %s, %s%n", entry.getKey().name(), entry.getValue().getName());
                expressionClassList.get(entry.getKey()).add(entry.getValue());
            });
        }
        
//        System.out.format("Read plugin expressions%n");
//        for (LogixNGPluginFactory expressionFactory : ServiceLoader.load(LogixNGPluginFactory.class)) {
//            System.out.format("Read plugin factory: %s%n", expressionFactory.getClass().getName());
//            expressionFactory.getAnalogExpressionClasses().forEach((entry) -> {
//                System.out.format("Add expression plugin: %s, %s%n", entry.getKey().name(), entry.getValue().getName());
//                expressionClassList.get(entry.getKey()).add(entry.getValue());
//            });
//        }
    }

    protected MaleAnalogExpressionSocket createMaleAnalogExpressionSocket(AnalogExpressionBean expression) {
        MaleAnalogExpressionSocket socket = new DefaultMaleAnalogExpressionSocket(expression);
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
    public MaleAnalogExpressionSocket registerExpression(@Nonnull AnalogExpressionBean expression)
            throws IllegalArgumentException {
        
        if (expression instanceof MaleAnalogExpressionSocket) {
            throw new IllegalArgumentException("registerAction() cannot register a MaleAnalogExpressionSocket. Use the method register() instead.");
        }
        
        // Check if system name is valid
        if (this.validSystemNameFormat(expression.getSystemName()) != NameValidity.VALID) {
            log.warn("SystemName " + expression.getSystemName() + " is not in the correct format");
            throw new IllegalArgumentException("System name is invalid");
        }
        
        // save in the maps
        MaleAnalogExpressionSocket maleSocket = createMaleAnalogExpressionSocket(expression);
        register(maleSocket);
        return maleSocket;
    }
    
    @Override
    public int getXMLOrder() {
        return LOGIXNGS;
    }

    @Override
    public String getBeanTypeHandled() {
        return Bundle.getMessage("BeanNameAnalogExpression");
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
        if (systemName.matches(getSystemNamePrefix()+"AE:?\\d+")) {
            return NameValidity.VALID;
        } else {
            return NameValidity.INVALID;
        }
    }

    @Override
    public String getNewSystemName() {
        int nextAutoLogixNGRef = lastAutoExpressionRef + 1;
        StringBuilder b = new StringBuilder(getSystemNamePrefix());
        b.append("AE:");
        String nextNumber = paddedNumber.format(nextAutoLogixNGRef);
        b.append(nextNumber);
        return b.toString();
    }

    @Override
    public FemaleAnalogExpressionSocket createFemaleAnalogExpressionSocket(
            Base parent, FemaleSocketListener listener, String socketName) {
        
        LogixNGPreferences preferences = InstanceManager.getDefault(LogixNGPreferences.class);
        if (preferences.getUseGenericFemaleSockets()) {
            return new DefaultFemaleGenericExpressionSocket(
                    FemaleGenericExpressionSocket.SocketType.ANALOG, parent, listener, socketName)
                    .getAnalogSocket();
        } else {
            return new DefaultFemaleAnalogExpressionSocket(parent, listener, socketName);
        }
    }
/*
    @Override
    public FemaleAnalogExpressionSocket createFemaleAnalogExpressionSocket(
            Base parent, FemaleSocketListener listener, String socketName,
            MaleAnalogExpressionSocket maleSocket) {
        
        FemaleAnalogExpressionSocket socket =
                new DefaultFemaleAnalogExpressionSocket(parent, listener, socketName, maleSocket);
        
        return socket;
    }
*/    
    @Override
    public Map<Category, List<Class<? extends Base>>> getExpressionClasses() {
        return expressionClassList;
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
        registerExpression(expression);
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

    /** {@inheritDoc} */
    @Override
    public String getBeanTypeHandled(boolean plural) {
        return Bundle.getMessage(plural ? "BeanNameAnalogExpressions" : "BeanNameAnalogExpression");
    }
    
    static volatile DefaultAnalogExpressionManager _instance = null;

    @InvokeOnGuiThread  // this method is not thread safe
    static public DefaultAnalogExpressionManager instance() {
        if (log.isDebugEnabled()) {
            if (!ThreadingUtil.isGUIThread()) {
                Log4JUtil.warnOnce(log, "instance() called on wrong thread");
            }
        }
        
        if (_instance == null) {
            _instance = new DefaultAnalogExpressionManager();
        }
        return (_instance);
    }

    private final static Logger log = LoggerFactory.getLogger(DefaultAnalogExpressionManager.class);
}
