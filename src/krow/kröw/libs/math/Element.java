package kr�w.libs.math;

import kr�w.libs.math.exceptions.EmptyEquationException;
import kr�w.libs.math.exceptions.IrregularCharacterException;
import kr�w.libs.math.exceptions.UnmatchedParenthesisException;

interface Element {
	public double evaluate() throws EmptyEquationException, UnmatchedParenthesisException, IrregularCharacterException;
}