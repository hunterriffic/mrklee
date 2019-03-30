public class Lexeme implements Types
{
    String type;
    Object value;
    Lexeme left;
    Lexeme right;

    public Lexeme(String t)
        {
        type = t;
        value = null;
        left = null;
        right = null;
        }

    public Lexeme(String t, Object v)
        {
        type = t;
        value = v;
        left = null;
        right = null;
        }

    //  String type
    public Lexeme(String t, String str)
        {
        type = t;
        value = str;
        left = null;
        right = null;
        }

    // Character type
    public Lexeme(String t, char c)
        {
        type = t;
        value = c;
        left = null;
        right = null;
        }


    // Integer type
    public Lexeme(String t, int i)
        {
        type = t;
        if (t.equals("UNKNOWN"))
            {
            System.out.println("A lexing error occurred at line " + Lexer.lineNumber + ". Exiting the program.");
            System.exit(0);
            }
        value = i;
        left = null;
        right = null;
        }

    // Real type
    public Lexeme(String t, double r)
        {
        type = t;
        value = r;
        left = null;
        right = null;
        }

    // for cons
    public Lexeme(String t, Lexeme l, Lexeme r)
        {
        type = t;
        left = l;
        right = r;
        value = null;
        }

    void display()
        {
        if (type == "STRING") System.out.println(type + ": \"" + value + "\"");
        else if (type == "INTEGER") System.out.println(type + ": " + value);
        else if (type == "REAL") System.out.println(type + ": " + value);
        else if (type == "ID") System.out.println(type + ": " + value);
        else if (type == "CHAR") System.out.println(type + ": \'" + value + "\'");
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