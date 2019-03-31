public class Lexeme implements Types
{
    String type;
    Object value;
    Lexeme left;
    Lexeme right;
    boolean bool;
    Lexeme[] arr;

    public Lexeme(String t)
        {
        type = t;
        value = null;
        left = null;
        right = null;
        bool = false;
        arr = null;
        }

    public Lexeme(String t, Object v)
        {
        type = t;
        value = v;
        left = null;
        right = null;
        bool = false;
        arr = null;
        }

    public Lexeme(String t, Lexeme[] array)
        {
        type = t;
        arr = array;
        value = null;
        left = null;
        right = null;
        bool = false;
        }
    // for conditionals
    public Lexeme(String t, boolean tf)
        {
        type = t;
        value = null;
        left = null;
        right = null;
        bool = tf;
        arr = null;
    }

    //  String type
    public Lexeme(String t, String str)
        {
        type = t;
        value = str;
        left = null;
        right = null;
        bool = false;
        arr = null;
        }

    // Character type
    public Lexeme(String t, char c)
        {
        type = t;
        value = c;
        left = null;
        right = null;
        bool = false;
        arr = null;
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
        bool = false;
        arr = null;
        }

    // Real type
    public Lexeme(String t, double r)
        {
        type = t;
        value = r;
        left = null;
        right = null;
        bool = false;
        arr = null;
        }

    // for cons
    public Lexeme(String t, Lexeme l, Lexeme r)
        {
        type = t;
        left = l;
        right = r;
        value = null;
        bool = false;
        arr = null;
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

    public static void printDebug(Lexeme l)
        {
        System.out.println("[[printDebug]]");
        if (l == null) {System.out.println("This lexeme is null."); return;}
        String type = l.type;
        Object value = l.value;
        if (type == null) System.out.println("Type is NULL.");
        else if (type.equals("STRING")) System.out.println(type + ": \"" + value + "\"");
        else if (type.equals("INTEGER")) System.out.println(type + ": " + value);
        else if (type.equals("REAL")) System.out.println(type + ": " + value);
        else if (type.equals("ID")) System.out.println(type + ": " + value);
        else if (type.equals("CHAR")) System.out.println(type + ": \'" + value + "\'");
        else if (type != "UNKNOWN")
            {
            System.out.println("type: " + type);
            if (l.left != null) System.out.println("left: " + l.left.type);
            else System.out.println("left: null");
            if (l.right != null) System.out.println("right: " + l.right.type);
            else System.out.println("right: null");
            }
        }

}