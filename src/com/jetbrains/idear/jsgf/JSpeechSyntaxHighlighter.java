package com.jetbrains.idear.jsgf;

/**
 * Created by breandan on 11/13/2015.
 */

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.containers.ContainerUtil;
import com.jetbrains.idear.jsgf.psi.JSpeechTypes;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;

public class JSpeechSyntaxHighlighter extends SyntaxHighlighterBase {
    public static final Map<String, TextAttributesKey> KEYS = new HashMap<>();

    public static final TextAttributesKey SEPARATOR = createTextAttributesKey("SEPARATOR", DefaultLanguageHighlighterColors.OPERATION_SIGN);
    public static final TextAttributesKey RULE = createTextAttributesKey("RULE", DefaultLanguageHighlighterColors.KEYWORD);
    public static final TextAttributesKey LITERAL = createTextAttributesKey("LITERAL", DefaultLanguageHighlighterColors.STRING);
    public static final TextAttributesKey COMMENT = createTextAttributesKey("COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT);
    public static final TextAttributesKey BAD_CHARACTER = createTextAttributesKey("BAD_CHARACTER", HighlighterColors.BAD_CHARACTER);

    private static final TextAttributesKey[] BAD_CHAR_KEYS = new TextAttributesKey[]{BAD_CHARACTER};
    private static final TextAttributesKey[] SEPARATOR_KEYS = new TextAttributesKey[]{SEPARATOR};
    private static final TextAttributesKey[] KEY_KEYS = new TextAttributesKey[]{RULE};
    private static final TextAttributesKey[] VALUE_KEYS = new TextAttributesKey[]{LITERAL};
    private static final TextAttributesKey[] EMPTY_KEYS = new TextAttributesKey[0];

    static {
        new ContainerUtil.ImmutableMapBuilder<String, TextAttributesKey>()
                .put("KEYWORD", DefaultLanguageHighlighterColors.KEYWORD)
                .put("OPERATION_NAME", DefaultLanguageHighlighterColors.CLASS_NAME)
                .put("TYPE_REF", DefaultLanguageHighlighterColors.CLASS_REFERENCE)
                .put("FIELD_NAME", DefaultLanguageHighlighterColors.INSTANCE_FIELD)
                .put("ARGUMENT_NAME", DefaultLanguageHighlighterColors.PARAMETER)
                .put("VARIABLE", DefaultLanguageHighlighterColors.LOCAL_VARIABLE)
                .put("REFERENCE", DefaultLanguageHighlighterColors.CLASS_REFERENCE)
                .put("DIRECTIVE", DefaultLanguageHighlighterColors.METADATA)
                .put("NUMBER", DefaultLanguageHighlighterColors.NUMBER)
                .put("STRING", DefaultLanguageHighlighterColors.STRING)
                .put("BRACES", DefaultLanguageHighlighterColors.BRACES)
                .put("PARENS", DefaultLanguageHighlighterColors.PARENTHESES)
                .put("BAD_CHARACTER", HighlighterColors.BAD_CHARACTER)
                .build().forEach((s, key) ->
                KEYS.put(s, createTextAttributesKey(s, key)));
    }


    @NotNull
    @Override
    public Lexer getHighlightingLexer() {
        return new JSpeechLexerAdapter();
    }

    @NotNull
    @Override
    public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
        if (tokenType.equals(JSpeechTypes.EQUALS)) {
            return SEPARATOR_KEYS;
        } else if (tokenType.equals(JSpeechTypes.RULENAME)) {
            return KEY_KEYS;
        } else if (tokenType.equals(JSpeechTypes.LITERAL)) {
            return VALUE_KEYS;
        } else if (tokenType.equals(TokenType.BAD_CHARACTER)) {
            return BAD_CHAR_KEYS;
        } else {
            return EMPTY_KEYS;
        }
    }
}