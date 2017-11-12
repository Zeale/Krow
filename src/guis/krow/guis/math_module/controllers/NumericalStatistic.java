package krow.guis.math_module.controllers;

import java.text.NumberFormat;

public final class NumericalStatistic extends Statistic {

	public double value;

	public NumericalStatistic(final String name, final double value) {
		super(name);
		this.value = value;
	}

	@Override
	protected String getValue() {
		return NumberFormat.getNumberInstance().format(value);
	}

}
