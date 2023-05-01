package org.openasr.idiolect.asr

import com.intellij.openapi.diagnostic.logger
import org.openasr.idiolect.asr.ListeningState.Status.*
import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

object ListeningState {
    private val log = logger<ListeningState>()
    private val status = AtomicReference(INITIALISING)
    private val lock = ReentrantLock()
    private val condition = lock.newCondition()

    enum class Status {
        INITIALISING,  // needs to be initialised
        STARTED,       // actively listening for voice commands
        STOPPED,       // user pressed the stop button
        TERMINATED     // Idiolect is closed. Restart required
    }

    private fun setStatus(s: Status) {
        lock.withLock {
            log.info("ListeningState -> $s")
            status.set(s)
            condition.signal()
        }
    }

    fun getStatus() = status.get()

    val isTerminated get() = getStatus() == TERMINATED

    val isInit get() = getStatus() == INITIALISING

    val isStarted get() = getStatus() == STARTED

    fun waitIfStandby() {
        lock.withLock {
            if (status.get() == STOPPED) {
//                log.debug("ListeningState is STOPPED. ${Thread.currentThread().name} waiting...")
                condition.await()
            }
//            log.debug("ListeningState is ${status.get()}. ${Thread.currentThread().name} proceeding")
        }
    }

    /**
     * IllegalStateException may be thrown if app calls microphone.stream.read() before calling line.start()
     */
    fun waitForStarted() {
        lock.withLock {
            while (status.get() != STARTED) {
//                log.debug("ListeningState is ${status.get()}. ${Thread.currentThread().name} waiting for STARTED...")
                condition.await()
            }
//            log.debug("...ListeningState is STARTED. ${Thread.currentThread().name} proceeding")
        }
    }

    fun stopped() = setStatus(STOPPED)

    fun started() = setStatus(STARTED)

    fun terminated() = setStatus(TERMINATED)
}
