package jmri.jmrit.newlogix;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.Set;
import jmri.JmriException;
import jmri.NamedBean;
import jmri.NewLogixAction;
import jmri.NewLogixCategory;
import jmri.NewLogixCommon;
import jmri.NewLogixCommon.NewLogixSocket;

/**
 *
 */
public class NewLogixActionSocket implements NewLogixAction, NewLogixSocket {

    private NewLogixAction _action;

    @Override
    public NewLogixCategory getCategory() {
        if (_action != null) {
            return _action.getCategory();
        } else {
            return null;
        }
    }

    @Override
    public boolean isExternal() {
        if (_action != null) {
            return _action.isExternal();
        } else {
            return false;
        }
    }

    @Override
    public boolean executeStart() {
        if (_action != null) {
            return _action.executeStart();
        } else {
            return false;
        }
    }

    @Override
    public boolean executeContinue() {
        if (_action != null) {
            return _action.executeContinue();
        } else {
            return false;
        }
    }

    @Override
    public boolean executeRestart() {
        if (_action != null) {
            return _action.executeRestart();
        } else {
            return false;
        }
    }

    @Override
    public void abort() {
        if (_action != null) {
            _action.abort();
        }
    }

    @Override
    public String getShortDescription() {
        if (_action != null) {
            return _action.getShortDescription();
        } else {
            return null;
        }
    }

    @Override
    public String getLongDescription() {
        if (_action != null) {
            return _action.getLongDescription();
        } else {
            return null;
        }
    }

    @Override
    public NewLogixSocket getChild(int index) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getChildCount() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getUserName() {
        if (_action != null) {
            return _action.getUserName();
        } else {
            return null;
        }
    }

    @Override
    public void setUserName(String s) throws BadUserNameException {
        if (_action != null) {
            _action.setUserName(s);
        }
    }

    @Override
    public String getSystemName() {
        if (_action != null) {
            return _action.getSystemName();
        } else {
            return null;
        }
    }

    @Override
    public String getDisplayName() {
        if (_action != null) {
            return _action.getDisplayName();
        } else {
            return null;
        }
    }

    @Override
    public String getFullyFormattedDisplayName() {
        if (_action != null) {
            return _action.getFullyFormattedDisplayName();
        } else {
            return null;
        }
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener l, String name, String listenerRef) {
        if (_action != null) {
            _action.addPropertyChangeListener(l, name, listenerRef);
        }
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener l) {
        if (_action != null) {
            _action.addPropertyChangeListener(l);
        }
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener l) {
        if (_action != null) {
            _action.removePropertyChangeListener(l);
        }
    }

    @Override
    public void updateListenerRef(PropertyChangeListener l, String newName) {
        if (_action != null) {
            _action.updateListenerRef(l, newName);
        }
    }

    @Override
    public void vetoableChange(PropertyChangeEvent evt) throws PropertyVetoException {
        if (_action != null) {
            _action.vetoableChange(evt);
        }
    }

    @Override
    public String getListenerRef(PropertyChangeListener l) {
        if (_action != null) {
            return _action.getListenerRef(l);
        } else {
            return null;
        }
    }

    @Override
    public ArrayList<String> getListenerRefs() {
        if (_action != null) {
            return _action.getListenerRefs();
        } else {
            return null;
        }
    }

    @Override
    public int getNumPropertyChangeListeners() {
        if (_action != null) {
            return _action.getNumPropertyChangeListeners();
        } else {
            return 0;
        }
    }

    @Override
    public PropertyChangeListener[] getPropertyChangeListenersByReference(String name) {
        if (_action != null) {
            return _action.getPropertyChangeListenersByReference(name);
        } else {
            return null;
        }
    }

    @Override
    public void dispose() {
        if (_action != null) {
            _action.dispose();
        }
    }

    @Override
    public void setState(int s) throws JmriException {
        if (_action != null) {
            _action.setState(s);
        }
    }

    @Override
    public int getState() {
        if (_action != null) {
            return _action.getState();
        } else {
            return NamedBean.UNKNOWN;
        }
    }

    @Override
    public String describeState(int state) {
        if (_action != null) {
            return _action.describeState(state);
        } else {
            return null;
        }
    }

    @Override
    public String getComment() {
        if (_action != null) {
            return _action.getComment();
        } else {
            return null;
        }
    }

    @Override
    public void setComment(String comment) {
        if (_action != null) {
            _action.setComment(comment);
        }
    }

    @Override
    public void setProperty(String key, Object value) {
        if (_action != null) {
            _action.setProperty(key, value);
        }
    }

    @Override
    public Object getProperty(String key) {
        if (_action != null) {
            return _action.getProperty(key);
        } else {
            return null;
        }
    }

    @Override
    public void removeProperty(String key) {
        if (_action != null) {
            _action.removeProperty(key);
        }
    }

    @Override
    public Set<String> getPropertyKeys() {
        if (_action != null) {
            return _action.getPropertyKeys();
        } else {
            return null;
        }
    }

    @Override
    public String getBeanType() {
        if (_action != null) {
            return _action.getBeanType();
        } else {
            return null;
        }
    }

    @Override
    public int compareSystemNameSuffix(String suffix1, String suffix2, NamedBean n2) {
        if (_action != null) {
            return _action.compareSystemNameSuffix(suffix1, suffix2, n2);
        } else {
            return 0;
        }
    }

    @Override
    public String getConfiguratorClassName() {
        if (_action != null) {
            return _action.getConfiguratorClassName();
        } else {
            return null;
        }
    }

}
