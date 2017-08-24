package kröw.app.api.callables;

public interface ParameterizedTask<P> {
	void execute(P param);
}
