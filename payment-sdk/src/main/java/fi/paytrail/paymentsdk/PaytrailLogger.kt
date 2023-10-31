package fi.paytrail.paymentsdk

import android.util.Log

object PaytrailLogger {
    var isLoggingEnabled = false // Turn this on/off to enable/disable logs
    val tag = "MSDK"
    fun d(message: String) {
        if (isLoggingEnabled) {
            Log.d(tag, message)
        }
    }

    fun w(message: String) {
        if (isLoggingEnabled) {
            Log.w(tag, message)
        }
    }

    fun e(message: String) {
        if (isLoggingEnabled) {
            Log.e(tag, message)
        }
    }

    fun i(message: String) {
        if (isLoggingEnabled) {
            Log.i(tag, message)
        }
    }

    fun v(message: String) {
        if (isLoggingEnabled) {
            Log.v(tag, message)
        }
    }
}
