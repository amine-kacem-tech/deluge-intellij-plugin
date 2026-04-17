package org.zohocrm.deluge;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import org.jetbrains.annotations.NotNull;

public class DelugeFile extends PsiFileBase {
    public DelugeFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, DelugeLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return DelugeFileType.INSTANCE;
    }

    @Override
    public String toString() {
        return "Deluge File";
    }
}

