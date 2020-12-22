package com.github.ragnaroek.calc.ast;
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
import java.util.Collections;
import java.util.List;

import com.github.ragnaroek.calc.Assert;

public class ArithmeticExpression 
extends Expression
{
	public static enum Operator {
		TIMES,
		DIV,
		PLUS,
		MINUS
	}
	
	private final Operator fOp;
	private final List<Expression> fExpressions;
	private final List<Expression> fUnmodifiableView;
	
	public ArithmeticExpression(final Operator op, final List<Expression> expressions)
	{
		Assert.notNull(op);
		Assert.notNull(expressions);
		
		this.fExpressions = expressions;
		this.fUnmodifiableView = Collections.unmodifiableList(expressions);
		this.fOp = op;
	}
	
	public Operator getOperator() {
		return fOp;
	}
	
	public List<Expression> getExpressions() {
		return fUnmodifiableView;
	}
	
	@Override
	public <A> void accept(final IVisitor<A> visitor, final A arg) {
		 visitor.visit(this, arg);
		 
	}
	
	//rewrite interface
	
	public void rewriteExpression(final int i, final Expression newExpression) {
		this.fExpressions.set(i, newExpression);
	}
}
