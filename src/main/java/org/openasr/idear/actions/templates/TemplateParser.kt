package org.openasr.idear.actions.templates

import io.github.xstream.mxparser.MXParser
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream

// Template files are only required for user-created templates
// Windows: %HOMEPATH%\AppData\Roaming\JetBrains\IntelliJIdea<version>\templates\
// Linux:   ~IntelliJ IDEA<version>/config/templates
// OS X:    ~/Library/Preferences/IntelliJ IDEA<version>/templates
object TemplateParser {
    private val ns: String? = null

    fun parseTemplateSetFromXml(inputStream: InputStream): TemplateSet {
        val parser: XmlPullParser = MXParser()
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
        parser.setInput(inputStream, null)
        parser.nextTag()

        parser.require(XmlPullParser.START_TAG, ns, "templateSet")
        val templateSet = TemplateSet(parser.getAttributeValue(null, "group"), readTemplates(parser))
        parser.require(XmlPullParser.END_TAG, ns, "templateSet")

        return templateSet
    }

    private fun readTemplates(parser: XmlPullParser): List<Template> {
        val templates = mutableListOf<Template>()

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }

            if (parser.name == "template") {
                templates.add(readTemplate(parser))
            } else {
                skip(parser)
            }
        }
        return templates
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readTemplate(parser: XmlPullParser): Template {
        parser.require(XmlPullParser.START_TAG, ns, "template")
        var name = parser.getAttributeValue(null, "name")
        var value = parser.getAttributeValue(null, "value")
        var description = parser.getAttributeValue(null, "description")
        var toReformat = "true" == parser.getAttributeValue(null, "toReformat")
        var toShortenFQNames = "true" == parser.getAttributeValue(null, "toShortenFQNames")

        val template = Template(name, value, description, "", toReformat, toShortenFQNames)

//        while (parser.next() != XmlPullParser.END_TAG) {
//            if (parser.eventType != XmlPullParser.START_TAG) {
//                continue
//            }
//            when (parser.name) {
//                "variable" -> template.addVariable(readVariable(parser))
//                "context" -> template.setContext(readContext(parser))
//                else -> skip(parser)
//            }
//        }

        return template
    }

   /* @Throws(IOException::class, XmlPullParserException::class)
    private fun readVariable(parser: XmlPullParser): Variable {
        var name = ""
        var expresion = ""
        var defaultValue: String? = null
        var alwaysStopAt: Boolean = true

        parser.require(XmlPullParser.START_TAG, ns, "link")
        val tag = parser.name
        val relType = parser.getAttributeValue(null, "rel")
        if (tag == "link") {
            if (relType == "alternate") {
                link = parser.getAttributeValue(null, "href")
                parser.nextTag()
            }
        }
        parser.require(XmlPullParser.END_TAG, ns, "link")
        return variable
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun readOption(parser: XmlPullParser): Option {
        parser.require(XmlPullParser.START_TAG, ns, "option")

        val option = Option(parser.getAttributeValue(null, "name"), parser.getAttributeValue(null, "value"))

        parser.require(XmlPullParser.END_TAG, ns, "option")
        return option
    }*/

    @Throws(XmlPullParserException::class, IOException::class)
    private fun skip(parser: XmlPullParser) {
        if (parser.eventType != XmlPullParser.START_TAG) {
            throw IllegalStateException()
        }
        var depth = 1
        while (depth != 0) {
            when (parser.next()) {
                XmlPullParser.END_TAG -> depth--
                XmlPullParser.START_TAG -> depth++
            }
        }
    }
}
