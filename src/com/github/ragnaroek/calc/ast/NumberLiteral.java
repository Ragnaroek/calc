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
import java.math.BigInteger;

import com.github.ragnaroek.calc.Assert;

public class NumberLiteral 
extends Expression 
{
	private final BigInteger fNum;
	
	public NumberLiteral(final BigInteger num) {
		Assert.notNull(num);
		this.fNum = num;
	}
	
	public BigInteger getNumber() {
		return fNum;
	}
	
	@Override
	public <A> void accept(final IVisitor<A> visitor, final A arg) {
		visitor.visit(this, arg);
	}
}
