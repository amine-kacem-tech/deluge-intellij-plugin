package org.zohocrm.deluge;

import com.intellij.formatting.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.TokenType;
import com.intellij.psi.formatter.common.AbstractBlock;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.zohocrm.deluge.psi.DelugeTypes;

import java.util.*;

/**
 * Formatting block for Deluge language.
 *
 * Since the PSI tree is flat (all tokens are direct children of the file),
 * this block computes indentation based on brace nesting depth and defines
 * spacing rules between adjacent tokens.
 *
 * Special handling for invokeurl [...] blocks: brackets are treated like braces
 * for indentation, and properties inside are separated by newlines.
 */
public class DelugeBlock extends AbstractBlock {

    private final SpacingBuilder spacingBuilder;
    private final int indentLevel;

    /**
     * Map from child AST node to its computed depth (for indentation).
     * Built once by the root block and shared.
     */
    private final Map<ASTNode, Integer> depthMap;

    /** Set of LBRACKET nodes that open invokeurl blocks. */
    private final Set<ASTNode> invokeUrlLBrackets;

    /** Set of RBRACKET nodes that close invokeurl blocks. */
    private final Set<ASTNode> invokeUrlRBrackets;

    /** Set of all non-whitespace nodes inside invokeurl [...] blocks. */
    private final Set<ASTNode> insideInvokeUrl;

    /** Known invokeurl property names that start a new line inside the block. */
    private static final Set<String> INVOKE_URL_PROPERTIES = Set.of(
            "url", "type", "headers", "parameters", "body", "connection"
    );

    /**
     * Root block constructor — scans all children to compute brace depths
     * and identify invokeurl bracket blocks.
     */
    public DelugeBlock(@NotNull ASTNode node, @NotNull SpacingBuilder spacingBuilder) {
        super(node, null, null);
        this.spacingBuilder = spacingBuilder;
        this.indentLevel = 0;
        this.invokeUrlLBrackets = new HashSet<>();
        this.invokeUrlRBrackets = new HashSet<>();
        this.insideInvokeUrl = new HashSet<>();
        identifyInvokeUrlBrackets(node);
        this.depthMap = buildDepthMap(node);
    }

    /**
     * Child block constructor — uses pre-computed depth and invokeurl sets.
     */
    private DelugeBlock(@NotNull ASTNode node, @NotNull SpacingBuilder spacingBuilder,
                        int indentLevel, @NotNull Map<ASTNode, Integer> depthMap,
                        @NotNull Set<ASTNode> invokeUrlLBrackets,
                        @NotNull Set<ASTNode> invokeUrlRBrackets,
                        @NotNull Set<ASTNode> insideInvokeUrl) {
        super(node, null, null);
        this.spacingBuilder = spacingBuilder;
        this.indentLevel = indentLevel;
        this.depthMap = depthMap;
        this.invokeUrlLBrackets = invokeUrlLBrackets;
        this.invokeUrlRBrackets = invokeUrlRBrackets;
        this.insideInvokeUrl = insideInvokeUrl;
    }

    /**
     * Identify LBRACKET/RBRACKET pairs that belong to invokeurl blocks,
     * and collect all non-whitespace nodes inside those blocks.
     */
    private void identifyInvokeUrlBrackets(@NotNull ASTNode root) {
        for (ASTNode child = root.getFirstChildNode(); child != null; child = child.getTreeNext()) {
            if (child.getElementType() != DelugeTypes.INVOKE_URL) continue;

            // Find the next non-whitespace token — should be LBRACKET
            ASTNode next = child.getTreeNext();
            while (next != null && next.getElementType() == TokenType.WHITE_SPACE) {
                next = next.getTreeNext();
            }
            if (next == null || next.getElementType() != DelugeTypes.LBRACKET) continue;

            invokeUrlLBrackets.add(next);

            // Walk forward to find the matching RBRACKET, tracking nested brackets
            int bracketDepth = 1;
            ASTNode inner = next.getTreeNext();
            while (inner != null && bracketDepth > 0) {
                IElementType innerType = inner.getElementType();
                if (innerType == DelugeTypes.LBRACKET) {
                    bracketDepth++;
                } else if (innerType == DelugeTypes.RBRACKET) {
                    bracketDepth--;
                    if (bracketDepth == 0) {
                        invokeUrlRBrackets.add(inner);
                        break;
                    }
                }
                if (innerType != TokenType.WHITE_SPACE) {
                    insideInvokeUrl.add(inner);
                }
                inner = inner.getTreeNext();
            }
        }
    }

    /**
     * Walk all direct children of the root node and assign each a depth.
     * Opening braces and invokeurl brackets increment depth;
     * closing braces and invokeurl brackets decrement depth.
     */
    private Map<ASTNode, Integer> buildDepthMap(@NotNull ASTNode root) {
        Map<ASTNode, Integer> map = new LinkedHashMap<>();
        int depth = 0;
        for (ASTNode child = root.getFirstChildNode(); child != null; child = child.getTreeNext()) {
            IElementType type = child.getElementType();
            if (type == DelugeTypes.RBRACE || invokeUrlRBrackets.contains(child)) {
                depth = Math.max(0, depth - 1);
            }
            map.put(child, depth);
            if (type == DelugeTypes.LBRACE || invokeUrlLBrackets.contains(child)) {
                depth++;
            }
        }
        return map;
    }

    @Override
    protected List<Block> buildChildren() {
        if (myNode.getFirstChildNode() == null) {
            // Leaf node — no children
            return Collections.emptyList();
        }

        List<Block> blocks = new ArrayList<>();
        ASTNode child = myNode.getFirstChildNode();
        while (child != null) {
            IElementType type = child.getElementType();
            if (type != TokenType.WHITE_SPACE) {
                int childDepth = depthMap.getOrDefault(child, 0);
                blocks.add(new DelugeBlock(child, spacingBuilder, childDepth, depthMap,
                        invokeUrlLBrackets, invokeUrlRBrackets, insideInvokeUrl));
            }
            child = child.getTreeNext();
        }
        return blocks;
    }

    @Override
    public @NotNull Indent getIndent() {
        if (indentLevel == 0) {
            return Indent.getNoneIndent();
        }
        return Indent.getSpaceIndent(indentLevel * 4);
    }

    @Override
    public @Nullable Spacing getSpacing(@Nullable Block child1, @NotNull Block child2) {
        if (child1 == null) return null;

        // Get token types
        IElementType type1 = getBlockType(child1);
        IElementType type2 = getBlockType(child2);
        if (type1 == null || type2 == null) return null;

        ASTNode node1 = ((DelugeBlock) child1).myNode;
        ASTNode node2 = ((DelugeBlock) child2).myNode;

        // --- invokeurl block rules ---

        // After INVOKE_URL keyword: newline (the [ goes on next line)
        if (type1 == DelugeTypes.INVOKE_URL) {
            return newline();
        }

        // After invokeurl opening bracket: newline
        if (invokeUrlLBrackets.contains(node1) && type2 != DelugeTypes.RBRACKET) {
            return newline();
        }

        // Before invokeurl closing bracket: newline
        if (invokeUrlRBrackets.contains(node2) && type1 != DelugeTypes.LBRACKET) {
            return newline();
        }

        // Empty invokeurl brackets: no space
        if (invokeUrlLBrackets.contains(node1) && invokeUrlRBrackets.contains(node2)) {
            return Spacing.createSpacing(0, 0, 0, false, 0);
        }

        // Inside invokeurl: newline before each property name (url, type, headers, etc.)
        if (insideInvokeUrl.contains(node2) && type2 == DelugeTypes.IDENTIFIER
                && INVOKE_URL_PROPERTIES.contains(node2.getText())
                && type1 != DelugeTypes.COLON) {
            return newline();
        }

        // --- Newline rules ---

        // After opening brace: newline
        if (type1 == DelugeTypes.LBRACE && type2 != DelugeTypes.RBRACE) {
            return newline();
        }

        // Before closing brace: newline
        if (type2 == DelugeTypes.RBRACE && type1 != DelugeTypes.LBRACE) {
            return newline();
        }

        // Empty braces: no space
        if (type1 == DelugeTypes.LBRACE && type2 == DelugeTypes.RBRACE) {
            return Spacing.createSpacing(0, 0, 0, false, 0);
        }

        // After semicolon: newline
        if (type1 == DelugeTypes.SEMICOLON) {
            return newline();
        }

        // After single-line comment: newline
        if (type1 == DelugeTypes.COMMENT) {
            return newline();
        }

        // Before control keywords when preceded by closing brace (else, catch)
        if (type1 == DelugeTypes.RBRACE &&
                (type2 == DelugeTypes.ELSE || type2 == DelugeTypes.CATCH)) {
            return Spacing.createSpacing(1, 1, 0, false, 0);
        }

        // After closing brace (new statement): newline, unless followed by else/catch
        if (type1 == DelugeTypes.RBRACE &&
                type2 != DelugeTypes.ELSE && type2 != DelugeTypes.CATCH &&
                type2 != DelugeTypes.SEMICOLON && type2 != DelugeTypes.RPAREN) {
            return newline();
        }

        // --- Space rules ---

        // Space before opening brace (e.g., if(...) {)
        if (type2 == DelugeTypes.LBRACE) {
            return singleSpace();
        }

        // Space around assignment operator
        if (type1 == DelugeTypes.ASSIGN || type2 == DelugeTypes.ASSIGN) {
            return singleSpace();
        }

        // Space around comparison and logical operators
        if (isComparisonOrLogical(type1) || isComparisonOrLogical(type2)) {
            return singleSpace();
        }

        // Space around arithmetic operators
        if (isArithmetic(type1) || isArithmetic(type2)) {
            return singleSpace();
        }

        // Space after comma
        if (type1 == DelugeTypes.COMMA) {
            return singleSpace();
        }

        // No space before comma
        if (type2 == DelugeTypes.COMMA) {
            return noSpace();
        }

        // No space before semicolon
        if (type2 == DelugeTypes.SEMICOLON) {
            return noSpace();
        }

        // Space after colon
        if (type1 == DelugeTypes.COLON) {
            return singleSpace();
        }

        // No space before colon
        if (type2 == DelugeTypes.COLON) {
            return noSpace();
        }

        // No space after opening paren/bracket (skip invokeurl brackets — handled above)
        if (type1 == DelugeTypes.LPAREN ||
                (type1 == DelugeTypes.LBRACKET && !invokeUrlLBrackets.contains(node1))) {
            return noSpace();
        }

        // No space before closing paren/bracket (skip invokeurl brackets — handled above)
        if (type2 == DelugeTypes.RPAREN ||
                (type2 == DelugeTypes.RBRACKET && !invokeUrlRBrackets.contains(node2))) {
            return noSpace();
        }

        // No space before/after dot
        if (type1 == DelugeTypes.DOT || type2 == DelugeTypes.DOT) {
            return noSpace();
        }

        // No space after NOT operator
        if (type1 == DelugeTypes.NOT) {
            return noSpace();
        }

        // Space after keywords: if, else, for, each, in, while, return, try, catch
        if (isKeyword(type1) && type2 != DelugeTypes.LPAREN && type2 != DelugeTypes.SEMICOLON) {
            return singleSpace();
        }

        // Space after closing paren before opening brace (already covered above)
        // Space between identifiers/literals (at least one space)
        if ((isValueToken(type1) || type1 == DelugeTypes.RPAREN) &&
                (isValueToken(type2) || isKeyword(type2))) {
            return singleSpace();
        }

        return spacingBuilder.getSpacing(this, child1, child2);
    }

    @Override
    public boolean isLeaf() {
        return myNode.getFirstChildNode() == null;
    }

    @Override
    public @NotNull ChildAttributes getChildAttributes(int newChildIndex) {
        return new ChildAttributes(Indent.getNoneIndent(), null);
    }

    // --- Helpers ---

    private static @Nullable IElementType getBlockType(@NotNull Block block) {
        if (block instanceof DelugeBlock) {
            return ((DelugeBlock) block).myNode.getElementType();
        }
        return null;
    }

    private static Spacing singleSpace() {
        return Spacing.createSpacing(1, 1, 0, false, 0);
    }

    private static Spacing noSpace() {
        return Spacing.createSpacing(0, 0, 0, false, 0);
    }

    private static Spacing newline() {
        return Spacing.createSpacing(0, 0, 1, true, 1);
    }

    private static boolean isComparisonOrLogical(IElementType type) {
        return type == DelugeTypes.EQUALS || type == DelugeTypes.NOT_EQUALS ||
                type == DelugeTypes.GREATER_THAN || type == DelugeTypes.LESS_THAN ||
                type == DelugeTypes.GREATER_EQUALS || type == DelugeTypes.LESS_EQUALS ||
                type == DelugeTypes.AND || type == DelugeTypes.OR;
    }

    private static boolean isArithmetic(IElementType type) {
        return type == DelugeTypes.PLUS || type == DelugeTypes.MINUS ||
                type == DelugeTypes.MULTIPLY || type == DelugeTypes.DIVIDE;
    }

    private static boolean isKeyword(IElementType type) {
        return type == DelugeTypes.IF || type == DelugeTypes.ELSE ||
                type == DelugeTypes.FOR || type == DelugeTypes.EACH ||
                type == DelugeTypes.IN || type == DelugeTypes.WHILE ||
                type == DelugeTypes.RETURN || type == DelugeTypes.TRY ||
                type == DelugeTypes.CATCH || type == DelugeTypes.BREAK ||
                type == DelugeTypes.CONTINUE || type == DelugeTypes.VOID ||
                type == DelugeTypes.STRING_TYPE || type == DelugeTypes.MAP_TYPE ||
                type == DelugeTypes.INT_TYPE || type == DelugeTypes.LIST_TYPE ||
                type == DelugeTypes.BOOL_TYPE;
    }

    private static boolean isValueToken(IElementType type) {
        return type == DelugeTypes.IDENTIFIER || type == DelugeTypes.NUMBER ||
                type == DelugeTypes.DECIMAL || type == DelugeTypes.STRING ||
                type == DelugeTypes.TRUE || type == DelugeTypes.FALSE ||
                type == DelugeTypes.NULL;
    }
}
