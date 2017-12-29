package oop.ex6;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class Builds Method objects.
 */
public class MethodFactory {

    // Regex to get rid of the whitespace between the parameters
    private static final String GET_PARAMETER_INFO = "(\\S)+";
    // Regex to get what inside the parentheses ()
    private static final String GET_PARAMETERS_PARENTHESES = "\\(.*\\)";
    // Regex to get the method name
    private static final String GET_NAME = "[a-zA-Z0-9_]+";

    /**
     * Builds a new Method.
     * @param data A list represents the method's code in the file.
     * @return a new method.
     * @throws VariableException Problem with the method parameters.
     * @throws MethodException Problem with the declaration of the method.
     */
    public static Method createMethod(ArrayList<String> data) throws VariableException, MethodException {
        String name = getName(data.get(0));
        ArrayList<Variable> parameters = getParameters(data.get(0));
        return new Method(name, parameters, data);
    }

    // Returns the method name
    private static String getName(String line) throws MethodException{
        Pattern p = Pattern.compile("\\w");
        Matcher m = p.matcher(line);
        if (m.find()){
            // Getting rid of the first whitespaces at the beginning of the title
            line = line.substring(m.start());
        }
        String[] data = line.split("(\\s+|\\(.*\\))");
        if (data[2].matches(GET_NAME)){
            throw new MethodException("Bad method name");
        }
        return data[1];
    }

    // data is the parameters in this form: (type1 name1, ..., typeN nameN)
    private static ArrayList<Variable> getParameters(String line) throws VariableException, MethodException{
        Pattern pattern = Pattern.compile(GET_PARAMETERS_PARENTHESES);
        Matcher matcher = pattern.matcher(line);
        String data;
        if (matcher.find()){
            data = line.substring(matcher.start(), matcher.end());
        }
        else {
            throw new MethodException("Method has no parentheses");
        }
        data = data.substring(1, data.length() - 1); // getting rid of the (..)
        if (data.length() == 0){ // There are no parameter
            return new ArrayList<>();
        }
        String[] arrayParameters = data.split(",");
        // Now arrayParameters has the parameters in it
        ArrayList<Variable> parameters = new ArrayList<>();
        for (int i = 0; i < arrayParameters.length; i++){
            Pattern p = Pattern.compile(GET_PARAMETER_INFO);
            Matcher m = p.matcher(arrayParameters[i]);
            String type = "";
            String name = "";
            if (m.find()) {
                // First one is the type
                type = arrayParameters[i].substring(m.start(), m.end());
            }
            if (m.find()) {
                // Second is the name
                name = arrayParameters[i].substring(m.start(), m.end());
            }
            if (m.find()){
                // There can't be a third word in a variable declaration
                throw new MethodException("Parameter is not declared well");
            }
            // parameter's value is always null, and always not final
            Variable variable = new Variable(name, type, null, false, null);
            parameters.add(variable);
        }
        return parameters;
    }
}
