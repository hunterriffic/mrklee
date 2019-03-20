public class Lexeme implements Types
{
    String type;
    String string;
    int integer;
    double real;
    Lexeme left;
    Lexeme right;

    // FIXME initialize all variables in constructor
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

    // FIXME maybe not static?
    void display()
    {
        if (type == "STRING") System.out.println(type + ": \"" + string + "\"");
        else if (type == "INTEGER") System.out.println(type + ": " + integer);
        else if (type == "REAL") System.out.println(type + ": " + real);
        else if (type == "ID") System.out.println(type + ": " + string);
        else if (type != "UNKNOWN") System.out.println(type);
    }

    public static Lexeme cdr(Lexeme l)
        {
        return l.right;
        }


    public static void setCdr(Lexeme l, Lexeme s)
    {
        l.right = s;
    }


    public static Lexeme car(Lexeme l)
    {
        return l.left;
    }


    public static void setCar(Lexeme l, Lexeme s)
    {
        l.left = s;
    }
}