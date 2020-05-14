package jmri;

import edu.umd.cs.findbugs.annotations.OverrideMustInvoke;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.util.*;
import javax.annotation.CheckForNull;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import jmri.NamedBean.BadSystemNameException;
import jmri.NamedBean.DuplicateSystemNameException;
import jmri.beans.PropertyChangeProvider;
import jmri.beans.VetoableChangeProvider;
import jmri.jmrix.SystemConnectionMemo;

/**
 * Basic interface for access to named, managed objects.
 * <p>
 * {@link NamedBean} objects represent various real elements, and have a "system
 * name" and perhaps "user name". A specific Manager object provides access to
 * them by name, and serves as a factory for new objects.
 * <p>
 * Right now, this interface just contains the members needed by
 * {@link InstanceManager} to handle managers for more than one system.
 * <p>
 * Although they are not defined here because their return type differs, any
 * specific Manager subclass provides "get" methods to locate specific objects,
 * and a "new" method to create a new one via the Factory pattern. The "get"
 * methods will return an existing object or null, and will never create a new
 * object. The "new" method will log a warning if an object already exists with
 * that system name.
 * <p>
 * add/remove PropertyChangeListener methods are provided. At a minimum,
 * subclasses must notify of changes to the list of available NamedBeans; they
 * may have other properties that will also notify.
 * <p>
 * Probably should have been called NamedBeanManager
 * <hr>
 * This file is part of JMRI.
 * <p>
 * JMRI is free software; you can redistribute it and/or modify it under the
 * terms of version 2 of the GNU General Public License as published by the Free
 * Software Foundation. See the "COPYING" file for a copy of this license.
 * <p>
 * JMRI is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * @param <E> the type of NamedBean supported by this manager
 * @author Bob Jacobsen Copyright (C) 2003
 */
public interface Manager<E extends NamedBean> extends PropertyChangeProvider, VetoableChangeProvider {

    /**
     * Get the system connection for this manager.
     *
     * @return the system connection for this manager
     */
    @CheckReturnValue
    @Nonnull
    public SystemConnectionMemo getMemo();

    /**
     * Provide access to the system prefix string. This was previously called
     * the "System letter"
     *
     * @return the system prefix
     */
    @CheckReturnValue
    @Nonnull
    public String getSystemPrefix();

    /**
     * @return The type letter for a specific implementation
     */
    @CheckReturnValue
    public char typeLetter();

    /**
     * Get the class of NamedBean supported by this Manager. This should be the
     * generic class used in the Manager's class declaration.
     * 
     * @return the class supported by this Manager.
     */
    public abstract Class<E> getNamedBeanClass();

    /**
     * Get the prefix and type for the system name of the NamedBeans handled by
     * this manager.
     *
     * @return the prefix generated by concatenating the result of
     * {@link #getSystemPrefix() } and {@link #typeLetter() }
     */
    public default String getSystemNamePrefix() {
        return getSystemPrefix() + typeLetter();
    }

    /**
     * Create a SystemName by prepending the system name prefix to the name if
     * not already present.
     * <p>
     * <strong>Note:</strong> implementations <em>must</em> call
     * {@link #validateSystemNameFormat(java.lang.String, java.util.Locale)} to
     * ensure the returned name is valid.
       *
     * @param name the item to make the system name for
     * @return A system name from a user input, typically a number.
     * @throws BadSystemNameException if a valid name can't be created
     */
    @Nonnull
    public default String makeSystemName(@Nonnull String name) {
        return makeSystemName(name, true);
    }

    /**
     * Create a SystemName by prepending the system name prefix to the name if
     * not already present.
     * <p>
     * The {@code logErrors} parameter is present to allow user interface input
     * validation to use this method without logging system name validation
     * errors as the user types.
     * <p>
     * <strong>Note:</strong> implementations <em>must</em> call
     * {@link #validateSystemNameFormat(java.lang.String, java.util.Locale)} to ensure
     * the returned name is valid.
     *
     * @param name      the item to make the system name for
     * @param logErrors true to log errors; false to not log errors
     * @return a valid system name
     * @throws BadSystemNameException if a valid name can't be created
     */
    @Nonnull
    public default String makeSystemName(@Nonnull String name, boolean logErrors) {
        return makeSystemName(name, logErrors, Locale.getDefault());
    }

    /**
     * Create a SystemName by prepending the system name prefix to the name if
     * not already present.
     * <p>
     * The {@code logErrors} parameter is present to allow user interface input
     * validation to use this method without logging system name validation
     * errors as the user types.
     * <p>
     * <strong>Note:</strong> implementations <em>must</em> call
     * {@link #validateSystemNameFormat(java.lang.String, java.util.Locale)} to ensure
     * the returned name is valid.
     *
     * @param name      the item to make the system name for
     * @param logErrors true to log errors; false to not log errors
     * @param locale    the locale for a localized exception; this is needed for
     *                      the JMRI web server, which supports multiple locales
     * @return a valid system name
     * @throws BadSystemNameException if a valid name can't be created
     */
    @Nonnull
    public default String makeSystemName(@Nonnull String name, boolean logErrors, Locale locale) {
        String prefix = getSystemNamePrefix();
        // the one special case that is not caught by validation here
        if (name.trim().isEmpty()) { // In Java 9+ use name.isBlank() instead
            throw new NamedBean.BadSystemNameException(Locale.getDefault(), "InvalidSystemNameInvalidPrefix", prefix);
        }
        return validateSystemNameFormat(name.startsWith(prefix) ? name : prefix + name, locale);
    }

    /**
     * Validate the format of a system name, returning it unchanged if valid.
     * <p>
     * This is a convenience form of {@link #validateSystemNameFormat(java.lang.String, java.util.Locale)}.
     * <p>
     * This method should not be overridden;
     * {@link #validateSystemNameFormat(java.lang.String, java.util.Locale)}
     * should be overridden instead.
     *
     * @param name      the system name to validate
     * @return the system name unchanged from its input so that this method can
     *         be chained or used as an parameter to another method
     * @throws BadSystemNameException if the name is not valid with error
     *                                      messages in the default locale
     */
    @Nonnull
    public default String validateSystemNameFormat(@Nonnull String name) {
        return Manager.this.validateSystemNameFormat(name, Locale.getDefault());
    }

    /**
     * Validate the format of name, returning it unchanged if valid.
     * <p>
     * Although further restrictions may be added by system-specific
     * implementations, at a minimum, the implementation must consider a name
     * that does not start with the System Name prefix for this manager to be
     * invalid, and must consider a name that is the same as the System Name
     * prefix to be invalid.
     * <p>
     * Overriding implementations may rely on
     * {@link #validSystemNameFormat(java.lang.String)}, however they must
     * provide an actionable message in the thrown exception if that method does
     * not return {@link NameValidity#VALID}. When overriding implementations
     * of this method rely on validSystemNameFormat(), implementations of
     * that method <em>must not</em> throw an exception, log an error, or
     * otherwise disrupt the user.
     *
     * @param name      the system name to validate
     * @param locale    the locale for a localized exception; this is needed for
     *                      the JMRI web server, which supports multiple locales
     * @return the unchanged value of the name parameter
     * @throws BadSystemNameException if provided name is an invalid format
     */
    @Nonnull
    public default String validateSystemNameFormat(@Nonnull String name, @Nonnull Locale locale) {
        return validateSystemNamePrefix(name, locale);
    }

    /**
     * Basic validation that the system name prefix is correct. Used within the
     * default implementation of
     * {@link #validateSystemNameFormat(java.lang.String, java.util.Locale)} and
     * abstracted out of that method so this can be used by validation
     * implementations in {@link jmri.jmrix.SystemConnectionMemo}s to avoid
     * duplicating code in all managers relying on a single subclass of
     * SystemConnectionMemo.
     *
     * @param name      the system name to validate
     * @param locale    the locale for a localized exception; this is needed for
     *                      the JMRI web server, which supports multiple locales
     * @return the unchanged value of the name parameter
     * @throws BadSystemNameException if provided name is an invalid format
     */
    @Nonnull
    public default String validateSystemNamePrefix(@Nonnull String name, @Nonnull Locale locale) {
        String prefix = getSystemNamePrefix();
        if (name.equals(prefix)) {
            throw new NamedBean.BadSystemNameException(locale, "InvalidSystemNameMatchesPrefix", name);
        }
        if (!name.startsWith(prefix)) {
            throw new NamedBean.BadSystemNameException(locale, "InvalidSystemNameInvalidPrefix", prefix);
        }
        return name;
    }

    /**
     * Convenience implementation of
     * {@link #validateSystemNameFormat(java.lang.String, java.util.Locale)}
     * that verifies name has no trailing white space and no white space between
     * the prefix and suffix.
     * <p>
     * <strong>Note</strong> this <em>must</em> only be used if the connection
     * type is externally documented to require these restrictions.
     *
     * @param name   the system name to validate
     * @param locale the locale for a localized exception; this is needed for
     *               the JMRI web server, which supports multiple locales
     * @return the unchanged value of the name parameter
     * @throws BadSystemNameException if provided name is an invalid format
     */
    @Nonnull
    public default String validateTrimmedSystemNameFormat(@Nonnull String name, @Nonnull Locale locale) {
        name = validateSystemNamePrefix(name, locale);
        String prefix = getSystemNamePrefix();
        String suffix = name.substring(prefix.length());
        if (!suffix.equals(suffix.trim())) {
            throw new NamedBean.BadSystemNameException(locale, "InvalidSystemNameTrailingWhitespace", name, prefix);
        }
        return name;
    }

    /**
     * Convenience implementation of
     * {@link #validateSystemNameFormat(java.lang.String, java.util.Locale)}
     * that verifies name is upper case and has no trailing white space and not
     * white space between the prefix and suffix.
     * <p>
     * <strong>Note</strong> this <em>must</em> only be used if the connection
     * type is externally documented to require these restrictions.
     *
     * @param name   the system name to validate
     * @param locale the locale for a localized exception; this is needed for
     *               the JMRI web server, which supports multiple locales
     * @return the unchanged value of the name parameter
     * @throws BadSystemNameException if provided name is an invalid format
     */
    @Nonnull
    public default String validateUppercaseTrimmedSystemNameFormat(@Nonnull String name, @Nonnull Locale locale) {
        name = validateTrimmedSystemNameFormat(name, locale);
        String prefix = getSystemNamePrefix();
        String suffix = name.substring(prefix.length());
        String upper = suffix.toUpperCase();
        if (!suffix.equals(upper)) {
            throw new NamedBean.BadSystemNameException(locale, "InvalidSystemNameNotUpperCase", name, prefix);
        }
        return name;
    }

    /**
     * Convenience implementation of
     * {@link #validateSystemNameFormat(java.lang.String, java.util.Locale)}
     * that verifies name is an integer after the prefix.
     * <p>
     * <strong>Note</strong> this <em>must</em> only be used if the connection
     * type is externally documented to require these restrictions.
     *
     * @param name   the system name to validate
     * @param min    the minimum valid integer value
     * @param max    the maximum valid integer value
     * @param locale the locale for a localized exception; this is needed for
     *               the JMRI web server, which supports multiple locales
     * @return the unchanged value of the name parameter
     * @throws BadSystemNameException if provided name is an invalid format
     */
    @Nonnull
    public default String validateIntegerSystemNameFormat(@Nonnull String name, int min, int max, @Nonnull Locale locale) {
        name = validateTrimmedSystemNameFormat(name, locale);
        String prefix = getSystemNamePrefix();
        String suffix = name.substring(prefix.length());
        try {
            int number = Integer.parseInt(suffix);
            if (number < min) {
                throw new NamedBean.BadSystemNameException(locale, "InvalidSystemNameIntegerLessThan", name, min);
            } else if (number > max) {
                throw new NamedBean.BadSystemNameException(locale, "InvalidSystemNameIntegerGreaterThan", name, max);
            }
        } catch (NumberFormatException ex) {
            throw new NamedBean.BadSystemNameException(locale, "InvalidSystemNameNotInteger", name, prefix);
        }
        return name;
    }

    /**
     * Convenience implementation of
     * {@link #validateSystemNameFormat(java.lang.String, java.util.Locale)}
     * that verifies name is a valid NMRA Accessory address after the prefix. A
     * name is considered a valid NMRA accessory address if it is an integer
     * between {@value NmraPacket#accIdLowLimit} and
     * {@value NmraPacket#accIdHighLimit}, inclusive.
     * <p>
     * <strong>Note</strong> this <em>must</em> only be used if the connection
     * type is externally documented to require these restrictions.
     *
     * @param name   the system name to validate
     * @param locale the locale for a localized exception; this is needed for
     *               the JMRI web server, which supports multiple locales
     * @return the unchanged value of the name parameter
     * @throws BadSystemNameException if provided name is an invalid format
     */
    @Nonnull
    public default String validateNmraAccessorySystemNameFormat(@Nonnull String name, @Nonnull Locale locale) {
        return this.validateIntegerSystemNameFormat(name, NmraPacket.accIdLowLimit, NmraPacket.accIdHighLimit, locale);
    }

    /**
     * Code the validity (including just as a prefix) of a proposed name string.
     *
     * @since 4.9.5
     */
    enum NameValidity {
        /**
         * Indicates the name is valid as is, and can also be a valid prefix for
         * longer names
         */
        VALID,
        /**
         * Indicates name is not valid as-is, nor can it be made valid by adding
         * more characters; just a bad name.
         */
        INVALID,
        /**
         * Indicates that adding additional characters might (or might not) turn
         * this into a valid name; it is not a valid name now.
         */
        VALID_AS_PREFIX_ONLY
    }

    /**
     * Test if parameter is a properly formatted system name. Implementations of
     * this method <em>must not</em> throw an exception, log an error, or
     * otherwise disrupt the user.
     *
     * @since 4.9.5, although similar methods existed previously in lower-level
     * classes
     * @param systemName the system name
     * @return enum indicating current validity, which might be just as a prefix
     */
    @CheckReturnValue
    @OverrideMustInvoke
    public default NameValidity validSystemNameFormat(@Nonnull String systemName) {
        String prefix = getSystemNamePrefix();
        if (prefix.equals(systemName)) {
            return NameValidity.VALID_AS_PREFIX_ONLY;
        }
        return systemName.startsWith(prefix)
                ? NameValidity.VALID
                : NameValidity.INVALID;
    }

    /**
     * Test if a given name is in a valid format for this Manager.
     *
     * @param systemName the name to check
     * @return {@code true} if {@link #validSystemNameFormat(java.lang.String)}
     *         equals {@link NameValidity#VALID}; {@code false} otherwise
     */
    public default boolean isValidSystemNameFormat(@Nonnull String systemName) {
        return validSystemNameFormat(systemName) == NameValidity.VALID;
    }

    /**
     * Free resources when no longer used. Specifically, remove all references
     * to and from this object, so it can be garbage-collected.
     */
    public void dispose();

    /**
     * Get the count of managed objects.
     *
     * @return the number of managed objects
     */
    @CheckReturnValue
    public int getObjectCount();

    /**
     * Provide an
     * {@linkplain java.util.Collections#unmodifiableList unmodifiable} List of
     * system names.
     * <p>
     * Note: this is ordered by the underlying NamedBeans, not on the Strings
     * themselves.
     * <p>
     * Note: Access via {@link #getNamedBeanSet()} is faster.
     * <p>
     * Note: This is not a live list; the contents don't stay up to date
     *
     * @return Unmodifiable access to a list of system names
     * @deprecated 4.11.5 - use direct access via {@link #getNamedBeanSet()}
     */
    @Deprecated // 4.11.5
    @CheckReturnValue
    @Nonnull
    public List<String> getSystemNameList();

    /**
     * Provide an
     * {@linkplain java.util.Collections#unmodifiableList unmodifiable} List of
     * NamedBeans in system-name order.
     * <p>
     * Note: Access via {@link #getNamedBeanSet()} is faster.
     * <p>
     * Note: This is not a live list; the contents don't stay up to date
     *
     * @return Unmodifiable access to a List of NamedBeans
     * @deprecated 4.11.5 - use direct access via {@link #getNamedBeanSet()}
     */
    @Deprecated // 4.11.5
    @CheckReturnValue
    @Nonnull
    public List<E> getNamedBeanList();

    /**
     * Provide an
     * {@linkplain java.util.Collections#unmodifiableSet unmodifiable} SortedSet
     * of NamedBeans in system-name order.
     * <p>
     * Note: This is the fastest of the accessors, and is the only long-term
     * form.
     * <p>
     * Note: This is a live set; the contents are kept up to date
     *
     * @return Unmodifiable access to a SortedSet of NamedBeans
     */
    @CheckReturnValue
    @Nonnull
    public SortedSet<E> getNamedBeanSet();

    /**
     * Deprecated form to locate an existing instance based on a system name.
     *
     * @param systemName System Name of the required NamedBean
     * @return requested NamedBean object or null if none exists
     * @throws IllegalArgumentException if provided name is invalid
     * @deprecated since 4.19.1
     */
    @CheckReturnValue
    @CheckForNull
    @Deprecated // 4.19.1
    public default E getBeanBySystemName(@Nonnull String systemName) {
        jmri.util.Log4JUtil.deprecationWarning(deprecatedManagerLogger, "getBeanBySystemName");
        return getBySystemName(systemName);
    }

    /**
     * Deprecated form to locate an existing instance based on a user name.
     *
     * @param userName System Name of the required NamedBean
     * @return requested NamedBean object or null if none exists
     * @deprecated since 4.19.1
     */
    @CheckReturnValue
    @CheckForNull
    @Deprecated // 4.19.1
    public default E getBeanByUserName(@Nonnull String userName) {
        jmri.util.Log4JUtil.deprecationWarning(deprecatedManagerLogger, "getBeanByUserName");
        return getByUserName(userName);
    }

    // needed for deprecationWarning calls above, remove with them
    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings(value = "SLF4J_LOGGER_SHOULD_BE_PRIVATE",justification="Private not available in interface; just needed for deprecation")
    static final org.slf4j.Logger deprecatedManagerLogger = org.slf4j.LoggerFactory.getLogger(Manager.class);

    /**
     * Locate an existing instance based on a system name.
     *
     * @param systemName System Name of the required NamedBean
     * @return requested NamedBean object or null if none exists
     * @throws IllegalArgumentException if provided name is invalid
     */
    @CheckReturnValue
    @CheckForNull
    public E getBySystemName(@Nonnull String systemName);

    /**
     * Locate an existing instance based on a user name.
     *
     * @param userName System Name of the required NamedBean
     * @return requested NamedBean object or null if none exists
     */
    @CheckReturnValue
    @CheckForNull
    public E getByUserName(@Nonnull String userName);

    /**
     * Locate an existing instance based on a name.
     *
     * @param name User Name or System Name of the required NamedBean
     * @return requested NamedBean object or null if none exists
     */
    @CheckReturnValue
    @CheckForNull
    public E getNamedBean(@Nonnull String name);

    /**
     * Return the descriptors for the system-specific properties of the
     * NamedBeans that are kept in this manager.
     *
     * @return list of known properties, or empty list if there are none
     */
    @Nonnull
    public default List<NamedBeanPropertyDescriptor<?>> getKnownBeanProperties() {
        return new LinkedList<>();
    }

    /**
     * Method for a UI to delete a bean.
     * <p>
     * The UI should first request a "CanDelete", this will return a list of
     * locations (and descriptions) where the bean is in use via throwing a
     * VetoException, then if that comes back clear, or the user agrees with the
     * actions, then a "DoDelete" can be called which inform the listeners to
     * delete the bean, then it will be deregistered and disposed of.
     * <p>
     * If a property name of "DoNotDelete" is thrown back in the VetoException
     * then the delete process should be aborted.
     *
     * @param n        The NamedBean to be deleted
     * @param property The programmatic name of the request. "CanDelete" will
     *                 enquire with all listeners if the item can be deleted.
     *                 "DoDelete" tells the listener to delete the item
     * @throws java.beans.PropertyVetoException - If the recipients wishes the
     *                                          delete to be aborted (see
     *                                          above)
     */
    public void deleteBean(@Nonnull E n, @Nonnull String property) throws PropertyVetoException;

    /**
     * Remember a NamedBean Object created outside the manager.
     * <p>
     * The non-system-specific SignalHeadManagers use this method extensively.
     *
     * @param n the bean
     * @throws DuplicateSystemNameException if a different bean with the same system
     *                                      name is already registered in the
     *                                      manager
     */
    public void register(@Nonnull E n);

    /**
     * Forget a NamedBean Object created outside the manager.
     * <p>
     * The non-system-specific RouteManager uses this method.
     *
     * @param n the bean
     */
    public void deregister(@Nonnull E n);

    /**
     * The order in which things get saved to the xml file.
     */
    public static final int SENSORS = 10;
    public static final int TURNOUTS = SENSORS + 10;
    public static final int LIGHTS = TURNOUTS + 10;
    public static final int REPORTERS = LIGHTS + 10;
    public static final int MEMORIES = REPORTERS + 10;
    public static final int SENSORGROUPS = MEMORIES + 10;
    public static final int SIGNALHEADS = SENSORGROUPS + 10;
    public static final int SIGNALMASTS = SIGNALHEADS + 10;
    public static final int SIGNALGROUPS = SIGNALMASTS + 10;
    public static final int BLOCKS = SIGNALGROUPS + 10;
    public static final int OBLOCKS = BLOCKS + 10;
    public static final int LAYOUTBLOCKS = OBLOCKS + 10;
    public static final int SECTIONS = LAYOUTBLOCKS + 10;
    public static final int TRANSITS = SECTIONS + 10;
    public static final int BLOCKBOSS = TRANSITS + 10;
    public static final int ROUTES = BLOCKBOSS + 10;
    public static final int WARRANTS = ROUTES + 10;
    public static final int SIGNALMASTLOGICS = WARRANTS + 10;
    public static final int IDTAGS = SIGNALMASTLOGICS + 10;
    public static final int LOGIXS = IDTAGS + 10;
    public static final int CONDITIONALS = LOGIXS + 10;
    public static final int AUDIO = LOGIXS + 10;
    public static final int TIMEBASE = AUDIO + 10;
    public static final int PANELFILES = TIMEBASE + 10;
    public static final int ENTRYEXIT = PANELFILES + 10;

    /**
     * Determine the order that types should be written when storing panel
     * files. Uses one of the constants defined in this class.
     * <p>
     * Yes, that's an overly-centralized methodology, but it works for now.
     *
     * @return write order for this Manager; larger is later.
     */
    @CheckReturnValue
    public int getXMLOrder();

    /**
     * Get the user-readable name of the type of NamedBean handled by this
     * manager.
     * <p>
     * For instance, in the code where we are dealing with just a bean and a
     * message that needs to be passed to the user or in a log.
     *
     * @return a string of the bean type that the manager handles, eg Turnout,
     *         Sensor etc
     */
    @CheckReturnValue
    @Nonnull
    public default String getBeanTypeHandled() {
        return getBeanTypeHandled(false);
    }

    /**
     * Get the user-readable name of the type of NamedBean handled by this
     * manager.
     * <p>
     * For instance, in the code where we are dealing with just a bean and a
     * message that needs to be passed to the user or in a log.
     *
     * @param plural true to return plural form of the type; false to return
     *               singular form
     *
     * @return a string of the bean type that the manager handles, eg Turnout,
     *         Sensor etc
     */
    @CheckReturnValue
    @Nonnull
    public String getBeanTypeHandled(boolean plural);

    /**
     * Provide length of the system prefix of the given system name.
     * <p>
     * This is a common operation across JMRI, as the system prefix can be
     * parsed out without knowledge of the type of NamedBean involved.
     *
     * @param inputName System Name to provide the prefix
     * @throws NamedBean.BadSystemNameException If the inputName can't be
     *                                          converted to normalized form
     * @return The length of the system-prefix part of the system name in
     *         standard normalized form
     */
    @CheckReturnValue
    public static int getSystemPrefixLength(@Nonnull String inputName) {
        if (inputName.isEmpty()) {
            throw new NamedBean.BadSystemNameException();
        }
        if (!Character.isLetter(inputName.charAt(0))) {
            throw new NamedBean.BadSystemNameException();
        }

        int i;
        for (i = 1; i < inputName.length(); i++) {
            if (!Character.isDigit(inputName.charAt(i))) {
                break;
            }
        }
        return i;
    }

    /**
     * Provides the system prefix of the given system name.
     * <p>
     * This is a common operation across JMRI, as the system prefix can be
     * parsed out without knowledge of the type of NamedBean involved.
     *
     * @param inputName System name to provide the prefix
     * @throws NamedBean.BadSystemNameException If the inputName can't be
     *                                          converted to normalized form
     * @return The system-prefix part of the system name in standard normalized
     *         form
     */
    @CheckReturnValue
    @Nonnull
    public static String getSystemPrefix(@Nonnull String inputName) {
        return inputName.substring(0, getSystemPrefixLength(inputName));
    }

    /**
     * Get a manager-specific tool tip for adding an entry to the manager.
     *
     * @return the tool tip or null to disable the tool tip
     */
    public default String getEntryToolTip() {
        return null;
    }

    /**
     * Register a {@link ManagerDataListener} to hear about adding or removing
     * items from the list of NamedBeans.
     *
     * @param e the data listener to add
     * @deprecated since 4.19.6; use
     *             {@link #addPropertyChangeListener(String, PropertyChangeListener)}
     *             or {@link #addPropertyChangeListener(PropertyChangeListener)}
     *             instead, listening for changes to the
     *             {@code beans} property
     */
    @Deprecated
    public void addDataListener(ManagerDataListener<E> e);

    /**
     * Unregister a previously-added {@link ManagerDataListener}.
     *
     * @param e the data listener to remove
     * @deprecated since 4.19.6; use
     *             {@link #removePropertyChangeListener(String, PropertyChangeListener)}
     *             or
     *             {@link #removePropertyChangeListener(PropertyChangeListener)}
     *             instead
     * @see #addDataListener(ManagerDataListener)
     */
    @Deprecated
    public void removeDataListener(ManagerDataListener<E> e);

    /**
     * Temporarily suppress DataListener notifications.
     * <p>
     * This avoids O(N^2) behavior when doing bulk updates, i.e. when loading
     * lots of Beans. Note that this is (1) optional, in the sense that the
     * manager is not required to mute and (2) if present, its' temporary, in
     * the sense that the manager must do a cumulative notification when done.
     *
     * @param muted true if notifications should be suppressed; false otherwise
     * @deprecated since 4.19.6 without direct replacement
     */
    @Deprecated
    public default void setDataListenerMute(boolean muted) {
    }

    /**
     * Intended to be equivalent to {@link javax.swing.event.ListDataListener}
     * without introducing a Swing dependency into core JMRI.
     *
     * @param <E> the type to support listening for
     * @since JMRI 4.11.4
     * @deprecated since 4.19.6 without direct replacement
     */
    @Deprecated
    interface ManagerDataListener<E extends NamedBean> {

        /**
         * Sent when the contents of the list has changed in a way that's too
         * complex to characterize with the previous methods.
         *
         * @param e encapsulates event information
         */
        void contentsChanged(ManagerDataEvent<E> e);

        /**
         * Sent after the indices in the index0,index1 interval have been
         * inserted in the data model.
         *
         * @param e encapsulates the event information
         */
        void intervalAdded(ManagerDataEvent<E> e);

        /**
         * Sent after the indices in the index0,index1 interval have been
         * removed from the data model.
         *
         * @param e encapsulates the event information
         */
        void intervalRemoved(ManagerDataEvent<E> e);
    }

    /**
     * Define an event that encapsulates changes to a list.
     * <p>
     * Intended to be equivalent to {@link javax.swing.event.ListDataEvent}
     * without introducing a Swing dependency into core JMRI.
     *
     * @param <E> the type to support in the event
     * @since JMRI 4.11.4
     * @deprecated since 4.19.6 without direct replacement
     */
    @Deprecated
    @javax.annotation.concurrent.Immutable
    public final class ManagerDataEvent<E extends NamedBean> extends java.util.EventObject {

        /**
         * Equal to {@link javax.swing.event.ListDataEvent#CONTENTS_CHANGED}
         */
        public static final int CONTENTS_CHANGED = 0;
        /**
         * Equal to {@link javax.swing.event.ListDataEvent#INTERVAL_ADDED}
         */
        public static final int INTERVAL_ADDED = 1;
        /**
         * Equal to {@link javax.swing.event.ListDataEvent#INTERVAL_REMOVED}
         */
        public static final int INTERVAL_REMOVED = 2;

        private final int type;
        private final int index0;
        private final int index1;
        private final transient E changedBean; // used when just one bean is added or removed as an efficiency measure
        private final transient Manager<E> source;

        /**
         * Create a <code>ListDataEvent</code> object.
         *
         * @param source      the source of the event (<code>null</code> not
         *                    permitted).
         * @param type        the type of the event (should be one of
         *                    {@link #CONTENTS_CHANGED}, {@link #INTERVAL_ADDED}
         *                    or {@link #INTERVAL_REMOVED}, although this is not
         *                    enforced).
         * @param index0      the index for one end of the modified range of
         *                    list elements.
         * @param index1      the index for the other end of the modified range
         *                    of list elements.
         * @param changedBean used when just one bean is added or removed,
         *                    otherwise null
         */
        public ManagerDataEvent(@Nonnull Manager<E> source, int type, int index0, int index1, E changedBean) {
            super(source);
            this.source = source;
            this.type = type;
            this.index0 = Math.min(index0, index1);  // from javax.swing.event.ListDataEvent implementation
            this.index1 = Math.max(index0, index1);  // from javax.swing.event.ListDataEvent implementation
            this.changedBean = changedBean;
        }

        /**
         * Get the source of the event in a type-safe manner.
         *
         * @return the event source
         */
        @Override
        public Manager<E> getSource() {
            return source;
        }

        /**
         * Get the index of the first item in the range of modified list
         * items.
         *
         * @return index of the first item in the range of modified list items
         */
        public int getIndex0() {
            return index0;
        }

        /**
         * Get the index of the last item in the range of modified list
         * items.
         *
         * @return index of the last item in the range of modified list items
         */
        public int getIndex1() {
            return index1;
        }

        /**
         * Get the changed bean or null.
         *
         * @return null if more than one bean was changed
         */
        public E getChangedBean() {
            return changedBean;
        }

        /**
         * Get a code representing the type of this event, which is usually
         * one of {@link #CONTENTS_CHANGED}, {@link #INTERVAL_ADDED} or
         * {@link #INTERVAL_REMOVED}.
         *
         * @return the event type
         */
        public int getType() {
            return type;
        }

        /**
         * Get a string representing the state of this event.
         *
         * @return event state as a string
         */
        @Override
        public String toString() {
            return getClass().getName() + "[type=" + type + ", index0=" + index0 + ", index1=" + index1 + "]";
        }
    }

}
