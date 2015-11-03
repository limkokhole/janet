/* -*-Java-*- */
 
/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

/*
 * Janet grammar file for embedded C expressions.
 */

%{                                                                                    
/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

/* Important information:
 * THIS FILE HAS BEEN AUTOMATICALLY GENERATED by the public domain JB tool
 * (see README.html for details).
 */

import pl.edu.agh.icsr.janet.*;
import pl.edu.agh.icsr.janet.LexException;
import pl.edu.agh.icsr.janet.ParseException;
import pl.edu.agh.icsr.janet.yytree.*;
%}

%union {
    YYCBlock;
    YYCChunk;

    YYExpression;
    YYStatement;

    String;
}

%token LEX_ERROR
%token EPSILON

%token<String> CTEXT CSTATEMENT_CHAR
%token<YYExpression> JAVA_EXPRESSION
%token<YYStatement> JAVA_STATEMENTS JAVA_ENCLOSED_STATEMENTS

%type<YYStatement> CExpressionElem
%type<YYCChunk> Goal CExpression

%start Outer

%%

Outer             /* never reached */
    : Goal       {}
    | Goal error {}
    ;

Goal
    : CExpression { $$ = $1; yyclearin(); yyreturn(YYRET_EXPRESSION); }
//    | error       { result = null; yyreturn(YYACCEPT); }
    | /* empty */ { $$ = null; yyclearin(); yyreturn(YYRET_EPSILON); } /* epsilon-production */
    ; 

CExpression
    : CExpressionElem             { $$ = new YYCChunk(cxt).add($1.compact()); }
    | CExpression CExpressionElem { $$ = $1.add($2.compact()); }
    ;

CExpressionElem
    : CTEXT                                     { $$ = new YYCChunk(cxt); }
    | '`' JavaBegin JAVA_EXPRESSION JavaEnd '`' { $$ = $3.expand(cxt); }
    | '(' CExpression ')'                       { $$ = $2.expand(cxt); }
    | '[' CExpression ']'                       { $$ = $2.expand(cxt); }
    ;

JavaBegin
    : { lexmode = Lexer.JAVA_EXPRESSION; }
    ;

JavaEnd
    : { lexmode = Lexer.NATIVE_EXPRESSION; }
    ;

%%

    public int getInitialLexMode() { //int yymode) {
	return Lexer.NATIVE_EXPRESSION;
    }
