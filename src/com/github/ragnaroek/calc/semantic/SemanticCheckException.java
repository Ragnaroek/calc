package com.github.ragnaroek.calc.semantic;
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
public class SemanticCheckException 
extends RuntimeException 
{	
	private static final long serialVersionUID = -6812880017691692065L;
	private final String fMessage;
	
	public SemanticCheckException(final String message) {
		this.fMessage = message;
	}
	
	public String getError() {
		return fMessage;
	}
}
