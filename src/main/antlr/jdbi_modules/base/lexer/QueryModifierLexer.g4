lexer grammar QueryModifierLexer;

@header {
package jdbi_modules.base.lexer;

/**
 * @since 27.05.2019
 */
//CHECKSTYLE:OFF
}

/* Partly lifted from https://github.com/jdbi/jdbi/blob/master/core/src/main/antlr4/org/jdbi/v3/core/internal/lexer/ColonStatementLexer.g4 */
/* Partly lifted from https://github.com/jdbi/jdbi/blob/master/core/src/main/antlr4/org/jdbi/v3/core/internal/lexer/DefineStatementLexer.g4 */
fragment QUOTE: '\'';
fragment ESCAPE: '\\';
fragment ESCAPE_QUOTE: ESCAPE QUOTE;
fragment DOUBLE_QUOTE: '"';
fragment COLON: {_input.LA(2) != ':'}? ':';
fragment DOUBLE_COLON: {_input.LA(2) == ':'}? '::';
fragment QUESTION: {_input.LA(2) != '?'}? '?';
fragment DOUBLE_QUESTION: {_input.LA(2) == '?'}? '??';
fragment NAME: JAVA_LETTER | [0-9];
fragment LT: '<' ;
fragment GT: '>' ;

/* Lovingly lifted from https://github.com/antlr/grammars-v4/blob/master/java/JavaLexer.g4 */
fragment JAVA_LETTER : [a-zA-Z$_] | ~[\u0000-\u007F\uD800-\uDBFF] | [\uD800-\uDBFF] [\uDC00-\uDFFF];

COMMENT: '/*' .*? '*/';
QUOTED_TEXT: QUOTE (ESCAPE_QUOTE | ~'\'')* QUOTE;
DOUBLE_QUOTED_TEXT: DOUBLE_QUOTE (~'"')+ DOUBLE_QUOTE;
ESCAPED_TEXT : ESCAPE . ;

BINDING: COLON (NAME)+ -> pushMode(INSIDE_BINDING);
POSITIONAL_PARAM: QUESTION;
LITERAL: DOUBLE_COLON | DOUBLE_QUESTION | .;
DEFINITION: LT (NAME)+ GT;

// ----------------------- everything inside of a binding ---------------------
mode INSIDE_BINDING;

BINDING_FIELD_NAME: ('.' | '?.') (NAME+);
CLOSE:  -> popMode;