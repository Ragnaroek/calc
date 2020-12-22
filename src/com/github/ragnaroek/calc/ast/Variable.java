package com.github.ragnaroek.calc.ast;
import com.github.ragnaroek.calc.Assert;

public class Variable 
extends Expression
{
	private final String fName;
	
	public Variable(final String name) {
		Assert.notNull(name);
		this.fName = name;
	}
	
	public String getName() {
		return fName;
	}
	
	@Override
	public <A> void accept(final IVisitor<A> visitor, final A arg) {
		visitor.visit(this, arg);
	}
}
