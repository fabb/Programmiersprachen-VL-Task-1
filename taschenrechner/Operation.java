package taschenrechner;

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
		int arg2 = this.stack.safepop().getInt();
		int arg1 = this.stack.safepop().getInt();
		new Int(arg1 + arg2, this.stack).exec();
	}
}

class Subtraction extends Operation {
	public Subtraction(Stack stack, Token opc) {
		super(stack, opc);
	}
	public void exec() throws ExecuteException {
		int arg2 = this.stack.safepop().getInt();
		int arg1 = this.stack.safepop().getInt();
		new Int(arg1 - arg2, this.stack).exec();
	}
}

class Multiplication extends Operation {
	public Multiplication(Stack stack, Token opc) {
		super(stack, opc);
	}
	public void exec() throws ExecuteException {
		int arg2 = this.stack.safepop().getInt();
		int arg1 = this.stack.safepop().getInt();
		new Int(arg1 * arg2, this.stack).exec();;
	}
}

class Division extends Operation {
	public Division(Stack stack, Token opc) {
		super(stack, opc);
	}
	public void exec() throws ExecuteException {
		int arg2 = this.stack.safepop().getInt();
		int arg1 = this.stack.safepop().getInt();
		new Int(arg1 / arg2, this.stack).exec();;
	}
}

class Modulus extends Operation {
	public Modulus(Stack stack, Token opc) {
		super(stack, opc);
	}
	public void exec() throws ExecuteException {
		int arg2 = this.stack.safepop().getInt();
		int arg1 = this.stack.safepop().getInt();
		new Int(arg1 % arg2, this.stack).exec();
	}
}

class And extends Operation {
	public And(Stack stack, Token opc) {
		super(stack, opc);
	}
	public void exec() throws ExecuteException {
		int arg2 = this.stack.safepop().getInt();
		int arg1 = this.stack.safepop().getInt();
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
		int arg2 = this.stack.safepop().getInt();
		int arg1 = this.stack.safepop().getInt();
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
		Type arg2 = this.stack.safepop();
		Type arg1 = this.stack.safepop();
		int erg = arg1.eq(arg2) ? 0 : 1;

		new Int(erg, this.stack).exec();
	}
}

class LessThan extends Operation {
	public LessThan(Stack stack, Token opc) {
		super(stack, opc);
	}
	public void exec() throws ExecuteException {
		int arg2 = this.stack.safepop().getInt();
		int arg1 = this.stack.safepop().getInt();
		int erg = arg1 < arg2 ? 0 : 1;

		new Int(erg, this.stack).exec();
	}
}

class GreaterThan extends Operation {
	public GreaterThan(Stack stack, Token opc) {
		super(stack, opc);
	}
	public void exec() throws ExecuteException {
		int arg2 = this.stack.safepop().getInt();
		int arg1 = this.stack.safepop().getInt();
		int erg = arg1 > arg2 ? 0 : 1;

		new Int(erg, this.stack).exec();
	}
}

class Negation extends Operation {
	public Negation(Stack stack, Token opc) {
		super(stack, opc);
	}
	public void exec() throws ExecuteException {
		int arg1 = this.stack.safepop().getInt();

		new Int(-1 * arg1, this.stack).exec();
	}
}

class Copy extends Operation {
	public Copy(Stack stack, Token opc) {
		super(stack, opc);
	}
	public void exec() throws ExecuteException {
		/* note that the assignment starts counting at "1" */
		int arg1 = this.stack.safepop().getInt();
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
		int arg1 = this.stack.safepop().getInt();

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
		String arg1 = this.stack.safepop().getUnit();

		try {
			ParserCode ret = new Parser(new Scanner(arg1), this.stack, this.inputlist).parse();
			if (ret == ParserCode.QUIT) {
				throw new ExecuteException("`q' in Unit not allowed");
			}
		} catch (ScannerException trse) {
			throw new ExecuteException("<app-scanner> " + trse.getMessage());
		} catch (ParserException trpe) {
			throw new ExecuteException("<app-parser> " + trpe.getMessage());
		}
	}
}
