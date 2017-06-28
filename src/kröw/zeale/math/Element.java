package kröw.zeale.math;

import kröw.zeale.math.exceptions.EmptyEquationException;
import kröw.zeale.math.exceptions.UnmatchedParenthesisException;

interface Element {
	public double evaluate() throws EmptyEquationException, UnmatchedParenthesisException;
}