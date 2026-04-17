DELUGE CODING STANDARDS v2 — Full Edition

Below is the complete and expanded version of the Deluge Coding Standards,
including naming, logging, error handling, structuring, API usage,
input validation, return conventions, safe syntax, and AI copilot rules.

---

1. NAMING STANDARDS

---

1.1 Variables

- Use snake_case only (deal_id, sop_value, variant_list).
- Names must reflect actual meaning (updated_variants_count).
- IDs must always be converted to long (deal_long = deal_id.toLong()).
- Never use single-letter or meaningless variable names.
  1.2 Function Names
- Use camelCase for functions (Deluge-friendly).
- Must describe what action the function performs.
- Example: updateVariantSRFDates(), calculateSOPForVariant().
  1.3 Constants
- Must be in uppercase with underscores.
- Example: DEFAULT_PAGE_SIZE = 200.

---

2. CODE STRUCTURING STANDARDS

---

Each function MUST follow this structure:
function name(args)
{
// ===== Initialization =====
try
{
// ===== Input Validation =====
// ===== Fetch Required Records =====
// ===== Business Logic =====
// ===== API Updates =====
// ===== Build Success Response =====
return result_obj;
}
catch (e)
{
// ===== Error Logging =====
return error_obj;
}
return "FAKE_RETURN_TO_SATISFY_DELUGE"; // Should never be reached
}

- All code must be grouped into these blocks.
- No loose code outside try/catch (except variable declarations).

---

3. LOGGING STANDARDS

---

3.1 Required Logging Method
All errors MUST use:
standalone.developerLog(function_name, log_type, log_message, log_user, opt_data);
3.2 Log Types
Allowed values:

- "info"
- "warning"
- "error"
  3.3 Error Log Payload Requirements
  Logs must include:
  {
  "error": e.toString(),
  "input": <complete function input>,
  "context": <additional details if needed>
  }
  3.4 No Excessive Logging
- Only log what is necessary.
- Debug logs allowed only via: info "Debug: <text>";

---

4. ERROR HANDLING STANDARDS

---

4.1 Entire Function Logic Must be Inside try/catch
No exceptions.
4.2 Catch Block Rules

- Must log error with developerLog().
- Must return a structured error object.
- Must NOT rethrow or silently swallow errors.
  4.3 Error Return Format
  error_obj = map();
  error_obj.put("success", false);
  error_obj.put("error", e.toString());
  return error_obj;
  4.4 Fallback Values
- Use map() instead of null for objects.
- Use list() instead of null for lists.
- Use empty string "" instead of null strings.

---

5. DELUGE-SAFE SYNTAX RULES

---

Allowed:

- for each
- ifnull()
- map() / list()
- try/catch
- toLong()
- info
  Forbidden:
- .empty
- .isEmpty()
- .isNotNull()
- .toBoolean()
- JavaScript-like syntax
- Ternary operator
- for loops with Java syntax

---

6. API USAGE STANDARDS

---

6.1 Fetching Records
record = zoho.crm.getRecordById("Deals", deal_long);
record = ifnull(record, map());
6.2 Updating Records

- Only update if update_map.size() > 0.
- Never update with empty map.
- Minimize API calls by reusing variables.
  6.3 Related Records
  related_list = zoho.crm.getRelatedRecords("Module", "Parent", id);
  related_list = ifnull(related_list, list());

---

7. INPUT VALIDATION STANDARDS

---

Input validation must be at the top of the try block:
if (isNull(deal_id) || deal_id == "")
{
throw "Invalid deal_id";
}
No function may run without validating core parameters.

---

8. RETURN OBJECT STANDARDS

---

Success Response:
result_obj = map();
result_obj.put("success", true);
result_obj.put("data", <payload>);
return result_obj;
Error Response:
Defined in error handling section.
Final Fallback:
return "FAKE_RETURN_TO_SATISFY_DELUGE"; // Should never be reached

---

9. AI COPILOT STANDARDS

---

The AI MUST follow:

- Always use snake_case for variables.
- Always wrap logic inside try/catch.
- Always log errors via developerLog().
- Never output JS-like code (.empty, .isNotNull, etc.).
- Always return a Deluge code block when writing code.
- Always include the unreachable fallback return.
- Never assume field names; ask for clarification.
- Always use for each loops.
- Always use map(), list(), ifnull().

---

10. FIELD NAME AND MODULE ASSOCIATION

---

- Always confirm field names with the user or schema.
- Never assume field names; prompt for clarification if not found.
- Always include associated module when listing or generating functions.

---

11. BATCH API USAGE

---

- Prefer batch or bulk API calls (getRecords, COQL) over per-record calls.
- Minimize credit usage and improve performance by batching requests.

---

12. COMMENTING AND DOCUMENTATION

---

- Every function must have a header comment with:
  - Function name
  - Author
  - Arguments
  - Description
  - Return sample
  - Info steps

---

13. FILE EXTENSIONS

---

- All Deluge functions must use the .deluge extension unless otherwise specified.

---

14. TESTING AND VALIDATION

---

- Always run tests or validations after code changes.

---

15. NO HARDCODED VALUES

---

- Avoid hardcoding values; use constants or configuration.

---

16. AI COPILOT USAGE

---

- AI must always follow these standards.
- Always ask for missing context.
- Never output forbidden syntax.

---

## END OF DOCUMENT
