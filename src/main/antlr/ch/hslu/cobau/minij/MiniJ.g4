/*
 * Reference grammar for language "miniJ"
 */
grammar MiniJ;

@header {
package ch.hslu.cobau.minij;
}

///////////////////////////////////////////////////////////////////////////////
// Parsing rules
///////////////////////////////////////////////////////////////////////////////

// declaractions
unit        : member* EOF;
member      : declaration | record | procedure | SEMICOLON;

record      : RECORD identifier (declaration)* END;

// procedures and blocks
procedure     : PROCEDURE identifier LPAREN (parameter (COMMA parameter)*)? RPAREN declarations procedureBody;
parameter     : (REF)? type identifier;
declarations  : (declarationStatement)*;

procedureBody : LBRACE block RBRACE
              | BEGIN block END
              ;

block         : (statement)*;

// statements
declarationStatement : declaration | SEMICOLON;
statement            : assignment | callStatement | returnStatement | ifStatement | whileStatement | SEMICOLON;

assignment           : memoryAccess ASSIGN expression SEMICOLON;
callStatement        : identifier LPAREN (expression (COMMA expression)*)? RPAREN SEMICOLON;
whileStatement       : WHILE LPAREN expression RPAREN DO block END;
returnStatement      : RETURN SEMICOLON;

ifStatement          : IF LPAREN expression RPAREN THEN block (elsifClause)* (elseClause)? END;
elsifClause          : ELSIF LPAREN expression RPAREN THEN block;
elseClause           : ELSE block;

// expressions
expression : LPAREN expression RPAREN
           | memoryAccess (INCREMENT | DECREMENT)
           | unaryExpression
           | expression binaryOp=(TIMES | DIV | MOD) expression                          // NOTE: order is important.
           | expression binaryOp=(PLUS | MINUS) expression                               // In ANTLR order reflects
           | expression binaryOp=(EQUAL | UNEQUAL | AND | OR) expression                 // the associativity of the
           | expression binaryOp=(LESSER | GREATER | LESSER_EQ | GREATER_EQ) expression  // operations.
           | expression binaryOp=(EQUAL | UNEQUAL) expression                            // Thus, operator with highest
           | expression binaryOp=AND expression                                          // precendence MUST be listed
           | expression binaryOp=OR expression                                           // first.
           | trueConstant
           | falseConstant
           | integerConstant
           | stringConstant
           | memoryAccess
           ;

unaryExpression : unaryOp=(NOT | MINUS | PLUS | INCREMENT | DECREMENT) expression;
trueConstant    : TRUE;
falseConstant   : FALSE;
integerConstant : INTEGER;
stringConstant  : STRINGCONSTANT;
memoryAccess    : ID
                | memoryAccess DOT ID
                | memoryAccess LBRACKET expression RBRACKET
                ;

// types and identifier
declaration   : type identifier SEMICOLON;
type          : basicType | type LBRACKET RBRACKET;
basicType     : integerType | booleanType | stringType | recordType;
integerType   : INT;
stringType    : STRING;
booleanType   : BOOLEAN;
recordType    : identifier;

identifier    : ID;

///////////////////////////////////////////////////////////////////////////////
// Lexer rules
///////////////////////////////////////////////////////////////////////////////

// operators, blocks, arrays indexes, and parameter lists
LPAREN:        '(';
RPAREN:        ')';
LBRACE:        '{';
RBRACE:        '}';
LBRACKET:      '[';
RBRACKET:      ']';
SEMICOLON:     ';';
COMMA:         ',';
ASSIGN:        '=';
INCREMENT:     '++';
DECREMENT:     '--';
PLUS:          '+';
MINUS:         '-';
TIMES:         '*';
DIV:           '/';
MOD:           '%';
DOT:           '.';
EQUAL:         '==';
UNEQUAL:       '!=';
LESSER:        '<';
GREATER:       '>';
LESSER_EQ:     '<=';
GREATER_EQ:    '>=';
NOT:           '!';
AND:           '&&';
OR:            '||';


// declaraction
RECORD:       'record';
BEGIN:        'begin';
END:          'end';
PROCEDURE:    'procedure';
REF:          'ref';

// control flow
IF:           'if';
THEN:         'then';
ELSIF:        'elsif';
ELSE:         'else';
WHILE:        'while';
DO:           'do';
RETURN:       'return';

// types
INT:          'int';
BOOLEAN:      'boolean';
STRING:       'string';

// values
TRUE:           'true';
FALSE:          'false';
INTEGER:        ('+'|'-')?[0-9]+;
STRINGCONSTANT: '"' (~'"')* '"'; //

// identifiers: order is important as all other keywords have precendence
ID : [a-zA-Z][a-zA-Z0-9_$]*;

// comments
LINE_COMMENT: '//' ~[\r\n]* -> skip; // skip contents of line comments
BLOCKCOMMENT: '/*' .*? '*/' -> skip; // skip contents of block comments
WS:           [ \t\r\n]+    -> skip; // skip spaces, tabs, newlines