package com.github.ragnaroek.calc.optimization;
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
import java.math.BigInteger;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.github.ragnaroek.calc.ast.ArithmeticExpression;
import com.github.ragnaroek.calc.ast.CalcLProgram;
import com.github.ragnaroek.calc.ast.Calculation;
import com.github.ragnaroek.calc.ast.ConstantDefinition;
import com.github.ragnaroek.calc.ast.DefaultVisitor;
import com.github.ragnaroek.calc.ast.Expression;
import com.github.ragnaroek.calc.ast.NumberLiteral;
import com.github.ragnaroek.calc.ast.Variable;
import com.github.ragnaroek.calc.ast.ArithmeticExpression.Operator;

/**
 * A Visitor that performs constant folding on the AST.
 * The {@link OutParam} object that is the argument for each method
 * saves a reference to a possible new AST-node that should replace
 * the node to which the argument was passed. For example, if
 * the expression (+ 1 2 3) is folded to the constant 6, the argument contains
 * a reference to a {@link NumberLiteral} with value 6 and the Expression is replaced
 * by this {@link NumberLiteral}.
 * 
 * @author Michael Bohn
 *
 */
public class ConstantFolding 
extends DefaultVisitor<OutParam<Expression>> 
{
	/**
	 * A map of known constant values (map value) for variables (map key).
	 */
	private final Map<String, BigInteger> fConstVars;
	/**
	 * A list of constant definitions in the AST.
	 */
	private List<ConstantDefinition> fConstantDefs;
	
	public ConstantFolding() {
		this.fConstVars = new HashMap<String, BigInteger>();
		this.fConstantDefs = new LinkedList<ConstantDefinition>();
	}

	@Override
	public void visit(final CalcLProgram prog, final OutParam<Expression> arg) {
		super.visit(prog, arg);
		
		//remove all ConstantDefinitions! -> all constant variables have been folded
 		for(ConstantDefinition def : fConstantDefs) {
 			prog.removeDefinition(def);
 		}
	}

	@Override
	public void visit(final ConstantDefinition def, final OutParam<Expression> arg) {
		//store constant value for variable in map
		this.fConstVars.put(def.getVariable().getName(), def.getNumberLiteral().getNumber());
		//store constant definition in list (for later removal)
		this.fConstantDefs.add(def);
	}

	@Override
	public void visit(final Calculation cal, final OutParam<Expression> arg) {
		OutParam<Expression> reducedCalcExpr = new OutParam<Expression>();
 		super.visit(cal, reducedCalcExpr);
 		if(cal.getExpression() != reducedCalcExpr.get()) {
 			cal.rewriteExpression(reducedCalcExpr.get());
 		} 		
	}

	@Override
	public void visit(final ArithmeticExpression aexp, final OutParam<Expression> arg) {
		//recursively fold constants first
		for(int i=0; i < aexp.getExpressions().size(); i++ ) {
			Expression expression = aexp.getExpressions().get(i);
			OutParam<Expression> reducedChildExpr = new OutParam<Expression>();
			expression.accept(this, reducedChildExpr);
			if(reducedChildExpr.get() != expression) {
				aexp.rewriteExpression(i, reducedChildExpr.get());
			}
		}
		
		int numExprs = aexp.getExpressions().size();
		List<Expression> expressions = new LinkedList<Expression>(aexp.getExpressions());
		if(numExprs == 0) {
			switch(aexp.getOperator()) {
			case TIMES: //fallthrough
			case DIV: arg.set(new NumberLiteral(BigInteger.ONE));
			          break;
			case PLUS: //fallthrough
			case MINUS: arg.set(new NumberLiteral(BigInteger.ZERO));
					  break;
			default: throw new RuntimeException("unknown operator");
			}
		} else if(numExprs == 1) {
			arg.set(expressions.get(0));
		} else {
			if(aexp.getOperator() == Operator.TIMES || aexp.getOperator() == Operator.PLUS) {
				//sort expression so that numbers are in front and other expressions come at last
				//allowed, because * and + are associative and commutative
				Collections.sort(expressions, new Comparator<Expression>() {
					@Override
					public int compare(final Expression exp1, final Expression exp2) {
						if(exp1 instanceof NumberLiteral && !(exp2 instanceof NumberLiteral)) {
							return -1;
						}
						if( !(exp1 instanceof NumberLiteral) && exp2 instanceof NumberLiteral) {
							return 1;
						}
						return 0;
					}
				});
			}
			
			Expression first = expressions.get(0);
			BigInteger folded = null;
			if(first instanceof NumberLiteral) {
				folded = ((NumberLiteral)first).getNumber();
			} else {
				arg.set(aexp);
				return;
			}
			
			for(int i=1; i < expressions.size(); i++) {
				Expression next = expressions.get(i);
				if( !(next instanceof NumberLiteral) ) {
					LinkedList<Expression> newExpressions = new LinkedList<Expression>();
					newExpressions.addFirst(new NumberLiteral(folded));
					for(int z = i; z < expressions.size(); z++) {
						newExpressions.addLast(expressions.get(z));
					}
					ArithmeticExpression newaexp = new ArithmeticExpression(aexp.getOperator(), newExpressions);
					arg.set(newaexp);
					return;
				} else {
					BigInteger num = ((NumberLiteral)expressions.get(i)).getNumber();
					switch(aexp.getOperator()) {
					case TIMES: folded = folded.multiply(num);
					            break;
					case DIV:   folded = folded.divide(num);
					            break;
					case PLUS:  folded = folded.add(num);
					            break;
					case MINUS: folded = folded.subtract(num);
					            break;
					default: throw new RuntimeException("unknown operator");
					}
				}
			}
			//if we get here, all expression have been folded to one number
			arg.set(new NumberLiteral(folded));
			return;
		}
	}

	@Override
	public void visit(final Variable var, final OutParam<Expression> arg) {
		 BigInteger constValue = fConstVars.get(var.getName());
		 if(constValue != null) { //var is constant
			 arg.set(new NumberLiteral(constValue));
		 } else {
			 arg.set(var);
		 }
	}

	@Override
	public void visit(final NumberLiteral num, final OutParam<Expression> arg) {
		arg.set(num);
	}
}
