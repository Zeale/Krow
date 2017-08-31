package kröw.libs.mindset;

@Deprecated
public class ObjectAlreadyExistsException extends Exception {

	/**
	 * Serial Version Unique Identifier
	 */
	private static final long serialVersionUID = 1L;

	private final MindsetObject thrower, victim;

	ObjectAlreadyExistsException(final MindsetObject thrower, final MindsetObject victim) {
		this.thrower = thrower;
		this.victim = victim;
	}

	public MindsetObject getThrower() {
		return thrower;
	}

	public MindsetObject getVictim() {
		return victim;
	}

}
