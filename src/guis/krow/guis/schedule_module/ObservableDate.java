package krow.guis.schedule_module;

import java.util.Date;

import kröw.callables.ParameterizedTask;

public class ObservableDate {

	private transient ParameterizedTask<Date> listener;

	public ObservableDate(ParameterizedTask<Date> listener) {
		this.listener = listener;
	}

	/**
	 * @return the listener
	 */
	public ParameterizedTask<Date> getListener() {
		return listener;
	}

	private Date date;

	/**
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * @param date
	 *            the date to set
	 */
	public void setDate(Date date) {
		this.date = date;
	}

	/**
	 * @param listener
	 *            the listener to set
	 */
	public void setListener(ParameterizedTask<Date> listener) {
		this.listener = listener;
	}

	public ObservableDate(ParameterizedTask<Date> listener, Date date) {
		this.listener = listener;
		this.date = date;
	}

	public ObservableDate() {
	}

}
