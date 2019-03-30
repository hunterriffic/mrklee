/**
 * Environment generator for MRKLEE: a Modern Right-reKursive Language for Everyday Expressions.
 * Designed and implemented by Hunter James, with guidance from Dr. John Lusth.
 */

public class Environment implements Types {

    static boolean debug = true;
    static Lexeme cons(String type, Lexeme l, Lexeme r) { return new Lexeme(type, l, r); }
    static Lexeme car(Lexeme l) {
        return l.left;
    }
    static Lexeme cdr(Lexeme l) {
        return l.right;
    }
    static void setCar(Lexeme l, Lexeme s) {
        l.left = s;
    }
    static void setCdr(Lexeme l, Lexeme s) {
        l.right = s;
    }

    static Lexeme createEnv()
        {
        if (debug) System.out.println("Creating a new environment");
        Lexeme env = cons(ENV,cons(VALUES,null,null),null);
        if (debug)
            {
            System.out.print("The environment is ");
            env.display();
            System.out.print("\n");
            }
        return env;
        }

    static boolean sameVariable(String variable, Lexeme check)
        {
        System.out.println("check's value is " + check.value.toString() +", and variable is " + variable);
        check.display();
        if (variable.equals(check.value.toString())) {
            return true;
        }
        else return false;
        }

    static Lexeme lookupEnv(String variable, Lexeme env)
        {
        if (debug) System.out.println("Looking for value in environment");
        while (env != null)
            {
            Lexeme table = car(env);
            Lexeme vars = car(table);
            Lexeme vals = cdr(table);
            while (vars != null)
                {
                if (sameVariable(variable,car(vars)))
                    {
                    return car(vals);
                    }
                vars = cdr(vars);
                vals = cdr(vals);
                }
            env = cdr(env);
            }
        System.out.println("ERROR: variable " + variable + " is undefined");
        return null;
        }

    static Lexeme update(String variable, Lexeme env, Object replacement)
        {
        if (debug) System.out.println("Updating value in environment");
        while (env != null)
            {
            Lexeme table = car(env);
            Lexeme vars = car(table);
            Lexeme vals = cdr(table);
            while (vars != null)
                {
                if (sameVariable(variable,car(vars)))
                    {
                    Lexeme r = car(vals);
                    r.value = replacement;
                    setCar(vals,r);
                    return car(vals);
                    }
                vars = cdr(vars);
                vals = cdr(vals);
                }
            env = cdr(env);
            }
            System.out.println("ERROR: variable " + variable + " is undefined");
            return null;
        }

    static Lexeme insertEnv(Lexeme variable, Lexeme value, Lexeme env)
        {
        if (debug)
            {
            System.out.print("Adding variable ");
            variable.display();
            System.out.println(" with value " + variable.value.toString() + " and value type:");
            value.display();
            }
        Lexeme table = car(env);
        setCar(table,cons(JOIN,variable,car(table)));
        setCdr(table,cons(JOIN,value,cdr(table)));
        return value;
        }

    static Lexeme extendEnv(Lexeme variables, Lexeme values, Lexeme env)
        {
        if (debug)
            {
            System.out.print("Extending the environment with ");
            variables.display();
            System.out.print(" and ");
            values.display();
            System.out.print("\n");
            }
        return cons(ENV, cons(VALUES, variables, values), env);
        }
    /*
    // displaying the environment; this function should have two forms; one form displays only the local table, the other all tables
    static void printAllEnvironments(Lexeme tree)
        {
            int i = 0;
            while (tree != null)
                {
                System.out.println("Table " + i + ":");
                System.out.println("ID \'" + car(tree).value +"/' is" + cdr(tree));
                }
        }
    */
    public static void main(String[] args)
        {
//        Lexeme env = createEnv();
//        Lexeme x = new Lexeme(INTEGER,5);
//        insertEnv(x,x,env);
        //Parser tree =  new Parser;
        System.out.println("Creating new Environment");
        Lexeme GlobalTree = createEnv();
        //printAllEnvironments(GlobalTree);
        System.out.println("Inserting variable \'x\' with value 3");
        Lexeme idLexeme = new Lexeme(ID, "x");
        Lexeme valLexeme = new Lexeme(INTEGER, 3);
        insertEnv(idLexeme, valLexeme, GlobalTree);

        //printAllEnvironments(GlobalTree);

        System.out.println("Extending the environment with y:4 and z:\"hello\"\n");
        Lexeme localTree = createEnv();
        idLexeme = new Lexeme(ID, "y");
        valLexeme = new Lexeme(INTEGER, 4);
        System.out.println("ID \'y\' has value " + insertEnv(idLexeme, valLexeme, localTree).value);
        idLexeme = new Lexeme(ID, "z");
        valLexeme = new Lexeme(STRING, "hello");
        insertEnv(idLexeme, valLexeme, localTree);
        System.out.println("EXTENDING...");
        GlobalTree = extendEnv(car(car(localTree)), cdr(car(localTree)), GlobalTree);
        //printAllEnvironments(GlobalTree);

        System.out.println("Inserting variable w with value \"why\" into most local environment");

        idLexeme = new Lexeme(ID, "w");
        valLexeme = new Lexeme(STRING, "why");
        System.out.println("ID \'w\' has value " + insertEnv(idLexeme, valLexeme, GlobalTree).value);
        System.out.println();


        //printAllEnvironments(GlobalTree);

        System.out.println("Finding value of variable y");
        System.out.println("ID \'y\' has value " + lookupEnv("y", GlobalTree).value);
        System.out.println();
        System.out.println("Finding value of variable x");
        System.out.println("ID \'x\' has value " + lookupEnv("x", GlobalTree).value);
        System.out.println();
        System.out.println("Updating value of variable x to 6");
        System.out.println("ID \'x\' value updated to " + update("x", GlobalTree,6).value);
        System.out.println();
        //printAllEnvironments(GlobalTree);
        System.out.println();

        System.exit(0);
        }
    }