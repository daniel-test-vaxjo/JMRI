package jmri.jmrit.logixng.analog.actions;

import java.util.AbstractMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import jmri.jmrit.logixng.AnalogActionFactory;
import jmri.jmrit.logixng.Base;
import jmri.jmrit.logixng.Category;
import org.openide.util.lookup.ServiceProvider;

/**
 * The factory for AnalogAction classes.
 */
@ServiceProvider(service = AnalogActionFactory.class)
public class Factory implements AnalogActionFactory {

    @Override
    public Set<Map.Entry<Category, Class<? extends Base>>> getClasses() {
        Set<Map.Entry<Category, Class<? extends Base>>> analogActionClasses = new HashSet<>();
        analogActionClasses.add(new AbstractMap.SimpleEntry<>(Category.ITEM, AnalogActionMemory.class));
        return analogActionClasses;
    }

}
