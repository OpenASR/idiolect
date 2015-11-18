package com.jetbrains.idear.jsgf;

/**
 * Created by breandan on 11/13/2015.
 */

import com.intellij.openapi.fileTypes.FileTypeConsumer;
import com.intellij.openapi.fileTypes.FileTypeFactory;
import org.jetbrains.annotations.NotNull;

public class JSpeechFileTypeFactory extends FileTypeFactory {
    @Override
    public void createFileTypes(@NotNull FileTypeConsumer fileTypeConsumer) {
        fileTypeConsumer.consume(JSpeechFileType.INSTANCE, "gram");
    }
}