package jmri.managers;

import java.text.DecimalFormat;
import jmri.Action;
import jmri.ActionManager;
import jmri.NewLogix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class providing the basic logic of the ActionManager interface.
 * 
 * @author Daniel Bergqvist Copyright 2018
 */
public class DefaultActionManager extends AbstractManager<Action>
        implements ActionManager {

    DecimalFormat paddedNumber = new DecimalFormat("0000");

    int lastAutoActionRef = 0;
    
    
    public DefaultActionManager() {
        super();
    }

    @Override
    public int getXMLOrder() {
        return NEWLOGIXS;
    }

    @Override
    public String getBeanTypeHandled() {
        return Bundle.getMessage("BeanNameAction");
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
        if (systemName.matches("IQ\\:[AM]\\:\\d+:[AM]\\:A\\d+")) {
            return NameValidity.VALID;
        } else {
            return NameValidity.INVALID;
        }
    }

    @Override
    public String getNewSystemName(NewLogix newLogix) {
        int nextAutoNewLogixRef = lastAutoActionRef + 1;
        StringBuilder b = new StringBuilder(newLogix.getSystemName());
        b.append(":A:A");
        String nextNumber = paddedNumber.format(nextAutoNewLogixRef);
        b.append(nextNumber);
        return b.toString();
    }

    @Override
    public void addAction(Action action) throws IllegalArgumentException {
        // Check if system name is valid
        if (this.validSystemNameFormat(action.getSystemName()) != NameValidity.VALID) {
            log.warn("SystemName " + action.getSystemName() + " is not in the correct format");
            throw new IllegalArgumentException("System name is invalid");
        }
        // save in the maps
        register(action);
    }

    @Override
    public Action getAction(String name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Action getByUserName(String s) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Action getBySystemName(String s) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void deleteAction(Action x) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    private final static Logger log = LoggerFactory.getLogger(DefaultActionManager.class);
}
