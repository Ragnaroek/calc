package com.github.ragnaroek.calc.parser;
/*
 * CalcL example compiler Copyright (C) 2010 Michael Bohn
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
import java.io.IOException;
import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;

import com.github.ragnaroek.calc.Assert;
import com.github.ragnaroek.calc.ast.ArithmeticExpression;
import com.github.ragnaroek.calc.ast.CalcLProgram;
import com.github.ragnaroek.calc.ast.Calculation;
import com.github.ragnaroek.calc.ast.ConstantDefinition;
import com.github.ragnaroek.calc.ast.Definition;
import com.github.ragnaroek.calc.ast.Expression;
import com.github.ragnaroek.calc.ast.InputDefinition;
import com.github.ragnaroek.calc.ast.NumberLiteral;
import com.github.ragnaroek.calc.ast.Variable;
import com.github.ragnaroek.calc.ast.ArithmeticExpression.Operator;
import com.github.ragnaroek.calc.scanner.IScanner;
import com.github.ragnaroek.calc.scanner.ScanException;
import com.github.ragnaroek.calc.scanner.Token;

public class Parser {

	private final IScanner fScanner;
	private Token fLookahead;
	
	/**
	 * Creates a new CalcL-Parser.
	 * @param scanner the Scanner to read tokens from.
	 */
	public Parser(final IScanner scanner) {
		Assert.notNull(scanner);
		this.fScanner = scanner;
	}
	
	/**
	 * Parses the input file and returns the AST
	 * for this file.
	 */
	public CalcLProgram parse() throws ParseException {
		nextToken(); //set first lookahead token
		CalcLProgram prog = prog();
		
		return prog;
	}
	
	/**
	 * Implements rule 
	 * <PROG> := <DEFS> <CAL>.
	 */
	private CalcLProgram prog() throws ParseException {
		List<Definition> defs = defs();
		Calculation cal = cal();
		match(Token.Tag.EOF);
		return new CalcLProgram(defs, cal);
	}
	
	/**
	 * Implements rules:
	 * <DEFS> := <DEF_FRONT> <DEFS>  
     * <DEFS> := Epsilon 
	 */
	private LinkedList<Definition> defs() throws ParseException {
		if(is(Token.Tag.DEF)) {
			Definition def = def_front();
			LinkedList<Definition> defs = defs();
			defs.addFirst(def);
			return defs;
		} else { //epsilon
			return new LinkedList<Definition>();
		}
	}
	
	/**
	 * Implements rule
	 * <DEF_FRONT> := 'def' 'identifier' <DEF_REST>
	 */
	private Definition def_front() throws ParseException {
		match(Token.Tag.DEF);
		Token potentialIdentifier = fLookahead;
		match(Token.Tag.IDENTIFIER);
		return def_rest(potentialIdentifier);
	}
	
	/**
	 * Implements rules:
	 * <DEF_REST>  := 'number'
     * <DEF_REST>  := '(' 'input' ')' 
	 */
	private Definition def_rest(final Token identifier) throws ParseException {
		switch(fLookahead.getTag()) {
		case NUMBER:
			Token potentialNumber = fLookahead;
			match(Token.Tag.NUMBER);
			return new ConstantDefinition(new Variable(identifier.getValue()), makeNumber(potentialNumber.getValue()));
		case LPAREN:
			match(Token.Tag.LPAREN);
			match(Token.Tag.INPUT);
			match(Token.Tag.RPAREN);
			return new InputDefinition(new Variable(identifier.getValue()));
		default: 
			err();
		    break;
		}
		return null; //unreachable
	}
	
	/**
	 * Implements rule:
	 * <CAL> := '(' 'calculate' <ARITH> ')'
	 */
	private Calculation cal() throws ParseException {
		match(Token.Tag.LPAREN);
		match(Token.Tag.CALCULATE);
		Expression expr = arith();
		match(Token.Tag.RPAREN);
		return new Calculation(expr);
	}
	
	/**
	 * Implements rules:
	 * <ARITH>  := '(' <OP> <ARITHS> ')'
     * <ARITH>  := 'number'
     * <ARITH>  := 'identifier' 
	 */
	private Expression arith() throws ParseException {
		switch(fLookahead.getTag()) {
		case LPAREN: 
			match(Token.Tag.LPAREN);
			Operator op = op();
			List<Expression> exprs = ariths();
			match(Token.Tag.RPAREN);
			return new ArithmeticExpression(op, exprs);
		case NUMBER:
			Token potentialNumber = fLookahead;
			match(Token.Tag.NUMBER);
			return makeNumber(potentialNumber.getValue());
		case IDENTIFIER:
			Token potentialIdentifier = fLookahead;
			match(Token.Tag.IDENTIFIER);
			return new Variable(potentialIdentifier.getValue());
		default:
			err();
			break;
		}
		return null; //unreachable
	}
	
	/**
	 * Implements rules:
	 * <ARITHS> := <ARITH> <ARITHS>
     * <ARITHS> := Epsilon
	 */
	private LinkedList<Expression> ariths() throws ParseException {
		Token.Tag tag = fLookahead.getTag();
		if(tag == Token.Tag.LPAREN ||
			tag == Token.Tag.NUMBER ||	
			tag == Token.Tag.IDENTIFIER) {
			Expression expr = arith();
			LinkedList<Expression> exprs = ariths();
			exprs.addFirst(expr);
			return exprs;
		} else { //epsilon
			return new LinkedList<Expression>();
		}
	}
	
	/**
	 * Implements rules: 
	 * <OP> := '*'
     * <OP> := '/'
     * <OP> := '-'
     * <OP> := '+'
	 */
	private Operator op() throws ParseException {
		switch(fLookahead.getTag()) {
		case TIMES : 
			match(Token.Tag.TIMES);
		    return Operator.TIMES;
		case DIV:
			match(Token.Tag.DIV);
			return Operator.DIV;
		case MINUS:
			match(Token.Tag.MINUS);
			return Operator.MINUS;
		case PLUS:
			match(Token.Tag.PLUS);
			return Operator.PLUS;
		default:
			err();
			break;
		}
		return null; //unreachable
	}
	
	//"Helper" methods:
	
	private NumberLiteral makeNumber(final String num) throws ParseException {
		try {
			return new NumberLiteral(new BigInteger(num));
		} catch (NumberFormatException e) {
			//if this fails, something went wrong in the Lexer
			throw new ParseException(e.getMessage());
		}
	}
	
	private void match(final Token.Tag tag) throws ParseException {
		if(tag == fLookahead.getTag()) {
			nextToken();
		} else {
			throw new ParseException(String.format("syntax error: %s expected", tag));
		}
	}
	
	private void err() throws ParseException {
		throw new ParseException(String.format("syntax error: unexpected %s", fLookahead.getTag()));
	}
	
	private boolean is(final Token.Tag tag) {
		return fLookahead.getTag() == tag;
	}
	
	private void nextToken() {
		try {
			fLookahead = fScanner.nextToken();
		} catch (IOException e) {
			System.err.println(e);
		} catch (ScanException e) {
			System.err.println(e);
		}
	}
}
