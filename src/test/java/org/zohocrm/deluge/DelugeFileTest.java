package org.zohocrm.deluge;

import com.intellij.psi.PsiFile;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;

/**
 * Test suite for Deluge file creation and manipulation
 */
public class DelugeFileTest extends BasePlatformTestCase {

    @Override
    protected String getTestDataPath() {
        return "src/test/resources/testData";
    }

    /**
     * Test creating a simple Deluge file
     */
    public void testCreateDelugeFile() {
        PsiFile file = myFixture.configureByText("test.deluge", "// Deluge test file");

        assertNotNull("File should be created", file);
        assertTrue("Should be DelugeFile instance", file instanceof DelugeFile);
        assertEquals("File type should be Deluge",
                    DelugeFileType.INSTANCE,
                    file.getFileType());
    }

    /**
     * Test Deluge file with function content
     */
    public void testDelugeFileWithFunctionContent() {
        String content = """
                // Standalone function example
                string standaloneFunction(string param1, int param2) {
                    try {
                        // Function logic here
                        info "Processing: " + param1;
                        return "SUCCESS";
                    } catch (e) {
                        info "ERROR: " + e;
                        return "ERROR";
                    }
                }
                """;

        PsiFile file = myFixture.configureByText("standalone.deluge", content);

        assertNotNull("File should be created", file);
        assertTrue("Should be DelugeFile instance", file instanceof DelugeFile);
        assertEquals("File language should be Deluge",
                    DelugeLanguage.INSTANCE,
                    file.getLanguage());
    }

    /**
     * Test Deluge file toString method
     */
    public void testDelugeFileToString() {
        PsiFile file = myFixture.configureByText("test.deluge", "// test");

        assertTrue("toString should contain 'Deluge'",
                  file.toString().contains("Deluge"));
    }

    /**
     * Test that empty Deluge files are valid
     */
    public void testEmptyDelugeFile() {
        PsiFile file = myFixture.configureByText("empty.deluge", "");

        assertNotNull("Empty file should be created", file);
        assertTrue("Should be DelugeFile instance", file instanceof DelugeFile);
    }

    /**
     * Test Deluge file with comments
     */
    public void testDelugeFileWithComments() {
        String content = """
                // Single line comment
                /* Multi-line
                   comment */
                string myFunction() {
                    return "test";
                }
                """;

        PsiFile file = myFixture.configureByText("comments.deluge", content);
        assertNotNull("File with comments should be created", file);
        assertTrue("Should be DelugeFile instance", file instanceof DelugeFile);
    }

    /**
     * Test that .js files are not recognized as Deluge
     */
    public void testJavaScriptFileNotDeluge() {
        PsiFile file = myFixture.configureByText("test.js",
                "function test() { return 'test'; }");

        assertNotNull("JavaScript file should be created", file);
        assertFalse("JavaScript file should not be DelugeFile",
                   file instanceof DelugeFile);
    }

    /**
     * Test multiple Deluge files can coexist
     */
    public void testMultipleDelugeFiles() {
        PsiFile file1 = myFixture.configureByText("function1.deluge",
                "string func1() { return 'one'; }");
        PsiFile file2 = myFixture.configureByText("function2.deluge",
                "string func2() { return 'two'; }");

        assertNotNull("First file should be created", file1);
        assertNotNull("Second file should be created", file2);
        assertTrue("First should be DelugeFile", file1 instanceof DelugeFile);
        assertTrue("Second should be DelugeFile", file2 instanceof DelugeFile);
    }
}

