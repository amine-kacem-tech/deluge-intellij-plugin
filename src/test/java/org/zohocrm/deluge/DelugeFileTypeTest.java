package org.zohocrm.deluge;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;

/**
 * Test suite for Deluge file type recognition
 */
public class DelugeFileTypeTest extends BasePlatformTestCase {

    @Override
    protected String getTestDataPath() {
        return "src/test/resources/testData";
    }

    /**
     * Test that .deluge extension is properly recognized
     */
    public void testDelugeFileTypeRecognition() {
        FileTypeManager fileTypeManager = FileTypeManager.getInstance();
        FileType fileType = fileTypeManager.getFileTypeByFileName("test.deluge");

        assertNotNull("Deluge file type should be registered", fileType);
        assertEquals("File type name should be 'Deluge'", "Deluge", fileType.getName());
        assertTrue("Should be DelugeFileType instance", fileType instanceof DelugeFileType);
    }

    /**
     * Test that the default extension is 'deluge'
     */
    public void testDefaultExtension() {
        DelugeFileType fileType = DelugeFileType.INSTANCE;
        assertEquals("Default extension should be 'deluge'", "deluge", fileType.getDefaultExtension());
    }

    /**
     * Test the file type description
     */
    public void testFileTypeDescription() {
        DelugeFileType fileType = DelugeFileType.INSTANCE;
        assertEquals("Description should match", "Zoho Deluge script file", fileType.getDescription());
    }

    /**
     * Test that .js files are NOT recognized as Deluge files
     */
    public void testJsFileNotRecognizedAsDeluge() {
        FileTypeManager fileTypeManager = FileTypeManager.getInstance();
        FileType fileType = fileTypeManager.getFileTypeByFileName("test.js");

        assertFalse("JavaScript files should not be recognized as Deluge",
                    fileType instanceof DelugeFileType);
    }

    /**
     * Test that various .deluge file names are recognized
     */
    public void testVariousDelugeFileNames() {
        FileTypeManager fileTypeManager = FileTypeManager.getInstance();

        String[] testFiles = {
            "standalone.deluge",
            "automation.deluge",
            "validation_rule.deluge",
            "schedule.deluge",
            "MyFunction.deluge",
            "my-function.deluge",
            "my_function_v2.deluge"
        };

        for (String fileName : testFiles) {
            FileType fileType = fileTypeManager.getFileTypeByFileName(fileName);
            assertTrue("File '" + fileName + "' should be recognized as Deluge",
                      fileType instanceof DelugeFileType);
        }
    }

    /**
     * Test that DelugeFileType is a singleton
     */
    public void testSingletonInstance() {
        DelugeFileType instance1 = DelugeFileType.INSTANCE;
        DelugeFileType instance2 = DelugeFileType.INSTANCE;

        assertSame("DelugeFileType should be a singleton", instance1, instance2);
    }

    /**
     * Test that the file type has the correct language
     */
    public void testFileTypeLanguage() {
        DelugeFileType fileType = DelugeFileType.INSTANCE;
        assertEquals("Language should be DelugeLanguage",
                    DelugeLanguage.INSTANCE,
                    fileType.getLanguage());
    }
}

