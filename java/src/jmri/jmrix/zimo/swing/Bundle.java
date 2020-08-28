package jmri.jmrix.zimo.swing;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Locale;
import javax.annotation.CheckReturnValue;
import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@CheckReturnValue
@SuppressFBWarnings(value = "NM_SAME_SIMPLE_NAME_AS_SUPERCLASS", justification = "Desired pattern is repeated class names with package-level access to members")

@javax.annotation.concurrent.Immutable

/**
 * Provides standard access for resource bundles in a package.
 *
 * Convention is to provide a subclass of this name in each package, working off
 * the local resource bundle name.
 *
 * @author Bob Jacobsen Copyright (C) 2012
 * @since 3.3.1
 */
public class Bundle extends jmri.jmrix.Bundle {

    @CheckForNull
    private static final String name = "jmri.jmrix.zimo.swing.Mx1SwingBundle"; // NOI18N

    //
    // below here is boilerplate to be copied exactly
    //
    /**
     * Provides a translated string for a given key from the package resource
     * bundle or parent.
     * <p>
     * Note that this is intentionally package-local access.
     *
     * @param key Bundle key to be translated
     * @return Internationalized text
     */
    @Nonnull
    static String getMessage(String key) {
        return getBundle().handleGetMessage(key);
    }

    /**
     * Merges user data with a translated string for a given key from the
     * package resource bundle or parent.
     * <p>
     * Uses the transformation conventions of the Java MessageFormat utility.
     * <p>
     * Note that this is intentionally package-local access.
     *
     * @see java.text.MessageFormat
     * @param key  Bundle key to be translated
     * @param subs One or more objects to be inserted into the message
     * @return Internationalized text
     */
    @Nonnull
    static String getMessage(String key, Object... subs) {
        return getBundle().handleGetMessage(key, subs);
    }

    /**
     * Merges user data with a translated string for a given key in a given
     * locale from the package resource bundle or parent.
     * <p>
     * Uses the transformation conventions of the Java MessageFormat utility.
     * <p>
     * Note that this is intentionally package-local access.
     *
     * @see java.text.MessageFormat
     * @param locale The locale to be used
     * @param key    Bundle key to be translated
     * @param subs   One or more objects to be inserted into the message
     * @return Internationalized text
     */
    @Nonnull
    static String getMessage(@Nullable Locale locale, String key, Object... subs) {    // Spotbugs warns that Locale.ENGLISH may be null
        return getBundle().handleGetMessage(locale, key, subs);
    }

    private final static Bundle b = new Bundle();

    @Override
    @CheckForNull
    protected String bundleName() {
        return name;
    }

    protected static jmri.Bundle getBundle() {
        return b;
    }

    @Override
    @Nonnull
    protected String retry(@Nullable Locale locale, String key) {    // Spotbugs warns that Locale.ENGLISH may be null
        return super.getBundle().handleGetMessage(locale,key);
    }

}
