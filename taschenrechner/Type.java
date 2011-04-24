package taschenrechner;

public interface Type {
	public void exec() throws ExecuteException;

	public int getInt() throws ExecuteException;

	public String getUnit() throws ExecuteException;

	public boolean eq(Type o) throws ExecuteException;
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