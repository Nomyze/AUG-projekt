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

%state STRING

%%

<YYINITIAL>{
"echo"      { return symbol("echo", COMMAND, Constant.ECHO); }
"wc"        { return symbol("wc", COMMAND, Constant.WC); }
"cat"       { return symbol("cat", COMMAND, Constant.CAT); }

">"         { return symbol("greater-than sign", GTSIGN); }
">>"        { return symbol("double-greater-than sign", GTSIGN); }
\n          { return symbol("break", BREAK); }
";"         { return symbol("break", BREAK); }
{Argument}  { return symbol("argument", ARG, yytext()); }
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
