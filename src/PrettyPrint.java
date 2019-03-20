/**
 * Pretty Printer for MRKLEE, the Modern Right-reKursive Language for Everyday Expressions.
 * Designed and implemented by Hunter James, with guidance from Dr. John Lusth.
 */

public class PrettyPrinter
    {
    public static void prettyPrint(Lexeme tree)
        {
        switch (tree.type)
            {
            case INTEGER:
                System.out.println(tree.integer);
            case REAL:
                System.out.println(tree.real);
            case VARIABLE:
                System.out.println(tree.string);
            case STRING:
                System.out.println('\"' + tree.string + '\"');
            case OPAREN:
                System.out.println("(");
                prettyPrint(tree.right);
                System.out.println(")");
            case MINUS:
                System.out.println("-");
                System.out.println(tree.right);
            case PLUS:
                prettyPrint(tree.left);
                System.out.println(" + ");
                prettyPrint(tree.right);
            default:
                System.out.println("bad expression!");
            }
        }
    }