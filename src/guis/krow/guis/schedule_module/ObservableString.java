package krow.guis.schedule_module;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import kröw.callables.ParameterizedTask;

public class ObservableString implements Serializable {

	/**
	 * SUID
	 */
	private static final long serialVersionUID = 1L;
	private String value;

	/**
	 * @return the value
	 */
	public String get() {
		return value;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void set(String value) {
		if (getListener() != null)
			getListener().execute(value);
		this.value = value;
	}

	private void writeObject(ObjectOutputStream os) throws IOException {
		os.writeObject(value);
	}

	private void readObject(ObjectInputStream is) throws IOException {
		try {
			value = (String) is.readObject();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	private ParameterizedTask<String> listener;

	/**
	 * @return the listener
	 */
	public ParameterizedTask<String> getListener() {
		return listener;
	}

	/**
	 * @param listener
	 *            the listener to set
	 */
	public void setListener(ParameterizedTask<String> listener) {
		this.listener = listener;
	}

	public ObservableString(String value) {
		set(value);
	}

	public ObservableString() {
	}

}
