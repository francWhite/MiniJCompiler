/*
 * Header for VSL-Language"
 *
*/
grammar Vsl;

@header {
package ch.hslu.cobau.vsl;
}

///////////////////////////////////////////////////////////////////////////////
// Parser-Regeln
///////////////////////////////////////////////////////////////////////////////
programm : PROGRAM BEZEICHNER
           BEGIN
              ( zuweisung ';' )*
           END '.' EOF;
zuweisung : BEZEICHNER ':=' ( expression | BEZEICHNER | STRING ) ;
expression : expression binaryOp=(TIMES | DIV) expression
           | expression binaryOp=(PLUS | MINUS) expression
           | ZAHL;

///////////////////////////////////////////////////////////////////////////////
// Scanner(Lexer)-Regeln
///////////////////////////////////////////////////////////////////////////////
PROGRAM:     'PROGRAM';
BEGIN:       'BEGIN';
END:         'END';
PLUS:        '+';
MINUS:       '-';
TIMES:       '*';
DIV:         '/';
BEZEICHNER:  BUCHSTABE (BUCHSTABE|ZIFFER)*;
WS:          [ \t\r\n]+ -> skip; // überlese spaces, tabs, cr/nl
STRING :     '"' ALLEZEICHEN* '"' ;
BUCHSTABE:  'A'..'Z' | 'a'..'z';
ZAHL :       ZIFFER+ ;
ZIFFER:     '0'..'9';
ALLEZEICHEN: BUCHSTABE | ZIFFER | '.' | '!' | ' ' | ',' ;
