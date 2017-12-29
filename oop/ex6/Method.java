package oop.ex6;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class represents a method in the Sjavac file.
 */
public class Method extends Block {

    // The name of the method
    private String name;
    // The parameters of the method
    private ArrayList<Variable> parameters;

    // Regex to check if the method name is legal
    private static final String VALID_NAME = "(\\s*)[a-zA-z_][a-zA-Z0-9_]*";

    /**
     * Builds a new Method.
     * @param name The name of the method.
     * @param parameters The parameters of the method.
     * @param data A list represents the method's code in the file
     * @throws VariableException in case of duplicates of local variables.
     * @throws MethodException Problem in the method.
     */
    public Method(String name, ArrayList<Variable> parameters, ArrayList<String> data) throws
                                                                    VariableException, MethodException{
        this.name = name;
        for (int i = 0; i < parameters.size(); i++){
            Variable variable = parameters.get(i);
            // Parameters are always initialized
            variable.setInitialized(true);
            variable.setBlock(this);
        }
        // Checks for duplicates parameters
        for (int i = 0; i < parameters.size(); i++){
            Variable parameter1 = parameters.get(i);
            for (int j = 0; j < parameters.size(); j++){
                Variable parameter2 = parameters.get(j);
                if (i != j && parameter1.getName().equals(parameter2.getName())){
                    throw new VariableException("duplicates local variables are not allowed");
                }
            }
        }
        this.parameters = parameters;
        this.variables = new ArrayList<>(parameters);
        this.data = data;
        if (!isNameValid()){
            throw new MethodException("Method name is bad");
        }
        if (!hasReturn()){
            throw new MethodException("Method must has return");
        }
        this.globalVariables = new ArrayList<>();
    }

    // checks if the name is legal
    private boolean isNameValid(){
        Pattern p = Pattern.compile(VALID_NAME);
        Matcher m = p.matcher(name);
        return m.matches();
    }

    /**
     * @return The method name.
     */
    public String getName(){
        return name;
    }

    /**
     * @return The list of the method's parameters.
     */
    public ArrayList<Variable> getParameters(){
        if (parameters != null){
            return parameters;

        }
        return new ArrayList<>();
    }

    /**
     * Add new global variables to the method.
     * @param variables The variables to add.
     */
    public void addGlobalVariables(ArrayList<Variable> variables){
        globalVariables = variables;
    }

    // Check if the method ends with the reserve word return
    private boolean hasReturn(){
        final String HAS_RETURN = "\\s*return\\s*;\\s*";
        String line = data.get(data.size() - 2);
        Pattern p = Pattern.compile(HAS_RETURN);
        Matcher m = p.matcher(line);
        return m.matches();
    }
}
