import java_cup.runtime.*; // defines the Symbol class
// The generated scanner will return a Symbol for each token that it finds.
// A Symbol contains an Object field named value; that field will be of type
// TokenVal, defined below.
//
// A TokenVal object contains the line number on which the token occurs as
// well as the number of the character on that line that starts the token.
// Some tokens (literals and IDs) also include the value of the token.
class TokenVal {
  // fields
    int linenum;
    int charnum;
  // constructor
    TokenVal(int line, int ch) {
        linenum = line;
        charnum = ch;
    }
}
class IntLitTokenVal extends TokenVal {
  // new field: the value of the integer literal
    int intVal;
  // constructor
    IntLitTokenVal(int line, int ch, int val) {
        super(line, ch);
        intVal = val;
    }
}
class IdTokenVal extends TokenVal {
  // new field: the value of the identifier
    String idVal;
  // constructor
    IdTokenVal(int line, int ch, String val) {
        super(line, ch);
    idVal = val;
    }
}
class StrLitTokenVal extends TokenVal {
  // new field: the value of the string literal
    String strVal;
  // constructor
    StrLitTokenVal(int line, int ch, String val) {
        super(line, ch);
        strVal = val;
    }
}
// The following class is used to keep track of the character number at which
// the current token starts on its line.
class CharNum {
    static int num=1;
}
class Utility {
  public static Symbol parseBasic(int tokenNum, int lineNum, String text) {
    Symbol s = new Symbol(tokenNum, new TokenVal(lineNum, CharNum.num));
    CharNum.num += text.length();
    return s;
  }
}
// The most challenging JLex rules are for the STRINGLITERAL token (for which 
// you will need several rules: for a correct string literal, for an 
// unterminated string literal, for a string literal that contains a 
// bad escaped character, and for a string literal that contains a bad 
// escaped character and is unterminated).
// The special case is with the new line character. What is the difference
// between the following 2 strings?
// "foo\n"
// "foo
// "
// The problem is a backslash as the last character on the line, which is just 
// like escaping a newline character. The regex matching should stop.
// The case of escaping a newline character (backslash at the end of a line)
// is special. It should not be considered like a normal bad escape but should 
// be categorized as a VERY bad string, instead of a string containging
// bad escape.
// So in CANT_ESCAPE, we also negate {NEWLINE}. So there're two possibilites for
// VERY_BAD_STR:
// (1) The character before the newline character is not a backslash (nor a
// closing double quote), then the string should contain some other bad escapes
// and end with a newline character;
// (2) The character before the newline character is a backslash, then the
// string might not contain any other bad escapes. But the last character must
// be the backslash.


class Yylex implements java_cup.runtime.Scanner {
	private final int YY_BUFFER_SIZE = 512;
	private final int YY_F = -1;
	private final int YY_NO_STATE = -1;
	private final int YY_NOT_ACCEPT = 0;
	private final int YY_START = 1;
	private final int YY_END = 2;
	private final int YY_NO_ANCHOR = 4;
	private final int YY_BOL = 128;
	private final int YY_EOF = 129;
	private java.io.BufferedReader yy_reader;
	private int yy_buffer_index;
	private int yy_buffer_read;
	private int yy_buffer_start;
	private int yy_buffer_end;
	private char yy_buffer[];
	private int yyline;
	private boolean yy_at_bol;
	private int yy_lexical_state;

	Yylex (java.io.Reader reader) {
		this ();
		if (null == reader) {
			throw (new Error("Error: Bad input stream initializer."));
		}
		yy_reader = new java.io.BufferedReader(reader);
	}

	Yylex (java.io.InputStream instream) {
		this ();
		if (null == instream) {
			throw (new Error("Error: Bad input stream initializer."));
		}
		yy_reader = new java.io.BufferedReader(new java.io.InputStreamReader(instream));
	}

	private Yylex () {
		yy_buffer = new char[YY_BUFFER_SIZE];
		yy_buffer_read = 0;
		yy_buffer_index = 0;
		yy_buffer_start = 0;
		yy_buffer_end = 0;
		yyline = 0;
		yy_at_bol = true;
		yy_lexical_state = YYINITIAL;
	}

	private boolean yy_eof_done = false;
	private final int YYINITIAL = 0;
	private final int yy_state_dtrans[] = {
		0
	};
	private void yybegin (int state) {
		yy_lexical_state = state;
	}
	private int yy_advance ()
		throws java.io.IOException {
		int next_read;
		int i;
		int j;

		if (yy_buffer_index < yy_buffer_read) {
			return yy_buffer[yy_buffer_index++];
		}

		if (0 != yy_buffer_start) {
			i = yy_buffer_start;
			j = 0;
			while (i < yy_buffer_read) {
				yy_buffer[j] = yy_buffer[i];
				++i;
				++j;
			}
			yy_buffer_end = yy_buffer_end - yy_buffer_start;
			yy_buffer_start = 0;
			yy_buffer_read = j;
			yy_buffer_index = j;
			next_read = yy_reader.read(yy_buffer,
					yy_buffer_read,
					yy_buffer.length - yy_buffer_read);
			if (-1 == next_read) {
				return YY_EOF;
			}
			yy_buffer_read = yy_buffer_read + next_read;
		}

		while (yy_buffer_index >= yy_buffer_read) {
			if (yy_buffer_index >= yy_buffer.length) {
				yy_buffer = yy_double(yy_buffer);
			}
			next_read = yy_reader.read(yy_buffer,
					yy_buffer_read,
					yy_buffer.length - yy_buffer_read);
			if (-1 == next_read) {
				return YY_EOF;
			}
			yy_buffer_read = yy_buffer_read + next_read;
		}
		return yy_buffer[yy_buffer_index++];
	}
	private void yy_move_end () {
		if (yy_buffer_end > yy_buffer_start &&
		    '\n' == yy_buffer[yy_buffer_end-1])
			yy_buffer_end--;
		if (yy_buffer_end > yy_buffer_start &&
		    '\r' == yy_buffer[yy_buffer_end-1])
			yy_buffer_end--;
	}
	private boolean yy_last_was_cr=false;
	private void yy_mark_start () {
		int i;
		for (i = yy_buffer_start; i < yy_buffer_index; ++i) {
			if ('\n' == yy_buffer[i] && !yy_last_was_cr) {
				++yyline;
			}
			if ('\r' == yy_buffer[i]) {
				++yyline;
				yy_last_was_cr=true;
			} else yy_last_was_cr=false;
		}
		yy_buffer_start = yy_buffer_index;
	}
	private void yy_mark_end () {
		yy_buffer_end = yy_buffer_index;
	}
	private void yy_to_mark () {
		yy_buffer_index = yy_buffer_end;
		yy_at_bol = (yy_buffer_end > yy_buffer_start) &&
		            ('\r' == yy_buffer[yy_buffer_end-1] ||
		             '\n' == yy_buffer[yy_buffer_end-1] ||
		             2028/*LS*/ == yy_buffer[yy_buffer_end-1] ||
		             2029/*PS*/ == yy_buffer[yy_buffer_end-1]);
	}
	private java.lang.String yytext () {
		return (new java.lang.String(yy_buffer,
			yy_buffer_start,
			yy_buffer_end - yy_buffer_start));
	}
	private int yylength () {
		return yy_buffer_end - yy_buffer_start;
	}
	private char[] yy_double (char buf[]) {
		int i;
		char newbuf[];
		newbuf = new char[2*buf.length];
		for (i = 0; i < buf.length; ++i) {
			newbuf[i] = buf[i];
		}
		return newbuf;
	}
	private final int YY_E_INTERNAL = 0;
	private final int YY_E_MATCH = 1;
	private java.lang.String yy_error_string[] = {
		"Error: Internal error.\n",
		"Error: Unmatched input.\n"
	};
	private void yy_error (int code,boolean fatal) {
		java.lang.System.out.print(yy_error_string[code]);
		java.lang.System.out.flush();
		if (fatal) {
			throw new Error("Fatal Error.\n");
		}
	}
	private int[][] unpackFromString(int size1, int size2, String st) {
		int colonIndex = -1;
		String lengthString;
		int sequenceLength = 0;
		int sequenceInteger = 0;

		int commaIndex;
		String workString;

		int res[][] = new int[size1][size2];
		for (int i= 0; i < size1; i++) {
			for (int j= 0; j < size2; j++) {
				if (sequenceLength != 0) {
					res[i][j] = sequenceInteger;
					sequenceLength--;
					continue;
				}
				commaIndex = st.indexOf(',');
				workString = (commaIndex==-1) ? st :
					st.substring(0, commaIndex);
				st = st.substring(commaIndex+1);
				colonIndex = workString.indexOf(':');
				if (colonIndex == -1) {
					res[i][j]=Integer.parseInt(workString);
					continue;
				}
				lengthString =
					workString.substring(colonIndex+1);
				sequenceLength=Integer.parseInt(lengthString);
				workString=workString.substring(0,colonIndex);
				sequenceInteger=Integer.parseInt(workString);
				res[i][j] = sequenceInteger;
				sequenceLength--;
			}
		}
		return res;
	}
	private int yy_acpt[] = {
		/* 0 */ YY_NOT_ACCEPT,
		/* 1 */ YY_NO_ANCHOR,
		/* 2 */ YY_NO_ANCHOR,
		/* 3 */ YY_NO_ANCHOR,
		/* 4 */ YY_NO_ANCHOR,
		/* 5 */ YY_NO_ANCHOR,
		/* 6 */ YY_NO_ANCHOR,
		/* 7 */ YY_NO_ANCHOR,
		/* 8 */ YY_NO_ANCHOR,
		/* 9 */ YY_NO_ANCHOR,
		/* 10 */ YY_NO_ANCHOR,
		/* 11 */ YY_NO_ANCHOR,
		/* 12 */ YY_NO_ANCHOR,
		/* 13 */ YY_NO_ANCHOR,
		/* 14 */ YY_NO_ANCHOR,
		/* 15 */ YY_NO_ANCHOR,
		/* 16 */ YY_NO_ANCHOR,
		/* 17 */ YY_NO_ANCHOR,
		/* 18 */ YY_NO_ANCHOR,
		/* 19 */ YY_NO_ANCHOR,
		/* 20 */ YY_NO_ANCHOR,
		/* 21 */ YY_NO_ANCHOR,
		/* 22 */ YY_NO_ANCHOR,
		/* 23 */ YY_NO_ANCHOR,
		/* 24 */ YY_NO_ANCHOR,
		/* 25 */ YY_NO_ANCHOR,
		/* 26 */ YY_NO_ANCHOR,
		/* 27 */ YY_NO_ANCHOR,
		/* 28 */ YY_NO_ANCHOR,
		/* 29 */ YY_NO_ANCHOR,
		/* 30 */ YY_NO_ANCHOR,
		/* 31 */ YY_NO_ANCHOR,
		/* 32 */ YY_NO_ANCHOR,
		/* 33 */ YY_NO_ANCHOR,
		/* 34 */ YY_NO_ANCHOR,
		/* 35 */ YY_NO_ANCHOR,
		/* 36 */ YY_NO_ANCHOR,
		/* 37 */ YY_NO_ANCHOR,
		/* 38 */ YY_NO_ANCHOR,
		/* 39 */ YY_NO_ANCHOR,
		/* 40 */ YY_NO_ANCHOR,
		/* 41 */ YY_NO_ANCHOR,
		/* 42 */ YY_NO_ANCHOR,
		/* 43 */ YY_NO_ANCHOR,
		/* 44 */ YY_NO_ANCHOR,
		/* 45 */ YY_NO_ANCHOR,
		/* 46 */ YY_NO_ANCHOR,
		/* 47 */ YY_NO_ANCHOR,
		/* 48 */ YY_NO_ANCHOR,
		/* 49 */ YY_NOT_ACCEPT,
		/* 50 */ YY_NO_ANCHOR,
		/* 51 */ YY_NO_ANCHOR,
		/* 52 */ YY_NO_ANCHOR,
		/* 53 */ YY_NO_ANCHOR,
		/* 54 */ YY_NOT_ACCEPT,
		/* 55 */ YY_NO_ANCHOR,
		/* 56 */ YY_NO_ANCHOR,
		/* 57 */ YY_NO_ANCHOR,
		/* 58 */ YY_NO_ANCHOR,
		/* 59 */ YY_NO_ANCHOR,
		/* 60 */ YY_NO_ANCHOR,
		/* 61 */ YY_NO_ANCHOR,
		/* 62 */ YY_NO_ANCHOR,
		/* 63 */ YY_NO_ANCHOR,
		/* 64 */ YY_NO_ANCHOR,
		/* 65 */ YY_NO_ANCHOR,
		/* 66 */ YY_NO_ANCHOR,
		/* 67 */ YY_NO_ANCHOR,
		/* 68 */ YY_NO_ANCHOR,
		/* 69 */ YY_NO_ANCHOR,
		/* 70 */ YY_NO_ANCHOR,
		/* 71 */ YY_NO_ANCHOR,
		/* 72 */ YY_NO_ANCHOR,
		/* 73 */ YY_NO_ANCHOR,
		/* 74 */ YY_NO_ANCHOR,
		/* 75 */ YY_NO_ANCHOR,
		/* 76 */ YY_NO_ANCHOR,
		/* 77 */ YY_NO_ANCHOR,
		/* 78 */ YY_NO_ANCHOR,
		/* 79 */ YY_NO_ANCHOR,
		/* 80 */ YY_NO_ANCHOR,
		/* 81 */ YY_NO_ANCHOR,
		/* 82 */ YY_NO_ANCHOR,
		/* 83 */ YY_NO_ANCHOR,
		/* 84 */ YY_NO_ANCHOR,
		/* 85 */ YY_NO_ANCHOR,
		/* 86 */ YY_NO_ANCHOR,
		/* 87 */ YY_NO_ANCHOR,
		/* 88 */ YY_NO_ANCHOR,
		/* 89 */ YY_NO_ANCHOR,
		/* 90 */ YY_NO_ANCHOR,
		/* 91 */ YY_NO_ANCHOR,
		/* 92 */ YY_NO_ANCHOR,
		/* 93 */ YY_NO_ANCHOR,
		/* 94 */ YY_NO_ANCHOR
	};
	private int yy_cmap[] = unpackFromString(1,130,
"27:9,45,25,27:2,24,27:18,45,41,21,28,27:2,42,23,31,32,40,38,34,39,35,26,20:" +
"10,27,33,36,44,37,23,27,18:26,27,22,27:2,19,27,13,1,15,8,11,12,18,17,4,18:2" +
",3,18,5,2,18:2,9,14,6,10,7,16,18:3,29,43,30,27:2,0:2")[0];

	private int yy_rmap[] = unpackFromString(1,95,
"0,1,2,3,4,1:2,5,1:7,6,7,8,9,1,10,11,12,13,1,14,1:10,13:2,1,13:5,1,13:4,15,1" +
"6,1,17,18,19,20,21,1,22,23,24,25,26,27,28,29,30,31,32,33,34,35,13,36,37,38," +
"39,40,41,42,43,44,45,46,47,48,13,49,50,51,52,53,54,55,56")[0];

	private int yy_nxt[][] = unpackFromString(57,46,
"1,2,86:2,50,86,88,89,86,90,86,91,92,86,93,70,94,86:3,3,4,5:2,-1,6,7,5,52,8," +
"9,10,11,12,13,14,15,16,17,18,19,20,56,59,21,22,-1:47,86,71,86:16,72:2,-1:45" +
",3,-1:26,4:20,24,25,4:2,51,4:20,-1:26,49,-1:55,26,-1:7,27,-1:38,28,-1:6,29," +
"-1:39,30,-1:46,31,-1:50,32,-1:45,35,-1:46,22,-1,86:18,72:2,-1:26,53:4,4:2,5" +
"3:14,4:3,53,57,53:20,-1:26,54,-1:20,86:4,55,86:6,23,86:6,72:2,-1:53,54,-1:1" +
"8,53:20,44,60,53:2,57,53:20,-1,54:23,-1,38,54:20,-1,86:5,36,86:12,72:2,-1:6" +
"7,33,-1:4,86:4,37,86:13,72:2,-1:68,34,-1:3,53:24,57,53:20,-1,86:2,39,86:15," +
"72:2,-1:26,86:10,40,86:7,72:2,-1:26,86:7,41,86:10,72:2,-1:26,86:10,42,86:7," +
"72:2,-1:26,86:5,43,86:12,72:2,-1:26,86:10,45,86:7,72:2,-1:26,86:10,46,86:7," +
"72:2,-1:26,86:4,47,86:13,72:2,-1:26,86:5,48,86:12,72:2,-1:26,86,79,86,58,86" +
":14,72:2,-1:26,86,61,86:16,72:2,-1:26,86:9,62,86:8,72:2,-1:26,86:3,63,86:14" +
",72:2,-1:26,86:5,81,86:12,72:2,-1:26,86:13,64,86:4,72:2,-1:26,86:2,82,86:15" +
",72:2,-1:26,86:8,87,86:9,72:2,-1:26,86:9,65,86:8,72:2,-1:26,86:3,83,86:14,7" +
"2:2,-1:26,86:9,84,86:8,72:2,-1:26,86:13,66,86:4,72:2,-1:26,86:2,67,86:15,72" +
":2,-1:26,86:8,68,86:9,72:2,-1:26,86:14,69,86:3,72:2,-1:26,86:9,85,86:8,72:2" +
",-1:26,86:8,73,86:9,72:2,-1:26,86,74,86:16,72:2,-1:26,86:10,75,86:7,72:2,-1" +
":26,86:2,76,86:15,72:2,-1:26,86:12,77,86:5,72:2,-1:26,86:5,78,86:12,72:2,-1" +
":26,86:16,80,86,72:2,-1:25");

	public java_cup.runtime.Symbol next_token ()
		throws java.io.IOException {
		int yy_lookahead;
		int yy_anchor = YY_NO_ANCHOR;
		int yy_state = yy_state_dtrans[yy_lexical_state];
		int yy_next_state = YY_NO_STATE;
		int yy_last_accept_state = YY_NO_STATE;
		boolean yy_initial = true;
		int yy_this_accept;

		yy_mark_start();
		yy_this_accept = yy_acpt[yy_state];
		if (YY_NOT_ACCEPT != yy_this_accept) {
			yy_last_accept_state = yy_state;
			yy_mark_end();
		}
		while (true) {
			if (yy_initial && yy_at_bol) yy_lookahead = YY_BOL;
			else yy_lookahead = yy_advance();
			yy_next_state = YY_F;
			yy_next_state = yy_nxt[yy_rmap[yy_state]][yy_cmap[yy_lookahead]];
			if (YY_EOF == yy_lookahead && true == yy_initial) {

return new Symbol(sym.EOF);
			}
			if (YY_F != yy_next_state) {
				yy_state = yy_next_state;
				yy_initial = false;
				yy_this_accept = yy_acpt[yy_state];
				if (YY_NOT_ACCEPT != yy_this_accept) {
					yy_last_accept_state = yy_state;
					yy_mark_end();
				}
			}
			else {
				if (YY_NO_STATE == yy_last_accept_state) {
					throw (new Error("Lexical Error: Unmatched Input."));
				}
				else {
					yy_anchor = yy_acpt[yy_last_accept_state];
					if (0 != (YY_END & yy_anchor)) {
						yy_move_end();
					}
					yy_to_mark();
					switch (yy_last_accept_state) {
					case 1:
						
					case -2:
						break;
					case 2:
						{ 
    Symbol s = new Symbol(sym.ID, new IdTokenVal(yyline+1, CharNum.num, yytext()));
    CharNum.num += yytext().length();
    return s;
}
					case -3:
						break;
					case 3:
						{ 
    // NOTE: the following computation of the integer value does NOT
    //       check for overflow.  This must be modified.
    int val;
    try {
        val = Integer.parseInt(yytext());
    } catch(NumberFormatException e) {
        // https://stackoverflow.com/q/9451066
        // NumberFormatException can be thrown by Java for reasons other 
        // than overflow. However, since {DIGIT}+ must match a series of
        // digits, the only possiblity is overflow, so we are fine. 
        String msg = "integer literal too large; using max value";
        ErrMsg.warn(yyline+1, CharNum.num, msg);
        val = Integer.MAX_VALUE;
    }
    Symbol s = new Symbol(sym.INTLITERAL, new IntLitTokenVal(yyline+1, CharNum.num, val));
    CharNum.num += yytext().length();
    return s;
}
					case -4:
						break;
					case 4:
						{
    String msg = "unterminated string literal ignored";
    ErrMsg.fatal(yyline+1, CharNum.num, msg);
    CharNum.num = 1; 
}
					case -5:
						break;
					case 5:
						{ 
    String msg = "ignoring illegal character: " + yytext();
    ErrMsg.fatal(yyline+1, CharNum.num, msg);
    CharNum.num++;
}
					case -6:
						break;
					case 6:
						{ CharNum.num = 1; }
					case -7:
						break;
					case 7:
						{ return Utility.parseBasic(sym.DIVIDE, yyline+1, yytext()); }
					case -8:
						break;
					case 8:
						{ return Utility.parseBasic(sym.LCURLY, yyline+1, yytext()); }
					case -9:
						break;
					case 9:
						{ return Utility.parseBasic(sym.RCURLY, yyline+1, yytext()); }
					case -10:
						break;
					case 10:
						{ return Utility.parseBasic(sym.LPAREN, yyline+1, yytext()); }
					case -11:
						break;
					case 11:
						{ return Utility.parseBasic(sym.RPAREN, yyline+1, yytext()); }
					case -12:
						break;
					case 12:
						{ return Utility.parseBasic(sym.SEMICOLON, yyline+1, yytext()); }
					case -13:
						break;
					case 13:
						{ return Utility.parseBasic(sym.COMMA, yyline+1, yytext()); }
					case -14:
						break;
					case 14:
						{ return Utility.parseBasic(sym.DOT, yyline+1, yytext()); }
					case -15:
						break;
					case 15:
						{ return Utility.parseBasic(sym.LESS, yyline+1, yytext()); }
					case -16:
						break;
					case 16:
						{ return Utility.parseBasic(sym.GREATER, yyline+1, yytext()); }
					case -17:
						break;
					case 17:
						{ return Utility.parseBasic(sym.PLUS, yyline+1, yytext()); }
					case -18:
						break;
					case 18:
						{ return Utility.parseBasic(sym.MINUS, yyline+1, yytext()); }
					case -19:
						break;
					case 19:
						{ return Utility.parseBasic(sym.TIMES, yyline+1, yytext()); }
					case -20:
						break;
					case 20:
						{ return Utility.parseBasic(sym.NOT, yyline+1, yytext()); }
					case -21:
						break;
					case 21:
						{ return Utility.parseBasic(sym.ASSIGN, yyline+1, yytext()); }
					case -22:
						break;
					case 22:
						{ CharNum.num += yytext().length(); }
					case -23:
						break;
					case 23:
						{ return Utility.parseBasic(sym.IF, yyline+1, yytext()); }
					case -24:
						break;
					case 24:
						{ 
    Symbol s = new Symbol(sym.STRINGLITERAL, new StrLitTokenVal(yyline+1, CharNum.num, yytext()));
    CharNum.num += yytext().length();
    return s; 
}
					case -25:
						break;
					case 25:
						{
    String msg = "unterminated string literal with bad escaped character ignored";
    ErrMsg.fatal(yyline+1, CharNum.num, msg);
    CharNum.num = 1; 
}
					case -26:
						break;
					case 26:
						{ return Utility.parseBasic(sym.READ, yyline+1, yytext()); }
					case -27:
						break;
					case 27:
						{ return Utility.parseBasic(sym.LESSEQ, yyline+1, yytext()); }
					case -28:
						break;
					case 28:
						{ return Utility.parseBasic(sym.WRITE, yyline+1, yytext()); }
					case -29:
						break;
					case 29:
						{ return Utility.parseBasic(sym.GREATEREQ, yyline+1, yytext()); }
					case -30:
						break;
					case 30:
						{ return Utility.parseBasic(sym.PLUSPLUS, yyline+1, yytext()); }
					case -31:
						break;
					case 31:
						{ return Utility.parseBasic(sym.MINUSMINUS, yyline+1, yytext()); }
					case -32:
						break;
					case 32:
						{ return Utility.parseBasic(sym.NOTEQUALS, yyline+1, yytext()); }
					case -33:
						break;
					case 33:
						{ return Utility.parseBasic(sym.AND, yyline+1, yytext()); }
					case -34:
						break;
					case 34:
						{ return Utility.parseBasic(sym.OR, yyline+1, yytext()); }
					case -35:
						break;
					case 35:
						{ return Utility.parseBasic(sym.EQUALS, yyline+1, yytext()); }
					case -36:
						break;
					case 36:
						{ return Utility.parseBasic(sym.INT, yyline+1, yytext()); }
					case -37:
						break;
					case 37:
						{ return Utility.parseBasic(sym.CIN, yyline+1, yytext()); }
					case -38:
						break;
					case 38:
						{ CharNum.num = 1; }
					case -39:
						break;
					case 39:
						{ return Utility.parseBasic(sym.BOOL, yyline+1, yytext()); }
					case -40:
						break;
					case 40:
						{ return Utility.parseBasic(sym.TRUE, yyline+1, yytext()); }
					case -41:
						break;
					case 41:
						{ return Utility.parseBasic(sym.VOID, yyline+1, yytext()); }
					case -42:
						break;
					case 42:
						{ return Utility.parseBasic(sym.ELSE, yyline+1, yytext()); }
					case -43:
						break;
					case 43:
						{ return Utility.parseBasic(sym.COUT, yyline+1, yytext()); }
					case -44:
						break;
					case 44:
						{ 
    String msg = "string literal with bad escaped character ignored";
    ErrMsg.fatal(yyline+1, CharNum.num, msg);
    CharNum.num++; 
}
					case -45:
						break;
					case 45:
						{ return Utility.parseBasic(sym.FALSE, yyline+1, yytext()); }
					case -46:
						break;
					case 46:
						{ return Utility.parseBasic(sym.WHILE, yyline+1, yytext()); }
					case -47:
						break;
					case 47:
						{ return Utility.parseBasic(sym.RETURN, yyline+1, yytext()); }
					case -48:
						break;
					case 48:
						{ return Utility.parseBasic(sym.STRUCT, yyline+1, yytext()); }
					case -49:
						break;
					case 50:
						{ 
    Symbol s = new Symbol(sym.ID, new IdTokenVal(yyline+1, CharNum.num, yytext()));
    CharNum.num += yytext().length();
    return s;
}
					case -50:
						break;
					case 51:
						{
    String msg = "unterminated string literal ignored";
    ErrMsg.fatal(yyline+1, CharNum.num, msg);
    CharNum.num = 1; 
}
					case -51:
						break;
					case 52:
						{ 
    String msg = "ignoring illegal character: " + yytext();
    ErrMsg.fatal(yyline+1, CharNum.num, msg);
    CharNum.num++;
}
					case -52:
						break;
					case 53:
						{
    String msg = "unterminated string literal with bad escaped character ignored";
    ErrMsg.fatal(yyline+1, CharNum.num, msg);
    CharNum.num = 1; 
}
					case -53:
						break;
					case 55:
						{ 
    Symbol s = new Symbol(sym.ID, new IdTokenVal(yyline+1, CharNum.num, yytext()));
    CharNum.num += yytext().length();
    return s;
}
					case -54:
						break;
					case 56:
						{ 
    String msg = "ignoring illegal character: " + yytext();
    ErrMsg.fatal(yyline+1, CharNum.num, msg);
    CharNum.num++;
}
					case -55:
						break;
					case 57:
						{
    String msg = "unterminated string literal with bad escaped character ignored";
    ErrMsg.fatal(yyline+1, CharNum.num, msg);
    CharNum.num = 1; 
}
					case -56:
						break;
					case 58:
						{ 
    Symbol s = new Symbol(sym.ID, new IdTokenVal(yyline+1, CharNum.num, yytext()));
    CharNum.num += yytext().length();
    return s;
}
					case -57:
						break;
					case 59:
						{ 
    String msg = "ignoring illegal character: " + yytext();
    ErrMsg.fatal(yyline+1, CharNum.num, msg);
    CharNum.num++;
}
					case -58:
						break;
					case 60:
						{
    String msg = "unterminated string literal with bad escaped character ignored";
    ErrMsg.fatal(yyline+1, CharNum.num, msg);
    CharNum.num = 1; 
}
					case -59:
						break;
					case 61:
						{ 
    Symbol s = new Symbol(sym.ID, new IdTokenVal(yyline+1, CharNum.num, yytext()));
    CharNum.num += yytext().length();
    return s;
}
					case -60:
						break;
					case 62:
						{ 
    Symbol s = new Symbol(sym.ID, new IdTokenVal(yyline+1, CharNum.num, yytext()));
    CharNum.num += yytext().length();
    return s;
}
					case -61:
						break;
					case 63:
						{ 
    Symbol s = new Symbol(sym.ID, new IdTokenVal(yyline+1, CharNum.num, yytext()));
    CharNum.num += yytext().length();
    return s;
}
					case -62:
						break;
					case 64:
						{ 
    Symbol s = new Symbol(sym.ID, new IdTokenVal(yyline+1, CharNum.num, yytext()));
    CharNum.num += yytext().length();
    return s;
}
					case -63:
						break;
					case 65:
						{ 
    Symbol s = new Symbol(sym.ID, new IdTokenVal(yyline+1, CharNum.num, yytext()));
    CharNum.num += yytext().length();
    return s;
}
					case -64:
						break;
					case 66:
						{ 
    Symbol s = new Symbol(sym.ID, new IdTokenVal(yyline+1, CharNum.num, yytext()));
    CharNum.num += yytext().length();
    return s;
}
					case -65:
						break;
					case 67:
						{ 
    Symbol s = new Symbol(sym.ID, new IdTokenVal(yyline+1, CharNum.num, yytext()));
    CharNum.num += yytext().length();
    return s;
}
					case -66:
						break;
					case 68:
						{ 
    Symbol s = new Symbol(sym.ID, new IdTokenVal(yyline+1, CharNum.num, yytext()));
    CharNum.num += yytext().length();
    return s;
}
					case -67:
						break;
					case 69:
						{ 
    Symbol s = new Symbol(sym.ID, new IdTokenVal(yyline+1, CharNum.num, yytext()));
    CharNum.num += yytext().length();
    return s;
}
					case -68:
						break;
					case 70:
						{ 
    Symbol s = new Symbol(sym.ID, new IdTokenVal(yyline+1, CharNum.num, yytext()));
    CharNum.num += yytext().length();
    return s;
}
					case -69:
						break;
					case 71:
						{ 
    Symbol s = new Symbol(sym.ID, new IdTokenVal(yyline+1, CharNum.num, yytext()));
    CharNum.num += yytext().length();
    return s;
}
					case -70:
						break;
					case 72:
						{ 
    Symbol s = new Symbol(sym.ID, new IdTokenVal(yyline+1, CharNum.num, yytext()));
    CharNum.num += yytext().length();
    return s;
}
					case -71:
						break;
					case 73:
						{ 
    Symbol s = new Symbol(sym.ID, new IdTokenVal(yyline+1, CharNum.num, yytext()));
    CharNum.num += yytext().length();
    return s;
}
					case -72:
						break;
					case 74:
						{ 
    Symbol s = new Symbol(sym.ID, new IdTokenVal(yyline+1, CharNum.num, yytext()));
    CharNum.num += yytext().length();
    return s;
}
					case -73:
						break;
					case 75:
						{ 
    Symbol s = new Symbol(sym.ID, new IdTokenVal(yyline+1, CharNum.num, yytext()));
    CharNum.num += yytext().length();
    return s;
}
					case -74:
						break;
					case 76:
						{ 
    Symbol s = new Symbol(sym.ID, new IdTokenVal(yyline+1, CharNum.num, yytext()));
    CharNum.num += yytext().length();
    return s;
}
					case -75:
						break;
					case 77:
						{ 
    Symbol s = new Symbol(sym.ID, new IdTokenVal(yyline+1, CharNum.num, yytext()));
    CharNum.num += yytext().length();
    return s;
}
					case -76:
						break;
					case 78:
						{ 
    Symbol s = new Symbol(sym.ID, new IdTokenVal(yyline+1, CharNum.num, yytext()));
    CharNum.num += yytext().length();
    return s;
}
					case -77:
						break;
					case 79:
						{ 
    Symbol s = new Symbol(sym.ID, new IdTokenVal(yyline+1, CharNum.num, yytext()));
    CharNum.num += yytext().length();
    return s;
}
					case -78:
						break;
					case 80:
						{ 
    Symbol s = new Symbol(sym.ID, new IdTokenVal(yyline+1, CharNum.num, yytext()));
    CharNum.num += yytext().length();
    return s;
}
					case -79:
						break;
					case 81:
						{ 
    Symbol s = new Symbol(sym.ID, new IdTokenVal(yyline+1, CharNum.num, yytext()));
    CharNum.num += yytext().length();
    return s;
}
					case -80:
						break;
					case 82:
						{ 
    Symbol s = new Symbol(sym.ID, new IdTokenVal(yyline+1, CharNum.num, yytext()));
    CharNum.num += yytext().length();
    return s;
}
					case -81:
						break;
					case 83:
						{ 
    Symbol s = new Symbol(sym.ID, new IdTokenVal(yyline+1, CharNum.num, yytext()));
    CharNum.num += yytext().length();
    return s;
}
					case -82:
						break;
					case 84:
						{ 
    Symbol s = new Symbol(sym.ID, new IdTokenVal(yyline+1, CharNum.num, yytext()));
    CharNum.num += yytext().length();
    return s;
}
					case -83:
						break;
					case 85:
						{ 
    Symbol s = new Symbol(sym.ID, new IdTokenVal(yyline+1, CharNum.num, yytext()));
    CharNum.num += yytext().length();
    return s;
}
					case -84:
						break;
					case 86:
						{ 
    Symbol s = new Symbol(sym.ID, new IdTokenVal(yyline+1, CharNum.num, yytext()));
    CharNum.num += yytext().length();
    return s;
}
					case -85:
						break;
					case 87:
						{ 
    Symbol s = new Symbol(sym.ID, new IdTokenVal(yyline+1, CharNum.num, yytext()));
    CharNum.num += yytext().length();
    return s;
}
					case -86:
						break;
					case 88:
						{ 
    Symbol s = new Symbol(sym.ID, new IdTokenVal(yyline+1, CharNum.num, yytext()));
    CharNum.num += yytext().length();
    return s;
}
					case -87:
						break;
					case 89:
						{ 
    Symbol s = new Symbol(sym.ID, new IdTokenVal(yyline+1, CharNum.num, yytext()));
    CharNum.num += yytext().length();
    return s;
}
					case -88:
						break;
					case 90:
						{ 
    Symbol s = new Symbol(sym.ID, new IdTokenVal(yyline+1, CharNum.num, yytext()));
    CharNum.num += yytext().length();
    return s;
}
					case -89:
						break;
					case 91:
						{ 
    Symbol s = new Symbol(sym.ID, new IdTokenVal(yyline+1, CharNum.num, yytext()));
    CharNum.num += yytext().length();
    return s;
}
					case -90:
						break;
					case 92:
						{ 
    Symbol s = new Symbol(sym.ID, new IdTokenVal(yyline+1, CharNum.num, yytext()));
    CharNum.num += yytext().length();
    return s;
}
					case -91:
						break;
					case 93:
						{ 
    Symbol s = new Symbol(sym.ID, new IdTokenVal(yyline+1, CharNum.num, yytext()));
    CharNum.num += yytext().length();
    return s;
}
					case -92:
						break;
					case 94:
						{ 
    Symbol s = new Symbol(sym.ID, new IdTokenVal(yyline+1, CharNum.num, yytext()));
    CharNum.num += yytext().length();
    return s;
}
					case -93:
						break;
					default:
						yy_error(YY_E_INTERNAL,false);
					case -1:
					}
					yy_initial = true;
					yy_state = yy_state_dtrans[yy_lexical_state];
					yy_next_state = YY_NO_STATE;
					yy_last_accept_state = YY_NO_STATE;
					yy_mark_start();
					yy_this_accept = yy_acpt[yy_state];
					if (YY_NOT_ACCEPT != yy_this_accept) {
						yy_last_accept_state = yy_state;
						yy_mark_end();
					}
				}
			}
		}
	}
}
