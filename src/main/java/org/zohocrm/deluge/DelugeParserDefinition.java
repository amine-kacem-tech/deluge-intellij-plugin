package org.zohocrm.deluge;

import com.intellij.lang.ASTNode;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;
import org.zohocrm.deluge.psi.DelugeTypes;

public class DelugeParserDefinition implements ParserDefinition {
    public static final IFileElementType FILE = new IFileElementType(DelugeLanguage.INSTANCE);

    public static final TokenSet WHITE_SPACES = TokenSet.create(TokenType.WHITE_SPACE);
    public static final TokenSet COMMENTS = TokenSet.create(DelugeTypes.COMMENT);
    public static final TokenSet STRINGS = TokenSet.create(DelugeTypes.STRING);

    @NotNull
    @Override
    public Lexer createLexer(Project project) {
        return new DelugeLexerAdapter();
    }

    @NotNull
    @Override
    public PsiParser createParser(Project project) {
        // Return a minimal parser that just wraps the file
        return (root, builder) -> {
            com.intellij.lang.PsiBuilder.Marker rootMarker = builder.mark();
            while (!builder.eof()) {
                builder.advanceLexer();
            }
            rootMarker.done(root);
            return builder.getTreeBuilt();
        };
    }

    @NotNull
    @Override
    public TokenSet getWhitespaceTokens() {
        return WHITE_SPACES;
    }

    @NotNull
    @Override
    public TokenSet getCommentTokens() {
        return COMMENTS;
    }

    @NotNull
    @Override
    public TokenSet getStringLiteralElements() {
        return STRINGS;
    }

    @NotNull
    @Override
    public PsiElement createElement(ASTNode node) {
        return new DelugePsiElement(node);
    }

    @NotNull
    @Override
    public PsiFile createFile(@NotNull FileViewProvider viewProvider) {
        return new DelugeFile(viewProvider);
    }

    @NotNull
    @Override
    public IFileElementType getFileNodeType() {
        return FILE;
    }

    // Simple PSI element for AST nodes
    private static class DelugePsiElement extends com.intellij.extapi.psi.ASTWrapperPsiElement {
        public DelugePsiElement(@NotNull ASTNode node) {
            super(node);
        }
    }
}

