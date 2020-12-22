package com.github.ragnaroek.calc;

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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.github.ragnaroek.calc.ast.ASTViewer;
import com.github.ragnaroek.calc.ast.CalcLProgram;
import com.github.ragnaroek.calc.ast.Expression;
import com.github.ragnaroek.calc.backend.c.CGenerator;
import com.github.ragnaroek.calc.optimization.ConstantFolding;
import com.github.ragnaroek.calc.optimization.OutParam;
import com.github.ragnaroek.calc.parser.ParseException;
import com.github.ragnaroek.calc.parser.Parser;
import com.github.ragnaroek.calc.scanner.IScanner;
import com.github.ragnaroek.calc.scanner.Scanner;
import com.github.ragnaroek.calc.semantic.SemanticCheckException;
import com.github.ragnaroek.calc.semantic.VariableUsage;

/**
 * The main class.
 */
public class Compiler {

	private static final String PRINT_AST_OPTION = "-printAST";
	private static final String OPTIMZE_OPTION = "-optimize";
	private static final String OUT_OPTION = "-o";
	
	private static boolean optimize = false;
	private static boolean astView = false;
	private static String outFile = null;
	private static String sourceFile = null;
	
	public static void main(final String[] args) throws Exception {		
		parseArgs(args);
		//invariant: args ok
		doCompile();
	}
	
	/**
	 * Parses the command line arguments and stores the information
	 * in the static fields in this class.
	 * @param args the command line options
	 */
	private static void parseArgs(final String[] args) {
		if(args.length < 2 || args.length > 4) {
			printUsageAndTerminate();
		}
		sourceFile = args[0];
		
		for(int i=1; i < args.length; i++) {
			String arg = args[i];
			if(arg.equals(OPTIMZE_OPTION)) {
				optimize = true;
			} else if(arg.equals(OUT_OPTION)) {
				if(args.length < i+1) {
					printUsageAndTerminate();
				}
				outFile = args[i+1];
				i++;
			} else if(arg.equals(PRINT_AST_OPTION)) {
				astView = true;
			} else {
				printUsageAndTerminate();
			}
		}
		
		if(astView && outFile != null) {
			printUsageAndTerminate();
		}
	}
	
	/**
	 * Prints the usage and terminates the VM.
	 */
	private static void printUsageAndTerminate() {
		System.out.println("Usage: java de.fh_trier.calcl.Compiler source [-optimize] -o output");
		System.out.println("\t OR java.de.fh_trier.calcl.Compiler source [-optimize] -printAST");
		System.out.println("\tsource \t <input CalcL source file>");
		System.out.println("\toutput \t <output file name (C-source)>");
		System.out.println();
		System.out.println("Options:\n" +
				"\t-printAST - prints the AST to the console and exits the VM\n" +
				"\t-optimize - performs constant folding");
		System.exit(-1);
	}

	/**
	 * Does the compilation steps.
	 * @throws IOException if the source file cannot be read or the destination file not be written
	 */
	private static void doCompile() throws IOException {
		CalcLProgram ast = null;
		try {
			ast = runFrontend(new File(sourceFile));
		} catch (ParseException e) {
			System.err.println(e.getError());
			return; //abort compilation
		}
		System.out.println("file parsed successfully");
		
		try {
			runSemanticCheck(ast);
		} catch (SemanticCheckException e) {
			System.err.println(e.getError());
			return; //abort compilation
		}
		System.out.println("semantic check successfull");
		
		if(optimize) {
			runOptimization(ast);
		}
		
		if(astView) {
			printAST(ast);
		} else { //print c-source
			runBackend(ast, new File(outFile));
			System.out.println("code generation successfull");
		}
	}
	
	/**
	 * Runs the frontend of the compiler that parses the source file and
	 * creates an AST for this file.
	 * @param source the source file
	 * @return the AST
	 * @throws ParseException if there are syntax error in the source file
	 * @throws IOException if the source file cannot be read
	 */
	private static CalcLProgram runFrontend(final File source) throws ParseException, IOException {
		FileInputStream src = new FileInputStream(source);
		IScanner scanner = new Scanner(src);
		Parser parser = new Parser(scanner);
		return parser.parse();
	}
	
	/**
	 * Runs the semantic check (static semantic).
	 * @param ast the AST to check for semantic errors
	 */
	private static void runSemanticCheck(final CalcLProgram ast) {
		VariableUsage varCheck = new VariableUsage();
		ast.accept(varCheck, null);
	}
	
	/**
	 * Optimizes the AST (constant folding).
	 * @param ast the AST to optimize
	 */
	private static void runOptimization(final CalcLProgram ast) {
		ConstantFolding folding = new ConstantFolding();
		ast.accept(folding, new OutParam<Expression>());
	}
	
	/**
	 * Runs the backend of the compiler (C code generation).
	 * @param ast the AST to generate C code for
	 * @param out the File to write the output to
	 * @throws IOException if the output file cannot be writtens
	 */
	private static void runBackend(final CalcLProgram ast, final File out) throws IOException {
		CGenerator generator = new CGenerator(out);
		ast.accept(generator, null);
	}
	
	/**
	 * Prints the AST to the console.
	 * @param ast the AST to print
	 */
	private static void printAST(final CalcLProgram ast) {
		ASTViewer viewer = new ASTViewer();
		ast.accept(viewer, 0);
	}
}
