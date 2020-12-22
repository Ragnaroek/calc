package com.github.ragnaroek.calc.scanner;
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
public class Token {
	
	private final Tag fTag;
	private final String fValue;
	
	public static enum Tag {
		IDENTIFIER,
		NUMBER,
		LPAREN,
		RPAREN,
		TIMES,
		DIV,
		MINUS,
		PLUS,
		/**
		 * Keywords
		 */
		INPUT,
		DEF,
		CALCULATE,
		EOF
	}
	
	public Token(final Tag tag, final String value) {
		this.fTag = tag;
		this.fValue = value;
	}
	
	public String getValue() {
		return fValue;
	}
	
	public Tag getTag() {
		return fTag;
	}
}
