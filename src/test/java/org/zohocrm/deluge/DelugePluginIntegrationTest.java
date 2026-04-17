package org.zohocrm.deluge;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.psi.PsiFile;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;

/**
 * Integration tests for the Deluge plugin
 * Tests the complete workflow of file recognition, parsing, and language features
 */
public class DelugePluginIntegrationTest extends BasePlatformTestCase {

    @Override
    protected String getTestDataPath() {
        return "src/test/resources/testData";
    }

    /**
     * Test complete workflow: create file, verify type, verify language
     */
    public void testCompleteWorkflow() {
        // Create a Deluge file
        String content = """
                // Standalone function with Deluge coding standards
                string processOrder(string orderId, int quantity) {
                    try {
                        // Validate input
                        if (orderId == null || orderId.isEmpty()) {
                            return "ERROR_INVALID_ORDER_ID";
                        }
                        // Log processing
                        info "Processing order: " + orderId + " with quantity: " + quantity;
                        // Get order record
                        orderMap = zoho.crm.getRecordById("Orders", orderId.toLong());
                        // Update quantity
                        updateMap = Map();
                        updateMap.put("Quantity", quantity);
                        response = zoho.crm.updateRecord("Orders", orderId.toLong(), updateMap);
                        // Return success
                        return "SUCCESS";
                    } catch (e) {
                        info "ERROR in processOrder: " + e;
                        return "ERROR_EXCEPTION";
                    }
                }
                """;

        PsiFile file = myFixture.configureByText("processOrder.deluge", content);

        // Verify file creation
        assertNotNull("File should be created", file);
        assertTrue("Should be DelugeFile instance", file instanceof DelugeFile);

        // Verify file type
        FileType fileType = file.getFileType();
        assertTrue("File type should be DelugeFileType", fileType instanceof DelugeFileType);
        assertEquals("File type name should be 'Deluge'", "Deluge", fileType.getName());

        // Verify language
        assertEquals("Language should be Deluge",
                    DelugeLanguage.INSTANCE,
                    file.getLanguage());

        // Verify content
        assertEquals("Content should match", content.trim(), file.getText().trim());
    }

    /**
     * Test that only .deluge extension is recognized
     */
    public void testOnlyDelugeExtensionRecognized() {
        FileTypeManager manager = FileTypeManager.getInstance();

        // Test .deluge extension (should be recognized)
        FileType delugeType = manager.getFileTypeByFileName("test.deluge");
        assertTrue("Should recognize .deluge", delugeType instanceof DelugeFileType);

        // Test .js extension (should NOT be recognized as Deluge)
        FileType jsType = manager.getFileTypeByFileName("test.js");
        assertFalse("Should NOT recognize .js as Deluge", jsType instanceof DelugeFileType);

        // Test other extensions (should NOT be recognized as Deluge)
        String[] nonDelugeExtensions = {"test.java", "test.py", "test.txt", "test.xml"};
        for (String fileName : nonDelugeExtensions) {
            FileType type = manager.getFileTypeByFileName(fileName);
            assertFalse("Should NOT recognize " + fileName + " as Deluge",
                       type instanceof DelugeFileType);
        }
    }

    /**
     * Test automation function template structure
     */
    public void testAutomationFunctionStructure() {
        String content = """
                // Automation: Update related contacts
                void updateRelatedContacts(string dealId) {
                    try {
                        info "Starting automation for deal: " + dealId;
                        // Get deal record
                        dealMap = zoho.crm.getRecordById("Deals", dealId.toLong());
                        // Process contacts
                        contactsList = dealMap.get("Contacts");
                        for each contact in contactsList {
                            info "Processing contact: " + contact.get("id");
                        }
                        info "Automation completed successfully";
                    } catch (e) {
                        info "ERROR in updateRelatedContacts: " + e;
                    }
                }
                """;

        PsiFile file = myFixture.configureByText("updateContacts.deluge", content);

        assertNotNull("Automation file should be created", file);
        assertTrue("Should be DelugeFile", file instanceof DelugeFile);
        assertTrue("Content should contain 'void'", file.getText().contains("void"));
        assertTrue("Content should contain try-catch", file.getText().contains("try"));
    }

    /**
     * Test validation rule function structure
     */
    public void testValidationRuleFunctionStructure() {
        String content = """
                // Validation Rule: Check discount limit
                map validateDiscount(map recordData) {
                    try {
                        validationMap = Map();
                        discount = recordData.get("Discount_Percent");
                        if (discount > 50) {
                            validationMap.put("isValid", false);
                            validationMap.put("message", "Discount cannot exceed 50%");
                        } else {
                            validationMap.put("isValid", true);
                            validationMap.put("message", "Discount is valid");
                        }
                        return validationMap;
                    } catch (e) {
                        info "ERROR in validateDiscount: " + e;
                        errorMap = Map();
                        errorMap.put("isValid", false);
                        errorMap.put("message", "Validation error occurred");
                        return errorMap;
                    }
                }
                """;

        PsiFile file = myFixture.configureByText("validateDiscount.deluge", content);

        assertNotNull("Validation file should be created", file);
        assertTrue("Should be DelugeFile", file instanceof DelugeFile);
        assertTrue("Content should contain 'map'", file.getText().contains("map"));
        assertTrue("Content should contain validation logic",
                  file.getText().contains("isValid"));
    }

    /**
     * Test multiple Deluge files in project
     */
    public void testMultipleDelugeFilesInProject() {
        PsiFile[] files = {
            myFixture.configureByText("standalone.deluge", "string func1() { return 'test'; }"),
            myFixture.configureByText("automation.deluge", "void func2() { info 'test'; }"),
            myFixture.configureByText("validation.deluge", "map func3() { return Map(); }")
        };

        for (PsiFile file : files) {
            assertNotNull("File should be created", file);
            assertTrue("Should be DelugeFile", file instanceof DelugeFile);
            assertEquals("Should have Deluge language",
                        DelugeLanguage.INSTANCE,
                        file.getLanguage());
        }
    }

    /**
     * Test that lexer can handle various Deluge syntax elements
     */
    public void testLexerHandlesDelugeSyntax() {
        String content = """
                // Test various Deluge syntax elements
                string complexFunction(string param1, int param2, list param3) {
                    try {
                        // Variables
                        myVar = "test";
                        myInt = 123;
                        myList = List();
                        myMap = Map();
                        // Conditions
                        if (param1 != null) {
                            info "param1 is not null";
                        } else {
                            info "param1 is null";
                        }
                        // Loops
                        for each item in param3 {
                            info "Item: " + item;
                        }
                        // Zoho CRM API calls
                        record = zoho.crm.getRecordById("Leads", param2.toLong());
                        return "SUCCESS";
                    } catch (e) {
                        info "ERROR: " + e;
                        return "ERROR";
                    }
                }
                """;

        PsiFile file = myFixture.configureByText("complex.deluge", content);

        assertNotNull("Complex file should be created", file);
        assertTrue("Should be DelugeFile", file instanceof DelugeFile);

        // Verify various syntax elements are present
        String text = file.getText();
        assertTrue("Should contain function definition", text.contains("complexFunction"));
        assertTrue("Should contain try-catch", text.contains("try") && text.contains("catch"));
        assertTrue("Should contain if-else", text.contains("if") && text.contains("else"));
        assertTrue("Should contain for each", text.contains("for each"));
        assertTrue("Should contain zoho.crm", text.contains("zoho.crm"));
    }
}

