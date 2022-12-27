package org.openasr.idear.actions.recognition

import com.intellij.testFramework.HeavyPlatformTestCase
import com.intellij.testFramework.assertEqualsToFile
import org.junit.Test
import org.openasr.idear.nlp.NlpGrammar
import org.openasr.idear.testing.TestContext
import org.reflections.Reflections
import java.io.File

class ActionRecognizerManagerTest : HeavyPlatformTestCase() { // }: BasePlatformTestCase() {
    val manager = object : ActionRecognizerManager(TestContext()) {
        override fun getExtensions(): Array<ActionRecognizer> {
            return Reflections("org.openasr.idear.actions.recognition").getSubTypesOf(ActionRecognizer::class.java)
//                        .filter { it != RegisteredActionRecognizer::class.java && it != RegisteredEditorActionRecognizer::class.java }
                .map { clazz ->
                    println(clazz.name)
                    clazz.constructors.first().newInstance() as ActionRecognizer
                }
                .toTypedArray()
        }
    }

    @Test
    fun testDocumentGrammars() {
        // When
        val examples = manager.documentGrammars { recognizer, grammars ->
            return@documentGrammars listOf("\n## ${recognizer.displayName}")
                .plus(examplesToMarkdown(grammars))
        }

        // Then
        assertNotNull(examples)

        assertEqualsToFile("Documentation", File("docs/example-phrases.md"),
                examples.joinToString("\n", "# Example Phrases\n" ))
    }

    @Test
    fun testPhrasesExample() {
        // When
        val examples = manager.documentGrammars { recognizer, grammars ->
            return@documentGrammars listOf("\n## ${recognizer.displayName}")
                .plus(examplesToProperties(grammars))
        }

        // Then
        assertNotNull(examples)

        assertEqualsToFile("Examples", File("src/main/resources/phrases.example.properties"),
            examples.joinToString("\n", "# Example Phrases\n" ))
    }

    private fun examplesToMarkdown(grammars: List<NlpGrammar>): List<String> {
        return grammars.sortedBy { it.rank }
            .flatMap { grammar ->
                grammar.examples
                    .sorted()
                    .map { " - $it" }
            }
    }

    private fun examplesToProperties(grammars: List<NlpGrammar>): List<String> {
        return grammars.sortedBy { it.rank }
            .map { grammar ->
                "${grammar.intentName}=${grammar.examples.joinToString("|")}"
            }
    }
}
