package oop.ex6;

import oop.ex6.main.Sjavac;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class represents a variable in the Sjavac file.
 */
public class Variable {

    // The name of the variable
    private String name;
    // The type of the variable
    private String type;
    // Indicates whether the variable is a final
    private boolean isFinal;
    // Indicates whether the variable is initialized with a proper value
    private boolean isInitialized;
    // The block in which the variable is at
    private Block block;
    /** A list of the global variables */
    public static ArrayList<Variable> globalVariables;

    // Regex to check if the variable name is legal
    private static final String VALID_NAME = "(\\s*)[a-zA-z_][a-zA-Z0-9_]*";
    // Regex to check if a value is an int
    private static final String IS_INT = "(-)?(\\d)+";
    // Regex to check if a value is a double
    private static final String IS_DOUBLE = "(-)?(\\d)+(\\.\\d+)?";
    // Regex to check if a value is a boolean
    private static final String IS_BOOLEAN = "(-)?(\\d)+(\\.\\d+)?|(true)|(false)";
    // Regex to check if a value is a String
    private static final String STRING = "String";
    private static final String INT = "int";
    private static final String BOOLEAN = "boolean";
    private static final String DOUBLE = "double";
    private static final String CHAR = "char";
    private static final String TRUE = "true";
    private static final String FALSE = "false";

    /**
     * Builds a new variable.
     * @param name The variable's name.
     * @param type The variable's type.
     * @param value The value to assign to the variable.
     * @param isFinal true to final variable, false otherwise.
     * @param block The block in which the variable is at.
     * @throws VariableException In case of a problem in the variable.
     */
    public Variable(String name, String type, String value, boolean isFinal, Block block) throws
                                                                                        VariableException{
        this.name = name;
        this.type = type;
        this.block = block;
        if (!isNameValid()){
            throw new VariableException("Invalid variable name");
        }
        if (!isTypeValid()){
            throw new VariableException("Unknown variable type");
        }
        if (value != null){
            isInitialized = true;
        }
        if (!isValueValid(value)){
            throw new VariableException("value does not match the variable's type");
        }
        this.isFinal = isFinal;
        if (this.isFinal && !isInitialized){
            throw new VariableException("Final variable must be given a value");
        }
        globalVariables = Sjavac.instance().getVariables();
    }

    /**
     * Copy constructor.
     * @param variable The variable to make a copy of.
     */
    public Variable(Variable variable){
        this.name = variable.name;
        this.type = variable.type;
        this.isFinal = variable.isFinal;
        this.isInitialized = variable.isInitialized;
        this.block = variable.block;
    }

    /**
     * Update the variable's value.
     * @param value the new value.
     * @throws VariableException in case of the value does not match.
     */
    public void updateValue(String value) throws VariableException{
        if (!isValueValid(value)){
            throw new VariableException("value does not match the variable's type");
        }
        if (isFinal){
            throw new VariableException("Final variable can't be changed");
        }
        isInitialized = true;
    }

    // checks if the name is valid
    private boolean isNameValid(){
        if (name.equals("_")){
            return false;
        }
        Pattern p = Pattern.compile(VALID_NAME);
        Matcher m = p.matcher(name);
        return m.matches();
    }

    /**
     * Check whether the value can be assigned to the variable.
     * @param value The value to check.
     * @return true if the value can be assigned, false otherwise.
     * @throws VariableException If the value is a variable, but it was not found.
     */
    public boolean isValueValid(String value) throws VariableException{
        // No value
        if (value == null){
            return true;
        }
        Pattern p = Pattern.compile(VALID_NAME);
        Matcher m = p.matcher(value);
        // If the value is a valid variable's name, then the value is a variable
        if (m.matches() && !value.equals(TRUE) && !value.equals(FALSE)){
            // Trying to find this variable
            Variable variable = null;
            // Searching in the local variables, then in the globals
            if (block != null) {
                variable = block.findVariable(value);
            }
            else {
                for (int i = 0; i < globalVariables.size(); i++){
                    if (value.equals(globalVariables.get(i).getName())){
                        variable = globalVariables.get(i);
                    }
                }
            }
            // if variable is still null, it was not found
            if (variable == null){
                throw new VariableException("Variable not found");
            }
            // return true if the variable is in the right type and initialized
            return isCopyAssignmentLegal(variable);
        }
        if (STRING.equals(type)){
            return value.startsWith("\"") && value.endsWith("\"");
        }
        else if (INT.equals(type)){
            p = Pattern.compile(IS_INT);
            m = p.matcher(value);
            return m.matches();
        }
        else if (DOUBLE.equals(type)){
            p = Pattern.compile(IS_DOUBLE);
            m = p.matcher(value);
            return m.matches();
        }
        else if (CHAR.equals(type)){
            return value.startsWith("'") && value.endsWith("'") && value.length() == 3; // ' + char + ' == 3
        }
        else if (BOOLEAN.equals(type)){
            p = Pattern.compile(IS_BOOLEAN);
            m = p.matcher(value);
            return m.matches();
        }
        return false;
    }

    // checks if assignment of other variable to this variable is legal
    private boolean isCopyAssignmentLegal(Variable other){
        if (!other.isInitialized){
            return false;
        }
        if (this.type.equals(STRING)){
            return other.type.equals(STRING);
        }
        if (this.type.equals(INT)){
            return other.type.equals(INT);
        }
        if (this.type.equals(DOUBLE)){
            return other.type.equals(DOUBLE) || other.type.equals(INT);
        }
        if (this.type.equals(CHAR)){
            return other.type.equals(CHAR);
        }
        if (this.type.equals(BOOLEAN)){
            return other.type.equals(BOOLEAN) || other.type.equals(DOUBLE) || other.type.equals(INT);
        }
        return false;
    }
    // Returns true if the variable type is legal, false otherwise
    private boolean isTypeValid(){
        return (STRING.equals(type) || INT.equals(type) || BOOLEAN.equals(type) || CHAR.equals(type) ||
                DOUBLE.equals(type));
    }

    /**
     * @return The variable's name.
     */
    public String getName(){
        return name;
    }

    /**
     * @return The variable's type.
     */
    public String getType(){
        return type;
    }

    /**
     * @return true if the variable is initialized, false otherwise.
     */
    public boolean isInitialized(){
        return isInitialized;
    }

    /**
     * Set the parent of the variable.
     * @param block The block that the variable belongs to.
     */
    public void setBlock(Block block){
        this.block = block;
    }

    /**
     * Set the variable's initialized state.
     * @param condition true to make the variable initialized, false otherwise.
     */
    public void setInitialized(boolean condition){
        isInitialized = condition;
    }
}
