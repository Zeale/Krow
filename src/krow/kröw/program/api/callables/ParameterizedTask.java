package kröw.program.api.callables;

public interface ParameterizedTask<P> {
	void execute(P param);
}
