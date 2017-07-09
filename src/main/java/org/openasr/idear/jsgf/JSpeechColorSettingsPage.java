package org.openasr.idear.jsgf;

/**
 * Created by breandan on 11/13/2015.
 */

import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.*;
import org.jetbrains.annotations.*;

import javax.swing.*;
import java.util.Map;

public class JSpeechColorSettingsPage implements ColorSettingsPage {
    private static final AttributesDescriptor[] DESCRIPTORS = new AttributesDescriptor[]{
            new AttributesDescriptor("Key", JSpeechSyntaxHighlighter.RULE),
            new AttributesDescriptor("Separator", JSpeechSyntaxHighlighter.SEPARATOR),
            new AttributesDescriptor("Value", JSpeechSyntaxHighlighter.LITERAL),
    };

    @Nullable
    @Override
    public Icon getIcon() {
        return JSpeechIcons.FILE;
    }

    @NotNull
    @Override
    public SyntaxHighlighter getHighlighter() {
        return new JSpeechSyntaxHighlighter();
    }

    @NotNull
    @Override
    public String getDemoText() {
        return "#JSGF V1.0;\n" +
                "\n" +
                "grammar com.acme.commands;\n" +
                "\n" +
                "public <basicCmd> = <startPolite> <command> <endPolite>;\n" +
                "\n" +
                "<command> = <action> <object>;\n" +
                "<action> = /10/ open |/2/ close |/1/ delete |/1/ move;\n" +
                "<object> = [the | a] (window | file | menu);";
    }

    @Nullable
    @Override
    public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
        return null;
    }

    @NotNull
    @Override
    public AttributesDescriptor[] getAttributeDescriptors() {
        return DESCRIPTORS;
    }

    @NotNull
    @Override
    public ColorDescriptor[] getColorDescriptors() {
        return ColorDescriptor.EMPTY_ARRAY;
    }

    @NotNull
    @Override
    public String getDisplayName() {
        return "JSpeech";
    }
}