/**
 * Parser for MRKLEE, the Modern Right-reKursive Language for Everyday Expressions.
 * Designed and implemented by Hunter James, with guidance from Dr. John Lusth.
 */

import java.io.*;

public class Parser implements Types
    {
    private static boolean debug = false;
    private static Lexeme current;
    private static Lexer i;

    private static void lexInit(String fileName) throws IOException
        {
        File inFile = new File(fileName);
        FileInputStream fileInput = new FileInputStream(inFile);
        PushbackInputStream inputStream = new PushbackInputStream(fileInput);
        i = new Lexer(inputStream);
        }

    private static Lexeme advance() throws IOException
        {
        Lexeme prev = current;
        current = i.lex();
        if (debug)
            {
            System.out.print("Advancing: current is ");
                current.display();
            }
        return prev;
        }

    public static boolean check(String type)
        {
        if (debug) System.out.println("Checking for "+type+", is actually "+current.type);
        return current.type == type;
        }

    public static Lexeme match(String type) throws IOException
        {
        if (debug) System.out.println("Matching to "+type);
        if (check(type))
            {
            if (debug) System.out.println("Successful match");
            return advance();
            }
        else
            {
            System.out.println("SYNTAX ERROR at Line " + Lexer.lineNumber + ": expected " + type + ", got " + current.type);
            System.out.println("Illegal");
            System.exit(1);
            return null;
            }
        }

    public static Lexeme cons(String type, Lexeme l, Lexeme r)
        {
        if (debug)
            {
            System.out.print("CONS pre-set: current is ");
                current.display();
            }
        Lexeme q = new Lexeme(type,l,r);
        if (debug)
            {
            System.out.print("CONS post-set: current is ");
            current.display();
            }
        return q;
        }

    /***** Beginning of grammar rule functions*****/

    /*             PROGRAM
     *          /          \
     *      fdef()         GLUE
     *                     /    \
     *                  block()  GLUE
     *                           /
     *                  program() || null
     */
    public static Lexeme program() throws IOException
        {
        if (debug) System.out.println("INSIDE OF PROGRAM");
        Lexeme f,b,p;
        f = fdef();
        b = block();
        if (programPending())
            {
            p = program();
            return cons(PROGRAM,f,cons(GLUE,b,cons(GLUE,p,null)));
            }
        return cons(PROGRAM,f,cons(GLUE,b,null));
        }

    public static boolean programPending()
    {
        return fdefPending();
    }

    /*             FDEF
     *          /         \
     *      ID         zeroParamList()
     */
    public static Lexeme fdef() throws IOException
        {
        if (debug) System.out.println("INSIDE OF FDEF");
        match(FUNCTION);
        Lexeme i = match(ID);
        match(OPAREN);
        Lexeme z = zeroParamList();
        match(CPAREN);
        return cons(FDEF,i,z);
        }

    public static boolean fdefPending()
    {
        return check(FUNCTION);
    }

    /*        ZERO_PARAM_LIST
     *          /
     *      paramList()
     */
    public static Lexeme zeroParamList() throws IOException
        {
        if (debug) System.out.println("INSIDE OF ZERO_PARAM_LIST");
        if (paramListPending())
            {
            Lexeme p = paramList();
            return cons(ZERO_PARAM_LIST,p,null);
            }
        return null;
        }

    public static boolean zeroParamListPending()
    {
        return paramListPending();
    }

    /*         PARAM_LIST
     *          /       \
     *      varDec()    paramList() || null
     */
    public static Lexeme paramList() throws IOException
        {
        if (debug) System.out.println("INSIDE OF PARAM_LIST");
        if (debug)
            {
            System.out.print("---> Before setting varDec, current is ");
            current.display();
            }
        Lexeme v = varDec();
        if (debug)
            {
            System.out.print("---> Set varDec, current is now ");
            current.display();
            }

        Lexeme p = null;
        if (check(COMMA))
            {
            match(COMMA);
            p = paramList();
            }
        return cons(PARAM_LIST,v,p);
        }

    public static boolean paramListPending()
    {
        return varDecPending();
    }

    /*          VAR_DEC
     *         /
     *      ID
     */
    public static Lexeme varDec() throws IOException
        {
        if (debug) System.out.println("INSIDE OF VAR_DEC");
        match(VAR);
        Lexeme i = match(ID);
        if (debug)
            {
            System.out.print("---> Set i to ID, current is ");
            current.display();
            }
        return cons(VAR_DEC,i,null);
        }

    public static boolean varDecPending()
    {
        return check(VAR);
    }

    /*          BLOCK
     *         /
     *   statementList()
     */
    public static Lexeme block() throws IOException
        {
        if (debug) System.out.println("INSIDE OF BLOCK");
        match(OBRACE);
        Lexeme s = statementList();
        match(CBRACE);
        return cons(BLOCK,s,null);
        }

    public static boolean blockPending()
    {
        return check(OBRACE);
    }

    /*         STATEMENT_LIST
     *          /           \
     *      statement()    statementList() || null
     */
    public static Lexeme statementList() throws IOException
        {
        if (debug) System.out.println("INSIDE OF STATEMENT LIST");
        Lexeme s = statement();
        Lexeme l = null;
        if (statementListPending())
            {
            l = statementList();
            }
        return cons(STATEMENT_LIST, s, l);
        }

    public static boolean statementListPending()
    {
        return statementPending();
    }

    /*                  STATEMENT
     *                   /
     * ifState() || whileLoop() || printCommand() || varDef() || block()
     * || assignment() || fcall() || returnState()
     */
    public static Lexeme statement() throws IOException
        {
        if (debug) System.out.println("INSIDE OF STATEMENT");
        Lexeme x;
        if (ifStatePending())
            x = ifState();
        else if (whileLoopPending())
            x = whileLoop();
        else if (printPending())
            x = print();
        else if (varDefPending())
            x = varDef();
        else if (blockPending())
            x = block();
        else if (idStatementPending())
            x = idStatement();
        else if (returnStatementPending())
            x = returnStatement();
        else
            x = null;
        return cons(STATEMENT,x,null);
        }

    public static boolean statementPending()
        {
        return ifStatePending() || whileLoopPending() || printPending() || varDefPending() ||
                blockPending() || idStatementPending() || returnStatementPending();
        }

    /*           IF_STATEMENT
     *          /          \
     *      expr()         GLUE
     *                     /    \
     *                  block()  GLUE
     *                           /
     *                  altIfState() || null
     */
    public static Lexeme ifState() throws IOException
        {
        if (debug) System.out.println("INSIDE OF IF_STATE");
        match(IF);
        Lexeme e = expr();
        match(GO);
        Lexeme b = block();
        if (altIfStatePending())
            {
            Lexeme x = altIfState();
            return cons(IF_STATE,e,cons(GLUE,b,cons(GLUE,x,null)));
            }
        return cons(IF_STATE,e,cons(GLUE,b,null));
        }

    public static boolean ifStatePending()
    {
        return check(IF);
    }

    /*           ALT_IF_STATEMENT
     *          /
     *      orIfState() || block() || null
     */
    public static Lexeme altIfState() throws IOException
        {
        if (debug) System.out.println("INSIDE OF ALT_IF_STATE");
        Lexeme i = null;
        if (orIfStatePending())
            {
            i = orIfState();
            return i;       // FIXME how to handle?
            }
        else if (check(IF))
            {
            match(IF);
            match(NONE);
            match(GO);
            i = block();
            return cons(IF_NONE,i,null); // FIXME how to handle?
            }
        return null;
        }

    public static boolean altIfStatePending()
    {
        return orIfStatePending() || check(IF);
    }

    /*           OR_IF_STATEMENT
     *          /           \
     *      expr()          GLUE
     *                      /   \
     *                  block()  GLUE
     *                           /
     *                     altIfState()
     */
    public static Lexeme orIfState() throws IOException
        {
        if (debug) System.out.println("INSIDE OF OR_IF_STATE");
        Lexeme a = null;
        match(OR);
        match(IF);
        Lexeme e = expr();
        match(GO);
        Lexeme b = block();
        if (altIfStatePending())
            {
            a = altIfState();
            return cons(OR_IF_STATE,e,cons(GLUE,b,cons(GLUE,a,null)));
            }
        return cons(OR_IF_STATE,e,cons(GLUE,b,null));
        }

    public static boolean orIfStatePending()
    {
        return check(OR);
    }

    /*              EXPR
     *          /           \
     *      unary()   checkVal() || op() || null
     */
    public static Lexeme expr() throws IOException
        {
        if (debug) System.out.println("INSIDE OF EXPR");
        Lexeme u = unary();
        Lexeme p = null;
        if (checkValuePending())
            {
            p = checkValue();
            }
        else if (opPending())
            {
            p = op();
            }
        return cons(EXPR,u,p);
        }

    public static boolean exprPending()
    {
        return unaryPending();
    }

    /*                     UNARY
     *                   /
     *      STRING || INTEGER || REAL || varDec() || idUnary()
     */
    public static Lexeme unary() throws IOException
        {
        if (debug) System.out.println("INSIDE OF UNARY");
        Lexeme v = null;
        if (check(STRING))
            return match(STRING);
        else if (check(INTEGER))
            return match(INTEGER);
        else if (check(REAL))
            return match(REAL);
        else if (idUnaryPending())
            return idUnary();       //FIXME might need cons?
        return v;
        }

    public static boolean unaryPending()
        {
        return check(STRING) || check(INTEGER) || check(REAL) || idUnaryPending();
        }

    /*        ID_UNARY
     *       /      \
     *     ID       fcall()
     */
    public static Lexeme idUnary() throws IOException
        {
        if (debug) System.out.println("INSIDE OF ID_UNARY");
        Lexeme f = null;
        Lexeme i = match(ID);
        if (fcallPending())
            f = fcall();
        return cons(ID_UNARY,i,f);
        }

    public static boolean idUnaryPending()
    {
        return check(ID);
    }

    /*        FCALL
     *       /
     *  zeroArgList()
     */
    public static Lexeme fcall() throws IOException
        {
        if (debug) System.out.println("INSIDE OF FCALL");
        match(OPAREN);
        Lexeme z = zeroArgList();
        match(CPAREN);
        match(EXCLAMATION);
        return cons(FCALL,z,null);
        }

    public static boolean fcallPending()
    {
        return check(OPAREN);
    }

    /*        ZERO_ARG_LIST
     *       /
     *  argList() || null
     */
    public static Lexeme zeroArgList() throws IOException
        {
        if (debug) System.out.println("INSIDE OF ZERO_ARG_LIST");
        Lexeme a = null;
        if (argListPending())
            {
            a = argList();
            return cons(ZERO_ARG_LIST,a,null);
            }
        return a;
        }

    public static boolean zeroArgListPending()
    {
        return argListPending();
    }

    /*        ARG_LIST
     *       /      \
     *  expr()      argList() || null
     */
    public static Lexeme argList() throws IOException
        {
        if (debug) System.out.println("INSIDE OF ARG_LIST");
        Lexeme e = expr();
        Lexeme a = null;
        if (check(COMMA))
            {
            match(COMMA);
            a = argList();
            }
        return cons(e.type,e,a);
        }

    public static boolean argListPending()
    {
        return exprPending();
    }

    /*     CHECK_VAL (named after check type)
     *       /      \
     *  (type)      unary
     */
    public static Lexeme checkValue() throws IOException
        {
        if (debug) System.out.println("INSIDE OF CHECK_VAL");
        Lexeme u = null;
        if (check(LESS_THAN))
            {
            match(LESS_THAN);
            u = unary();
            return cons(LESS_THAN,u,null);
            }
        else if (check(LESS_EQUALS))
            {
            match(LESS_EQUALS);
            u = unary();
            return cons(LESS_EQUALS,u,null);
            }
        else if (check(GREATER_THAN))
            {
            match(GREATER_THAN);
            u = unary();
            return cons(GREATER_THAN,u,null);
            }
        else if (check(GREATER_EQUALS))
            {
            match(GREATER_EQUALS);
            u = unary();
            return cons(GREATER_EQUALS,u,null);
            }
        else if (check(EQUAL_TO))
            {
            match(EQUAL_TO);
            u = unary();
            return cons(EQUAL_TO,u,null);
            }
        else if (check(NOT_EQUAL_TO))
            {
            match(NOT_EQUAL_TO);
            u = unary();
            return cons(NOT_EQUAL_TO,u,null);
            }
        return u;
        }

    public static boolean checkValuePending()
        {
        return check(LESS_THAN) || check(LESS_EQUALS) || check(GREATER_THAN) || check(GREATER_EQUALS)
                || check(EQUAL_TO) || check(NOT_EQUAL_TO);
        }

    /*     OP (named after op type)
     *       /      \
     *  (type)      unary
     */
    public static Lexeme op() throws IOException
        {
        if (debug) System.out.println("INSIDE OF OP");
        Lexeme u = null;
        if (check(PLUS))
            {
            match(PLUS);
            u = unary();
            return cons(PLUS,u, null);
            }
        else if (check(MINUS))
            {
            match(MINUS);
            u = unary();
            return cons(MINUS, u, null);
            }
        else if (check(TIMES))
            {
            match(TIMES);
            u = unary();
            return cons(TIMES, u, null);
            }
        else if (check(BY))
            {
            match(BY);
            u = unary();
            return cons(BY, u, null);
            }
        return u;
        }

    public static boolean opPending()
    {
        return check(PLUS) || check(MINUS) || check(TIMES) || check(BY);
    }

    /*     WHILE_LOOP
     *       /      \
     *  expr()      block()
     */
    public static Lexeme whileLoop() throws IOException
        {
        if (debug) System.out.println("INSIDE OF WHILE_LOOP");
        match(WHILE);
        Lexeme e = expr();
        Lexeme b = block();
        return cons(WHILE,e,b);
        }

    public static boolean whileLoopPending()
    {
        return check(WHILE);
    }

    /*        PRINT
     *       /
     *  printItem()
     */
    public static Lexeme print() throws IOException
        {
        if (debug) System.out.println("INSIDE OF PRINT");
        match(YELL);
        match(OPAREN);
        Lexeme p = printItem();
        match(CPAREN);
        match(EXCLAMATION);
        return cons(PRINT,p,null);
        }

    public static boolean printPending()
    {
        return check(YELL);
    }

    /*        PRINT_ITEM
     *       /
     *  STRING || expr() || unary()
     */
    public static Lexeme printItem() throws IOException
        {
        if (debug) System.out.println("INSIDE OF PRINT_ITEM");
        Lexeme a = null;
        if (check(DOUBLE_QUOTE))
            {
            match(DOUBLE_QUOTE);
            a = match(STRING);
            match(DOUBLE_QUOTE);
            return cons(STRING,a,null);
            }
        else if (exprPending())
            a = expr();
        else if (unaryPending())
            a = unary();
        return cons(PRINT_ITEM,a,null);
        }

    /*        VAR_DEF
     *       /      \
     *  varDec()    unary()
     */
    public static Lexeme varDef() throws IOException
        {
        if (debug) System.out.println("INSIDE OF VAR_DEF");
        Lexeme v = varDec();
        match(ASSIGN);
        Lexeme u = unary();
        match(EXCLAMATION);
        return cons(VAR_DEF,v,u);
        }

    public static boolean varDefPending()
    {
        return varDecPending();
    }

    /*       ASSIGNMENT
     *       /
     *  expr()
     */
    public static Lexeme assignment() throws IOException
        {
        if (debug) System.out.println("INSIDE OF ASSIGNMENT");
        Lexeme x = null;
        match(ASSIGN);
        x = expr();
        match(EXCLAMATION);
        return cons(ASSIGNMENT,x,null);
        }

    public static boolean assignPending()
    {
        return check(ASSIGN);
    }

    /*       ID_STATE
     *       /      \
     *      ID      assignment() || fcall()
     */
    public static Lexeme idStatement() throws IOException
        {
        if (debug) System.out.println("INSIDE OF ID_STATE");
        Lexeme i = null;
        Lexeme p = null;
        i = match(ID);
        if (assignPending())
            p = assignment();
        else if (fcallPending())
            p = fcall();
        return cons(ID,i,p);
        }

    public static boolean idStatementPending()
    {
        return check(ID);
    }

    /*       RETURN_STATEMENT
     *       /
     *  expr()
     */
    public static Lexeme returnStatement() throws IOException
        {
        if (debug) System.out.println("INSIDE OF RETURN_STATE");
        match(RECORD);
        Lexeme e = expr();
        match(EXCLAMATION);
        return cons(RETURN_STATEMENT,e,null);
        }

    public static boolean returnStatementPending()
    {
        return check(RECORD);
    }

    /***** End of grammar rule functions*****/

    public static void main(String args[]) throws IOException
        {
        lexInit(args[0]);
        current = i.lex();
        if (debug) current.display(); // ONLY FOR TESTING
        //varDef();
        program();
        match(ENDOFFILE);
        System.out.println("Legal");
        }
    }