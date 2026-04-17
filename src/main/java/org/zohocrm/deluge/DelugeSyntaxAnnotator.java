package org.zohocrm.deluge;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.zohocrm.deluge.psi.DelugeTypes;

import java.util.*;

/**
 * Annotator to provide syntax error detection for Deluge language.
 * This includes checking for missing braces, semicolons, and other syntax issues.
 */
public class DelugeSyntaxAnnotator implements Annotator {

    /**
     * Helper class to store element and its type
     */
    private static class ElementInfo {
        final PsiElement element;
        final IElementType type;

        ElementInfo(PsiElement element, IElementType type) {
            this.element = element;
            this.type = type;
        }
    }

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!(element instanceof PsiFile)) {
            return;
        }

        PsiFile file = (PsiFile) element;

        // Perform various syntax checks
        checkBraceMatching(file, holder);
        checkParenthesesMatching(file, holder);
        checkBracketMatching(file, holder);
        checkMissingSemicolons(file, holder);
    }

    /**
     * Check for matching braces { }
     */
    private void checkBraceMatching(PsiFile file, AnnotationHolder holder) {
        Stack<ElementInfo> braceStack = new Stack<>();
        List<ElementInfo> allBraces = new ArrayList<>();

        collectElements(file, allBraces, DelugeTypes.LBRACE, DelugeTypes.RBRACE);

        for (ElementInfo info : allBraces) {
            if (info.type == DelugeTypes.LBRACE) {
                braceStack.push(info);
            } else if (info.type == DelugeTypes.RBRACE) {
                if (braceStack.isEmpty()) {
                    holder.newAnnotation(HighlightSeverity.ERROR, "Unmatched closing brace '}'")
                            .range(info.element.getTextRange())
                            .create();
                } else {
                    braceStack.pop();
                }
            }
        }

        // Check for unclosed braces
        while (!braceStack.isEmpty()) {
            ElementInfo unclosed = braceStack.pop();
            holder.newAnnotation(HighlightSeverity.ERROR, "Missing closing brace '}'")
                    .range(unclosed.element.getTextRange())
                    .create();
        }
    }

    /**
     * Check for matching parentheses ( )
     */
    private void checkParenthesesMatching(PsiFile file, AnnotationHolder holder) {
        Stack<ElementInfo> parenStack = new Stack<>();
        List<ElementInfo> allParens = new ArrayList<>();

        collectElements(file, allParens, DelugeTypes.LPAREN, DelugeTypes.RPAREN);

        for (ElementInfo info : allParens) {
            if (info.type == DelugeTypes.LPAREN) {
                parenStack.push(info);
            } else if (info.type == DelugeTypes.RPAREN) {
                if (parenStack.isEmpty()) {
                    holder.newAnnotation(HighlightSeverity.ERROR, "Unmatched closing parenthesis ')'")
                            .range(info.element.getTextRange())
                            .create();
                } else {
                    parenStack.pop();
                }
            }
        }

        while (!parenStack.isEmpty()) {
            ElementInfo unclosed = parenStack.pop();
            holder.newAnnotation(HighlightSeverity.ERROR, "Missing closing parenthesis ')'")
                    .range(unclosed.element.getTextRange())
                    .create();
        }
    }

    /**
     * Check for matching brackets [ ]
     */
    private void checkBracketMatching(PsiFile file, AnnotationHolder holder) {
        Stack<ElementInfo> bracketStack = new Stack<>();
        List<ElementInfo> allBrackets = new ArrayList<>();

        collectElements(file, allBrackets, DelugeTypes.LBRACKET, DelugeTypes.RBRACKET);

        for (ElementInfo info : allBrackets) {
            if (info.type == DelugeTypes.LBRACKET) {
                bracketStack.push(info);
            } else if (info.type == DelugeTypes.RBRACKET) {
                if (bracketStack.isEmpty()) {
                    holder.newAnnotation(HighlightSeverity.ERROR, "Unmatched closing bracket ']'")
                            .range(info.element.getTextRange())
                            .create();
                } else {
                    bracketStack.pop();
                }
            }
        }

        while (!bracketStack.isEmpty()) {
            ElementInfo unclosed = bracketStack.pop();
            holder.newAnnotation(HighlightSeverity.ERROR, "Missing closing bracket ']'")
                    .range(unclosed.element.getTextRange())
                    .create();
        }
    }

    /**
     * Check for missing semicolons at the end of statements.
     * Only checks at statement level — tokens inside parentheses (function args,
     * if/for conditions) and brackets (invokeurl blocks, array subscripts) are skipped.
     */
    private void checkMissingSemicolons(PsiFile file, AnnotationHolder holder) {
        List<PsiElement> elements = collectAllElements(file);

        // Build a set of RBRACE indices that close control structure blocks
        // (these do NOT require semicolons)
        Set<Integer> controlBlockCloseBraces = findControlBlockCloseBraces(elements);

        int parenDepth = 0;
        int bracketDepth = 0;

        for (int i = 0; i < elements.size(); i++) {
            PsiElement element = elements.get(i);
            if (element.getNode() == null) continue;

            IElementType type = element.getNode().getElementType();

            // Track nesting depth
            if (type == DelugeTypes.LPAREN) { parenDepth++; continue; }
            if (type == DelugeTypes.RPAREN) { parenDepth = Math.max(0, parenDepth - 1); }
            if (type == DelugeTypes.LBRACKET) { bracketDepth++; continue; }
            if (type == DelugeTypes.RBRACKET) { bracketDepth = Math.max(0, bracketDepth - 1); }

            // Skip semicolon checks when inside parentheses or brackets
            if (parenDepth > 0 || bracketDepth > 0) continue;

            // Skip closing braces of control structures (if/else/for/try/catch)
            if (type == DelugeTypes.RBRACE && controlBlockCloseBraces.contains(i)) continue;

            // Check if this is a token that could end a statement
            if (shouldHaveSemicolon(element, type)) {
                // Look ahead to see if next non-whitespace token is a semicolon
                PsiElement next = getNextNonWhitespaceElement(elements, i);

                if (next != null && next.getNode() != null) {
                    IElementType nextType = next.getNode().getElementType();

                    // If not followed by semicolon, and not followed by something that indicates continuation
                    if (nextType != DelugeTypes.SEMICOLON && !isContinuationToken(nextType)) {
                        // Don't error if this is part of a larger expression
                        if (!isPartOfExpression(element, next, nextType)) {
                            holder.newAnnotation(HighlightSeverity.ERROR, "Missing semicolon ';'")
                                    .range(element.getTextRange())
                                    .create();
                        }
                    }
                }
            }
        }
    }

    /**
     * Find indices of RBRACE tokens that close control structure blocks
     * (if/else/for each/try/catch). These should NOT require semicolons.
     */
    private Set<Integer> findControlBlockCloseBraces(List<PsiElement> elements) {
        Set<Integer> controlBraces = new HashSet<>();

        for (int i = 0; i < elements.size(); i++) {
            PsiElement el = elements.get(i);
            if (el.getNode() == null) continue;
            IElementType type = el.getNode().getElementType();

            // if (...) { or else if (...) {
            if (type == DelugeTypes.IF) {
                // Find matching RPAREN then LBRACE
                int lparenIdx = findNextOfType(elements, i + 1, DelugeTypes.LPAREN);
                if (lparenIdx >= 0) {
                    int rparenIdx = findMatchingParen(elements, lparenIdx);
                    if (rparenIdx >= 0) {
                        int lbraceIdx = findNextNonWhitespace(elements, rparenIdx + 1);
                        if (lbraceIdx >= 0 && elements.get(lbraceIdx).getNode().getElementType() == DelugeTypes.LBRACE) {
                            int rbraceIdx = findMatchingBrace(elements, lbraceIdx);
                            if (rbraceIdx >= 0) {
                                controlBraces.add(rbraceIdx);
                            }
                        }
                    }
                }
            }

            // else { (without if)
            if (type == DelugeTypes.ELSE) {
                int nextIdx = findNextNonWhitespace(elements, i + 1);
                if (nextIdx >= 0 && elements.get(nextIdx).getNode().getElementType() == DelugeTypes.LBRACE) {
                    int rbraceIdx = findMatchingBrace(elements, nextIdx);
                    if (rbraceIdx >= 0) {
                        controlBraces.add(rbraceIdx);
                    }
                }
            }

            // for each ... in ... {
            if (type == DelugeTypes.FOR) {
                int eachIdx = findNextNonWhitespace(elements, i + 1);
                if (eachIdx >= 0 && elements.get(eachIdx).getNode().getElementType() == DelugeTypes.EACH) {
                    int lbraceIdx = findNextOfType(elements, eachIdx + 1, DelugeTypes.LBRACE);
                    if (lbraceIdx >= 0) {
                        int rbraceIdx = findMatchingBrace(elements, lbraceIdx);
                        if (rbraceIdx >= 0) {
                            controlBraces.add(rbraceIdx);
                        }
                    }
                }
            }

            // try {
            if (type == DelugeTypes.TRY) {
                int nextIdx = findNextNonWhitespace(elements, i + 1);
                if (nextIdx >= 0 && elements.get(nextIdx).getNode().getElementType() == DelugeTypes.LBRACE) {
                    int rbraceIdx = findMatchingBrace(elements, nextIdx);
                    if (rbraceIdx >= 0) {
                        controlBraces.add(rbraceIdx);
                    }
                }
            }

            // catch (...) {
            if (type == DelugeTypes.CATCH) {
                int lparenIdx = findNextOfType(elements, i + 1, DelugeTypes.LPAREN);
                if (lparenIdx >= 0) {
                    int rparenIdx = findMatchingParen(elements, lparenIdx);
                    if (rparenIdx >= 0) {
                        int lbraceIdx = findNextNonWhitespace(elements, rparenIdx + 1);
                        if (lbraceIdx >= 0 && elements.get(lbraceIdx).getNode().getElementType() == DelugeTypes.LBRACE) {
                            int rbraceIdx = findMatchingBrace(elements, lbraceIdx);
                            if (rbraceIdx >= 0) {
                                controlBraces.add(rbraceIdx);
                            }
                        }
                    }
                }
            }

            // Function definitions: returnType namespace.name(...) {
            if (type == DelugeTypes.VOID || type == DelugeTypes.STRING_TYPE ||
                type == DelugeTypes.MAP_TYPE || type == DelugeTypes.INT_TYPE ||
                type == DelugeTypes.LIST_TYPE || type == DelugeTypes.BOOL_TYPE) {
                int nsIdx = findNextNonWhitespace(elements, i + 1);
                if (nsIdx >= 0) {
                    IElementType nsType = elements.get(nsIdx).getNode().getElementType();
                    if (nsType == DelugeTypes.STANDALONE || nsType == DelugeTypes.AUTOMATION ||
                        nsType == DelugeTypes.VALIDATION_RULE || nsType == DelugeTypes.SCHEDULE ||
                        nsType == DelugeTypes.RELATED_LIST || nsType == DelugeTypes.BUTTON) {
                        // Find the LPAREN after namespace.name
                        int lparenIdx = findNextOfTypeNoStop(elements, nsIdx + 1, DelugeTypes.LPAREN);
                        if (lparenIdx >= 0) {
                            int rparenIdx = findMatchingParen(elements, lparenIdx);
                            if (rparenIdx >= 0) {
                                int lbraceIdx = findNextNonWhitespace(elements, rparenIdx + 1);
                                if (lbraceIdx >= 0 && elements.get(lbraceIdx).getNode().getElementType() == DelugeTypes.LBRACE) {
                                    int rbraceIdx = findMatchingBrace(elements, lbraceIdx);
                                    if (rbraceIdx >= 0) {
                                        controlBraces.add(rbraceIdx);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return controlBraces;
    }

    private int findNextOfType(List<PsiElement> elements, int startIdx, IElementType targetType) {
        for (int i = startIdx; i < elements.size(); i++) {
            PsiElement el = elements.get(i);
            if (el.getNode() == null) continue;
            IElementType type = el.getNode().getElementType();
            if (type == targetType) return i;
            // Stop at statement boundaries
            if (type == DelugeTypes.SEMICOLON || type == DelugeTypes.RBRACE) return -1;
        }
        return -1;
    }

    private int findNextOfTypeNoStop(List<PsiElement> elements, int startIdx, IElementType targetType) {
        for (int i = startIdx; i < elements.size(); i++) {
            PsiElement el = elements.get(i);
            if (el.getNode() == null) continue;
            IElementType type = el.getNode().getElementType();
            if (type == targetType) return i;
            // Stop at LBRACE (we went too far)
            if (type == DelugeTypes.LBRACE || type == DelugeTypes.SEMICOLON) return -1;
        }
        return -1;
    }

    private int findNextNonWhitespace(List<PsiElement> elements, int startIdx) {
        for (int i = startIdx; i < elements.size(); i++) {
            PsiElement el = elements.get(i);
            if (el.getNode() == null) continue;
            if (!isWhitespaceOrComment(el.getNode().getElementType())) return i;
        }
        return -1;
    }

    private int findMatchingParen(List<PsiElement> elements, int lparenIdx) {
        int depth = 1;
        for (int i = lparenIdx + 1; i < elements.size(); i++) {
            PsiElement el = elements.get(i);
            if (el.getNode() == null) continue;
            IElementType type = el.getNode().getElementType();
            if (type == DelugeTypes.LPAREN) depth++;
            else if (type == DelugeTypes.RPAREN) {
                depth--;
                if (depth == 0) return i;
            }
        }
        return -1;
    }

    private int findMatchingBrace(List<PsiElement> elements, int lbraceIdx) {
        int depth = 1;
        for (int i = lbraceIdx + 1; i < elements.size(); i++) {
            PsiElement el = elements.get(i);
            if (el.getNode() == null) continue;
            IElementType type = el.getNode().getElementType();
            if (type == DelugeTypes.LBRACE) depth++;
            else if (type == DelugeTypes.RBRACE) {
                depth--;
                if (depth == 0) return i;
            }
        }
        return -1;
    }

    /**
     * Check if an element should be followed by a semicolon
     */
    private boolean shouldHaveSemicolon(PsiElement element, IElementType type) {
        // Closing parens after function calls or control structures
        if (type == DelugeTypes.RPAREN) {
            // Check if this is a function call (not if/for/while/catch)
            PsiElement prev = getPreviousNonWhitespaceElement(element);
            if (prev != null && prev.getNode() != null) {
                // This is complex - for now, check if preceded by identifiers/values
                return true;
            }
        }

        // End of array/map access
        if (type == DelugeTypes.RBRACKET) {
            return true;
        }

        // String, number, boolean literals that aren't in expressions
        if (type == DelugeTypes.STRING || type == DelugeTypes.NUMBER ||
            type == DelugeTypes.DECIMAL || type == DelugeTypes.TRUE ||
            type == DelugeTypes.FALSE || type == DelugeTypes.NULL) {
            return true;
        }

        // Variable names/identifiers
        if (type == DelugeTypes.IDENTIFIER) {
            // Check if next token suggests this ends a statement
            return true;
        }

        // Closing brace of a map/list literal or code block
        if (type == DelugeTypes.RBRACE) {
            // Map/list literals need semicolons, control structure blocks don't
            // For now, assume they need it - we'll filter out false positives with continuation check
            return true;
        }

        return false;
    }

    /**
     * Check if the next token indicates this is a continuation, not a new statement
     */
    private boolean isContinuationToken(IElementType type) {
        // Operators that continue an expression
        if (type == DelugeTypes.PLUS || type == DelugeTypes.MINUS ||
            type == DelugeTypes.MULTIPLY || type == DelugeTypes.DIVIDE ||
            type == DelugeTypes.EQUALS || type == DelugeTypes.NOT_EQUALS ||
            type == DelugeTypes.GREATER_THAN || type == DelugeTypes.LESS_THAN ||
            type == DelugeTypes.GREATER_EQUALS || type == DelugeTypes.LESS_EQUALS ||
            type == DelugeTypes.AND || type == DelugeTypes.OR ||
            type == DelugeTypes.ASSIGN) {
            return true;
        }

        // Member access or method call
        if (type == DelugeTypes.DOT) {
            return true;
        }

        // Comma (part of parameter list or array/map elements)
        if (type == DelugeTypes.COMMA) {
            return true;
        }

        // Colon (part of map definition)
        if (type == DelugeTypes.COLON) {
            return true;
        }

        // Opening paren (function/method call: identifier followed by '(')
        if (type == DelugeTypes.LPAREN) {
            return true;
        }

        // Opening/closing brackets (array subscript)
        if (type == DelugeTypes.LBRACKET) {
            return true;
        }

        // Control structure keywords
        if (type == DelugeTypes.IF || type == DelugeTypes.ELSE ||
            type == DelugeTypes.FOR || type == DelugeTypes.WHILE ||
            type == DelugeTypes.TRY || type == DelugeTypes.CATCH ||
            type == DelugeTypes.EACH || type == DelugeTypes.IN) {
            return true;
        }

        // Block start/end
        if (type == DelugeTypes.LBRACE || type == DelugeTypes.RBRACE) {
            return true;
        }

        // Comments (statement could be commented out but complete)
        if (type == DelugeTypes.COMMENT) {
            return true;
        }

        return false;
    }

    /**
     * Check if this is part of a larger expression
     */
    private boolean isPartOfExpression(PsiElement current, PsiElement next, IElementType nextType) {
        // If followed by an operator, it's part of an expression
        if (nextType == DelugeTypes.PLUS || nextType == DelugeTypes.MINUS ||
            nextType == DelugeTypes.MULTIPLY || nextType == DelugeTypes.DIVIDE ||
            nextType == DelugeTypes.EQUALS || nextType == DelugeTypes.NOT_EQUALS ||
            nextType == DelugeTypes.GREATER_THAN || nextType == DelugeTypes.LESS_THAN ||
            nextType == DelugeTypes.GREATER_EQUALS || nextType == DelugeTypes.LESS_EQUALS ||
            nextType == DelugeTypes.AND || nextType == DelugeTypes.OR ||
            nextType == DelugeTypes.DOT) {
            return true;
        }

        return false;
    }

    /**
     * Get previous non-whitespace element
     */
    private PsiElement getPreviousNonWhitespaceElement(PsiElement element) {
        PsiElement prev = element.getPrevSibling();
        while (prev != null && isWhitespaceOrComment(prev.getNode().getElementType())) {
            prev = prev.getPrevSibling();
        }
        return prev;
    }

    /**
     * Collect elements of specific types from the PSI tree
     */
    private void collectElements(PsiElement root, List<ElementInfo> result, IElementType... types) {
        Set<IElementType> typeSet = new HashSet<>(Arrays.asList(types));
        collectElementsRecursive(root, result, typeSet);
    }

    private void collectElementsRecursive(PsiElement element, List<ElementInfo> result, Set<IElementType> types) {
        if (element == null) return;

        IElementType elementType = element.getNode().getElementType();
        if (types.contains(elementType)) {
            result.add(new ElementInfo(element, elementType));
        }

        for (PsiElement child : element.getChildren()) {
            collectElementsRecursive(child, result, types);
        }
    }

    /**
     * Collect all elements from the PSI tree
     */
    private List<PsiElement> collectAllElements(PsiElement root) {
        List<PsiElement> result = new ArrayList<>();
        collectAllElementsRecursive(root, result);
        return result;
    }

    private void collectAllElementsRecursive(PsiElement element, List<PsiElement> result) {
        if (element == null) return;
        result.add(element);
        for (PsiElement child : element.getChildren()) {
            collectAllElementsRecursive(child, result);
        }
    }

    /**
     * Get the next non-whitespace, non-comment element
     */
    private PsiElement getNextNonWhitespaceElement(List<PsiElement> elements, int currentIndex) {
        for (int i = currentIndex + 1; i < elements.size(); i++) {
            PsiElement element = elements.get(i);
            IElementType type = element.getNode().getElementType();
            if (!isWhitespaceOrComment(type)) {
                return element;
            }
        }
        return null;
    }

    /**
     * Check if element type is whitespace or comment
     */
    private boolean isWhitespaceOrComment(IElementType type) {
        return type.toString().contains("WHITE_SPACE") ||
               type == DelugeTypes.COMMENT;
    }
}

