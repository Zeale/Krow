package krow.guis.math_module.controllers;

public final class NumericalStatistic extends Statistic {

	public double value;

	public NumericalStatistic(String name, double value) {
		super(name);
		this.value = value;
	}

	@Override
	protected String getValue() {
		return "" + value;
	}

}
