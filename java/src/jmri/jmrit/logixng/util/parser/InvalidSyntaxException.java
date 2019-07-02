
package jmri.jmrit.logixng.util.parser;

/**
 *
 */
public class InvalidSyntaxException extends Exception {

    private final int _position;
    
    /**
     * Constructs an instance of <code>InvalidExpressionException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public InvalidSyntaxException(String msg) {
        super(msg);
        _position = -1;
    }
    
    /**
     * Constructs an instance of <code>InvalidExpressionException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public InvalidSyntaxException(String msg, int position) {
        super(msg);
        _position = position;
    }
    
    public int getPosition() {
        return _position;
    }
    
}
