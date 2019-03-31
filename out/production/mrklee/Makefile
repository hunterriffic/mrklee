JFLAGS = -g
JC = javac
JVM = java
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
	Types.java \
	Lexeme.java \
	Lexer.java \
	Parser.java \
	Environment.java \
	PrettyPrinter.java \
	Evaluator.java \

MAIN = Evaluator

default: classes

classes: $(CLASSES:.java=.class)

evaluator : classes

error1 : Parser.class
	$(JVM) Parser error1.mrk

error1x : Evaluator.class
	$(JVM) Evaluator error1.mrk

error2 : Parser.class
	$(JVM) Parser error2.mrk

error2x : Evaluator.class
	$(JVM) Evaluator error2.mrk

error3 : Parser.class
	$(JVM) Parser error3.mrk

error3x : Evaluator.class
	$(JVM) Evaluator error3.mrk

error4 : Parser.class
	$(JVM) Parser error4.mrk

error4x : Evaluator.class
	$(JVM) Evaluator error4.mrk

error5 : Parser.class
	$(JVM) Parser error5.mrk

error5x : Evaluator.class
	$(JVM) Evaluator error5.mrk

arrays : Parser.class
	$(JVM) Parser arrays.mrk

arraysx : Evaluator.class
	$(JVM) Evaluator arrays.mrk

conditionals : Parser.class
	$(JVM) Parser conditionals.mrk

conditionalsx : Evaluator.class
	$(JVM) Evaluator conditionals.mrk

recursion : Parser.class
	$(JVM) Parser recursion.mrk

recursionx : Evaluator.class
	$(JVM) Evaluator recursion.mrk

iteration : Parser.class
	$(JVM) Parser iteration.mrk

iterationx : Evaluator.class
	$(JVM) Evaluator iteration.mrk

functions : Parser.class
	$(JVM) Parser functions.mrk

functionsx : Evaluator.class
	$(JVM) Evaluator functions.mrk

lambda : Parser.class
	$(JVM) Parser lambda.mrk

lambdax : Evaluator.class
	$(JVM) Evaluator lambda.mrk

objects : Parser.class
	$(JVM) Parser objects.mrk

objectsx : Evaluator.class
	$(JVM) Evaluator objects.mrk

run : $(MAIN).class
	$(JVM) $(MAIN) error1.mrk
	$(JVM) $(MAIN) error2.mrk
	$(JVM) $(MAIN) error3.mrk
	$(JVM) $(MAIN) error4.mrk
	$(JVM) $(MAIN) error5.mrk
	$(JVM) $(MAIN) arrays.mrk
	$(JVM) $(MAIN) conditionals.mrk
	$(JVM) $(MAIN) recursion.mrk
	$(JVM) $(MAIN) iteration.mrk
	$(JVM) $(MAIN) functions.mrk
	$(JVM) $(MAIN) lambda.mrk
	$(JVM) $(MAIN) objects.mrk

clean:
	$(RM) *.class
