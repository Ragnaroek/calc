# CalcL

A simple compiler for S-Expression based arithmetic expression that are compiled to C.
It even has a constant folding optimsation phase.

This compiler was created for educational purpose too show the full compiler phases
with a hands-on example.

## Usage

~~~ bash
java com.github.ragnaroek.calc.Compiler source [-optimize] -o output
	 OR com.github.ragnaroek.calc.Compiler source [-optimize] -printAST
	source 	 <input CalcL source file>
	output 	 <output file name (C-source)>

Options:
	-printAST - prints the AST to the console and exits the VM
	-optimize - performs constant folding
~~~
	
Invoke from the jar:
~~~ bash
java -cp <path_to_jar>/calcl_<version>.jar com.github.ragnaroek.calc.Compiler <arguments_see_above>
~~~

## CalcL Language

You can define variables like so:

~~~ lisp
def x (input)
def z 3
~~~
The `(input)` expression prompts the user to enter a value for `x` that is then used as value for `x`.

An arbitrary arithmetic expression can be expressed in the calculate statement, like so:
~~~ lisp
(calculate 
   (* (+ 1 x) 
      z 
      4))
~~~

The value of the calculated expression is printed to the console when the program is run.

You can find more examples in the `example` subfolder.