/* -*-Java-*- */

/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

/*
 * Janet grammar file for pure C (without embedded Java).
 */

%{                                                                                    
/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

/* THIS FILE HAS BEEN AUTOMATICALLY GENERATED by the public domain JB tool
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

%type<YYStatement> CStatement

%type<YYCBlock> CBlock
%type<YYCChunk> Goal CStatements

%start Start

%%

Start
    : { pushChunk(new YYCChunk(cxt)); } Outer
    ;

Outer             /* never reached */
    : Goal       {}
    | Goal error {}
    ;

Goal
    : CStatements             { $$ = $1.compact(); yyclearin(); yyreturn(YYRET_STATEMENTS); }
    | /* empty */             { $$ = null; yyclearin(); yyreturn(YYRET_EPSILON); }
    ;

CStatements_opt
    : /* empty */ {}
    | CStatements {}
    ;

CStatements
    : CStatement              { $$ = peekChunk().add($1.compact()); } 
    | CStatements CStatement  { $$ = $1.add($2.compact()); }
    ;

CStatement
    : CStatement CSTATEMENT_CHAR                { $$ = $1.expand(cxt); }
    | CTEXT                                     { $$ = new YYCChunk(cxt); }

    | '(' { pushChunk(new YYCChunk(cxt)); } CStatements_opt ')' { $$ = popChunk().expand(cxt); }
    | '[' { pushChunk(new YYCChunk(cxt)); } CStatements_opt ']' { $$ = popChunk().expand(cxt); }

    | CBlock                                    { $$ = $1; }
    ;

CBlock
    : '{'                      { YYCBlock b = new YYCBlock(cxt); pushChunk(b); cxt.pushScope(b); } 
          CStatements_opt 
      '}'                      { cxt.popScope(); $$ = popChunk().compact().expand(cxt); }
    ;

%%
    public int getInitialLexMode() { //int yymode) {
	return Lexer.NATIVE_STATEMENTS;
    }

    Stack chunks = new Stack();
    void pushChunk(YYCChunk c) { chunks.push(c); }
    YYCChunk popChunk() { return (YYCChunk)chunks.pop(); }
    YYCChunk peekChunk() { return (YYCChunk)chunks.peek(); }
