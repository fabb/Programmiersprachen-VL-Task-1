import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.ArrayList;

import jline.*;

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

class Scanner {
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

class Parser {
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

enum ConsoleColor {
	Red(31), Green(32),
	Cyan(36), Reset(0);

	private String consolecode;
	private ConsoleColor(int code) {
		this.consolecode = ((char) 27) + "[" + code + "m";
	}
	public String toString() {
		return this.consolecode;
	}
	public String color(String str) {
		return this.consolecode + str + ConsoleColor.Reset;
	}
}

enum ParserCode {
	QUIT, RUN;
}

public class VirtualMachine {
	private Stack stack;
	private InputList inputlist;
	private boolean debug;

	public VirtualMachine(Stack st, InputList il, boolean debug) {
		this.stack = st;
		this.inputlist = il;
		this.debug = debug;
	}

	public void start() throws ExecuteException {
		boolean first = true;

		while (!inputlist.isEmpty()) {
			if (first || debug) {
				first = false;
				System.out.println(ConsoleColor.Green.color(">") + " "
						+ this.stack + ConsoleColor.Red.color("#") + " "
						+ this.inputlist);
			}
			Type i = inputlist.get(0);
			inputlist.remove(0);
			i.exec();
		}
		System.out.println(ConsoleColor.Green.color(">") + " "
				+ this.stack + ConsoleColor.Red.color("#") + " "
				+ this.inputlist);
	}

	public static void main(String args[]) {
		ConsoleReader reader = null;
		BufferedReader filein = null;
		boolean debug = false;

		for (int i = 0; i < args.length; i++) {
			if ("-f".equals(args[i])) {
				try {
					filein = new BufferedReader(new FileReader(args[++i]));
				} catch (IOException e) {
					System.err.println(ConsoleColor.Red.color("<main> " + e.getMessage()));
					System.exit(1);
				}
			} else if ("-d".equals(args[i])) {
				debug = true;
			} else if ("-h".equals(args[i]) || "--help".equals(args[i])) {
				System.out.println("Usage: java -cp dist TRVM [-d] [-f FILE] [-h|--help]\n\n" +
						"\t-d\t\tdisplay intermediate steps of evaluation\n" +
						"\t-f FILE\t\tuse FILE as input (instead of prompt)\n" +
						"\t-h|--help\tprint this text");
				System.exit(0);
			} else {
				System.err.println(ConsoleColor.Red.color("<main> Invalid Option: " + args[i]));
				System.exit(1);
			}
		}

		String input = null;
		if (filein == null) {
			try {
				reader = new ConsoleReader();
			} catch (IOException ioe) {
				System.err.println(ConsoleColor.Red.color("<main> " + ioe.getMessage()));
				System.exit(3);
			}
		}

		ParserCode ret = ParserCode.RUN;
		do {
			try {
				Stack stack = new Stack();
				InputList inputlist = new InputList(200);

				if (filein == null) {
					input = reader.readLine(ConsoleColor.Cyan.color("$") + " ");
				} else {
					input = filein.readLine();
				}
				if (input != null) {
					ret = new Parser(new Scanner(input), stack, inputlist).parse();
					new VirtualMachine(stack, inputlist, debug).start();
				} else { // input was EOF (CTRL + D)
					ret = ParserCode.QUIT;
				}
			} catch (IOException e) {
				System.err.println(ConsoleColor.Red.color("<main> " + e.getMessage()));
			} catch (ScannerException trse) {
				System.err.println(ConsoleColor.Red.color("<scanner> " + trse.getMessage()));
			} catch (ParserException trpe) {
				System.err.println(ConsoleColor.Red.color("<parser> " + trpe.getMessage()));
			} catch (ExecuteException tree) {
				System.err.println(ConsoleColor.Red.color("<vm> " + tree.getMessage()));
			}
		} while((ret != ParserCode.QUIT) && (filein == null));
		System.exit(0);
	}
}

class Stack extends java.util.Stack<Type> {
	public String toString() {
		StringBuilder t = new StringBuilder("");
		for (Type i : this) {
			t.append(i).append(" ");
		}
		return t + "";
	}
}

class InputList extends ArrayList<Type> {
	public InputList(int s) {
		super(s);
	}
	public String toString() {
		StringBuilder t = new StringBuilder("");
		for (Type i : this) {
			t.append(i).append(" ");
		}
		return t + "";
	}
}

interface Type {
	public void exec() throws ExecuteException;

	public int getInt() throws ExecuteException;

	public String getUnit() throws ExecuteException;

	public boolean eq(Type o) throws ExecuteException;
}

abstract class Operation implements Type {
	protected Token opc;
	protected Stack stack;

	public Operation(Stack stack, Token opc) {
		this.stack = stack;
		this.opc = opc;
	}

	protected static void checkBool(int i) throws ExecuteException {
		if (i != 0 && i != 1)
			throw new ExecuteException("not a valid Boolean: \"" + i + "\"");
	}

	public String toString() {
		return "" + this.opc.getToken();
	}

	public int getInt() throws ExecuteException {
		throw new ExecuteException("not an Integer or Boolean: \"" + this + "\"");
	}

	public String getUnit() throws ExecuteException {
		throw new ExecuteException("not an Unit: \"" + this + "\"");
	}

	/* Comparing operations doesn't make sense in this context */
	public boolean eq(Type o) throws ExecuteException {
		throw new ExecuteException("can't compare Types: \"" + this + "\" and \"" + o + "\"");
	}
}

class Addition extends Operation {
	public Addition(Stack stack, Token opc) {
		super(stack, opc);
	}
	public void exec() throws ExecuteException {
		int arg2 = this.stack.pop().getInt();
		int arg1 = this.stack.pop().getInt();
		new Int(arg1 + arg2, this.stack).exec();
	}
}

class Subtraction extends Operation {
	public Subtraction(Stack stack, Token opc) {
		super(stack, opc);
	}
	public void exec() throws ExecuteException {
		int arg2 = this.stack.pop().getInt();
		int arg1 = this.stack.pop().getInt();
		new Int(arg1 - arg2, this.stack).exec();
	}
}

class Multiplication extends Operation {
	public Multiplication(Stack stack, Token opc) {
		super(stack, opc);
	}
	public void exec() throws ExecuteException {
		int arg2 = this.stack.pop().getInt();
		int arg1 = this.stack.pop().getInt();
		new Int(arg1 * arg2, this.stack).exec();;
	}
}

class Division extends Operation {
	public Division(Stack stack, Token opc) {
		super(stack, opc);
	}
	public void exec() throws ExecuteException {
		int arg2 = this.stack.pop().getInt();
		int arg1 = this.stack.pop().getInt();
		new Int(arg1 / arg2, this.stack).exec();;
	}
}

class Modulus extends Operation {
	public Modulus(Stack stack, Token opc) {
		super(stack, opc);
	}
	public void exec() throws ExecuteException {
		int arg2 = this.stack.pop().getInt();
		int arg1 = this.stack.pop().getInt();
		new Int(arg1 % arg2, this.stack).exec();
	}
}

class And extends Operation {
	public And(Stack stack, Token opc) {
		super(stack, opc);
	}
	public void exec() throws ExecuteException {
		int arg2 = this.stack.pop().getInt();
		int arg1 = this.stack.pop().getInt();
		checkBool(arg2); checkBool(arg1);
		int erg = (arg1 == 0) && (arg2 == 0) ? 0 : 1;

		new Int(erg, this.stack).exec();
	}
}

class Or extends Operation {
	public Or(Stack stack, Token opc) {
		super(stack, opc);
	}
	public void exec() throws ExecuteException {
		int arg2 = this.stack.pop().getInt();
		int arg1 = this.stack.pop().getInt();
		checkBool(arg2); checkBool(arg1);
		int erg = (arg1 == 0) || (arg2 == 0) ? 0 : 1;

		new Int(erg, this.stack).exec();
	}
}

class Equal extends Operation {
	public Equal(Stack stack, Token opc) {
		super(stack, opc);
	}
	public void exec() throws ExecuteException {
		Type arg2 = this.stack.pop();
		Type arg1 = this.stack.pop();
		int erg = arg1.eq(arg2) ? 0 : 1;

		new Int(erg, this.stack).exec();
	}
}

class LessThan extends Operation {
	public LessThan(Stack stack, Token opc) {
		super(stack, opc);
	}
	public void exec() throws ExecuteException {
		int arg2 = this.stack.pop().getInt();
		int arg1 = this.stack.pop().getInt();
		int erg = arg1 < arg2 ? 0 : 1;

		new Int(erg, this.stack).exec();
	}
}

class GreaterThan extends Operation {
	public GreaterThan(Stack stack, Token opc) {
		super(stack, opc);
	}
	public void exec() throws ExecuteException {
		int arg2 = this.stack.pop().getInt();
		int arg1 = this.stack.pop().getInt();
		int erg = arg1 > arg2 ? 0 : 1;

		new Int(erg, this.stack).exec();
	}
}

class Negation extends Operation {
	public Negation(Stack stack, Token opc) {
		super(stack, opc);
	}
	public void exec() throws ExecuteException {
		int arg1 = this.stack.pop().getInt();

		new Int(-1 * arg1, this.stack).exec();
	}
}

class Copy extends Operation {
	public Copy(Stack stack, Token opc) {
		super(stack, opc);
	}
	public void exec() throws ExecuteException {
		/* note that the assignment starts counting at "1" */
		int arg1 = this.stack.pop().getInt();
		int pos = this.stack.size() - arg1 + 1;

		try {
			this.stack.push(this.stack.get(pos));
		} catch(ArrayIndexOutOfBoundsException e) {
			throw new ExecuteException("<c>: invalid index \"" + arg1 + "\"");
		}
	}
}

class Deletion extends Operation {
	public Deletion(Stack stack, Token opc) {
		super(stack, opc);
	}
	public void exec() throws ExecuteException {
		//TODO: assert == 1?
		int arg1 = this.stack.pop().getInt();

		try {
			this.stack.remove(this.stack.size() - arg1 + 1);
		} catch(ArrayIndexOutOfBoundsException e) {
			throw new ExecuteException("<d>: invalid index \"" + arg1 + "\"");
		}
	}
}

class Application extends Operation {
	private InputList inputlist;

	public Application(Stack stack, InputList il, Token opc) {
		super(stack, opc);
		this.inputlist = il;
	}
	public void exec() throws ExecuteException {
		String arg1 = this.stack.pop().getUnit();

		try {
			new Parser(new Scanner(arg1), this.stack, this.inputlist).parse();
		} catch (ScannerException trse) {
			throw new ExecuteException("<app-scanner> " + trse.getMessage());
		} catch (ParserException trpe) {
			throw new ExecuteException("<app-parser> " + trpe.getMessage());
		}
	}
}

class Unit implements Type {
	private Stack stack = null;
	private String unit;

	public Unit(String str, Stack s) {
		this.unit = str;
		this.stack = s;
	}

	public String getUnit() {
		return this.unit;
	}

	public void exec() {
		this.stack.push(this);
	}

	public int getInt() throws ExecuteException {
		throw new ExecuteException("not a Integer or Bool: \"" + this + "\"");
	}

	public boolean eq(Type o) throws ExecuteException {
		try {
			return this.unit.equals(o.getUnit());
		} catch (ExecuteException e) {
			throw new ExecuteException("can't compare Types: \"" + this + "\" and \"" + o + "\"");
		}
	}

	public String toString() {
		return '[' + this.unit + ']';
	}
}

/* extends Integer isn't allowed, since java.lang.Integer is final :( */
class Int implements Type {
	private Stack stack = null;
	private int i;

	public Int(int i, Stack s) {
		this.i = i;
		this.stack = s;
	}

	public int getInt() {
		return this.i;
	}

	public void exec() {
		this.stack.push(this);
	}

	public String getUnit() throws ExecuteException {
		throw new ExecuteException("not a Unit: \"" + this + "\"");
	}

	public boolean eq(Type o) throws ExecuteException {
		try {
			return this.i == o.getInt();
		} catch (ExecuteException e) {
			throw new ExecuteException("can't compare Types: \"" + this + "\" and \"" + o + "\"");
		}
	}

	public String toString() {
		return this.i + "";
	}
}

class ScannerException extends Exception {
	public ScannerException(String str) {
		super(str);
	}
}

class ParserException extends Exception {
	public ParserException(String str) {
		super(str);
	}
}

class ExecuteException extends Exception {
	public ExecuteException(String str) {
		super(str);
	}
}
