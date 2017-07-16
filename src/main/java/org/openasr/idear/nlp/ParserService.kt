package org.openasr.idear.nlp

import opennlp.tools.parser.Parse

abstract class ParserService {
    open fun init() {}
    abstract fun parseSentence(sentence: String): Parse?
}