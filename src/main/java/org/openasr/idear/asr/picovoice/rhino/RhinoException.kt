package org.openasr.idear.asr.picovoice.rhino

class RhinoException : Exception {
    internal constructor(cause: Throwable?) : super(cause) {}
    internal constructor(message: String?) : super(message) {}
}
