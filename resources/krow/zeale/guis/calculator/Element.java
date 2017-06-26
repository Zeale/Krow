package krow.zeale.guis.calculator;

import krow.zeale.guis.calculator.exceptions.EmptyEquationException;
import krow.zeale.guis.calculator.exceptions.UnmatchedParenthesisException;

interface Element {
	public double evaluate() throws EmptyEquationException, UnmatchedParenthesisException;
}