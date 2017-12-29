package oop.ex6;

import oop.ex6.main.Sjavac;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.regex.*;

/**
 * This class is responsible for reading data from the Sjavac file, and to make the Variables and Methods
 * lists for the main class.
 */
public class Parsing {

    // Regex for empty or comments lines
    private static final String TO_BE_IGNORED = "(^//.*|\\s*)";
    // Regex for {
    private static final String OPEN_BRACKET = ".+[{]\\s*";
    // Regex for }
    private static final String CLOSE_BRACKET = "\\s*}\\s*";
    // Regex for a method line
    private static final String METHOD_PATTERN = "\\s*void\\s+[A-Za-z].*[(].*[)]\\s*[{]\\s*";
    // Regex for a variable declaration.
    private static final String VARIABLE_DECLARATION = "\\s*(String|int|double|boolean|char|final)\\s*.*";
    // Regex to check if a line ends with ;
    private static final String END_WITH_SEMICOLON = ".*;\\s*";

    // Reading the commands file and makes an array out of it
    private static String[] readFile(String file) throws IOException {
        LinkedList<String> data = new LinkedList<String>();
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line = br.readLine();
        // reading the file
        while (line != null) {
            data.add(line);
            line = br.readLine();
        }
        br.close();
        // Casting the data from LinkedList to array
        String[] arrayData = new String[data.size()];
        for (int i = 0; i < data.size(); i++) {
            arrayData[i] = data.get(i);
        }
        return arrayData;
    }

    /**
     * @param file The full path of the file.
     * @return Returns a list of the Sjavac file without empty or comments lines.
     * @throws IOException if the file couldn't accessed.
     */
    public static ArrayList<String> reducedJavac(String file) throws IOException{
        String[] data = readFile(file);
        Pattern pattern = Pattern.compile(TO_BE_IGNORED);
        ArrayList<String> reducedData = new ArrayList<>();
        for (String line : data){
            Matcher matcher = pattern.matcher(line);
            // removes empty and comments lines
            if (!matcher.matches()){
                reducedData.add(line);
            }
        }
        return reducedData;
    }

    /**
     * Updates the variables and methods lists of Sjavac instance.
     * @param data The list containing the data of the file.
     * @throws VariableException Problem with a variable.
     * @throws MethodException Problem with a method.
     */
    public static void createData(ArrayList<String> data) throws VariableException, MethodException {
        Sjavac file = Sjavac.instance();
        for (int i = 0; i < data.size(); i++) {
            String line = data.get(i);
            Pattern pattern = Pattern.compile(METHOD_PATTERN);
            Matcher matcher = pattern.matcher(line);
            if (matcher.matches()) {
                int endBlock = Parsing.getBlockIndex(i, data);
                ArrayList<String> section = new ArrayList<>(data.subList(i, endBlock + 1));
                // Skipping the method part
                i = endBlock;
                // Building the new method
                Method method = MethodFactory.createMethod(section);
                file.addMethod(method);
                continue;
            }
            pattern = Pattern.compile(VARIABLE_DECLARATION);
            matcher = pattern.matcher(line);
            if (matcher.matches()){
                file.addVariables(VariableFactory.createVariable(data.get(i), null));
            }
            // If the line is for assigning a new value for a variable:
            else {
                String[] variableInfo = line.split("\\s+");
                String name = variableInfo[0];
                if (variableInfo.length < 3){
                    throw new VariableException("Something went wrong");
                }
                String value = variableInfo[2];
                pattern = Pattern.compile(END_WITH_SEMICOLON);
                matcher = pattern.matcher(value);
                if (matcher.find()){
                    value = value.substring(0, value.lastIndexOf(";"));
                }
                else {
                    throw new VariableException("Variable assignment must end with ;");
                }
                file.updateVariable(name, value);
            }
        }
    }

    /**
     * The method is given the first line of a block, and returns the last line of it.
     * @param lineNum The first line of the block.
     * @param data The file that has the block.
     * @return The last line of the block.
     * @throws MethodException In case there is a problem with the parentheses { }.
     */
    public static int getBlockIndex(int lineNum, ArrayList<String> data) throws MethodException {
        Pattern patternOpenBracket = Pattern.compile(OPEN_BRACKET);
        Pattern patternCloseBracket = Pattern.compile(CLOSE_BRACKET);
        int counter = 1;
        for (int i = lineNum + 1; i < data.size(); i++) {
            String line = data.get(i);
            Matcher openMatcher = patternOpenBracket.matcher(line);
            Matcher closeMatcher = patternCloseBracket.matcher(line);
            if (openMatcher.matches()) {
                counter++;
            }
            else if (closeMatcher.matches()) {
                counter--;
            }
            if (counter == 0) {
                return i;
            }
        }
        throw new MethodException("Problem with the parentheses");
    }
}
