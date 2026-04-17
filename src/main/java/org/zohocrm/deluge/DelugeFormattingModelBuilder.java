package org.zohocrm.deluge;

import com.intellij.formatting.*;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import org.jetbrains.annotations.NotNull;

/**
 * Provides code formatting for Deluge files (Ctrl+Alt+L / Reformat Code).
 *
 * Formatting rules follow Deluge Coding Standards v2.3:
 * - 4-space indentation per brace level
 * - Spaces around operators (=, ==, !=, +, -, etc.)
 * - Space after commas, after colons in maps
 * - No space inside parentheses/brackets
 * - No space around dot operator
 * - Newline after semicolons, opening braces, before closing braces
 * - } else { and } catch { on same line
 */
public class DelugeFormattingModelBuilder implements FormattingModelBuilder {

    @Override
    public @NotNull FormattingModel createModel(@NotNull FormattingContext formattingContext) {
        PsiFile file = formattingContext.getPsiElement().getContainingFile();
        CodeStyleSettings settings = formattingContext.getCodeStyleSettings();

        SpacingBuilder spacingBuilder = new SpacingBuilder(settings, DelugeLanguage.INSTANCE);

        DelugeBlock rootBlock = new DelugeBlock(file.getNode(), spacingBuilder);
        return FormattingModelProvider.createFormattingModelForPsiFile(file, rootBlock, settings);
    }
}
