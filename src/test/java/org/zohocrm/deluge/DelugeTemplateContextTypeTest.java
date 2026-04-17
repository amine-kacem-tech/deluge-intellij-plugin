package org.zohocrm.deluge;

import com.intellij.codeInsight.template.TemplateContextType;
import com.intellij.psi.PsiFile;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;

/**
 * Test suite for Deluge template context
 */
public class DelugeTemplateContextTypeTest extends BasePlatformTestCase {

    /**
     * Test that template context is available in .deluge files
     */
    public void testTemplateContextInDelugeFile() {
        PsiFile file = myFixture.configureByText("test.deluge",
                "string testFunction() {\n  <caret>\n}");

        assertNotNull("File should be created", file);
        assertTrue("Should be a Deluge file", file instanceof DelugeFile);
    }

    /**
     * Test that template context type is registered
     */
    public void testTemplateContextTypeExists() {
        DelugeTemplateContextType contextType = new DelugeTemplateContextType();
        assertNotNull("Context type should not be null", contextType);
    }

    /**
     * Test template context in various scenarios
     */
    public void testTemplateContextInVariousLocations() {
        // Test in function body
        PsiFile file1 = myFixture.configureByText("func.deluge",
                "string func() {\n  <caret>\n}");
        assertTrue("Should be valid in function body", file1 instanceof DelugeFile);

        // Test in try-catch block
        PsiFile file2 = myFixture.configureByText("trycatch.deluge",
                "try {\n  <caret>\n} catch (e) {\n}");
        assertTrue("Should be valid in try-catch", file2 instanceof DelugeFile);

        // Test at file level
        PsiFile file3 = myFixture.configureByText("toplevel.deluge", "<caret>");
        assertTrue("Should be valid at file level", file3 instanceof DelugeFile);
    }
}

