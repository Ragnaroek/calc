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

package de.fh_trier.calcl.scanner;

%%

%class Scanner
%public
%implements IScanner
%function nextToken
%type Token

%yylexthrow{
ScanException
%yylexthrow}

%unicode
%line
%column

STD_CHAR = [a-z]
IDENTIFIER = {STD_CHAR}+
DIGIT = [0-9]
DIGITS = {DIGIT}+
SIGN = [+-]
NUMBER = {SIGN}? {DIGITS}

WHITESPACE = [ ] | \t | \r | \n | \r\n 

%%

<YYINITIAL> {
  {WHITESPACE}+  {/*ignore whitespace*/}

  {NUMBER} { return new Token(Token.Tag.NUMBER, yytext());}

  "input" { return new Token(Token.Tag.INPUT, null); }
  "def" { return new Token(Token.Tag.DEF, null); }
  "calculate" {return new Token(Token.Tag.CALCULATE, null);}
    
  {IDENTIFIER} {return new Token(Token.Tag.IDENTIFIER, yytext());}
  \*   {return new Token(Token.Tag.TIMES, null);}
  \/   {return new Token(Token.Tag.DIV, null);}
  \-   {return new Token(Token.Tag.MINUS, null);}
  \+   {return new Token(Token.Tag.PLUS, null);}

  \( {return new Token(Token.Tag.LPAREN, null);}
  \) {return new Token(Token.Tag.RPAREN, null);}
  
  <<EOF>> {return new Token(Token.Tag.EOF, null);}
}

.|\n {throw new RuntimeException("premature end of file");}
