package com.jetbrains.idear.jsgf;
import com.intellij.lexer.*;
import com.intellij.psi.tree.IElementType;
import static com.jetbrains.idear.jsgf.psi.JSpeechTypes.*;

%%

%{
  public _JSpeechLexer() {
    this((java.io.Reader)null);
  }
%}

%public
%class _JSpeechLexer
%implements FlexLexer
%function advance
%type IElementType
%unicode

EOL="\r"|"\n"|"\r\n"
LINE_WS=[\ \t\f]
WHITE_SPACE=({LINE_WS}|{EOL})+

STRING=[A-Za-z_\-]+[a-zA-Z_0-9]*
NUMBER=(\+|\-)?[:digit:]*
FLOAT=(\+|\-)?[:digit:]*(\.[:digit:]+)?

%%
<YYINITIAL> {
  {WHITE_SPACE}      { return com.intellij.psi.TokenType.WHITE_SPACE; }

  ","                { return COMMA; }
  "."                { return PERIOD; }
  ":"                { return COLON; }
  ";"                { return SEMICOLON; }
  "="                { return EQUALS; }
  "{"                { return BRACE1; }
  "}"                { return BRACE2; }
  "("                { return PAREN1; }
  ")"                { return PAREN2; }
  "["                { return BRACKET1; }
  "]"                { return BRACKET2; }
  "<"                { return BRACKET3; }
  ">"                { return BRACKET4; }
  "!"                { return BANG; }
  "|"                { return OR; }
  "/"                { return SLASH; }
  "#"                { return HASH; }
  "public"           { return PUBLIC; }
  "private"          { return PRIVATE; }
  "grammar"          { return GRAMMAR; }
  "JSGF V1.0"        { return VERSION; }

  {STRING}           { return STRING; }
  {NUMBER}           { return NUMBER; }
  {FLOAT}            { return FLOAT; }

  [^] { return com.intellij.psi.TokenType.BAD_CHARACTER; }
}
