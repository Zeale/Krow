package kröw.lexers.equation;

import kröw.lexers.equation.exceptions.EmptyEquationException;
import kröw.lexers.equation.exceptions.IrregularCharacterException;
import kröw.lexers.equation.exceptions.UnmatchedParenthesisException;

interface Element {
	public double evaluate() throws EmptyEquationException, UnmatchedParenthesisException, IrregularCharacterException;
}