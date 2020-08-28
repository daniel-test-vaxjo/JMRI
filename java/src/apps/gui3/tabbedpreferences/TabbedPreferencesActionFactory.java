package apps.gui3.tabbedpreferences;

import java.util.Locale;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jmri.util.startup.AbstractStartupActionFactory;
import jmri.util.startup.StartupActionFactory;
import org.openide.util.lookup.ServiceProvider;

/**
 * Preferences startup action factory.
 * 
 * @author Randall Wood Copyright 2020
 */
@ServiceProvider(service = StartupActionFactory.class)
public final class TabbedPreferencesActionFactory extends AbstractStartupActionFactory {

    @Override
    public String getTitle(Class<?> clazz, @Nullable Locale locale) throws IllegalArgumentException {    // Spotbugs warns that Locale.ENGLISH may be null
        if (clazz.equals(TabbedPreferencesAction.class)) {
            return Bundle.getMessage(locale, "MenuItemPreferences");
        } else if (clazz.equals(TabbedPreferencesProfileAction.class)) {
            return Bundle.getMessage(locale, "MenuItemPreferencesProfile");
        }
        throw new IllegalArgumentException(clazz.getName() + " is not supported by " + this.getClass().getName());
    }

    @Override
    public Class<?>[] getActionClasses() {
        return new Class[]{TabbedPreferencesAction.class, TabbedPreferencesProfileAction.class};
    }

}
