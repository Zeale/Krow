package kr�w.program.api.callables;

public interface VarArgsTask<PT> {
	void execute(@SuppressWarnings("unchecked") PT... params);
}
