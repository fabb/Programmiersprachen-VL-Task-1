/**
 * @author Bernhard Urban, Fabian Ehrentraud
 */

package taschenrechner;

/**
 * Generic structure which is used for the stack and inputlist
 */
public interface Type {
	/**
	 * execute the main purpose of the implementation
	 *
	 * @throws ExecuteException
	 */
	public void exec() throws ExecuteException;

	/**
	 * @return the integer value
	 * @throws ExecuteException if it isn't an Int
	 */
	public int getInt() throws ExecuteException;

	/**
	 * @return the string value of the unit
	 * @throws ExecuteException if it isn't an Unit
	 */
	public String getUnit() throws ExecuteException;

	/**
	 * equals two objects of the type `Type' (because equals() doesn't work in
	 * this case)
	 *
	 * @param o object to compare
	 * @return if they're equal or not
	 * @throws ExecuteException when the types don't match
	 */
	public boolean eq(Type o) throws ExecuteException;
}

/**
 * An unit contains a string, which represents some operations and numbers of
 * the calculator. the operations gets executes if the operator `a' is applied
 * to an unit.
 */
class Unit implements Type {
	private Stack stack = null;
	private String unit;

	public Unit(String str, Stack s) {
		this.unit = str;
		this.stack = s;
	}

	@Override
	public String getUnit() {
		return this.unit;
	}

	/**
	 * The execution of an unit pushes the object itself to the stack
	 *
	 * @see taschenrechner.Type#exec()
	 */
	@Override
	public void exec() {
		this.stack.push(this);
	}

	@Override
	public int getInt() throws ExecuteException {
		throw new ExecuteException("not a Integer or Bool: \"" + this + "\"");
	}

	@Override
	public boolean eq(Type o) throws ExecuteException {
		try {
			return this.unit.equals(o.getUnit());
		} catch (ExecuteException e) {
			throw new ExecuteException("can't compare Types: \"" + this + "\" and \"" + o + "\"");
		}
	}

	@Override
	public String toString() {
		return '[' + this.unit + ']';
	}
}

/**
 * An Int contains an integer, which represents the internal datatype of the
 * calculator for an integer.
 */
class Int implements Type {
	private Stack stack = null;
	private int i;

	public Int(int i, Stack s) {
		this.i = i;
		this.stack = s;
	}

	@Override
	public int getInt() {
		return this.i;
	}

	/**
	 * The execution of an int pushes the object itself to the stack
	 *
	 * @see taschenrechner.Type#exec()
	 */
	@Override
	public void exec() {
		this.stack.push(this);
	}

	@Override
	public String getUnit() throws ExecuteException {
		throw new ExecuteException("not a Unit: \"" + this + "\"");
	}

	@Override
	public boolean eq(Type o) throws ExecuteException {
		try {
			return this.i == o.getInt();
		} catch (ExecuteException e) {
			throw new ExecuteException("can't compare Types: \"" + this + "\" and \"" + o + "\"");
		}
	}

	@Override
	public String toString() {
		return this.i + "";
	}
}
