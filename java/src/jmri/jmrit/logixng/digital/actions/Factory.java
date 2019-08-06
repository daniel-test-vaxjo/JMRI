package jmri.jmrit.logixng.digital.actions;

import java.util.AbstractMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import jmri.jmrit.logixng.Base;
import jmri.jmrit.logixng.Category;
import jmri.jmrit.logixng.DigitalActionFactory;
import org.openide.util.lookup.ServiceProvider;

/**
 * The factory for DigitalAction classes.
 */
@ServiceProvider(service = DigitalActionFactory.class)
public class Factory implements DigitalActionFactory {

    @Override
    public Set<Map.Entry<Category, Class<? extends Base>>> getClasses() {
        Set<Map.Entry<Category, Class<? extends Base>>> digitalActionClasses = new HashSet<>();
        digitalActionClasses.add(new AbstractMap.SimpleEntry<>(Category.COMMON, IfThen.class));
        digitalActionClasses.add(new AbstractMap.SimpleEntry<>(Category.COMMON, Many.class));
        digitalActionClasses.add(new AbstractMap.SimpleEntry<>(Category.COMMON, HoldAnything.class));
        digitalActionClasses.add(new AbstractMap.SimpleEntry<>(Category.COMMON, DoAnalogAction.class));
        digitalActionClasses.add(new AbstractMap.SimpleEntry<>(Category.COMMON, DoStringAction.class));
        digitalActionClasses.add(new AbstractMap.SimpleEntry<>(Category.ITEM, ActionLight.class));
        digitalActionClasses.add(new AbstractMap.SimpleEntry<>(Category.ITEM, ActionSensor.class));
        digitalActionClasses.add(new AbstractMap.SimpleEntry<>(Category.ITEM, ActionTurnout.class));
        digitalActionClasses.add(new AbstractMap.SimpleEntry<>(Category.EXRAVAGANZA, ShutdownComputer.class));
        return digitalActionClasses;
    }

}
