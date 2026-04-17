package org.zohocrm.deluge;

import com.intellij.application.options.IndentOptionsEditor;
import com.intellij.lang.Language;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import com.intellij.psi.codeStyle.LanguageCodeStyleSettingsProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Provides code style settings for Deluge in Settings > Editor > Code Style.
 *
 * Defaults follow Deluge Coding Standards v2.3:
 * - 4-space indentation (no tabs)
 * - 120 character right margin
 */
public class DelugeLanguageCodeStyleSettingsProvider extends LanguageCodeStyleSettingsProvider {

    @Override
    public @NotNull Language getLanguage() {
        return DelugeLanguage.INSTANCE;
    }

    @Override
    public @Nullable IndentOptionsEditor getIndentOptionsEditor() {
        return new IndentOptionsEditor();
    }

    @Override
    public void customizeDefaults(@NotNull CommonCodeStyleSettings commonSettings,
                                  @NotNull CommonCodeStyleSettings.IndentOptions indentOptions) {
        indentOptions.INDENT_SIZE = 4;
        indentOptions.TAB_SIZE = 4;
        indentOptions.USE_TAB_CHARACTER = false;
        indentOptions.CONTINUATION_INDENT_SIZE = 8;
        commonSettings.RIGHT_MARGIN = 120;
    }

    @Override
    public @Nullable String getCodeSample(@NotNull SettingsType settingsType) {
        return "string standalone.calculateDiscount(string deal_id_str) {\n" +
                "    // Validate input and calculate discount\n" +
                "    try {\n" +
                "        id_long = deal_id_str.toLong();\n" +
                "        deal = zoho.crm.getRecordById(\"Deals\", id_long);\n" +
                "        if(isNull(deal)) {\n" +
                "            response = Map();\n" +
                "            response.put(\"success\", false);\n" +
                "            response.put(\"error\", \"Deal not found\");\n" +
                "            return response.toString();\n" +
                "        }\n" +
                "        amount = deal.get(\"Amount\");\n" +
                "        discount = amount * 0.1;\n" +
                "        update_map = Map();\n" +
                "        update_map.put(\"Discount\", discount);\n" +
                "        zoho.crm.updateRecord(\"Deals\", id_long, update_map);\n" +
                "        result = Map();\n" +
                "        result.put(\"success\", true);\n" +
                "        result.put(\"data\", discount);\n" +
                "        return result.toString();\n" +
                "    } catch(e) {\n" +
                "        standalone.developerLog(\"calculateDiscount\", \"error\", e, \"admin\", \"\");\n" +
                "        error_map = Map();\n" +
                "        error_map.put(\"success\", false);\n" +
                "        error_map.put(\"error\", e);\n" +
                "        return error_map.toString();\n" +
                "    }\n" +
                "    return \"\";\n" +
                "}";
    }
}
