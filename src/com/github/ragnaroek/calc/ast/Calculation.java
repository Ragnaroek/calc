package com.github.ragnaroek.calc.ast;
import com.github.ragnaroek.calc.Assert;

public class Calculation 
extends ASTNode 
{
	private Expression fExpr;
	
	public Calculation(final Expression expr) {
		Assert.notNull(expr);
		this.fExpr = expr;
	}
	
	@Override
	public <A> void accept(final IVisitor<A> visitor, final A arg) {
		visitor.visit(this, arg);
	}
	
	public Expression getExpression() {
		return fExpr;
	}
	
	//Rewrite interface
	
	public void rewriteExpression(final Expression newExpr) {
		Assert.notNull(newExpr);
		this.fExpr = newExpr;
	}
}
