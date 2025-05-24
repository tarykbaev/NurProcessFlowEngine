package kg.nurtelecom.processflow.util

import android.os.CountDownTimer

object TimerUtils {

    fun startCountDownTimer(
        duration: Long,
        interval: Long,
        onTick: (Long) -> Unit,
        onFinish: (() -> Unit)? = null
    ): CountDownTimer {
        return object : CountDownTimer(duration, interval) {
            override fun onTick(millisUntilFinished: Long) = onTick(millisUntilFinished)
            override fun onFinish() = onFinish?.invoke() ?: Unit
        }.apply { start() }
    }
}