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
import java.util.LinkedList;
import java.util.List;

import com.github.ragnaroek.calc.ast.Calculation;
import com.github.ragnaroek.calc.ast.ConstantDefinition;
import com.github.ragnaroek.calc.ast.DefaultVisitor;
import com.github.ragnaroek.calc.ast.Definition;
import com.github.ragnaroek.calc.ast.InputDefinition;
import com.github.ragnaroek.calc.ast.Variable;

public class VariableUsage 
extends DefaultVisitor<Void> 
{
	private final List<Variable> fDefinedVariables;
	private boolean fCheckVariableUsage;
	
	public VariableUsage() {
		this.fDefinedVariables = new LinkedList<Variable>();
		this.fCheckVariableUsage = false;
	}
	
	@Override
	public void visit(final InputDefinition def, final Void arg) {
		handleDefinition(def);
		super.visit(def, arg);
	}

	@Override
	public void visit(final ConstantDefinition def, final Void arg) {
		handleDefinition(def);
		super.visit(def, arg);
	}
	
	private void handleDefinition(final Definition def) {
		
		Variable alreadyDefined = getVariableWithName(def.getVariable().getName());
		if(alreadyDefined == null) {
			fDefinedVariables.add(def.getVariable());
		} else {
			throw new SemanticCheckException(String.format("variable %s is doubly defined", def.getVariable().getName()));
		}
	}

	@Override
	public void visit(final Calculation cal, final Void arg) {
		//if we see the calculation node, we have to start checking
		//variable usage
		fCheckVariableUsage = true;
		super.visit(cal, arg);
	}
	
	@Override
	public void visit(final Variable var, final Void arg) {
		if(fCheckVariableUsage) {
			Variable definedVar = getVariableWithName(var.getName());
			if(definedVar == null) {
				throw new SemanticCheckException(String.format("undefined variable: %s", var.getName()));
			}
		}
	}
	
	private Variable getVariableWithName(final String name) {
		for(Variable var : fDefinedVariables) {
			if(var.getName().equals(name)) {
				return var;
			}
		}
		return null;
	}
}
