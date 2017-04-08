package com.jetbrains.idear.nlp

import com.intellij.openapi.components.ServiceManager
import opennlp.tools.parser.Parse

abstract class ParserService {
    open fun init() {}

    abstract fun parseSentence(sentence: String): Parse?

    companion object {
        val instance: ParserService
            get() = ServiceManager.getService(ParserService::class.java)
    }
}
