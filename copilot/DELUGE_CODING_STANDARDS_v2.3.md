# DELUGE CODING STANDARDS v2.2 — Full Edition (Copilot-Optimized)

This document defines the **mandatory Deluge coding standards** for Zoho CRM development.
These rules apply to **all Deluge functions** (CRM, Automation, Standalone, Schedulers, Integrations).

Deviation is **not allowed** unless explicitly approved.

---

## 1. NAMING STANDARDS

### Variables

- Use `snake_case` only
- Meaningful, descriptive names
- IDs must be converted using `toLong()`
- No single-letter variables
- **NO type declarations** - Deluge is dynamically typed

**Variable Declaration Pattern:**
```deluge
// ✅ CORRECT - Untyped declarations
psfos_rec = zoho.crm.getRecordById("PSFOS", psfos_id);
is_reused_platform = false;
total_value = 0;
my_map = Map();
my_list = List();

// ❌ INCORRECT - Type declarations not supported
map psfos_rec = zoho.crm.getRecordById("PSFOS", psfos_id);
bool is_reused_platform = false;
int total_value = 0;
```

### Function Names

- Use `camelCase`
- Must clearly describe the action

### Function Type Prefixes

- **Automation functions:** `void automation.functionName()`
- **Standalone functions:** `string standalone.functionName()` (always `string`, see note below)
- **Validation rule functions:** `map validation_rule.functionName(String crmAPIRequest)`
- **Schedule functions:** `void schedule.functionName()`
- **Related list functions:** `string related_list.functionName(Int recordId, ...)`
- **Button functions:** `string button.functionName(String recordId)`

**Important Note on Standalone Functions:**
All standalone functions MUST use `string` as the return type, even when returning maps or lists. Deluge automatically serializes returned objects to JSON strings. The calling code deserializes using `.toMap()` or `.toList()`.

### Constants

- Use `UPPERCASE_WITH_UNDERSCORES`

---

## 2. FUNCTION STRUCTURE (MANDATORY)

one function per file

### Standalone Functions

```deluge
string standalone.functionName(args)
{
	/*
	 * Description:
	 * - Purpose
	 * - Inputs
	 * - Outputs
	 */

	try
	{
		// Input Validation
		// Business Logic
		
		// Return a map (auto-converted to JSON string)
		return {"success": true, "data": result_data};
	}
	catch (e)
	{
		return {"success": false, "error": e};
	}

	return "FAKE_RETURN_TO_SATISFY_DELUGE";
}
```

**Important:** 
- Return type is ALWAYS `string`
- You can return maps, lists, or strings - Deluge auto-serializes to JSON
- Calling code uses `.toMap()` or `.toList()` to deserialize
- Example: `response = standalone.someFunction(args).toMap();`

**Standalone Function Best Practices:**

1. **Function Documentation**
   ```deluge
   /*
   Function Name: functionName
   Author: Author Name
   Parameters:
       param1 (Type) - description
       param2 (Type) - description
   
   Description: Clear description of what the function does
   
   Info:#
   1. Step 1 description
   2. Step 2 description
   
   Return Sample:
   {
       "success": true,
       "data": "result"
   }
   */
   ```

2. **Testing Block (Optional but Recommended)**
   ```deluge
   /* ========================== For Testing Start ========================== */
   // param1 = "test value";
   // param2 = 12345;
   /* =========================== For Testing End =========================== */
   ```

3. **Parameter Validation Pattern**
   ```deluge
   // Check required parameters first
   if(isNull(param1) || param1 == "")
   {
       return {"status":"error","message":"Param1 is required"};
   }
   
   // OR for multiple params
   missingParams = {};
   if(isNull(param1)) { missingParams.add("param1"); }
   if(missingParams.size() > 0)
   {
       return {"status":"error","message":"Missing params: " + missingParams};
   }
   ```

4. **Developer Logging Variables**
   ```deluge
   functionName = "actualFunctionName";
   logType = "info";  // or "warning", "error"
   logMessage = "";
   optData = "";
   ```

5. **Standard Return Patterns**
   ```deluge
   // Success
   return {"status":"success","data":result_data};
   
   // Error
   return {"status":"error","message":"Error description"};
   
   // Complex response
   responseMap = Map();
   responseMap.put("status","SUCCESS");
   responseMap.put("message",data);
   return responseMap;
   ```

6. **Error Handling with Logging**
   ```deluge
   catch (e)
   {
       logType = "error";
       logMessage = e;
       optData = "Additional context: " + params;
       standalone.developerLog(functionName,logType,logMessage,zoho.loginuserid,optData);
       return {"status":"error","message":e};
   }
   ```

7. **Large Data Handling**
   - Use `zoho.encryption.base64Encode()` for large result sets
   - Calling code must decode: `zoho.encryption.base64Decode(result)`

8. **Pagination Pattern**
   ```deluge
   currentPage = 1;
   pageSize = 200;
   maxPages = ceil(recordCount / pageSize);
   counter = leftpad("1",maxPages).replaceAll(" ","1,").toList();
   
   for each index i in counter
   {
       // Fetch page data
       currentPage = currentPage + 1;
   }
   ```

### Automation Functions (Workflows, Field Updates, etc.)

```deluge
void automation.functionName(args)
{
	/*
	 * Description:
	 * - Purpose
	 * - Inputs
	 * - Outputs
	 */

	try
	{
		// Input Validation
		// Business Logic
	}
	catch (e)
	{
		// Error handling
		// Optional logging
	}
}
```

### Validation Rule Functions

```deluge
map validation_rule.functionName(String crmAPIRequest)
{
	entityMap = crmAPIRequest.toMap().get("record");
	response = Map();
	
	try
	{
		// Input Validation
		// Business Logic
		
		// On success:
		response.put('status','success');
		
		// On error:
		// response.put('status','error');
		// response.put('message', '<your message(100 characters)>');
	}
	catch (e)
	{
		response.put('status','error');
		response.put('message', 'Validation failed');
	}
	
	return response;
}
```

### Schedule Functions

```deluge
void schedule.functionName()
{
	/*
	 * Description:
	 * - Purpose
	 * - Schedule frequency
	 */

	try
	{
		// Business Logic
		// Call other standalone/automation functions
	}
	catch (e)
	{
		// Error handling and logging
	}
	
	return;
}
```

### Related List Functions

```deluge
string related_list.functionName(Int recordId, String optionalParam)
{
	/*
	 * Description:
	 * - Purpose
	 * - Inputs
	 * - Outputs: XML string
	 */

	try
	{
		// Business Logic
		// Build XML response
		responseXML = "<record>...</record>";
		return responseXML;
	}
	catch (e)
	{
		// Log error
		return "<error><message>Something went wrong!</message></error>";
	}
	
	return "";
}
```

### Button Functions

```deluge
string button.functionName(String recordId)
{
	/*
	 * Description:
	 * - Purpose
	 * - Inputs
	 * - Outputs
	 */

	try
	{
		// Input Validation
		// Business Logic
		
		// Common return patterns:
		// return "Success message";
		// return openUrl(url, "new window");
		return result_string;
	}
	catch (e)
	{
		return "Error: " + e;
	}
	
	return "";
}
```

### General Rules:

- No executable code outside `try/catch`
- Description must be inside the function
- **Standalone functions:** Fake return is mandatory
- **Automation functions:** Use `void` prefix, NO fake return required
- **Validation functions:** Must return map with `status` key
- **Schedule functions:** Use `void` prefix, return `;` at end
- **Related list functions:** Must return XML string
- **Button functions:** Must return string (message or openUrl)

---

## 2.1. DELUGE TYPE SYSTEM

### Type Declarations

**Function Signatures:** ✅ Types ARE supported
```deluge
string standalone.functionName(String arg1, Int arg2, Map arg3)
void automation.functionName(Int recordId)
map validation_rule.functionName(String crmAPIRequest)
```

**Variable Declarations:** ❌ Types are NOT supported
```deluge
// ✅ CORRECT - Untyped (dynamic)
my_variable = value;
psfos_rec = zoho.crm.getRecordById("PSFOS", psfos_id);
is_reused = false;
total = 0;
my_map = Map();

// ❌ INCORRECT - Will cause syntax error
map my_map = Map();
bool is_reused = false;
int total = 0;
```

**Important:** Deluge is dynamically typed. Variables do not have and cannot have type annotations. Types are only used in:
1. Function return types
2. Function parameter types

---

## 3. LOGGING

```deluge
standalone.developerLog(function_name, log_type, log_message, log_user, opt_data);
```

Log types:

- info
- warning
- error

---

## 4. ERROR HANDLING

- `try/catch` is mandatory
- No `throw`
- Early return on validation errors

Error format:

```json
{
  "success": false,
  "error": "<message>"
}
```

---

## 5. DELUGE-SAFE SYNTAX

### Allowed Functions

- `Map()`, `List()` - Collection creation
- `ifnull()`, `isNull()`, `isBlank()`, `isEmpty()` - Null checking
- `for each` - Iteration
- `try/catch` - Error handling
- `toLong()`, `toDecimal()`, `toDate()`, `toList()`, `toMap()` - Type conversion
- `.toString()`, `.toJSONString()` - Serialization
- `zoho.encryption.base64Encode()`, `zoho.encryption.base64Decode()` - Encoding
- `zoho.encryption.urlEncode()` - URL encoding
- `ceil()`, `round()` - Math functions
- `leftpad()`, `replaceAll()` - String manipulation
- `.trim()`, `.toUpperCase()`, `.toLowerCase()` - String methods
- `.size()`, `.add()`, `.addAll()`, `.contains()`, `.containsKey()` - Collection methods
- `.get()`, `.put()`, `.keys()` - Map methods
- `.subList()` - List methods

### Forbidden

- `throw` - Use return with error object instead
- Ternary operators (`? :`) - Use `if` statement
- Java-style loops (`for(i=0; i<n; i++)`) - Use `for each`
- `.isEmpty()` on strings - Use `isEmpty()` function or check `== ""`
- `.toBoolean()` - Not reliable in Deluge

### Conditional Patterns

```deluge
// ✅ CORRECT - Deluge boolean checks
if(value is true)
if(value is false)
if(value is null)
if(checkCondition is false && otherCondition is false)

// ✅ CORRECT - Using functions
if(isNull(value))
if(isBlank(value))
if(isEmpty(value))

// ✅ CORRECT - Direct comparison
if(value == true)
if(value == false)
if(value == "")
if(value != null)

// ❌ INCORRECT
if(value) // Avoid implicit boolean
if(!value) // Avoid negation operator
```

### Collection Iteration

```deluge
// ✅ CORRECT - For each loop
for each item in itemList
{
    // Process item
}

// ✅ CORRECT - For each with index
for each index i in counter
{
    // Use i for indexing
}

// ❌ INCORRECT - Java-style loop
for(i = 0; i < list.size(); i++)
{
    // Not supported
}
```

---

## 6. PLATFORM LIMITS

- Max 5000 executed statements per function
- invokeUrl timeout ~40 seconds
- parameters and body cannot be used together
- No user-created SSL domains

---

## 7. API & INTEGRATIONS

### General Rules

- Wrap all API calls in try/catch
- Use Connections when available
- Never log secrets or sensitive data
- Use `detailed : true` for better error messages

### Zoho CRM API Patterns

```deluge
// ✅ Get record with error handling
response = zoho.crm.getRecordById("Module", recordId);
if(response.get("status") == "failure")
{
    // Handle error
    return {"status":"error","message":"Record not found"};
}

// ✅ Search with criteria
searchCriteria = "((Field_Name:equals:value))";
records = zoho.crm.searchRecords("Module", searchCriteria);

// ✅ Get related records
relatedRecords = zoho.crm.getRelatedRecords("Related_List", "Module", recordId);

// ✅ Update record with trigger control
updateMap = Map();
updateMap.put("Field_Name", value);
response = zoho.crm.updateRecord("Module", recordId, updateMap, {"trigger":{"workflow"}});

// ✅ Create record
createMap = Map();
createMap.put("Field_Name", value);
response = zoho.crm.createRecord("Module", createMap, {"trigger":{"workflow"}});
```

### InvokeUrl Patterns

```deluge
// ✅ Using connection (preferred)
response = invokeurl
[
    url : "https://www.zohoapis.eu/crm/v6/Module"
    type : GET
    connection : "crm"
];

// ✅ POST with parameters (use toString())
body = {"select_query": "select id from Module where condition"};
bodyStr = body.toString();
response = invokeurl
[
    url : "https://www.zohoapis.eu/crm/v2/coql"
    type : POST
    parameters : bodyStr
    connection : "crm"
];

// ❌ INCORRECT - Cannot use parameters and body together
response = invokeurl
[
    url : "..."
    type : POST
    parameters : bodyStr
    body : bodyMap  // This will fail
    connection : "crm"
];
```

### COQL Queries

```deluge
// ✅ COQL query pattern
body = {
    "select_query": "select id, Name, Field from Module where (condition) limit 100"
};
bodyStr = body.toString();
response = invokeurl
[
    url : "https://www.zohoapis.eu/crm/v2/coql"
    type : POST
    parameters : bodyStr
    connection : "crm"
];
records = response.get("data");
```

### API Error Handling

```deluge
// ✅ Check response status
if(response.get("status") == "failure")
{
    logMessage = "API call failed: " + response;
    standalone.developerLog(functionName, "error", logMessage, zoho.loginuserid, optData);
    return {"status":"error", "message":"API error"};
}

// ✅ Check for null data
records = response.get("data");
if(isNull(records))
{
    return {"status":"error", "message":"No data returned"};
}
```

---

## 8. INPUT VALIDATION

- Must be first executable logic in try block
- Return structured error on validation failure
- Check for null, empty, and blank values
- Validate required parameters before processing

### Validation Patterns

```deluge
// ✅ Single parameter validation
if(isNull(param1) || param1 == "")
{
    return {"status":"error","message":"Param1 is required"};
}

// ✅ Multiple parameter validation with feedback
missingParams = list();
if(isNull(param1) || param1 == "") { missingParams.add("param1"); }
if(isNull(param2) || param2 == "") { missingParams.add("param2"); }
if(missingParams.size() > 0)
{
    return {"status":"error","message":"Missing required parameters: " + missingParams};
}

// ✅ Map parameter key validation
requiredKeys = {"key1", "key2", "key3"};
missingKeys = list();
for each key in requiredKeys
{
    if(!params.containKey(key))
    {
        missingKeys.add(key);
    }
}
if(missingKeys.size() > 0)
{
    return {"status":"error","message":"Missing keys: " + missingKeys};
}

// ✅ Validate values are not null/empty
nullEmptyValues = list();
for each key in requiredKeys
{
    value = params.get(key);
    if(isNull(value) || isEmpty(value) || isBlank(value))
    {
        nullEmptyValues.add(key);
    }
}
if(nullEmptyValues.size() > 0)
{
    return {"status":"error","message":"Empty values for keys: " + nullEmptyValues};
}
```

---

## 9. COMMON PATTERNS & UTILITIES

### Data Size Handling

```deluge
// ✅ Handle large data with base64 encoding
if(resultList.size() > 100)
{
    encryptedResult = zoho.encryption.base64Encode(resultList);
    return encryptedResult;
}

// Calling code decodes:
// decodedData = zoho.encryption.base64Decode(response);
// dataList = decodedData.toList();

// ✅ Handle long optional data in logging
optDataLength = optionalData.length();
if(optDataLength > 50000)
{
    fileName = "Log_" + now.toString().unixEpoch() + ".txt";
    fileObj = optionalData.toFile(fileName);
    // Attach file to record later
    optionalData = "Data too large, file attached";
}
else if(optDataLength > 2000)
{
    optionalDataExtended = optionalData;
    optionalData = "See extended field";
}
```

### Pagination Pattern

```deluge
// ✅ Standard pagination for large datasets
currentPage = 1;
pageSize = 200;
maxPages = ceil(recordCount / pageSize);

// Create counter for pagination loop
counter = leftpad("1", maxPages).replaceAll(" ", "1,").toList();

recordList = list();
for each index i in counter
{
    searchURL = baseURL + "&page=" + currentPage + "&per_page=" + pageSize;
    response = invokeurl
    [
        url : searchURL
        type : GET
        connection : "crm"
    ];
    
    records = response.get("data");
    if(!isNull(records))
    {
        recordList.addAll(records);
    }
    
    currentPage = currentPage + 1;
}
```

### List Batching

```deluge
// ✅ Batch a large list into smaller chunks
batchSize = 50;
listSize = originalList.size();
maxIterations = ceil(listSize / batchSize);
loopList = leftpad("1", maxIterations).replaceAll(" ", "1,").toList();

batchedList = list();
currentIndex = 0;

for each iteration in loopList
{
    endIndex = if(currentIndex + batchSize > listSize, listSize, currentIndex + batchSize);
    batch = originalList.subList(currentIndex, endIndex);
    batchedList.add(batch);
    currentIndex = currentIndex + batchSize;
}
```

### Number Formatting

```deluge
// ✅ Format numbers with thousands separator
numberValue = "1234567.89";
decimalSeparator = ".";
thousandsSeparator = ",";

formattedNumber = numberValue.toDecimal().round(2);
numberList = formattedNumber.toList(decimalSeparator);
beforeDecimal = numberList.get(0);
beforeDecimal = beforeDecimal.replaceAll("(?<=\\d)(?=(\\d\\d\\d)+(?!\\d))", thousandsSeparator);
result = beforeDecimal + decimalSeparator + numberList.get(1);
```

### Date Calculations

```deluge
// ✅ Calculate months between dates with fractional handling
startDate = "2023-01-01".toDate();
endDate = "2023-06-15".toDate();

numMonths = startDate.monthsDiff(endDate);
exactMonthsBetween = startDate.addMonth(numMonths);
daysDifference = exactMonthsBetween.daysBetween(endDate);
representing = daysDifference / 30;

additionalMonths = 0;
if(representing > 0 && representing <= 0.5)
{
    additionalMonths = 0.5;
}
else if(representing > 0.5)
{
    additionalMonths = representing.ceil();
}

totalMonths = numMonths + additionalMonths;
```

### List to Map Conversion

```deluge
// ✅ Convert list to map indexed by key
itemsList = list();  // List of maps
keyName = "id";
resultMap = Map();

for each item in itemsList
{
    if(item.containsKey(keyName))
    {
        keyValue = item.get(keyName);
        
        // Check for duplicates
        if(resultMap.containsKey(keyValue.toString()))
        {
            return {"status":"error","message":"Duplicate key found: " + keyValue};
        }
        
        resultMap.put(keyValue.toString(), item);
    }
    else
    {
        return {"status":"error","message":"Key missing: " + keyName};
    }
}
```

### URL Encoding for Search

```deluge
// ✅ URL encode search criteria
searchCriteria = "((Field:equals:value))";
encodedCriteria = zoho.encryption.urlEncode(searchCriteria);
searchURL = baseURL + "?criteria=" + encodedCriteria;
```

---

## 10. RETURN OBJECTS

Success:

```json
{
  "success": true,
  "data": "<payload>"
}
```

---

## 10. AI COPILOT RULES

AI-generated code MUST:

- Follow all rules above
- Use Deluge-safe syntax only
- **NEVER use typed variable declarations** (e.g., `map x = Map()` is INVALID)
- Variables are untyped: `x = Map()` not `map x = Map()`
- **Standalone functions:** Always include fake return
- **Automation functions:** Use `void` prefix, NO fake return
- **Validation functions:** Return map with `status` key (success/error)
- **Schedule functions:** Use `void` prefix, include `return;` at end
- **Related list functions:** Return XML string, empty string as fallback
- **Button functions:** Return string (message or openUrl result)
- Always log errors using `standalone.developerLog()`

---

**END OF DOCUMENT**
