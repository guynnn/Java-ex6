# Java-ex6


=============================
=      File description     =
=============================
1. README - this file.

2. Block.java - An abstract class represents block in a sjava file.

3. ConditionBlock.java - A specific block in sjava file, represents if or while blocks.

4. Method.java - represents a method in our program.

5. MethodException.java - holds an informative message for a certain error in a method.

6. MethodFactory.java - responsible for creating method objects.

7. Parsing.java - Parses the given sjava file and collects the created methods and variables.

8. Variable.java - Represents a variable in a sjava file.

9. VariableException.java - holds an informative message for a certain  variable error.

10. VariableFactory.java - responsible for creating variables objects.

11. Sjavac.java - The main class. Runs the whole program using the other objects.

=============================
=          Design           =
=============================
Our program representing a verifier for a simple java file (sjava). By a given sjava file, it checks
whether this file is legal (i.e. has a valid code according to certain criteria) and prints 0 for valid
code, 1 for illegal code or 2 in case of IO errors.

In order to implement this task, we divided it to small units, each one has its own task; methods which
contains if/while blocks, variables, a parser (for the reading file task) and the Sjavac class (manager).
By given a source sjava file, the parsing class parses its lines and creates methods and variables
accordingly, by calling the proper factory. So it collects all of the variables and methods exist in a file.
Method and ConditionBlock are both treated as block in our design, and both extend Block.
Each Block (method of condition block) has the functionality to check its inner lines and signals if there is
an illegal line.
In addition, a Block takes part of creating if/while blocks in case such conditional blocks exists in its own
lines. Variables, when created, holds necessary information about themselves such as its type and value and
has the functionality for update their data according to the following sjava code and in accordance with
the sjava criteria.
In case an illegal line code, we should inform the user about that, so we also created two exception class:
MethodException which informs the user about errors occurred inside a method lines and VariableException
which informs about variety of errors belongs to variables, each error invokes unique error message.
The main method, which is a Singleton, gets the lists of variables and methods from the Parsing class.
It then runs each one of the method by calling to the "run" method that Block defines.

=============================
=  Implementation details   =
=============================


=============================
=  Questions   =
=============================
***NOTE: we've mentioned earlier how the exceptions are handled in our program***

1. In order to add new types of variables, we would only need to change some method in the Variable class
   (e.g. isValueValid(), isTypeValid() etc), and add a case with the new types. The rest of the code won't
   be changed.

2. In order to support switch statements in our program we would add another class, switch block. 
   We already have an abstract class - block. So switch block will extend block, thus it will support the
   self-check method (that block class has). In each case statement line we would check wether the value
   after the reserved keyword case is valid.
   
3. Support operators: we already save the value of each variable as a data member in Variable class. So, we
   would save the reserved signs of operations (i.e '-', '+', '*') and by use of a proper regex, we would
   change the value of x (i.e. in case x = 3-1).
   We would determine weather this line is valid or not by checking each variable in this equation.

**** Regex Questions ****

1. First regex: ".*;\\s*" - This regex checks that a line ends with ; .  .* search for any string with
   any\length, then ; demands     that ; must be there, and then \\s* demands that only whitespaces can be
   placed after the ;.

2. Second regex: "(\s*)[a-zA-z_][a-zA-Z0-9_]*" - This regex checks that a name of variable is legal. \s* is
   there because there might be whitespaces before the name, then [a-zA-z_] so the first letter is only
   letter or digit, and finally [a-zA-Z0-9_]* because the rest of the name can be any digit or letter or _ in
   any length.
