package org.openasr.idear.nlp

import com.intellij.ide.plugins.PluginManager
import com.intellij.openapi.extensions.PluginId
import opennlp.tools.cmdline.parser.ParserTool
import opennlp.tools.parser.Parse
import opennlp.tools.parser.Parser
import opennlp.tools.parser.ParserFactory
import opennlp.tools.parser.ParserModel
import org.jetbrains.annotations.TestOnly
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream

class NlpParserService @TestOnly
constructor(private val path: String?) : ParserService() {
    private var parser: Parser? = null

    private val modelInputStream: InputStream
        @Throws(FileNotFoundException::class)
        get() {
            if (path != null) return FileInputStream(path)
            val id = PluginId.getId("org.openasr.idear")
            val plugin = PluginManager.getPlugin(id)!!

            return plugin.pluginClassLoader.getResourceAsStream("en-parser-chunking.bin")
        }


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




