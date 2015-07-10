package com.jetbrains.idear;

import com.intellij.openapi.components.ServiceManager;
import opennlp.tools.parser.Parse;
import org.jetbrains.annotations.Nullable;

public abstract class ParserService {

    public void init() {
    }

    public void dispose() {
    }

    @Nullable
    public abstract Parse parseSentence(String sentence);

    public static ParserService getInstance() {
        return ServiceManager.getService(ParserService.class);
    }

}
