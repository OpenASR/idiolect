package org.openasr.idiolect.asr.whisper.cpp.model

import com.sun.jna.Callback
import com.sun.jna.Pointer
import com.sun.jna.Structure
import java.io.InputStream

/** Adapted from the whisper.cpp Android example */
class WhisperModelLoader(private val inputStream: InputStream) : Structure() {
    var context: Pointer? = null
    var read: ReadFunction? = ReadFunction(inputStream)
    var eof: EOFFunction? = EOFFunction(inputStream)
    var close: CloseFunction? = CloseFunction(inputStream)

    init {
//        read.setCallback(this);
//        eof.setCallback(this);
//        close.setCallback(this);
//        read.write();
//        eof.write();
//        close.write();
    }

    class ReadFunction(private val inputStream: InputStream) : Callback {
        operator fun invoke(ctx: Pointer, output: Pointer?, readSize: Int): Pointer {
//            inputStream.read(buffer, readSize)
//            output.write()
            return ctx
        }
    }

    class EOFFunction(private val inputStream: InputStream) : Callback {
        operator fun invoke(ctx: Pointer?): Boolean {
            return inputStream.available() == 0
        }
    }

    class CloseFunction(private val inputStream: InputStream) : Callback {
        operator fun invoke(ctx: Pointer?) {
            inputStream.close()
        }
    }

//    interface ReadCallback : Callback {
//        operator fun invoke(ctx: Pointer?, output: Pointer?, readSize: Int): Pointer?
//    }
//
//    interface EOFCallback : Callback {
//        operator fun invoke(ctx: Pointer?): Boolean
//    }
//
//    interface CloseCallback : Callback {
//        operator fun invoke(ctx: Pointer?)
//    }
}

