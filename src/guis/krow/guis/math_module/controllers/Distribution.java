package krow.guis.math_module.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Distribution {
	private final ArrayList<Double> numbs;
	private Double mean, sum, median, mode;
	private DoubleRef sampleVariance, normalVariance, sampleStandardDeviation, normalStandardDeviation;

	public Distribution(double... numbs) {
		this.numbs = new ArrayList<>();
		for (double d : numbs)
			this.numbs.add(d);
	}

	public Distribution(ArrayList<Double> numbs) {
		this.numbs = new ArrayList<>(numbs);
	}

	public int getCount() {
		return numbs.size();
	}

	public double getMean() {
		if (mean == null)
			return mean = getSum() / getCount();
		return mean;

	}

	public double getSum() {
		if (sum == null) {
			sum = new Double(0);
			for (double d : numbs)
				sum += d;
		}
		return sum;
	}

	public double getMin() {
		numbs.sort(null);
		return numbs.get(0);
	}

	public double getMax() {
		numbs.sort(null);
		return numbs.get(getCount() - 1);
	}

	public double getMedian() {
		if (median == null)
			return median = (numbs.size() & 1) == 0
					? (numbs.get(numbs.size() / 2 - 1).doubleValue() + numbs.get(numbs.size() / 2).doubleValue()) / 2
					: numbs.get(numbs.size() / 2).doubleValue();
		return median;
	}

	public double getVariance(boolean sample) {
		if (sample)
			if (sampleVariance != null)
				return sampleVariance.numb;
			else
				sampleVariance = new DoubleRef();
		else {// We have braces here for formatting. Removing braces will
				// produce the exact same result.
			if (normalVariance != null)
				return normalVariance.numb;
			else
				normalVariance = new DoubleRef();
		}

		DoubleRef variance = sample ? sampleVariance : normalVariance;

		variance.numb = 0d;
		for (Number d : numbs) {
			double dist = d.doubleValue() - getMean();
			variance.numb += dist * dist;
		}

		return variance.numb /= (numbs.size() + (sample ? -1 : 0));

	}

	public double getStandardDeviation(boolean sample) {
		if (sample)
			if (sampleStandardDeviation != null)
				return sampleStandardDeviation.numb;
			else
				sampleStandardDeviation = new DoubleRef();
		else {
			if (normalStandardDeviation != null)
				return normalStandardDeviation.numb;
			else
				normalStandardDeviation = new DoubleRef();
		}

		DoubleRef standardDeviation = sample ? sampleStandardDeviation : normalStandardDeviation;
		return standardDeviation.numb = Math.sqrt(getVariance(sample));
	}

	private class DoubleRef {
		public double numb;
	}

	public double getMode() {
		if (mode != null)
			return mode;
		Map<Double, Integer> numbmap = new HashMap<>();
		for (Number n : numbs)
			numbmap.put(n.doubleValue(), (numbmap.containsKey(n.doubleValue())) ? numbmap.get(n.doubleValue()) + 1 : 1);
		double nmax = 0;
		int ncount = 0;
		for (Double d : numbmap.keySet())
			if (numbmap.get(d) > ncount) {
				ncount = numbmap.get(d);
				nmax = d;
			}
		return mode = nmax;
	}

}
