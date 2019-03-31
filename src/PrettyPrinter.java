public class PrettyPrinter implements Types
    {
    public static void pp(Lexeme tree)
        {
        switch (tree.type)
            {
            case INTEGER:
                System.out.print(tree.value);
                break;
            case REAL:
                System.out.print(tree.value);
                break;
            case ID:
                System.out.print(tree.value);
                break;
            case STRING:
                System.out.print('\"' + tree.value.toString() + '\"');
                break;
            case CHAR:  // FIXME check
                System.out.print('\'' + tree.value.toString() + '\'');
                break;
            case MAIN:
                pp(tree.left);
                break;
            case PROGRAM:
                pp(tree.left);
                if (tree.right.left != null)
                    pp(tree.right.left);
                break;
            case FDEF:
                System.out.print(" funky ");
                pp(tree.left);
                System.out.print(" ( ");
                if (tree.right.left != null) pp(tree.right.left);
                System.out.print(" ) ");
                if (tree.right.right != null) pp(tree.right.right);
                break;
            case ZERO_PARAM_LIST:
                if (tree.left != null) pp(tree.left);
                break;
            case PARAM_LIST:
                pp(tree.left);
                if (tree.right != null)
                {
                    System.out.print(" , ");
                    pp(tree.right);
                }
                break;
            case VAR_DEC:
                System.out.print(" var ");
                pp(tree.left);
                break;
            case BLOCK:
                System.out.print(" { ");
                pp(tree.left);
                System.out.print(" } ");
                break;
            case STATEMENT_LIST:
                //System.out.print("[statement list]: ");
                pp(tree.left);
                if (tree.right != null)
                {
                    //System.out.print("[SL is not null]: ");
                    pp(tree.right);
                }
                break;
            case STATEMENT:
                pp(tree.left);
                if (tree.left.type.equals(ID_STATEMENT)) System.out.print(" ! ");
                break;
            case IF_STATE:
                System.out.print(" if ");
                pp(tree.left);
                System.out.print(" go ");
                pp(tree.right.left);
                if (tree.right.right != null) pp(tree.right.right.left);
                break;
            case ALT_IF_STATE:
                if (tree.left != null) pp(tree.left);
                break;
            case OR_IF_STATE:
                System.out.print(" or if ");
                pp(tree.left);
                System.out.print(" go ");
                pp(tree.right.left);
                if (tree.right.right != null) pp(tree.right.right.left);
                break;
            case IF_NONE_STATE:
                System.out.print(" ifnone go ");
                pp(tree.left);
                break;
            case EXPR:
                pp(tree.left);
                if (tree.right != null) pp(tree.right);
                break;
            case UNARY:
                if (tree.left != null) pp(tree.left);
                break;
            case ID_STATEMENT:
                //System.out.print("[idStatement]: ");
                pp(tree.left);
                System.out.print(" ");
                if (tree.right != null)
                {
                    pp(tree.right);
                    if (tree.right.right != null)
                    {
                        pp(tree.right.left);
                        pp(tree.right.right);
                    }
                }
                break;
/*            case ID_UNARY:
                //System.out.print("[ID_UNARY] ");
                pp(tree.left);
                if (tree.right != null) pp(tree.right);
                break;*/
            case LAMBDA:
                System.out.print(" lamdba ( ");
                pp(tree.right.left);
                System.out.print(" ) ");
                pp(tree.right.right);
                break;
            case FCALL:
                System.out.print(" ( ");
                if (tree.left != null) pp(tree.left);
                System.out.print(" ) ");
                break;
            case ZERO_ARG_LIST:
                //System.out.print("[zero_arg_list] ");
                if (tree.left != null) pp(tree.left);
                break;
            case ARG_LIST:
                pp(tree.left);
                if (tree.right != null)
                {
                    System.out.print(" , ");
                    pp(tree.right);
                }
                break;
            case LESS_THAN: // start check_value cases
                System.out.print(" < ");
                pp(tree.left);
                break;
            case LESS_EQUALS:
                System.out.print(" <= ");
                pp(tree.left);
                break;
            case GREATER_THAN:
                System.out.print(" > ");
                pp(tree.left);
                break;
            case GREATER_EQUALS:
                System.out.print(" >= ");
                pp(tree.left);
                break;
            case EQUAL_TO:
                System.out.print(" == ");
                pp(tree.left);
                break;
            case NOT_EQUAL_TO:
                System.out.print(" != ");
                pp(tree.left);
                break;
            case PLUS:  // start OP cases
                System.out.print(" + ");
                pp(tree.left);
                break;
            case MINUS:
                System.out.print(" - ");
                pp(tree.left);
                break;
            case TIMES:
                System.out.print(" * ");
                pp(tree.left);
                break;
            case BY:
                System.out.print(" / ");
                pp(tree.left);
                break;
            case MOD:
                System.out.print(" % ");
                pp(tree.left);
                break;
            case WHILE_LOOP:
                System.out.print(" while ");
                pp(tree.left);
                pp(tree.right);
                break;
            case PRINT:
                System.out.print(" yell (");
                pp(tree.left);
                System.out.print(" ) ! ");
                break;
            case PRINT_ITEM:
                if (tree.left.type.equals(STRING))
                {
                    System.out.print(" \" ");
                    pp(tree.left);
                    System.out.print(" \" ");
                }
                else
                {
                    pp(tree.left);
                }
                break;
            case VAR_DEF:
                pp(tree.left);
                System.out.print(" = ");
                pp(tree.right);
                System.out.print(" ! ");
                break;
            case ASSIGNMENT:
                System.out.print(" = ");
                pp(tree.left);
                break;
            case RETURN_STATEMENT:
                System.out.print(" record ");
                if (tree.left != null) pp(tree.left);
                System.out.print(" ! ");
                break;
            case ARRAY_DEC:
                System.out.print(" setlist ");
                pp(tree.left);
                System.out.print(" [ ");
                pp(tree.right);
                System.out.print(" ] ! ");
                break;
            case ARRAY_CALL:
                System.out.print(" [ ");
                pp(tree.left);
                System.out.print(" ] ");
                break;
            case ARRAY_ASSIGN:
                pp(tree.left);
                pp(tree.right.left);
                pp(tree.right.right);
                System.out.print(" ! ");
                break;
            default:
                System.out.print(" ~bad " + tree.type + " expression~ ");
                break;
            }
        }
    }
