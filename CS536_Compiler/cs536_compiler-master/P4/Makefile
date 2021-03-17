###
# This Makefile can be used to make a parser for the Wumbo language
# (parser.class) and to make a program (P4.class) that tests the parser, unparse and
# name analysis methods in ast.java.
#
# make clean removes all generated files.
#
###

JC = javac
FLAGS = -g
CP = ./deps:.

P4.class: P4.java parser.class Yylex.class ASTnode.class
	$(JC) $(FLAGS) -cp $(CP) P4.java

parser.class: parser.java ASTnode.class Yylex.class ErrMsg.class
	$(JC) $(FLAGS) -cp $(CP) parser.java

parser.java: Wumbo.cup
	java -cp $(CP) java_cup.Main < Wumbo.cup

Yylex.class: Wumbo.jlex.java sym.class ErrMsg.class
	$(JC) $(FLAGS) -cp $(CP) Wumbo.jlex.java

ASTnode.class: ast.java SymTable.class
	$(JC) $(FLAGS) -cp $(CP) ast.java

Wumbo.jlex.java: Wumbo.jlex sym.class
	java -cp $(CP) JLex.Main Wumbo.jlex

sym.class: sym.java
	$(JC) $(FLAGS) -cp $(CP) sym.java

sym.java: Wumbo.cup
	java -cp $(CP) java_cup.Main < Wumbo.cup

ErrMsg.class: ErrMsg.java
	$(JC) $(FLAGS) -cp $(CP) ErrMsg.java

Sym.class: Sym.java
	$(JC) $(FLAGS) -cp $(CP) Sym.java

SymTable.class: SymTable.java Sym.class DuplicateSymException.class EmptySymTableException.class
	$(JC) $(FLAGS) -cp $(CP) SymTable.java

DuplicateSymException.class: DuplicateSymException.java
	$(JC) $(FLAGS) -cp $(CP) DuplicateSymException.java

EmptySymTableException.class: EmptySymTableException.java
	$(JC) $(FLAGS) -cp $(CP) EmptySymTableException.java

##test
test:
	java -cp $(CP) P4 nameErrors.wumbo nameErrors.out
	java -cp $(CP) P4 test.wumbo test.out

###
# clean
###
clean:
	rm -f *~ *.class parser.java Wumbo.jlex.java sym.java

## cleantest (delete test artifacts)
cleantest:
	rm -f *.out
