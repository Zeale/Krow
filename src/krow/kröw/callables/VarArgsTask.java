package kr�w.callables;

public interface VarArgsTask<PT> {
	void execute(@SuppressWarnings("unchecked") PT... params);
}
