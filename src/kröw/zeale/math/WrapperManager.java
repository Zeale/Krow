package kröw.zeale.math;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

class WrapperManager {
	public static final class Wrapper {

		public static boolean isOpenChar(char c) {
			for (WrapperType wr : WrapperType.values())
				if (c == wr.open)
					return true;
			return false;
		}

		public static boolean isCloseChar(char c) {
			for (WrapperType wr : WrapperType.values())
				if (c == wr.close)
					return true;
			return false;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (!(obj instanceof Wrapper))
				return false;
			Wrapper other = (Wrapper) obj;
			if (function != other.function)
				return false;
			if (type != other.type)
				return false;
			return true;
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
			result = prime * result + ((function == null) ? 0 : function.hashCode());
			result = prime * result + ((type == null) ? 0 : type.hashCode());
			return result;
		}

		private WrapperType type;
		private WrapperFunction function;

		public Wrapper(WrapperType type, WrapperFunction function) {
			this.type = type;
			this.function = function;
		}

		/**
		 * @return the type
		 */
		public WrapperType getType() {
			return type;
		}

		/**
		 * @return the function
		 */
		public WrapperFunction getFunction() {
			return function;
		}

	}

	public static enum WrapperType {

		PARENTHESIS('(', ')'), BRACKETS('[', ']'), BRACES('{', '}'), PIPELINE('|', '|');

		public final char open, close;

		private WrapperType(char open, char close) {
			this.open = open;
			this.close = close;
		}

	}

	public static enum WrapperFunction {
		OPEN, CLOSE
	}

	private Stack<Wrapper> wrappers = new Stack<>();

	public boolean add(Wrapper wrapper) {
		if (wrapper.getFunction() == WrapperFunction.CLOSE)
			if (getLastOpenWrapper().getType() != wrapper.getType())
				return false;
		wrappers.push(wrapper);
		return true;
	}

	private Wrapper getLastOpenWrapper() {
		if (wrappers.size() == 0)
			return null;
		List<Wrapper> wrappers = new ArrayList<>(this.wrappers);
		Collections.reverse(wrappers);
		List<Wrapper> closeWrappers = new ArrayList<>();
		for (Wrapper w : wrappers)
			if (w.getFunction() == WrapperFunction.CLOSE)
				closeWrappers.add(w);
			else if (w.getFunction() == WrapperFunction.OPEN) {
				Wrapper complement = new Wrapper(w.getType(), WrapperFunction.CLOSE);
				if (closeWrappers.contains(complement))
					closeWrappers.remove(complement);
				else
					return w;
			}
		return null;
	}

}
