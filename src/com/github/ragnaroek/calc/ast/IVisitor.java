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
public interface IVisitor<A> {
	public void visit(CalcLProgram prog, A arg);
	public void visit(InputDefinition def, A arg);
	public void visit(ConstantDefinition def, A arg);
	public void visit(Calculation cal, A arg);
	public void visit(Variable var, A arg);
	public void visit(ArithmeticExpression expr, A arg);
	public void visit(NumberLiteral num, A arg);
}
