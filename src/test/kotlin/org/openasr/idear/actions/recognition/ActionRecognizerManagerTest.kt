package org.openasr.idear.actions.recognition

import com.intellij.testFramework.HeavyPlatformTestCase
import com.intellij.testFramework.assertEqualsToFile
import org.junit.Test
import org.openasr.idear.nlp.NlpGrammar
import org.openasr.idear.nlp.intent.resolvers.IntentResolver
import org.openasr.idear.testing.TestContext
import org.reflections.Reflections
import java.io.File

class ActionRecognizerManagerTest : HeavyPlatformTestCase() { // }: BasePlatformTestCase() {
    val manager = object : ActionRecognizerManager(TestContext()) {
        override fun getResolvers(): Array<IntentResolver> =
            Reflections("org.openasr.idear.actions.recognition").getSubTypesOf(IntentResolver::class.java)
                .map { clazz ->
                    println(clazz.name)
                    clazz.constructors.first().newInstance() as IntentResolver
                }.toTypedArray()
    }


    @Test
    fun testDocumentGrammars() {
        // When
        val examples = manager.documentGrammars { recognizer, grammars ->
            listOf("\n## ${recognizer.displayName}") + examplesToMarkdown(grammars)
        }

        // Then
        assertNotNull(examples)

        assertEqualsToFile("Documentation", File("docs/example-phrases.md"),
                examples.joinToString("\n"))
    }

    @Test
    fun testPhrasesExample() {
        // When
        val examples = manager.documentGrammars { recognizer, grammars ->
            listOf("\n## ${recognizer.displayName}") + examplesToProperties(grammars)
        }

        // Then
        assertNotNull(examples)

        assertEqualsToFile("Examples", File("src/main/resources/phrases.example.properties"),
            examples.joinToString("\n", "# Example Phrases\n" ))
    }

    private fun examplesToMarkdown(grammars: List<NlpGrammar>): List<String> =
        grammars.sortedBy { it.rank }
            .flatMap { grammar -> grammar.examples.sorted().map { " - $it" } }

    private fun examplesToProperties(grammars: List<NlpGrammar>): List<String> =
        grammars.sortedBy { it.rank }
            .map { grammar -> "${grammar.intentName}=${grammar.examples.joinToString("|")}" }
}
