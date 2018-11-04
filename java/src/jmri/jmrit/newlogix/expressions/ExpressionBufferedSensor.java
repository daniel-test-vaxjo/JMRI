package jmri.jmrit.newlogix.expressions;

/**
 * An expression that buffers the changes of a sensor.
 * 
 * A sensor, like an optical sensor looking at wheel axles or a button switch
 * may be "on" or "off" a very short time and in some cases that time might be
 * too short. This expression remembers the new state of a sensor and triggers
 * the NewLogixs again.
 * 
 * Each time a change of the sensor is notified which this expression should
 * trigger on, this expression notifies the NewLogixManager that it should
 * run all NewLogixs again. If NewLogixManager is already running the NewLogixs,
 * the expression waits to notify the NewLogixManager until it's done.
 * 
 * The expression remembers the state of the sensor until the NewLogixs has been
 * run.
 */
public class ExpressionBufferedSensor {

    // For this expression to work, the NewLogix engine needs to notify those
    // who wants to be notified when all NewLogixs are finished running.
    
    // Enum Trigger: ON_TRUE, ON_FALSE, ON_CHANGE
    // Put this enum in Expression since it may be used by many expressions
    
}
