package jmri.jmrit.logixng.string.actions;

import java.util.AbstractMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import jmri.jmrit.logixng.Base;
import jmri.jmrit.logixng.Category;
import jmri.jmrit.logixng.StringActionFactory;
import org.openide.util.lookup.ServiceProvider;

/**
 * The factory for DigitalAction classes.
 */
@ServiceProvider(service = StringActionFactory.class)
public class Factory implements StringActionFactory {

    @Override
    public Set<Map.Entry<Category, Class<? extends Base>>> getClasses() {
        Set<Map.Entry<Category, Class<? extends Base>>> stringActionClasses = new HashSet<>();
        stringActionClasses.add(new AbstractMap.SimpleEntry<>(Category.ITEM, StringActionMemory.class));
        return stringActionClasses;
    }

}
