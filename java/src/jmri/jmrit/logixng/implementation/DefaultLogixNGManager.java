package jmri.jmrit.logixng.implementation;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.awt.GraphicsEnvironment;
import java.beans.PropertyVetoException;
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import jmri.InstanceManager;
import jmri.InvokeOnGuiThread;
import jmri.JmriException;
import jmri.Light;
import jmri.LightManager;
import jmri.Memory;
import jmri.MemoryManager;
import jmri.Sensor;
import jmri.SensorManager;
import jmri.Turnout;
import jmri.TurnoutManager;
import jmri.jmrit.logixng.AnalogExpressionManager;
import jmri.jmrit.logixng.AnalogActionManager;
import jmri.jmrit.logixng.Base;
import jmri.jmrit.logixng.ConditionalNG;
import jmri.jmrit.logixng.DigitalActionManager;
import jmri.jmrit.logixng.DigitalExpressionManager;
import jmri.jmrit.logixng.FemaleSocket;
import jmri.jmrit.logixng.FemaleSocketFactory;
import jmri.jmrit.logixng.LogixNG;
import jmri.jmrit.logixng.LogixNG_Manager;
import jmri.jmrit.logixng.MaleAnalogActionSocket;
import jmri.jmrit.logixng.MaleAnalogExpressionSocket;
import jmri.jmrit.logixng.MaleDigitalActionSocket;
import jmri.jmrit.logixng.MaleDigitalExpressionSocket;
import jmri.jmrit.logixng.MaleStringActionSocket;
import jmri.jmrit.logixng.MaleStringExpressionSocket;
import jmri.jmrit.logixng.MaleSocket;
import jmri.jmrit.logixng.SocketAlreadyConnectedException;
import jmri.jmrit.logixng.StringExpressionManager;
import jmri.jmrit.logixng.StringActionManager;
import jmri.jmrit.logixng.analog.actions.AnalogActionMemory;
import jmri.jmrit.logixng.analog.expressions.AnalogExpressionMemory;
import jmri.jmrit.logixng.digital.actions.ActionLight;
import jmri.jmrit.logixng.digital.actions.ActionSensor;
import jmri.jmrit.logixng.digital.actions.ActionTurnout;
import jmri.jmrit.logixng.digital.actions.DoAnalogAction;
import jmri.jmrit.logixng.digital.actions.DoStringAction;
import jmri.jmrit.logixng.digital.actions.HoldAnything;
import jmri.jmrit.logixng.digital.actions.IfThen;
import jmri.jmrit.logixng.digital.actions.Many;
import jmri.jmrit.logixng.digital.actions.ShutdownComputer;
import jmri.jmrit.logixng.digital.expressions.And;
import jmri.jmrit.logixng.digital.expressions.Antecedent;
import jmri.jmrit.logixng.digital.expressions.ExpressionLight;
import jmri.jmrit.logixng.digital.expressions.ExpressionSensor;
import jmri.jmrit.logixng.digital.expressions.ExpressionTurnout;
import jmri.jmrit.logixng.digital.expressions.False;
import jmri.jmrit.logixng.digital.expressions.Hold;
import jmri.jmrit.logixng.digital.expressions.Or;
import jmri.jmrit.logixng.digital.expressions.ResetOnTrue;
import jmri.jmrit.logixng.digital.expressions.Timer;
import jmri.jmrit.logixng.digital.expressions.TriggerOnce;
import jmri.jmrit.logixng.digital.expressions.True;
import jmri.jmrit.logixng.string.actions.StringActionMemory;
import jmri.jmrit.logixng.string.expressions.StringExpressionMemory;
import jmri.jmrit.logixng.Is_IsNot_Enum;
import jmri.managers.AbstractManager;
import jmri.util.FileUtil;
import jmri.util.Log4JUtil;
import jmri.util.ThreadingUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class providing the basic logic of the LogixNG_Manager interface.
 * 
 * @author Daniel Bergqvist Copyright 2018
 */
public class DefaultLogixNGManager extends AbstractManager<LogixNG>
        implements LogixNG_Manager {

    // FOR TESTING ONLY. REMOVE LATER.
    private boolean hasRunOnce = false;
    
    DecimalFormat paddedNumber = new DecimalFormat("0000");

    int lastAutoLogixNGRef = 0;
    List<FemaleSocketFactory> _femaleSocketFactories = new ArrayList<>();
    
    
    public DefaultLogixNGManager() {
        super();
        
        // The LogixNGPreferences class may load plugins so we must ensure
        // it's loaded here.
        InstanceManager.getDefault(LogixNGPreferences.class);
    }

    @Override
    public int getXMLOrder() {
        return LOGIXNGS;
    }

    @Override
    public String getBeanTypeHandled() {
        return Bundle.getMessage("BeanNameLogixNG");
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
        if (systemName.matches(getSystemNamePrefix()+":?\\d+")) {
            return NameValidity.VALID;
        } else {
            return NameValidity.INVALID;
        }
    }

    /**
     * Method to create a new LogixNG if the LogixNG does not exist.
     * <p>
     * Returns null if
     * a Logix with the same systemName or userName already exists, or if there
     * is trouble creating a new LogixNG.
     */
    @Override
    public LogixNG createLogixNG(String systemName, String userName)
            throws IllegalArgumentException {
        
        // Check that LogixNG does not already exist
        LogixNG x;
        if (userName != null && !userName.equals("")) {
            x = getByUserName(userName);
            if (x != null) {
                return null;
            }
        }
        x = getBySystemName(systemName);
        if (x != null) {
            return null;
        }
        // Check if system name is valid
        if (this.validSystemNameFormat(systemName) != NameValidity.VALID) {
            throw new IllegalArgumentException("SystemName " + systemName + " is not in the correct format");
        }
        // LogixNG does not exist, create a new LogixNG
        x = new DefaultLogixNG(systemName, userName);
        // save in the maps
        register(x);

        /* The following keeps track of the last created auto system name.
         currently we do not reuse numbers, although there is nothing to stop the
         user from manually recreating them */
        if (systemName.startsWith(getSystemNamePrefix()+":")) {
            try {
                int autoNumber = Integer.parseInt(systemName.substring(5));
                if (autoNumber > lastAutoLogixNGRef) {
                    lastAutoLogixNGRef = autoNumber;
                }
            } catch (NumberFormatException e) {
                log.warn("Auto generated SystemName " + systemName + " is not in the correct format");
            }
        }
        
//        if (setupTree) {
            // Setup initial tree for the LogixNG
//            setupInitialConditionalNGTree(x);
//            throw new UnsupportedOperationException("Throw exception for now until this is fixed");
//        }
        
        return x;
    }

    @Override
    public LogixNG createLogixNG(String userName) throws IllegalArgumentException {
        int nextAutoLogixNGRef = lastAutoLogixNGRef + 1;
        StringBuilder b = new StringBuilder(getSystemNamePrefix()+":");
        String nextNumber = paddedNumber.format(nextAutoLogixNGRef);
        b.append(nextNumber);
        return createLogixNG(b.toString(), userName);
    }

    @Override
    public void setupInitialConditionalNGTree(ConditionalNG conditionalNG) {
        try {
            FemaleSocket femaleSocket = conditionalNG.getFemaleSocket();
            MaleDigitalActionSocket actionManySocket =
                    InstanceManager.getDefault(DigitalActionManager.class).registerAction(new Many(getSystemNamePrefix()+"DA:00001"));
            femaleSocket.connect(actionManySocket);
            femaleSocket.setLock(Base.Lock.HARD_LOCK);

            femaleSocket = actionManySocket.getChild(0);
            MaleDigitalActionSocket actionHoldAnythingSocket =
                    InstanceManager.getDefault(DigitalActionManager.class).registerAction(new HoldAnything(getSystemNamePrefix()+"DA:00002"));
            femaleSocket.connect(actionHoldAnythingSocket);
            femaleSocket.setLock(Base.Lock.HARD_LOCK);

            femaleSocket = actionManySocket.getChild(1);
            MaleDigitalActionSocket actionIfThenSocket =
                    InstanceManager.getDefault(DigitalActionManager.class)
                            .registerAction(new IfThen(getSystemNamePrefix()+"DA:00003", IfThen.Type.TRIGGER_ACTION));
            femaleSocket.connect(actionIfThenSocket);

            /* FOR TESTING ONLY */
            /* FOR TESTING ONLY */
            /* FOR TESTING ONLY */
            /* FOR TESTING ONLY */
/*            
            femaleSocket = actionIfThenSocket.getChild(0);
            MaleDigitalExpressionSocket expressionAndSocket =
                    InstanceManager.getDefault(DigitalExpressionManager.class)
                            .registerExpression(new And(femaleSocket.getConditionalNG()));
            femaleSocket.connect(expressionAndSocket);
            
            femaleSocket = actionIfThenSocket.getChild(1);
            MaleDigitalActionSocket actionIfThenSocket2 =
                    InstanceManager.getDefault(DigitalActionManager.class)
                            .registerAction(new IfThen(femaleSocket.getConditionalNG(), IfThen.Type.CONTINOUS_ACTION));
            femaleSocket.connect(actionIfThenSocket2);
*/            
            /* FOR TESTING ONLY */
            /* FOR TESTING ONLY */
            /* FOR TESTING ONLY */
            /* FOR TESTING ONLY */

        } catch (SocketAlreadyConnectedException e) {
            // This should never be able to happen.
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public LogixNG getLogixNG(String name) {
        LogixNG x = getByUserName(name);
        if (x != null) {
            return x;
        }
        return getBySystemName(name);
    }

    @Override
    public LogixNG getByUserName(String name) {
        return _tuser.get(name);
    }

    @Override
    public LogixNG getBySystemName(String name) {
        return _tsys.get(name);
    }

    /** {@inheritDoc} */
    @Override
    public String getBeanTypeHandled(boolean plural) {
        return Bundle.getMessage(plural ? "BeanNameLogixNGs" : "BeanNameLogixNG");
    }

//    @Override
//    public MaleDigitalActionSocket createMaleActionSocket(DigitalAction action) {
//        return new DefaultMaleActionSocket(action);
//    }

//    @Override
//    public MaleDigitalExpressionSocket createMaleExpressionSocket(DigitalExpression expression) {
//        return new DefaultMaleExpressionSocket(expression);
//    }

    /** {@inheritDoc} */
    @Override
    public void resolveAllTrees() {
        for (LogixNG logixNG : _tsys.values()) {
            logixNG.setParentForAllChildren();
        }
    }
    
    @SuppressFBWarnings(value = {"DM_EXIT", "DMI_HARDCODED_ABSOLUTE_FILENAME"},
            justification = "This is a test method that must be removed before merging this PR")
    public void testLogixNGs() throws PropertyVetoException {
        
        // FOR TESTING ONLY. REMOVE LATER.
        if (1==0) {
            if ((!hasRunOnce) && (!GraphicsEnvironment.isHeadless())) {

                hasRunOnce = true;
                
                for (jmri.Logix l : InstanceManager.getDefault(jmri.LogixManager.class).getNamedBeanSet()) {
                    String sysName = l.getSystemName();
                    if (!sysName.equals("SYS") && !sysName.startsWith("RTX")) {
                        jmri.jmrit.logixng.tools.ImportLogix il = new jmri.jmrit.logixng.tools.ImportLogix(l);
                        il.doImport();
                    }
                }
            }
        }
        
        // FOR TESTING ONLY. REMOVE LATER.
        int test = 1;
        if (test == 1) {
            int store = 1;
            int load = 1;
            try {
                if (store == 1) {
                    Light light1 = InstanceManager.getDefault(LightManager.class).provide("IL1_Daniel");
                    light1.setCommandedState(Light.OFF);
                    Light light2 = InstanceManager.getDefault(LightManager.class).provide("IL2_Daniel");
                    light2.setCommandedState(Light.OFF);
                    Sensor sensor1 = InstanceManager.getDefault(SensorManager.class).provide("IS1_Daniel");
                    sensor1.setCommandedState(Sensor.INACTIVE);
                    Sensor sensor2 = InstanceManager.getDefault(SensorManager.class).provide("IS2_Daniel");
                    sensor2.setCommandedState(Sensor.INACTIVE);
                    Turnout turnout1 = InstanceManager.getDefault(TurnoutManager.class).provide("IT1_Daniel");
                    turnout1.setCommandedState(Turnout.CLOSED);
                    Turnout turnout2 = InstanceManager.getDefault(TurnoutManager.class).provide("IT2_Daniel");
                    turnout2.setCommandedState(Turnout.CLOSED);
                    Turnout turnout3 = InstanceManager.getDefault(TurnoutManager.class).provide("IT3_Daniel");
                    turnout3.setCommandedState(Turnout.CLOSED);
                    Turnout turnout4 = InstanceManager.getDefault(TurnoutManager.class).provide("IT4_Daniel");
                    turnout4.setCommandedState(Turnout.CLOSED);
                    Turnout turnout5 = InstanceManager.getDefault(TurnoutManager.class).provide("IT5_Daniel");
                    turnout5.setCommandedState(Turnout.CLOSED);
    //                AtomicBoolean atomicBoolean = new AtomicBoolean(false);
                    LogixNG logixNG = InstanceManager.getDefault(LogixNG_Manager.class).createLogixNG("A logixNG");
                    ConditionalNG conditionalNG = new DefaultConditionalNG(logixNG.getSystemName()+":1");
                    InstanceManager.getDefault(LogixNG_Manager.class).setupInitialConditionalNGTree(conditionalNG);
                    
                    logixNG.addConditionalNG(conditionalNG);

//                    DigitalAction actionIfThen = new IfThen(conditionalNG, IfThen.Type.TRIGGER_ACTION);
//                    MaleSocket socketIfThen = InstanceManager.getDefault(DigitalActionManager.class).registerAction(actionIfThen);
//                    conditionalNG.getChild(0).connect(socketIfThen);

                    MaleSocket socketMany = conditionalNG.getChild(0).getConnectedSocket();
                    MaleSocket socketIfThen = socketMany.getChild(1).getConnectedSocket();
                    
                    Or expressionOr = new Or(getSystemNamePrefix()+"DE:00001");
                    MaleSocket socketOr = InstanceManager.getDefault(DigitalExpressionManager.class).registerExpression(expressionOr);
                    socketIfThen.getChild(0).connect(socketOr);
                    
                    int index = 0;
                    
                    Or expressionOr2 = new Or(getSystemNamePrefix()+"DE:00002", "My Or expression");
                    MaleSocket socketOr2 = InstanceManager.getDefault(DigitalExpressionManager.class).registerExpression(expressionOr2);
                    socketOr.getChild(index++).connect(socketOr2);
                    
                    And expressionAnd = new And(getSystemNamePrefix()+"DE:00003");
                    MaleSocket socketAnd = InstanceManager.getDefault(DigitalExpressionManager.class).registerExpression(expressionAnd);
                    socketOr.getChild(index++).connect(socketAnd);
                    
                    And expressionAnd2 = new And(getSystemNamePrefix()+"DE:00004", "My And expression");
                    MaleSocket socketAnd2 = InstanceManager.getDefault(DigitalExpressionManager.class).registerExpression(expressionAnd2);
                    socketOr.getChild(index++).connect(socketAnd2);
                    
                    ExpressionTurnout expressionTurnout3 = new ExpressionTurnout(getSystemNamePrefix()+"DE:00005");
                    expressionTurnout3.setTurnout(turnout3);
                    expressionTurnout3.setTurnoutState(ExpressionTurnout.TurnoutState.THROWN);
                    MaleSocket socketTurnout3 = InstanceManager.getDefault(DigitalExpressionManager.class).registerExpression(expressionTurnout3);
                    expressionAnd.getChild(0).connect(socketTurnout3);
                    
                    ExpressionTurnout expressionTurnout4 = new ExpressionTurnout(getSystemNamePrefix()+"DE:00006", "My new turnout");
                    expressionTurnout4.setTurnout(turnout4);
                    expressionTurnout4.setTurnoutState(ExpressionTurnout.TurnoutState.CLOSED);
                    expressionTurnout4.set_Is_IsNot(Is_IsNot_Enum.IS);
                    MaleSocket socketTurnout4 = InstanceManager.getDefault(DigitalExpressionManager.class).registerExpression(expressionTurnout4);
                    expressionAnd.getChild(1).connect(socketTurnout4);
                    
                    ExpressionTurnout expressionTurnout5 = new ExpressionTurnout(getSystemNamePrefix()+"DE:00007");
                    expressionTurnout5.setTurnout(turnout5);
                    expressionTurnout5.setTurnoutState(ExpressionTurnout.TurnoutState.OTHER);
                    expressionTurnout5.set_Is_IsNot(Is_IsNot_Enum.IS_NOT);
                    MaleSocket socketTurnout5 = InstanceManager.getDefault(DigitalExpressionManager.class).registerExpression(expressionTurnout5);
                    expressionAnd.getChild(2).connect(socketTurnout5);
                    
                    Antecedent expressionAntecedent = new Antecedent(getSystemNamePrefix()+"DE:00008", null, "R1");
                    MaleSocket socketAntecedent = InstanceManager.getDefault(DigitalExpressionManager.class).registerExpression(expressionAntecedent);
                    socketOr.getChild(index++).connect(socketAntecedent);
                    
                    Antecedent expressionAntecedent2 = new Antecedent(getSystemNamePrefix()+"DE:00009", "My Antecedent expression", "R1");
                    MaleSocket socketAntecedent2 = InstanceManager.getDefault(DigitalExpressionManager.class).registerExpression(expressionAntecedent2);
                    socketOr.getChild(index++).connect(socketAntecedent2);
                    
                    False expressionFalse = new False(getSystemNamePrefix()+"DE:00010");
                    MaleSocket socketFalse = InstanceManager.getDefault(DigitalExpressionManager.class).registerExpression(expressionFalse);
                    socketOr.getChild(index++).connect(socketFalse);
                    
                    False expressionFalse2 = new False(getSystemNamePrefix()+"DE:00011", "My False expression");
                    MaleSocket socketFalse2 = InstanceManager.getDefault(DigitalExpressionManager.class).registerExpression(expressionFalse2);
                    socketOr.getChild(index++).connect(socketFalse2);
                    
                    Hold expressionHold = new Hold(getSystemNamePrefix()+"DE:00012");
                    MaleSocket socketHold = InstanceManager.getDefault(DigitalExpressionManager.class).registerExpression(expressionHold);
                    socketOr.getChild(index++).connect(socketHold);
                    
                    Hold expressionHold2 = new Hold(getSystemNamePrefix()+"DE:00013", "My Hold expression");
                    MaleSocket socketHold2 = InstanceManager.getDefault(DigitalExpressionManager.class).registerExpression(expressionHold2);
                    socketOr.getChild(index++).connect(socketHold2);
                    
                    ResetOnTrue expressionResetOnTrue = new ResetOnTrue(getSystemNamePrefix()+"DE:00016", null);
                    MaleSocket socketResetOnTrue = InstanceManager.getDefault(DigitalExpressionManager.class).registerExpression(expressionResetOnTrue);
                    socketOr.getChild(index++).connect(socketResetOnTrue);
                    
                    ResetOnTrue expressionResetOnTrue2 = new ResetOnTrue(getSystemNamePrefix()+"DE:00017", "My ResetOnTrue expression");
                    MaleSocket socketResetOnTrue2 = InstanceManager.getDefault(DigitalExpressionManager.class).registerExpression(expressionResetOnTrue2);
                    socketOr.getChild(index++).connect(socketResetOnTrue2);
                    
                    Timer expressionTimer = new Timer(getSystemNamePrefix()+"DE:000020", null);
                    MaleSocket socketTimer = InstanceManager.getDefault(DigitalExpressionManager.class).registerExpression(expressionTimer);
                    socketOr.getChild(index++).connect(socketTimer);
                    
                    Timer expressionTimer2 = new Timer(getSystemNamePrefix()+"DE:000021", "My Timer expression");
                    MaleSocket socketTimer2 = InstanceManager.getDefault(DigitalExpressionManager.class).registerExpression(expressionTimer2);
                    socketOr.getChild(index++).connect(socketTimer2);
                    
                    TriggerOnce expressionTriggerOnce = new TriggerOnce(getSystemNamePrefix()+"DE:000022", null);
                    MaleSocket socketTriggerOnce = InstanceManager.getDefault(DigitalExpressionManager.class).registerExpression(expressionTriggerOnce);
                    socketOr.getChild(index++).connect(socketTriggerOnce);
                    
                    TriggerOnce expressionTriggerOnce2 = new TriggerOnce(getSystemNamePrefix()+"DE:000023", "My TriggerOnce expression");
                    MaleSocket socketTriggerOnce2 = InstanceManager.getDefault(DigitalExpressionManager.class).registerExpression(expressionTriggerOnce2);
                    socketOr.getChild(index++).connect(socketTriggerOnce2);
                    
                    True expressionTrue = new True(getSystemNamePrefix()+"DE:000025");
                    MaleSocket socketTrue = InstanceManager.getDefault(DigitalExpressionManager.class).registerExpression(expressionTrue);
                    socketOr.getChild(index++).connect(socketTrue);
                    
                    True expressionTrue2 = new True(getSystemNamePrefix()+"DE:000026", "My True expression");
                    MaleSocket socketTrue2 = InstanceManager.getDefault(DigitalExpressionManager.class).registerExpression(expressionTrue2);
                    socketOr.getChild(index++).connect(socketTrue2);
                    
                    ExpressionLight expressionLight = new ExpressionLight(getSystemNamePrefix()+"DE:000013");
                    expressionLight.setLight(light1);
                    expressionLight.set_Is_IsNot(Is_IsNot_Enum.IS);
                    expressionLight.setLightState(ExpressionLight.LightState.ON);
                    MaleSocket socketLight = InstanceManager.getDefault(DigitalExpressionManager.class).registerExpression(expressionLight);
                    socketOr.getChild(index++).connect(socketLight);
                    
                    ExpressionLight expressionLight2 = new ExpressionLight(getSystemNamePrefix()+"DE:000014", "My light");
                    expressionLight2.setLight((Light)null);
                    expressionLight2.set_Is_IsNot(Is_IsNot_Enum.IS);
                    expressionLight2.setLightState(ExpressionLight.LightState.ON);
                    MaleSocket socketLight2 = InstanceManager.getDefault(DigitalExpressionManager.class).registerExpression(expressionLight2);
                    socketOr.getChild(index++).connect(socketLight2);
                    
                    ExpressionSensor expressionSensor = new ExpressionSensor(getSystemNamePrefix()+"DE:000015");
                    expressionSensor.setSensor(sensor1);
                    expressionSensor.set_Is_IsNot(Is_IsNot_Enum.IS);
                    expressionSensor.setSensorState(ExpressionSensor.SensorState.ACTIVE);
                    MaleSocket socketSensor = InstanceManager.getDefault(DigitalExpressionManager.class).registerExpression(expressionSensor);
                    socketOr.getChild(index++).connect(socketSensor);
                    
                    ExpressionSensor expressionSensor2 = new ExpressionSensor(getSystemNamePrefix()+"DE:000016", "My sensor");
                    expressionSensor2.setSensor((Sensor)null);
                    expressionSensor2.set_Is_IsNot(Is_IsNot_Enum.IS);
                    expressionSensor2.setSensorState(ExpressionSensor.SensorState.ACTIVE);
                    MaleSocket socketSensor2 = InstanceManager.getDefault(DigitalExpressionManager.class).registerExpression(expressionSensor2);
                    socketOr.getChild(index++).connect(socketSensor2);
                    
                    ExpressionTurnout expressionTurnout = new ExpressionTurnout(getSystemNamePrefix()+"DE:000017");
                    expressionTurnout.setTurnout(turnout1);
                    expressionTurnout.set_Is_IsNot(Is_IsNot_Enum.IS);
                    expressionTurnout.setTurnoutState(ExpressionTurnout.TurnoutState.THROWN);
                    MaleSocket socketTurnout = InstanceManager.getDefault(DigitalExpressionManager.class).registerExpression(expressionTurnout);
                    socketOr.getChild(index++).connect(socketTurnout);
                    
                    ExpressionTurnout expressionTurnout2 = new ExpressionTurnout(getSystemNamePrefix()+"DE:000018", "My turnout");
                    expressionTurnout2.setTurnout((Turnout)null);
                    expressionTurnout2.set_Is_IsNot(Is_IsNot_Enum.IS);
                    expressionTurnout2.setTurnoutState(ExpressionTurnout.TurnoutState.THROWN);
                    MaleSocket socketTurnout2 = InstanceManager.getDefault(DigitalExpressionManager.class).registerExpression(expressionTurnout2);
                    socketOr.getChild(index++).connect(socketTurnout2);
                    
                    
                    
                    Many expressionMany = new Many(getSystemNamePrefix()+"DA:00010");
                    MaleSocket socketSecondMany = InstanceManager.getDefault(DigitalActionManager.class).registerAction(expressionMany);
                    socketIfThen.getChild(1).connect(socketSecondMany);
                    
                    index = 0;
                    
                    HoldAnything actionHoldAnything = new HoldAnything(getSystemNamePrefix()+"DA:00011", "My hold anything");
                    MaleSocket socketHoldAnything = InstanceManager.getDefault(DigitalActionManager.class).registerAction(actionHoldAnything);
                    socketSecondMany.getChild(index++).connect(socketHoldAnything);
                    
                    IfThen actionIfThen2 = new IfThen(getSystemNamePrefix()+"DA:10011", "My if then", IfThen.Type.TRIGGER_ACTION);
                    MaleSocket socketIfThen2 = InstanceManager.getDefault(DigitalActionManager.class).registerAction(actionIfThen2);
                    socketSecondMany.getChild(index++).connect(socketIfThen2);
                    
                    Many actionMany = new Many(getSystemNamePrefix()+"DA:10012", "My many");
                    MaleSocket socketMany2 = InstanceManager.getDefault(DigitalActionManager.class).registerAction(actionMany);
                    socketSecondMany.getChild(index++).connect(socketMany2);
                    
                    ShutdownComputer actionShutdownComputer = new ShutdownComputer(getSystemNamePrefix()+"DA:10013", "My shutdown computer", 10);
                    MaleSocket socketShutdownComputer = InstanceManager.getDefault(DigitalActionManager.class).registerAction(actionShutdownComputer);
                    socketSecondMany.getChild(index++).connect(socketShutdownComputer);
                    
                    DoAnalogAction actionDoAnalogAction = new DoAnalogAction(getSystemNamePrefix()+"DA:10014");
                    MaleSocket socketDoAnalogAction = InstanceManager.getDefault(DigitalActionManager.class).registerAction(actionDoAnalogAction);
                    socketSecondMany.getChild(index++).connect(socketDoAnalogAction);
                    
                    DoStringAction actionDoStringAction = new DoStringAction(getSystemNamePrefix()+"DA:00012");
                    MaleSocket socketDoStringAction = InstanceManager.getDefault(DigitalActionManager.class).registerAction(actionDoStringAction);
                    socketSecondMany.getChild(index++).connect(socketDoStringAction);
                    
                    ShutdownComputer expressionShutdownComputer = new ShutdownComputer(getSystemNamePrefix()+"DA:00013", 10);
                    MaleSocket socketShutdownComputer2 = InstanceManager.getDefault(DigitalActionManager.class).registerAction(expressionShutdownComputer);
                    socketSecondMany.getChild(index++).connect(socketShutdownComputer2);
                    
                    ActionLight actionLight = new ActionLight(getSystemNamePrefix()+"DA:00014");
                    actionLight.setLight(light2);
                    actionLight.setLightState(ActionLight.LightState.ON);
                    socketLight2 = InstanceManager.getDefault(DigitalActionManager.class).registerAction(actionLight);
                    socketSecondMany.getChild(index++).connect(socketLight2);
                    
                    actionLight = new ActionLight(getSystemNamePrefix()+"DA:10015", "My light action");
                    actionLight.setLight(light2);
                    actionLight.setLightState(ActionLight.LightState.ON);
                    socketLight2 = InstanceManager.getDefault(DigitalActionManager.class).registerAction(actionLight);
                    socketSecondMany.getChild(index++).connect(socketLight2);
                    
                    ActionSensor actionSensor = new ActionSensor(getSystemNamePrefix()+"DA:00015");
                    actionSensor.setSensor(sensor2);
                    actionSensor.setSensorState(ActionSensor.SensorState.ACTIVE);
                    socketSensor2 = InstanceManager.getDefault(DigitalActionManager.class).registerAction(actionSensor);
                    socketSecondMany.getChild(index++).connect(socketSensor2);
                    
                    actionSensor = new ActionSensor(getSystemNamePrefix()+"DA:10016", "My sensor action");
                    actionSensor.setSensor(sensor2);
                    actionSensor.setSensorState(ActionSensor.SensorState.ACTIVE);
                    socketSensor2 = InstanceManager.getDefault(DigitalActionManager.class).registerAction(actionSensor);
                    socketSecondMany.getChild(index++).connect(socketSensor2);
                    
                    ActionTurnout actionTurnout = new ActionTurnout(getSystemNamePrefix()+"DA:00016");
                    actionTurnout.setTurnout(turnout2);
                    actionTurnout.setTurnoutState(ActionTurnout.TurnoutState.THROWN);
                    socketTurnout2 = InstanceManager.getDefault(DigitalActionManager.class).registerAction(actionTurnout);
                    socketSecondMany.getChild(index++).connect(socketTurnout2);
                    
                    actionTurnout = new ActionTurnout(getSystemNamePrefix()+"DA:00017", "My turnout action");
                    actionTurnout.setTurnout(turnout2);
                    actionTurnout.setTurnoutState(ActionTurnout.TurnoutState.THROWN);
                    socketTurnout2 = InstanceManager.getDefault(DigitalActionManager.class).registerAction(actionTurnout);
                    socketSecondMany.getChild(index++).connect(socketTurnout2);
                    
                    Memory memory1 = InstanceManager.getDefault(MemoryManager.class).provide("IM1");
                    Memory memory2 = InstanceManager.getDefault(MemoryManager.class).provide("IM2");
                    Memory memory3 = InstanceManager.getDefault(MemoryManager.class).provide("IM3");
                    Memory memory4 = InstanceManager.getDefault(MemoryManager.class).provide("IM4");
                    
                    AnalogExpressionMemory analogExpressionMemory = new AnalogExpressionMemory(getSystemNamePrefix()+"AE:00001");
                    analogExpressionMemory.setMemory(memory1);
                    MaleSocket socketAnalogExpressionMemory = InstanceManager.getDefault(AnalogExpressionManager.class).registerExpression(analogExpressionMemory);

                    AnalogActionMemory analogActionMemory = new AnalogActionMemory(getSystemNamePrefix()+"AA:00001");
                    analogActionMemory.setMemory(memory2);
                    MaleSocket socketAnalogActionMemory = InstanceManager.getDefault(AnalogActionManager.class).registerAction(analogActionMemory);

                    DoAnalogAction doAnalogAction = new DoAnalogAction(getSystemNamePrefix()+"DA:00101", "My do analog action");
                    doAnalogAction.setAnalogExpressionSocketSystemName(socketAnalogExpressionMemory.getSystemName());
                    doAnalogAction.setAnalogActionSocketSystemName(socketAnalogActionMemory.getSystemName());
                    MaleSocket socket = InstanceManager.getDefault(DigitalActionManager.class).registerAction(doAnalogAction);
                    socketSecondMany.getChild(index++).connect(socket);
                    
                    analogExpressionMemory = new AnalogExpressionMemory(getSystemNamePrefix()+"AE:00002", "My expression");
                    socketAnalogExpressionMemory = InstanceManager.getDefault(AnalogExpressionManager.class).registerExpression(analogExpressionMemory);

                    analogActionMemory = new AnalogActionMemory(getSystemNamePrefix()+"AA:00002", "My action");
                    socketAnalogActionMemory = InstanceManager.getDefault(AnalogActionManager.class).registerAction(analogActionMemory);

                    doAnalogAction = new DoAnalogAction(getSystemNamePrefix()+"DA:00102");
                    doAnalogAction.setAnalogExpressionSocketSystemName(socketAnalogExpressionMemory.getSystemName());
                    doAnalogAction.setAnalogActionSocketSystemName(socketAnalogActionMemory.getSystemName());
                    socket = InstanceManager.getDefault(DigitalActionManager.class).registerAction(doAnalogAction);
                    socketSecondMany.getChild(index++).connect(socket);
                    
                    StringExpressionMemory stringExpressionMemory = new StringExpressionMemory(getSystemNamePrefix()+"SE:00001");
                    stringExpressionMemory.setMemory(memory3);
                    MaleSocket socketStringExpressionMemory = InstanceManager.getDefault(StringExpressionManager.class).registerExpression(stringExpressionMemory);

                    StringActionMemory stringActionMemory = new StringActionMemory(getSystemNamePrefix()+"SA:00001");
                    stringActionMemory.setMemory(memory4);
                    MaleSocket socketStringActionMemory = InstanceManager.getDefault(StringActionManager.class).registerAction(stringActionMemory);

                    DoStringAction doStringAction = new DoStringAction(getSystemNamePrefix()+"DA:00103", "My do string action");
                    doStringAction.setStringExpressionSocketSystemName(socketStringExpressionMemory.getSystemName());
                    doStringAction.setStringActionSocketSystemName(socketStringActionMemory.getSystemName());
                    socket = InstanceManager.getDefault(DigitalActionManager.class).registerAction(doStringAction);
                    socketSecondMany.getChild(index++).connect(socket);

                    stringExpressionMemory = new StringExpressionMemory(getSystemNamePrefix()+"SE:00002", "My expression");
                    socketStringExpressionMemory = InstanceManager.getDefault(StringExpressionManager.class).registerExpression(stringExpressionMemory);

                    stringActionMemory = new StringActionMemory(getSystemNamePrefix()+"SA:00002", "My action");
                    socketStringActionMemory = InstanceManager.getDefault(StringActionManager.class).registerAction(stringActionMemory);

                    doStringAction = new DoStringAction(getSystemNamePrefix()+"DA:00104");
                    doStringAction.setStringExpressionSocketSystemName(socketStringExpressionMemory.getSystemName());
                    doStringAction.setStringActionSocketSystemName(socketStringActionMemory.getSystemName());
                    socket = InstanceManager.getDefault(DigitalActionManager.class).registerAction(doStringAction);
                    socketSecondMany.getChild(index++).connect(socket);

                    logixNG.setEnabled(true);
                    conditionalNG.setEnabled(true);
    
//                    logixNG.activateLogixNG();
                }
                
                // Store panels
                jmri.ConfigureManager cm = InstanceManager.getNullableDefault(jmri.ConfigureManager.class);
                if (cm == null) {
                    log.error("Failed to make backup due to unable to get default configure manager");
                } else {
                    FileUtil.createDirectory(FileUtil.getUserFilesPath() + "temp");
                    File file = new File(FileUtil.getUserFilesPath() + "temp/" + "LogixNG.xml");
                    System.out.format("Temporary file: %s%n", file.getAbsoluteFile());
//                    java.io.File file = new java.io.File("F:\\temp\\DanielTestarLogixNG.xml");
//                    cm.makeBackup(file);
                    // and finally store
                    
                    if (store == 1) {
                        boolean results = cm.storeUser(file);
                        log.debug(results ? "store was successful" : "store failed");
                        if (!results) {
                            log.error("Failed to store panel");
                            System.exit(-1);
                        }
                    }
                    
                    if (load == 1) {
                        
                        for (LogixNG logixNG : InstanceManager.getDefault(LogixNG_Manager.class).getNamedBeanSet()) {
                            InstanceManager.getDefault(LogixNG_Manager.class).deleteLogixNG(logixNG);
                        }
                        java.util.SortedSet<MaleAnalogActionSocket> set1 = InstanceManager.getDefault(AnalogActionManager.class).getNamedBeanSet();
                        List<MaleSocket> l = new ArrayList<>(set1);
                        for (MaleSocket x1 : l) {
                            InstanceManager.getDefault(AnalogActionManager.class).deleteBean((MaleAnalogActionSocket)x1, "DoDelete");
                        }
                        java.util.SortedSet<MaleAnalogExpressionSocket> set2 = InstanceManager.getDefault(AnalogExpressionManager.class).getNamedBeanSet();
                        l = new ArrayList<>(set2);
                        for (MaleSocket x2 : l) {
                            InstanceManager.getDefault(AnalogExpressionManager.class).deleteBean((MaleAnalogExpressionSocket)x2, "DoDelete");
                        }
                        java.util.SortedSet<MaleDigitalActionSocket> set3 = InstanceManager.getDefault(DigitalActionManager.class).getNamedBeanSet();
                        l = new ArrayList<>(set3);
                        for (MaleSocket x3 : l) {
                            InstanceManager.getDefault(DigitalActionManager.class).deleteBean((MaleDigitalActionSocket)x3, "DoDelete");
                        }
                        java.util.SortedSet<MaleDigitalExpressionSocket> set4 = InstanceManager.getDefault(DigitalExpressionManager.class).getNamedBeanSet();
                        l = new ArrayList<>(set4);
                        for (MaleSocket x4 : l) {
                            InstanceManager.getDefault(DigitalExpressionManager.class).deleteBean((MaleDigitalExpressionSocket)x4, "DoDelete");
                        }
                        java.util.SortedSet<MaleStringActionSocket> set5 = InstanceManager.getDefault(StringActionManager.class).getNamedBeanSet();
                        l = new ArrayList<>(set5);
                        for (MaleSocket x5 : l) {
                            InstanceManager.getDefault(StringActionManager.class).deleteBean((MaleStringActionSocket)x5, "DoDelete");
                        }
                        java.util.SortedSet<MaleStringExpressionSocket> set6 = InstanceManager.getDefault(StringExpressionManager.class).getNamedBeanSet();
                        l = new ArrayList<>(set6);
                        for (MaleSocket x6 : l) {
                            InstanceManager.getDefault(StringExpressionManager.class).deleteBean((MaleStringExpressionSocket)x6, "DoDelete");
                        }
                        
                        boolean results = cm.load(file);
                        log.debug(results ? "load was successful" : "store failed");
                        if (results) {
                            resolveAllTrees();
                            setupAllLogixNGs();
                        } else {
                            throw new RuntimeException("Failed to load panel");
//                            log.error("Failed to load panel");
//                            System.exit(-1);
                        }
                    }
                }
                
            } catch (JmriException ex) {
                log.error("Failed to store panel", ex);
                System.exit(-1);
            }
        }
    }
    
    /** {@inheritDoc} */
    @Override
    public void setupAllLogixNGs() {
        for (LogixNG logixNG : _tsys.values()) {
            logixNG.setup();
        }
    }

    @Override
    public void deleteLogixNG(LogixNG x) {
        // delete the LogixNG
        deregister(x);
        x.dispose();
    }

    @Override
    public void setLoadDisabled(boolean s) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    static volatile DefaultLogixNGManager _instance = null;

    @InvokeOnGuiThread  // this method is not thread safe
    static public DefaultLogixNGManager instance() {
        if (log.isDebugEnabled()) {
            if (!ThreadingUtil.isGUIThread()) {
                Log4JUtil.warnOnce(log, "instance() called on wrong thread");
            }
        }
        
        if (_instance == null) {
            _instance = new DefaultLogixNGManager();
        }
        return (_instance);
    }

    @Override
    public void registerFemaleSocketFactory(FemaleSocketFactory factory) {
        _femaleSocketFactories.add(factory);
    }

    @Override
    public List<FemaleSocketFactory> getFemaleSocketFactories() {
        return new ArrayList<>(_femaleSocketFactories);
    }

    private final static Logger log = LoggerFactory.getLogger(DefaultLogixNGManager.class);
}
