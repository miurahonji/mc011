import java.io.*;

import Minijava.node.*;
import Minijava.lexer.*;
import Minijava.parser.*;

public class RunTeste
{
    public static void main(String[] arguments)
    {
        try
        {
            if(arguments.length != 1)
            {
                System.out.println("usage:");
                System.out.println("  java Main filename");
                System.exit(1);
            }

            Lexer lexer = new Lexer(
				new PushbackReader(
                new BufferedReader(
                new FileReader(arguments[0])), 1024));
            Parser parser = new Parser(lexer);
            Start ast = parser.parse();

            System.out.println(ast);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            System.out.println(e);
            System.exit(1);
        }
    }
}
// Just a test...
