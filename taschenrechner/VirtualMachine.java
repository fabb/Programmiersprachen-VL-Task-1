package taschenrechner;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import static java.lang.Math.max;

import jline.*;

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
		int size = this.size();
		for (Type i : this.subList(max(0, size - 4), size)) {
			t.append(i).append(" ");
		}
		if (size > 4) {
			return ConsoleColor.Cyan.color("{" + (size - 4) + "}") + " " + t;
		} else {
			return t + "";
		}
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