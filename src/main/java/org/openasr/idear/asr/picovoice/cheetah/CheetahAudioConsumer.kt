package org.openasr.idear.asr.picovoice.cheetah

import org.openasr.idear.asr.picovoice.AudioConsumer

class CheetahAudioConsumer(val callback: CheetahCallback) : AudioConsumer {

    override fun consume(pcm: ShortArray?) {
        callback.onUtterance("Sorry, cheetah binding is not yet implemented")
//        TODO("Need Cheetah binding")
//        if (isFinalized) {
//            return
//        }
//        isFinalized = rhino!!.process(pcm!!)
//        if (isFinalized) {
//            val isUnderstood = rhino!!.isUnderstood()
//            val intent = if (isUnderstood) rhino!!.getIntent() else null
//            callback!!.run(isUnderstood, intent)
//        }
    }

    override fun getFrameLength(): Int {
        return 0;
//        return rhino!!.frameLength()
    }

    /** @return sample rate in Hz - eg 16000Hz */
    override fun getSampleRate(): Int {
        return 0;
//        return rhino!!.sampleRate()
    }
}