package kr�w.lexers.equation;

import kr�w.lexers.equation.exceptions.EmptyEquationException;
import kr�w.lexers.equation.exceptions.IrregularCharacterException;
import kr�w.lexers.equation.exceptions.UnmatchedParenthesisException;

interface Element {
	public double evaluate() throws EmptyEquationException, UnmatchedParenthesisException, IrregularCharacterException;
}