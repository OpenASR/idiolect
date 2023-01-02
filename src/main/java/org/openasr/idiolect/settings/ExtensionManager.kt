package org.openasr.idiolect.settings

import com.intellij.openapi.extensions.ExtensionPointName
import org.openasr.idiolect.asr.*
import org.openasr.idiolect.nlp.NlpProvider
import org.openasr.idiolect.tts.TtsProvider

object ExtensionManager {
    internal val asrSystemEp: ExtensionPointName<AsrSystem> = ExtensionPointName.create("org.openasr.idiolect.asrSystem")
    internal val asrEp: ExtensionPointName<AsrProvider> = ExtensionPointName.create("org.openasr.idiolect.asrProvider")
    internal val ttsEp: ExtensionPointName<TtsProvider> = ExtensionPointName.create("org.openasr.idiolect.ttsProvider")
    internal val nlpEp: ExtensionPointName<NlpProvider> = ExtensionPointName.create("org.openasr.idiolect.nlpProvider")

    internal var asrSelector: ExtensionSelector<AsrProvider> = ExtensionSelector(asrEp)
    internal var ttsSelector: ExtensionSelector<TtsProvider> = ExtensionSelector(ttsEp)
    internal var nlpSelector: ExtensionSelector<NlpProvider> = ExtensionSelector(nlpEp)
}
