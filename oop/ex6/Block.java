package oop.ex6;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class represents a block in the Sjavac file, which is a method or a condition block (if/while).
 */
public abstract class Block {

    /** A list represents the block's code in the file. */
    protected ArrayList<String> data;
    /** The Block that contains this block. */
    protected Block parent;
    /** A list of the local variables. */
    protected ArrayList<Variable> variables;
    /** A list of the global variables. */
    public ArrayList<Variable> globalVariables;
    /** This list has all the calls for methods in the Sjavac file */
    public static ArrayList<String> calledMethods;

    // Regex for a variable declaration.
    private static final String VARIABLE_DECLARATION = "\\s*(String|int|double|boolean|char|final)\\s*.*";
    // Regex for a variable assignment.
    private static final String VARIABLE_ASSIGNMENT = "(\\s*)[a-zA-z_][a-zA-Z0-9_]*\\s*=\\s*\\S+";
    // Regex for a condition block.
    private static final String CONDITION_BLOCK_TITLE = "\\s*(if|while)\\s*(.*)\\s*\\{\\s*";
    // Regex to remove the whitespace in the beginning of the line.
    private static final String REMOVE_FIRST_WHITESPACES = "\\S.*";
    // Regex the check if the line ends with a ; .
    private static final String END_WITH_SEMICOLON = ".*;\\s*";
    // Regex to check in the line is the reserve word return.
    private static final String RETURN = "\\s*return\\s*;\\s*";

    /**
     * This methods checks whether the block is legal or not.
     * @throws VariableException Problem with a variable.
     * @throws MethodException Problem with a method.
     */
    public void run() throws VariableException, MethodException{
        for (int i = 1; i < data.size() - 1; i++){
            String line = data.get(i);
            Pattern p = Pattern.compile(REMOVE_FIRST_WHITESPACES);
            Matcher m = p.matcher(line);
            if (m.find()){
                line = line.substring(m.start(), m.end());
            }
            p = Pattern.compile(CONDITION_BLOCK_TITLE);
            m = p.matcher(line);
            if (m.matches()){
                int upperBound = Parsing.getBlockIndex(i, data);
                ArrayList<String> blockData = new ArrayList<>(data.subList(i, upperBound + 1));
                ConditionBlock ifWhile = new ConditionBlock(this, blockData, globalVariables);
                // Checking if the inner block is legal as well
                ifWhile.run();
                // Skipping the condition method part
                i = upperBound;
                continue;
            }
            p = Pattern.compile(VARIABLE_DECLARATION);
            m = p.matcher(line);
            if (m.matches()){
                ArrayList<Variable> newVariables = VariableFactory.createVariable(line, this);
                addVariable(newVariables);
                continue;
            }
            p = Pattern.compile(VARIABLE_ASSIGNMENT);
            m = p.matcher(line);
            if (m.matches()){
                String[] variableInfo = line.split("\\s+");
                String name = variableInfo[0];
                String value = variableInfo[2];
                p = Pattern.compile(END_WITH_SEMICOLON);
                m = p.matcher(value);
                if (m.find()){
                    value = value.substring(0, value.lastIndexOf(";"));
                }
                else {
                    throw new VariableException("Variable assignment must end with ;");
                }
                updateVariable(name, value);
                continue;
            }
            p = Pattern.compile(RETURN);
            m = p.matcher(line);
            if (m.matches()){
                continue;
            }
            // this line is a call for another method
            calledMethods.add(line);
        }
    }

    /**
     * Adds new variables.
     * @param variable the variables to be added.
     * @throws VariableException in case of duplicates variables.
     */
    protected void addVariable(ArrayList<Variable> variable) throws VariableException{
        for (int i = 0; i < variable.size(); i++){
            Variable newVariable = variable.get(i);
            for (int j = 0; j < variables.size(); j++){
                if (newVariable.getName().equals(variables.get(j).getName())){
                    throw new VariableException("duplicates variables are not allowed");
                }
            }
            variables.add(newVariable);
        }
    }

    /**
     * Assigning new value for existing variable.
     * @param name The name of the variable.
     * @param value The value to assign.
     * @throws VariableException If the variable is not found or the value is illegal.
     */
    protected void updateVariable(String name, String value) throws VariableException {
        Variable variable = findVariable(name);
        variable.updateValue(value);
    }

    /**
     * Finds a variable. Goes from the current scope, until reaches the global variables.
     * @param name The name of the variable to find.
     * @return a variable that has the same name as name.
     * @throws VariableException If the variable wasn't found.
     */
    protected Variable findVariable(String name) throws VariableException{
        Block currentBlock = this;
        while (currentBlock != null){
            ArrayList<Variable> parentVariables = currentBlock.variables;
            for (int i = 0; i < parentVariables.size(); i++) {
                Variable variable = parentVariables.get(i);
                if (name.equals(variable.getName())) {
                    return variable;
                }
            }
            currentBlock = currentBlock.parent;
        }
        // Not a local variable. Searching in the global variables now
        for (int i = 0; i < globalVariables.size(); i++) {
            Variable variable = globalVariables.get(i);
            if (name.equals(variable.getName())) {
                return variable;
            }
        }
        throw new VariableException("Variable not found");
    }
}
