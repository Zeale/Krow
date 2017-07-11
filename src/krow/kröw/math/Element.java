package kröw.math;

import kröw.math.exceptions.EmptyEquationException;
import kröw.math.exceptions.IrregularCharacterException;
import kröw.math.exceptions.UnmatchedParenthesisException;

interface Element {
	public double evaluate() throws EmptyEquationException, UnmatchedParenthesisException, IrregularCharacterException;
}