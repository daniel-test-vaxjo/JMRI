package jmri.jmrit.logixng.tools;

import jmri.Conditional;
import jmri.InstanceManager;
import jmri.Logix;
import jmri.jmrit.logixng.LogixNG;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Imports Logixs to LogixNG
 * 
 * @author Daniel Bergqvist 2019
 */
public class ImportLogix {

    private final Logix _logix;
    private final LogixNG _logixNG;
    
    public ImportLogix(Logix logix) {
        
        if ("SYS".equals(logix.getSystemName())) {
            throw new IllegalArgumentException("Cannot import Logix SYS to LogixNG");
        }
        if (logix.getSystemName().startsWith("RTX")) {
            log.error("Warning. Trying to import RTX from Logix to LogixNG: ", logix.getSystemName());
        }
        
        _logix = logix;
        _logixNG = InstanceManager.getDefault(jmri.jmrit.logixng.LogixNG_Manager.class)
                .createLogixNG("Logix: "+_logix.getDisplayName(), false);
        
//        log.debug("Import Logix {} to LogixNG {}", _logix.getSystemName(), _logixNG.getSystemName());
        log.error("Import Logix {} to LogixNG {}", _logix.getSystemName(), _logixNG.getSystemName());
    }
    
    public void doImport() {
        for (int i=0; i < _logix.getNumConditionals(); i++) {
            Conditional c = _logix.getConditional(_logix.getConditionalByNumberOrder(i));
            log.error("Import Conditional {} to ConditionalNG {}", c.getSystemName(), _logixNG.getSystemName());
            ImportConditional ic = new ImportConditional(_logix, c, _logixNG, _logixNG.getSystemName()+":"+Integer.toString(i));
            ic.doImport();
        }
    }
    
    
    private final static Logger log = LoggerFactory.getLogger(ImportLogix.class);

}
