package org.zohocrm.deluge.standards;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Deluge Coding Standards v2.3
 *
 * This class defines all coding standards for Deluge language support.
 */
public class DelugeStandards {

    // ====================
    // NAMING CONVENTIONS
    // ====================

    /**
     * Variables must use snake_case
     */
    public static final String VARIABLE_NAMING_PATTERN = "^[a-z][a-z0-9_]*$";

    /**
     * Functions must use camelCase
     */
    public static final String FUNCTION_NAMING_PATTERN = "^[a-z][a-zA-Z0-9]*$";

    /**
     * Constants must use UPPERCASE_WITH_UNDERSCORES
     */
    public static final String CONSTANT_NAMING_PATTERN = "^[A-Z][A-Z0-9_]*$";

    // ====================
    // FUNCTION TYPES
    // ====================

    public static final Set<String> FUNCTION_TYPES = new HashSet<>(Arrays.asList(
        "standalone",
        "automation",
        "validation_rule",
        "schedule",
        "related_list",
        "button"
    ));

    /**
     * Standalone functions MUST return string type
     */
    public static final String STANDALONE_RETURN_TYPE = "string";

    /**
     * Automation functions MUST use void return type
     */
    public static final String AUTOMATION_RETURN_TYPE = "void";

    /**
     * Validation rule functions MUST return map type
     */
    public static final String VALIDATION_RETURN_TYPE = "map";

    /**
     * Schedule functions MUST use void return type
     */
    public static final String SCHEDULE_RETURN_TYPE = "void";

    /**
     * Related list functions MUST return string type (XML)
     */
    public static final String RELATED_LIST_RETURN_TYPE = "string";

    /**
     * Button functions MUST return string type
     */
    public static final String BUTTON_RETURN_TYPE = "string";

    // ====================
    // FORBIDDEN SYNTAX
    // ====================

    /**
     * These JavaScript-like methods are not supported in Deluge
     */
    public static final Set<String> FORBIDDEN_METHODS = new HashSet<>(Arrays.asList(
        ".empty",
        ".isNotNull()",
        ".toBoolean()"
    ));

    /**
     * Typed variable declarations are NOT allowed (variables are dynamically typed)
     */
    public static final Set<String> FORBIDDEN_TYPE_DECLARATIONS = new HashSet<>(Arrays.asList(
        "map ",
        "list ",
        "string ",
        "int ",
        "bool ",
        "Map ",
        "List ",
        "String ",
        "Int ",
        "Bool "
    ));

    // ====================
    // ALLOWED FUNCTIONS
    // ====================

    public static final Set<String> ALLOWED_FUNCTIONS = new HashSet<>(Arrays.asList(
        // Collection creation
        "Map", "List",
        // Null checking
        "ifnull", "isNull", "isBlank", "isEmpty",
        // Type conversion
        "toLong", "toDecimal", "toDate", "toList", "toMap",
        "toString", "toJSONString", "toNumber", "toFile",
        // String manipulation
        "trim", "toUpperCase", "toLowerCase", "leftpad", "replaceAll",
        // Math functions
        "ceil", "round", "floor",
        // Encoding
        "base64Encode", "base64Decode", "urlEncode"
    ));

    // ====================
    // ZOHO API FUNCTIONS
    // ====================

    public static final Set<String> ZOHO_CRM_FUNCTIONS = new HashSet<>(Arrays.asList(
        "getRecordById",
        "getRelatedRecords",
        "searchRecords",
        "updateRecord",
        "createRecord",
        "deleteRecord",
        "getRecords",
        "getOrgVariable",
        "attachFile"
    ));

    public static final Set<String> ZOHO_ENCRYPTION_FUNCTIONS = new HashSet<>(Arrays.asList(
        "base64Encode",
        "base64Decode",
        "urlEncode"
    ));

    // ====================
    // LOGGING STANDARDS
    // ====================

    /**
     * Required logging method for all errors
     */
    public static final String DEVELOPER_LOG_METHOD = "standalone.developerLog";

    /**
     * Valid log types
     */
    public static final Set<String> LOG_TYPES = new HashSet<>(Arrays.asList(
        "info",
        "warning",
        "error"
    ));

    // ====================
    // STRUCTURE REQUIREMENTS
    // ====================

    /**
     * Standalone functions must have a fake return statement
     */
    public static final String FAKE_RETURN_PATTERN = "return\\s+[\"'][^\"']*[\"'];\\s*$";

    /**
     * All function logic must be wrapped in try/catch
     */
    public static final boolean REQUIRE_TRY_CATCH = true;

    /**
     * Catch blocks must include error logging for standalone functions
     */
    public static final boolean REQUIRE_CATCH_LOGGING = true;

    // ====================
    // RETURN OBJECT STANDARDS
    // ====================

    /**
     * Standard success response keys
     */
    public static final String SUCCESS_KEY = "success";
    public static final String STATUS_KEY = "status";
    public static final String DATA_KEY = "data";
    public static final String ERROR_KEY = "error";
    public static final String MESSAGE_KEY = "message";

    // ====================
    // VALIDATION MESSAGES
    // ====================

    public static final String MSG_USE_SNAKE_CASE = "Variable names must use snake_case (e.g., deal_id, variant_list)";
    public static final String MSG_USE_CAMEL_CASE = "Function names must use camelCase (e.g., updateVariant, calculateSOP)";
    public static final String MSG_FORBIDDEN_SYNTAX = "This syntax is not supported in Deluge. Use Deluge-safe alternatives.";
    public static final String MSG_TYPED_VARIABLE = "Variable declarations must not have type annotations. Deluge is dynamically typed.";
    public static final String MSG_MISSING_TRY_CATCH = "All function logic must be wrapped in try/catch block";
    public static final String MSG_MISSING_FAKE_RETURN = "Standalone functions must include unreachable return statement after catch block";
    public static final String MSG_MISSING_DEVELOPER_LOG = "Catch blocks must log errors using standalone.developerLog()";
    public static final String MSG_WRONG_RETURN_TYPE = "Function return type does not match standards for this function type";
    public static final String MSG_USE_FOR_EACH = "Use 'for each' loop instead of Java-style for loop";
    public static final String MSG_CONVERT_TO_LONG = "IDs must be converted using .toLong() before API calls";
    public static final String MSG_FORBIDDEN_THROW = "'throw' is not supported in Deluge. Use return with error map instead.";
    public static final String MSG_FORBIDDEN_TERNARY = "Ternary operator '?' is not supported in Deluge. Use if/else instead.";
    public static final String MSG_FORBIDDEN_METHOD = "Method '%s' is not supported in Deluge. Use Deluge-safe alternatives.";
    public static final String MSG_FORBIDDEN_WHILE = "'while' loops are not supported in Deluge. Use 'for each' instead.";
    public static final String MSG_MISSING_BRACES_IF = "All 'if' statements must use braces '{}'. Inline if without braces is forbidden.";
    public static final String MSG_MISSING_BRACES_FOR = "All 'for each' loops must use braces '{}'. Inline for without braces is forbidden.";
    public static final String MSG_MISSING_SEMICOLON_RETURN = "Missing semicolon after 'return' statement";
    public static final String MSG_COMMENT_OUTSIDE_FUNCTION = "Comments are not allowed outside function body. Place comments inside the function.";

    // ====================
    // FILE EXTENSIONS
    // ====================

    /**
     * Preferred file extension for Deluge functions
     */
    public static final String DELUGE_FILE_EXTENSION = ".deluge";

    /**
     * Alternative file extension (for compatibility)
     */
    public static final String JAVASCRIPT_FILE_EXTENSION = ".js";

    // ====================
    // HELPER METHODS
    // ====================

    /**
     * Check if a variable name follows snake_case convention
     */
    public static boolean isValidVariableName(String name) {
        return name != null && name.matches(VARIABLE_NAMING_PATTERN);
    }

    /**
     * Check if a function name follows camelCase convention
     */
    public static boolean isValidFunctionName(String name) {
        return name != null && name.matches(FUNCTION_NAMING_PATTERN);
    }

    /**
     * Check if a constant name follows UPPERCASE convention
     */
    public static boolean isValidConstantName(String name) {
        return name != null && name.matches(CONSTANT_NAMING_PATTERN);
    }

    /**
     * Check if the return type is correct for the function type
     */
    public static boolean isCorrectReturnType(String functionType, String returnType) {
        if (functionType == null || returnType == null) {
            return false;
        }

        switch (functionType.toLowerCase()) {
            case "standalone":
                return STANDALONE_RETURN_TYPE.equals(returnType.toLowerCase());
            case "automation":
            case "schedule":
                return AUTOMATION_RETURN_TYPE.equals(returnType.toLowerCase());
            case "validation_rule":
                return VALIDATION_RETURN_TYPE.equals(returnType.toLowerCase());
            case "related_list":
                return RELATED_LIST_RETURN_TYPE.equals(returnType.toLowerCase());
            case "button":
                return BUTTON_RETURN_TYPE.equals(returnType.toLowerCase());
            default:
                return false;
        }
    }

    /**
     * Check if code contains forbidden syntax
     */
    public static boolean containsForbiddenSyntax(String code) {
        if (code == null) {
            return false;
        }

        for (String forbidden : FORBIDDEN_METHODS) {
            if (code.contains(forbidden)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Check if code contains typed variable declarations
     */
    public static boolean containsTypedVariableDeclaration(String code) {
        if (code == null) {
            return false;
        }

        // Check for patterns like "map variable_name =" or "Map variable_name ="
        for (String typeDecl : FORBIDDEN_TYPE_DECLARATIONS) {
            if (code.matches(".*\\b" + typeDecl.trim() + "\\s+[a-z_][a-z0-9_]*\\s*=.*")) {
                return true;
            }
        }

        return false;
    }
}

