package org.openasr.idear.actions.recognition

import com.intellij.testFramework.HeavyPlatformTestCase
import com.intellij.testFramework.assertEqualsToFile
import org.junit.Test
import org.openasr.idear.testing.TestContext
import org.reflections.Reflections
import java.io.File

class ActionRecognizerManagerTest : HeavyPlatformTestCase() { // }: BasePlatformTestCase() {
    @Test
    fun testListGrammarExamples() {
        // Given
        val manager = object : ActionRecognizerManager(TestContext()) {
            override fun getExtensions(): Array<ActionRecognizer> {
                return Reflections("org.openasr.idear.actions.recognition").getSubTypesOf(ActionRecognizer::class.java)
//                        .filter { it != RegisteredActionRecognizer::class.java && it != RegisteredEditorActionRecognizer::class.java }
                        .map { clazz ->
//                            println(clazz.name)
                            clazz.constructors.first().newInstance() as ActionRecognizer
                        }
                        .toTypedArray()
            }
        }

        // When
        val examples = manager.listGrammarExamples()

        // Then
        assertNotNull(examples)

        var path = File("docs/example").absolutePath
        assertEqualsToFile("Examples", File("docs/example-phrases.md"),
                examples.joinToString("\n- ", "# Example Phrases\n\n- " ))
    }
}
