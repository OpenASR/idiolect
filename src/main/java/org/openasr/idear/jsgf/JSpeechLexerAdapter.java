package com.jetbrains.idear.jsgf;

/**
 * Created by breandan on 11/13/2015.
 */
import com.intellij.lexer.FlexAdapter;

import java.io.Reader;

public class JSpeechLexerAdapter extends FlexAdapter {
    public JSpeechLexerAdapter() {
        super(new _JSpeechLexer((Reader) null));
    }
}
