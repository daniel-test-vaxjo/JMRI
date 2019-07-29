package jmri.jmrit.logixng;

import java.io.PrintWriter;
import javax.annotation.CheckForNull;
import jmri.NamedBean;

/**
 * The base interface for LogixNG expressions and actions.
 * Used to simplify the user interface.
 * 
 * @author Daniel Bergqvist Copyright 2018
 */
public interface Base {
    
    
    public enum Lock {
        
        /**
         * The item is not locked.
         */
        NONE("BaseLockNone"),
        
        /**
         * The item is locked by the user and can be unlocked by the user.
         */
        USER_LOCK("BaseLockUser"),
        
        /**
         * The item is locked by a hard lock that cannot be unlocked by the
         * user. But it can be removed by editing the xml file. This lock is
         * used for items that normally shouldn't be changed.
         */
        HARD_LOCK("BaseLockHard"),
        
        /**
         * The item is locked by a hard lock that cannot be unlocked by the
         * user. The children are also locked in the sense that they cannot be
         * added or removed.
         * <p>
         * But it can be removed by editing the xml file. This lock is
         * used for items that normally shouldn't be changed.
         */
        HARD_WITH_CHILDREN_LOCK("BaseLockHardWithChildren"),
        
        /**
         * The item is based on a template and therefore cannot be changed.
         * The item should never be changed directly, not even by editing the
         * xml file, but instead all changes must be done by editing the
         * template.
         */
        TEMPLATE_LOCK("BaseLockTemplate");
        
        
        private final String _bundleKey;
        
        private Lock(String bundleKey) {
            _bundleKey = bundleKey;
        }
        
        public final boolean isChangeableByUser() {
            switch (this) {
                case NONE:
                case USER_LOCK:
                    return true;
                    
                case HARD_LOCK:
                case HARD_WITH_CHILDREN_LOCK:
                case TEMPLATE_LOCK:
                    return false;
                    
                default:
                    throw new RuntimeException("lock has unknown value: "+this.name());
            }
        }
        
        @Override
        public String toString() {
            return Bundle.getMessage(_bundleKey);
        }
    }
    
    /**
     * The name of the property child count.
     * To get the number of children, use the method getChildCount().
     * This constant is used in calls to firePropertyChange().
     * The class fires a property change then a child is added or removed.
     */
    public static final String PROPERTY_CHILD_COUNT = "ChildCount";

    /**
     * The status of the socket, if it is connected or not.
     * This constant is used in calls to firePropertyChange().
     * The socket fires a property change to its _parent_ when it is connected
     * or disconnected. Note that the parent does not need to register a
     * listener for this.
     */
    public static final String PROPERTY_SOCKET_CONNECTED = "SocketConnected";

    /**
     * Constant representing an "connected" state of the socket
     */
    public static final int SOCKET_CONNECTED = 0x02;

    /**
     * Constant representing an "disconnected" state of the socket
     */
    public static final int SOCKET_DISCONNECTED = 0x04;


    /**
     * Get the system name.
     * @return the system name
     */
    public String getSystemName();
    
    /**
     * Get the user name.
     * @return the user name
     */
    public String getUserName();
    
    /**
     * Get the user name.
     * @param s the new user name
     */
    public void setUserName(@CheckForNull String s) throws NamedBean.BadUserNameException;
    
    /**
     * Get a short description of this item.
     * @return a short description
     */
    public String getShortDescription();
    
    /**
     * Get a long description of this item.
     * @return a long description
     */
    public String getLongDescription();
    
    /**
     * Returns a new object which is using this object as a template.
     * <P>
     * If this object never should be used as a template, it's valid for it to
     * return null. If this method returns null, the caller must handle this
     * object as if it never existed.
     * 
     * @param sys the system name
     * @return the new object or null if this object is not valid as a template
     */
    @CheckForNull
    public Base getNewObjectBasedOnTemplate(String sys);
    
    /**
     * Get the ConditionalNG of this item.
     * @return the ConditionalNG that owns this item
     */
    public ConditionalNG getConditionalNG();
    
    /**
     * Get the LogixNG of this item.
     * @return the LogixNG that owns this item
     */
    public LogixNG getLogixNG();
    
    /**
     * Get the LogixNG_InstanceManager.
     * 
     * This method will ask the parent about the instance manager, and if no
     * parent exist, it will return the default instance manager.
     * 
     * @return the instance manager that this object should use
     */
    default public LogixNG_InstanceManager getInstanceManager() {
        Base parent = getParent();
        if (parent != null) {
            return parent.getInstanceManager();
        } else {
            return InstanceManagerContainer.defaultInstanceManager;
        }
    }
    
    /**
     * Set the parent.
     * <P>
     * The following rules apply
     * <ul>
     * <li>LogixNGs has no parent. The method throws an UnsupportedOperationException if called.</li>
     * <li>Expressions and actions has the male socket that they are connected to as their parent.</li>
     * <li>Male sockets has the female socket that they are connected to as their parent.</li>
     * <li>The parent of a female sockets is the LogixNG, expression or action that
     * has this female socket.</li>
     * <li>The parent of a male sockets is the same parent as the expression or
     * action that it contains.</li>
     * </ul>
     * 
     * @return the parent of this object
     */
    public Base getParent();
    
    /**
     * Set the parent.
     * <P>
     * The following rules apply
     * <ul>
     * <li>ExecutionGroups has no parent. The method throws an UnsupportedOperationException if called.</li>
     * <li>LogixNGs has the execution group as its parent.</li>
     * <li>Expressions and actions has the male socket that they are connected to as their parent.</li>
     * <li>Male sockets has the female socket that they are connected to as their parent.</li>
     * <li>The parent of a female sockets is the LogixNG, expression or action that
     * has this female socket.</li>
     * <li>The parent of a male sockets is the same parent as the expression or
     * action that it contains.</li>
     * </ul>
     * 
     * @param parent the new parent of this object
     */
    public void setParent(Base parent);
    
    /**
     * Set the parent for all the children.
     */
    public void setParentForAllChildren();
    
    /**
     * Get a child of this item
     * @param index the index of the child to get
     * @return the child
     * @throws IllegalArgumentException if the index is less than 0 or greater
     * or equal with the value returned by getChildCount()
     */
    public FemaleSocket getChild(int index)
            throws IllegalArgumentException, UnsupportedOperationException;

    /**
     * Get the number of children.
     * @return the number of children
     */
    public int getChildCount();
    
    /**
     * Get the category.
     * @return the category
     */
    public Category getCategory();
    
    /**
     * Is this external?
     * Does it affects or is dependent on external things, like
     * turnouts and sensors? Timers are considered as internal since they
     * behavies the same on every computer on every layout.
     * @return true if this is external
     */
    public boolean isExternal();
    
    /**
     * Get the status of the lock.
     * @return the current lock
     */
    public Lock getLock();
    
    /**
     * Set the status of the lock.
     * 
     * Note that the user interface should normally not allow editing a hard lock.
     * 
     * @param lock the new lock
     */
    public void setLock(Lock lock);

    /**
     * Setup this object and its children.
     * This method is used to lookup system names for child sockets, turnouts,
     * sensors, and so on.
     */
    public void setup();
    
    /**
     * Deactivate this object, so that it releases as many resources as possible
     * and no longer effects others.
     * <p>
     * For example, if this object has listeners, after a call to this method it
     * should no longer notify those listeners. Any native or system-wide
     * resources it maintains should be released, including threads, files, etc.
     * <p>
     * It is an error to invoke any other methods on this object once dispose()
     * has been called. Note, however, that there is no guarantee about behavior
     * in that case.
     * <p>
     * Afterwards, references to this object may still exist elsewhere,
     * preventing its garbage collection. But it's formally dead, and shouldn't
     * be keeping any other objects alive. Therefore, this method should null
     * out any references to other objects that this object contained.
     * <p>
     * dispose calls Base.unregisterListeners() to unregister all the listeners.
     */
    public void dispose();  // remove _all_ connections!

    /**
     * Set whenether this object is enabled or disabled.
     * If the parent is disabled, this object must also be disabled, regardless
     * of this flag.
     * 
     * @param enable true if this object should be enabled, false otherwise
     */
//    public void setEnabled(boolean enable);
    
    /**
     * Determines whether this object is enabled.
     * 
     * @return true if the object is enabled, false otherwise
     */
//    public boolean isEnabled();
    
    /**
     * Register listeners if this object needs that.
     * <P>
     * Important: This method may be called more than once. Methods overriding
     * this method must ensure that listeners are not registered more than once.
     */
    public void registerListeners();
    
    /**
     * Unregister listeners if this object needs that.
     * <P>
     * Important: This method may be called more than once. Methods overriding
     * this method must ensure that listeners are not unregistered more than once.
     */
    public void unregisterListeners();
    
     /**
     * Print the tree to a stream.
     * 
     * @param writer the stream to print the tree to
     * @param indent the indentation of each level
     */
    public void printTree(PrintWriter writer, String indent);
    
     /**
     * Print the tree to a stream.
     * 
     * @param writer the stream to print the tree to
     * @param indent the indentation of each level
     * @param currentIndent the current indentation
     */
    public void printTree(PrintWriter writer, String indent, String currentIndent);
    
    
    
    public interface RunnableWithBase {
        public void run(Base b);
    }
    
    
    
    /**
     * This class is used to keep the field instanceManager private.
     */
    static final class InstanceManagerContainer {
        
        private static LogixNG_InstanceManager defaultInstanceManager =
                new jmri.jmrit.logixng.implementation.DefaultLogixNG_InstanceManager();
    }
    
}
