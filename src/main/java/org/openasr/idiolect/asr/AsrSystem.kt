package org.openasr.idiolect.asr

import org.openasr.idiolect.nlp.NlpProvider
import org.openasr.idiolect.nlp.NlpRequest
import org.openasr.idiolect.nlp.NlpResultListener

/**
 * Processes audio input, recognises speech to text and executes actions
 */
interface AsrSystem {
    fun supportsAsrAndNlp(asrProvider: AsrProvider, nlpProvider: NlpProvider) = false

    fun initialise(asrProvider: AsrProvider, nlpProvider: NlpProvider) {}

    fun start()

    /**
     * Starts recognition process.
     */
    fun startRecognition(): Boolean

    /**
     * Stops recognition process.
     * Recognition process is paused until the next call to startRecognition.
     */
    fun stopRecognition(): Boolean

    /** Blocks until we recognise something from the user. Called from [AsrControlLoop.run] */
    fun waitForUtterance(): String

    fun waitForUtterance(grammar: Array<String>,
                         escapeWords: Array<String>
                         = arrayOf("dont worry", "never mind", "quit", "forget it", "escape")): String

    fun setGrammar(grammar: Array<String>) {}

    fun onNlpRequest(nlpRequest: NlpRequest)

    /**
     * When utterance does not correspond to an actionable command, possibly due to stopwords, verbal fillers or extraneous text which cannot be directly parsed.
     * In short, if a given phrase, e.g., ``open uh foo java'' is received, we attempt to repair the utterance.
     * At the user's discretion, or in case of ambiguity when the phrase has multiple plausible alternatives,
     * we can either visually or verbally prompt the user to choose from a set of actionable phrases, e.g.,
     * ``Did you mean (a) open file foo.java, (b) open folder foo/java, or (c) something else?''
     *
     * To correct recognition errors, we apply Considine et al.'s~\cite{considine2022tidyparse} work on Tidyparse,
     * which supports recognition and parsing of context-free and mildly context-sensitive grammars,
     * and computing language edit distance. Tidyparse implements a novel approach to error correction based on
     * the theory of context-free language reachability, conjunctive grammars and Levenshtein automata.
     * We use a SAT solver to find the smallest edit transforming a string outside the language to a string inside the language.
     * Only when the utterance is one or two tokens away from a known command do we attempt a repair.
     */
    fun repairUtterance(nlpRequest: NlpRequest) {
        // TODO
    }

    fun terminate(): Boolean
}
