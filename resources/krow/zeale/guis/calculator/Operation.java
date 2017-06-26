package krow.zeale.guis.calculator;

interface Operation {

	default byte getPrecedence() {
		return 2;
	}

	final Operation ADD = new Operation() {
		@Override
		public byte getPrecedence() {
			return 0;
		}

		@Override
		public double evaluate(double input1, double input2) {
			return input1 + input2;
		}

	};

	final Operation NONE = new Operation() {

		@Override
		public double evaluate(double input1, double input2) {
			return input1;
		}

	};

	final Operation SUBTRACT = new Operation() {
		@Override
		public byte getPrecedence() {
			return 0;
		}

		@Override
		public double evaluate(double input1, double input2) {
			return input1 - input2;
		}

	};

	final Operation MULTIPLY = new Operation() {
		@Override
		public byte getPrecedence() {
			return 1;
		}

		@Override
		public double evaluate(double input1, double input2) {
			return input1 * input2;
		}

	};

	final Operation DIVIDE = new Operation() {
		@Override
		public byte getPrecedence() {
			return 1;
		}

		@Override
		public double evaluate(double input1, double input2) {
			return input1 / input2;
		}

	};

	final Operation POWER = new Operation() {
		@Override
		public byte getPrecedence() {
			return 2;
		}

		@Override
		public double evaluate(double input1, double input2) {
			return Math.pow(input1, input2);
		}
	};

	final Operation MODULUS = new Operation() {

		@Override
		public double evaluate(double input1, double input2) {
			return input1 % input2;
		}

		@Override
		public byte getPrecedence() {
			return 1;
		}
	};

	double evaluate(double input1, double input2);

	public static Operation getOperation(char c) {
		if (!Parser.isOperator(c))
			throw new NumberFormatException();
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
			throw new NumberFormatException();
		}
	}

	public static Operation getOperation(String c) {
		return getOperation(c.charAt(0));
	}

}