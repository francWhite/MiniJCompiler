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
procedure: PROCEDURE IDENTIFIER LPAREN (param? | param (COMMA param)*) RPAREN declaration* (BEGIN body END | BEGINBLOCK body ENDBLOCK) SEMICOLON?;
declaration : param SEMICOLON;
record : RECORD IDENTIFIER declaration* END SEMICOLON;
param: (TYPE | IDENTIFIER) IDENTIFIER;
body : (assignment | procedurecall)* returnrule?;
assignment : IDENTIFIER ASSIGN CONSTVALUE SEMICOLON;
procedurecall : IDENTIFIER LPAREN (IDENTIFIER? | IDENTIFIER (COMMA IDENTIFIER)*) RPAREN SEMICOLON;
returnrule : RETURN SEMICOLON;
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
ASSIGN: '=';
AND : '&&';
OR : '||';

BEGIN : 'begin';
END : 'end';
BEGINBLOCK: '{';
ENDBLOCK: '}';
INDEXBEGIN : '[';
INDEXEND : ']';
LPAREN : '(';
RPAREN : ')';
COMMA : ',';
PERIOD : '.';
SEMICOLON : ';';
QUOTES : '"';

PROCEDURE : 'procedure';
RECORD : 'record';
IF : 'if';
ELSIF : 'elsif';
ELSE : 'else';
WHILE : 'while';
DO : 'do';
REF : 'ref';
RETURN : 'return';

TYPE : BASETYPE (INDEXBEGIN INDEXEND)?;
BASETYPE : (INT | BOOL | STRING);
INT: 'int';
BOOL: 'boolean';
STRING: 'string';

CONSTVALUE : (NUMBER | STRINGVALUE | BOOLVALUE);
IDENTIFIER : (LOWERCHAR | UPPERCHAR) (LOWERCHAR | UPPERCHAR | DIGIT)*;
ALLCHARS : DIGIT | LOWERCHAR | UPPERCHAR | PERIOD | WHITESPACE | COMMA | NEGATE;
DIGIT : [0-9];
NUMBER : (ADD | SUB)? DIGIT+;
STRINGVALUE: QUOTES ALLCHARS* QUOTES;
LOWERCHAR : 'a'..'z';
UPPERCHAR : 'A'..'Z';
BOOLVALUE : TRUE | FALSE;
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
