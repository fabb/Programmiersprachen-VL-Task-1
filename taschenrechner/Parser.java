/**
 * @author Bernhard Urban, Fabian Ehrentraud
 */

package taschenrechner;

/**
 * Returncodes for the parser
 */
enum ParserCode {
	QUIT, RUN;
}

/**
 * The parser gets tokens from the scanner and adds the corresponding operations
 * to the inputlist of the calculator
 */
public class Parser {
	private Scanner scanner;
	private InputList inputlist;
	private Stack stack;

	public Parser(Scanner s, Stack st, InputList il) {
		this.scanner = s;
		this.stack = st;
		this.inputlist = il;
	}

	/**
	 * @return next scanned token
	 */
	private Token next() throws ScannerException {
		return scanner.scan();
	}

	/**
	 * This method tries to parse the given input (provided by the scanner) and
	 * adds the elements to the inputlist
	 *
	 * @return QUIT on `q' or RUN on everything else
	 */
	public ParserCode parse() throws ScannerException, ParserException {
		InputList localil = new InputList(10);
		Token ch;
		ParserCode ret = ParserCode.RUN;
		do {
			ch = this.next();
			switch(ch) {
				case S_lbr: unit(localil); break;
				case S_rbr: throw new ParserException("Syntax Error: ]");

				case S_num:
					localil.add(new Int(ch.getIVal(), this.stack)); break;

				case S_add: localil.add(new Addition(this.stack, ch)); break;
				case S_sub: localil.add(new Subtraction(this.stack, ch)); break;
				case S_mul: localil.add(new Multiplication(this.stack, ch)); break;
				case S_div: localil.add(new Division(this.stack, ch)); break;
				case S_mod: localil.add(new Modulus(this.stack, ch)); break;

				case S_and: localil.add(new And(this.stack, ch)); break;
				case S_or : localil.add(new Or(this.stack, ch)); break;
				case S_eq : localil.add(new Equal(this.stack, ch)); break;
				case S_lt : localil.add(new LessThan(this.stack, ch)); break;
				case S_gt : localil.add(new GreaterThan(this.stack, ch)); break;

				case S_neg: localil.add(new Negation(this.stack, ch)); break;
				case S_cpy: localil.add(new Copy(this.stack, ch)); break;
				case S_del: localil.add(new Deletion(this.stack, ch)); break;
				case S_app:
					localil.add(new Application(this.stack, this.inputlist, ch)); break;
				case S_qit: ret = ParserCode.QUIT;
					/* FALLTHROUGH */
				case S_eof: break;
				default: /* TODO: is this reachable atm? */
					throw new ParserException("unknown instruction: " +
							ch.getToken() + " (" + (int) ch.getToken() + ")");
			}
		} while (ch != Token.S_eof && ch != Token.S_qit);

		this.inputlist.addAll(0, localil);
		return ret;
	}

	/**
	 * This method handles unit's separately, since they could be nested.<br/>
	 * <b>Note</b>: An unit is already scanned here, so we could (theoretically)
	 * skip the scan-pass in the application operator and store the tokenlist
	 * somewhere at this point. However, for simplification reasons, we just do
	 * the scan-pass twice (once here, and once in the application operator).
	 *
	 * @param localil temporary inputlist, which'll be added in parse() to the
	 * actual inputlist
	 */
	private void unit(InputList localil) throws ScannerException, ParserException {
		StringBuilder unit = new StringBuilder("");
		Token ch = this.next();
		int i = 0;
		while (ch != Token.S_rbr || i > 0) {
			if (ch == Token.S_eof)
				throw new ParserException("missing ']'. probably malformed expression?");
			else if (ch == Token.S_lbr)
				i++;
			else if (ch == Token.S_rbr)
				i--;
			if (ch == Token.S_num) {
				unit.append(ch.getIVal()).append(" ");
			} else {
				unit.append(ch.getToken()).append(" ");
			}
			ch = this.next();
		}
		localil.add(new Unit(unit + "", this.stack));
	}
}
