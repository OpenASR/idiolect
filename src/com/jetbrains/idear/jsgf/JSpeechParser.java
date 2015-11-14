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
    if (t == SELF_IDENT_HEADER) {
      r = SelfIdentHeader(b, 0);
    }
    else if (t == WEIGHT) {
      r = Weight(b, 0);
    }
    else if (t == DECLARATION) {
      r = declaration(b, 0);
    }
    else if (t == LITERAL) {
      r = literal(b, 0);
    }
    else if (t == RULE_ALTERNATIVE) {
      r = ruleAlternative(b, 0);
    }
    else if (t == RULE_DEFINITION) {
      r = ruleDefinition(b, 0);
    }
    else if (t == RULE_EXPANSION) {
      r = ruleExpansion(b, 0);
    }
    else if (t == RULENAME) {
      r = rulename(b, 0);
    }
    else if (t == SCOPE) {
      r = scope(b, 0);
    }
    else if (t == SEQUENCE_ELEMENT) {
      r = sequenceElement(b, 0);
    }
    else if (t == SUBEXPANSION) {
      r = subexpansion(b, 0);
    }
    else {
      r = parse_root_(t, b, 0);
    }
    exit_section_(b, 0, m, t, r, true, TRUE_CONDITION);
  }

  protected boolean parse_root_(IElementType t, PsiBuilder b, int l) {
    return grammar(b, l + 1);
  }

  /* ********************************************************** */
  // '#JSGF V1.0;'
  public static boolean SelfIdentHeader(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "SelfIdentHeader")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<self ident header>");
    r = consumeToken(b, "#JSGF V1.0;");
    exit_section_(b, l, m, SELF_IDENT_HEADER, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // '/' FLOAT '/'
  public static boolean Weight(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Weight")) return false;
    if (!nextTokenIs(b, SLASH)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, SLASH);
    r = r && consumeToken(b, FLOAT);
    r = r && consumeToken(b, SLASH);
    exit_section_(b, m, WEIGHT, r);
    return r;
  }

  /* ********************************************************** */
  // 'grammar' STRING ';'
  public static boolean declaration(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "declaration")) return false;
    if (!nextTokenIs(b, GRAMMAR)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, GRAMMAR);
    r = r && consumeToken(b, STRING);
    r = r && consumeToken(b, SEMICOLON);
    exit_section_(b, m, DECLARATION, r);
    return r;
  }

  /* ********************************************************** */
  // SelfIdentHeader declaration* ruleDefinition*
  static boolean grammar(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "grammar")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = SelfIdentHeader(b, l + 1);
    r = r && grammar_1(b, l + 1);
    r = r && grammar_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // declaration*
  private static boolean grammar_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "grammar_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!declaration(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "grammar_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // ruleDefinition*
  private static boolean grammar_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "grammar_2")) return false;
    int c = current_position_(b);
    while (true) {
      if (!ruleDefinition(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "grammar_2", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  /* ********************************************************** */
  // STRING
  public static boolean literal(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "literal")) return false;
    if (!nextTokenIs(b, STRING)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, STRING);
    exit_section_(b, m, LITERAL, r);
    return r;
  }

  /* ********************************************************** */
  // Weight? sequenceElement+
  public static boolean ruleAlternative(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ruleAlternative")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<rule alternative>");
    r = ruleAlternative_0(b, l + 1);
    r = r && ruleAlternative_1(b, l + 1);
    exit_section_(b, l, m, RULE_ALTERNATIVE, r, false, null);
    return r;
  }

  // Weight?
  private static boolean ruleAlternative_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ruleAlternative_0")) return false;
    Weight(b, l + 1);
    return true;
  }

  // sequenceElement+
  private static boolean ruleAlternative_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ruleAlternative_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = sequenceElement(b, l + 1);
    int c = current_position_(b);
    while (r) {
      if (!sequenceElement(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "ruleAlternative_1", c)) break;
      c = current_position_(b);
    }
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // scope? rulename '=' ruleExpansion ';'
  public static boolean ruleDefinition(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ruleDefinition")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<rule definition>");
    r = ruleDefinition_0(b, l + 1);
    r = r && rulename(b, l + 1);
    r = r && consumeToken(b, EQUALS);
    r = r && ruleExpansion(b, l + 1);
    r = r && consumeToken(b, SEMICOLON);
    exit_section_(b, l, m, RULE_DEFINITION, r, false, null);
    return r;
  }

  // scope?
  private static boolean ruleDefinition_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ruleDefinition_0")) return false;
    scope(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // ruleAlternative ( '|' ruleAlternative )*
  public static boolean ruleExpansion(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ruleExpansion")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<rule expansion>");
    r = ruleAlternative(b, l + 1);
    r = r && ruleExpansion_1(b, l + 1);
    exit_section_(b, l, m, RULE_EXPANSION, r, false, null);
    return r;
  }

  // ( '|' ruleAlternative )*
  private static boolean ruleExpansion_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ruleExpansion_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!ruleExpansion_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "ruleExpansion_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // '|' ruleAlternative
  private static boolean ruleExpansion_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ruleExpansion_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, OR);
    r = r && ruleAlternative(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // '<' STRING '>'
  public static boolean rulename(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "rulename")) return false;
    if (!nextTokenIs(b, BRACKET3)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, BRACKET3);
    r = r && consumeToken(b, STRING);
    r = r && consumeToken(b, BRACKET4);
    exit_section_(b, m, RULENAME, r);
    return r;
  }

  /* ********************************************************** */
  // 'private' | 'public'
  public static boolean scope(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "scope")) return false;
    if (!nextTokenIs(b, "<scope>", PRIVATE, PUBLIC)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<scope>");
    r = consumeToken(b, PRIVATE);
    if (!r) r = consumeToken(b, PUBLIC);
    exit_section_(b, l, m, SCOPE, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // subexpansion
  public static boolean sequenceElement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "sequenceElement")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<sequence element>");
    r = subexpansion(b, l + 1);
    exit_section_(b, l, m, SEQUENCE_ELEMENT, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // literal
  //     | rulename
  //     | '(' ')'
  //     | '(' ruleExpansion ')'
  //     | '[' ruleExpansion ']'
  public static boolean subexpansion(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "subexpansion")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<subexpansion>");
    r = literal(b, l + 1);
    if (!r) r = rulename(b, l + 1);
    if (!r) r = subexpansion_2(b, l + 1);
    if (!r) r = subexpansion_3(b, l + 1);
    if (!r) r = subexpansion_4(b, l + 1);
    exit_section_(b, l, m, SUBEXPANSION, r, false, null);
    return r;
  }

  // '(' ')'
  private static boolean subexpansion_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "subexpansion_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, PAREN1);
    r = r && consumeToken(b, PAREN2);
    exit_section_(b, m, null, r);
    return r;
  }

  // '(' ruleExpansion ')'
  private static boolean subexpansion_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "subexpansion_3")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, PAREN1);
    r = r && ruleExpansion(b, l + 1);
    r = r && consumeToken(b, PAREN2);
    exit_section_(b, m, null, r);
    return r;
  }

  // '[' ruleExpansion ']'
  private static boolean subexpansion_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "subexpansion_4")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, BRACKET1);
    r = r && ruleExpansion(b, l + 1);
    r = r && consumeToken(b, BRACKET2);
    exit_section_(b, m, null, r);
    return r;
  }

}
