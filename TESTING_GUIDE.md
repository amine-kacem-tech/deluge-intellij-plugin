# Quick Testing Guide

## 🎯 Installation

1. **Install the plugin**:
   ```
   File → Settings → Plugins → ⚙️ → Install Plugin from Disk...
   → Select: build/distributions/deluge-intellij-plugin-1.0.1.zip
   ```

2. **Restart IntelliJ IDEA** (required for plugin to activate)

## 🧪 Testing Checklist

### ✅ Test 1: Function Colorization

Open `test_guidelines_colorization.deluge` and verify these are colored (not plain gray):

**Should be COLORED as built-in functions:**
- ✓ `info "message"`
- ✓ `toLong()`, `toDecimal()`, `toDate()`, `toList()`, `toMap()`, `toString()`, `toJSONString()`, `toNumber()`, `toFile()`
- ✓ `ceil()`, `round()`, `floor()`
- ✓ `leftpad()`, `replaceAll()`, `trim()`, `toUpperCase()`, `toLowerCase()`
- ✓ `zoho.crm.getRecordById()`, `getRelatedRecords()`, `searchRecords()`, `updateRecord()`, `createRecord()`, `deleteRecord()`, `getRecords()`, `getOrgVariable()`, `attachFile()`
- ✓ `invokeurl`, `zoho.loginuserid`, `zoho.now`
- ✓ `base64Encode()`, `base64Decode()`, `urlEncode()`

### ✅ Test 2: Code Folding

Create a test file with nested structures:

```deluge
string standalone.testFolding()
{
    map data = {
        "name": "Test",
        "nested": {
            "level1": {
                "level2": "value"
            }
        },
        "array": [
            [1, 2, 3],
            [4, 5, 6],
            [7, 8, 9]
        ]
    };
    
    return "done";
}
```

**Verify:**
- ✓ Function body `{...}` can fold
- ✓ Nested map `{"level1": {...}}` can fold
- ✓ Nested arrays `[[1,2,3], ...]` can fold
- ✓ Each level folds independently
- ✓ Placeholder shows `{...}` for braces, `[...]` for brackets

### ✅ Test 3: Semicolon Error Detection

Create statements **WITHOUT** semicolons and verify errors appear:

```deluge
string standalone.testSemicolons()
{
    // Should error - missing semicolon after variable
    name = "test"
    
    // Should error - missing semicolon after function call
    info "message"
    
    // Should error - missing semicolon after number
    count = 5
    
    // Should error - missing semicolon after map literal
    data = {"key": "value"}
    
    // Should NOT error - statement continues with operator
    result = 5 + 
             10;
    
    // Should NOT error - dot notation continues
    record = zoho.crm.
             getRecordById("Leads", id);
    
    return "test";
}
```

**Expected errors:**
- ✓ Red underline on `"test"` (line 1)
- ✓ Red underline on `"message"` (line 2)
- ✓ Red underline on `5` (line 3)
- ✓ Red underline on `}` of map literal (line 4)
- ✗ NO error on continued expressions (lines 5-6)

## 🐛 Troubleshooting

### Functions not colored?
1. Clear IntelliJ caches: `File → Invalidate Caches → Invalidate and Restart`
2. Ensure file extension is `.deluge`
3. Check if syntax highlighting is enabled in the IDE

### Folding not working?
1. Check if folding is enabled: `Settings → Editor → General → Code Folding`
2. Look for small `+`/`-` icons in the gutter (left margin)
3. Use `Ctrl+Shift+Numpad-` to collapse all

### Too many semicolon errors?
1. This is a known issue - the detection is aggressive
2. Errors should disappear when you add semicolons
3. Report specific false positives for improvement

## 📊 Color Reference

In a properly working installation, colors should match:

| Element Type | Color (typically) | Example |
|-------------|------------------|---------|
| Keywords | **Purple/Blue** | `if`, `for`, `return`, `try` |
| Built-in Functions | **Yellow/Orange** | `info`, `toList`, `ceil` |
| Strings | **Green** | `"text"`, `'text'` |
| Numbers | **Blue** | `123`, `45.67` |
| Comments | **Gray** | `// comment`, `/* block */` |
| Identifiers | **White/Black** | `myVariable`, `myFunction` |
| Operators | **White** | `+`, `-`, `=`, `==` |

## ✨ What's New

1. **Nested folding**: All nested `{}` and `[]` structures now fold correctly
2. **Semicolon detection**: Missing semicolons are now highlighted as errors
3. **All built-in functions**: Already colorized (info, conversion, math, string, zoho.crm, etc.)

## 📝 Notes

- File icon: Still uses default (custom icon requires additional resources)
- Semicolon checking: May have edge cases - let us know if you find false positives
- Performance: Plugin is lightweight and should not impact IDE performance

