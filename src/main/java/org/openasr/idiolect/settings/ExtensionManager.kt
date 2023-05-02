package org.openasr.idiolect.settings

import com.intellij.openapi.extensions.ExtensionPointName
import org.openasr.idiolect.asr.*
import org.openasr.idiolect.nlp.NlpProvider
import org.openasr.idiolect.tts.TtsProvider

object ExtensionManager {
    internal val asrSystemEp: ExtensionPointName<AsrSystem> = ExtensionPointName("org.openasr.idiolect.asrSystem")
    internal val asrEp: ExtensionPointName<AsrProvider> = ExtensionPointName("org.openasr.idiolect.asrProvider")
    internal val ttsEp: ExtensionPointName<TtsProvider> = ExtensionPointName("org.openasr.idiolect.ttsProvider")
    internal val nlpEp: ExtensionPointName<NlpProvider> = ExtensionPointName("org.openasr.idiolect.nlpProvider")

    internal var asrSelector: ExtensionSelector<AsrProvider> = ExtensionSelector(asrEp)
    internal var ttsSelector: ExtensionSelector<TtsProvider> = ExtensionSelector(ttsEp)
    internal var nlpSelector: ExtensionSelector<NlpProvider> = ExtensionSelector(nlpEp)
}
