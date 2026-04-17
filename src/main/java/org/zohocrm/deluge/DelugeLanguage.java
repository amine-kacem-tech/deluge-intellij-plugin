package org.zohocrm.deluge;

import com.intellij.lang.Language;

/**
 * Deluge language definition
 */
public class DelugeLanguage extends Language {
    public static final DelugeLanguage INSTANCE = new DelugeLanguage();

    private DelugeLanguage() {
        super("Deluge");
    }
}

