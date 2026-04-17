package org.zohocrm.deluge;

import com.intellij.codeInsight.template.TemplateActionContext;
import com.intellij.codeInsight.template.TemplateContextType;
import org.jetbrains.annotations.NotNull;

public class DelugeTemplateContextType extends TemplateContextType {

    protected DelugeTemplateContextType() {
        super("DELUGE");
    }

    @Override
    public boolean isInContext(@NotNull TemplateActionContext context) {
        // Check if the file is a Deluge file
        return context.getFile() instanceof DelugeFile;
    }
}
