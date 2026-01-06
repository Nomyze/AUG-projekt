import java_cup.runtime.*;
import java_cup.runtime.ComplexSymbolFactory.*;

%%

%public
%class Lexer
%cup
%implements sym
%char
%line
%column
%{
    StringBuffer string = new StringBuffer();
    ComplexSymbolFactory symbolFactory;
    public Lexer(java.io.Reader in, ComplexSymbolFactory sf) {
        this(in);
        symbolFactory = sf;
    }
    private Symbol symbol(String name, int sym) {
        return symbolFactory.newSymbol(name, sym, 
            new Location(yyline+1,yycolumn+1,(int)yychar),
            new Location(yyline+1,yycolumn+yylength(),(int)yychar+yylength()));
    }
    private Symbol symbol(String name, int sym, Object val) {
        return symbolFactory.newSymbol(name, sym, 
            new Location(yyline+1,yycolumn+1,(int)yychar),
            new Location(yyline+1,yycolumn+yylength(),(int)yychar+yylength()), val);
    }
    private Symbol symbol(String name, int sym, Object val, int bufLen) {
        return symbolFactory.newSymbol(name, sym, 
            new Location(yyline+1,yycolumn+yylength()-bufLen,(int)yychar+yylength()-bufLen),
            new Location(yyline+1,yycolumn+yylength(),(int)yychar+yylength()), val);
    }
    private void error(String msg) {
        System.out.println("Error at line " + (yyline+1) + ", column " + (yycolumn+1) + " : " + msg);
    }
%}

%eofval{
    return symbolFactory.newSymbol("EOF", EOF, new Location(yyline+1, yycolumn+1,(int)yychar), new Location(yyline+1, yycolumn+1, (int)yychar+1));
%eofval}

Argument = [0-9a-zA-Z.\-_]+
Argument_quote = \"(\\.|[^\"\\])*\"

%state STRING

%%

<YYINITIAL>{
"echo"      { return symbol("echo", COMMAND, Constant.ECHO); }
"wc"        { return symbol("wc", COMMAND, Constant.WC); }
"cat"       { return symbol("cat", COMMAND, Constant.CAT); }
"grep"      { return symbol("grep", COMMAND, Constant.GREP); }

\|         { return symbol("pipe", PIPE); }
">>"        { return symbol("double-greater-than sign", DGTSIGN); }
">"         { return symbol("greater-than sign", GTSIGN); }
"\n"        { return symbol("break", BREAK); }
";"         { return symbol("break", BREAK); }
\#.*\n     { return symbol("comment", COMMENT); }
{Argument}  { return symbol("argument", ARG, yytext()); }
"$?"        { return symbol("argument", ARG, yytext()); }
{Argument_quote}  { return symbol("argument", ARG, yytext().substring(1, yytext().length() - 1).replace("\\\\", "\\").replace("\\\"", "\"")); }
\s			{ /* nic */ }

}

<STRING>{
\"          { yybegin(YYINITIAL);
              return symbol("StringConst", STRINGCONST, string.toString(), string.length()); }
[^\n\r\"\\]+ { string.append(yytext()); }
\\t         { string.append('\t'); }
\\n         { string.append('\n'); }
\\r         { string.append('\r'); }
\\\"        { string.append('\"'); }
\\          { string.append('\\'); }
}

[^]         { throw new Error("Illegal character <" + yytext() + ">"); }
