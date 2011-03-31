import java.util.*;
import java.io.*;

class TRScanner {
	public final static int S_num = 1; // num
	public final static int S_eof = 2; // EOF
	public final static int S_lbr = '[';
	public final static int S_rbr = ']';
	public final static int S_add = '+';
	public final static int S_sub = '-';
	public final static int S_mul = '*';
	public final static int S_div = '/';
	public final static int S_mod = '%';
	public final static int S_and = '&';
	public final static int S_or  = '|';
	public final static int S_eq  = '=';
	public final static int S_lt  = '<';
	public final static int S_gt  = '>';

	public final static int S_neg = '~';
	public final static int S_cpy = 'c';
	public final static int S_del = 'd';
	public final static int S_app = 'a';
	public final static int S_qit = 'q';

	/* don't fear the OOP zombies */
	public int ival;
	private StreamTokenizer st;

	public TRScanner(String input) {
		BufferedReader br = new BufferedReader(new StringReader(input));
		st = new StreamTokenizer(br);
		st.ordinaryChar('/');
		st.ordinaryChar('*');
		st.ordinaryChar('-');

		/* don't try to interpret a 'word' */
		st.ordinaryChar(S_cpy);
		st.ordinaryChar(S_del);
		st.ordinaryChar(S_app);
		st.ordinaryChar(S_qit);
	}

	public int scan() throws TRScannerException {
		try {
			switch (st.nextToken()) {
				case StreamTokenizer.TT_WORD:
					throw new TRScannerException("Invalid Instruction/Token: " + st.sval);
				case StreamTokenizer.TT_EOL:
					throw new TRScannerException("What have you done?");
				case StreamTokenizer.TT_EOF:
					return S_eof;
				case StreamTokenizer.TT_NUMBER:
					this.ival = (int) st.nval;
					return S_num;
				default:
					return (char) st.ttype;
			}
		} catch (IOException e) {
			System.err.println("scanner: " + e.getMessage());
			return S_eof;
		}
	}
}

class TRParser {
	private TRScanner scanner;
	private TRArrayList<TRTypes> inputlist;
	private TRStack<TRTypes> stack;
	private char ch = 0;
	private int pos = 0;

	public TRParser(TRScanner s, TRStack<TRTypes> st, TRArrayList<TRTypes> il) {
		this.scanner = s;
		this.stack = st;
		this.inputlist = il;
	}

	private void next() throws TRScannerException {
		this.ch = (char) scanner.scan();
	}

	private void unit() throws TRScannerException, TRParserException {
		StringBuilder unit = new StringBuilder("");
		int i = 0;
		next();
		while (this.ch != TRScanner.S_rbr || i > 0) {
			if (this.ch == TRScanner.S_eof)
				throw new TRParserException("missing ']'. probably malformed expression?");
			else if (this.ch == TRScanner.S_lbr)
				i++;
			else if (this.ch == TRScanner.S_rbr)
				i--;
			if (this.ch == TRScanner.S_num) {
				unit.append(scanner.ival).append(" ");
			} else {
				unit.append(this.ch).append(" ");
			}
			next();
		}
		inputlist.add(pos, new TRUnit(unit + "", this.stack));
	}

	public int parse() throws TRScannerException, TRParserException {
		do {
			next();
			switch(this.ch) {
				case TRScanner.S_lbr: unit(); break;
				case TRScanner.S_rbr: throw new TRParserException("Syntax Error: ]");

				case TRScanner.S_num:
					inputlist.add(pos, new TRInteger(scanner.ival, this.stack)); break;

				case TRScanner.S_add: inputlist.add(pos, new TRAdd(this.stack, this.ch)); break;
				case TRScanner.S_sub: inputlist.add(pos, new TRSub(this.stack, this.ch)); break;
				case TRScanner.S_mul: inputlist.add(pos, new TRMul(this.stack, this.ch)); break;
				case TRScanner.S_div: inputlist.add(pos, new TRDiv(this.stack, this.ch)); break;
				case TRScanner.S_mod: inputlist.add(pos, new TRMod(this.stack, this.ch)); break;

				case TRScanner.S_and: inputlist.add(pos, new TRAnd(this.stack, this.ch)); break;
				case TRScanner.S_or : inputlist.add(pos, new TROr(this.stack, this.ch)); break;
				case TRScanner.S_eq : inputlist.add(pos, new TREq(this.stack, this.ch)); break;
				case TRScanner.S_lt : inputlist.add(pos, new TRLt(this.stack, this.ch)); break;
				case TRScanner.S_gt : inputlist.add(pos, new TRGt(this.stack, this.ch)); break;

				case TRScanner.S_neg: inputlist.add(pos, new TRNeg(this.stack, this.ch)); break;
				case TRScanner.S_cpy: inputlist.add(pos, new TRCpy(this.stack, this.ch)); break;
				case TRScanner.S_del: inputlist.add(pos, new TRDel(this.stack, this.ch)); break;
				case TRScanner.S_app:
					inputlist.add(pos, new TRApp(this.stack, this.inputlist, this.ch)); break;

				case TRScanner.S_eof: break;
				case TRScanner.S_qit: return 0;
				default: /* TODO: is this reachable atm? */
					throw new TRParserException("unknown instruction: " +
							this.ch + " (" + (int) this.ch + ")");
			}
			pos++;
		} while (this.ch != TRScanner.S_eof);
		return 1;
	}
}

public class TRVM {
	private TRStack<TRTypes> stack;
	private TRArrayList<TRTypes> inputlist;
	private boolean debug;

	public TRVM(TRStack<TRTypes> st, TRArrayList<TRTypes> il, boolean debug) {
		this.stack = st;
		this.inputlist = il;
		this.debug = debug;
	}

	public void start() throws TRExecuteException {
		boolean first = true;

		String redc = ((char) 27) + "[31m"; //red color
		String greenc = ((char) 27) + "[32m"; //green color
		String resetc = ((char) 27) + "[0m"; //reset color
		while (!inputlist.isEmpty()) {
			if (first || debug) {
				if (first)
					first = false;
				System.out.println(greenc + ">" + resetc + " " + this.stack +
						redc + "#" + resetc + " " + this.inputlist);
			}
			TRTypes i = inputlist.get(0);
			inputlist.remove(0);
			i.exec();
		}
		System.out.println(greenc + ">" + resetc + " " + this.stack +
				redc + "#" + resetc + " " + this.inputlist);
	}

	public static void main(String args[]) {
		String redc = ((char) 27) + "[31m"; //red color
		String cyanc = ((char) 27) + "[36m"; //cyan color
		String resetc = ((char) 27) + "[0m"; //reset color
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		BufferedReader filein = null;
		int ret = 1;
		String input = "";
		boolean debug = false;

		for (int i = 0; i < args.length; i++) {
			if ("-f".equals(args[i])) {
				try {
					filein = new BufferedReader(new FileReader(args[++i]));
				} catch (IOException e) {
					System.err.println(redc + "<main> " + e.getMessage() + resetc);
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
				System.err.println(redc + "<main> Invalid Option: " + args[i] + resetc);
				System.exit(1);
			}
		}
		do {
			if (filein == null)
				System.out.print(cyanc + "$" + resetc + " ");
			try {
				BufferedReader ta = filein == null ? br : filein;
				TRStack<TRTypes> stack = new TRStack<TRTypes>();
				TRArrayList<TRTypes> inputlist = new TRArrayList<TRTypes>(200);

				input = ta.readLine();
				if (input != null) {
					ret = new TRParser(new TRScanner(input), stack, inputlist).parse();
					new TRVM(stack, inputlist, debug).start();
				} else { // input was EOF (CTRL + D)
					ret = 0;
				}
			} catch (IOException e) {
				System.err.println(redc + "<main> " + e.getMessage() + resetc);
			} catch (TRScannerException trse) {
				System.err.println(redc + "<scanner> " + trse.getMessage() + resetc);
			} catch (TRParserException trpe) {
				System.err.println(redc + "<parser> " + trpe.getMessage() + resetc);
			} catch (TRExecuteException tree) {
				System.err.println(redc + "<vm> " + tree.getMessage() + resetc);
			}
		} while((ret > 0) && (filein == null));
		System.exit(0);
	}
}

class TRStack<E> extends Stack<E> {
	public String toString() {
		StringBuilder t = new StringBuilder("");
		for (E i : this) {
			t.append(i).append(" ");
		}
		return t + "";
	}

	/* TODO: ... wtf @ Mr. Carsten :(
	 * warum kann ich hier das nicht ohne casten machen?
	 * gebe ich den Cast weg, dann kommt folgender Fehler:
	 *
	 * > TRVM.java:246: cannot find symbol
	 * > symbol  : method getInt()
	 * > location: class java.lang.Object
	 * >         return (pop()).getInt();
	 * >                       ^
	 * > 1 error
	 *
	 * FABB, ERKLAER ES MIR!!!!!1111
	 * ich verstehe den technischen Grund dahinter nicht.
	 *
	 * falls es wir nicht hinkriegen sollten:
	 * (1) wir koennten selber einen Stack implementieren, oder
	 * (2) wir bauen einen Wrapper um Stack<TRTypes>
	 */
	public int popInt() throws TRExecuteException {
		return ((TRTypes) pop()).getInt();
	}
}

class TRArrayList<E> extends ArrayList<E> {
	public TRArrayList(int s) {
		super(s);
	}
	public String toString() {
		StringBuilder t = new StringBuilder("");
		for (E i : this) {
			t.append(i).append(" ");
		}
		return t + "";
	}
}

interface TRTypes {
	public void exec() throws TRExecuteException;

	public int getInt() throws TRExecuteException;

	public String getUnit() throws TRExecuteException;

	public boolean eq(TRTypes o) throws TRExecuteException;
}

abstract class TROperation implements TRTypes {
	protected char opc;
	protected TRStack<TRTypes> stack;

	public TROperation(TRStack<TRTypes> stack, char opc) {
		this.stack = stack;
		this.opc = opc;
	}

	protected static void checkBool(int i) throws TRExecuteException {
		if (i != 0 && i != 1)
			throw new TRExecuteException("Not a valid Boolean: \"" + i + "\"");
	}

	public String toString() {
		return "" + this.opc;
	}

	public int getInt() throws TRExecuteException {
		throw new TRExecuteException("Not an Integer or Boolean: \"" + this + "\"");
	}

	public String getUnit() throws TRExecuteException {
		throw new TRExecuteException("Not an Unit: \"" + this + "\"");
	}

	/* Comparing operations doesn't make sense in this context */
	public boolean eq(TRTypes o) throws TRExecuteException {
		throw new TRExecuteException("Can't compare Types: \"" + this + "\" and \"" + o + "\"");
	}
}

class TRAdd extends TROperation {
	public TRAdd(TRStack<TRTypes> stack, char opc) {
		super(stack, opc);
	}
	public void exec() throws TRExecuteException {
		int arg2 = this.stack.popInt();
		int arg1 = this.stack.popInt();
		new TRInteger(arg1 + arg2, this.stack).exec();
	}
}

class TRSub extends TROperation {
	public TRSub(TRStack<TRTypes> stack, char opc) {
		super(stack, opc);
	}
	public void exec() throws TRExecuteException {
		int arg2 = this.stack.popInt();
		int arg1 = this.stack.popInt();
		new TRInteger(arg1 - arg2, this.stack).exec();
	}
}

class TRMul extends TROperation {
	public TRMul(TRStack<TRTypes> stack, char opc) {
		super(stack, opc);
	}
	public void exec() throws TRExecuteException {
		int arg2 = this.stack.popInt();
		int arg1 = this.stack.popInt();
		new TRInteger(arg1 * arg2, this.stack).exec();;
	}
}

class TRDiv extends TROperation {
	public TRDiv(TRStack<TRTypes> stack, char opc) {
		super(stack, opc);
	}
	public void exec() throws TRExecuteException {
		int arg2 = this.stack.popInt();
		int arg1 = this.stack.popInt();
		new TRInteger(arg1 / arg2, this.stack).exec();;
	}
}

class TRMod extends TROperation {
	public TRMod(TRStack<TRTypes> stack, char opc) {
		super(stack, opc);
	}
	public void exec() throws TRExecuteException {
		int arg2 = this.stack.popInt();
		int arg1 = this.stack.popInt();
		new TRInteger(arg1 % arg2, this.stack).exec();
	}
}

class TRAnd extends TROperation {
	public TRAnd(TRStack<TRTypes> stack, char opc) {
		super(stack, opc);
	}
	public void exec() throws TRExecuteException {
		int arg2 = this.stack.popInt();
		int arg1 = this.stack.popInt();
		checkBool(arg2); checkBool(arg1);
		int erg = (arg1 == 0) && (arg2 == 0) ? 0 : 1;

		new TRInteger(erg, this.stack).exec();
	}
}

class TROr extends TROperation {
	public TROr(TRStack<TRTypes> stack, char opc) {
		super(stack, opc);
	}
	public void exec() throws TRExecuteException {
		int arg2 = this.stack.popInt();
		int arg1 = this.stack.popInt();
		checkBool(arg2); checkBool(arg1);
		int erg = (arg1 == 0) || (arg2 == 0) ? 0 : 1;

		new TRInteger(erg, this.stack).exec();
	}
}

class TREq extends TROperation {
	public TREq(TRStack<TRTypes> stack, char opc) {
		super(stack, opc);
	}
	public void exec() throws TRExecuteException {
		TRTypes arg2 = this.stack.pop();
		TRTypes arg1 = this.stack.pop();
		int erg = arg1.eq(arg2) ? 0 : 1;

		new TRInteger(erg, this.stack).exec();
	}
}

class TRLt extends TROperation {
	public TRLt(TRStack<TRTypes> stack, char opc) {
		super(stack, opc);
	}
	public void exec() throws TRExecuteException {
		int arg2 = this.stack.popInt();
		int arg1 = this.stack.popInt();
		int erg = arg1 < arg2 ? 0 : 1;

		new TRInteger(erg, this.stack).exec();
	}
}

class TRGt extends TROperation {
	public TRGt(TRStack<TRTypes> stack, char opc) {
		super(stack, opc);
	}
	public void exec() throws TRExecuteException {
		int arg2 = this.stack.popInt();
		int arg1 = this.stack.popInt();
		int erg = arg1 > arg2 ? 0 : 1;

		new TRInteger(erg, this.stack).exec();
	}
}

class TRNeg extends TROperation {
	public TRNeg(TRStack<TRTypes> stack, char opc) {
		super(stack, opc);
	}
	public void exec() throws TRExecuteException {
		int arg1 = this.stack.popInt();

		new TRInteger(-1 * arg1, this.stack).exec();
	}
}

class TRCpy extends TROperation {
	public TRCpy(TRStack<TRTypes> stack, char opc) {
		super(stack, opc);
	}
	public void exec() throws TRExecuteException {
		/* note that the assigment starts counting at "1" */
		int size = this.stack.indexOf(stack.lastElement());
		int arg1 = this.stack.popInt();
		int pos = size - arg1 + 1;

		this.stack.push(this.stack.get(pos));
	}
}

class TRDel extends TROperation {
	public TRDel(TRStack<TRTypes> stack, char opc) {
		super(stack, opc);
	}
	public void exec() throws TRExecuteException {
		//TODO: assert == 1?
		int size = this.stack.indexOf(this.stack.lastElement());
		int arg1 = this.stack.popInt();

		this.stack.remove(size - arg1 + 1);
	}
}

class TRApp extends TROperation {
	private TRArrayList<TRTypes> inputlist;

	public TRApp(TRStack<TRTypes> stack, TRArrayList<TRTypes> il, char opc) {
		super(stack, opc);
		this.inputlist = il;
	}
	public void exec() throws TRExecuteException {
		String arg1 = this.stack.pop().getUnit();

		try {
			new TRParser(new TRScanner(arg1), this.stack, this.inputlist).parse();
		} catch (TRScannerException trse) {
			System.err.println("<scanner> " + trse.getMessage());
		} catch (TRParserException trpe) {
			System.err.println("<parser> " + trpe.getMessage());
		}
		/* TODO: when those exceptions happens here? */
	}
}

class TRUnit implements TRTypes {
	private TRStack<TRTypes> stack = null;
	private String unit;

	public TRUnit(String str, TRStack<TRTypes> s) {
		this.unit = str;
		this.stack = s;
	}

	public String getUnit() {
		return this.unit;
	}

	public void exec() {
		this.stack.push(this);
	}

	public int getInt() throws TRExecuteException {
		throw new TRExecuteException("not a Integer or Bool: \"" + this + "\"");
	}

	public boolean eq(TRTypes o) throws TRExecuteException {
		try {
			return this.unit.equals(o.getUnit());
		} catch (TRExecuteException e) {
			throw new TRExecuteException("Can't compare Types: \"" + this + "\" and \"" + o + "\"");
		}
	}

	public String toString() {
		return '[' + this.unit + ']';
	}
}

/* extends Integer isn't allowed, since java.lang.Integer is final :( */
class TRInteger implements TRTypes {
	private TRStack<TRTypes> stack = null;
	private int i;

	public TRInteger(int i, TRStack<TRTypes> s) {
		this.i = i;
		this.stack = s;
	}

	public int getInt() {
		return this.i;
	}

	public void exec() {
		this.stack.push(this);
	}

	public String getUnit() throws TRExecuteException {
		throw new TRExecuteException("not a Unit: \"" + this + "\"");
	}

	public boolean eq(TRTypes o) throws TRExecuteException {
		try {
			return this.i == o.getInt();
		} catch (TRExecuteException e) {
			throw new TRExecuteException("Can't compare Types: \"" + this + "\" and \"" + o + "\"");
		}
	}

	public String toString() {
		return this.i + "";
	}
}

class TRScannerException extends Exception {
	public TRScannerException(String str) {
		super(str);
	}
}

class TRParserException extends Exception {
	public TRParserException(String str) {
		super(str);
	}
}

class TRExecuteException extends Exception {
	public TRExecuteException(String str) {
		super(str);
	}
}

