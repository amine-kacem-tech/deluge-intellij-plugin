# GitHub Copilot Instructions — Zoho Deluge

These instructions define **how GitHub Copilot must generate Zoho Deluge code** for Zoho CRM projects.

Copilot **MUST strictly follow** these rules when generating, completing, or refactoring Deluge code.

---

## Target Language

- Zoho **Deluge** only
- Use **Deluge-safe syntax**
- Never invent unsupported Deluge features

---

## Naming Conventions

### Variables

- Use `snake_case`
- Use meaningful, descriptive names
- Never use single-letter variables
- Convert record IDs using `toLong()`

### Functions

- Use `camelCase`
- Function names must clearly describe the action

### Constants

- Use `UPPERCASE_WITH_UNDERSCORES`

---

## Mandatory Function Structure

Every Deluge function **MUST** follow this structure exactly:

```deluge
function functionName(args)
{
	/*
	 * Description:
	 * - Purpose
	 * - Inputs
	 * - Outputs
	 */

	try
	{
		// ===== Input Validation =====
		// ===== Fetch Required Records =====
		// ===== Business Logic =====
		// ===== API Calls / Updates =====
		// ===== Build Success Response =====

		return result_obj;
	}
	catch (e)
	{
		standalone.developerLog(
			"functionName",
			"error",
			"Descriptive error message",
			log_user,
			{"error": e.toString()}
		);

		return {
			"success": false,
			"error": "Descriptive error message"
		};
	}

	return "FAKE_RETURN_TO_SATISFY_DELUGE";
}
```

### Structural Rules

- No executable code outside `try/catch`
- Function description must be **inside** the function
- Fake return is **mandatory**

---

## Error Handling Rules

- `try/catch` is mandatory
- **Never use `throw`**
- Validation failures must use **early return**
- Errors must always be logged

---

## Logging Rules

- Use `standalone.developerLog`
- Allowed log types: `info`, `warning`, `error`
- Never log secrets, tokens, or Authorization headers
- Error logs must include context when available

---

## Deluge-Safe Syntax

### Allowed

- `map()`, `list()`
- `ifnull()`
- `for each`
- `try/catch`
- `toLong()`

### Forbidden

- `throw`
- Ternary operators
- Java-style `for` loops
- `.isEmpty()`, `.isNotNull()`, `.toBoolean()`

---

## Platform & Runtime Constraints

- Maximum **5000 executed statements per function**
- Loops count toward the limit
- Large datasets must be paginated or batched
- `invokeUrl` timeout is approximately **40 seconds**

---

## API & Integration Rules

- Wrap all API calls in `try/catch`
- Prefer **Zoho Connections** over manual tokens
- Never use both `parameters` and `body` in `invokeUrl`
- Always use `detailed : true`
- Log and normalize non-2xx responses
- Never log credentials or secrets

---

## Return Object Standard

### Success

```json
{
  "success": true,
  "data": "<payload>"
}
```

### Error

```json
{
  "success": false,
  "error": "<message>"
}
```

---

## Copilot Behavior Constraints

Copilot **MUST**:

- Follow all rules in this document
- Never generate forbidden syntax
- Never omit logging in error paths
- Never omit the fake return
- Generate defensive, safe Deluge code when uncertain

---

**END OF COPILOT INSTRUCTIONS**
