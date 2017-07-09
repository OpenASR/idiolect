package org.openasr.idear.jsgf;

/**
 * Created by breandan on 11/13/2015.
 */

import com.intellij.lang.*;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.tree.*;
import org.jetbrains.annotations.NotNull;
import org.openasr.idear.jsgf.psi.JSpeechTypes;

public class JSpeechParserDefinition implements ParserDefinition{
    public static final TokenSet WHITE_SPACES = TokenSet.create(TokenType.WHITE_SPACE);
    public static final TokenSet COMMENTS = TokenSet.create(new JSpeechElementType("COMMENT"));

    public static final IFileElementType FILE = new IFileElementType(Language.<JSpeechLanguage>findInstance(JSpeechLanguage.class));

    @NotNull
    @Override
    public Lexer createLexer(Project project) {
        return new JSpeechLexerAdapter();
    }

    @NotNull
    public TokenSet getWhitespaceTokens() {
        return WHITE_SPACES;
    }

    @NotNull
    public TokenSet getCommentTokens() {
        return COMMENTS;
    }

    @NotNull
    public TokenSet getStringLiteralElements() {
        return TokenSet.EMPTY;
    }

    @NotNull
    public PsiParser createParser(final Project project) {
        return new JSpeechParser();
    }

    @Override
    public IFileElementType getFileNodeType() {
        return FILE;
    }

    public PsiFile createFile(FileViewProvider viewProvider) {
        return new JSpeechFile(viewProvider);
    }

    public SpaceRequirements spaceExistanceTypeBetweenTokens(ASTNode left, ASTNode right) {
        return SpaceRequirements.MAY;
    }

    @NotNull
    public PsiElement createElement(ASTNode node) {
        return JSpeechTypes.Factory.createElement(node);
    }
}