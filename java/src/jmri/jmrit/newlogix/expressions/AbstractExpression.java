package jmri.jmrit.newlogix.expressions;

import jmri.JmriException;
import jmri.implementation.AbstractNamedBean;
import jmri.jmrit.newlogix.Expression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public abstract class AbstractExpression extends AbstractNamedBean
        implements Expression {

    private Lock _lock = Lock.NONE;
    
    
    public AbstractExpression(String sys) throws BadSystemNameException {
        super(sys);
    }

    public AbstractExpression(String sys, String user) throws BadUserNameException, BadSystemNameException {
        super(sys, user);
    }

    @Override
    public String getBeanType() {
        return Bundle.getMessage("BeanNameExpression");
    }

    @Override
    public void setState(int s) throws JmriException {
        log.warn("Unexpected call to setState in AbstractExpression.");  // NOI18N
    }

    @Override
    public int getState() {
        log.warn("Unexpected call to getState in AbstractExpression.");  // NOI18N
        return UNKNOWN;
    }
    
    
    /** {@inheritDoc} */
    @Override
    public Lock getLock() {
        return _lock;
    }
    
    /** {@inheritDoc} */
    @Override
    public void setLock(Lock lock) {
        _lock = lock;
    }
    
    /** {@inheritDoc} */
    @Override
    public String getConfiguratorClassName() {
        String className = this.getClass().getName();
        log.trace("handle object of class {}", className);
        int lastDot = className.lastIndexOf(".");
        if (lastDot > 0) {
            // found package-class boundary OK
            String result = className.substring(0, lastDot)
                    + ".swing."
                    + className.substring(lastDot + 1, className.length())
                    + "Configurator";
            log.trace("adapter class name is {}", result);
            return result;
        } else {
            // no last dot found!
            log.error("No package name found, which is not yet handled!");
            return null;
        }
    }
    
    private final static Logger log = LoggerFactory.getLogger(AbstractExpression.class);
}
