package kröw.math;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

class WrapperManager {
	public static final class Wrapper {

		public static boolean isCloseChar(final char c) {
			for (final WrapperType wr : WrapperType.values())
				if (c == wr.close)
					return true;
			return false;
		}

		public static boolean isOpenChar(final char c) {
			for (final WrapperType wr : WrapperType.values())
				if (c == wr.open)
					return true;
			return false;
		}

		private final WrapperType type;

		private final WrapperFunction function;

		public Wrapper(final WrapperType type, final WrapperFunction function) {
			this.type = type;
			this.function = function;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(final Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (!(obj instanceof Wrapper))
				return false;
			final Wrapper other = (Wrapper) obj;
			if (function != other.function)
				return false;
			if (type != other.type)
				return false;
			return true;
		}

		/**
		 * @return the function
		 */
		public WrapperFunction getFunction() {
			return function;
		}

		/**
		 * @return the type
		 */
		public WrapperType getType() {
			return type;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + (function == null ? 0 : function.hashCode());
			result = prime * result + (type == null ? 0 : type.hashCode());
			return result;
		}

	}

	public static enum WrapperFunction {
		OPEN, CLOSE
	}

	public static enum WrapperType {

		PARENTHESIS('(', ')'), BRACKETS('[', ']'), BRACES('{', '}'), PIPELINE('|', '|');

		public final char open, close;

		private WrapperType(final char open, final char close) {
			this.open = open;
			this.close = close;
		}

	}

	private final Stack<Wrapper> wrappers = new Stack<>();

	public boolean add(final Wrapper wrapper) {
		if (wrapper.getFunction() == WrapperFunction.CLOSE)
			if (getLastOpenWrapper().getType() != wrapper.getType())
				return false;
		wrappers.push(wrapper);
		return true;
	}

	private Wrapper getLastOpenWrapper() {
		if (wrappers.size() == 0)
			return null;
		final List<Wrapper> wrappers = new ArrayList<>(this.wrappers);
		Collections.reverse(wrappers);
		final List<Wrapper> closeWrappers = new ArrayList<>();
		for (final Wrapper w : wrappers)
			if (w.getFunction() == WrapperFunction.CLOSE)
				closeWrappers.add(w);
			else if (w.getFunction() == WrapperFunction.OPEN) {
				final Wrapper complement = new Wrapper(w.getType(), WrapperFunction.CLOSE);
				if (closeWrappers.contains(complement))
					closeWrappers.remove(complement);
				else
					return w;
			}
		return null;
	}

}
