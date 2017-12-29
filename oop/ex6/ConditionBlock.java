package oop.ex6;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class represents a condition block (if/while).
 */
public class ConditionBlock extends Block {

    // Regex to get what's inside the parentheses ()
    private static final String GET_BLOCK_PARAMETERS = "\\(.+\\)";
    // Regex to get the actual parameters with any whitespace into an array.
    private static final String WITHOUT_SPACE = "\\s*(&&|[|]{2})\\s*";
    private static final String FALSE = "false";
    private static final String TRUE = "true";
    private static final String BOOLEAN = "boolean";
    private static final String INT = "int";
    private static final String DOUBLE = "double";

    /**
     * Builds a new condition block.
     * @param parent The block that wraps this block.
     * @param data A list represents the block's code in the file.
     * @param globals A list of the global variables.
     * @throws VariableException Problem with a variable.
     * @throws MethodException Problem with a method.
     */
    public ConditionBlock(Block parent, ArrayList<String> data, ArrayList<Variable> globals) throws
                                                                VariableException, MethodException{
        this.data = data;
        this.globalVariables = globals;
        this.variables = new ArrayList<>();
        this.parent = parent;
        isTitleValid();
    }

    // Gets what inside the parentheses (), excluding the parentheses
    private String getTitle() throws MethodException{
        Pattern p = Pattern.compile(GET_BLOCK_PARAMETERS);
        Matcher m = p.matcher(data.get(0));
        if (m.find()){
            return data.get(0).substring(m.start() + 1, m.end() - 1); // excludes parentheses.
        }
        throw new MethodException("Condition is not declared well");
    }

    /**
     * Check if the condition block's title is legal.
     * @throws MethodException In the title in not declared well.
     * @throws VariableException In case there is uninitialized parameters or wrong parameter's type.
     */
    public void isTitleValid() throws MethodException, VariableException{
        Pattern p = Pattern.compile("\\w+\\s+\\w+");
        Matcher m = p.matcher(data.get(0));
        if (m.find()){
            throw new MethodException("If/While block is bad");
        }
        String name = getTitle();
        // Now we have an array of the parameters
        String[] conditions = name.split(WITHOUT_SPACE);
        for (int i = 0; i < conditions.length; i++){
            String condition = conditions[i].replaceAll("\\s*", "");
            if (condition.equals(TRUE) || condition.equals(FALSE)){
                continue;
            }
            p = Pattern.compile("\\d*(\\.\\d)?\\d*");
            m = p.matcher(condition);
            // In case the parameter is a double or int. This parameter is legal, so we just go to next
            // parameter
            if (m.matches()){
                continue;
            }
            Variable variable = findVariable(condition);
            if (!variable.isInitialized()){
                throw new VariableException("Using uninitialized variable is forbidden");
            }
            String type = variable.getType();
            if (!(type.equals(BOOLEAN) || type.equals(INT) || type.equals(DOUBLE))){
                throw new VariableException("Wrong variable's type in the condition");
            }
        }
    }
}
