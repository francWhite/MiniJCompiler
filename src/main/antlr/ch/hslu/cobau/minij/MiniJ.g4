grammar MiniJ;

@header {
package ch.hslu.cobau.minij;
}

// milestone 2: parser
///////////////////////////////////////////////////////////////////////////////
// Parser-Regeln
///////////////////////////////////////////////////////////////////////////////
unit : program;
program : (procedure | declaration | record)+ EOF;
procedure: PROCEDURE IDENTIFIER declaration* body;
declaration : (TYPE | IDENTIFIER) IDENTIFIER SEMICOLON;
record : RECORD IDENTIFIER declaration* END SEMICOLON;
body : ;
///////////////////////////////////////////////////////////////////////////////
// Scanner(Lexer)-Regeln
///////////////////////////////////////////////////////////////////////////////
WHITESPACE : [ \t\r\n]+ -> skip;

INCREMENT : '++';
DECREMENT : '--';
NEGATE : '!';
MULT : '*';
DIV : '/';
MOD : '%';
ADD : '+';
SUB : '-';
LESSER : '<';
GREATER : '>';
LESSEREQ : '<=';
GREATEREQ : '>=';
EQUAL : '==';
NOTEQUAL : '!=';
AND : '&&';
OR : '||';

BEGIN : 'begin' | '{';
END : 'end' | '}';
INDEXBEGIN : '[';
INDEXEND : ']';
LPAREN : '(';
RPAREN : ')';
COMMA : ',';
PERIOD : '.';
SEMICOLON : ';';

PROCEDURE : 'procedure';
RECORD : 'record';
IF : 'if';
ELSIF : 'elsif';
ELSE : 'else';
WHILE : 'while';
DO : 'do';
REF : 'ref';
RETURN : 'return';

TYPE : (INT | BOOL | STRING);
INT: 'int';
BOOL: 'boolean';
STRING: 'string';

IDENTIFIER : (LOWERCHAR | UPPERCHAR) (LOWERCHAR | UPPERCHAR | DIGIT)*;
ALLCHARS : DIGIT | LOWERCHAR | UPPERCHAR | PERIOD | WHITESPACE | COMMA | NEGATE;
DIGIT : [0-9];
NUMBER : DIGIT+;
LOWERCHAR : 'a'..'z';
UPPERCHAR : 'A'..'Z';
TRUE : 'true';
FALSE : 'false';

READINT : 'readInt';
WRITEINT : 'writeInt';
READCHAR : 'readChar';
WRITECHAR : 'writeChar';
MAIN: 'main' LPAREN RPAREN;

COMMENT : '//';
BLOCKCOMMENTSTART : '/*';
BLOCKCOMMENTEND : '*/';