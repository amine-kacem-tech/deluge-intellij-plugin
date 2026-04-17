package org.zohocrm.deluge;

import com.intellij.lexer.FlexAdapter;

public class DelugeLexerAdapter extends FlexAdapter {
    public DelugeLexerAdapter() {
        super(new DelugeLexer(null));
    }
}

