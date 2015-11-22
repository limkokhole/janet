/* -*-Java-*- */

package pl.edu.agh.icsr.janet.natives.c;

/**************************************************/

import java.util.*;
import java.io.*;
import jbf.Int_Stack;
import jbf.Object_Stack;
import jbf.YYlocation;

import pl.edu.agh.icsr.janet.YYLocation;
import pl.edu.agh.icsr.janet.YYLocationStack;
import pl.edu.agh.icsr.janet.natives.IParser;
import pl.edu.agh.icsr.janet.natives.YYNativeCode;
import pl.edu.agh.icsr.janet.VariableStack;
import pl.edu.agh.icsr.janet.reflect.ClassManager;

/**************************************************/


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



/* The template file is based on parser templates defined in JB and
 * (indirectly) bison, and is therefore subject to GPL license as shown
 * below. The JANET MPL 1.1 license do not apply in this case. Note that
 * the template file is not an integral part of JANET and does not affect
 * JANET licensing.
 */

/*
   This is a parser template file, in java, for running parsers
   generated by the GNU Bison parser generator.
*/

/* Skeleton output parser for bison,
   Copyright (C) 1984, 1989, 1990 Free Software Foundation, Inc.

   This program is free software; you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation; either version 2, or (at your option)
   any later version.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with this program; if not, write to the Free Software
   Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  */

/* As a special exception, when this file is copied by Bison into a
   Bison output file, you may use that output file without restriction.
   This special exception was added by the Free Software Foundation
   in version 1.24 of Bison.  */

/**************************************************/

public class BlockParser {

    // Set up the fake goto mechanism
    protected int Goto;
    protected static final int yynewstate = 0;
    protected static final int yybackup = 1;
    protected static final int yydefault = 2;
    protected static final int yyreduce = 3;
    protected static final int yyerrlab = 4;
    protected static final int yyerrlab1 = 5;
    protected static final int yyerrdefault = 6;
    protected static final int yyerrpop = 7;
    protected static final int yyerrhandle = 8;

    public static final int YYCONTINUE = 1;
    public static final int YYACCEPT = 0;
    public static final int YYABORT = -1;
    public static final int YYERROR = -2;

    public static final int YYRET_EPSILON              = IParser.YYRET_EPSILON;
    public static final int YYRET_STATEMENTS           = IParser.YYRET_STATEMENTS;
    public static final int YYRET_STATEMENTS_WITH_TAIL = IParser.YYRET_STATEMENTS_WITH_TAIL;
    public static final int YYRET_BLOCK                = IParser.YYRET_BLOCK;
    public static final int YYRET_EXPRESSION           = IParser.YYRET_EXPRESSION;
    public static final int YYRET_STRING               = IParser.YYRET_STRING;
    public static final int YYRET_UNICODE_STRING       = IParser.YYRET_UNICODE_STRING;


    /*  public static final int YYRET_EPSILON              = 0x0041;
    public static final int YYRET_STATEMENTS           = 0x0042;
    public static final int YYRET_STATEMENTS_WITH_TAIL = 0x0043;
    public static final int YYRET_BLOCK                = 0x0044;
    public static final int YYRET_EXPRESSION           = 0x0045;
    public static final int YYRET_STRING               = 0x0046;
    public static final int YYRET_UNICODE_STRING       = 0x0047;*/


    public static final int YYEOF = 0;

    protected static final int YYEMPTY = -2;
    protected static final int YYTERROR = 1;
    protected static final int YYTERRCODE = 256;

    protected static final char EOL = '\n';

// CONSTANTS

public static final int YYFINAL = 35;
public static final int YYFLAG = -32768;
public static final int YYNTBASE = 17;
public static final int YYLAST = 24;


    protected int yyreturn; // see ACTIONS switch
    protected int yystate;
    protected int yyn;
    protected int yylen;
    protected int yychar; /* the lookahead symbol */
    protected int yychar1;  /* lookahead token as an internal (translated)
                           Token number */
    protected int yynerrs; /* number of parse errors so far */
    protected int yydebug; /* nonzero means print parse trace */
    protected int yyerrstatus;  /* number of tokens to shift before error
                           messages enabled */
    public int yyshiftcount = 3; // default tokens to shift

    protected YYLocation loc;    // shared loc
    protected YYLocation yylbeg; // beg of current lexeme
    protected YYLocation yylend; // end of current lexeme

    protected IMutableContext outer_cxt;

    public YYLocation tmploc = new YYLocation(); // auxiliary

    public Object yylval; // the semantic value of the lookahead symbol
    protected Object yyval;   // the variable used to return semantic values
                                // from the action routines
    protected boolean yyerrthrow; /* t=>yyerror should throw ParseException;
                                     f=>just return and let normal error
                                        recovery operate; default is false;*/
    protected LexException yylexexception;

    protected Int_Stack yyss;
    protected Object_Stack yyvs;
    protected YYLocationStack yyls;

    protected Lexer yylex;
    protected java.io.PrintWriter yyerr;

    //protected int yymode;
    protected int lexmode;

    boolean isNowParsing; // check if recurrent invocation
    BlockParser nextParser; // if recurrent, new instance will be created

// PROCS

static final int YYTRANSLATE(int x)
	{ return ((x) <= 263 ? yytranslate[x] : 29);}


    // default constructor for newInstance()
    public BlockParser() {
        yyss = new Int_Stack();
        yyvs = new Object_Stack();
        yyls = new YYLocationStack();
        yylval = null;
        yylbeg = new YYLocation();
        yylend = new YYLocation();
        yydebug = 0;
        yyerrthrow = true; //false;
        yylexexception = null;
        isNowParsing = false;
        nextParser = null;
    }

    public BlockParser(Lexer yyl, java.io.PrintWriter ferr) {
        this();
        _init(yyl, ferr);
    }

    protected void _init(Lexer yyl, java.io.PrintWriter ferr) {
        yylex = yyl;
        yyerr = ferr;
        loc = yylex.loc();
    }

    public int yyparse(IMutableContext cxt, YYResultReceiver resrecv)//nt mode)
            throws ParseException {
        if (isNowParsing) { // recursive call; need another parser instance
            if (nextParser == null) {
                (nextParser = new BlockParser(yylex, yyerr)).setdebug(yydebug);
            }
            return nextParser.yyparse(cxt, resrecv);//, mode);
        } else {
            isNowParsing = true;
            this.outer_cxt = cxt;
            //this.yymode = mode;
            this.lexmode = getInitialLexMode();//yymode);
            yylbeg.copyFrom(loc);
            yylend.copyFrom(loc);
            if (yydebug > 0) yyprint("\n****** Running new embedded native parser (" + getClass().getName() + ") ******\n");
            int yyresult = _parse();
            if (yydebug > 0) yyprint("****** Embedded native parser finished    ******\n\n");

            resrecv.setYYlval(this.yyval);
            isNowParsing = false;
            return yyresult;
        }
    }

    int _parse() throws ParseException {
        int ok;
        yychar1 = 0;  /* lookahead Token as an internal (translated) */
        yystate = 0;
        yyerrstatus = 0;
        yynerrs = 0;
        yychar = YYEMPTY;  /* Cause a Token to be read.  */
        yyval = null;
        Goto = yynewstate;

        /* Initialize stack pointers.
           Waste one element of value and location stack
           so that they stay on the same level as the state stack.
           The wasted elements are never initialized.  */

        yyss.clear();
        yyvs.clear();
        yyls.clear();

        /* need to push a null value to match state 0 */
        yyvs.push(null);

        if (yydebug > 0) yyprint("Starting parse\n");

        /* Push a new state, which is found in  yystate  .  */
        /* In all cases, when you get here, the value and location stacks
           have just been pushed. so pushing a state here evens the stacks.  */
        for(;;) {
        branch: switch (Goto) {

        case yynewstate:

            yyss.push(yystate);

            if (yydebug > 0) {
                yyprint("Entering state ");
                yyprint(yystate);
                yyprint("\n");
            }

        case yybackup:

            /* Do appropriate processing given the current state.  */
            /* Read a lookahead token if we need one and don't already have one.  */
            /* yyresume: */

            /* First try to decide what to do without reference to lookahead token.  */

            yyn = yypact[yystate];
            if (yyn == YYFLAG) {
                Goto = yydefault;
                break branch;
            }

            /* Not known => get a lookahead token if don't already have one.  */

            /* yychar is either YYEMPTY or YYEOF
               or a valid token in external form.        */

            if (yychar == YYEMPTY) {
                if (yydebug > 0) yyprint("Reading a token: ");
                try {
                    // yylval = yylex.yylex();
                    yychar = yylex.yylex(cxt, this.lexmode);
                    yylval = yylex.yylval();
                } catch (LexException le) {
                    yyprintln("yyparse: LexException: " + le);
                    yylexexception = le;
                    // on lex error, treat like a syntax error
                    Goto = yyerrlab; break branch;
                }
            }

            /* Convert token to internal form (in yychar1) for indexing tables with */

            if (yychar <= 0) {          /* This means end of input. */
                yychar1 = 0;
                yychar = YYEOF;         /* Don't call YYLEX any more */
                if (yydebug > 0) yyprint("Now at end of input.\n");
            } else {
                yychar1 = YYTRANSLATE(yychar);
                if (yydebug >= 10) {
                    yyprint("Next token is ");
                    yyprint(yychar);
                    yyprint(" (");
                    yyprint(yytname[yychar1]);
                    yyprint(")\n");
                }
            }

            yyn += yychar1;
            if (yyn < 0 || yyn > YYLAST || yycheck[yyn] != yychar1) {
                Goto = yydefault; break branch;
            }

            yyn = yytable[yyn];

            /* yyn is what to do for this token type in this state.
               Negative => reduce, -yyn is rule number.
               Positive => shift, yyn is new state.
               New state is final state => don't bother to shift,
               just return success.
               0, or most negative number => error.  */

            if (yyn < 0) {
                if (yyn == YYFLAG) {
                    Goto = yyerrlab; break branch;
                }
                yyn = -yyn;
                Goto = yyreduce; break branch;
            } else if (yyn == 0) {
                Goto = yyerrlab; break branch;
            }

            if (yyn == YYFINAL) return YYACCEPT;

            /* Shift the lookahead token.        */

            if (yydebug > 0) {
                yyprint("Shifting token ");
                yyprint(yychar);
                yyprint(" (");
                yyprint(yytname[yychar1]);
                yyprint("), ");
            }

            /* Discard the token being shifted unless it is eof.        */
            if (yychar != YYEOF) yychar = YYEMPTY;

            yyvs.push(yylval);
            yylend.copyFrom(loc);
            yyls.push(yylex.tokenloc());
            yylbeg.copyFrom(yylex.loc());

            /* count tokens shifted since error; after three, turn off error status.  */
            if (yyerrstatus > 0) yyerrstatus--;

            yystate = yyn;
            Goto = yynewstate; break branch;

            /* Do the default action for the current state.      */
        case yydefault:

            yyn = yydefact[yystate];
            if (yyn == 0) {
                Goto = yyerrlab; break branch;
            }

            /* Do a reduction.  yyn is the number of a rule to reduce with.      */
        case yyreduce:
            yylen = yyr2[yyn];
            if (yylen > 0) {
                yyval = yyvs.tth(1-yylen); /* implement default value of the action */
                yylbeg = yyls.tth(1-yylen, yylbeg); /* COPY to yylbeg - DK */
            }
            if (yydebug > 0) {
                int i;
                lstackdump("location stack now"); /* DK */
                yyprint("Reducing via rule ");
                yyprint(yyn);
                yyprint(" (line ");
                yyprint(yyrline[yyn]);
                yyprint("), ");
                /* Print the symbols being reduced, and their result.  */
                for (i = yyprhs[yyn]; yyrhs[i] > 0; i++) {
                    yyprint(yytname[yyrhs[i]]);
                    yyprint(" ");
                }
                yyprint(" -> ");
                yyprint(yytname[yyr1[yyn]]);
                yyprint(" " + lbeg() + " <--> " + lend()); /* DK */
                yyprint("\n");
            }

            /*$*/       /* the action file gets copied in in place of this dollarsign */

            /* Provide a way to avoid having return statements in the actions
               and so avoid "statement not reached" errors"
            */
            yyreturn = YYCONTINUE;

            switch (yyn) {

case 1:
{;
    break;}
case 2:
{;
    break;}
case 3:
{ yyval = ((YYCBlock)(yyvs.tth(0))); yyclearin(); yyreturn(YYRET_BLOCK); ;
    break;}
case 4:
{ yyval = null; yyclearin(); yyreturn(YYRET_EPSILON); ;
    break;}
case 5:
{ YYCBlock b = new YYCBlock(cxt); b.markBeg(cxt.lend());
                                 pushChunk(b); cxt.pushScope(b); ;
    break;}
case 6:
{ ((YYCBlock)peekChunk()).markEnd(cxt.lend()); ;
    break;}
case 7:
{ cxt.popScope(); yyval = popChunk().compact().expand(cxt); ;
    break;}
case 8:
{;
    break;}
case 9:
{;
    break;}
case 10:
{ yyval = peekChunk().add(((YYStatement)(yyvs.tth(0))).compact()); ;
    break;}
case 11:
{ yyval = ((YYCChunk)(yyvs.tth(-1))).add(((YYStatement)(yyvs.tth(0))).compact()); ;
    break;}
case 12:
{ yyval = ((YYStatement)(yyvs.tth(-1))).expand(cxt); ;
    break;}
case 13:
{ yyval = new YYCChunk(cxt); ;
    break;}
case 14:
{ yyval = ((YYExpression)(yyvs.tth(-2))).expand(cxt); ;
    break;}
case 15:
{ yyval = ((YYStatement)(yyvs.tth(-2))).expand(cxt); ;
    break;}
case 16:
{ yyval = ((YYStatement)(yyvs.tth(-1))).expand(cxt); ;
    break;}
case 17:
{ pushChunk(new YYCChunk(cxt)); ;
    break;}
case 18:
{ yyval = popChunk().expand(cxt); ;
    break;}
case 19:
{ pushChunk(new YYCChunk(cxt)); ;
    break;}
case 20:
{ yyval = popChunk().expand(cxt); ;
    break;}
case 21:
{ yyval = ((YYCBlock)(yyvs.tth(0))); ;
    break;}
case 22:
{ lexmode = Lexer.JAVA_STATEMENTS; ;
    break;}
case 23:
{ lexmode = Lexer.NATIVE_STATEMENTS; ;
    break;}
}



            if(yyreturn == YYERROR) {
                Goto = yyerrlab1; break branch;
            } else if(yyreturn != YYCONTINUE) { yylex.skipWhites(); return yyreturn; }

            yyvs.popn(yylen);
            yyss.popn(yylen);
            /* yyls.popn(yylen); */

            yyvs.push(yyval);
            /*  yylsp++;*/
            if (yylen == 0) {
                yyls.push(yylbeg);
            } else {
                yyls.popn(yylen-1);
            }

            if (yydebug > 0) {
                stackdump("state stack now");
                lstackdump("location stack now");
            }

            /* Now "shift" the result of the reduction.
               Determine what state that goes to,
               based on the state we popped back to
               and the rule number reduced by.  */

            yyn = yyr1[yyn];

            yystate = yypgoto[yyn - YYNTBASE] + yyss.peek();
            if (yystate >= 0 && yystate <= YYLAST && yycheck[yystate] == yyss.peek()) {
                yystate = yytable[yystate];
            } else {
                yystate = yydefgoto[yyn - YYNTBASE];
            }

            Goto = yynewstate; break branch;

        case yyerrlab:   /* here on detecting error */

            if (yyerrstatus == 0) {
                /* If not already recovering from an error, report this error.  */
                ++yynerrs;
                parseError("parse error");
            }

        case yyerrlab1:   /* here on error raised explicitly by an action */

            if (yyerrstatus == yyshiftcount) {
                /* if just tried and failed to reuse lookahead token
                   after an error, discard it.  */
                /* return failure if at end of input */
                if (yychar == YYEOF) {
                    if(yydebug > 0)
                        yyprint("EOF during error recovery; aborting.");
                    return YYABORT;
                }

                if (yydebug > 0) {
                    yyprint("Discarding token ");
                    yyprint(yychar);
                    yyprint(" (");
                    yyprint(yytname[yychar1]);
                    yyprint(").\n");
                }
                yychar = YYEMPTY;
            }

            /* Else will try to reuse lookahead token
               after shifting the error token.  */

            yyerrstatus = yyshiftcount; /* Each real token shifted decrements this */

            Goto = yyerrhandle; break branch;

        case yyerrdefault:  /* state does notthing special for the error token. */
        case yyerrpop:   /* pop the current state because it cannot handle the error token */

            yyvs.popn(1);
            yyss.pop();
            if (yyss.empty()) {
                if(yydebug > 0)
                    yyprint("Empty stack during error recovery; aborting.");
                return YYABORT;
            }
            yystate = yyss.top();
            yyls.popn(1);

            if (yydebug > 0) {
                stackdump("Error: state stack now");
                lstackdump("Error: location stack now");
            }

        case yyerrhandle:

            yyn = yypact[yystate];
            if (yyn == YYFLAG) {
                Goto = yyerrdefault; break branch;
            }

            yyn += YYTERROR;
            if (yyn < 0 || yyn > YYLAST || yycheck[yyn] != YYTERROR) {
                Goto = yyerrdefault; break branch;
            }

            yyn = yytable[yyn];
            if (yyn < 0) {
                if (yyn == YYFLAG) {
                    Goto = yyerrpop; break branch;
                }
                yyn = -yyn;
                Goto = yyreduce; break branch;
            } else if (yyn == 0) {
                Goto = yyerrpop; break branch;
            }
            if (yyn == YYFINAL) return YYACCEPT;

            if (yydebug > 0) {
                yyprint("Shifting error token, ");
            }

            yyvs.push(yylval);
            yylend.copyFrom(loc);
            yyls.push(yylex.tokenloc());
            yylbeg.copyFrom(yylex.loc());

            yystate = yyn;
            Goto = yynewstate; break branch;

        }//switch
        }//for(;;)

    } // parse

    public void yyclearin() {
        if (yychar != YYEMPTY) {
            loc.copyFrom(yylend);
            yylval = null;
            yychar = YYEMPTY;
        }
    }

    public String yyerror_verbose(String msg0) throws ParseException {
        yyn = yypact[yystate];
        if(yyn > YYFLAG && yyn < YYLAST) {
            int size = msg0.length();
            StringBuffer msg;
            int yychar1; // a TRANSLATEd char value
            int count;
            count = 0;

            /* Start yychar1 at -yyn if nec to avoid negative indexes in yycheck.*/
            for (yychar1 = (yyn < 0 ? -yyn : 0);
                 (yyn+yychar1) < (yytname.length);
                 yychar1++) {
                if((yyn+yychar1) < yycheck.length
                   && yycheck[yyn+yychar1] == yychar1) {
                    size += (yytname[yychar1].length()) + 15;
                    count++;
                }
            }
            msg = new StringBuffer(size+15);
            msg.append(msg0);
            msg.append("(state " + Integer.toString(yystate) + ")");
            yychar1 = (yyn < 0)? - yyn :0;
            for(count=0,yychar1 = (yyn < 0 ? -yyn : 0);
                (yyn+yychar1) < (yytname.length) && count < 5;
                yychar1++) {
                if((yyn+yychar1) < yycheck.length
                   && yycheck[yyn+yychar1] == yychar1) {
                    msg.append(count == 0 ? ", expecting " : " or ");
                    msg.append(yytname[yychar1]);
                    count++;
                }
            }
            return msg.toString();
        }
        return msg0;
    }

    public void yyerror(String msg, boolean verbose, boolean dumpstack)
            throws ParseException {
        String s;
        if(verbose) {
            s = yyerror_verbose(msg);
        } else {
            s = msg;
        }
        yyprint("yyerror: ");
        if(yychar == YYEOF) {
            yyprint("at end of file");
            yyprintln(" ; " + s);
        } else {
            YYlocation loc = yylex.tokenloc();
            yyprint("line ");
            yyprint(loc.lineno()+1);
            yyprint(" char ");
            int cno = loc.charno();
            yyprint(cno+1);
            yyprintln(" token=|"+yytext()+"| ; "+ s);
            String line = yyline();
            if(line != null) {
                if(cno >= line.length()) cno = line.length();
                if(line.charAt(line.length()-1) == EOL)
                    yyprint(line);
                else
                    yyprintln(line);

                // for(int i=0;i<cno;i++) yyprint(" ");
                /* Modified by Dawid Kurzyniec, February 2000 */
                for (int i=0; i<cno; i++) {
                    char c = line.charAt(i);
                    if (Character.isWhitespace(c)) // e.g. \t
                        yyprint("" + c);
                    else
                        yyprint(" ");
                }

                yyprintln("^");
            }
        }
        if(dumpstack) { stackdump("yyerror: state stack"); }
        if(yyerrthrow) throw new ParseException(s);
    }

    public void yyerror(String s, boolean verbose) throws ParseException {
        yyerror(s, verbose, false);
    }

    public void yyerror(String s) throws ParseException {
        yyerror(s, false, false);
    }

    public void yyreturn(int t) { yyreturn = t; }

    public void YYERROR() { yyreturn(YYERROR); }
    public void yyerrok() { yyerrstatus = 0; }
    public boolean YYRECOVERING() { return (yyerrstatus != 0); }

    public void setdebug(boolean i) { yydebug = i ? 1:0; }
    public void setdebug(int i) { yydebug = i > 0 ? i:0; }
    public void seterrthrow(boolean b) { yyerrthrow = b; }

    protected void stackdump(String s) {
        int i;
        int x;
        int ssp1 = yyss.depth();
        yyprint(s);
        if(ssp1 == 0) {
            yyprint("<empty>");
        } else {
            for(i=0;i<ssp1;i++) {
                x = yyss.ith(i);
                yyprint(" ");
                yyprint(x);
            }
        }
        yyprint("\n");
    }

    protected void lstackdump(String s) {
        int i;
        int lsp1 = yyls.size();
        yyprint(s);
        if(lsp1 == 0) {
            yyprint("<empty>");
        } else {
            for(i=0;i<lsp1;i++) {
                YYLocation x = yyls.ith(i, tmploc);
                yyprint("  ");
                yyprint("" + x);
            }
            yyprint("  <-->  " + yylend);
        }
        yyprint("\n");
    }

    public String yyline() { return yylex.yyline(); }

    public StringBuffer yytext() { return yylex.yytext(); }

    //    public YYlocation currentloc() { return yylex.currentloc(); }
    //    public YYlocation tokenloc() { return yylex.tokenloc(); }

    public final pl.edu.agh.icsr.janet.YYLocation loc() { return loc; }
    public final pl.edu.agh.icsr.janet.YYLocation lbeg() { return yylbeg; }
    public final pl.edu.agh.icsr.janet.YYLocation lend() { return yylend; }
    public final pl.edu.agh.icsr.janet.YYLocation loc(int pos) { return yyls.tth(pos-yylen); }

    protected void yyprint(Object x) { yyerr.print(x); yyerr.flush(); }
    protected void yyprintln(Object x) { yyerr.println(x); yyerr.flush(); }
    protected void yyprint(int x) { yyerr.print(x); yyerr.flush(); }
    protected void yyprintln(int x) { yyerr.println(x); yyerr.flush(); }

    IMutableContext cxt = new IMutableContext() {
            public final YYLocation lbeg() { return BlockParser.this.lbeg(); }
            public final YYLocation lend() { return BlockParser.this.lend(); }
            public final JanetSourceReader ibuf() { return outer_cxt.ibuf(); }

            public final void reportError(String msg) throws CompileException { pl.edu.agh.icsr.janet.Parser.reportError(this, msg); }

            public final ClassManager getClassManager() { return outer_cxt.getClassManager(); }
            public final YYCompilationUnit getCompilationUnit() { return outer_cxt.getCompilationUnit(); }
            public final IScope getScope() { return outer_cxt.getScope(); }
            public VariableStack getVariables() { return outer_cxt.getVariables(); }

            public void pushScope(IScope unit) { outer_cxt.pushScope(unit); }
            public IScope popScope() { return outer_cxt.popScope(); }
            public void addVariable(YYVariableDeclarator var) throws CompileException { outer_cxt.addVariable(var); }
            public void addVariables(YYVariableDeclaratorList vars) throws CompileException { outer_cxt.addVariables(vars); }
        };

    ILocationContext tokencxt = new ILocationContext() {
            public final YYLocation lbeg() { return yylex.tokenloc(); }
            public final YYLocation lend() { return cxt.lend(); }
            public final JanetSourceReader ibuf() { return cxt.ibuf(); }
            public final void reportError(String msg) throws CompileException { throw new UnsupportedOperationException(); }
        };

    public void parseError(String msg) throws ParseException {
        String desc;
        if (yychar == YYEOF) {
            desc = "unexpected end of file";
        } else {
            desc = "parse error: '" + yytext() + "'";
        }
        if (msg != null && msg != "parse error") desc += "; " + msg;
        pl.edu.agh.icsr.janet.Parser.reportError(tokencxt, desc);
    }

// TABLES

public static final int yytranslate[] = yytranslateTableCreator();

private static final int[] yytranslateTableCreator() {
  final int yytranslate[] = {
     0,
     2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
     2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
     2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
     2,     2,     2,     2,     2,     2,     2,     2,     2,    13,
    14,     2,     2,     2,     2,     2,     2,     2,     2,     2,
     2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
     2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
     2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
     2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
    15,     2,    16,     2,     2,    12,     2,     2,     2,     2,
     2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
     2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
     2,     2,    10,     2,    11,     2,     2,     2,     2,     2,
     2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
     2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
     2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
     2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
     2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
     2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
     2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
     2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
     2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
     2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
     2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
     2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
     2,     2,     2,     2,     2,     1,     3,     4,     5,     6,
     7,     8,     9

  };
  return yytranslate;
}

public static final int yyprhs[] = yyprhsTableCreator();

private static final int[] yyprhsTableCreator() {
  final int yyprhs[] = {
     0,
     0,     2,     5,     7,     8,     9,    10,    16,    17,    19,
    21,    24,    27,    29,    35,    41,    46,    47,    52,    53,
    58,    60,    61

  };
  return yyprhs;
}

public static final int yyrhs[] = yyrhsTableCreator();

private static final int[] yyrhsTableCreator() {
  final int yyrhs[] = {
    18,
     0,    18,     1,     0,    19,     0,     0,     0,     0,    10,
    20,    22,    21,    11,     0,     0,    23,     0,    24,     0,
    23,    24,     0,    24,     6,     0,     5,     0,    12,    27,
     7,    28,    12,     0,    12,    27,     8,    28,    12,     0,
    12,    27,     9,    28,     0,     0,    13,    25,    22,    14,
     0,     0,    15,    26,    22,    16,     0,    19,     0,     0,
     0

  };
  return yyrhs;
}

public static final int yyrline[] = yyrlineTableCreator();

private static final int[] yyrlineTableCreator() {
  final int yyrline[] = {
 0,
    53,    54,    58,    59,    63,    65,    66,    69,    70,    74,
    75,    79,    80,    81,    82,    83,    85,    85,    86,    86,
    88,    92,    96

  };
  return yyrline;
}

public static final int yyr1[] = yyr1TableCreator();

private static final int[] yyr1TableCreator() {
  final int yyr1[] = {
     0,
    17,    17,    18,    18,    20,    21,    19,    22,    22,    23,
    23,    24,    24,    24,    24,    24,    25,    24,    26,    24,
    24,    27,    28

  };
  return yyr1;
}

public static final int yyr2[] = yyr2TableCreator();

private static final int[] yyr2TableCreator() {
  final int yyr2[] = {
     0,
     1,     2,     1,     0,     0,     0,     5,     0,     1,     1,
     2,     2,     1,     5,     5,     4,     0,     4,     0,     4,
     1,     0,     0

  };
  return yyr2;
}

public static final int yydefact[] = yydefactTableCreator();

private static final int[] yydefactTableCreator() {
  final int yydefact[] = {
     4,
     5,     0,     3,     8,     2,    13,    22,    17,    19,    21,
     6,     9,    10,     0,     8,     8,     0,    11,    12,    23,
    23,    23,     0,     0,     7,     0,     0,    16,    18,    20,
    14,    15,     0,     0,     0

  };
  return yydefact;
}

public static final int yydefgoto[] = yydefgotoTableCreator();

private static final int[] yydefgotoTableCreator() {
  final int yydefgoto[] = {
    33,
     2,    10,     4,    17,    11,    12,    13,    15,    16,    14,
    26

  };
  return yydefgoto;
}

public static final int yypact[] = yypactTableCreator();

private static final int[] yypactTableCreator() {
  final int yypact[] = {
    -1,
-32768,    11,-32768,    -5,-32768,-32768,-32768,-32768,-32768,-32768,
-32768,    -5,     0,    -6,    -5,    -5,    -7,     0,-32768,-32768,
-32768,-32768,     1,     3,-32768,     4,     8,-32768,-32768,-32768,
-32768,-32768,    21,    22,-32768

  };
  return yypact;
}

public static final int yypgoto[] = yypgotoTableCreator();

private static final int[] yypgotoTableCreator() {
  final int yypgoto[] = {
-32768,
-32768,    23,-32768,-32768,    -2,-32768,    12,-32768,-32768,-32768,
    -4

  };
  return yypgoto;
}

public static final int yytable[] = yytableTableCreator();

private static final int[] yytableTableCreator() {
  final int yytable[] = {
     6,
    20,    21,    22,    25,     1,    19,     7,     8,     1,     9,
    -1,     5,    23,    24,    29,    31,    27,    28,    30,    32,
    34,    35,     3,    18

  };
  return yytable;
}

public static final int yycheck[] = yycheckTableCreator();

private static final int[] yycheckTableCreator() {
  final int yycheck[] = {
     5,
     7,     8,     9,    11,    10,     6,    12,    13,    10,    15,
     0,     1,    15,    16,    14,    12,    21,    22,    16,    12,
     0,     0,     0,    12

  };
  return yycheck;
}



// SUFFIX


    public int getInitialLexMode() {
        return Lexer.NATIVE_STATEMENTS;
    }

    Stack<YYCChunk> chunks = new Stack<YYCChunk>();
    void pushChunk(YYCChunk c) { chunks.push(c); }
    YYCChunk popChunk() { return chunks.pop(); }
    YYCChunk peekChunk() { return chunks.peek(); }


    static final String[] yytname = TokenTypes.yytname;

}; /*class*/
