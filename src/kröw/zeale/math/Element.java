package kr�w.zeale.math;

import kr�w.zeale.math.exceptions.EmptyEquationException;
import kr�w.zeale.math.exceptions.UnmatchedParenthesisException;

interface Element {
	public double evaluate() throws EmptyEquationException, UnmatchedParenthesisException;
}