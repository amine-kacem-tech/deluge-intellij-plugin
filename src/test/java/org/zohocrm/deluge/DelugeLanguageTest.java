package org.zohocrm.deluge;

import com.intellij.lang.Language;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;

/**
 * Test suite for Deluge language registration
 */
public class DelugeLanguageTest extends BasePlatformTestCase {

    /**
     * Test that DelugeLanguage is properly registered
     */
    public void testLanguageRegistration() {
        Language language = DelugeLanguage.INSTANCE;
        assertNotNull("DelugeLanguage should not be null", language);
    }

    /**
     * Test that the language ID is correct
     */
    public void testLanguageId() {
        Language language = DelugeLanguage.INSTANCE;
        assertEquals("Language ID should be 'Deluge'", "Deluge", language.getID());
    }

    /**
     * Test that DelugeLanguage is a singleton
     */
    public void testSingletonInstance() {
        Language instance1 = DelugeLanguage.INSTANCE;
        Language instance2 = DelugeLanguage.INSTANCE;

        assertSame("DelugeLanguage should be a singleton", instance1, instance2);
    }

    /**
     * Test that the language can be found by ID
     */
    public void testLanguageFindById() {
        Language found = Language.findLanguageByID("Deluge");
        assertNotNull("Language should be findable by ID", found);
        assertEquals("Found language should be DelugeLanguage",
                    DelugeLanguage.INSTANCE, found);
    }

    /**
     * Test that the language display name is correct
     */
    public void testLanguageDisplayName() {
        Language language = DelugeLanguage.INSTANCE;
        assertEquals("Display name should be 'Deluge'", "Deluge", language.getDisplayName());
    }
}

