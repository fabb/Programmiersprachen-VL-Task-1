/**
 * @author Bernhard Urban, Fabian Ehrentraud
 */

package taschenrechner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;

/**
 * Define relevant tokens and provide methods in order to operate on them.
 */
enum Token {
	S_num, S_eof,
	S_lbr('['), S_rbr(']'),
	S_add('+'), S_sub('-'), S_mul('*'), S_div('/'), S_mod('%'),
	S_and('&'), S_or ('|'), S_eq ('='),
	S_lt ('<'), S_gt ('>'), S_neg('~'),
	S_cpy('c'), S_del('d'), S_app('a'), S_qit('q');

	private char token;
	private int ival;

	/**
	 * @return the ASCII representation of the token
	 */
	public char getToken() {
		return this.token;
	}

	/**
	 * @param ival set the numeric representation of the token. only for numbers necessary
	 */
	public void setIVal(int ival) {
		this.ival = ival;
	}

	/**
	 * @return the numeric representation of the token. only for numbers necessary
	 */
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

/**
 * At first we need to scan the userinput, and generate tokens for the parser.
 * For this purpose StreamTokenizer is used.
 */
public class Scanner {
	private StreamTokenizer st;

	/**
	 * @param input try to scan this String
	 */
	public Scanner(String input) {
		BufferedReader br = new BufferedReader(new StringReader(input));
		st = new StreamTokenizer(br);

		/* for some characters, we've to disable the standard behavior of StreamTokenizer */
		st.ordinaryChar(Token.S_div.getToken());
		st.ordinaryChar(Token.S_mul.getToken());
		st.ordinaryChar(Token.S_sub.getToken());

		/* don't try to interpret a 'word' */
		st.ordinaryChar(Token.S_cpy.getToken());
		st.ordinaryChar(Token.S_del.getToken());
		st.ordinaryChar(Token.S_app.getToken());
		st.ordinaryChar(Token.S_qit.getToken());
	}

	/**
	 * This method tries to scan the given String.
	 * @return the scanned token
	 * @throws ScannerException e.g. a invalid instruction or I/O failure
	 */
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
