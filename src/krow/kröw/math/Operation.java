package kröw.math;

interface Operation {

	final Operation ADD = new Operation() {
		@Override
		public double evaluate(final double input1, final double input2) {
			return input1 + input2;
		}

		@Override
		public byte getPrecedence() {
			return 0;
		}

	};

	final Operation NONE = (input1, input2) -> input1;

	final Operation SUBTRACT = new Operation() {
		@Override
		public double evaluate(final double input1, final double input2) {
			return input1 - input2;
		}

		@Override
		public byte getPrecedence() {
			return 0;
		}

	};

	final Operation MULTIPLY = new Operation() {
		@Override
		public double evaluate(final double input1, final double input2) {
			return input1 * input2;
		}

		@Override
		public byte getPrecedence() {
			return 1;
		}

	};

	final Operation DIVIDE = new Operation() {
		@Override
		public double evaluate(final double input1, final double input2) {
			return input1 / input2;
		}

		@Override
		public byte getPrecedence() {
			return 1;
		}

	};

	final Operation POWER = new Operation() {
		@Override
		public double evaluate(final double input1, final double input2) {
			return Math.pow(input1, input2);
		}

		@Override
		public byte getPrecedence() {
			return 2;
		}
	};

	final Operation MODULUS = new Operation() {

		@Override
		public double evaluate(final double input1, final double input2) {
			return input1 % input2;
		}

		@Override
		public byte getPrecedence() {
			return 1;
		}
	};

	public static Operation getOperation(final char c) {
		switch (c) {
		case '+':
			return ADD;
		case '-':
			return SUBTRACT;
		case '*':
		case 'x':
			return MULTIPLY;
		case '/':
		case '÷':
			return DIVIDE;
		case '^':
			return POWER;
		case '%':
			return MODULUS;
		default:
			return null;
		}
	}

	public static Operation getOperation(final String c) {
		return getOperation(c.charAt(0));
	}

	double evaluate(double input1, double input2);

	default byte getPrecedence() {
		return 2;
	}

}