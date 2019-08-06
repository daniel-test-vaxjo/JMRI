package jmri.jmrit.logixng.analog.expressions;

import java.util.AbstractMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import jmri.jmrit.logixng.AnalogExpressionFactory;
import jmri.jmrit.logixng.Base;
import jmri.jmrit.logixng.Category;
import org.openide.util.lookup.ServiceProvider;

/**
 * The factory for DigitalAction classes.
 */
@ServiceProvider(service = AnalogExpressionFactory.class)
public class Factory implements AnalogExpressionFactory {

    @Override
    public Set<Map.Entry<Category, Class<? extends Base>>> getClasses() {
        Set<Map.Entry<Category, Class<? extends Base>>> analogExpressionClasses = new HashSet<>();
        analogExpressionClasses.add(new AbstractMap.SimpleEntry<>(Category.ITEM, AnalogExpressionMemory.class));
        return analogExpressionClasses;
    }

}
