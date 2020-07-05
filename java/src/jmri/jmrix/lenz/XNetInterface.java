package jmri.jmrix.lenz;

import org.apiguardian.api.API;
import static org.apiguardian.api.API.Status.*;

/**
 * XNetInterface defines the general connection to an XNet layout.
 * <p>
 * Use this interface to send messages to an XNet layout. Classes implementing
 * the XNetListener interface can register here to receive incoming XNet
 * messages as events.
 * <p>
 * The jmri.jrmix.lenz.XNetTrafficControler provides the first implementation of
 * this interface.
 * <p>
 * How do you locate an implemenation of this interface? That's an interesting
 * question. This is inherently XNet specific, so it would be inappropriate to
 * put it in the jmri.InterfaceManager. And Java interfaces can't have static
 * members, so we can't provide an implementation() member. For now, we use a
 * static implementation member in the XNetTrafficController implementation to
 * locate _any_ implementation; this clearly needs to be improved.
 * <p>
 * XNetListener implementations registering for traffic updates cannot assume
 * that messages will be returned in any particular thread. See the XNetListener
 * doc for more background.
 *
 * @author Bob Jacobsen Copyright (C) 2001, 2002
 * @see jmri.jmrix.lenz.XNetListener
 * @see jmri.jmrix.lenz.XNetTrafficController
 */
@API(status = EXPERIMENTAL)
public interface XNetInterface {

    /**
     * Request a message be sent to the attached XNet. Return is immediate,
     * with the message being queued for eventual sending.  If you're interested
     * in a reply, you need to register an XNetListener object to watch the
     * message stream. When sending, you specify (in 2nd parameter) who
     * you are so you're not redundantly notified of this message.
     * @param msg the XNet message to send.
     * @param replyTo sending listener to NOT notify.
     */
    void sendXNetMessage(XNetMessage msg, XNetListener replyTo);

    /**
     * Request notification of things happening on the XNet.
     * <p>
     * The same listener can register multiple times with different masks.
     * (Multiple registrations with a single mask value are equivalent to a
     * single registration) Mask values are defined as class constants. Note
     * that these are bit masks, and should be OR'd, not added, if multiple
     * values are desired.
     * <p>
     * The event notification contains the received message as source, not this
     * object, so that we can notify of an incoming message to multiple places
     * and then move on.
     *
     * @param mask The OR of the key values of messages to be reported (to
     *             reduce traffic, provide for listeners interested in different
     *             things)
     *
     * @param l    Object to be notified of new messages as they arrive.
     *
     */
    void addXNetListener(int mask, XNetListener l);

    /**
     * Stop notification of things happening on the XNet.
     * <p>
     * Note that mask and XNetListener must match a previous request exactly.
     * @param mask listening mask.
     * @param listener listener to remove notifications for. 
     */
    void removeXNetListener(int mask, XNetListener listener);

    /**
     * Check whether an implementation is operational.
     * @return true if OK, else false.
     */
    boolean status();

    /**
     * Mask value to request notification of all incoming messages
     */
    int ALL = ~0;

    /**
     * Mask value to request notification of communications related messages
     * generated by the computer interface
     */
    int COMMINFO = 1;

    /**
     * Mask value to request notification of Command Station informational
     * messages This includes all broadcast messages, except for the feedback
     * broadcast and all programming messages
     */
    int CS_INFO = 2;

    /**
     * Mask value to request notification of messages associated with
     * programming
     */
    int PROGRAMMING = 4;

    /**
     * Mask value to request notification of XpressNet FeedBack (i.e. sensor)
     * related messages
     */
    int FEEDBACK = 8;

    /**
     * Mask value to request notification of messages associated with throttle
     * status
     *
     */
    int THROTTLE = 16;

    /**
     * Mask value to request notification of messages associated with consists
     *
     */
    int CONSIST = 32;

    /**
     * Mask value to request notification of messages associated with the interface
     *
     */
    int INTERFACE = 64;

}
