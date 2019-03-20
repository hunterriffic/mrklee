public class Lexeme implements Types
{
    static String type;
    static String string;
    static int integer;
    static double real;
    static Lexeme left;
    static Lexeme right;

    public Lexeme(String t)
    {
        type = t;
    }

    //  String type
    public Lexeme(String t, String str)
    {
        type = t;
        string = str;
    }

    // Integer type
    public Lexeme(String t, int i)
    {
        type = t;
        if (t == "UNKNOWN")
        {
            System.out.println("A lexing error occurred at line " + Lexer.lineNumber + ". Exiting the program.");
            System.exit(0);
        }
        integer = i;
    }

    // Integer type
    public Lexeme(String t, double r)
    {
        type = t;
        real = r;
    }

    // for cons
    public Lexeme(String t, Lexeme l, Lexeme r)
    {
        type = t;
        left = l;
        right = r;
    }

    static void display(Lexeme l)
    {
        if (type == "STRING") System.out.println(l.type + ": \"" + l.string + "\"");
        else if (type == "INTEGER") System.out.println(l.type + ": " + l.integer);
        else if (type == "REAL") System.out.println(l.type + ": " + l.real);
        else if (type == "ID") System.out.println(l.type + ": " + l.string);
        else if (type != "UNKNOWN") System.out.println(l.type);
    }

    public static Lexeme cdr(Lexeme l)
        {
        return right;
        }


    public static void setCdr(Lexeme l, Lexeme s)
    {
        right = s;
    }


    public static Lexeme car(Lexeme l)
    {
        return left;
    }


    public static void setCar(Lexeme l, Lexeme s)
    {
        left = s;
    }
}