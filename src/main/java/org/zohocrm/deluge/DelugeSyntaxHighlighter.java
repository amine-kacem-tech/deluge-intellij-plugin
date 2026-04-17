package org.zohocrm.deluge;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.zohocrm.deluge.psi.DelugeTypes;

import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;

public class DelugeSyntaxHighlighter extends SyntaxHighlighterBase {

    public static final TextAttributesKey KEYWORD =
            createTextAttributesKey("DELUGE_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD);
    public static final TextAttributesKey STRING =
            createTextAttributesKey("DELUGE_STRING", DefaultLanguageHighlighterColors.STRING);
    public static final TextAttributesKey NUMBER =
            createTextAttributesKey("DELUGE_NUMBER", DefaultLanguageHighlighterColors.NUMBER);
    public static final TextAttributesKey COMMENT =
            createTextAttributesKey("DELUGE_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT);
    public static final TextAttributesKey OPERATOR =
            createTextAttributesKey("DELUGE_OPERATOR", DefaultLanguageHighlighterColors.OPERATION_SIGN);
    public static final TextAttributesKey PARENTHESES =
            createTextAttributesKey("DELUGE_PARENTHESES", DefaultLanguageHighlighterColors.PARENTHESES);
    public static final TextAttributesKey BRACES =
            createTextAttributesKey("DELUGE_BRACES", DefaultLanguageHighlighterColors.BRACES);
    public static final TextAttributesKey BRACKETS =
            createTextAttributesKey("DELUGE_BRACKETS", DefaultLanguageHighlighterColors.BRACKETS);
    public static final TextAttributesKey COMMA =
            createTextAttributesKey("DELUGE_COMMA", DefaultLanguageHighlighterColors.COMMA);
    public static final TextAttributesKey DOT =
            createTextAttributesKey("DELUGE_DOT", DefaultLanguageHighlighterColors.DOT);
    public static final TextAttributesKey SEMICOLON =
            createTextAttributesKey("DELUGE_SEMICOLON", DefaultLanguageHighlighterColors.SEMICOLON);
    public static final TextAttributesKey FUNCTION_CALL =
            createTextAttributesKey("DELUGE_FUNCTION_CALL", DefaultLanguageHighlighterColors.FUNCTION_CALL);
    public static final TextAttributesKey IDENTIFIER =
            createTextAttributesKey("DELUGE_IDENTIFIER", DefaultLanguageHighlighterColors.IDENTIFIER);

    private static final TextAttributesKey[] KEYWORD_KEYS = new TextAttributesKey[]{KEYWORD};
    private static final TextAttributesKey[] STRING_KEYS = new TextAttributesKey[]{STRING};
    private static final TextAttributesKey[] NUMBER_KEYS = new TextAttributesKey[]{NUMBER};
    private static final TextAttributesKey[] COMMENT_KEYS = new TextAttributesKey[]{COMMENT};
    private static final TextAttributesKey[] OPERATOR_KEYS = new TextAttributesKey[]{OPERATOR};
    private static final TextAttributesKey[] PARENTHESES_KEYS = new TextAttributesKey[]{PARENTHESES};
    private static final TextAttributesKey[] BRACES_KEYS = new TextAttributesKey[]{BRACES};
    private static final TextAttributesKey[] BRACKETS_KEYS = new TextAttributesKey[]{BRACKETS};
    private static final TextAttributesKey[] COMMA_KEYS = new TextAttributesKey[]{COMMA};
    private static final TextAttributesKey[] DOT_KEYS = new TextAttributesKey[]{DOT};
    private static final TextAttributesKey[] SEMICOLON_KEYS = new TextAttributesKey[]{SEMICOLON};
    private static final TextAttributesKey[] FUNCTION_CALL_KEYS = new TextAttributesKey[]{FUNCTION_CALL};
    private static final TextAttributesKey[] IDENTIFIER_KEYS = new TextAttributesKey[]{IDENTIFIER};
    private static final TextAttributesKey[] EMPTY_KEYS = new TextAttributesKey[0];

    @NotNull
    @Override
    public Lexer getHighlightingLexer() {
        return new DelugeLexerAdapter();
    }

    @NotNull
    @Override
    public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
        // Keywords
        if (tokenType == DelugeTypes.IF || tokenType == DelugeTypes.ELSE ||
            tokenType == DelugeTypes.FOR || tokenType == DelugeTypes.EACH ||
            tokenType == DelugeTypes.IN || tokenType == DelugeTypes.WHILE ||
            tokenType == DelugeTypes.RETURN || tokenType == DelugeTypes.BREAK ||
            tokenType == DelugeTypes.CONTINUE || tokenType == DelugeTypes.TRY ||
            tokenType == DelugeTypes.CATCH || tokenType == DelugeTypes.IS ||
            tokenType == DelugeTypes.VOID || tokenType == DelugeTypes.STRING_TYPE ||
            tokenType == DelugeTypes.MAP_TYPE || tokenType == DelugeTypes.INT_TYPE ||
            tokenType == DelugeTypes.LIST_TYPE || tokenType == DelugeTypes.BOOL_TYPE ||
            tokenType == DelugeTypes.STRING_PARAM || tokenType == DelugeTypes.INT_PARAM ||
            tokenType == DelugeTypes.MAP_PARAM || tokenType == DelugeTypes.LIST_PARAM ||
            tokenType == DelugeTypes.BOOL_PARAM || tokenType == DelugeTypes.STANDALONE ||
            tokenType == DelugeTypes.AUTOMATION || tokenType == DelugeTypes.VALIDATION_RULE ||
            tokenType == DelugeTypes.SCHEDULE || tokenType == DelugeTypes.RELATED_LIST ||
            tokenType == DelugeTypes.BUTTON || tokenType == DelugeTypes.TRUE ||
            tokenType == DelugeTypes.FALSE || tokenType == DelugeTypes.NULL) {
            return KEYWORD_KEYS;
        }

        // Built-in functions
        if (tokenType == DelugeTypes.MAP_CONSTRUCTOR || tokenType == DelugeTypes.LIST_CONSTRUCTOR ||
            tokenType == DelugeTypes.IS_NULL || tokenType == DelugeTypes.IS_BLANK ||
            tokenType == DelugeTypes.IS_EMPTY || tokenType == DelugeTypes.IF_NULL ||
            tokenType == DelugeTypes.IS_TEXT || tokenType == DelugeTypes.IS_NUMBER ||
            tokenType == DelugeTypes.TO_LONG || tokenType == DelugeTypes.TO_DECIMAL ||
            tokenType == DelugeTypes.TO_DATE || tokenType == DelugeTypes.TO_LIST ||
            tokenType == DelugeTypes.TO_MAP || tokenType == DelugeTypes.TO_STRING ||
            tokenType == DelugeTypes.TO_JSON_STRING || tokenType == DelugeTypes.TO_NUMBER ||
            tokenType == DelugeTypes.TO_FILE || tokenType == DelugeTypes.CEIL ||
            tokenType == DelugeTypes.ROUND || tokenType == DelugeTypes.FLOOR ||
            tokenType == DelugeTypes.LEFTPAD || tokenType == DelugeTypes.REPLACE_ALL ||
            tokenType == DelugeTypes.TRIM || tokenType == DelugeTypes.TO_UPPER_CASE ||
            tokenType == DelugeTypes.TO_LOWER_CASE || tokenType == DelugeTypes.INFO ||
            tokenType == DelugeTypes.ZOHO || tokenType == DelugeTypes.CRM ||
            tokenType == DelugeTypes.GET_RECORD_BY_ID || tokenType == DelugeTypes.GET_RELATED_RECORDS ||
            tokenType == DelugeTypes.SEARCH_RECORDS || tokenType == DelugeTypes.UPDATE_RECORD ||
            tokenType == DelugeTypes.CREATE_RECORD || tokenType == DelugeTypes.DELETE_RECORD ||
            tokenType == DelugeTypes.GET_RECORDS || tokenType == DelugeTypes.GET_ORG_VARIABLE ||
            tokenType == DelugeTypes.ATTACH_FILE || tokenType == DelugeTypes.ENCRYPTION ||
            tokenType == DelugeTypes.BASE64_ENCODE || tokenType == DelugeTypes.BASE64_DECODE ||
            tokenType == DelugeTypes.URL_ENCODE || tokenType == DelugeTypes.INVOKE_URL ||
            tokenType == DelugeTypes.LOGIN_USER_ID || tokenType == DelugeTypes.NOW) {
            return FUNCTION_CALL_KEYS;
        }

        // Strings
        if (tokenType == DelugeTypes.STRING) {
            return STRING_KEYS;
        }

        // Numbers
        if (tokenType == DelugeTypes.NUMBER || tokenType == DelugeTypes.DECIMAL) {
            return NUMBER_KEYS;
        }

        // Comments
        if (tokenType == DelugeTypes.COMMENT) {
            return COMMENT_KEYS;
        }

        // Operators
        if (tokenType == DelugeTypes.PLUS || tokenType == DelugeTypes.MINUS ||
            tokenType == DelugeTypes.MULTIPLY || tokenType == DelugeTypes.DIVIDE ||
            tokenType == DelugeTypes.ASSIGN || tokenType == DelugeTypes.EQUALS ||
            tokenType == DelugeTypes.NOT_EQUALS || tokenType == DelugeTypes.GREATER_THAN ||
            tokenType == DelugeTypes.LESS_THAN || tokenType == DelugeTypes.GREATER_EQUALS ||
            tokenType == DelugeTypes.LESS_EQUALS || tokenType == DelugeTypes.AND ||
            tokenType == DelugeTypes.OR || tokenType == DelugeTypes.NOT) {
            return OPERATOR_KEYS;
        }

        // Delimiters
        if (tokenType == DelugeTypes.LPAREN || tokenType == DelugeTypes.RPAREN) {
            return PARENTHESES_KEYS;
        }
        if (tokenType == DelugeTypes.LBRACE || tokenType == DelugeTypes.RBRACE) {
            return BRACES_KEYS;
        }
        if (tokenType == DelugeTypes.LBRACKET || tokenType == DelugeTypes.RBRACKET) {
            return BRACKETS_KEYS;
        }
        if (tokenType == DelugeTypes.COMMA) {
            return COMMA_KEYS;
        }
        if (tokenType == DelugeTypes.DOT) {
            return DOT_KEYS;
        }
        if (tokenType == DelugeTypes.SEMICOLON || tokenType == DelugeTypes.COLON) {
            return SEMICOLON_KEYS;
        }

        // Identifiers
        if (tokenType == DelugeTypes.IDENTIFIER) {
            return IDENTIFIER_KEYS;
        }

        return EMPTY_KEYS;
    }
}

