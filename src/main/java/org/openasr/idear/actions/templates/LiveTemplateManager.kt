package org.openasr.idear.actions.templates

import com.intellij.codeInsight.template.impl.DefaultLiveTemplatesProvider
import com.intellij.openapi.extensions.ExtensionPointName
import org.openasr.idear.asr.AsrSystem

class LiveTemplateManager {
//    private val templateEp: ExtensionPointName<> = ExtensionPointName.create("com.intellij.defaultLiveTemplatesProvider")
//    private val templateEp: ExtensionPointName<> = ExtensionPointName.create("com.intellij.defaultLiveTemplates")
//    private val contextEp: ExtensionPointName<> = ExtensionPointName.create("com.intellij.liveTemplateContext")

    init {
//        templateEp.extensionList
//        com.intellij.codeInsight.template.impl.DefaultLiveTemplateEP
        DefaultLiveTemplatesProvider.EP_NAME

//        com.intellij.codeInsight.template.impl.DefaultLiveTemplateEP
    }
}