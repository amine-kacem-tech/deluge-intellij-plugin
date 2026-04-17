package org.zohocrm.deluge;

import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class DelugeFileType extends LanguageFileType {
    public static final DelugeFileType INSTANCE = new DelugeFileType();

    private DelugeFileType() {
        super(DelugeLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public String getName() {
        return "Deluge";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Zoho Deluge script file";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return "deluge";
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return DelugeIcons.FILE;
    }
}

