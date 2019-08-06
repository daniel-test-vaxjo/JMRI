package jmri.jmrit.logixng.digital.actions;

import apps.AppsBase;
import java.io.IOException;
import jmri.InstanceManager;
import jmri.ShutDownManager;
import jmri.jmrit.logixng.Base;
import jmri.jmrit.logixng.Category;
import jmri.jmrit.logixng.DigitalActionManager;
import jmri.jmrit.logixng.FemaleSocket;
import jmri.util.SystemType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This action sets the state of a turnout.
 * 
 * @author Daniel Bergqvist Copyright 2018
 */
public class ShutdownComputer extends AbstractDigitalAction {

    private ShutdownComputer _template;
    private int _seconds;
    
    public ShutdownComputer(int seconds)
            throws BadUserNameException {
        super(InstanceManager.getDefault(DigitalActionManager.class).getNewSystemName());
    }

    public ShutdownComputer(String sys, int seconds)
            throws BadUserNameException {
        super(sys);
    }

    public ShutdownComputer(String sys, String user, int seconds)
            throws BadUserNameException, BadSystemNameException {
        super(sys, user);
    }
    
    private ShutdownComputer(ShutdownComputer template, String sys) {
        super(sys);
        _template = template;
        if (_template == null) throw new NullPointerException();    // Temporary solution to make variable used.
    }
    
    /** {@inheritDoc} */
    @Override
    public Base getNewObjectBasedOnTemplate(String sys) {
        return new ShutdownComputer(this, sys);
    }
    
    public void setSeconds(int seconds) {
        _seconds = seconds;
    }
    
    public int getSeconds() {
        return _seconds;
    }
    
    /** {@inheritDoc} */
    @Override
    public Category getCategory() {
        return Category.EXRAVAGANZA;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isExternal() {
        return true;
    }
    
    /** {@inheritDoc} */
    @Override
    public void execute() {
        String time = (_seconds == 0) ? "now" : Integer.toString(_seconds);
        try {
            if (SystemType.isLinux() || SystemType.isUnix() || SystemType.isMacOSX()) {
                Runtime runtime = Runtime.getRuntime();
                runtime.exec("shutdown -h " + time);
//                Process proc = runtime.exec("shutdown -s -t " + time);
            } else if (SystemType.isWindows()) {
                Runtime runtime = Runtime.getRuntime();
                runtime.exec("shutdown.exe -s -t " + time);
//                Process proc = runtime.exec("shutdown -s -t " + time);
                InstanceManager.getDefault(ShutDownManager.class).shutdown();
            } else {
                throw new UnsupportedOperationException("Unknown OS: "+SystemType.getOSName());
            }
        } catch (SecurityException | IOException e) {
            log.error("Shutdown failed", e);  // NOI18N
        }
        
        // Quit program
        AppsBase.handleQuit();
    }

    @Override
    public FemaleSocket getChild(int index) throws IllegalArgumentException, UnsupportedOperationException {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public int getChildCount() {
        return 0;
    }

    @Override
    public String getShortDescription() {
        return Bundle.getMessage("ShutdownComputer_Short");
    }

    @Override
    public String getLongDescription() {
        return Bundle.getMessage("ShutdownComputer_Long", _seconds);
    }
    
    /** {@inheritDoc} */
    @Override
    public void setup() {
        // Do nothing
    }
    
    /** {@inheritDoc} */
    @Override
    public void registerListenersForThisClass() {
    }
    
    /** {@inheritDoc} */
    @Override
    public void unregisterListenersForThisClass() {
    }
    
    /** {@inheritDoc} */
    @Override
    public void disposeMe() {
    }

    
    private final static Logger log = LoggerFactory.getLogger(ShutdownComputer.class);
}
