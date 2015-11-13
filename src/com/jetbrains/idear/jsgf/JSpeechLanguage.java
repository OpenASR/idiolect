package com.jetbrains.idear.jsgf;

/**
 * Created by breandan on 11/12/2015.
 */

import com.intellij.lang.Language;

public class JSpeechLanguage extends Language {
    public static final JSpeechLanguage INSTANCE = new JSpeechLanguage();

    private JSpeechLanguage() {
        super("Simple");
    }
}