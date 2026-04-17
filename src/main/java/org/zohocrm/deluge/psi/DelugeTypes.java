package org.zohocrm.deluge.psi;

import com.intellij.psi.tree.IElementType;
import org.zohocrm.deluge.DelugeLanguage;

/**
 * Token types for Deluge language
 */
public interface DelugeTypes {

    // Operators
    IElementType NOT = new DelugeTokenType("NOT");
    IElementType PLUS = new DelugeTokenType("PLUS");
    IElementType MINUS = new DelugeTokenType("MINUS");
    IElementType MULTIPLY = new DelugeTokenType("MULTIPLY");
    IElementType DIVIDE = new DelugeTokenType("DIVIDE");
    IElementType ASSIGN = new DelugeTokenType("ASSIGN");
    IElementType EQUALS = new DelugeTokenType("EQUALS");
    IElementType NOT_EQUALS = new DelugeTokenType("NOT_EQUALS");
    IElementType GREATER_THAN = new DelugeTokenType("GREATER_THAN");
    IElementType LESS_THAN = new DelugeTokenType("LESS_THAN");
    IElementType GREATER_EQUALS = new DelugeTokenType("GREATER_EQUALS");
    IElementType LESS_EQUALS = new DelugeTokenType("LESS_EQUALS");
    IElementType AND = new DelugeTokenType("AND");
    IElementType OR = new DelugeTokenType("OR");
    IElementType IS = new DelugeTokenType("IS");

    // Delimiters
    IElementType LPAREN = new DelugeTokenType("LPAREN");
    IElementType RPAREN = new DelugeTokenType("RPAREN");
    IElementType LBRACE = new DelugeTokenType("LBRACE");
    IElementType RBRACE = new DelugeTokenType("RBRACE");
    IElementType LBRACKET = new DelugeTokenType("LBRACKET");
    IElementType RBRACKET = new DelugeTokenType("RBRACKET");
    IElementType SEMICOLON = new DelugeTokenType("SEMICOLON");
    IElementType COMMA = new DelugeTokenType("COMMA");
    IElementType DOT = new DelugeTokenType("DOT");
    IElementType COLON = new DelugeTokenType("COLON");

    // Literals
    IElementType NUMBER = new DelugeTokenType("NUMBER");
    IElementType DECIMAL = new DelugeTokenType("DECIMAL");
    IElementType STRING = new DelugeTokenType("STRING");
    IElementType IDENTIFIER = new DelugeTokenType("IDENTIFIER");

    // Comments
    IElementType COMMENT = new DelugeTokenType("COMMENT");

    // Keywords - Return Types
    IElementType VOID = new DelugeTokenType("VOID");
    IElementType STRING_TYPE = new DelugeTokenType("STRING_TYPE");
    IElementType MAP_TYPE = new DelugeTokenType("MAP_TYPE");
    IElementType INT_TYPE = new DelugeTokenType("INT_TYPE");
    IElementType LIST_TYPE = new DelugeTokenType("LIST_TYPE");
    IElementType BOOL_TYPE = new DelugeTokenType("BOOL_TYPE");

    // Keywords - Parameter Types
    IElementType STRING_PARAM = new DelugeTokenType("STRING_PARAM");
    IElementType INT_PARAM = new DelugeTokenType("INT_PARAM");
    IElementType MAP_PARAM = new DelugeTokenType("MAP_PARAM");
    IElementType LIST_PARAM = new DelugeTokenType("LIST_PARAM");
    IElementType BOOL_PARAM = new DelugeTokenType("BOOL_PARAM");

    // Keywords - Namespaces
    IElementType STANDALONE = new DelugeTokenType("STANDALONE");
    IElementType AUTOMATION = new DelugeTokenType("AUTOMATION");
    IElementType VALIDATION_RULE = new DelugeTokenType("VALIDATION_RULE");
    IElementType SCHEDULE = new DelugeTokenType("SCHEDULE");
    IElementType RELATED_LIST = new DelugeTokenType("RELATED_LIST");
    IElementType BUTTON = new DelugeTokenType("BUTTON");

    // Keywords - Control Flow
    IElementType IF = new DelugeTokenType("IF");
    IElementType ELSE = new DelugeTokenType("ELSE");
    IElementType FOR = new DelugeTokenType("FOR");
    IElementType EACH = new DelugeTokenType("EACH");
    IElementType IN = new DelugeTokenType("IN");
    IElementType WHILE = new DelugeTokenType("WHILE");
    IElementType RETURN = new DelugeTokenType("RETURN");
    IElementType BREAK = new DelugeTokenType("BREAK");
    IElementType CONTINUE = new DelugeTokenType("CONTINUE");

    // Keywords - Exception Handling
    IElementType TRY = new DelugeTokenType("TRY");
    IElementType CATCH = new DelugeTokenType("CATCH");

    // Keywords - Boolean
    IElementType TRUE = new DelugeTokenType("TRUE");
    IElementType FALSE = new DelugeTokenType("FALSE");
    IElementType NULL = new DelugeTokenType("NULL");

    // Built-in Functions - Constructors
    IElementType MAP_CONSTRUCTOR = new DelugeTokenType("MAP_CONSTRUCTOR");
    IElementType LIST_CONSTRUCTOR = new DelugeTokenType("LIST_CONSTRUCTOR");

    // Built-in Functions - Null Checking
    IElementType IS_NULL = new DelugeTokenType("IS_NULL");
    IElementType IS_BLANK = new DelugeTokenType("IS_BLANK");
    IElementType IS_EMPTY = new DelugeTokenType("IS_EMPTY");
    IElementType IF_NULL = new DelugeTokenType("IF_NULL");

    // Built-in Functions - Type Checking
    IElementType IS_TEXT = new DelugeTokenType("IS_TEXT");
    IElementType IS_NUMBER = new DelugeTokenType("IS_NUMBER");

    // Built-in Functions - Conversion
    IElementType TO_LONG = new DelugeTokenType("TO_LONG");
    IElementType TO_DECIMAL = new DelugeTokenType("TO_DECIMAL");
    IElementType TO_DATE = new DelugeTokenType("TO_DATE");
    IElementType TO_LIST = new DelugeTokenType("TO_LIST");
    IElementType TO_MAP = new DelugeTokenType("TO_MAP");
    IElementType TO_STRING = new DelugeTokenType("TO_STRING");
    IElementType TO_JSON_STRING = new DelugeTokenType("TO_JSON_STRING");
    IElementType TO_NUMBER = new DelugeTokenType("TO_NUMBER");
    IElementType TO_FILE = new DelugeTokenType("TO_FILE");

    // Built-in Functions - Math
    IElementType CEIL = new DelugeTokenType("CEIL");
    IElementType ROUND = new DelugeTokenType("ROUND");
    IElementType FLOOR = new DelugeTokenType("FLOOR");

    // Built-in Functions - String
    IElementType LEFTPAD = new DelugeTokenType("LEFTPAD");
    IElementType REPLACE_ALL = new DelugeTokenType("REPLACE_ALL");
    IElementType TRIM = new DelugeTokenType("TRIM");
    IElementType TO_UPPER_CASE = new DelugeTokenType("TO_UPPER_CASE");
    IElementType TO_LOWER_CASE = new DelugeTokenType("TO_LOWER_CASE");

    // Built-in Functions - Logging
    IElementType INFO = new DelugeTokenType("INFO");

    // Zoho API - zoho.crm
    IElementType ZOHO = new DelugeTokenType("ZOHO");
    IElementType CRM = new DelugeTokenType("CRM");
    IElementType GET_RECORD_BY_ID = new DelugeTokenType("GET_RECORD_BY_ID");
    IElementType GET_RELATED_RECORDS = new DelugeTokenType("GET_RELATED_RECORDS");
    IElementType SEARCH_RECORDS = new DelugeTokenType("SEARCH_RECORDS");
    IElementType UPDATE_RECORD = new DelugeTokenType("UPDATE_RECORD");
    IElementType CREATE_RECORD = new DelugeTokenType("CREATE_RECORD");
    IElementType DELETE_RECORD = new DelugeTokenType("DELETE_RECORD");
    IElementType GET_RECORDS = new DelugeTokenType("GET_RECORDS");
    IElementType GET_ORG_VARIABLE = new DelugeTokenType("GET_ORG_VARIABLE");
    IElementType ATTACH_FILE = new DelugeTokenType("ATTACH_FILE");

    // Zoho API - zoho.encryption
    IElementType ENCRYPTION = new DelugeTokenType("ENCRYPTION");
    IElementType BASE64_ENCODE = new DelugeTokenType("BASE64_ENCODE");
    IElementType BASE64_DECODE = new DelugeTokenType("BASE64_DECODE");
    IElementType URL_ENCODE = new DelugeTokenType("URL_ENCODE");

    // Zoho API - Other
    IElementType INVOKE_URL = new DelugeTokenType("INVOKE_URL");
    IElementType LOGIN_USER_ID = new DelugeTokenType("LOGIN_USER_ID");
    IElementType NOW = new DelugeTokenType("NOW");

    /**
     * Token type implementation for Deluge
     */
    class DelugeTokenType extends IElementType {
        public DelugeTokenType(String debugName) {
            super(debugName, DelugeLanguage.INSTANCE);
        }

        @Override
        public String toString() {
            return "DelugeTokenType." + super.toString();
        }
    }
}

