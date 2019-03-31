/**
 * Evaluator for MRKLEE, the Modern Right-reKursive Language for Everyday Expressions.
 * Designed and implemented by Hunter James, with guidance from Dr. John Lusth.
 */

import java.io.*;

public class Evaluator implements Types {
    public static boolean debug = false;

    public static Lexeme eval(Lexeme tree, Lexeme env)
        {
        if (debug) System.out.println(" [starting evaluation..] ");
        if (debug && tree != null) System.out.println(" [tree.type is " + tree.type + "]");
        switch (tree.type) {
            case MAIN:
                evalMain(tree,env);
                break;
            case PROGRAM:
                evalProgram(tree,env);
                break;
            case INTEGER:
                if (debug) System.out.println(" [evaluating integer]");
                return new Lexeme(INTEGER,tree.value);
            case REAL:
                return tree;
            case STRING:
                return tree;
            case CHAR:  // FIXME check
                return tree;
            case ID:
                if (debug) System.out.println(" [evaluating ID]");
                Lexeme l = Environment.lookupEnv(tree.value.toString(),env);
                if (l == null) {
                    System.out.println("SEMANTIC ERROR: " + tree.value.toString() + " is undefined.");
                    System.exit(0);
                }
                return l;
            case FDEF:
                evalFdef(tree,env);
                break;
            case BLOCK:
                return evalBlock(tree,env);
            case STATEMENT_LIST:
                return evalStatementList(tree,env);
            case STATEMENT:
                return evalStatement(tree,env);
            case IF_STATE:
                return evalIfState(tree,env);
            case IF_NONE_STATE: // FIXME maybe unnecessary?
                return evalIfNoneState(tree,env);
            case EXPR:
                return evalExpr(tree,env);
            case UNARY:
                return evalUnary(tree,env);
            case ID_STATEMENT:
                return evalIdStatement(tree,env);
//            case LAMBDA:
//                break;
            case FCALL:
                return evalFcall(tree,env);
            case CHECK_VALUE:
                return evalCheckValue(tree,env);
            case OP:
                return evalOp(tree,env);
            case WHILE_LOOP:
                return evalWhileLoop(tree,env);
            case PRINT:
                evalPrint(tree,env);
                break;
            case PRINT_ITEM:
                return eval(tree.left,env);
           case VAR_DEF:
                return evalVarDef(tree,env);
            case ASSIGNMENT:
                if (tree.left.type.equals(ID)) return evalAssignmentFromIdStatement(tree,env);
                else return evalAssignment(tree,env);
            case RETURN_STATEMENT:
                return evalReturn(tree,env);
            case GLUE:
                evalGlue(tree,env);
                break;
            case ARRAY_DEC:
                return evalArrayDec(tree,env);
            case ARRAY_CALL:
                return evalArrayCall(tree,env);
            case ARRAY_ASSIGN:
                return evalArrayAssign(tree,env);
            default:
                System.out.print(" ~bad " + tree.type + " expression~ ");
            }
        return null;
        }

    public static void evalMain(Lexeme tree, Lexeme env)
        {
        if (debug) System.out.println(" [evaluating main] ");
        eval(tree.left,env);
        if (debug) System.out.println(" [evaluating main: onto the right...] ");
        if (tree.right != null) eval(tree.right,env);
        }

    public static void evalProgram(Lexeme tree, Lexeme env)
        {
        if (debug) System.out.println(" [evaluating program] ");
        eval(tree.left, env);
        if (tree.right != null) eval(tree.right,env);
        }

    public static Lexeme evalFcall(Lexeme tree, Lexeme env)
        {
        if (debug)
            {
            System.out.println(" [evaluating fcall: " + tree.left.value.toString() + "] ");
            Lexeme.printDebug(tree);
            tree.right.display();
            if (tree.right.left != null) tree.right.left.display();
            }
        Lexeme closure = Environment.lookupEnv(tree.left.value.toString(),env);
        Lexeme prelimArgs = getArgs(tree);
        if (debug && prelimArgs != null) Lexeme.printDebug(prelimArgs);
//        // must determine if user-defined or built in
//        if (isBuiltIn(closure))
//            return evalBuiltIn(closure,args);
        if (debug) {Lexeme.printDebug(closure); Lexeme.printDebug(closure.right); Lexeme.printDebug(closure.right.right);}
        Lexeme params = getParams(closure.right); // closure.right.right.left.left;
        if (debug && params != null) Lexeme.printDebug(params);
        Lexeme body = getBody(closure.right);
        Lexeme senv = closure.left; 	// static env
        Lexeme args = evalArgs(prelimArgs,env);
        Lexeme lenv = Environment.extendEnv(params,args,senv);
        return evalBlock(body,lenv);
        }

    public static void evalFdef(Lexeme tree, Lexeme env)
        {
        if (debug) System.out.println(" [evaluating fdef] ");
        String fName = tree.left.value.toString();
        if (debug) System.out.println(" [fdef: " + fName + "] ");
        Lexeme closure = Environment.cons(CLOSURE,env,tree);
        if (debug)
            {
            Lexeme.printDebug(closure);
            Lexeme.printDebug(closure.right);
            Lexeme.printDebug(closure.right.right);
            if (closure.right.right.left != null) {
                Lexeme.printDebug(closure.right.right.left);
                if (closure.right.right.left.left != null)
                    Lexeme.printDebug(closure.right.right.left.left);
            }
            if (closure.right.right.right != null) Lexeme.printDebug(closure.right.right.right);
            }
        Environment.insertEnv(new Lexeme(FNAME,fName),closure,env);
        }

    /***************************************************
     *             idStatement/fcall
     *               /           \
     *             ID           fcall
     *                          /
     *                      zeroArgList || null
     *                      /
     *             argList || null
     *             /    \
     *          expr   argList || null
     ***************************************************/
    public static Lexeme getArgs(Lexeme tree)
        {
        if (debug) {
            System.out.println(" [getting args] ");
            tree.display();
            tree.right.display();
            if (tree.right.left != null) tree.right.left.display();
        }
        Lexeme curr = tree.right.left;
        if (curr != null) {                     // if zeroArgList is not null
            if (curr.left != null) {            // if argList is not null
                curr = curr.left;               // move curr to first argList
                Lexeme args = curr.left;        // args = expr in argList
                curr = curr.right;               // curr to next arglist
                while (curr != null) {           // if next argList is not null
                    Lexeme l = args;            // store current arg exprs
                    args = curr.left;           // update args to new arg expr
                    args.right = l;             // store old argList to right of new
                    curr = curr.right;          // move curr to next argList
                }
                return args;                    // this should be a string of arg expr from new to old
            }
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

    /****************************************************
     *             FDEF
     *          /         \
     *      ID                GLUE
     *                      /       \
     *            zeroParamList()   block()
     *                      /
     *             paramList || null
     *             /        \
     *          varDec()   paramList || null
     ****************************************************/
    public static Lexeme getParams(Lexeme tree)
        {
        if (debug) {
            System.out.println(" [getting params] ");
            Lexeme.printDebug(tree);
        }
        Lexeme curr = tree.right;
        if (debug) Lexeme.printDebug(curr);
        if (curr.left != null)                  // if zeroParamList is not null
            {
            curr = curr.left;                   // move curr to zeroParamList
            if (debug) Lexeme.printDebug(curr);
            if (curr.left != null)
                {
                curr = curr.left;               // move curr to paramList
                Lexeme params = curr.left;     // params = varDec in paramList
                curr = curr.right;             // curr to next paramList
                while (curr != null)                // if next paramList is not null
                    {
                    Lexeme l = params;              // store current params
                    params = curr.left;              // update params to new param dec
                    params.right = l;               // store old paramList to right
                    curr = curr.right;
                    }
                if (debug) Lexeme.printDebug(params);
                return params;
                }
            }
        return null;
        }

    /****************************************************
     *            FDEF
     *          /         \
     *      ID                GLUE
     *                      /       \
     *            zeroParamList()   block()
     ****************************************************/
    public static Lexeme getBody(Lexeme tree)
        {
        if (debug) System.out.println(" [getting body] ");
        return tree.right.right;
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
            if (statementList.right != null) statementList = statementList.right;
            else break;
            curr = statementList.left;
            }
        return finished;
        }

    public static Lexeme evalStatement(Lexeme tree, Lexeme env)
        {
        if (debug)
            {
            System.out.println(" [evaluating statement] ");
            tree.display();
            }
        return eval(tree.left,env);
        }

    public static void evalGlue(Lexeme tree, Lexeme env)
        {
        if (debug) System.out.println(" [evaluating glue] ");
        if (tree.left != null)
            {
            eval(tree.left,env);
            if (tree.right != null) eval(tree.right,env);
            }
        }

    public static Lexeme evalIdStatement(Lexeme tree, Lexeme env)
        {
        if (tree.right == null)
            {
            return eval(tree.left,env);
            }
        else if (tree.right.type.equals(FCALL)) tree.type = FCALL;
        else if (tree.right.type.equals(ASSIGNMENT))
            {
            tree.type = ASSIGNMENT;
            return evalAssignmentFromIdStatement(tree,env);
            }
        else if (tree.right.type.equals(ARRAY_CALL))
            {
            tree.type = ARRAY_CALL;
            return evalArrayCallFromIdStatement(tree,env);
            }
        return eval(tree,env);
        }

    /*                PRINT
     *                /
     *          PRINT_ITEM
     *            /
     *  STRING || expr() || unary()
     */
    public static void evalPrint(Lexeme tree, Lexeme env)
        {
        if (debug)
            {
            System.out.println(" [evaluating print] ");
            tree.display();
            tree.left.display();
            tree.left.left.display();
            }
        // Case 1: String
        if (tree.left.type.equals(STRING))
            {
            System.out.println(tree.left.value.toString());
            return;
            }
        // Case 2 and 3: expr or unary
        Lexeme finished = eval(tree.left.left,env);
        System.out.println(finished.value.toString());  // FIXME needs work!
        }

    /****************************************************
     *            expr
     *          /       \
     *      unary       checkVal || op || null
     ****************************************************/
    public static Lexeme evalExpr(Lexeme tree, Lexeme env)
        {
        if (debug)
            {
            System.out.println(" [evaluating expr] ");
            tree.display();
            }
        if (tree.right == null)
            tree.type = UNARY;
        else if (tree.right.type.equals(PLUS) || tree.right.type.equals(MINUS) ||
                tree.right.type.equals(TIMES) || tree.right.type.equals(BY) || tree.right.type.equals(MOD))
            tree.type = OP;
        else
            tree.type = CHECK_VALUE;
        if (debug) System.out.print(" tree type is now " + tree.type + "\n");
        return eval(tree,env);
        }

    public static Lexeme evalReturn(Lexeme tree, Lexeme env)
        {
        if (debug) System.out.println(" [evaluating return] ");
        if (tree.left != null) return eval(tree.left,env);
        return null;
        }

    public static Lexeme evalUnary(Lexeme tree, Lexeme env)
        {
        if (debug)
            {
            System.out.println(" [evaluating unary] ");
            tree.display();
            }
        return eval(tree.left,env);
        }

    /***************************************************
     *             idStatement/assignment
     *               /           \
     *             ID           assignment
     *                          /
     *                      expr()
     ***************************************************/
    public static Lexeme evalAssignmentFromIdStatement(Lexeme tree, Lexeme env)
        {
        if (debug)
            {
            System.out.println(" [evaluating assignment from IdStatement] ");
            tree.display();
            tree.left.display();
            tree.right.display();
            }
        String id = tree.left.value.toString();
        Lexeme result = eval(tree.right.left,env);
        Object val = result.value;
        return Environment.update(id,env,val);
        }

    public static Lexeme evalAssignment(Lexeme tree, Lexeme env)
    {
        if (debug)
        {
            System.out.println(" [evaluating assignment] ");
            tree.display();
            tree.left.display();
            tree.right.display();
        }
        return eval(tree.left,env);
    }

    /********************************
     *        VAR_DEF
     *       /      \
     *  varDec()    expr()
     *    /
     *  ID
     ********************************/
    public static Lexeme evalVarDef(Lexeme tree, Lexeme env)
        {
        if (debug)
            {
            System.out.println(" [evaluating varDef] ");
            tree.display();
            }
        Lexeme var = new Lexeme(VAR,tree.left.left.value);
        Lexeme val = eval(tree.right,env);
        return Environment.insertEnv(var,val,env);
        }

    public static Lexeme evalOp(Lexeme tree, Lexeme env)
        {
        if (debug) System.out.println(" [evaluating op] ");
        Lexeme operator = tree.right;
        switch (operator.type)
            {
            case PLUS:
                return evalPlus(tree,env);
            case MINUS:
                return evalMinus(tree,env);
            case TIMES:
                return evalTimes(tree,env);
            case BY:
                return evalBy(tree,env);
            case MOD:
                return evalMod(tree,env);
            }
            return null;
        }

    public static Lexeme evalPlus(Lexeme tree, Lexeme env)
        {
        Lexeme x = eval(tree.left,env);
        Lexeme y = eval(tree.right.left,env);
        if (x.type.equals(INTEGER))
            {
            if (y.type.equals(INTEGER))
                return new Lexeme(INTEGER,((int)x.value + (int)y.value));
            else if (y.type.equals(REAL))
                return new Lexeme(REAL,((int)x.value + (double)y.value));
            else if (y.type.equals(STRING))
                return new Lexeme(STRING,(x.value.toString() + y.value.toString()));
            }
        else if (x.type.equals(REAL))
            {
            if (y.type.equals(INTEGER))
                return new Lexeme(REAL,((double)x.value + (int)y.value));
            else if (y.type.equals(REAL))
                return new Lexeme(REAL,((double)x.value + (double)y.value));
            else if (y.type.equals(STRING))
                return new Lexeme(STRING,(x.value.toString() + y.value.toString()));
            }
        else    // x is a STRING
            {
            return new Lexeme(STRING,(x.value.toString() + y.value.toString()));
            }
        return null;
        }

    public static Lexeme evalMinus(Lexeme tree, Lexeme env)
        {
        Lexeme x = eval(tree.left,env);
        Lexeme y = eval(tree.right.left,env);
        if (x.type.equals(INTEGER))
            {
            if (y.type.equals(INTEGER))
                return new Lexeme(INTEGER,((int)x.value - (int)y.value));
            else if (y.type.equals(REAL))
                return new Lexeme(REAL,((int)x.value - (double)y.value));
            else if (y.type.equals(STRING))
                {
                System.out.println("SEMANTIC ERROR: Cannot subtract variables of type " + x.type + " and " + y.type);
                System.exit(0);
                return null;
                }
            }
        else if (x.type.equals(REAL))
            {
            if (y.type.equals(INTEGER))
                return new Lexeme(REAL, ((double) x.value - (int) y.value));
            else if (y.type.equals(REAL))
                return new Lexeme(REAL, ((double) x.value - (double) y.value));
            else if (y.type.equals(STRING))
                {
                System.out.println("SEMANTIC ERROR: Cannot subtract variables of type " + x.type + " and " + y.type);
                System.exit(0);
                return null;
                }
            }
        else    // x is a STRING
            {
            System.out.println("SEMANTIC ERROR: Cannot subtract variables of type " + x.type + " and " + y.type);
            System.exit(0);
            return null;
            }
        return null;
        }

    public static Lexeme evalTimes(Lexeme tree, Lexeme env)
    {
        Lexeme x = eval(tree.left,env);
        Lexeme y = eval(tree.right.left,env);
        if (x.type.equals(INTEGER))
        {
            if (y.type.equals(INTEGER))
                return new Lexeme(INTEGER,((int)x.value * (int)y.value));
            else if (y.type.equals(REAL))
                return new Lexeme(REAL,((int)x.value * (double)y.value));
            else if (y.type.equals(STRING))
            {
                System.out.println("SEMANTIC ERROR: Cannot multiply variables of type " + x.type + " and " + y.type);
                System.exit(0);
                return null;
            }
        }
        else if (x.type.equals(REAL))
        {
            if (y.type.equals(INTEGER))
                return new Lexeme(REAL, ((double) x.value * (int) y.value));
            else if (y.type.equals(REAL))
                return new Lexeme(REAL, ((double) x.value * (double) y.value));
            else if (y.type.equals(STRING))
            {
                System.out.println("SEMANTIC ERROR: Cannot multiply variables of type " + x.type + " and " + y.type);
                System.exit(0);
                return null;
            }
        }
        else    // x is a STRING
        {
            System.out.println("SEMANTIC ERROR: Cannot multiply variables of type " + x.type + " and " + y.type);
            System.exit(0);
            return null;
        }
        return null;
    }

    public static Lexeme evalBy(Lexeme tree, Lexeme env)
    {
        Lexeme x = eval(tree.left,env);
        Lexeme y = eval(tree.right.left,env);
        if (x.type.equals(INTEGER))
        {
            if (y.type.equals(INTEGER))
                return new Lexeme(INTEGER,((int)x.value / (int)y.value));
            else if (y.type.equals(REAL))
                return new Lexeme(REAL,((int)x.value / (double)y.value));
            else if (y.type.equals(STRING))
            {
                System.out.println("SEMANTIC ERROR: Cannot divide variables of type " + x.type + " and " + y.type);
                System.exit(0);
                return null;
            }
        }
        else if (x.type.equals(REAL))
        {
            if (y.type.equals(INTEGER))
                return new Lexeme(REAL, ((double) x.value / (int) y.value));
            else if (y.type.equals(REAL))
                return new Lexeme(REAL, ((double) x.value / (double) y.value));
            else if (y.type.equals(STRING))
            {
                System.out.println("SEMANTIC ERROR: Cannot divide variables of type " + x.type + " and " + y.type);
                System.exit(0);
                return null;
            }
        }
        else    // x is a STRING
        {
            System.out.println("SEMANTIC ERROR: Cannot divide variables of type " + x.type + " and " + y.type);
            System.exit(0);
            return null;
        }
        return null;
    }

    public static Lexeme evalMod(Lexeme tree, Lexeme env)
        {
        Lexeme x = eval(tree.left,env);
        Lexeme y = eval(tree.right.left,env);
        if (x.type.equals(INTEGER))
            {
            if (y.type.equals(INTEGER))
                return new Lexeme(INTEGER,((int)x.value % (int)y.value));
            else if (y.type.equals(REAL))
                return new Lexeme(REAL,((int)x.value % (double)y.value));
            else if (y.type.equals(STRING))
                {
                System.out.println("SEMANTIC ERROR: Cannot modulo variables of type " + x.type + " and " + y.type);
                System.exit(0);
                return null;
                }
            }
        else if (x.type.equals(REAL))
            {
            if (y.type.equals(INTEGER))
                return new Lexeme(REAL, ((double) x.value % (int) y.value));
            else if (y.type.equals(REAL))
                return new Lexeme(REAL, ((double) x.value % (double) y.value));
            else if (y.type.equals(STRING))
                {
                System.out.println("SEMANTIC ERROR: Cannot modulo variables of type " + x.type + " and " + y.type);
                System.exit(0);
                return null;
                }
            }
        else    // x is a STRING
            {
            System.out.println("SEMANTIC ERROR: Cannot modulo variables of type " + x.type + " and " + y.type);
            System.exit(0);
            return null;
            }
        return null;
        }

    public static Lexeme evalCheckValue(Lexeme tree, Lexeme env)
        {
        if (debug)
            {
            System.out.println(" [evaluating checkVal] ");
            tree.display();
            tree.right.display();
            }
        Lexeme check = tree.right;
        switch (check.type)
            {
            case LESS_THAN:
                return evalLessThan(tree,env);
            case LESS_EQUALS:
                return evalLessEquals(tree,env);
            case GREATER_THAN:
                  return evalGreaterThan(tree,env);
            case GREATER_EQUALS:
                return evalGreaterEquals(tree,env);
            case EQUAL_TO:
                return evalEqualTo(tree,env);
            case NOT_EQUAL_TO:
                return evalNotEqualTo(tree,env);
            }
        return null;
        }

    public static Lexeme evalLessThan(Lexeme tree, Lexeme env)
        {
        if (debug) System.out.println(" [evaluating lessThan] ");
        if (debug) Lexeme.printDebug(tree);
        if (debug) Lexeme.printDebug(tree.left);
        if (debug) Lexeme.printDebug(tree.right);
        if (debug) Lexeme.printDebug(tree.right.left);
        Lexeme x = eval(tree.left,env);
        Lexeme y = tree.right.left;
        if (tree.right.left.type.equals(ID_STATEMENT)) y = eval(tree.right.left,env);
        if (x.type.equals(INTEGER))
            {
            if (y.type.equals(INTEGER))
                return new Lexeme(BOOLEAN,((int)x.value < (int)y.value));
            else if (y.type.equals(REAL))
                return new Lexeme(BOOLEAN,((int)x.value < (double)y.value));
            else if (y.type.equals(STRING))
                {
                System.out.println("SEMANTIC ERROR: Cannot compare variables of type " + x.type + " and " + y.type);
                System.exit(0);
                return null;
                }
            }
        else if (x.type.equals(REAL))
            {
            if (y.type.equals(INTEGER))
                return new Lexeme(BOOLEAN, ((double) x.value < (int) y.value));
            else if (y.type.equals(REAL))
                return new Lexeme(BOOLEAN, ((double) x.value < (double) y.value));
            else if (y.type.equals(STRING))
                {
                System.out.println("SEMANTIC ERROR: Cannot compare variables of type " + x.type + " and " + y.type);
                System.exit(0);
                return null;
                }
            }
        else    // x is a STRING
            {
            System.out.println("SEMANTIC ERROR: Cannot compare variables of type " + x.type + " and " + y.type);
            System.exit(0);
            return null;
            }
        return null;
        }

    public static Lexeme evalLessEquals(Lexeme tree, Lexeme env)
    {
        Lexeme x = eval(tree.left,env);
        Lexeme y = tree.right.left;
        if (tree.right.left.type.equals(ID_STATEMENT)) y = eval(tree.right.left,env);
        if (x.type.equals(INTEGER))
            {
            if (y.type.equals(INTEGER))
                return new Lexeme(BOOLEAN,((int)x.value <= (int)y.value));
            else if (y.type.equals(REAL))
                return new Lexeme(BOOLEAN,((int)x.value <= (double)y.value));
            else if (y.type.equals(STRING))
                {
                System.out.println("SEMANTIC ERROR: Cannot compare variables of type " + x.type + " and " + y.type);
                System.exit(0);
                return null;
                }
            }
        else if (x.type.equals(REAL))
            {
            if (y.type.equals(INTEGER))
                return new Lexeme(BOOLEAN, ((double) x.value <= (int) y.value));
            else if (y.type.equals(REAL))
                return new Lexeme(BOOLEAN, ((double) x.value <= (double) y.value));
            else if (y.type.equals(STRING))
                {
                System.out.println("SEMANTIC ERROR: Cannot compare variables of type " + x.type + " and " + y.type);
                System.exit(0);
                return null;
                }
            }
        else    // x is a STRING
            {
            System.out.println("SEMANTIC ERROR: Cannot compare variables of type " + x.type + " and " + y.type);
            System.exit(0);
            return null;
            }
        return null;
        }

    public static Lexeme evalGreaterThan(Lexeme tree, Lexeme env)
        {
        Lexeme x = eval(tree.left,env);
        Lexeme y = tree.right.left;
        if (tree.right.left.type.equals(ID_STATEMENT)) y = eval(tree.right.left,env);
        if (x.type.equals(INTEGER))
            {
            if (y.type.equals(INTEGER))
                return new Lexeme(BOOLEAN,((int)x.value > (int)y.value));
            else if (y.type.equals(REAL))
                return new Lexeme(BOOLEAN,((int)x.value > (double)y.value));
            else if (y.type.equals(STRING))
                {
                System.out.println("SEMANTIC ERROR: Cannot compare variables of type " + x.type + " and " + y.type);
                System.exit(0);
                return null;
                }
            }
        else if (x.type.equals(REAL))
            {
            if (y.type.equals(INTEGER))
                return new Lexeme(BOOLEAN, ((double) x.value > (int) y.value));
            else if (y.type.equals(REAL))
                return new Lexeme(BOOLEAN, ((double) x.value > (double) y.value));
            else if (y.type.equals(STRING))
                {
                System.out.println("SEMANTIC ERROR: Cannot compare variables of type " + x.type + " and " + y.type);
                System.exit(0);
                return null;
                }
            }
        else    // x is a STRING
            {
            System.out.println("SEMANTIC ERROR: Cannot compare variables of type " + x.type + " and " + y.type);
            System.exit(0);
            return null;
            }
        return null;
        }

    public static Lexeme evalGreaterEquals(Lexeme tree, Lexeme env)
        {
        Lexeme x = eval(tree.left,env);
        Lexeme y = tree.right.left;
        if (tree.right.left.type.equals(ID_STATEMENT)) y = eval(tree.right.left,env);
        if (x.type.equals(INTEGER))
            {
            if (y.type.equals(INTEGER))
                return new Lexeme(BOOLEAN,((int)x.value >= (int)y.value));
            else if (y.type.equals(REAL))
                return new Lexeme(BOOLEAN,((int)x.value >= (double)y.value));
            else if (y.type.equals(STRING))
                {
                System.out.println("SEMANTIC ERROR: Cannot compare variables of type " + x.type + " and " + y.type);
                System.exit(0);
                return null;
                }
            }
        else if (x.type.equals(REAL))
            {
            if (y.type.equals(INTEGER))
                return new Lexeme(BOOLEAN, ((double) x.value >= (int) y.value));
            else if (y.type.equals(REAL))
                return new Lexeme(BOOLEAN, ((double) x.value >= (double) y.value));
            else if (y.type.equals(STRING))
                {
                System.out.println("SEMANTIC ERROR: Cannot compare variables of type " + x.type + " and " + y.type);
                System.exit(0);
                return null;
                }
            }
        else    // x is a STRING
            {
            System.out.println("SEMANTIC ERROR: Cannot compare variables of type " + x.type + " and " + y.type);
            System.exit(0);
            return null;
            }
        return null;
        }

    public static Lexeme evalEqualTo(Lexeme tree, Lexeme env)
        {
        Lexeme x = eval(tree.left,env);
        Lexeme y = tree.right.left;
        if (tree.right.left.type.equals(ID_STATEMENT)) y = eval(tree.right.left,env);
        if (x.type.equals(INTEGER))
            {
            if (y.type.equals(INTEGER))
                return new Lexeme(BOOLEAN,((int)x.value == (int)y.value));
            else if (y.type.equals(REAL))
                return new Lexeme(BOOLEAN,((int)x.value == (double)y.value));
            else if (y.type.equals(STRING))
                {
                System.out.println("SEMANTIC ERROR: Cannot compare variables of type " + x.type + " and " + y.type);
                System.exit(0);
                return null;
                }
            }
        else if (x.type.equals(REAL))
            {
            if (y.type.equals(INTEGER))
                return new Lexeme(BOOLEAN, ((double) x.value == (int) y.value));
            else if (y.type.equals(REAL))
                return new Lexeme(BOOLEAN, ((double) x.value == (double) y.value));
            else if (y.type.equals(STRING))
                {
                System.out.println("SEMANTIC ERROR: Cannot compare variables of type " + x.type + " and " + y.type);
                System.exit(0);
                return null;
                }
            }
        else    // x is a STRING
            {
            return new Lexeme(BOOLEAN, (x.value.toString().equals(y.value.toString())));
            }
        return null;
        }

    public static Lexeme evalNotEqualTo(Lexeme tree, Lexeme env)
    {
        Lexeme x = eval(tree.left,env);
        Lexeme y = tree.right.left;
        if (tree.right.left.type.equals(ID_STATEMENT)) y = eval(tree.right.left,env);
        if (x.type.equals(INTEGER))
        {
            if (y.type.equals(INTEGER))
                return new Lexeme(BOOLEAN,((int)x.value != (int)y.value));
            else if (y.type.equals(REAL))
                return new Lexeme(BOOLEAN,((int)x.value != (double)y.value));
            else if (y.type.equals(STRING))
            {
                System.out.println("SEMANTIC ERROR: Cannot compare variables of type " + x.type + " and " + y.type);
                System.exit(0);
                return null;
            }
        }
        else if (x.type.equals(REAL))
        {
            if (y.type.equals(INTEGER))
                return new Lexeme(BOOLEAN, ((double) x.value != (int) y.value));
            else if (y.type.equals(REAL))
                return new Lexeme(BOOLEAN, ((double) x.value != (double) y.value));
            else if (y.type.equals(STRING))
            {
                System.out.println("SEMANTIC ERROR: Cannot compare variables of type " + x.type + " and " + y.type);
                System.exit(0);
                return null;
            }
        }
        else    // x is a STRING
        {
            return new Lexeme(BOOLEAN, !(x.value.toString().equals(y.value.toString())));
        }
        return null;
    }

    public static Lexeme evalWhileLoop(Lexeme tree, Lexeme env)
        {
        if (debug) System.out.println(" [evaluating whileLoop] ");
        Lexeme finished = null;
        if (debug)
            {
            tree.display();
            tree.left.display();
            tree.left.left.display();
            tree.left.right.display();
            tree.left.left.left.display();
            tree.right.display();
            }

        while (eval(tree.left,env).bool)
            {
            finished = evalBlock(tree.right,env);
            }
        return finished;
        }

    /****************************************
     *          IF_STATEMENT
     *          /          \
     *      expr()         GLUE
     *                     /    \
     *                  block()  GLUE
     *                           /
     *                  altIfState() || null
     *****************************************/
    public static Lexeme evalIfState(Lexeme tree, Lexeme env)
        {
        if (debug)
            {
            System.out.println(" [evaluating ifState] ");
            tree.display();
            tree.left.display();
//            tree.right.right.display();
//            tree.right.right.left.display();
            }
        if (eval(tree.left,env) == null) return null;
        if (eval(tree.left,env).bool)
            {
            return evalBlock(tree.right.left,env);
            }
        if (tree.right.right == null) return null;
        return evalAltIfState(tree.right.right.left,env);
        }

    /*           ALT_IF_STATEMENT
     *          /
     *      orIfState() || ifNoneState() || null
     */
    public static Lexeme evalAltIfState(Lexeme tree, Lexeme env)
        {
        if (debug)
            {
            System.out.println(" [evaluating altIfState] ");
            tree.display(); // tree is of type orIf or ifNone
            tree.left.display();
            }
        switch (tree.type)
            {
            case OR_IF_STATE:
                return evalOrIfState(tree,env);
            case IF_NONE_STATE:
                return evalIfNoneState(tree,env);
            }
        return null;
        }

    public static Lexeme evalOrIfState(Lexeme tree, Lexeme env)
        {
        if (debug)
        {
            System.out.println(" [evaluating orIfState] ");
            tree.display();
            tree.left.display();
            tree.right.right.left.display();
        }
        if (eval(tree.left,env).bool)
            return evalBlock(tree.right.left,env);
        else if (tree.right.right != null)
            return evalAltIfState(tree.right.right.left,env);
        else
            {
            if (debug) System.out.println(" [orIfState: no alts] ");
            return null;
            }
        }

    public static Lexeme evalIfNoneState(Lexeme tree, Lexeme env)
        {
        return evalBlock(tree.left,env);
        }

    public static Lexeme evalGetArray(Lexeme id, int index)
        {
        if (debug) System.out.println(" [eval getting array] ");
        return id.arr[index];
        }

    public static void evalSetArray(Lexeme id, int index, Lexeme val)
        {
        if (debug) System.out.println(" [eval setting array] ");
        id.arr[index] = val;
        }

    public static Lexeme evalArrayDec(Lexeme tree, Lexeme env)
        {
        if (debug) System.out.println(" [evaluating arrayDec] ");
        Lexeme id = tree.left;
        int size = (int)tree.right.value;
        id.arr = new Lexeme[size];
        Lexeme l = new Lexeme(ARRAY,id.arr);
        //l.value = id.value;
        return Environment.insertEnv(id,l,env);
        }

    // in idStatement tree, but arrayCall type
    public static Lexeme evalArrayCallFromIdStatement(Lexeme tree, Lexeme env)
        {
        if (debug)
            {
            System.out.println(" [evaluating arrayCall from idStatement] ");
            tree.display();
            tree.left.display();
            }
        String id = tree.left.value.toString();   // or just call?
        if (debug) System.out.println(" [arrayCall: id is " + id + " ]");
        Lexeme found = Environment.lookupEnv(id,env);
        if (found == null) return null;
        int index = (int)eval(tree.right.left,env).value;    // FIXME maybe tree.right?
//        if (index >= found.arr.size())                    // FIXME add size check!
//            {
//            System.out.println("SEMANTIC ERROR: Index is out of bounds");
//            System.exit(0);
//            }
        return evalGetArray(found,index);
        }

    public static Lexeme evalArrayCall(Lexeme tree, Lexeme env)
        {
        if (debug) System.out.println(" [evaluating arrayCall] ");
        //int index = (int)eval(tree.left,env).value;
        return eval(tree.left,env);
        }

    public static Lexeme evalArrayAssign(Lexeme tree, Lexeme env)
        {
        if (debug)
            {
            System.out.println(" [evaluating arrayAssign] ");
            tree.display();
            tree.left.display();
            tree.right.display();
            tree.right.left.display();
            tree.right.left.left.display();
            tree.right.right.display();
            tree.right.right.left.display();
            }
        String idName = tree.left.value.toString();        // ID
        Lexeme id = Environment.lookupEnv(idName,env);
        int index = (int)eval(tree.right.left,env).value; // ARRAY_CALL
        Lexeme value = eval(tree.right.right.left,env);
        evalSetArray(id,index,value);
        if (debug)
            {
            System.out.println(" [arrayAssign: idName " + idName + " at index " + index + "]");
            if (id != null) {System.out.print("id is "); id.display();}
            else System.out.println("id is null");
            if (value != null) {System.out.print("value is "); value.display();}
            else System.out.println("value is null");
            }
        return value;
        }

    public static void main(String[] args) throws IOException
        {
        String fileName = args[0];
        Lexeme env = Environment.createEnv();
        Lexeme tree = Parser.parse(fileName);
        eval(tree,env);
        }

}
