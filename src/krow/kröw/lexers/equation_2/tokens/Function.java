package kröw.lexers.equation_2.tokens;

public class Function {

	public final String name, contents;

	public Function(final String name, final String contents) {
		this.name = name;
		this.contents = contents;
	}

	@Override
	public String toString() {
		return "Function " + name + "(" + super.toString() + ")" + ": " + contents;
	}

}
