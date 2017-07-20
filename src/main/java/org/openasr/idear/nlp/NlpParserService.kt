package org.openasr.idear.nlp

import opennlp.tools.cmdline.parser.ParserTool
import opennlp.tools.parser.*
import org.jetbrains.annotations.TestOnly
import org.openasr.idear.Idear
import java.io.*

class NlpParserService @TestOnly constructor(path: String?) : ParserService() {
    private var parser: Parser? = null

    private val modelInputStream: InputStream =
            if (path != null) FileInputStream(path)
            else Idear.plugin.pluginClassLoader.getResourceAsStream("en-parser-chunking.bin")

    override fun parseSentence(sentence: String): Parse? =
            if (parser == null) throw IllegalStateException()
            else ParserTool.parseLine(sentence, parser!!, 1).apply { assert(size == 1) }[0]

    override fun init() {
        val model = readParserModel() ?: return
        parser = ParserFactory.create(model)
    }

    private fun readParserModel(): ParserModel? {
        var model: ParserModel? = null
        try {
            model = ParserModel(modelInputStream)
            modelInputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return model
    }
}




