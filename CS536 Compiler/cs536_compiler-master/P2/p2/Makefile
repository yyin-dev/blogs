###
# This Makefile can be used to make a scanner for the Wumbo language
# (Yylex.class) and to make a program that tests the scanner (P2.class).
#
# The default makes both the scanner and the test program.
#
# make clean removes all generated files.
#
# Note: P2.java will not compile unless Yylex.class exists.
#
###

# define the java compiler to be used and the flags
JC = javac
FLAGS = -g -cp $(CP) 
CP = ./deps:.

P2.class: P2.java Yylex.class sym.class
	$(JC) $(FLAGS) P2.java

Yylex.class: wumbo.jlex.java ErrMsg.class sym.class
	$(JC) $(FLAGS) wumbo.jlex.java

wumbo.jlex.java: wumbo.jlex sym.class
	java -cp $(CP) JLex.Main wumbo.jlex

sym.class: sym.java
	$(JC) $(FLAGS) sym.java

ErrMsg.class: ErrMsg.java
	$(JC) $(FLAGS) ErrMsg.java

	
###
# testing - add more here to run your tester and compare its results
# to expected results
###
test:
	java -cp $(CP) P2
	diff allTokens.in allTokens.out
	diff allReservedWords.in allReservedWords.in
	diff goodStrings.in goodStrings.out
	diff unterminatedStrings.out unterminatedStrings.expected
	diff badEscapeStrings.out unterminatedStrings.expected
	diff veryBadStrings.out veryBadStrings.expected
	diff mixedStrings.out mixedStrings.expected
	diff comments.out comments.expected
	diff fib.out fib.expected
	diff identifiers.out identifiers.expected
	diff intLiterals.out intLiterals.expected

###
# clean up
###

clean:
	rm -f *~ *.class wumbo.jlex.java

cleantest:
	rm -f *.out
