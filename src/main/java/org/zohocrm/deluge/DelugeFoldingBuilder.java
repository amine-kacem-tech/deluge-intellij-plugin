package org.zohocrm.deluge;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingBuilder;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.zohocrm.deluge.psi.DelugeTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class DelugeFoldingBuilder implements FoldingBuilder {

    private static class BracePair {
        final PsiElement open;
        final PsiElement close;
        final String placeholder;

        BracePair(PsiElement open, PsiElement close, String placeholder) {
            this.open = open;
            this.close = close;
            this.placeholder = placeholder;
        }
    }

    @NotNull
    @Override
    public FoldingDescriptor[] buildFoldRegions(@NotNull ASTNode node, @NotNull Document document) {
        List<FoldingDescriptor> descriptors = new ArrayList<>();
        PsiElement root = node.getPsi();

        // Find matching braces {} for folding
        List<BracePair> bracePairs = findMatchingBraces(root, DelugeTypes.LBRACE, DelugeTypes.RBRACE, "{...}");
        for (BracePair pair : bracePairs) {
            if (isMultiLine(pair.open, pair.close, document)) {
                TextRange range = new TextRange(
                    pair.open.getTextRange().getStartOffset(),
                    pair.close.getTextRange().getEndOffset()
                );
                descriptors.add(new FoldingDescriptor(pair.open.getNode(), range));
            }
        }

        // Find matching brackets [] for folding
        List<BracePair> bracketPairs = findMatchingBraces(root, DelugeTypes.LBRACKET, DelugeTypes.RBRACKET, "[...]");
        for (BracePair pair : bracketPairs) {
            if (isMultiLine(pair.open, pair.close, document)) {
                TextRange range = new TextRange(
                    pair.open.getTextRange().getStartOffset(),
                    pair.close.getTextRange().getEndOffset()
                );
                descriptors.add(new FoldingDescriptor(pair.open.getNode(), range));
            }
        }

        // Fold block comments
        findBlockComments(root, descriptors, document);

        return descriptors.toArray(new FoldingDescriptor[0]);
    }

    private List<BracePair> findMatchingBraces(PsiElement root, IElementType openType, IElementType closeType, String placeholder) {
        List<BracePair> pairs = new ArrayList<>();
        Stack<PsiElement> stack = new Stack<>();
        List<PsiElement> elements = new ArrayList<>();

        collectAllElements(root, elements);

        for (PsiElement element : elements) {
            if (element.getNode() == null) continue;
            IElementType type = element.getNode().getElementType();

            if (type == openType) {
                stack.push(element);
            } else if (type == closeType) {
                if (!stack.isEmpty()) {
                    PsiElement open = stack.pop();
                    pairs.add(new BracePair(open, element, placeholder));
                }
            }
        }

        return pairs;
    }

    private void findBlockComments(PsiElement root, List<FoldingDescriptor> descriptors, Document document) {
        List<PsiElement> elements = new ArrayList<>();
        collectAllElements(root, elements);

        for (PsiElement element : elements) {
            if (element.getNode() != null && element.getNode().getElementType() == DelugeTypes.COMMENT) {
                String text = element.getText();
                if (text.startsWith("/*") && text.contains("\n")) {
                    descriptors.add(new FoldingDescriptor(element.getNode(), element.getTextRange()));
                }
            }
        }
    }

    private void collectAllElements(PsiElement element, List<PsiElement> result) {
        if (element == null) return;
        result.add(element);
        for (PsiElement child : element.getChildren()) {
            collectAllElements(child, result);
        }
    }

    private boolean isMultiLine(PsiElement start, PsiElement end, Document document) {
        int startLine = document.getLineNumber(start.getTextRange().getStartOffset());
        int endLine = document.getLineNumber(end.getTextRange().getEndOffset());
        return endLine > startLine;
    }

    @Nullable
    @Override
    public String getPlaceholderText(@NotNull ASTNode node) {
        IElementType type = node.getElementType();
        if (type == DelugeTypes.COMMENT) {
            return "/*...*/";
        } else if (type == DelugeTypes.LBRACE) {
            return "{...}";
        } else if (type == DelugeTypes.LBRACKET) {
            return "[...]";
        }
        return "{...}";
    }

    @Override
    public boolean isCollapsedByDefault(@NotNull ASTNode node) {
        return false;
    }
}

