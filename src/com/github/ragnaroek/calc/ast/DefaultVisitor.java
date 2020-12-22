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
/**
 * Implements a default traversal strategy.
 * @author Michael Bohn
 *
 * @param <A>
 */
public class DefaultVisitor<A> 
implements IVisitor<A> {

	@Override
	public void visit(final CalcLProgram prog, final A arg) {
		for(Definition def : prog.getDefinitions()) {
			def.accept(this, arg);
		}
		prog.getCalculation().accept(this, arg);
	}

	@Override
	public void visit(final InputDefinition def, final A arg) {
		def.getVariable().accept(this, arg);
	}

	@Override
	public void visit(final ConstantDefinition def, final A arg) {
		def.getVariable().accept(this, arg);
		def.getNumberLiteral().accept(this, arg);
	}

	@Override
	public void visit(final Calculation cal, final A arg) {
		cal.getExpression().accept(this, arg);
	}

	@Override
	public void visit(final Variable var, final A arg) {
		//no-op
	}

	@Override
	public void visit(final ArithmeticExpression aexp, final A arg) {
		for(Expression expr : aexp.getExpressions()) {
			 expr.accept(this, arg);
		 }
	}

	@Override
	public void visit(final NumberLiteral num, final A arg) {
		//no-op
	}
}
