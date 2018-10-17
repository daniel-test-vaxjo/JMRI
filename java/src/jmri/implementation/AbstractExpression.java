package jmri.implementation;

import jmri.JmriException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jmri.NewLogixExpression;

/**
 *
 */
public abstract class AbstractExpression extends AbstractNamedBean
        implements NewLogixExpression {

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
        log.warn("Unexpected call to setState in AbstractAction.");  // NOI18N
    }

    @Override
    public int getState() {
        log.warn("Unexpected call to getState in AbstractAction.");  // NOI18N
        return UNKNOWN;
    }

    private final static Logger log = LoggerFactory.getLogger(AbstractExpression.class);
}
