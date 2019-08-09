package jmri.jmrit.logixng.digital.actions;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import jmri.InstanceManager;
import jmri.JmriException;
import jmri.Turnout;
import jmri.TurnoutManager;
import jmri.jmrit.logixng.DigitalActionManager;
import jmri.jmrit.logixng.MaleSocket;
import jmri.util.JUnitUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test ActionTimer
 * 
 * @author Daniel Bergqvist 2019
 */
public class ActionTimerTest extends AbstractDigitalActionTestBase {

    @Test
    public void testCtor() {
        ActionTimer t = new ActionTimer("IQDA321");
        Assert.assertNotNull("exists",t);
        t = new ActionTimer("IQDA321", null);
        Assert.assertNotNull("exists",t);
    }
    
    @Test
    public void testToString() {
        ActionTimer a1 = new ActionTimer("IQDA321", null);
        Assert.assertEquals("strings are equal", "Execute after delay", a1.getShortDescription());
        ActionTimer a2 = new ActionTimer("IQDA321", null);
        Assert.assertEquals("strings are equal", "Execute A after 0 milliseconds", a2.getLongDescription());
    }
    
    @Test
    public void testTimer() throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException, JmriException {
        ActionTimer t = new ActionTimer("IQDA1");
        Assert.assertNotNull("exists",t);
        
        // Set field t._timer to accessible and remove "final" modifier
        Field timerField = t.getClass().getDeclaredField("_timer");
        timerField.setAccessible(true);
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(timerField, timerField.getModifiers() & ~Modifier.FINAL);
        
        MyTimer myTimer = new MyTimer();
        // Set the field t._timer to the new timer
        timerField.set(t, myTimer);
        
        Turnout turnout = InstanceManager.getDefault(TurnoutManager.class).provideTurnout("IT1");
        turnout.setState(Turnout.CLOSED);
        ActionTurnout actionTurnout = new ActionTurnout("IQDA2");
        actionTurnout.setTurnout(turnout);
        actionTurnout.setTurnoutState(ActionTurnout.TurnoutState.THROWN);
        MaleSocket actionTurnoutSocket =
                InstanceManager.getDefault(DigitalActionManager.class)
                        .registerAction(actionTurnout);
        t.getThenActionSocket().connect(actionTurnoutSocket);
        Assert.assertTrue("turnout is closed", Turnout.CLOSED == turnout.getState());
        t.execute();
        Assert.assertTrue("turnout is closed", Turnout.CLOSED == turnout.getState());
        myTimer.triggerTimer();     // Simulate timer has reached its time
        Assert.assertTrue("turnout is thrown", Turnout.THROWN == turnout.getState());
    }
    
    // The minimal setup for log4J
    @Before
    public void setUp() {
        JUnitUtil.setUp();
        JUnitUtil.resetProfileManager();
        JUnitUtil.resetInstanceManager();
        JUnitUtil.initInternalSensorManager();
        JUnitUtil.initInternalTurnoutManager();
        _base = new ActionTimer("IQDA321");
    }

    @After
    public void tearDown() {
        JUnitUtil.tearDown();
    }
    
    
    
    
    public class MyTimer extends Timer {
        
        private TimerTask _task;
        private long _delay;
        
        @Override
        public void schedule(TimerTask task, long delay) {
            if (_task != null) {
                throw new RuntimeException("Only one task at the time can be executed");
            }
            _task = task;
            _delay = delay;
        }
        
        @Override
        public void schedule(TimerTask task, Date time) {
            throw new UnsupportedOperationException("this method is not supported");
        }
        
        @Override
        public void schedule(TimerTask task, long delay, long period) {
            throw new UnsupportedOperationException("this method is not supported");
        }
        
        @Override
        public void schedule(TimerTask task, Date firstTime, long period) {
            throw new UnsupportedOperationException("this method is not supported");
        }
        
        @Override
        public void scheduleAtFixedRate(TimerTask task, long delay, long period) {
            throw new UnsupportedOperationException("this method is not supported");
        }
        
        @Override
        public void scheduleAtFixedRate(TimerTask task, Date firstTime, long period) {
            throw new UnsupportedOperationException("this method is not supported");
        }
        
        @Override
        public void cancel() {
            _task = null;
            _delay = 0;
        }
        
        @Override
        public int purge() {
            return 0;
        }
        
        public void triggerTimer() {
            _task.run();
        }
        
    }
    
}
