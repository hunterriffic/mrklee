/**
 * Environment generator for MRKLEE: a Modern Right-reKursive Language for Everyday Expressions.
 * Designed and implemented by Hunter James, with guidance from Dr. John Lusth.
 */

public class Environment implements Types {

    // should have 2 parallel linked lists
    Lexeme newEnvironment()
        {
        return cons(ENV,cons(TAB,NULL,NULL),NULL);
        }

    /* Returns a lexeme whose left pointer is the second argument, whose right pointer is the third argument, whose type is the first argument. */
    public static Lexeme cons(String type, Lexeme l, Lexeme r)
        {
        return Lexeme(type,l,r);
        }

    /* Finding the value of a variable simply means looking up a variable/value in the first two lists of an environment structure. */
    /* If it is not there, we lookup the variable in the next two lists and so on. */
    public static Lexeme lookup(String variable, Lexeme env)
        {
        while (env != null)
            {
            vars = car(env);
            vals = car(cdr(env));
            while (vars != null)
                {
                if (sameVariable(variable,car(vars)))   // will need to write this
                    {
                    return car(vals);
                    }
                vars = cdr(vars);
                vals = cdr(vals);
                }
            env = cdr(env);
            }

        System.out.println("Variable ", variable, " is undefined");

        return null;
        }

    /* The update function is similar, only setCar is used to set the appropriate car pointer of the values list. */
    public static void update(String variable, Lexeme env, String updated)
        {
        while (env != null)
            {
            vars = car(env);
            vals = car(cdr(env));
            while (vars != null)
                {
                if (sameVariable(variable,car(vars)))   // will need to write this
                    {
                    setCar(vals, updated); // is this right?
                    }
                vars = cdr(vars);
                vals = cdr(vals);
                }
            env = cdr(cdr(env));
            }

        System.out.println("Variable ", variable, " is undefined");

        return null;
        }

    /* A variable is inserted into the local environment any time a simple variable is declared or a function defined. */
    /* Note that the local environment is represented as the first two parallel lists in a list of environments. */
    public static void insert(Lexeme env, String id, String val)    // what is the type for variable??
        {
        setCar(car(env),cons(id,car(car(env))));
        setCdr(car(env),cons(​v,​val,cdr(car(env))));
        return val;
        }

    /* This is the step is performed for a function call; a new environment is created, populated with the local parameters and values, and finally pointed to the defining environment. */
    /* The populating step is performed by cons-ing on a list of variables and a list of values onto the environment list containing the defining environment. */
    public static Lexeme extend(variables,values,env)
        {
        return cons(ENV,variables,cons(ENV,values,env));
        }

    public static Lexeme newScope(Lexeme env, Lexeme vars, Lexeme vals)
        {
        return cons(E, cons(T,vars,vals), env);
        }











    function getVal(env,id)
        {
        while (env != NULL) // walk id and val table in parallel
            {
            vars = car(car(env));
            vals = cdr(car(env));
            while (vars != NULL)
                {
                if (sameVar(id,car(vars)))
                    return car(vals);
                vars = cdr(vars);
                vals = cdr(vals);
                }
            env = cdr(env);
            }
        }











    function create()
        {
        return extend(nil,nil,nil);
        }


    public static void main()
        {
        env = newEnv();
        tree = parse(fileName);
        // print stuff?
        }

}