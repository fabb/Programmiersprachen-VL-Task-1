package taschenrechner;

enum ParserCode {
	QUIT, RUN;
}

public class Parser {
	private Scanner scanner;
	private InputList inputlist;
	private Stack stack;

	public Parser(Scanner s, Stack st, InputList il) {
		this.scanner = s;
		this.stack = st;
		this.inputlist = il;
	}

	private Token next() throws ScannerException {
		return scanner.scan();
	}

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
}