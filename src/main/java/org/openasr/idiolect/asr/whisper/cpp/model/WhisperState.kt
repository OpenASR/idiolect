package org.openasr.idiolect.asr.whisper.cpp.model

import com.sun.jna.Structure
//import com.sun.jna.Native
//import com.sun.jna.Pointer
//import com.sun.jna.ptr.PointerByReference
//
class WhisperState : Structure() {
//    @JvmField
//    var t_sample_us: Long = 0
//    @JvmField
//    var t_encode_us: Long = 0
//    @JvmField
//    var t_decode_us: Long = 0
//    @JvmField
//    var t_mel_us: Long = 0
//
//    @JvmField
//    var n_sample: Int = 0
//    @JvmField
//    var n_encode: Int = 0
//    @JvmField
//    var n_decode: Int = 0
//    @JvmField
//    var n_fail_p: Int = 0
//    @JvmField
//    var n_fail_h: Int = 0
//
//    @JvmField
//    var kv_cross: WhisperKvCache = WhisperKvCache()
//    @JvmField
//    var mel: WhisperMel = WhisperMel()
//
//    @JvmField
//    var decoders: Array<WhisperDecoder> = arrayOfNulls<WhisperDecoder>(WHISPER_MAX_DECODERS) as Array<WhisperDecoder>
//
//    @JvmField
//    var buf_compute: Pointer? = null
//    @JvmField
//    var buf_scratch: Array<Pointer?> = arrayOfNulls<Pointer>(WHISPER_MAX_SCRATCH_BUFFERS)
//    @JvmField
//    var buf_last: Int = 0
//    @JvmField
//    var buf_max_size: LongArray = LongArray(WHISPER_MAX_SCRATCH_BUFFERS)
//
//    @JvmField
//    var logits: Pointer? = null
//
//    @JvmField
//    var result_all: Pointer? = null
//    @JvmField
//    var prompt_past: Pointer? = null
//
//    @JvmField
//    var logits_id: Pointer? = null
//
//    @JvmField
//    var rng: Pointer? = null
//
//    @JvmField
//    var lang_id: Int = 0
//
//    @JvmField
//    var path_model: String? = null
//
//    @JvmField
//    var ctx_coreml: Pointer? = null
//
//    @JvmField
//    var t_beg: Long = 0
//    @JvmField
//    var t_last: Long = 0
//    @JvmField
//    var tid_last: Int? = null
//    @JvmField
//    var energy: Pointer? = null
//
//    @JvmField
//    var exp_n_audio_ctx: Int = 0
//
//    override fun getFieldOrder(): List<String> {
//        return listOf(
//            "t_sample_us", "t_encode_us", "t_decode_us", "t_mel_us",
//            "n_sample", "n_encode", "n_decode", "n_fail_p", "n_fail_h",
//            "kv_cross", "mel", "decoders",
//            "buf_compute", "buf_scratch", "buf_last", "buf_max_size",
//            "logits",
//            "result_all", "prompt_past",
//            "logits_id",
//            "rng",
//            "lang_id",
//            "path_model",
//            "ctx_coreml",
//            "t_beg", "t_last", "tid_last", "energy",
//            "exp_n_audio_ctx"
//        )
//    }
//
//    companion object {
//        const val WHISPER_MAX_DECODERS = 16
//        const val WHISPER_MAX_SCRATCH_BUFFERS = 16
//    }
}
