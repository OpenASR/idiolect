package org.openasr.idear.nlp

import com.intellij.openapi.diagnostic.Logger
import opennlp.tools.cmdline.parser.ParserTool
import opennlp.tools.parser.Parse
import opennlp.tools.parser.Parser
import opennlp.tools.parser.ParserFactory
import opennlp.tools.parser.ParserModel
import org.jetbrains.annotations.TestOnly
import org.openasr.idear.Idear
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream

class NlpParserService @TestOnly constructor(path: String?) : ParserService() {
    private lateinit var parser: Parser
    private val logger = Logger.getInstance(javaClass)

    private val modelInputStream: InputStream =
            if (path != null) FileInputStream(path)
            else Idear.plugin.pluginClassLoader.getResourceAsStream("en-parser-chunking.bin")

    override fun parseSentence(sentence: String): Parse? =
            ParserTool.parseLine(sentence, parser, 1).apply { assert(size == 1) }[0]

    override fun init() {
        val model = readParserModel() ?: return
        parser = ParserFactory.create(model)
    }

    private fun readParserModel() =
            try {
                val model = ParserModel(modelInputStream)
                modelInputStream.close()
                model
            } catch (ioException: IOException) {
                logger.error(ioException)
                null
            }
}