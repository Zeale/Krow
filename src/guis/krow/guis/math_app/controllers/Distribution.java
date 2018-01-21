package krow.guis.math_app.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Distribution {
	private class DoubleRef {
		public double numb;
	}

	private final ArrayList<Double> numbs;
	private Double mean, sum, median, mode;

	private DoubleRef sampleVariance, normalVariance, sampleStandardDeviation, normalStandardDeviation;

	public Distribution(final ArrayList<Double> numbs) {
		this.numbs = new ArrayList<>(numbs);
	}

	public Distribution(final double... numbs) {
		this.numbs = new ArrayList<>();
		for (final double d : numbs)
			this.numbs.add(d);
	}

	public int getCount() {
		return numbs.size();
	}

	public double getMax() {
		numbs.sort(null);
		return numbs.get(getCount() - 1);
	}

	public double getMean() {
		if (mean == null)
			return mean = getSum() / getCount();
		return mean;

	}

	public double getMedian() {
		if (median == null)
			return median = (numbs.size() & 1) == 0
					? (numbs.get(numbs.size() / 2 - 1).doubleValue() + numbs.get(numbs.size() / 2).doubleValue()) / 2
					: numbs.get(numbs.size() / 2).doubleValue();
		return median;
	}

	public double getMin() {
		numbs.sort(null);
		return numbs.get(0);
	}

	public double getMode() {
		if (mode != null)
			return mode;
		final Map<Double, Integer> numbmap = new HashMap<>();
		for (final Number n : numbs)
			numbmap.put(n.doubleValue(), numbmap.containsKey(n.doubleValue()) ? numbmap.get(n.doubleValue()) + 1 : 1);
		double nmax = 0;
		int ncount = 0;
		for (final Double d : numbmap.keySet())
			if (numbmap.get(d) > ncount) {
				ncount = numbmap.get(d);
				nmax = d;
			}
		return mode = nmax;
	}

	public double getStandardDeviation(final boolean sample) {
		if (sample)
			if (sampleStandardDeviation != null)
				return sampleStandardDeviation.numb;
			else
				sampleStandardDeviation = new DoubleRef();
		else if (normalStandardDeviation != null)
			return normalStandardDeviation.numb;
		else
			normalStandardDeviation = new DoubleRef();

		final DoubleRef standardDeviation = sample ? sampleStandardDeviation : normalStandardDeviation;
		return standardDeviation.numb = Math.sqrt(getVariance(sample));
	}

	public double getSum() {
		if (sum == null) {
			sum = new Double(0);
			for (final double d : numbs)
				sum += d;
		}
		return sum;
	}

	public double getVariance(final boolean sample) {
		if (sample)
			if (sampleVariance != null)
				return sampleVariance.numb;
			else
				sampleVariance = new DoubleRef();
		else // produce the exact same result.
		if (normalVariance != null)
			return normalVariance.numb;
		else
			normalVariance = new DoubleRef();

		final DoubleRef variance = sample ? sampleVariance : normalVariance;

		variance.numb = 0d;
		for (final Number d : numbs) {
			final double dist = d.doubleValue() - getMean();
			variance.numb += dist * dist;
		}

		return variance.numb /= numbs.size() + (sample ? -1 : 0);

	}

}
