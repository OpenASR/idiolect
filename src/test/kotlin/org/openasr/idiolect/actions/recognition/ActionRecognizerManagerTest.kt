package org.openasr.idiolect.actions.recognition

import com.intellij.testFramework.HeavyPlatformTestCase
import com.intellij.testFramework.assertEqualsToFile
import org.jetbrains.annotations.TestOnly
import org.junit.Test
import org.openasr.idiolect.nlp.NlpContext
import org.openasr.idiolect.nlp.NlpGrammar
import org.openasr.idiolect.nlp.intent.resolvers.IntentResolver
import org.openasr.idiolect.testing.TestContext
import org.reflections.Reflections
import java.io.File

class ActionRecognizerManagerTest : HeavyPlatformTestCase() { // }: BasePlatformTestCase() {
    val manager = object : ActionRecognizerManager(NlpContext(TestContext())) {
        override fun getResolvers(): Array<IntentResolver> =
            Reflections("org.openasr.idiolect.actions.recognition").getSubTypesOf(IntentResolver::class.java)
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

//        val outputFile = File("docs/example-phrases.md")
//        outputFile.writeText(examples.joinToString("\n"))

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

//        val outputFile = File("src/main/resources/phrases.example.properties")
//        outputFile.writeText(examples.joinToString("\n", "# Example Phrases\n"))

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
