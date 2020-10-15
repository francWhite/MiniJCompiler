grammar MiniJ;

@header {
package ch.hslu.cobau.minij;
}

// milestone 2: parser
///////////////////////////////////////////////////////////////////////////////
// Parser-Regeln
///////////////////////////////////////////////////////////////////////////////
unit : program;
program : (procedure | declaration | record )* EOF;
procedure: PROCEDURE IDENTIFIER LPAREN (param? | param (COMMA param)*) RPAREN declaration* (BEGIN body END | BEGINBLOCK body ENDBLOCK) SEMICOLON?;
declaration : param SEMICOLON;
record : RECORD IDENTIFIER declaration* END SEMICOLON?;
param: REF? (TYPE | IDENTIFIER) (INDEXBEGIN INDEXEND)* IDENTIFIER;
body : (assignment | procedurecall | ifelse | whileblock)* returnrule?;
assignment : identifier ASSIGN expression SEMICOLON;
procedurecall : identifier LPAREN (callparam? | callparam (COMMA callparam)*) RPAREN SEMICOLON;
callparam: identifier | expression;
returnrule : RETURN SEMICOLON;
ifelse : IF LPAREN expression RPAREN THEN body (ELSIF LPAREN expression RPAREN THEN body)* (ELSE body)?  END SEMICOLON;
whileblock : WHILE LPAREN expression RPAREN DO body END SEMICOLON;
identifier: IDENTIFIER | identifier (PERIOD identifier)+ | identifier (INDEXBEGIN (NUMBER | identifier | expression) INDEXEND)+;

expression : identifier (INCREMENT | DECREMENT)
            | (INCREMENT | DECREMENT) expression
            | expression (MULT | DIV | MOD) expression
            | expression (ADD |SUB) expression
            | expression (LESSER | GREATER | LESSEREQ | GREATEREQ) expression
            | expression (EQUAL | NOTEQUAL) expression
            | expression (AND) expression
            | expression (OR) expression
            | LPAREN expression RPAREN
            | identifier
            | (SUB| NEGATE) expression
            | (NUMBER | STRINGVALUE | BOOLVALUE);//CONSTVALUE;

///////////////////////////////////////////////////////////////////////////////
// Scanner(Lexer)-Regeln
///////////////////////////////////////////////////////////////////////////////
COMMENT : '//' ALLCHARS* ([\r\n] | EOF) -> skip;
BLOCKCOMMENT : '/*' ALLCHARS* '*/' -> skip;
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
THEN : 'then';
WHILE : 'while';
DO : 'do';
REF : 'ref';
RETURN : 'return';

TYPE : BASETYPE (INDEXBEGIN INDEXEND)?;
BASETYPE : (INT | BOOL | STRING);
INT: 'int';
BOOL: 'boolean';
STRING: 'string';

NUMBER : (ADD | SUB)? DIGIT+;
DIGIT : [0-9];
IDENTIFIER : (LOWERCHAR | UPPERCHAR) (LOWERCHAR | UPPERCHAR | DIGIT)*;
ALLCHARS : DIGIT | LOWERCHAR | UPPERCHAR | PERIOD | WHITESPACE | COMMA | NEGATE | DIV;
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

BLOCKCOMMENTSTART : '/*';
BLOCKCOMMENTEND : '*/';
