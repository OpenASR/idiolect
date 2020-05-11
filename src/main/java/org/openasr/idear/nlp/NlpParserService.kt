package org.openasr.idear.nlp

import com.intellij.ide.plugins.PluginManagerCore
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.extensions.PluginId
import opennlp.tools.cmdline.parser.ParserTool
import opennlp.tools.parser.*
import org.jetbrains.annotations.TestOnly
import java.io.*

class NlpParserService @TestOnly constructor(path: String?) : ParserService() {
    private lateinit var parser: Parser
    private val logger = Logger.getInstance(javaClass)
    val plugin by lazy { PluginManagerCore.getPlugin(PluginId.getId("com.jetbrains.idear"))!! }

    private val modelInputStream: InputStream =
            if (path != null) FileInputStream(path)
            else plugin.pluginClassLoader.getResourceAsStream("en-parser-chunking.bin")

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