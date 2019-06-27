package jmri.jmrit.logixng;

/**
 * A LogixNG male AnalogAction socket.
 */
public interface MaleAnalogActionSocket
        extends MaleSocket, AnalogAction {

    /**
     * {@inheritDoc}
     * <P>
     * This method must ensure that the value is not a Double.NaN, negative
     * infinity or positive infinity. If that is the case, it must throw an
     * IllegalArgumentException before checking if an error has occured.
     */
    @Override
    public void setValue(double value);

}
