package oop.ex6;

public class VariableException extends Exception{

    private String message;

    private static final long serialVersionUID = 1l;

    public VariableException(String message){
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
