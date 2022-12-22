package org.openasr.idear.actions.templates

import org.codehaus.plexus.util.StringOutputStream
import org.junit.Assert.*
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.StringBufferInputStream
import java.io.StringReader

class TemplateParserTest {
    @Test
    fun testParseTemplateSetFromXml() {
        // Given template
        val templateText = """
            <templateSet group="Java">
              <template 
                    name="fun" value="public void () {&#10;    &#10;}&#10;" 
                    description="Creates new method" 
                    toReformat="true" 
                    toShortenFQNames="true">
                <variable name="NAME" expression="methodName()" defaultValue="foo" alwaysStopAt="true" />
                <context>
                  <option name="JAVA_DECLARATION" value="true" />
                </context>
              </template>
            </templateSet>
            """

        // When
        val templateSet = TemplateParser.parseTemplateSetFromXml(templateText.byteInputStream())

        // Then
        assertEquals("Java", templateSet.group)
    }
}