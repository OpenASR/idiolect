// This is a generated file. Not intended for manual editing.
package com.jetbrains.idear.jsgf;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import static com.jetbrains.idear.jsgf.psi.JSpeechTypes.*;
import static com.intellij.lang.parser.GeneratedParserUtilBase.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import com.intellij.lang.PsiParser;
import com.intellij.lang.LightPsiParser;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class JSpeechParser implements PsiParser, LightPsiParser {

  public ASTNode parse(IElementType t, PsiBuilder b) {
    parseLight(t, b);
    return b.getTreeBuilt();
  }

  public void parseLight(IElementType t, PsiBuilder b) {
    boolean r;
    b = adapt_builder_(t, b, this, null);
    Marker m = enter_section_(b, 0, _COLLAPSE_, null);
    if (t == RULE) {
      r = rule(b, 0);
    }
    else if (t == TAG) {
      r = tag(b, 0);
    }
    else {
      r = parse_root_(t, b, 0);
    }
    exit_section_(b, 0, m, t, r, true, TRUE_CONDITION);
  }

  protected boolean parse_root_(IElementType t, PsiBuilder b, int l) {
    return jsgfile(b, l + 1);
  }

  /* ********************************************************** */
  // rule|COMMENT|CRLF
  static boolean definition(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "definition")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = rule(b, l + 1);
    if (!r) r = consumeToken(b, COMMENT);
    if (!r) r = consumeToken(b, CRLF);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // definition*
  static boolean jsgfile(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "jsgfile")) return false;
    int c = current_position_(b);
    while (true) {
      if (!definition(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "jsgfile", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  /* ********************************************************** */
  // tag '=' (tag|KEY)+ ';'
  public static boolean rule(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "rule")) return false;
    if (!nextTokenIs(b, BRACKET3)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = tag(b, l + 1);
    r = r && consumeToken(b, SEPARATOR);
    r = r && rule_2(b, l + 1);
    r = r && consumeToken(b, SEMICOLON);
    exit_section_(b, m, RULE, r);
    return r;
  }

  // (tag|KEY)+
  private static boolean rule_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "rule_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = rule_2_0(b, l + 1);
    int c = current_position_(b);
    while (r) {
      if (!rule_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "rule_2", c)) break;
      c = current_position_(b);
    }
    exit_section_(b, m, null, r);
    return r;
  }

  // tag|KEY
  private static boolean rule_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "rule_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = tag(b, l + 1);
    if (!r) r = consumeToken(b, KEY);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // '<' KEY '>'
  public static boolean tag(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "tag")) return false;
    if (!nextTokenIs(b, BRACKET3)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, BRACKET3);
    r = r && consumeToken(b, KEY);
    r = r && consumeToken(b, BRACKET4);
    exit_section_(b, m, TAG, r);
    return r;
  }

}
