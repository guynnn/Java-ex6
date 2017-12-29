package oop.ex6;

/**
 * This class in an exception represents a problem in a method or a ConditionBlock.
 */
public class MethodException extends Exception {

    private static final long serialVersionUID = 1L;

    // An informative message about the error
    private String message;

    /**
     * Builds a new MethodException.
     * @param message The informative message about the error.
     */
    public MethodException(String message){
        this.message = message;
    }

    /**
     * @return The error message.
     */
    public String getMessage() {
        return message;
    }
}
