package org.zohocrm.deluge;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.zohocrm.deluge.psi.DelugeTypes;
import org.zohocrm.deluge.standards.DelugeStandards;

import java.util.*;

/**
 * Annotator that enforces Deluge coding standards v2.3.
 * Detects forbidden syntax, naming violations, missing try/catch, and more.
 *
 * PSI tree is flat (tokens only), so we collect all tokens once and scan for patterns.
 */
public class DelugeStandardsAnnotator implements Annotator {

    // Namespace tokens map to their standard name
    private static final Map<IElementType, String> NAMESPACE_TOKENS = new HashMap<>();
    static {
        NAMESPACE_TOKENS.put(DelugeTypes.STANDALONE, "standalone");
        NAMESPACE_TOKENS.put(DelugeTypes.AUTOMATION, "automation");
        NAMESPACE_TOKENS.put(DelugeTypes.VALIDATION_RULE, "validation_rule");
        NAMESPACE_TOKENS.put(DelugeTypes.SCHEDULE, "schedule");
        NAMESPACE_TOKENS.put(DelugeTypes.RELATED_LIST, "related_list");
        NAMESPACE_TOKENS.put(DelugeTypes.BUTTON, "button");
    }

    // Return type tokens map to their type name
    private static final Map<IElementType, String> RETURN_TYPE_TOKENS = new HashMap<>();
    static {
        RETURN_TYPE_TOKENS.put(DelugeTypes.VOID, "void");
        RETURN_TYPE_TOKENS.put(DelugeTypes.STRING_TYPE, "string");
        RETURN_TYPE_TOKENS.put(DelugeTypes.MAP_TYPE, "map");
        RETURN_TYPE_TOKENS.put(DelugeTypes.INT_TYPE, "int");
        RETURN_TYPE_TOKENS.put(DelugeTypes.LIST_TYPE, "list");
        RETURN_TYPE_TOKENS.put(DelugeTypes.BOOL_TYPE, "bool");
    }

    // Type tokens that indicate typed variable declarations
    private static final Set<IElementType> TYPE_TOKENS = new HashSet<>(Arrays.asList(
        DelugeTypes.MAP_TYPE, DelugeTypes.LIST_TYPE, DelugeTypes.STRING_TYPE,
        DelugeTypes.INT_TYPE, DelugeTypes.BOOL_TYPE
    ));

    // Forbidden method names (after dot)
    private static final Set<String> FORBIDDEN_IDENTIFIERS = new HashSet<>(Arrays.asList(
        "empty", "isNotNull", "toBoolean"
    ));

    /**
     * Represents a detected function boundary in the token stream.
     */
    private static class FunctionInfo {
        final String namespace;
        final String returnType;
        final PsiElement returnTypeElement;
        final PsiElement namespaceElement;
        final int bodyStartIndex; // index of LBRACE
        final int bodyEndIndex;   // index of matching RBRACE
        final int signatureStartIndex; // index of return type token
        final int signatureEndIndex;   // index of RPAREN before body LBRACE

        FunctionInfo(String namespace, String returnType, PsiElement returnTypeElement,
                     PsiElement namespaceElement, int signatureStartIndex, int signatureEndIndex,
                     int bodyStartIndex, int bodyEndIndex) {
            this.namespace = namespace;
            this.returnType = returnType;
            this.returnTypeElement = returnTypeElement;
            this.namespaceElement = namespaceElement;
            this.signatureStartIndex = signatureStartIndex;
            this.signatureEndIndex = signatureEndIndex;
            this.bodyStartIndex = bodyStartIndex;
            this.bodyEndIndex = bodyEndIndex;
        }
    }

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!(element instanceof PsiFile)) {
            return;
        }

        PsiFile file = (PsiFile) element;

        // Collect all tokens once
        List<PsiElement> tokens = new ArrayList<>();
        collectLeafElements(file, tokens);

        if (tokens.isEmpty()) return;

        // Build token type list for fast lookups
        IElementType[] types = new IElementType[tokens.size()];
        for (int i = 0; i < tokens.size(); i++) {
            types[i] = tokens.get(i).getNode().getElementType();
        }

        // Detect function boundaries
        List<FunctionInfo> functions = detectFunctions(tokens, types);

        // Build set of indices that are inside function signatures (to exclude from typed var checks)
        Set<Integer> signatureIndices = new HashSet<>();
        for (FunctionInfo fn : functions) {
            for (int i = fn.signatureStartIndex; i <= fn.signatureEndIndex; i++) {
                signatureIndices.add(i);
            }
        }

        // Run all checks
        checkForbiddenMethods(tokens, types, holder);
        checkTypedVariableDeclarations(tokens, types, signatureIndices, holder);
        checkThrowStatements(tokens, types, holder);
        checkTernaryOperators(tokens, types, holder);
        checkJavaStyleForLoops(tokens, types, holder);
        checkFunctionReturnTypes(functions, holder);
        checkMissingTryCatch(functions, tokens, types, holder);
        checkMissingFakeReturn(functions, tokens, types, holder);
        checkVariableNaming(tokens, types, signatureIndices, holder);
        checkWhileLoops(tokens, types, holder);
        checkMissingBraces(tokens, types, holder);
        checkReturnSemicolon(tokens, types, holder);
        checkCommentsOutsideFunction(tokens, types, functions, holder);
    }

    // ========================================================================
    // CHECK 1: Forbidden methods (.empty, .isNotNull(), .toBoolean())
    // ========================================================================

    private void checkForbiddenMethods(List<PsiElement> tokens, IElementType[] types,
                                       AnnotationHolder holder) {
        for (int i = 0; i < tokens.size() - 1; i++) {
            if (types[i] == DelugeTypes.DOT) {
                int nextIdx = nextNonWhitespace(types, i + 1);
                if (nextIdx < 0) continue;

                if (types[nextIdx] == DelugeTypes.IDENTIFIER) {
                    String name = tokens.get(nextIdx).getText();
                    if (FORBIDDEN_IDENTIFIERS.contains(name)) {
                        String msg = String.format(DelugeStandards.MSG_FORBIDDEN_METHOD, "." + name + (name.equals("empty") ? "" : "()"));
                        holder.newAnnotation(HighlightSeverity.ERROR, msg)
                                .range(tokens.get(nextIdx).getTextRange())
                                .create();
                    }
                }
            }
        }
    }

    // ========================================================================
    // CHECK 2: Typed variable declarations (map x = Map())
    // ========================================================================

    private void checkTypedVariableDeclarations(List<PsiElement> tokens, IElementType[] types,
                                                Set<Integer> signatureIndices,
                                                AnnotationHolder holder) {
        for (int i = 0; i < tokens.size() - 2; i++) {
            if (signatureIndices.contains(i)) continue;

            if (TYPE_TOKENS.contains(types[i])) {
                int identIdx = nextNonWhitespace(types, i + 1);
                if (identIdx < 0) continue;

                if (types[identIdx] == DelugeTypes.IDENTIFIER) {
                    int assignIdx = nextNonWhitespace(types, identIdx + 1);
                    if (assignIdx >= 0 && types[assignIdx] == DelugeTypes.ASSIGN) {
                        // Also skip if previous token is DOT (member access like resp.map)
                        int prevIdx = prevNonWhitespace(types, i - 1);
                        if (prevIdx >= 0 && types[prevIdx] == DelugeTypes.DOT) continue;

                        holder.newAnnotation(HighlightSeverity.ERROR, DelugeStandards.MSG_TYPED_VARIABLE)
                                .range(tokens.get(i).getTextRange())
                                .create();
                    }
                }
            }
        }
    }

    // ========================================================================
    // CHECK 3: throw statements
    // ========================================================================

    private void checkThrowStatements(List<PsiElement> tokens, IElementType[] types,
                                      AnnotationHolder holder) {
        for (int i = 0; i < tokens.size(); i++) {
            if (types[i] == DelugeTypes.IDENTIFIER && "throw".equals(tokens.get(i).getText())) {
                holder.newAnnotation(HighlightSeverity.ERROR, DelugeStandards.MSG_FORBIDDEN_THROW)
                        .range(tokens.get(i).getTextRange())
                        .create();
            }
        }
    }

    // ========================================================================
    // CHECK 4: Ternary operators (? shows up as BAD_CHARACTER)
    // ========================================================================

    private void checkTernaryOperators(List<PsiElement> tokens, IElementType[] types,
                                       AnnotationHolder holder) {
        for (int i = 0; i < tokens.size(); i++) {
            if (types[i] == TokenType.BAD_CHARACTER && "?".equals(tokens.get(i).getText())) {
                holder.newAnnotation(HighlightSeverity.ERROR, DelugeStandards.MSG_FORBIDDEN_TERNARY)
                        .range(tokens.get(i).getTextRange())
                        .create();
            }
        }
    }

    // ========================================================================
    // CHECK 5: Java-style for loops (for ( instead of for each)
    // ========================================================================

    private void checkJavaStyleForLoops(List<PsiElement> tokens, IElementType[] types,
                                        AnnotationHolder holder) {
        for (int i = 0; i < tokens.size(); i++) {
            if (types[i] == DelugeTypes.FOR) {
                int nextIdx = nextNonWhitespace(types, i + 1);
                if (nextIdx >= 0 && types[nextIdx] == DelugeTypes.LPAREN) {
                    holder.newAnnotation(HighlightSeverity.ERROR, DelugeStandards.MSG_USE_FOR_EACH)
                            .range(tokens.get(i).getTextRange())
                            .create();
                }
            }
        }
    }

    // ========================================================================
    // CHECK 6: Wrong function return type
    // ========================================================================

    private void checkFunctionReturnTypes(List<FunctionInfo> functions, AnnotationHolder holder) {
        for (FunctionInfo fn : functions) {
            if (!DelugeStandards.isCorrectReturnType(fn.namespace, fn.returnType)) {
                String expected = getExpectedReturnType(fn.namespace);
                String msg = DelugeStandards.MSG_WRONG_RETURN_TYPE +
                        " (expected '" + expected + "' for " + fn.namespace + ")";
                holder.newAnnotation(HighlightSeverity.ERROR, msg)
                        .range(fn.returnTypeElement.getTextRange())
                        .create();
            }
        }
    }

    // ========================================================================
    // CHECK 7: Missing try/catch in function body
    // ========================================================================

    private void checkMissingTryCatch(List<FunctionInfo> functions, List<PsiElement> tokens,
                                      IElementType[] types, AnnotationHolder holder) {
        for (FunctionInfo fn : functions) {
            boolean hasTry = false;
            for (int i = fn.bodyStartIndex; i <= fn.bodyEndIndex && i < types.length; i++) {
                if (types[i] == DelugeTypes.TRY) {
                    hasTry = true;
                    break;
                }
            }
            if (!hasTry) {
                holder.newAnnotation(HighlightSeverity.WARNING, DelugeStandards.MSG_MISSING_TRY_CATCH)
                        .range(fn.namespaceElement.getTextRange())
                        .create();
            }
        }
    }

    // ========================================================================
    // CHECK 8: Missing fake return after catch (standalone functions)
    // ========================================================================

    private void checkMissingFakeReturn(List<FunctionInfo> functions, List<PsiElement> tokens,
                                        IElementType[] types, AnnotationHolder holder) {
        for (FunctionInfo fn : functions) {
            if (!"standalone".equals(fn.namespace) && !"button".equals(fn.namespace)
                    && !"related_list".equals(fn.namespace)) {
                continue; // only types that require a return
            }

            // Find last CATCH in the function body
            int lastCatchIdx = -1;
            for (int i = fn.bodyStartIndex; i <= fn.bodyEndIndex && i < types.length; i++) {
                if (types[i] == DelugeTypes.CATCH) {
                    lastCatchIdx = i;
                }
            }

            if (lastCatchIdx < 0) continue; // no catch block, already warned by check 7

            // Find the closing brace of the catch block
            int catchBodyEnd = findMatchingBrace(tokens, types, lastCatchIdx);
            if (catchBodyEnd < 0) continue;

            // Look for RETURN between catch body end and function body end
            boolean hasReturn = false;
            for (int i = catchBodyEnd; i <= fn.bodyEndIndex && i < types.length; i++) {
                if (types[i] == DelugeTypes.RETURN) {
                    hasReturn = true;
                    break;
                }
            }

            if (!hasReturn) {
                holder.newAnnotation(HighlightSeverity.WARNING, DelugeStandards.MSG_MISSING_FAKE_RETURN)
                        .range(fn.namespaceElement.getTextRange())
                        .create();
            }
        }
    }

    // ========================================================================
    // CHECK 9: Variable naming (not snake_case)
    // ========================================================================

    private void checkVariableNaming(List<PsiElement> tokens, IElementType[] types,
                                     Set<Integer> signatureIndices, AnnotationHolder holder) {
        for (int i = 0; i < tokens.size(); i++) {
            if (signatureIndices.contains(i)) continue;
            if (types[i] != DelugeTypes.IDENTIFIER) continue;

            // Check if this is an assignment: IDENTIFIER = ...
            int nextIdx = nextNonWhitespace(types, i + 1);
            if (nextIdx < 0 || types[nextIdx] != DelugeTypes.ASSIGN) continue;

            // Skip if preceded by DOT (property access like record.field = ...)
            int prevIdx = prevNonWhitespace(types, i - 1);
            if (prevIdx >= 0 && types[prevIdx] == DelugeTypes.DOT) continue;

            String name = tokens.get(i).getText();

            // Skip UPPER_SNAKE_CASE constants
            if (DelugeStandards.isValidConstantName(name)) continue;

            // Skip single-char names (loop vars like i, j, k)
            if (name.length() <= 1) continue;

            // Check snake_case
            if (!DelugeStandards.isValidVariableName(name)) {
                holder.newAnnotation(HighlightSeverity.WEAK_WARNING, DelugeStandards.MSG_USE_SNAKE_CASE)
                        .range(tokens.get(i).getTextRange())
                        .create();
            }
        }
    }

    // ========================================================================
    // CHECK 10: while loops (forbidden in Deluge)
    // ========================================================================

    private void checkWhileLoops(List<PsiElement> tokens, IElementType[] types,
                                 AnnotationHolder holder) {
        for (int i = 0; i < tokens.size(); i++) {
            if (types[i] == DelugeTypes.WHILE) {
                holder.newAnnotation(HighlightSeverity.ERROR, DelugeStandards.MSG_FORBIDDEN_WHILE)
                        .range(tokens.get(i).getTextRange())
                        .create();
            }
        }
    }

    // ========================================================================
    // CHECK 11: if/for each without braces
    // ========================================================================

    private void checkMissingBraces(List<PsiElement> tokens, IElementType[] types,
                                    AnnotationHolder holder) {
        for (int i = 0; i < tokens.size(); i++) {
            // Check: if(...) NOT followed by {
            if (types[i] == DelugeTypes.IF) {
                int nextIdx = nextNonWhitespace(types, i + 1);
                if (nextIdx < 0) continue;
                if (types[nextIdx] == DelugeTypes.LPAREN) {
                    int rparenIdx = findMatchingParen(types, nextIdx);
                    if (rparenIdx < 0) continue;
                    int afterParen = nextNonWhitespace(types, rparenIdx + 1);
                    if (afterParen >= 0 && types[afterParen] != DelugeTypes.LBRACE) {
                        holder.newAnnotation(HighlightSeverity.WARNING, DelugeStandards.MSG_MISSING_BRACES_IF)
                                .range(tokens.get(i).getTextRange())
                                .create();
                    }
                }
            }

            // Check: else NOT followed by { or if
            if (types[i] == DelugeTypes.ELSE) {
                int nextIdx = nextNonWhitespace(types, i + 1);
                if (nextIdx >= 0 && types[nextIdx] != DelugeTypes.LBRACE && types[nextIdx] != DelugeTypes.IF) {
                    holder.newAnnotation(HighlightSeverity.WARNING, DelugeStandards.MSG_MISSING_BRACES_IF)
                            .range(tokens.get(i).getTextRange())
                            .create();
                }
            }

            // Check: for each ... in ... NOT followed by {
            if (types[i] == DelugeTypes.FOR) {
                int eachIdx = nextNonWhitespace(types, i + 1);
                if (eachIdx < 0 || types[eachIdx] != DelugeTypes.EACH) continue;

                // Find the 'in' keyword after 'each'
                int inIdx = -1;
                for (int j = eachIdx + 1; j < types.length; j++) {
                    if (types[j] == DelugeTypes.IN) {
                        inIdx = j;
                        break;
                    }
                    // Stop if we hit a brace or semicolon
                    if (types[j] == DelugeTypes.LBRACE || types[j] == DelugeTypes.SEMICOLON) break;
                }
                if (inIdx < 0) continue;

                // Find the collection expression end — look for LBRACE or next statement token
                // The collection could be: identifier, identifier.method(), etc.
                // We need to find the first non-expression token after 'in <expr>'
                int j = inIdx + 1;
                int lastExprToken = -1;
                while (j < types.length) {
                    IElementType t = types[j];
                    if (t == DelugeTypes.LBRACE) {
                        // Found brace, this is correct
                        lastExprToken = -1;
                        break;
                    }
                    if (t == DelugeTypes.SEMICOLON || t == DelugeTypes.IF ||
                            t == DelugeTypes.FOR || t == DelugeTypes.RETURN) {
                        // Hit a statement boundary without brace
                        lastExprToken = j;
                        break;
                    }
                    if (!isWhitespaceOrComment(types[j])) {
                        lastExprToken = j;
                    }
                    j++;
                }

                if (lastExprToken >= 0 && types[lastExprToken] != DelugeTypes.LBRACE) {
                    // Check that the token after the collection expression is not LBRACE
                    int afterExpr = nextNonWhitespace(types, lastExprToken);
                    if (afterExpr < 0 || types[afterExpr] != DelugeTypes.LBRACE) {
                        holder.newAnnotation(HighlightSeverity.WARNING, DelugeStandards.MSG_MISSING_BRACES_FOR)
                                .range(tokens.get(i).getTextRange())
                                .create();
                    }
                }
            }
        }
    }

    // ========================================================================
    // CHECK 12: Missing semicolon after return statement
    // ========================================================================

    private static final Set<IElementType> STATEMENT_BOUNDARY_TOKENS = new HashSet<>(Arrays.asList(
        DelugeTypes.IF, DelugeTypes.ELSE, DelugeTypes.FOR, DelugeTypes.WHILE,
        DelugeTypes.TRY, DelugeTypes.CATCH, DelugeTypes.RETURN,
        DelugeTypes.BREAK, DelugeTypes.CONTINUE, DelugeTypes.RBRACE
    ));

    private void checkReturnSemicolon(List<PsiElement> tokens, IElementType[] types,
                                      AnnotationHolder holder) {
        for (int i = 0; i < tokens.size(); i++) {
            if (types[i] != DelugeTypes.RETURN) continue;

            // Scan forward from RETURN looking for SEMICOLON or statement boundary.
            // Track brace depth so map literals like {"key": "value"} are skipped.
            boolean foundSemicolon = false;
            boolean foundExpression = false;
            int braceDepth = 0;
            for (int j = i + 1; j < tokens.size(); j++) {
                if (isWhitespaceOrComment(types[j])) continue;

                if (types[j] == DelugeTypes.LBRACE) {
                    braceDepth++;
                    foundExpression = true;
                    continue;
                }
                if (types[j] == DelugeTypes.RBRACE) {
                    if (braceDepth > 0) {
                        braceDepth--;
                        continue;
                    }
                    // braceDepth == 0: this is a real statement boundary
                    break;
                }

                // Skip everything inside braces (map literal contents)
                if (braceDepth > 0) {
                    continue;
                }

                if (types[j] == DelugeTypes.SEMICOLON) {
                    foundSemicolon = true;
                    break;
                }

                // If we hit a statement boundary token after seeing expression tokens,
                // the return is missing its semicolon
                if (foundExpression && STATEMENT_BOUNDARY_TOKENS.contains(types[j])) {
                    break;
                }

                foundExpression = true;
            }

            if (foundExpression && !foundSemicolon) {
                holder.newAnnotation(HighlightSeverity.ERROR, DelugeStandards.MSG_MISSING_SEMICOLON_RETURN)
                        .range(tokens.get(i).getTextRange())
                        .create();
            }
        }
    }

    // ========================================================================
    // CHECK 13: Comments outside function body
    // ========================================================================

    private void checkCommentsOutsideFunction(List<PsiElement> tokens, IElementType[] types,
                                              List<FunctionInfo> functions,
                                              AnnotationHolder holder) {
        for (int i = 0; i < tokens.size(); i++) {
            if (types[i] != DelugeTypes.COMMENT) continue;

            boolean insideFunction = false;
            for (FunctionInfo fn : functions) {
                if (i > fn.bodyStartIndex && i < fn.bodyEndIndex) {
                    insideFunction = true;
                    break;
                }
            }

            if (!insideFunction) {
                holder.newAnnotation(HighlightSeverity.ERROR, DelugeStandards.MSG_COMMENT_OUTSIDE_FUNCTION)
                        .range(tokens.get(i).getTextRange())
                        .create();
            }
        }
    }

    // ========================================================================
    // FUNCTION BOUNDARY DETECTION
    // ========================================================================

    /**
     * Detect function definitions by scanning for the pattern:
     * RETURN_TYPE NAMESPACE DOT IDENTIFIER LPAREN ...params... RPAREN LBRACE ...body... RBRACE
     */
    private List<FunctionInfo> detectFunctions(List<PsiElement> tokens, IElementType[] types) {
        List<FunctionInfo> functions = new ArrayList<>();

        for (int i = 0; i < tokens.size() - 6; i++) {
            // Look for return type token
            if (!RETURN_TYPE_TOKENS.containsKey(types[i])) continue;

            int nsIdx = nextNonWhitespace(types, i + 1);
            if (nsIdx < 0 || !NAMESPACE_TOKENS.containsKey(types[nsIdx])) continue;

            int dotIdx = nextNonWhitespace(types, nsIdx + 1);
            if (dotIdx < 0 || types[dotIdx] != DelugeTypes.DOT) continue;

            int nameIdx = nextNonWhitespace(types, dotIdx + 1);
            if (nameIdx < 0 || types[nameIdx] != DelugeTypes.IDENTIFIER) continue;

            int lparenIdx = nextNonWhitespace(types, nameIdx + 1);
            if (lparenIdx < 0 || types[lparenIdx] != DelugeTypes.LPAREN) continue;

            // Find matching RPAREN
            int rparenIdx = findMatchingParen(types, lparenIdx);
            if (rparenIdx < 0) continue;

            // Next should be LBRACE
            int lbraceIdx = nextNonWhitespace(types, rparenIdx + 1);
            if (lbraceIdx < 0 || types[lbraceIdx] != DelugeTypes.LBRACE) continue;

            // Find matching RBRACE
            int rbraceIdx = findMatchingBraceFromIndex(types, lbraceIdx);
            if (rbraceIdx < 0) continue;

            String returnType = RETURN_TYPE_TOKENS.get(types[i]);
            String namespace = NAMESPACE_TOKENS.get(types[nsIdx]);

            functions.add(new FunctionInfo(
                namespace, returnType,
                tokens.get(i), tokens.get(nsIdx),
                i, rparenIdx,
                lbraceIdx, rbraceIdx
            ));

            // Skip past this function to avoid re-processing
            i = rbraceIdx;
        }

        return functions;
    }

    // ========================================================================
    // HELPER METHODS
    // ========================================================================

    /**
     * Collect all leaf (token) elements from the PSI tree.
     */
    private void collectLeafElements(PsiElement element, List<PsiElement> result) {
        if (element.getFirstChild() == null) {
            // Leaf node
            if (element.getNode() != null) {
                result.add(element);
            }
            return;
        }
        for (PsiElement child = element.getFirstChild(); child != null; child = child.getNextSibling()) {
            collectLeafElements(child, result);
        }
    }

    /**
     * Find next non-whitespace token index starting from idx.
     */
    private int nextNonWhitespace(IElementType[] types, int idx) {
        for (int i = idx; i < types.length; i++) {
            if (!isWhitespaceOrComment(types[i])) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Find previous non-whitespace token index starting from idx.
     */
    private int prevNonWhitespace(IElementType[] types, int idx) {
        for (int i = idx; i >= 0; i--) {
            if (!isWhitespaceOrComment(types[i])) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Find matching RPAREN for LPAREN at given index.
     */
    private int findMatchingParen(IElementType[] types, int lparenIdx) {
        int depth = 1;
        for (int i = lparenIdx + 1; i < types.length; i++) {
            if (types[i] == DelugeTypes.LPAREN) depth++;
            else if (types[i] == DelugeTypes.RPAREN) {
                depth--;
                if (depth == 0) return i;
            }
        }
        return -1;
    }

    /**
     * Find matching RBRACE for LBRACE at given index.
     */
    private int findMatchingBraceFromIndex(IElementType[] types, int lbraceIdx) {
        int depth = 1;
        for (int i = lbraceIdx + 1; i < types.length; i++) {
            if (types[i] == DelugeTypes.LBRACE) depth++;
            else if (types[i] == DelugeTypes.RBRACE) {
                depth--;
                if (depth == 0) return i;
            }
        }
        return -1;
    }

    /**
     * Find the first LBRACE after the given index and return the matching RBRACE index.
     * Used to find the end of a catch block.
     */
    private int findMatchingBrace(List<PsiElement> tokens, IElementType[] types, int startIdx) {
        for (int i = startIdx + 1; i < types.length; i++) {
            if (types[i] == DelugeTypes.LBRACE) {
                return findMatchingBraceFromIndex(types, i);
            }
        }
        return -1;
    }

    private boolean isWhitespaceOrComment(IElementType type) {
        return type.toString().contains("WHITE_SPACE") || type == DelugeTypes.COMMENT;
    }

    private String getExpectedReturnType(String namespace) {
        switch (namespace) {
            case "standalone": return DelugeStandards.STANDALONE_RETURN_TYPE;
            case "automation": return DelugeStandards.AUTOMATION_RETURN_TYPE;
            case "validation_rule": return DelugeStandards.VALIDATION_RETURN_TYPE;
            case "schedule": return DelugeStandards.SCHEDULE_RETURN_TYPE;
            case "related_list": return DelugeStandards.RELATED_LIST_RETURN_TYPE;
            case "button": return DelugeStandards.BUTTON_RETURN_TYPE;
            default: return "unknown";
        }
    }
}
