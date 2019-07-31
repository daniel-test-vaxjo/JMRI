package jmri.jmrit.logixng.digital.expressions;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import javax.annotation.CheckForNull;
import jmri.InstanceManager;
import jmri.NamedBeanHandle;
import jmri.NamedBeanHandleManager;
import jmri.Turnout;
import jmri.TurnoutManager;
import jmri.jmrit.logixng.Base;
import jmri.jmrit.logixng.Category;
import jmri.jmrit.logixng.DigitalExpressionManager;
import jmri.jmrit.logixng.FemaleSocket;
import jmri.jmrit.logixng.Is_IsNot_Enum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Evaluates the state of a Turnout.
 * 
 * @author Daniel Bergqvist Copyright 2018
 */
public class ExpressionTurnout extends AbstractDigitalExpression
        implements PropertyChangeListener, VetoableChangeListener {

    private ExpressionTurnout _template;
    private NamedBeanHandle<Turnout> _turnoutHandle;
    private Is_IsNot_Enum _is_IsNot = Is_IsNot_Enum.IS;
    private TurnoutState _turnoutState = TurnoutState.THROWN;
    private boolean _listenersAreRegistered = false;

    public ExpressionTurnout()
            throws BadUserNameException {
        super(InstanceManager.getDefault(DigitalExpressionManager.class).getNewSystemName());
    }

    public ExpressionTurnout(String sys)
            throws BadUserNameException, BadSystemNameException {
        super(sys);
    }

    public ExpressionTurnout(String sys, String user)
            throws BadUserNameException, BadSystemNameException {
        super(sys, user);
    }
    
    private ExpressionTurnout(ExpressionTurnout template, String sys) {
        super(sys);
        _template = template;
        if (_template == null) throw new NullPointerException();    // Temporary solution to make variable used.
    }
    
    /** {@inheritDoc} */
    @Override
    public Base getNewObjectBasedOnTemplate(String sys) {
        return new ExpressionTurnout(this, sys);
    }
    
    public void setTurnout(String turnoutName) {
        if (_listenersAreRegistered) {
            RuntimeException e = new RuntimeException("setTurnout must not be called when listeners are registered");
            log.error("setTurnout must not be called when listeners are registered", e);
            throw e;
        }
        Turnout turnout = InstanceManager.getDefault(TurnoutManager.class).getTurnout(turnoutName);
        _turnoutHandle = InstanceManager.getDefault(NamedBeanHandleManager.class).getNamedBeanHandle(turnoutName, turnout);
    }
    
    public void setTurnout(NamedBeanHandle<Turnout> handle) {
        if (_listenersAreRegistered) {
            RuntimeException e = new RuntimeException("setTurnout must not be called when listeners are registered");
            log.error("setTurnout must not be called when listeners are registered", e);
            throw e;
        }
        _turnoutHandle = handle;
    }
    
    public void setTurnout(@CheckForNull Turnout turnout) {
        if (_listenersAreRegistered) {
            RuntimeException e = new RuntimeException("setTurnout must not be called when listeners are registered");
            log.error("setTurnout must not be called when listeners are registered", e);
            throw e;
        }
        if (turnout != null) {
            _turnoutHandle = InstanceManager.getDefault(NamedBeanHandleManager.class)
                    .getNamedBeanHandle(turnout.getDisplayName(), turnout);
        } else {
            _turnoutHandle = null;
        }
    }
    
    public NamedBeanHandle<Turnout> getTurnout() {
        return _turnoutHandle;
    }
    
    public void set_Is_IsNot(Is_IsNot_Enum is_IsNot) {
        _is_IsNot = is_IsNot;
    }
    
    public Is_IsNot_Enum get_Is_IsNot() {
        return _is_IsNot;
    }
    
    public void setTurnoutState(TurnoutState state) {
        _turnoutState = state;
    }
    
    public TurnoutState getTurnoutState() {
        return _turnoutState;
    }

    @Override
    public void vetoableChange(java.beans.PropertyChangeEvent evt) throws java.beans.PropertyVetoException {
        if ("CanDelete".equals(evt.getPropertyName())) { // No I18N
            if (evt.getOldValue() instanceof Turnout) {
                if (evt.getOldValue().equals(getTurnout().getBean())) {
                    throw new PropertyVetoException(getDisplayName(), evt);
                }
            }
        } else if ("DoDelete".equals(evt.getPropertyName())) { // No I18N
            if (evt.getOldValue() instanceof Turnout) {
                if (evt.getOldValue().equals(getTurnout().getBean())) {
                    setTurnout((Turnout)null);
                }
            }
        }
    }
    
    /** {@inheritDoc} */
    @Override
    public Category getCategory() {
        return Category.ITEM;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isExternal() {
        return true;
    }
    
    /** {@inheritDoc} */
    @Override
    public boolean evaluate() {
        TurnoutState currentTurnoutState = TurnoutState.get(_turnoutHandle.getBean().getCommandedState());
        if (_is_IsNot == Is_IsNot_Enum.IS) {
            return currentTurnoutState == _turnoutState;
        } else {
            return currentTurnoutState != _turnoutState;
        }
    }

    /** {@inheritDoc} */
    @Override
    public void reset() {
        // Do nothing.
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
        return Bundle.getMessage("Turnout_Short");
    }

    @Override
    public String getLongDescription() {
        String turnoutName;
        if (_turnoutHandle != null) {
            turnoutName = _turnoutHandle.getBean().getDisplayName();
        } else {
            turnoutName = Bundle.getMessage("BeanNotSelected");
        }
        return Bundle.getMessage("Turnout_Long", turnoutName, _is_IsNot.toString(), _turnoutState._text);
    }
    
    /** {@inheritDoc} */
    @Override
    public void setup() {
        // Do nothing
    }
    
    /** {@inheritDoc} */
    @Override
    public void registerListenersForThisClass() {
        if (!_listenersAreRegistered && (_turnoutHandle != null)) {
            _turnoutHandle.getBean().addPropertyChangeListener("KnownState", this);
            _listenersAreRegistered = true;
        }
    }
    
    /** {@inheritDoc} */
    @Override
    public void unregisterListenersForThisClass() {
        if (_listenersAreRegistered) {
            _turnoutHandle.getBean().removePropertyChangeListener("KnownState", this);
            _listenersAreRegistered = false;
        }
    }
    
    /** {@inheritDoc} */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        getConditionalNG().execute();
    }
    
    /** {@inheritDoc} */
    @Override
    public void disposeMe() {
    }
    
    
    
    public enum TurnoutState {
        CLOSED(Turnout.CLOSED, InstanceManager.getDefault(TurnoutManager.class).getClosedText()),
        THROWN(Turnout.THROWN, InstanceManager.getDefault(TurnoutManager.class).getThrownText()),
        OTHER(-1, Bundle.getMessage("TurnoutOtherStatus"));
        
        private final int _id;
        private final String _text;
        
        private TurnoutState(int id, String text) {
            this._id = id;
            this._text = text;
        }
        
        static public TurnoutState get(int id) {
            switch (id) {
                case Turnout.CLOSED:
                    return CLOSED;
                    
                case Turnout.THROWN:
                    return THROWN;
                    
                default:
                    return OTHER;
            }
        }
        
        public int getID() {
            return _id;
        }
        
        @Override
        public String toString() {
            return _text;
        }
        
    }
    
    
    private final static Logger log = LoggerFactory.getLogger(ExpressionTurnout.class);
    
}
