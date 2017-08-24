package kröw.app.api.callables;

public interface VarArgsTask<PT> {
	void execute(PT... params);
}
