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

public class CalcLProgram 
extends ASTNode 
{
	private final List<Definition> fDefs;
	private final List<Definition> fUnmodifiableDefs;
	private final Calculation fCalc;
	
	public CalcLProgram(final List<Definition> defs, final Calculation cal) {
		Assert.notNull(defs);
		Assert.notNull(cal);
		this.fDefs = defs;
		this.fUnmodifiableDefs = Collections.unmodifiableList(defs);
		this.fCalc = cal;
	}
	
	public List<Definition> getDefinitions() {
		return this.fUnmodifiableDefs;
	}
	
	public Calculation getCalculation() {
		return fCalc;
	}

	@Override
	public <A> void accept(final IVisitor<A> visitor, final A arg) {
		visitor.visit(this, arg);
	}
	
	//rewrite interface
	
	public void removeDefinition(final Definition def) {
		this.fDefs.remove(def);
	}
}
