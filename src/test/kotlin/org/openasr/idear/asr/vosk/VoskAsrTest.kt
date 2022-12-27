package org.openasr.idear.asr.vosk

import org.junit.Test

class VoskAsrTest {

    @Test
    fun testListModels() {
       val models = VoskAsr().listModels()
        println(models.map { it.name })
    }
}
