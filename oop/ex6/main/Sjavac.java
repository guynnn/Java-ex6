package oop.ex6.main;

import oop.ex6.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This is the main class. Represents a Sjavac file.
 */
public class Sjavac {

    // The only Sjavac instance
    private static Sjavac file = new Sjavac();
    // A list of all the methods in the file
    private ArrayList<Method> methods;
    // A list of all the global variable in the file
    private ArrayList<Variable> variables;

    // Builds the one and only one instance
    private Sjavac() {
        this.methods = new ArrayList<>();
        this.variables = new ArrayList<>();
    }

    /**
     * @return The Sjavac instance.
     */
    public static Sjavac instance() {
        return file;
    }

    /**
     * @return The global variables of the file.
     */
    public ArrayList<Variable> getVariables() {
        return variables;
    }

    /**
     * Adds new global Variable to the variables list.
     * @param variables The new variables to add.
     */
    public void addVariables(ArrayList<Variable> variables) {
        this.variables.addAll(variables);
    }

    /**
     * @return The methods of the file.
     */
    public ArrayList<Method> getMethods() {
        return methods;
    }

    /**
     * Adds new global Variable to the variables list.
     * @param method The new method to add.
     */
    public void addMethod(Method method) {
        this.methods.add(method);
    }

    // Checks whether called method in the file are legal
    private void checkCalledMethods() throws VariableException, MethodException{
        // Regex for getting the method name
        final String METHOD_NAME = "[a-zA-Z0-9_]*";
        // Regex to get what inside the parentheses ()
        final String PARAMETERS_VALUES = "\\(.*\\)";
        // Regex to check if the call ends with a ;
        final String END_WITH_SEMICOLON = ".*;\\s*";
        // A list of the called method exactly as they appear in the Sjavac file
        ArrayList<String> calledMethods = Method.calledMethods;
        for (int i = 0; i < calledMethods.size(); i++){
            String calledMethod = calledMethods.get(i);
            String name = "";
            Pattern p = Pattern.compile(END_WITH_SEMICOLON);
            Matcher m = p.matcher(calledMethod);
            if (!m.matches()){
                throw new MethodException("Called method line must end with ;");
            }
            p = Pattern.compile(METHOD_NAME);
            m = p.matcher(calledMethod);
            if (m.find()){
                name = calledMethod.substring(m.start(), m.end());
            }
            Method method = findMethod(name);
            if (method == null){
                throw new MethodException("Called method did not found");
            }
            p = Pattern.compile(PARAMETERS_VALUES);
            m = p.matcher(calledMethod);
            String parametersName;
            if (m.find()){
                parametersName = calledMethod.substring(m.start() + 1, m.end() - 1);
            }
            else {
                throw new MethodException("Bad call for a method");
            }
            String[] parameters = parametersName.split("\\s*,\\s*");
            ArrayList<Variable> originalParameters = method.getParameters();
            if (parameters[0].equals("") && (originalParameters.size() == 0 && parameters.length == 1)){
                continue;
            }
            if (originalParameters.size() != parameters.length){
                throw new MethodException("Method called with wrong number of parameters");
            }
            for (int j = 0; j < parameters.length; j++){
                String currentParameter = parameters[j].replaceAll("\\s+", "");
                if (!originalParameters.get(j).isValueValid(currentParameter)){
                    throw new MethodException("parameters value does not match");
                }
            }
        }
    }

    /**
     * Updates a global variable's value.
     * @param name The name of the variable.
     * @param value The new value to assign.
     * @throws VariableException In case that the value is in the type.
     */
    public void updateVariable(String name, String value) throws VariableException{
        Variable variable = findVariable(name);
        variable.updateValue(value);
    }

    // Given a name of variable. Returns the Variable's Object
    private Variable findVariable(String name) throws VariableException{
        for (int i = 0; i < variables.size(); i++){
            Variable variable = variables.get(i);
            if (name.equals(variable.getName())){
                return variable;
            }
        }
        throw new VariableException("Variable not found");
    }

    // Given a method name, return the Method's Object
    private Method findMethod(String name){
        for (int i = 0; i < methods.size(); i++){
            Method method = methods.get(i);
            if (name.equals(method.getName())){
                return method;
            }
        }
        return null; // not found
    }

    private void clear(){
        Method.calledMethods = new ArrayList<>();
        variables = new ArrayList<>();
        methods = new ArrayList<>();
        Variable.globalVariables = file.getVariables();
    }

    /**
     * Runs the program.
     * @param args a single String represents the path of the Sjavac file.
     */
    public static void main(String[] args){
        try {
            Sjavac file = Sjavac.instance();
            // Initialize the lists
            file.clear();
            ArrayList<String> data = Parsing.reducedJavac(args[0]);
            Parsing.createData(data); // This line updates the methods and variables of file
            ArrayList<Variable> variables = file.getVariables();
            ArrayList<Method> methods = file.getMethods();
            // This for-loop makes a deep-copy of the global variables list to each of the methods
            for (int i = 0; i < methods.size(); i++) {
                ArrayList<Variable> toAdd = new ArrayList<>();
                Method method = methods.get(i);
                for (int j = 0; j < variables.size(); j++) {
                    Variable variable = new Variable(variables.get(j));
                    toAdd.add(variable);
                }
                method.addGlobalVariables(toAdd);
            }
            // Every method check itself now
            for (int i = 0; i < methods.size(); i++){
                Method method = methods.get(i);
                method.run();
            }
            file.checkCalledMethods();
            System.out.println(0);
        } catch (IOException i){
            System.err.println("error");
            System.out.println(2);
        } catch (MethodException m){
            System.err.println(m.getMessage());
            System.out.println(1);
        } catch (VariableException v){
            System.err.println(v.getMessage());
            System.out.println(1);
        }
    }
}
