/**
 * The Lexical Analyzer for MRKLEE, the Modern Right-reKursive Language for Everyday Expressions.
 * Designed and implemented by Hunter James, with guidance from Dr. John Lusth. 
 */
import java.io.*;
import java.lang.Character;
import java.lang.String;

public class Lexer implements Types
{
    String pathName;
    private PushbackInputStream inputStream;
    static int lineNumber;

    /* Constructs Lexer object */
    public Lexer(PushbackInputStream input)
    {
        inputStream = input;
        lineNumber = 1;
    }


    private char readChar() throws java.io.IOException
    {
        char c = 0;
        try
        {
            c = (char)inputStream.read();
            if (c == '\n')
                ++Lexer.lineNumber;
        }
        catch (IOException e)
        {
            System.out.println("IOException occurred in readChar().");
        }
        return c;
    }

    private void pushback(char ch) throws java.io.IOException
    {
        try
        {
            if (ch == '\n')
                {
                --Lexer.lineNumber;
                }
            inputStream.unread(ch);
        }
        catch (IOException e)
        {
            System.out.println("An IOException has occurred in pushback().");
        }
    }


    /* Skips any arbitrary comments or whitespace not relevant to the language's implementation. */
    private void skipWhiteSpace() throws java.io.IOException
    {
        char ch = readChar();
        while (Character.isWhitespace(ch))
        {
            ch = readChar();
        }
        // the character that got us out of the loop was NOT whitespace, so we need to push it back so it can be read again
        pushback(ch);
        // check if comment
        if (ch == '#')
        {
            while (ch != '\n')
            {
                ch = readChar();
            }
            pushback(ch);
            skipWhiteSpace();
        }
    }

    /* Reads the complete variable or keyword token and distinguish between the two. */
    private Lexeme lexVariableOrKeyword() throws java.io.IOException
    {
        char ch;
        String token = "";

        ch = readChar();
        while (Character.isLetter(ch) || Character.isDigit(ch))
        {
            token = token + ch; // grow the token string
            ch = readChar();
        }
        // push back the character that got us out of the loop, may be some kind of punctuation
        pushback(ch);

        // token holds either a variable or a keyword, so figure it out
        switch (token) {
            case "if":
                return new Lexeme(IF);
            case "or":
                return new Lexeme(OR);
            case "none":
                return new Lexeme(NONE);
            case "go":
                return new Lexeme(GO);
            case "while":
                return new Lexeme(WHILE);
            case "yell":
                return new Lexeme(YELL);
            case "record":
                return new Lexeme(RECORD);
            case "funky":
                return new Lexeme(FUNCTION);
            case "var":
                return new Lexeme(VAR);
            default: // must be a ID (variable name)!
                return new Lexeme(ID, token);
        }
    }

    /* Reads the complete string. */
    private Lexeme lexString() throws java.io.IOException
    {
        char ch = readChar();
        StringBuilder token = new StringBuilder();
        while (ch != '\"' )
        {
            token.append(ch);
            ch = readChar();
        }
        return new Lexeme(STRING, token.toString());
    }

    private Lexeme lexNumber() throws java.io.IOException
    {
        char ch;
        StringBuilder token = new StringBuilder();
        boolean isReal = false;
        ch = readChar();
        while ((inputStream.available() > 0) && (Character.isDigit(ch) || ch == '.'))
        {
            token.append(ch);
            if (ch == '.' && isReal) return new Lexeme(BAD_NUMBER, token.toString());
            if (ch == '.') isReal = true;
            ch = readChar();
        }
        pushback(ch);
        if (isReal) return new Lexeme(REAL, Double.parseDouble(token.toString()));
        else return new Lexeme(INTEGER, Integer.parseInt(token.toString()));
    }

    /* Finds various tokens or characters and identify them as the proper lexemes. */
    Lexeme lex() throws java.io.IOException
    {
        char ch;
        skipWhiteSpace();
        ch = readChar();
        if (inputStream.available() == 0) return new Lexeme(ENDOFFILE);
        switch(ch)
        {
            //single character tokens
            case '(': return new Lexeme(OPAREN);
            case ')': return new Lexeme(CPAREN);
            case ',': return new Lexeme(COMMA);
            case '+': return new Lexeme(PLUS);
            case '-': return new Lexeme(MINUS);
            case '*': return new Lexeme(TIMES);
            case '/': return new Lexeme(BY);
            case '<':
            {
                ch = readChar();
                if (ch == '=') return new Lexeme(LESS_EQUALS);
                else
                {
                    pushback(ch);
                    return new Lexeme(LESS_THAN);
                }
            }
            case '>':
            {
                ch = readChar();
                if (ch == '=') return new Lexeme(GREATER_EQUALS);
                else
                {
                    pushback(ch);
                    return new Lexeme(GREATER_THAN);
                }
            }
            case '=':
            {
                ch = readChar();
                if (ch == '=') return new Lexeme(EQUAL_TO);
                else
                {
                    pushback(ch);
                    return new Lexeme(ASSIGN);
                }
            }
            case '!':
            {
                ch = readChar();
                if (ch == '=') return new Lexeme(NOT_EQUAL_TO);
                else
                {
                    pushback(ch);
                    return new Lexeme(EXCLAMATION);
                }
            }
            case '{': return new Lexeme(OBRACE);
            case '}': return new Lexeme(CBRACE);
            default:
                // multi-character tokens (only numbers, variables/keywords and strings)
                if (Character.isDigit(ch))
                {
                    pushback(ch);
                    return lexNumber();
                }
                else if (Character.isLetter(ch))
                {
                    pushback(ch);
                    return lexVariableOrKeyword();
                }
                else if (ch == '\"')
                {
                    return lexString();
                }
                else
                {
                    return new Lexeme(UNKNOWN,ch);
                }
        }
    }
}