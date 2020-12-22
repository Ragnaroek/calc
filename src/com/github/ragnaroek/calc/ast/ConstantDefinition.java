package com.github.ragnaroek.calc.ast;
import com.github.ragnaroek.calc.Assert;

public class ConstantDefinition 
extends Definition 
{
	private final NumberLiteral fNumber;
	
	public ConstantDefinition(final Variable var, final NumberLiteral number) {
		super(var);
		Assert.notNull(number);
		this.fNumber = number;
	}
	
	@Override
	public Kind getKind() {
		return Kind.CONSTANT;
	}

	@Override
	public <A> void accept(IVisitor<A> visitor, A arg) {
		visitor.visit(this, arg);
	}
	
	public NumberLiteral getNumberLiteral() {
		return fNumber;
	}
}
