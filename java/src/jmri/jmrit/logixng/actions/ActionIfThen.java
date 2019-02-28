package jmri.jmrit.logixng.actions;

import jmri.InstanceManager;
import jmri.jmrit.logixng.Category;
import jmri.jmrit.logixng.ActionManager;
import jmri.jmrit.logixng.ExpressionManager;
import jmri.jmrit.logixng.FemaleActionSocket;
import jmri.jmrit.logixng.FemaleExpressionSocket;
import jmri.jmrit.logixng.FemaleSocket;
import jmri.jmrit.logixng.FemaleSocketListener;
import jmri.jmrit.logixng.MaleActionSocket;
import jmri.jmrit.logixng.MaleExpressionSocket;
import jmri.jmrit.logixng.LogixNG;

/**
 * Executes an action when the expression is True.
 * 
 * @author Daniel Bergqvist Copyright 2018
 */
public class ActionIfThen extends AbstractAction implements FemaleSocketListener {

    /**
     * The type of Action. If the type is changed, the action is aborted if it
     * is currently running.
     */
    public enum Type {
        /**
         * Action is triggered when the expression is True. The action may
         * continue even if the expression becomes False.
         * 
         * If the expression is False and then True again before the action
         * is finished, action.executeAgain() is called instead of action.execute().
         * 
         * Note that in a tree of actions, some actions may have been finished
         * and some actions still running. In this case, the actions that are
         * still running will be called with executeAgain() but those actions
         * that are finished will be called with execute(). Actions that have
         * child actions need to deal with this.
         */
        TRIGGER_ACTION,
        
        /**
         * Action is executed when the expression is True but only as long as
         * the expression stays True. If the expression becomes False, the
         * action is aborted.
         */
        CONTINOUS_ACTION,
    }

    private Type _type;
    private boolean _lastExpressionResult = false;
    private boolean _lastActionResult = false;
    private final FemaleExpressionSocket _ifExpressionSocket;
    private final FemaleActionSocket _thenActionSocket;
    
    /**
     * Create a new instance of ActionIfThen and generate a new system name.
     * @param newLogix the LogixNG that this action is related to
     */
    public ActionIfThen(LogixNG newLogix, Type type) {
        super(InstanceManager.getDefault(ActionManager.class).getNewSystemName(newLogix));
        _type = type;
        _ifExpressionSocket = InstanceManager.getDefault(ExpressionManager.class)
                .createFemaleExpressionSocket(this, "E");
        _thenActionSocket = InstanceManager.getDefault(ActionManager.class)
                .createFemaleActionSocket(this, "A");
    }
    
    public ActionIfThen(String sys, Type type) {
        super(sys);
        _type = type;
        _ifExpressionSocket = InstanceManager.getDefault(ExpressionManager.class)
                .createFemaleExpressionSocket(this, "E");
        _thenActionSocket = InstanceManager.getDefault(ActionManager.class)
                .createFemaleActionSocket(this, "A");
    }
    
    public ActionIfThen(String sys, String user, Type type) {
        super(sys, user);
        _type = type;
        _ifExpressionSocket = InstanceManager.getDefault(ExpressionManager.class)
                .createFemaleExpressionSocket(this, "E");
        _thenActionSocket = InstanceManager.getDefault(ActionManager.class)
                .createFemaleActionSocket(this, "A");
    }
    
    public ActionIfThen(
            String sys, Type type,
            String ifExpressionSocketName,
            String thenActionSocketName,
            MaleExpressionSocket ifExpression,
            MaleActionSocket thenAction) {
        
        super(sys);
        _type = type;
        _ifExpressionSocket = InstanceManager.getDefault(ExpressionManager.class)
                .createFemaleExpressionSocket(this, ifExpressionSocketName, ifExpression);
        _thenActionSocket = InstanceManager.getDefault(ActionManager.class)
                .createFemaleActionSocket(this, thenActionSocketName, thenAction);
    }
    
    public ActionIfThen(
            String sys, String user, Type type,
            String ifExpressionSocketName,
            String thenActionSocketName,
            MaleExpressionSocket ifExpression,
            MaleActionSocket thenAction) {
        
        super(sys, user);
        _type = type;
        _ifExpressionSocket = InstanceManager.getDefault(ExpressionManager.class)
                .createFemaleExpressionSocket(this, ifExpressionSocketName, ifExpression);
        _thenActionSocket = InstanceManager.getDefault(ActionManager.class)
                .createFemaleActionSocket(this, thenActionSocketName, thenAction);
    }
    
    /** {@inheritDoc} */
    @Override
    public Category getCategory() {
        return Category.OTHER;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isExternal() {
        return false;
    }
    
    /** {@inheritDoc} */
    @Override
    public boolean executeStart() {
        _lastExpressionResult = _ifExpressionSocket.evaluate();
        _lastActionResult = false;

        if (_lastExpressionResult) {
            _lastActionResult = _thenActionSocket.executeStart();
        }

        return _lastActionResult;
    }

    /** {@inheritDoc} */
    @Override
    public boolean executeContinue() {
        switch (_type) {
            case TRIGGER_ACTION:
                _lastActionResult = _thenActionSocket.executeContinue();
                break;
                
            case CONTINOUS_ACTION:
                boolean exprResult = _ifExpressionSocket.evaluate();
                if (exprResult) {
                    _lastActionResult = _thenActionSocket.executeContinue();
                } else {
                    _thenActionSocket.abort();
                    _lastActionResult = false;
                }
                break;
                
            default:
                throw new RuntimeException(String.format("Unknown type '%s'", _type.name()));
        }
        
        return _lastActionResult;
    }

    /** {@inheritDoc} */
    @Override
    public boolean executeRestart() {
        switch (_type) {
            case TRIGGER_ACTION:
                _lastActionResult = _thenActionSocket.executeRestart();
                break;
                
            case CONTINOUS_ACTION:
                boolean exprResult = _ifExpressionSocket.evaluate();
                if (exprResult) {
                    _lastActionResult = _thenActionSocket.executeRestart();
                } else {
                    _thenActionSocket.abort();
                    _lastActionResult = false;
                }
                break;
                
            default:
                throw new RuntimeException(String.format("Unknown type '%s'", _type.name()));
        }
        
        return _lastActionResult;
    }

    /** {@inheritDoc} */
    @Override
    public void abort() {
        _thenActionSocket.abort();
    }
    
    /** {@inheritDoc} */
    public Type getType() {
        return _type;
    }
    
    /** {@inheritDoc} */
    public void setType(Type type) {
        if ((_type != type) && _lastActionResult) {
            _thenActionSocket.abort();
        }
        _type = type;
    }
    
    @Override
    public FemaleSocket getChild(int index) throws IllegalArgumentException, UnsupportedOperationException {
        switch (index) {
            case 0:
                return _ifExpressionSocket;
                
            case 1:
                return _thenActionSocket;
                
            default:
                throw new IllegalArgumentException(
                        String.format("index has invalid value: %d", index));
        }
    }

    @Override
    public int getChildCount() {
        return 2;
    }

    @Override
    public void connected(FemaleSocket socket) {
        // This class doesn't care.
    }

    @Override
    public void disconnected(FemaleSocket socket) {
        // This class doesn't care.
    }

    @Override
    public String getShortDescription() {
        return Bundle.getMessage("ActionIfThen_Short");
    }

    @Override
    public String getLongDescription() {
        return Bundle.getMessage("ActionIfThen_Long", _ifExpressionSocket.getName(), _thenActionSocket.getName());
    }

}