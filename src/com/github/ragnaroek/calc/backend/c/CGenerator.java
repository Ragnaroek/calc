package com.github.ragnaroek.calc.backend.c;
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
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;

import com.github.ragnaroek.calc.ast.ArithmeticExpression;
import com.github.ragnaroek.calc.ast.CalcLProgram;
import com.github.ragnaroek.calc.ast.Calculation;
import com.github.ragnaroek.calc.ast.ConstantDefinition;
import com.github.ragnaroek.calc.ast.DefaultVisitor;
import com.github.ragnaroek.calc.ast.Definition;
import com.github.ragnaroek.calc.ast.InputDefinition;
import com.github.ragnaroek.calc.ast.NumberLiteral;
import com.github.ragnaroek.calc.ast.Variable;
import com.github.ragnaroek.calc.ast.ArithmeticExpression.Operator;
import com.github.ragnaroek.calc.ast.Definition.Kind;

public class CGenerator 
extends DefaultVisitor<Void> 
{
	private final PrintStream fOut;
	
	private List<Definition> fCollectedDefs;
	
	public CGenerator(final File out) throws IOException {
		this.fOut = new PrintStream(out);
		this.fCollectedDefs = new LinkedList<Definition>();
	}
	
	@Override
	public void visit(CalcLProgram prog, Void arg) {
		fOut.printf("#include <stdio.h>\n\n");
		super.visit(prog, arg);
	}

	@Override
	public void visit(final InputDefinition def, final Void arg) {
		handleDefinition(def);
	}

	@Override
	public void visit(ConstantDefinition def, Void arg) {
		handleDefinition(def);
	}
	
	private void handleDefinition(final Definition def) {
		fOut.printf("long %s = 0;\n", def.getVariable().getName());
		fCollectedDefs.add(def);
	}

	@Override
	public void visit(final Calculation cal, final Void arg) {
		//generate main function
		fOut.printf("int main(void) {\n");
		//generate init global variables
		
		for(Definition def : fCollectedDefs) {
			if(def.getKind() == Kind.CONSTANT) {
				fOut.printf("%s = %d;\n", def.getVariable().getName(), 
						((ConstantDefinition)def).getNumberLiteral().getNumber());
			} else if(def.getKind() == Kind.INPUT) {
				fOut.printf("printf(\"Please input value for %s: \");\n", def.getVariable().getName());
				fOut.printf("scanf(\"%%ld\",&%s);\n", def.getVariable().getName());
			} else {
				throw new RuntimeException("unknown definition type");
			}
		}
		
		//generate calculation
		fOut.printf("printf(\"result = %%ld\\n\",");
		super.visit(cal, arg);
		fOut.printf(");\n");
		fOut.printf("return 0;");
		fOut.printf("\n}");	
	}

	@Override
	public void visit(final ArithmeticExpression aexp, final Void arg) {
		int numExprs = aexp.getExpressions().size();

		if(numExprs == 0) {
			switch(aexp.getOperator()) {
			case TIMES: //fallthrough
			case DIV:
				fOut.printf("1");
				break;
			case PLUS: //fallthrough
			case MINUS:
				fOut.printf("0");
				break;
			default: throw new RuntimeException("unknown operator");
			}
		} else if(numExprs == 1) {
			super.visit(aexp, arg);
		} else {
			//invariant: numExprs >= 2
			if(aexp.getOperator() == Operator.TIMES || 
					aexp.getOperator() == Operator.PLUS) {

				String cop = null;
				if(aexp.getOperator() == Operator.TIMES) {
					cop = "*";
				} else {
					cop = "+";
				}

				fOut.printf("(");
				aexp.getExpressions().get(0).accept(this, arg);
				for(int i=1; i<numExprs; i++) {
					fOut.printf(cop);
					aexp.getExpressions().get(i).accept(this, arg);
				}
				fOut.printf(")");
			} else if(aexp.getOperator() == Operator.MINUS || 
					aexp.getOperator() == Operator.DIV) {

				String cop = null;
				if(aexp.getOperator() == Operator.DIV) {
					cop = "/";
				} else {
					cop = "-";
				}
				
				//numExprs - 1 ( for correct nesting
				for(int i=0; i<numExprs-1; i++) {
					fOut.printf("(");
				}
				aexp.getExpressions().get(0).accept(this, arg);
				for(int i=1; i<numExprs; i++) {
					fOut.printf(cop);
					aexp.getExpressions().get(i).accept(this, arg);
					fOut.printf(")");
				}
			}  else {
				throw new RuntimeException("Unknown operator");
			}
		}
	}

	@Override
	public void visit(final Variable var, final Void arg) {
		//reference global variable with same name
		fOut.printf(var.getName(), arg);
	}

	@Override
	public void visit(final NumberLiteral num, final Void arg) {
		fOut.printf("%d", num.getNumber());
	}
}
