/*
 * Header for VSL-Language"
 *
*/
grammar Vsl2;

@header {
package ch.hslu.cobau.vsl2;
}

///////////////////////////////////////////////////////////////////////////////
// Parser-Regeln
///////////////////////////////////////////////////////////////////////////////
programm : PROGRAM BEZEICHNER
           BEGIN
              ( statement ';' )*
           END '.' EOF;
statement : zuweisung | writeInt;
zuweisung : BEZEICHNER ':=' ( expression | BEZEICHNER ) ;
writeInt  : WRITE_INT BEZEICHNER;
expression : expression binaryOp=(TIMES | DIV) expression
           | expression binaryOp=(PLUS | MINUS) expression
           | ZAHL;

///////////////////////////////////////////////////////////////////////////////
// Scanner(Lexer)-Regeln
///////////////////////////////////////////////////////////////////////////////
PROGRAM:     'PROGRAM';
BEGIN:       'BEGIN';
END:         'END';
WRITE_INT:   'writeInt';
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
