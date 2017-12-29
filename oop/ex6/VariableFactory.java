package oop.ex6;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class builds the variables in the Sjavac file.
 */
public class VariableFactory {

    // Regex to check if a line ends with a ;
    private static final String END_WITH_SEMICOLON = ".*;\\s*";

    /**
     * Builds a new Variables.
     * @param line The declaration line from the Sjavac file.
     * @param block The block the variable belongs to.
     * @return Variables declared in the given line.
     * @throws VariableException In case of a problem in the variables declaration.
     */
    public static ArrayList<Variable> createVariable(String line, Block block) throws VariableException{
        ArrayList<Variable> variables = new ArrayList<>();
        String type;
        boolean isFinal;
        Pattern p = Pattern.compile("\\S.*");
        Matcher m = p.matcher(line);
        if (m.find()){
            // Getting rid of unwanted whitespaces
            line = line.substring(m.start(), m.end());
        }
        String[] data = line.split("\\s+");
        if (data[0].equals("final")){
            isFinal = true;
            type = data[1];
            data = Arrays.copyOfRange(data, 2, data.length);
        }
        else {
            isFinal = false;
            type = data[0];
            data = Arrays.copyOfRange(data, 1, data.length);
        }
        if (!isValid(data)){
            throw new VariableException("Variable declaration is bad");
        }
        String result = "";
        for (int i = 0; i < data.length; i++) {
            result += data[i];
        }
        p = Pattern.compile(END_WITH_SEMICOLON);
        m = p.matcher(result);
        if (m.find()){
            result = result.substring(0, result.lastIndexOf(";") + 1);
        }
        else {
            throw new VariableException("Variable declaration must end with ;");
        }
        result = result.substring(0, result.length() - 1);
        data = result.split(",");
        for (int i = 0; i < data.length; i++){
            if (!data[i].contains("=")){
                String name = data[i];
                variables.add(new Variable(name, type, null, isFinal, block));
            }
            else {
                String[] currentVariable = data[i].split("=");
                if (currentVariable.length == 1){
                    throw new VariableException("ERROR: bad syntax, there is a = but not a value");
                }
                String name = currentVariable[0];
                String value = currentVariable[1];

                variables.add(new Variable(name, type,value, isFinal, block));
            }
        }
        return variables;
    }

    // check if there is whitespaces between two variables names or two values
    private static boolean isValid(String[] data){
        for (int i = 0; i < data.length - 1; i++){
            String first = data[i];
            String second = data[i + 1];
            if (first.matches("[a-zA-Z0-9_]+") && second.matches("[a-zA-Z0-9_]+;?")){
                return false;
            }
        }
        return true;
    }
}
