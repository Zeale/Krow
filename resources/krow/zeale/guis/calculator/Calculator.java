package krow.zeale.guis.calculator;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Calculator {

	private static Calculator calculator;// TODO Fix

	public static void main(String[] args) {
		// TEST method; used to test Parser's evaluate method.
		Parser p = new Parser();
		System.out.println(p.evaluate("1+1")); // Prints 2.0
		System.out.println(p.evaluate("13+19")); // Prints 32.0
		try {
			System.out.println(p.evaluate("potato"));
		} catch (NumberFormatException e) {
			System.err.println("Invalid equation");
		} // Prints "Invalid equation"
		System.out.println(p.evaluate("5+15.3")); // Prints 20.3
		System.out.println(p.evaluate("13.91231+1.32918")); // Prints 15.241489999999999
		// Operations with more than +
		System.out.println(p.evaluate("5/2")); // Prints 2.5

		// Calculation goes from right to left, so this prints correctly even
		// though order of operations is not supported yet...
		System.out.println(p.evaluate("4+5/6")); // Prints 4.833333333333333
		// This however, does not.
		System.out.println(p.evaluate("4/2+6")); // Prints 0.5

	}

	private Stage stage = new Stage();
	private CalculatorController controller;
	private Parser parser = new Parser();

	public Calculator() {
		FXMLLoader loader = new FXMLLoader(Calculator.class.getResource("Calculator.fxml"));
		try {
			stage.setScene(new Scene(loader.load()));
			stage.initStyle(StageStyle.UNDECORATED);
		} catch (IOException e) {
			// This block should never run. Just like in the FileManager class.
		}
		controller = loader.<CalculatorController>getController();
	}

	public final double getHeight() {
		return stage.getHeight();
	}

	public final String getTitle() {
		return stage.getTitle();
	}

	public final double getWidth() {
		return stage.getWidth();
	}

	public final double getX() {
		return stage.getX();
	}

	public final double getY() {
		return stage.getY();
	}

	public void hide() {
		stage.hide();
	}

	public final boolean isFocused() {
		return stage.isFocused();
	}

	public final boolean isFullScreen() {
		return stage.isFullScreen();
	}

	public final boolean isIconified() {
		return stage.isIconified();
	}

	public final boolean isMaximized() {
		return stage.isMaximized();
	}

	public final boolean isShowing() {
		return stage.isShowing();
	}

	public final void setFullScreen(boolean value) {
		stage.setFullScreen(value);
	}

	public final void setHeight(double value) {
		stage.setHeight(value);
	}

	public final void setIconified(boolean value) {
		stage.setIconified(value);
	}

	public final void setMaximized(boolean value) {
		stage.setMaximized(value);
	}

	public final void setTitle(String value) {
		stage.setTitle(value);
	}

	public final void setWidth(double value) {
		stage.setWidth(value);
	}

	public final void setX(double value) {
		stage.setX(value);
	}

	public final void setY(double value) {
		stage.setY(value);
	}

	public final void show() {
		stage.show();
	}

	public static Calculator open() {
		calculator.stage.show();
		return calculator;
	}

	public double calculate() {
		double result = parser.evaluate(controller.getEquation());
		controller.setEquation(Double.toString(result));
		return result;
	}

	public static class Parser {

		private static interface Element {
			public double evaluate();
		}

		@SuppressWarnings("unused")
		private static class Number implements Element {

			public void chain(Operation operation, Element nextElement) {
				this.operation = operation;
				this.nextElement = nextElement;
			}

			private double value;
			private Operation operation;
			private Element nextElement;

			public static interface Operation {

				final Operation ADD = new Operation() {

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
					public double evaluate(double input1, double input2) {
						return input1 - input2;
					}

				};

				final Operation MULTIPLY = new Operation() {

					@Override
					public double evaluate(double input1, double input2) {
						return input1 * input2;
					}

				};

				final Operation DIVIDE = new Operation() {

					@Override
					public double evaluate(double input1, double input2) {
						return input1 / input2;
					}

				};

				double evaluate(double input1, double input2);

				public static Operation getOperation(char c) {
					if (!isOperator(c))
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
					case '�':
						return DIVIDE;
					default:
						return NONE;
					}
				}

				public static Operation getOperation(String c) {
					return getOperation(c.charAt(0));
				}

				// TODO Add other operations here.
			}

			public Number(double value) {
				this.value = value;
			}

			public Number(double value, Operation operation, Element nextElement) {
				this.value = value;
				this.operation = operation;
				this.nextElement = nextElement;
			}

			@Override
			public double evaluate() {
				if (!(operation == null || nextElement == null)) {
					return operation.evaluate(value, nextElement.evaluate());
				}
				return value;
			}

		}

		public double evaluate(String equation) {
			reset();
			this.equation = equation;
			Number n = new Number(getNumber()), currNumb = n;
			while (position < equation.length())
				currNumb.chain(Number.Operation.getOperation(nextChar()), currNumb = new Number(getNumber()));
			return n.evaluate();
		}

		private volatile String equation;
		private int position;

		private double getNumber() {
			if (!(isNumb(getCurrChar()) || getCurrChar().equals(".")))
				throw new NumberFormatException();
			int flen = 0, blen = 0;
			while (position + --blen > -1 && (equation.charAt(position + blen) == '.'
					|| isNumb(String.valueOf(equation.charAt(position + blen)))))
				;
			blen++;
			while (position + ++flen < equation.length() && (equation.charAt(position + flen) == '.'
					|| isNumb(String.valueOf(equation.charAt(position + flen)))))
				;
			double value = Double.valueOf(equation.substring(position + blen, position + flen));
			position += flen;
			return value;

		}

		private static boolean isOperator(char c) {
			return c == '+' || c == '-' || c == '*' || c == '/' || c == 'x' || c == '�';
		}

		private static boolean isOperator(String c) {
			return isOperator(c.charAt(0));
		}

		private static boolean isNumb(char c) {
			return Character.isDigit(c) || c == '.';
		}

		/**
		 * Checks if the given character could be part of a number. All digits
		 * and a decimal point will cause this function to return true if they
		 * are passed into it.
		 * 
		 * @param c
		 *            The <i><b>single character</b></i> to be tested.
		 * @return <code>true</code> if <code>c</code> could be part of a
		 *         number.
		 */
		private static boolean isNumb(String c) {
			return isNumb(c.charAt(0));
		}

		private String getNextChar() {
			return equation.substring(position + 1, position + 1);
		}

		private String getCurrChar() {
			return equation.substring(position, position + 1);
		}

		/**
		 * Increments the position <b>after</b> returning the current character.
		 * 
		 * @return The current char.
		 */
		private String nextChar() {
			return equation.substring(position, position++ + 1);
		}

		private void reset() {
			position = 0;
		}

	}

}
