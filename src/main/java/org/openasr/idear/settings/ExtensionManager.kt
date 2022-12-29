package org.openasr.idear.settings

import com.intellij.openapi.extensions.ExtensionPointName
import org.openasr.idear.asr.AsrProvider
import org.openasr.idear.asr.AsrSystem
import org.openasr.idear.nlp.NlpProvider
import org.openasr.idear.tts.TtsProvider
import java.util.concurrent.atomic.AtomicReference

object ExtensionManager {
    internal val asrSystemEp: ExtensionPointName<AsrSystem> = ExtensionPointName.create("org.openasr.idear.asrSystem")
    internal val asrEp: ExtensionPointName<AsrProvider> = ExtensionPointName.create("org.openasr.idear.asrProvider")
    internal val ttsEp: ExtensionPointName<TtsProvider> = ExtensionPointName.create("org.openasr.idear.ttsProvider")
    internal val nlpEp: ExtensionPointName<NlpProvider> = ExtensionPointName.create("org.openasr.idear.nlpProvider")

    internal var asrSelector: ExtensionSelector<AsrProvider> = ExtensionSelector(asrEp)
    internal var ttsSelector: ExtensionSelector<TtsProvider> = ExtensionSelector(ttsEp)
    internal var nlpSelector: ExtensionSelector<NlpProvider> = ExtensionSelector(nlpEp)
}
