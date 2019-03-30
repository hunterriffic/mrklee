/**
 * Evaluator for MRKLEE, the Modern Right-reKursive Language for Everyday Expressions.
 * Designed and implemented by Hunter James, with guidance from Dr. John Lusth.
 */

import java.io.*;

public class Evaluator implements Types {
    public static boolean debug = true;

    public static Lexeme eval(Lexeme tree, Lexeme env)
        {
        if (debug) System.out.println(" [starting evaluation..] ");
        if (debug) System.out.println(" [tree.type is " + tree.type + "]");
        switch (tree.type) {
            case PROGRAM:
                return evalProgram(tree,env);
            case INTEGER:
                return tree;
            case REAL:
                return tree;
            case STRING:
                return tree;
            case CHAR:  // FIXME check
                return tree;
            case ID:
                return Environment.lookupEnv(tree.value.toString(),env);
            case FDEF:
                return evalFdef(tree,env);
//            case ZERO_PARAM_LIST:
//                break;
//            case PARAM_LIST:
//                break;
//            case VAR_DEC:
//                break;
            case BLOCK:
                return evalBlock(tree,env);
            case STATEMENT_LIST:
                return evalStatementList(tree,env);
//            case STATEMENT:
//                return evalStatement(tree,env);
//            case IF_STATE:
//                return evalIfState(tree,env);
//            case ALT_IF_STATE:  // FIXME maybe unnecessary?
//                break;
//            case OR_IF_STATE:   // FIXME maybe unnecessary?
//                break;
//            case IF_NONE_STATE: // FIXME maybe unnecessary?
//                break;
//            case EXPR:
//                return evalExpr(tree,env);
//            case UNARY:
//                break;
//            case ID_UNARY:
//                break;
//            case LAMBDA:
//                break;
            case FCALL:
                return evalFcall(tree,env);
//            case ZERO_ARG_LIST:
//                break;
//            case ARG_LIST:
//                break;
//            case LESS_THAN: // start check_value cases
//                break;
//            case LESS_EQUALS:
//                break;
//            case GREATER_THAN:
//                break;
//            case GREATER_EQUALS:
//                break;
//            case EQUAL_TO:
//                break;
//            case NOT_EQUAL_TO:
//                break;
//            case PLUS:  // start OP cases
//                break;
//            case MINUS:
//                break;
//            case TIMES:
//                break;
//            case BY:
//                break;
//            case WHILE_LOOP:
//                return evalWhileLoop(tree,env);
//            case PRINT:
//                return evalPrint(tree,env);
//            case PRINT_ITEM:
//                break;
//            case VAR_DEF:
//                return evalVarDef(tree,env);
//            case ASSIGNMENT:
//                return evalAssignment(tree,env);
//            case ID_STATEMENT:
//                break;
//            case RETURN_STATEMENT:
//                return evalReturn(tree,env);
            default:
                System.out.print(" ~bad " + tree.type + " expression~ ");
            }
        return null;
        }

    public static Lexeme evalProgram(Lexeme tree, Lexeme env)
        {
        return eval(tree.left, env);
        }

    public static Lexeme evalFcall(Lexeme tree, Lexeme env)
        {
        if (debug) System.out.println(" [evaluating fcall] ");
        Lexeme closure = Environment.lookupEnv(tree.left.value.toString(),env);
        Lexeme args = evalArgs(getArgs(tree),env);
//        // must determine if user-defined or built in
//        if (isBuiltIn(closure))
//            return evalBuiltIn(closure,args);
        Lexeme senv = closure.left; 	// static env
        Lexeme params = getParams(closure);
        Lexeme lenv = Environment.extendEnv(params,args,senv);
        Lexeme body = getBody(closure);
        return eval(body,lenv);
        }

    public static Lexeme evalFdef(Lexeme tree, Lexeme env)
        {
        if (debug) System.out.println(" [evaluating fdef] ");
        Lexeme closure = new Lexeme(CLOSURE);
        closure.left = new Lexeme(JOIN);
        closure.left.left = env;
        closure.left.right = tree;
        String fName = tree.left.value.toString();
        Environment.insertEnv(new Lexeme(FNAME,fName),closure,env);
        return tree;
        }

    public static Lexeme getArgs(Lexeme tree)
        {
        if (debug) System.out.println(" [getting args] ");
        // get arg list
        Lexeme curr = tree.left.left;
        if (curr.left != null)
            {
            Lexeme args = curr.left;
            curr = curr.right;
            while (curr.left != null)
                {
                Lexeme l = args;
                args = curr.left;
                args.right = l;
                curr = curr.right;
                }
            return args;
            }
        return null;
        }

    public static Lexeme evalArgs(Lexeme tree, Lexeme env)
        {
        if (debug) System.out.println(" [evaluating args] ");
        Lexeme argument = tree;
        Lexeme vals = null;
        while (argument != null)
            {
            Lexeme l = vals;
            vals = eval(argument,vals);
            vals.right = l;
            argument = argument.right;
            }
        return vals;
        }

    public static Lexeme getParams(Lexeme tree)
        {
        if (debug) System.out.println(" [getting params] ");
        Lexeme curr = tree.left.left;
        if (curr.left != null)
            {
            Lexeme params = curr.left.left;
            curr = curr.right;
            while (curr.left != null)
                {
                params.left = curr.left.left;
                curr = curr.right;
                }
            return params;
            }
        return null;
        }

    public static Lexeme getBody(Lexeme tree)
        {
        if (debug) System.out.println(" [getting body] ");
        return tree.left.right.left;
        }

    public static Lexeme evalBlock(Lexeme tree, Lexeme env)
        {
        if (debug) System.out.println(" [evaluating block] ");
        Lexeme statementList = tree.left;
        return evalStatementList(statementList,env);
        }

    public static Lexeme evalStatementList(Lexeme tree, Lexeme env)
        {
        if (debug) System.out.println(" [evaluating statement list] ");
        Lexeme statementList = tree;
        Lexeme curr = tree.left;
        Lexeme finished = null;
        while (curr != null)
            {
            finished = eval(curr,env);
            statementList = statementList.right;
            curr = statementList.left;
            }
        return finished;
        }

    public static void main(String[] args) throws IOException
        {
        String fileName = args[0];
        Lexeme env = Environment.createEnv();
        Lexeme tree = Parser.parse(fileName);
        eval(tree,env);
        }

}
