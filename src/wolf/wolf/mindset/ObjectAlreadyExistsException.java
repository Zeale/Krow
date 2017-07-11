package wolf.mindset;

public class ObjectAlreadyExistsException extends Exception {

	private final MindsetObject thrower, victim;

	ObjectAlreadyExistsException(final MindsetObject thrower, final MindsetObject victim) {
		this.thrower = thrower;
		this.victim = victim;
	}

	/**
	 * Serial Version Unique Identifier
	 */
	private static final long serialVersionUID = 1L;

	public MindsetObject getThrower() {
		return thrower;
	}

	public MindsetObject getVictim() {
		return victim;
	}

}
