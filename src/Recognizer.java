/**
 * Recognizer for MRKLEE, the Modern Right-reKursive Language for Everyday Expressions.
 * Designed and implemented by Hunter James, with guidance from Dr. John Lusth.
 */

import java.io.*;

public class Recognizer implements Types
{
    public static File inFile;
    public static FileInputStream fileInput;
    public static PushbackInputStream inputStream;
    public static Lexeme current;
    public static Lexer i;

    public static void lexInit(String fileName) throws IOException
    {
        inFile = new File(fileName);
        fileInput = new FileInputStream(inFile);
        inputStream = new PushbackInputStream(fileInput);
        i = new Lexer(inputStream);
    }

    public static void main(String args[]) throws IOException
    {
        lexInit(args[0]);
        current = i.lex(); // or call advance() and ignore the return value;
        //Lexeme.display(current); // ONLY FOR TESTING
        program();
        match(ENDOFFILE);
        System.out.println("Legal");
    }

    public static Lexeme advance() throws IOException
    {
        Lexeme prev = current;
        current = i.lex();
        //Lexeme.display(current); // ONLY FOR TESTING
        return prev;
    }

    public static boolean check(String type)
    {
        return current.type == type;
    }

    public static Lexeme match(String type) throws IOException
    {
        if (check(type))
            return advance();
        else {
            System.out.println("SYNTAX ERROR at Line " + i.lineNumber + ": expected " + type + ", got " + current.type);
            System.out.println("Illegal");
            System.exit(1);
            return null;
        }
    }

    /***** Beginning of grammar rule functions*****/

    public static void program() throws IOException
    {
        fdef();
        block();
        if (programPending())
            program();
    }

    public static boolean programPending()
    {
        return fdefPending();
    }

    public static void statement() throws IOException
    {
        if (ifStatePending())
            ifState();
        else if (whileLoopPending())
            whileLoop();
        else if (printPending())
            print();
        else if (varDefPending())
            varDef();
        else if (blockPending())
            block();
        else if (idStatementPending())
            idStatement();
        else if (returnStatementPending())
            returnStatement();
    }

    public static boolean statementPending()
    {
        return ifStatePending() || whileLoopPending() || printPending() || varDefPending() ||
                blockPending() || idStatementPending() || returnStatementPending();
    }

    public static void ifState() throws IOException
    {
        if (check(IF))
        {
            match(IF);
            expr();
            match(GO);
            block();
        }
        if (altIfStatePending())
            altIfState();
    }

    public static boolean ifStatePending()
    {
        return check(IF);
    }

    public static void altIfState() throws IOException
    {
        if (check(IF))
        {
            match(IF);
            match(NONE);
            match(GO);
            block();
        }
        else if (orIfStatePending())
            orIfState();
    }

    public static boolean altIfStatePending()
    {
        return orIfStatePending() || check(IF) /*|| empty?*/;
    }

    public static void orIfState() throws IOException
    {
        match(OR);
        match(IF);
        expr();
        match(GO);
        block();
        if (altIfStatePending())
            altIfState();
    }

    public static boolean orIfStatePending()
    {
        return check(OR);
    }

    public static void statementList() throws IOException
    {
        statement();
        if (statementListPending())
            statementList();
    }

    public static boolean statementListPending()
    {
        return statementPending();
    }

    public static void block() throws IOException
    {
        match(OBRACE);
        statementList();
        match(CBRACE);
    }

    public static boolean blockPending()
    {
        return check(OBRACE);
    }

    public static void whileLoop() throws IOException
    {
        match(WHILE);
        expr();
        block();
    }

    public static boolean whileLoopPending()
    {
        return check(WHILE);
    }

    public static void expr() throws IOException
    {
        unary();
        if (checkValuePending())
            checkValue();
        else if (opPending())
            op();
    }

    public static boolean exprPending()
    {
        return unaryPending();
    }

    public static void checkValue() throws IOException
    {
        if (check(LESS_THAN))
            match(LESS_THAN);
        else if (check(LESS_EQUALS))
            match(LESS_EQUALS);
        else if (check(GREATER_THAN))
            match(GREATER_THAN);
        else if (check(GREATER_EQUALS))
            match(GREATER_EQUALS);
        else if (check(EQUAL_TO))
            match(EQUAL_TO);
        else if (check(NOT_EQUAL_TO))
            match(NOT_EQUAL_TO);
        unary();
    }

    public static boolean checkValuePending()
    {
        return check(LESS_THAN) || check(LESS_EQUALS) || check(GREATER_THAN) || check(GREATER_EQUALS)
                || check(EQUAL_TO) || check(NOT_EQUAL_TO);
    }

    public static void op() throws IOException
    {
        if (check(PLUS))
            match(PLUS);
        else if (check(MINUS))
            match(MINUS);
        else if (check(TIMES))
            match(TIMES);
        else if (check(BY))
            match(BY);
        unary();
    }

    public static boolean opPending()
    {
        return check(PLUS) || check(MINUS) || check(TIMES) || check(BY);
    }

    public static void assignment() throws IOException
    {
        match(ASSIGN);
        expr();
        match(EXCLAMATION);
    }

    public static boolean assignPending()
    {
        return check(ASSIGN);
    }

    public static void idStatement() throws IOException
    {
        match(ID);
        if (assignPending())
            assignment();
        else if (functionCallPending())
            functionCall();
    }

    public static boolean idStatementPending()
    {
        return check(ID);
    }

    public static void unary() throws IOException
    {
        if (check(STRING))
            match(STRING);
        else if (check(INTEGER))
            match(INTEGER);
        else if (check(REAL))
            match(REAL);
        else if (check(CHAR))
            match(CHAR);
        else if (varDecPending())
            varDec();
        else if (idUnaryPending())
            idUnary();
    }

    public static boolean unaryPending()
    {
        return check(STRING) || check(INTEGER) || check(REAL) || varDecPending() || idUnaryPending();
    }

    public static void idUnary() throws IOException
    {
        match(ID);
        if (functionCallPending())
            functionCall();
    }

    public static boolean idUnaryPending()
    {
        return check(ID);
    }

    public static void varDec() throws IOException
    {
        match(VAR);
        match(ID);
    }

    public static boolean varDecPending()
    {
        return check(VAR);
    }

    public static void varDef() throws IOException
    {
        varDec();
        match(ASSIGN);
        unary();
        match(EXCLAMATION);
    }

    public static boolean varDefPending()
    {
        return varDecPending();
    }

    public static void paramList() throws IOException
    {
        varDec();
        if (check(COMMA))
        {
            match(COMMA);
            paramList();
        }
    }

    public static boolean paramListPending()
    {
        return varDecPending();
    }

    public static void zeroParamList() throws IOException
    {
        if (paramListPending())
            paramList();
    }

    public static boolean zeroParamListPending()
    {
        return paramListPending() /*|| empty?*/;
    }

    public static void argList() throws IOException
    {
        expr();
        if (check(COMMA))
        {
            match(COMMA);
            argList();
        }
    }

    public static boolean argListPending()
    {
        return exprPending();
    }

    public static void zeroArgList() throws IOException
    {
        if (argListPending())
            argList();
    }

    public static boolean zeroArgListPending()
    {
        return argListPending() /*|| empty?*/;
    }

    public static void functionCall() throws IOException
    {
        match(OPAREN);
        zeroArgList();
        match(CPAREN);
        match(EXCLAMATION);
    }

    public static boolean functionCallPending()
    {
        return check(OPAREN);
    }

    public static void fdef() throws IOException
    {
        match(FUNCTION);
        match(ID);
        match(OPAREN);
        zeroParamList();
        match(CPAREN);
    }

    public static boolean fdefPending()
    {
        return check(FUNCTION);
    }

    public static void print() throws IOException
    {
        match(YELL);
        match(OPAREN);
        printItem();
        match(CPAREN);
        match(EXCLAMATION);
    }

    public static boolean printPending()
    {
        return check(YELL);
    }

    public static void printItem() throws IOException
    {
        if (check(DOUBLE_QUOTE))
        {
            match(DOUBLE_QUOTE);
            match(STRING);
            match(DOUBLE_QUOTE);
        }
        else if (exprPending())
            expr();
        else if (unaryPending())
            unary();
    }

    public static boolean printItemPending()
    {
        return check(DOUBLE_QUOTE) || exprPending() || unaryPending();
    }

    public static void returnStatement() throws IOException
    {
        match(RECORD);
        expr();
        match(EXCLAMATION);
    }

    public static boolean returnStatementPending()
    {
        return check(RECORD);
    }

}