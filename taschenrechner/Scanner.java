package taschenrechner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;

enum Token {
	S_num, S_eof,
	S_lbr('['), S_rbr(']'),
	S_add('+'), S_sub('-'), S_mul('*'), S_div('/'), S_mod('%'),
	S_and('&'), S_or ('|'), S_eq ('='),
	S_lt ('<'), S_gt ('>'), S_neg('~'),
	S_cpy('c'), S_del('d'), S_app('a'), S_qit('q');

	private char token;
	private int ival;

	public char getToken() {
		return this.token;
	}
	public void setIVal(int ival) {
		this.ival = ival;
	}
	public int getIVal() {
		return this.ival;
	}
	private Token() {
		this.token = '_'; // dummy
	}
	private Token(char token) {
		this.token = token;
	}
}

public class Scanner {
	private StreamTokenizer st;

	public Scanner(String input) {
		BufferedReader br = new BufferedReader(new StringReader(input));
		st = new StreamTokenizer(br);
		st.ordinaryChar('/');
		st.ordinaryChar('*');
		st.ordinaryChar('-');

		/* don't try to interpret a 'word' */
		st.ordinaryChar(Token.S_cpy.getToken());
		st.ordinaryChar(Token.S_del.getToken());
		st.ordinaryChar(Token.S_app.getToken());
		st.ordinaryChar(Token.S_qit.getToken());
	}

	public Token scan() throws ScannerException {
		try {
			switch (st.nextToken()) {
				case StreamTokenizer.TT_WORD:
					throw new ScannerException("invalid Instruction/Token: \"" + st.sval + "\"");
				case StreamTokenizer.TT_EOL:
					throw new ScannerException("what have you done?");
				case StreamTokenizer.TT_EOF:
					return Token.S_eof;
				case StreamTokenizer.TT_NUMBER:
					Token t = Token.S_num;
					if (st.nval > Integer.MAX_VALUE || st.nval < Integer.MIN_VALUE) {
						throw new ScannerException("value too small/big: \"" + st.sval + "\"");
					}
					t.setIVal((int) st.nval);
					return t;
				default:
					for (Token i : Token.values()) {
						if (i.getToken() == (char) st.ttype) {
							return i;
						}
					}
					return Token.S_eof; // TODO: better failure value?
			}
		} catch (IOException e) {
			throw new ScannerException("<io-failure>: " + e.getMessage());
		}
	}
}