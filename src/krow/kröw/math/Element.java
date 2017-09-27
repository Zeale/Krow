package kr�w.math;

import kr�w.math.exceptions.EmptyEquationException;
import kr�w.math.exceptions.IrregularCharacterException;
import kr�w.math.exceptions.UnmatchedParenthesisException;

interface Element {
	public double evaluate() throws EmptyEquationException, UnmatchedParenthesisException, IrregularCharacterException;
}