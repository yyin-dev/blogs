###
# This Makefile can be used to make a parser for the Wumbo language
# (parser.class) and to make a program (P5.class) that tests the parser and
# the unparse methods in ast.java.
#
# make clean removes all generated files.
#
###

JC = javac

CP = ./deps:.

P5.class: P5.java parser.class Yylex.class ASTnode.class
	$(JC) -g -cp $(CP) P5.java

parser.class: parser.java ASTnode.class Yylex.class ErrMsg.class
	$(JC) -g -cp $(CP) parser.java

parser.java: Wumbo.cup
	java -cp $(CP) java_cup.Main < Wumbo.cup

Yylex.class: Wumbo.jlex.java sym.class ErrMsg.class
	$(JC) -g -cp $(CP) Wumbo.jlex.java

ASTnode.class: ast.java Type.java Sym.class
	$(JC) -g -cp $(CP) ast.java Type.java

Wumbo.jlex.java: Wumbo.jlex sym.class
	java -cp $(CP) JLex.Main Wumbo.jlex

sym.class: sym.java
	$(JC) -g -cp $(CP) sym.java

sym.java: Wumbo.cup
	java java_cup.Main < Wumbo.cup

ErrMsg.class: ErrMsg.java
	$(JC) -g -cp $(CP) ErrMsg.java

Sym.class: Sym.java Type.class ast.java
	$(JC) -g -cp $(CP) Sym.java ast.java

SymTable.class: SymTable.java Sym.class DuplicateSymException.class EmptySymTableException.class
	$(JC) -g -cp $(CP) SymTable.java

Type.class: Type.java
	$(JC) -g -cp $(CP) Type.java ast.java

DuplicateSymException.class: DuplicateSymException.java
	$(JC) -g -cp $(CP) DuplicateSymException.java

EmptySymTableException.class: EmptySymTableException.java
	$(JC) -g -cp $(CP) EmptySymTableException.java

###
# test
#
test:
	java -cp $(CP) P5 test.wumbo test.out
	java -cp $(CP) P5 typeErrors.wumbo testErr.out

example:
	java -cp $(CP) P5 example.wumbo example.out 2> example.err
	diff example.err example.expected

###
# clean
###
clean:
	rm -f *~ *.class parser.java Wumbo.jlex.java sym.java

cleantest:
	rm -f test.out
