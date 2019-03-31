/**
 * Environment generator for MRKLEE: a Modern Right-reKursive Language for Everyday Expressions.
 * Designed and implemented by Hunter James, with guidance from Dr. John Lusth.
 */

public class Environment implements Types {

    static boolean debug = false;
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
        if (debug) System.out.println("ENVIRONMENTS - create: Creating a new environment");
        Lexeme env = cons(ENV,cons(VALUES,null,null),null);
        if (debug)
            {
            System.out.print("ENVIRONMENTS - create: The environment is ");
            env.display();
            System.out.print("\n");
            }
        return env;
        }

    static boolean sameVariable(String variable, Lexeme check)
        {
        if (debug) System.out.println("ENVIRONMENTS - sameVariable: check's value is " + check.value.toString() +", and variable is " + variable);
        if (variable.equals(check.value.toString())) {
            return true;
        }
        else return false;
        }

    static Lexeme lookupEnv(String variable, Lexeme env)
        {
        if (debug) System.out.println("ENVIRONMENTS - lookup: Looking for value in environment");
        while (env != null)
            {
            Lexeme table = car(env);
            Lexeme vars = car(table);
            Lexeme vals = cdr(table);
            while (vars != null)
                {
                if (sameVariable(variable,car(vars)))
                    {
                    if (debug)
                        {
                        System.out.print("ENVIRONMENTS - lookup: found it, it is ");
                        car(vals).display();
                        }
                    return car(vals);
                    }
                vars = cdr(vars);
                vals = cdr(vals);
                }
            env = cdr(env);
            }
        //System.out.println("ENVIRONMENT ERROR: variable " + variable + " is undefined");
        return null;
        }

    static Lexeme update(String variable, Lexeme env, Object replacement)
        {
        if (debug) System.out.println("ENVIRONMENTS - update: Updating value in environment");
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
            System.out.print("ENVIRONMENTS - insert: Adding variable type " + variable.type +
                    " with value " + variable.value.toString() + " and value type " + value.type + "\n");
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
            System.out.print("ENVIRONMENTS: Extending the environment with ");
            if (variables != null) variables.display();
            else System.out.print("NULL");
            System.out.print(" and ");
            if (values != null) values.display();
            else System.out.print("NULL");
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

    // FOR TESTING ONLY
    public static void main(String[] args)
        {
        System.out.println("Creating new Environment");
        Lexeme GlobalTree = createEnv();
        System.out.println("Inserting variable \'x\' with value 3");
        Lexeme idLexeme = new Lexeme(ID, "x");
        Lexeme valLexeme = new Lexeme(INTEGER, 3);
        insertEnv(idLexeme, valLexeme, GlobalTree);
        }
    }