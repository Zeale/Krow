package kröw.data.protection.settings;

public class Setting<VT> {
	private VT value;
	private ModificationHandler<VT> onModified;

	public boolean setValue(VT value) {
		if (!onModified.handle(value, this.value))
			return false;
		this.value = value;
		return true;
	}

	// TODO Add save and load methods.

	public VT getValue() {
		return value;
	}

	private interface ModificationHandler<RT> {
		/**
		 * This method is called before a {@link Setting}'s value is changed. It can do,
		 * basically, whatever it wants. The return value determines whether the
		 * {@link Setting} will have its value changed to <code>newVal</code> after the
		 * handler has been called, so if an implementation of this method does not want
		 * a certain value in its {@link Setting}, <code>newVal</code> can be checked
		 * for this "unwanted value" and, if found, the handle method can return
		 * <code>false</code>.
		 * 
		 * @param newVal
		 *            The new value.
		 * @param oldVal
		 *            The old value.
		 * @return <code>true</code> if the value should be changed, <code>false</code>
		 *         otherwise.
		 */
		boolean handle(RT newVal, RT oldVal);
	}
}
