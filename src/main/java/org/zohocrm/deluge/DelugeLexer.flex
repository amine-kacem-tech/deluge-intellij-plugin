package org.zohocrm.deluge;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import org.zohocrm.deluge.psi.DelugeTypes;
import com.intellij.psi.TokenType;

%%

%class DelugeLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType
%eof{  return;
%eof}

// Whitespace
WHITE_SPACE = [ \t\n\r\f]+

// Comments
LINE_COMMENT = "//" [^\r\n]*
BLOCK_COMMENT = "/*" ( ([^"*"]|[\r\n])* ("*"+ [^"*""/"] )? )* ("*"+ "/")?

// Identifiers
IDENTIFIER = [a-zA-Z_][a-zA-Z0-9_]*

// Numbers
NUMBER = [0-9]+
DECIMAL = [0-9]+ \. [0-9]+

// Strings
STRING = \"([^\\\"\r\n]|\\[^\r\n])*\" | '([^\\'\r\n]|\\[^\r\n])*'

%%

// Whitespace
{WHITE_SPACE}       { return TokenType.WHITE_SPACE; }

// Comments
{LINE_COMMENT}      { return DelugeTypes.COMMENT; }
{BLOCK_COMMENT}     { return DelugeTypes.COMMENT; }

// Keywords - Return Types
"void"              { return DelugeTypes.VOID; }
"string"            { return DelugeTypes.STRING_TYPE; }
"map"               { return DelugeTypes.MAP_TYPE; }
"int"               { return DelugeTypes.INT_TYPE; }
"list"              { return DelugeTypes.LIST_TYPE; }
"bool"              { return DelugeTypes.BOOL_TYPE; }

// Keywords - Parameter Types (capitalized)
"String"            { return DelugeTypes.STRING_PARAM; }
"Int"               { return DelugeTypes.INT_PARAM; }
"Map"               { return DelugeTypes.MAP_PARAM; }
"List"              { return DelugeTypes.LIST_PARAM; }
"Bool"              { return DelugeTypes.BOOL_PARAM; }

// Keywords - Namespaces
"standalone"        { return DelugeTypes.STANDALONE; }
"automation"        { return DelugeTypes.AUTOMATION; }
"validation_rule"   { return DelugeTypes.VALIDATION_RULE; }
"schedule"          { return DelugeTypes.SCHEDULE; }
"related_list"      { return DelugeTypes.RELATED_LIST; }
"button"            { return DelugeTypes.BUTTON; }

// Keywords - Control Flow
"if"                { return DelugeTypes.IF; }
"else"              { return DelugeTypes.ELSE; }
"for"               { return DelugeTypes.FOR; }
"each"              { return DelugeTypes.EACH; }
"in"                { return DelugeTypes.IN; }
"while"             { return DelugeTypes.WHILE; }
"return"            { return DelugeTypes.RETURN; }
"break"             { return DelugeTypes.BREAK; }
"continue"          { return DelugeTypes.CONTINUE; }

// Keywords - Exception Handling
"try"               { return DelugeTypes.TRY; }
"catch"             { return DelugeTypes.CATCH; }

// Keywords - Boolean
"true"              { return DelugeTypes.TRUE; }
"false"             { return DelugeTypes.FALSE; }
"True"              { return DelugeTypes.TRUE; }
"False"             { return DelugeTypes.FALSE; }
"null"              { return DelugeTypes.NULL; }

// Keywords - Operators (Deluge-specific)
"is"                { return DelugeTypes.IS; }

// Built-in Functions - Constructors
"Map"               { return DelugeTypes.MAP_CONSTRUCTOR; }
"List"              { return DelugeTypes.LIST_CONSTRUCTOR; }

// Built-in Functions - Null Checking
"isNull"            { return DelugeTypes.IS_NULL; }
"isnull"            { return DelugeTypes.IS_NULL; }
"isBlank"           { return DelugeTypes.IS_BLANK; }
"isblank"           { return DelugeTypes.IS_BLANK; }
"isEmpty"           { return DelugeTypes.IS_EMPTY; }
"isempty"           { return DelugeTypes.IS_EMPTY; }
"ifNull"            { return DelugeTypes.IF_NULL; }
"ifnull"            { return DelugeTypes.IF_NULL; }

// Built-in Functions - Type Checking
"isText"            { return DelugeTypes.IS_TEXT; }
"isNumber"          { return DelugeTypes.IS_NUMBER; }

// Built-in Functions - Conversion
"toLong"            { return DelugeTypes.TO_LONG; }
"toDecimal"         { return DelugeTypes.TO_DECIMAL; }
"toDate"            { return DelugeTypes.TO_DATE; }
"toList"            { return DelugeTypes.TO_LIST; }
"toMap"             { return DelugeTypes.TO_MAP; }
"toString"          { return DelugeTypes.TO_STRING; }
"toJSONString"      { return DelugeTypes.TO_JSON_STRING; }
"toNumber"          { return DelugeTypes.TO_NUMBER; }
"toFile"            { return DelugeTypes.TO_FILE; }

// Built-in Functions - Math
"ceil"              { return DelugeTypes.CEIL; }
"round"             { return DelugeTypes.ROUND; }
"floor"             { return DelugeTypes.FLOOR; }

// Built-in Functions - String
"leftpad"           { return DelugeTypes.LEFTPAD; }
"replaceAll"        { return DelugeTypes.REPLACE_ALL; }
"trim"              { return DelugeTypes.TRIM; }
"toUpperCase"       { return DelugeTypes.TO_UPPER_CASE; }
"toLowerCase"       { return DelugeTypes.TO_LOWER_CASE; }

// Built-in Functions - Logging
"info"              { return DelugeTypes.INFO; }

// Zoho API - zoho.crm
"zoho"              { return DelugeTypes.ZOHO; }
"crm"               { return DelugeTypes.CRM; }
"getRecordById"     { return DelugeTypes.GET_RECORD_BY_ID; }
"getRelatedRecords" { return DelugeTypes.GET_RELATED_RECORDS; }
"searchRecords"     { return DelugeTypes.SEARCH_RECORDS; }
"updateRecord"      { return DelugeTypes.UPDATE_RECORD; }
"createRecord"      { return DelugeTypes.CREATE_RECORD; }
"deleteRecord"      { return DelugeTypes.DELETE_RECORD; }
"getRecords"        { return DelugeTypes.GET_RECORDS; }
"getOrgVariable"    { return DelugeTypes.GET_ORG_VARIABLE; }
"attachFile"        { return DelugeTypes.ATTACH_FILE; }

// Zoho API - zoho.encryption
"encryption"        { return DelugeTypes.ENCRYPTION; }
"base64Encode"      { return DelugeTypes.BASE64_ENCODE; }
"base64Decode"      { return DelugeTypes.BASE64_DECODE; }
"urlEncode"         { return DelugeTypes.URL_ENCODE; }

// Zoho API - Other
"invokeurl"         { return DelugeTypes.INVOKE_URL; }
"loginuserid"       { return DelugeTypes.LOGIN_USER_ID; }
"now"               { return DelugeTypes.NOW; }

// Operators
"+"                 { return DelugeTypes.PLUS; }
"-"                 { return DelugeTypes.MINUS; }
"*"                 { return DelugeTypes.MULTIPLY; }
"/"                 { return DelugeTypes.DIVIDE; }
"="                 { return DelugeTypes.ASSIGN; }
"=="                { return DelugeTypes.EQUALS; }
"!="                { return DelugeTypes.NOT_EQUALS; }
">"                 { return DelugeTypes.GREATER_THAN; }
"<"                 { return DelugeTypes.LESS_THAN; }
">="                { return DelugeTypes.GREATER_EQUALS; }
"<="                { return DelugeTypes.LESS_EQUALS; }
"&&"                { return DelugeTypes.AND; }
"||"                { return DelugeTypes.OR; }
"!"                 { return DelugeTypes.NOT; }

// Delimiters
"{"                 { return DelugeTypes.LBRACE; }
"}"                 { return DelugeTypes.RBRACE; }
"("                 { return DelugeTypes.LPAREN; }
")"                 { return DelugeTypes.RPAREN; }
"["                 { return DelugeTypes.LBRACKET; }
"]"                 { return DelugeTypes.RBRACKET; }
";"                 { return DelugeTypes.SEMICOLON; }
","                 { return DelugeTypes.COMMA; }
"."                 { return DelugeTypes.DOT; }
":"                 { return DelugeTypes.COLON; }

// Literals
{NUMBER}            { return DelugeTypes.NUMBER; }
{DECIMAL}           { return DelugeTypes.DECIMAL; }
{STRING}            { return DelugeTypes.STRING; }

// Identifier (must be last)
{IDENTIFIER}        { return DelugeTypes.IDENTIFIER; }

// Error
[^]                 { return TokenType.BAD_CHARACTER; }

