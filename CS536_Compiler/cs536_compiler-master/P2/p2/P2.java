import java.util.*;
import java.io.*;
import java_cup.runtime.*; // defines Symbol

/**
 * This program is to be used to test the Wumbo scanner. This version is set up
 * to test all tokens, but you are required to test other aspects of the scanner
 * (e.g., input that causes errors, character numbers, values associated with
 * tokens)
 */
public class P2 {
    public static void main(String[] args) throws IOException {
        // exception may be thrown by yylex
        // test all tokens
        testAllTokens();
        CharNum.num = 1;

        testAllReservedWords();
        CharNum.num = 1;

        testAllSymbols();
        CharNum.num = 1;

        testGoodStrings();
        CharNum.num = 1;

        testUnterminatedString();
        CharNum.num = 1;

        testBadEscapeStrings();
        CharNum.num = 1;

        testVeryBadStrings();
        CharNum.num = 1;

        testMixedStrings();
        CharNum.num = 1;

        testComments();
        CharNum.num = 1;

        testFibLineCharNums();
        CharNum.num = 1;

        testIdentifiers();
        CharNum.num = 1;

        testIntLiterals();
        CharNum.num = 1;
    }

    /**
     * testAllTokens
     *
     * Open and read from file allTokens.in. For each token read, write the
     * corresponding string to allTokens.out using testingPrint.
     * Use `diff allTokens.in allTokens.out` to compare result.
     */
    private static void testAllTokens() throws IOException {
        // open input and output files
        FileReader inFile = null;
        PrintWriter outFile = null;
        try {
            inFile = new FileReader("allTokens.in");
            outFile = new PrintWriter(new FileWriter("allTokens.out"));
        } catch (FileNotFoundException ex) {
            System.err.println("File allTokens.in not found.");
            System.exit(-1);
        } catch (IOException ex) {
            System.err.println("allTokens.out cannot be opened.");
            System.exit(-1);
        }

        testingPrint(inFile, outFile);
    }

    /**
     * testAllReservedWords
     *
     * Open and read from file allReservedWords.in. For each token read, 
     * write the corresponding string to allReservedWords.out using 
     * testingPrint.
     * Use `diff allReservedWords.in allReservedWords.out` to compare result.
     */
    private static void testAllReservedWords() throws IOException {
        // open input and output files
        FileReader inFile = null;
        PrintWriter outFile = null;
        try {
            inFile = new FileReader("allReservedWords.in");
            outFile = new PrintWriter(new FileWriter("allReservedWords.out"));
        } catch (FileNotFoundException ex) {
            System.err.println("File allReservedWords.in not found.");
            System.exit(-1);
        } catch (IOException ex) {
            System.err.println("allReservedWords.out cannot be opened.");
            System.exit(-1);
        }

        testingPrint(inFile, outFile);
    }

    /**
     * testAllSymbols
     *
     * Open and read from file allSymbols.in. For each token read, 
     * write the corresponding string to allSymbols.out using 
     * testingPrint.
     * Use `diff allSymbols.in allSymbols.out` to compare result.
     */
    private static void testAllSymbols() throws IOException {
        // open input and output files
        FileReader inFile = null;
        PrintWriter outFile = null;
        try {
            inFile = new FileReader("allSymbols.in");
            outFile = new PrintWriter(new FileWriter("allSymbols.out"));
        } catch (FileNotFoundException ex) {
            System.err.println("File allSymbols.in not found.");
            System.exit(-1);
        } catch (IOException ex) {
            System.err.println("allSymbols.out cannot be opened.");
            System.exit(-1);
        }

        testingPrint(inFile, outFile);
    }

    /**
     * testGoodStrings
     *
     * Open and read from file goodStrings.in. For each token read, 
     * write the corresponding string to goodStrings.out using 
     * testingPrint.
     * Use `diff goodStrings.in goodStrings.out` to compare result.
     */
    private static void testGoodStrings() throws IOException {
        // open input and output files
        FileReader inFile = null;
        PrintWriter outFile = null;
        try {
            inFile = new FileReader("goodStrings.in");
            outFile = new PrintWriter(new FileWriter("goodStrings.out"));
        } catch (FileNotFoundException ex) {
            System.err.println("File goodStrings.in not found.");
            System.exit(-1);
        } catch (IOException ex) {
            System.err.println("goodStrings.out cannot be opened.");
            System.exit(-1);
        }

        testingPrint(inFile, outFile);
    }

    /**
     * testUnterminatedString
     *
     * Open and read from file unterminatedStrings.in. For each token read, 
     * write the corresponding string to unterminatedStrings.out using 
     * testingPrint.
     * String literal with \n and eof before closing quote are both tested.
     * Use `diff allSymbols.expected allSymbols.out` to compare result.
     */
    private static void testUnterminatedString() throws IOException {
        // open input and output files
        FileReader inFile = null;
        PrintWriter outFile = null;
        try {
            inFile = new FileReader("unterminatedStrings.in");
            outFile = new PrintWriter(new FileWriter("unterminatedStrings.out"));
        } catch (FileNotFoundException ex) {
            System.err.println("File unterminatedStrings.in not found.");
            System.exit(-1);
        } catch (IOException ex) {
            System.err.println("unterminatedStrings.out cannot be opened.");
            System.exit(-1);
        }

        testingPrint(inFile, outFile);
    }

    /**
     * testBadEscapeStrings
     *
     * Open and read from file unterminatedStrings.in. For each token read, 
     * write the corresponding string to unterminatedStrings.out using 
     * testingPrint.
     * Use `diff allSymbols.expected allSymbols.out` to compare result.
     */
    private static void testBadEscapeStrings() throws IOException {
        // open input and output files
        FileReader inFile = null;
        PrintWriter outFile = null;
        try {
            inFile = new FileReader("badEscapeStrings.in");
            outFile = new PrintWriter(new FileWriter("badEscapeStrings.out"));
        } catch (FileNotFoundException ex) {
            System.err.println("File badEscapeStrings.in not found.");
            System.exit(-1);
        } catch (IOException ex) {
            System.err.println("badEscapeStrings.out cannot be opened.");
            System.exit(-1);
        }

        testingPrint(inFile, outFile);
    }

    /**
     * testVeryBadStrings
     *
     * Open and read from file veryBadStrings.in. For each token read, 
     * write the corresponding string to veryBadStrings.out using 
     * testingPrint.
     * String literal with \n and eof before closing quote are both tested.
     * Use `diff veryBadStrings.expected veryBadStrings.out` to compare result.
     */
    private static void testVeryBadStrings() throws IOException {
        // open input and output files
        FileReader inFile = null;
        PrintWriter outFile = null;
        try {
            inFile = new FileReader("veryBadStrings.in");
            outFile = new PrintWriter(new FileWriter("veryBadStrings.out"));
        } catch (FileNotFoundException ex) {
            System.err.println("File veryBadStrings.in not found.");
            System.exit(-1);
        } catch (IOException ex) {
            System.err.println("veryBadStrings.out cannot be opened.");
            System.exit(-1);
        }

        testingPrint(inFile, outFile);
    }

    /**
     * testMixedStrings
     *
     * Open and read from file mixedStrings.in. For each token read, 
     * write the corresponding string to mixedStrings.out using 
     * testingPrint.
     * Use `diff mixedStrings.expected mixedStrings.out` to compare result.
     */
    private static void testMixedStrings() throws IOException {
        // open input and output files
        FileReader inFile = null;
        PrintWriter outFile = null;
        try {
            inFile = new FileReader("mixedStrings.in");
            outFile = new PrintWriter(new FileWriter("mixedStrings.out"));
        } catch (FileNotFoundException ex) {
            System.err.println("File mixedStrings.in not found.");
            System.exit(-1);
        } catch (IOException ex) {
            System.err.println("mixedStrings.out cannot be opened.");
            System.exit(-1);
        }

        testingPrint(inFile, outFile);
    }

    /**
     * testComments
     *
     * Open and read from file comments.in. For each token read, 
     * write the corresponding string to comments.out using 
     * testingPrint.
     * Use `diff comments.expected comments.out` to compare result.
     */
    private static void testComments() throws IOException {
        // open input and output files
        FileReader inFile = null;
        PrintWriter outFile = null;
        try {
            inFile = new FileReader("comments.in");
            outFile = new PrintWriter(new FileWriter("comments.out"));
        } catch (FileNotFoundException ex) {
            System.err.println("File comments.in not found.");
            System.exit(-1);
        } catch (IOException ex) {
            System.err.println("comments.out cannot be opened.");
            System.exit(-1);
        }

        testingPrint(inFile, outFile);
    }

    /**
     * testFibLineCharNums
     *
     * Open and read from file fib.in. For each token read:
     * (1) write the corresponding string to fib.out;
     * (2) Check the scanned line number and char number with expected;
     * using testingPrintWithLineCharNumCheck.
     * Use `diff fib.expected fib.out` to compare result.
     */
    private static void testFibLineCharNums() throws IOException {
        // open input and output files
        FileReader inFile = null;
        PrintWriter outFile = null;
        try {
            inFile = new FileReader("fib.in");
            outFile = new PrintWriter(new FileWriter("fib.out"));
        } catch (FileNotFoundException ex) {
            System.err.println("File fib.in not found.");
            System.exit(-1);
        } catch (IOException ex) {
            System.err.println("fib.out cannot be opened.");
            System.exit(-1);
        }

        List<String> expected = new ArrayList<>(Arrays.asList(
            "1:1", "1:5", "1:8", "1:9", "1:13", "1:14", "1:16",
            "2:5", "2:8", "2:9", "2:11", "2:14", "2:15", "2:17",
            "3:9", "3:16", "3:17",
            "4:5", "4:7", "4:12",
            "5:9", "5:16", "5:19", "5:20", "5:21", "5:22", "5:23", "5:25", "5:27", "5:30", "5:31", "5:32", "5:33", "5:34", "5:35",
            "7:5",
            "9:5",
            "10:1"
        ));
        
        testingPrintWithLineCharNumCheck(inFile, outFile, expected);
    }

    /**
     * testIdentifiers
     *
     * Open and read from file identifiers.in. For each token read, 
     * write the corresponding string to identifiers.out using 
     * testingPrint.
     * Use `diff identifiers.expected identifiers.out` to compare result.
     */
    private static void testIdentifiers() throws IOException {
        // open input and output files
        FileReader inFile = null;
        PrintWriter outFile = null;
        try {
            inFile = new FileReader("identifiers.in");
            outFile = new PrintWriter(new FileWriter("identifiers.out"));
        } catch (FileNotFoundException ex) {
            System.err.println("File identifiers.in not found.");
            System.exit(-1);
        } catch (IOException ex) {
            System.err.println("identifiers.out cannot be opened.");
            System.exit(-1);
        }

        testingPrint(inFile, outFile);
    }

    /**
     * testIntLiterals
     *
     * Open and read from file intLiterals.in. For each token read, 
     * write the corresponding string to intLiterals.out using 
     * testingPrint.
     * Use `diff intLiterals.expected intLiterals.out` to compare result.
     */
    private static void testIntLiterals() throws IOException {
        // open input and output files
        FileReader inFile = null;
        PrintWriter outFile = null;
        try {
            inFile = new FileReader("intLiterals.in");
            outFile = new PrintWriter(new FileWriter("intLiterals.out"));
        } catch (FileNotFoundException ex) {
            System.err.println("File intLiterals.in not found.");
            System.exit(-1);
        } catch (IOException ex) {
            System.err.println("intLiterals.out cannot be opened.");
            System.exit(-1);
        }

        testingPrint(inFile, outFile);
    }

    /**
     * testingPrint
     * 
     * Takes an input file and output file. Create a scanner to read token from
     * the input file and print accordingly.
     * 
     * @param inFile input file
     * @param outFile output file
     * @throws IOException thrown by next_token when no rules match
     */
    private static void testingPrint(FileReader inFile, PrintWriter outFile) throws IOException {
        // create and call the scanner
        Yylex scanner = new Yylex(inFile);
        Symbol token = scanner.next_token();
        while (token.sym != sym.EOF) {
            switch (token.sym) {
            case sym.BOOL:
                outFile.println("bool");
                break;
            case sym.INT:
                outFile.println("int");
                break;
            case sym.VOID:
                outFile.println("void");
                break;
            case sym.TRUE:
                outFile.println("true");
                break;
            case sym.FALSE:
                outFile.println("false");
                break;
            case sym.STRUCT:
                outFile.println("struct");
                break;
            case sym.CIN:
                outFile.println("cin");
                break;
            case sym.COUT:
                outFile.println("cout");
                break;
            case sym.IF:
                outFile.println("if");
                break;
            case sym.ELSE:
                outFile.println("else");
                break;
            case sym.WHILE:
                outFile.println("while");
                break;
            case sym.RETURN:
                outFile.println("return");
                break;
            case sym.ID:
                outFile.println(((IdTokenVal) token.value).idVal);
                break;
            case sym.INTLITERAL:
                outFile.println(((IntLitTokenVal) token.value).intVal);
                break;
            case sym.STRINGLITERAL:
                outFile.println(((StrLitTokenVal) token.value).strVal);
                break;
            case sym.LCURLY:
                outFile.println("{");
                break;
            case sym.RCURLY:
                outFile.println("}");
                break;
            case sym.LPAREN:
                outFile.println("(");
                break;
            case sym.RPAREN:
                outFile.println(")");
                break;
            case sym.SEMICOLON:
                outFile.println(";");
                break;
            case sym.COMMA:
                outFile.println(",");
                break;
            case sym.DOT:
                outFile.println(".");
                break;
            case sym.WRITE:
                outFile.println("<<");
                break;
            case sym.READ:
                outFile.println(">>");
                break;
            case sym.PLUSPLUS:
                outFile.println("++");
                break;
            case sym.MINUSMINUS:
                outFile.println("--");
                break;
            case sym.PLUS:
                outFile.println("+");
                break;
            case sym.MINUS:
                outFile.println("-");
                break;
            case sym.TIMES:
                outFile.println("*");
                break;
            case sym.DIVIDE:
                outFile.println("/");
                break;
            case sym.NOT:
                outFile.println("!");
                break;
            case sym.AND:
                outFile.println("&&");
                break;
            case sym.OR:
                outFile.println("||");
                break;
            case sym.EQUALS:
                outFile.println("==");
                break;
            case sym.NOTEQUALS:
                outFile.println("!=");
                break;
            case sym.LESS:
                outFile.println("<");
                break;
            case sym.GREATER:
                outFile.println(">");
                break;
            case sym.LESSEQ:
                outFile.println("<=");
                break;
            case sym.GREATEREQ:
                outFile.println(">=");
                break;
            case sym.ASSIGN:
                outFile.println("=");
                break;
            default:
                outFile.println("UNKNOWN TOKEN");
            } // end switch

            token = scanner.next_token();
        } // end while
        outFile.close();
    }

    /**
     * testingPrintWithLineCharNumCheck
     * 
     * Takes an input file and output file. Create a scanner to read token from
     * the input file and print accordingly. For each token, check with the
     * entry in lineCharNums.
     * 
     * @param inFile input file
     * @param outFile output file
     * @param lineCharNums expected string containing line num and char num for 
     *        each token, in the format of"line:char"
     * @throws IOException thrown by next_token when no rules match
     */
    private static void testingPrintWithLineCharNumCheck(
        FileReader inFile, PrintWriter outFile, List<String> lineCharNums) 
        throws IOException {
        // create and call the scanner
        Yylex scanner = new Yylex(inFile);
        Symbol token = scanner.next_token();
        int i = 0;
        while (token.sym != sym.EOF) {
            TokenVal tokenVal = ((TokenVal) token.value);
            String expectedNum = lineCharNums.get(i);
            int lineNum = tokenVal.linenum;
            int charNum = tokenVal.charnum;
            int expectedLineNum = Integer.parseInt(expectedNum.split(":")[0]);
            int expectedCharNum = Integer.parseInt(expectedNum.split(":")[1]);
            if (lineNum != expectedLineNum || charNum != expectedCharNum) {
                System.out.println("***ERROR*** Expected: " + lineCharNums.get(i) + ", get: " + lineNum + ":" + charNum);
            }
            i = i + 1;

            switch (token.sym) {
            case sym.BOOL:
                outFile.println("bool");
                break;
            case sym.INT:
                outFile.println("int");
                break;
            case sym.VOID:
                outFile.println("void");
                break;
            case sym.TRUE:
                outFile.println("true");
                break;
            case sym.FALSE:
                outFile.println("false");
                break;
            case sym.STRUCT:
                outFile.println("struct");
                break;
            case sym.CIN:
                outFile.println("cin");
                break;
            case sym.COUT:
                outFile.println("cout");
                break;
            case sym.IF:
                outFile.println("if");
                break;
            case sym.ELSE:
                outFile.println("else");
                break;
            case sym.WHILE:
                outFile.println("while");
                break;
            case sym.RETURN:
                outFile.println("return");
                break;
            case sym.ID:
                outFile.println(((IdTokenVal) token.value).idVal);
                break;
            case sym.INTLITERAL:
                outFile.println(((IntLitTokenVal) token.value).intVal);
                break;
            case sym.STRINGLITERAL:
                outFile.println(((StrLitTokenVal) token.value).strVal);
                break;
            case sym.LCURLY:
                outFile.println("{");
                break;
            case sym.RCURLY:
                outFile.println("}");
                break;
            case sym.LPAREN:
                outFile.println("(");
                break;
            case sym.RPAREN:
                outFile.println(")");
                break;
            case sym.SEMICOLON:
                outFile.println(";");
                break;
            case sym.COMMA:
                outFile.println(",");
                break;
            case sym.DOT:
                outFile.println(".");
                break;
            case sym.WRITE:
                outFile.println("<<");
                break;
            case sym.READ:
                outFile.println(">>");
                break;
            case sym.PLUSPLUS:
                outFile.println("++");
                break;
            case sym.MINUSMINUS:
                outFile.println("--");
                break;
            case sym.PLUS:
                outFile.println("+");
                break;
            case sym.MINUS:
                outFile.println("-");
                break;
            case sym.TIMES:
                outFile.println("*");
                break;
            case sym.DIVIDE:
                outFile.println("/");
                break;
            case sym.NOT:
                outFile.println("!");
                break;
            case sym.AND:
                outFile.println("&&");
                break;
            case sym.OR:
                outFile.println("||");
                break;
            case sym.EQUALS:
                outFile.println("==");
                break;
            case sym.NOTEQUALS:
                outFile.println("!=");
                break;
            case sym.LESS:
                outFile.println("<");
                break;
            case sym.GREATER:
                outFile.println(">");
                break;
            case sym.LESSEQ:
                outFile.println("<=");
                break;
            case sym.GREATEREQ:
                outFile.println(">=");
                break;
            case sym.ASSIGN:
                outFile.println("=");
                break;
            default:
                outFile.println("UNKNOWN TOKEN");
            } // end switch

            token = scanner.next_token();
        } // end while
        outFile.close();
    }
}
