package org.openasr.idiolect.dsl

import org.openasr.idiolect.dsl.DIALOG.Companion.captureBuffer
import org.openasr.idiolect.dsl.DIALOG.Companion.matchStrategy
import org.openasr.idiolect.dsl.DIALOG.Companion.tellOptionsOnMismatch
import org.openasr.idiolect.dsl.DIALOG.Companion.escapeKeyword
import org.openasr.idiolect.dsl.MatchStrategy.*


val dialogTree =
    DIALOG {
        tellOptionsOnMismatch = true
        escapeKeyword = "nevermind"

        USER("open menu") {
            PROMPT("which menu?") {
                matchStrategy = FUZZY
                USER("file") {
                    PROMPT("which file?") {
                        CAPTURE(VOICE) { TODO("open file $it") }
                    }
                }
                USER("edit") { TODO("open edit") }
                USER("view") { TODO("open view") }
                //...
            }
        }

        USER("bind action") {
            PROMPT("do the action, then say 'done'") {
                CAPTURE(KEYS()) { captureBuffer.add(it.joinToString("::")) }
                USER("done") {
                    PROMPT("say the phrase to trigger the action") {
                        CAPTURE(VOICE) { captureBuffer.add(it) }
                        USER("done") { TODO("bind action ${captureBuffer.last()}") }
                    }
                }
            }
        }
    }

enum class MatchStrategy { EXACT, FUZZY, GPT }
open class KEYS { companion object: KEYS() }
open class VOICE { companion object: VOICE() }
open class MOUSE { companion object: MOUSE() }

open class DIALOG {
    var tellOptionsOnMismatch: Boolean = false
    var matchStrategy: MatchStrategy = EXACT
    var escapeKeyword: String = "escape"
    var captureBuffer: MutableList<String> = mutableListOf()

    companion object : DIALOG()

    operator fun invoke(function: () -> Unit): Unit = TODO("Not yet implemented")
    open operator fun invoke(s: String, function: () -> Unit): Unit = TODO("Not yet implemented")
}

object USER : DIALOG()

object PROMPT : DIALOG()

object CAPTURE : DIALOG() {
    operator fun invoke(action: VOICE, then: (String) -> Unit): Unit = TODO()
    operator fun invoke(action: KEYS, then: (List<String>) -> Unit): Unit = TODO()
}
