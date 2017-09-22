package krow.guis.math_module.controllers;

import java.text.DecimalFormat;

public final class NumericalStatistic extends Statistic {

	public double value;

	public NumericalStatistic(String name, double value) {
		super(name);
		this.value = value;
	}

	@Override
	protected String getValue() {
		return DecimalFormat.getNumberInstance().format(value);
	}

}
