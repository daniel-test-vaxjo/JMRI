package jmri.jmrit.logixng.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Parses and calculates an expression, for example "sin(2*pi*x)/3"
 * 
 * @author Daniel Bergqvist 2019
 */
public class ExpressionParser<E> {
    
    private final Map<String, OperatorInfo<E>> unaryOperators = new HashMap<>();
    private final Map<String, OperatorInfo<E>> binaryOperators = new HashMap<>();
    private final Map<String, OperatorInfo<E>> functions = new HashMap<>();
    
    
    public void addUnaryOperator(String operator, OperatorInfo<E> operatorInfo) {
        unaryOperators.put(operator, operatorInfo);
    }
    
    public void addBinaryOperator(String operator, OperatorInfo<E> operatorInfo) {
        unaryOperators.put(operator, operatorInfo);
    }
    
    public void addFunction(String operator, OperatorInfo<E> operatorInfo) {
        unaryOperators.put(operator, operatorInfo);
    }
    
    public ParsedExpression<E> parseExpression(String expression) throws InvalidSyntaxException {
        List<Token> tokens = getTokens(expression);
        
//        ParsedExpression<E> expr = new ParsedExpression<>();
        
        TokenType lastTokenType = TokenType.NONE;
        ExpressionNode<E> exprNode;
        
        for (int i = tokens.size(); i > 0; i--) {
//            System.out.println(tokens.get(i));
            Token token = tokens.get(i);
            switch (token.tokenType) {
                //$FALL-THROUGH$
                case LEFT_PARENTHESIS:
                case RIGHT_PARENTHESIS:
                case IDENTIFIER:
                case NUMBER:
                case STRING:
                    
                case ERROR:          // Invalid token, for example an identifier starting with a digit
                case SAME_AS_LAST:   // The same token as last time
                case NONE:
                case SPACE:
                    throw new RuntimeException("this token type should not be here: "+token.tokenType);
                    
                default:
                    throw new RuntimeException("unknown token type: "+token.tokenType);
            }
        }
        
        return null;
    }
    
    protected List<Token> getTokens(String expression) throws InvalidSyntaxException {
        
        List<Token> tokens = new ArrayList<>();
        Token currentToken = new Token();
        
        System.out.format("%n%n%n");
        System.out.format("getTokens(): %s%n", expression);
        
        AtomicBoolean eatNextChar = new AtomicBoolean(false);
        
        for (int i=0; i < expression.length(); i++) {
            char ch = expression.charAt(i);
            char nextChar = ' ';    // An extra space at the end of the string doesn't matter
            if (i+1 < expression.length()) {
                nextChar = expression.charAt(i+1);
            }
            
            System.out.format("index %d: %s, %s, %c, %c%n", i, currentToken.tokenType.name(), currentToken.string, ch, nextChar);
            
            // Handle escaped characters
            if ((currentToken.tokenType == TokenType.STRING)
                    && (ch == '\\')
                    && ((nextChar == '\\') || (nextChar == '"'))) {
                
                currentToken.string += nextChar;
                i++;
                continue;
            }
            
            TokenType nextToken = getTokenType(currentToken, ch, nextChar, eatNextChar);
            System.out.format("index %d: %s, %c%n", i, nextToken.name(), ch);
            
            if (nextToken == TokenType.SAME_AS_LAST) {
                currentToken.string += ch;
                continue;
            }
            
            switch (nextToken) {
                case ERROR:
                    throw new InvalidSyntaxException("invalid syntax at index "+Integer.toString(i), i);
                    
                case LEFT_PARENTHESIS:
                case RIGHT_PARENTHESIS:
                case LEFT_SQUARE_BRACKET:
                case RIGHT_SQUARE_BRACKET:
                case LEFT_CURLY_BRACKET:
                case RIGHT_CURLY_BRACKET:
                case DOT_DOT:
                case COMMA:
                case EQUAL:
                case NOT_EQUAL:
                case LESS:
                case LESS_THAN:
                case GREATER:
                case GREATER_THAN:
                case ADD:
                case SUBTRACKT:
                case MULTIPLY:
                case DIVIDE:
                case MODULO:
                case BOOLEAN_AND:
                case BOOLEAN_OR:
                case BOOLEAN_NOT:
                case BINARY_AND:
                case BINARY_OR:
                case BINARY_NOT:
                case IDENTIFIER:
                    if ((currentToken.tokenType != TokenType.NONE) && (currentToken.tokenType != TokenType.SPACE)) {
                        System.out.format("Add: index %d: %s, %s, %c, %c%n", i, currentToken.tokenType.name(), currentToken.string, ch, nextChar);
                        tokens.add(currentToken);
                        currentToken = new Token();
                    }
                    currentToken.tokenType = nextToken;
                    break;
                    
                case NUMBER:
                    if ((currentToken.tokenType == TokenType.NUMBER) && !currentToken.string.isEmpty() && !isNumber(currentToken.string)) {
                        System.out.format("Not a number: '%s'%n", currentToken.string);
                        throw new InvalidSyntaxException("invalid syntax at index "+Integer.toString(i), i);
                    }
                    if ((currentToken.tokenType != TokenType.NONE) && (currentToken.tokenType != TokenType.SPACE)) {
                        tokens.add(currentToken);
                        currentToken = new Token();
                    }
                    currentToken.tokenType = nextToken;
                    break;
                    
                case STRING:
                    if (!currentToken.string.endsWith("\"")) {
                        throw new InvalidSyntaxException("invalid syntax at index "+Integer.toString(i), i);
                    }
                    if ((currentToken.tokenType != TokenType.NONE) && (currentToken.tokenType != TokenType.SPACE)) {
                        tokens.add(currentToken);
                        currentToken = new Token();
                    }
                    currentToken.tokenType = nextToken;
                    break;
                    
                case SPACE:
//                    if (currentToken.tokenType == TokenType.SPACE) {
//                        continue;
//                    }
                    // Fall through
                    
                case NONE:
                    if ((currentToken.tokenType == TokenType.STRING) && !currentToken.string.endsWith("\"")) {
                        throw new InvalidSyntaxException("invalid syntax at index "+Integer.toString(i), i);
                    }
                    if ((currentToken.tokenType != TokenType.NONE) && (currentToken.tokenType != TokenType.SPACE)) {
                        tokens.add(currentToken);
                        currentToken = new Token();
                    }
                    currentToken.tokenType = nextToken;
                    break;
                    
                default:
                    throw new RuntimeException("unknown token type: "+nextToken.name());
            }
            
            if (currentToken.tokenType != TokenType.SPACE) {
                currentToken.string += ch;
            }
            
            if (eatNextChar.get()) {
                i++;
            }
            System.out.format("New string: '%s'%n", currentToken.string);
        }
        
        if (currentToken.tokenType != TokenType.NONE) {
            tokens.add(currentToken);
        }
        
        return tokens;
    }
    
    private TokenType getTokenType(Token currentToken, char ch, char nextChar, AtomicBoolean eatNextChar) {
        
        eatNextChar.set(false);
        
        if (ch == '"') {
            return TokenType.STRING;
        }
        
        if (Character.isSpaceChar(ch)) {
            return TokenType.SPACE;
        }
        
        if ((currentToken.tokenType == TokenType.STRING) && (ch != '"')) {
            return TokenType.SAME_AS_LAST;
        }
        
        if ((ch == '.') && (nextChar == '.')) {
            if ((currentToken.tokenType != TokenType.DOT_DOT)) {
                eatNextChar.set(true);
                return TokenType.DOT_DOT;
            } else {
                // Three dots in a row is an error
                return TokenType.ERROR;
            }
        }
        
        if (ch == '<') {
            if (nextChar == '=') {
                eatNextChar.set(true);
                return TokenType.LESS_THAN;
            } else if (nextChar == '<') {
                eatNextChar.set(true);
                return TokenType.SHIFT_LEFT;
            } else {
                return TokenType.LESS;
            }
        }
        
        if (ch == '>') {
            if (nextChar == '=') {
                eatNextChar.set(true);
                return TokenType.GREATER_THAN;
            } else if (nextChar == '>') {
                eatNextChar.set(true);
                return TokenType.SHIFT_RIGHT;
            } else {
                return TokenType.GREATER;
            }
        }
        
        if (ch == '=') {
            if (nextChar == '=') {
                eatNextChar.set(true);
                return TokenType.EQUAL;
            } else {
                return TokenType.ERROR;
            }
        }
        
        if (ch == '!') {
            if (nextChar == '=') {
                eatNextChar.set(true);
                return TokenType.NOT_EQUAL;
            } else {
                return TokenType.BOOLEAN_NOT;
            }
        }
        
        if (ch == '&') {
            if (nextChar == '&') {
                eatNextChar.set(true);
                return TokenType.BOOLEAN_AND;
            } else {
                return TokenType.BINARY_AND;
            }
        }
        
        if (ch == '~') {
            return TokenType.BINARY_NOT;
        }
        
        if (ch == ',') {
            return TokenType.COMMA;
        }
        
        if (ch == '+') {
            return TokenType.ADD;
        }
        
        if (ch == '-') {
            return TokenType.SUBTRACKT;
        }
        
        if (ch == '*') {
            return TokenType.MULTIPLY;
        }
        
        if (ch == '/') {
            return TokenType.DIVIDE;
        }
        
        if (ch == '%') {
            return TokenType.MODULO;
        }
        
        if (ch == '^') {
            return TokenType.BINARY_XOR;
        }
        
        if (ch == '(') {
            return TokenType.LEFT_PARENTHESIS;
        }
        
        if (ch == ')') {
            return TokenType.RIGHT_PARENTHESIS;
        }
        
        if (ch == '[') {
            return TokenType.LEFT_SQUARE_BRACKET;
        }
        
        if (ch == ']') {
            return TokenType.RIGHT_SQUARE_BRACKET;
        }
        
        if (ch == '{') {
            return TokenType.LEFT_CURLY_BRACKET;
        }
        
        if (ch == '}') {
            return TokenType.RIGHT_CURLY_BRACKET;
        }
        
        if ((currentToken.tokenType == TokenType.NUMBER) &&
                (isNumber(currentToken.string+ch) || isNumber(currentToken.string+ch+nextChar))) {
            return TokenType.SAME_AS_LAST;
        }
        
        if ((currentToken.tokenType == TokenType.IDENTIFIER) && (Character.isLetterOrDigit(ch))) {
            return TokenType.SAME_AS_LAST;
        }
        
        if (Character.isDigit(ch)) {
            return TokenType.NUMBER;
        }
        
        if ((currentToken.tokenType == TokenType.NUMBER) &&
                (Character.isLetterOrDigit(ch))) {
            return TokenType.ERROR;
        }
        
        if (Character.isDigit(ch)) {
            return TokenType.NUMBER;
        }
        
        if (Character.isLetter(ch)) {
            return TokenType.IDENTIFIER;
        }
        
        return TokenType.ERROR;
    }
    
    private boolean isNumber(String str) {
        return str.matches("\\d+") || str.matches("\\d+\\.\\d+");
    }
    
    public interface Function<E> {
        
        public E call(E param1, E param2);
    }
    
    
    public interface OperatorInfo<E> {
        
        public Function<E> getFunction();
        
        public int getPriority();
        
    }
    
    
    protected enum TokenType {
        // For precedence, see: https://introcs.cs.princeton.edu/java/11precedence/
        
        ERROR(-1),          // Invalid token, for example an identifier starting with a digit
        SAME_AS_LAST(-1),   // The same token as last time
        NONE(-1),
        SPACE(-1),          // Any space character outside of a string, like space, newline, ...
        
        BOOLEAN_OR(3),     // ||
        BOOLEAN_AND(4),    // &&
        
        BINARY_OR(5),       // |
        BINARY_XOR(6),      // ^
        BINARY_AND(7),      // &
        
        EQUAL(8),           // ==
        NOT_EQUAL(8),       // !=
        
        LESS(9),            // <
        LESS_THAN(9),       // <=
        GREATER(9),         // >
        GREATER_THAN(9),    // >=
        
        SHIFT_LEFT(10),     // <<
        SHIFT_RIGHT(10),    // >>
        
        ADD(11),            // +
        SUBTRACKT(11),      // -
        
        MULTIPLY(12),       // *
        DIVIDE(12),         // /
        MODULO(12),         // %
        
        BOOLEAN_NOT(14),    // !
        BINARY_NOT(14),     // ~
        
        LEFT_PARENTHESIS(16),       // (
        RIGHT_PARENTHESIS(16),      // )
        LEFT_SQUARE_BRACKET(16),    // [
        RIGHT_SQUARE_BRACKET(16),   // ]
        LEFT_CURLY_BRACKET(16),     // {
        RIGHT_CURLY_BRACKET(16),    // }
        
        COMMA(20),      // , , used for parameter lists
        DOT_DOT(20),    // .. , used for intervalls
        
        IDENTIFIER(Integer.MAX_VALUE),
        NUMBER(Integer.MAX_VALUE),
        STRING(Integer.MAX_VALUE);
        
        private final int _priority;
        
        private TokenType(int priority) {
            _priority = priority;
        }
        
        public boolean hasLowerPrecedence(TokenType tokenType) {
            return _priority < tokenType._priority;
        }
        
        public boolean hasSamePrecedence(TokenType tokenType) {
            return _priority == tokenType._priority;
        }
        
        public boolean hasHigherPrecedence(TokenType tokenType) {
            return _priority > tokenType._priority;
        }
        
    }
    
    protected final class Token {
        protected TokenType tokenType;
        protected String string;
        
        protected Token() {
            tokenType = TokenType.NONE;
            string = "";
        }
    }
    
}
