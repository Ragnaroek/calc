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
 * A {@link IVisitor} that prints the AST to the console.
 * The <code>depth</code> argument of each visitXXX method is the current
 * depth in the tree (to print the correct indentation).
 * @author Michael Bohn
 */
public class ASTViewer 
extends DefaultVisitor<Integer> 
{	
	@Override
	public void visit(final CalcLProgram prog, final Integer depth) {
		System.out.println("\nASTView:\n");
		System.out.println("CalcProgram");
		super.visit(prog, depth+1);
	}

	@Override
	public void visit(final InputDefinition def, final Integer depth) {
		printIndentation(depth);
		System.out.println("InputDefinition");
		super.visit(def, depth+1);
	}

	@Override
	public void visit(final ConstantDefinition def, final Integer depth) {
		printIndentation(depth);
		System.out.println("ConstantDefinition");
		super.visit(def, depth+1);
	}

	@Override
	public void visit(final Calculation cal, final Integer depth) {
		printIndentation(depth);
		System.out.println("Calculation");
		super.visit(cal, depth+1);
	}
	
	@Override
	public void visit(final Variable var, final Integer depth) {
		printIndentation(depth);
		System.out.printf("Variable [name=%s]\n", var.getName());
		super.visit(var, depth+1);
	}
	
	@Override
	public void visit(final NumberLiteral num, final Integer depth) {
		printIndentation(depth);
		System.out.printf("NumberLiteral [number=%d]\n", num.getNumber());
		super.visit(num, depth+1);
	}

	@Override
	public void visit(final ArithmeticExpression aexp, final Integer depth) {
		printIndentation(depth);
		System.out.printf("ArithmeticExpression [operator=%s]\n", aexp.getOperator());
		super.visit(aexp, depth+1);
	}

	private void printIndentation(final int depth) {
		for(int i=0; i < depth; i++) {
			System.out.print("--");
		}
	}
	
}
