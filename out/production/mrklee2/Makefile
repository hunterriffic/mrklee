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

MAIN = Parser

default: classes

classes: $(CLASSES:.java=.class)

recognizer : classes

test1 : Parser.class
	$(JVM) Parser test1.mrk

test2: Parser.class
	$(JVM) Parser test2.mrk

test3: Parser.class
	$(JVM) Parser test3.mrk

test4: Parser.class
	$(JVM) Parser test4.mrk

test5: Parser.class
	$(JVM) Parser test5.mrk

run : $(MAIN).class
	$(JVM) $(MAIN) test1.mrk
	$(JVM) $(MAIN) test2.mrk
	$(JVM) $(MAIN) test3.mrk
	$(JVM) $(MAIN) test4.mrk
	$(JVM) $(MAIN) test5.mrk

clean:
	$(RM) *.class