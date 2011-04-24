/**
 * @author Bernhard Urban, Fabian Ehrentraud
 */

package taschenrechner;

/**
 * This class defines a generic pattern for an operation in the calculator.
 */
abstract class Operation implements Type {
	protected Token opc;
	protected Stack stack;

	/**
	 * @param stack the corresponding stack
	 * @param opc the Token of the operation
	 */
	public Operation(Stack stack, Token opc) {
		this.stack = stack;
		this.opc = opc;
	}

	/**
	 * This method checks if a given integer is a bool in the calculator. A bool
	 * is 0 (true) or 1 (false)
	 *
	 * @param i integer to check
	 * @throws ExecuteException if the integer isn't a bool
	 */
	protected static void checkBool(int i) throws ExecuteException {
		if (i != 0 && i != 1)
			throw new ExecuteException("not a valid Boolean: \"" + i + "\"");
	}

	public String toString() {
		return "" + this.opc.getToken();
	}

	/**
	 * This method comes from Type. If any operation tries to call this method,
	 * this ends up in an runtime error of the calculator.
	 *
	 * @see taschenrechner.Type#getInt()
	 */
	@Override
	public int getInt() throws ExecuteException {
		throw new ExecuteException("not an Integer or Boolean: \"" + this + "\"");
	}

	/**
	 * This method comes from Type. If any operation tries to call this method,
	 * this ends up in an runtime error of the calculator.
	 *
	 * @see taschenrechner.Type#getUnit()
	 */
	@Override
	public String getUnit() throws ExecuteException {
		throw new ExecuteException("not an Unit: \"" + this + "\"");
	}

	/**
	 * Comparing operations doesn't make sense in this context
	 *
	 * @see taschenrechner.Type#eq(Type)
	 */
	public boolean eq(Type o) throws ExecuteException {
		throw new ExecuteException("can't compare Types: \"" + this + "\" and \"" + o + "\"");
	}
}

class Addition extends Operation {
	public Addition(Stack stack, Token opc) {
		super(stack, opc);
	}

	/**
	 * Pops two elements from the stack and pushes the result of the addition
	 * back to the stack.
	 *
	 * @see taschenrechner.Type#exec()
	 * @throws ExecuteException if there aren't enough elements on the stack or the type of
	 * the element isn't correct
	 */
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

	/**
	 * Pops two elements from the stack and pushes the result of the subtraction
	 * back to the stack.
	 *
	 * @see taschenrechner.Type#exec()
	 * @throws ExecuteException if there aren't enough elements on the stack or the type of
	 * the element isn't correct
	 */
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

	/**
	 * Pops two elements from the stack and pushes the result of the multiplication
	 * back to the stack.
	 *
	 * @see taschenrechner.Type#exec()
	 * @throws ExecuteException if there aren't enough elements on the stack or the type of
	 * the element isn't correct
	 */
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

	/**
	 * Pops two elements from the stack and pushes the result of the division
	 * back to the stack.
	 *
	 * @see taschenrechner.Type#exec()
	 * @throws ExecuteException if there aren't enough elements on the stack or the type of
	 * the element isn't correct
	 */
	public void exec() throws ExecuteException {
		int arg2 = this.stack.safepop().getInt();
		int arg1 = this.stack.safepop().getInt();
		try {
			new Int(arg1 / arg2, this.stack).exec();
		} catch (ArithmeticException e) {
			throw new ExecuteException("division by 0 not possible");
		}
	}
}

class Modulus extends Operation {
	public Modulus(Stack stack, Token opc) {
		super(stack, opc);
	}

	/**
	 * Pops two elements from the stack and pushes the result of the modulus
	 * back to the stack.
	 *
	 * @see taschenrechner.Type#exec()
	 * @throws ExecuteException if there aren't enough elements on the stack or the type of
	 * the element isn't correct
	 */
	public void exec() throws ExecuteException {
		int arg2 = this.stack.safepop().getInt();
		int arg1 = this.stack.safepop().getInt();
		try {
			new Int(arg1 % arg2, this.stack).exec();
		} catch (ArithmeticException e) {
			throw new ExecuteException("division by 0 not possible");
		}
	}
}

class And extends Operation {
	public And(Stack stack, Token opc) {
		super(stack, opc);
	}

	/**
	 * Pops two elements from the stack and pushes the result of the logical and
	 * back to the stack.
	 *
	 * @see taschenrechner.Type#exec()
	 * @throws ExecuteException if there aren't enough elements on the stack or the type of
	 * the element isn't correct
	 */
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

	/**
	 * Pops two elements from the stack and pushes the result of the logical or
	 * back to the stack.
	 *
	 * @see taschenrechner.Type#exec()
	 * @throws ExecuteException if there aren't enough elements on the stack or the type of
	 * the element isn't correct
	 */
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

	/**
	 * Pops two elements from the stack and pushes the result of the equal
	 * operator as a bool back to the stack.
	 * Both elements must have the same type, otherwise an exception is thrown.
	 *
	 * @see taschenrechner.Type#exec()
	 * @throws ExecuteException	 if there aren't enough elements on the stack or the type of
	 * the elements aren't correct.
	 */
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

	/**
	 * Pops two elements from the stack and pushes the result of the lessthan-operation
	 * back to the stack.
	 *
	 * @see taschenrechner.Type#exec()
	 * @throws ExecuteException if there aren't enough elements on the stack or the type of
	 * the element isn't correct
	 */
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

	/**
	 * Pops two elements from the stack and pushes the result of the greaterthan-operation
	 * back to the stack.
	 *
	 * @see taschenrechner.Type#exec()
	 * @throws ExecuteException if there aren't enough elements on the stack or the type of
	 * the element isn't correct
	 */
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

	/**
	 * Pops one element from the stack and pushes the result of the negation-operation
	 * back to the stack.
	 *
	 * @see taschenrechner.Type#exec()
	 * @throws ExecuteException if there aren't enough elements on the stack or the type of
	 * the element isn't correct
	 */
	public void exec() throws ExecuteException {
		int arg1 = this.stack.safepop().getInt();

		new Int(-1 * arg1, this.stack).exec();
	}
}

class Copy extends Operation {
	public Copy(Stack stack, Token opc) {
		super(stack, opc);
	}

	/**
	 * Pops one element from the stack and copy the n-th element from the stack to top of stack.
	 *
	 * @see taschenrechner.Type#exec()
	 * @throws ExecuteException if there aren't enough elements on the stack, the type of
	 * the element isn't correct or when it's a invalid index
	 */
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

	/**
	 * Pops one element from the stack and delete the n-th element from the stack.
	 *
	 * @see taschenrechner.Type#exec()
	 * @throws ExecuteException if there aren't enough elements on the stack, the type of
	 * the element isn't correct or when it's a invalid index
	 */
	public void exec() throws ExecuteException {
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

	/**
	 * Pops one element from the stack and try to scan and parse it. The result
	 * of the parsing operation is added to the inputlist.
	 *
	 * @see taschenrechner.Type#exec()
	 * @throws ExecuteException if the top of stack isn't a unit, the content
	 * of the unit can't be scanned correctly or there's a parsing error
	 */
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
