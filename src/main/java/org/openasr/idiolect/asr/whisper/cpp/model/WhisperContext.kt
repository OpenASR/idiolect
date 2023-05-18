package org.openasr.idiolect.asr.whisper.cpp.model

import com.sun.jna.Structure
import com.sun.jna.ptr.PointerByReference
import org.openasr.idiolect.asr.whisper.cpp.ggml.GgmlType


class WhisperContext : Structure() {
    var t_load_us = 0
    var t_start_us = 0

    /** weight type (FP32 / FP16 / QX)  */
    var wtype: GgmlType = GgmlType.GGML_TYPE_F16

    /** intermediate type (FP32 or FP16)  */
    var itype: GgmlType = GgmlType.GGML_TYPE_F16

    //    WhisperModel model;
    var model: PointerByReference? = null

    //    whisper_vocab vocab;
    //    whisper_state * state = nullptr;
    var vocab: PointerByReference? = null
    var state: PointerByReference? = null

    /** populated by whisper_init_from_file()  */
    var path_model: String? = null

//    public static class ByReference extends WhisperContext implements Structure.ByReference {
//    }
//
//    public static class ByValue extends WhisperContext implements Structure.ByValue {
//    }
//
//    @Override
//    protected List<String> getFieldOrder() {
//        return List.of("t_load_us", "t_start_us", "wtype", "itype", "model", "vocab", "state", "path_model");
//    }
}
