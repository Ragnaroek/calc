package com.github.ragnaroek.calc.ast;
import com.github.ragnaroek.calc.Assert;

public abstract class Definition 
extends ASTNode 
{
	public static enum Kind {
		INPUT,
		CONSTANT
	}
	
	private final Variable fVar;
	
	public Definition(final Variable var) {
		Assert.notNull(var);
		this.fVar = var;
	}
	
	public Variable getVariable() {
		return fVar;
	}
	
	public abstract Kind getKind();
}
