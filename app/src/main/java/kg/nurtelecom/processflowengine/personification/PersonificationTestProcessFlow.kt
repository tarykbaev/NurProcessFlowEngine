package kg.nurtelecom.processflowengine.personification

import android.content.Context
import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.Single
import kg.nurtelecom.processflow.extension.toTimeFromMillis
import kg.nurtelecom.processflow.model.Event
import kg.nurtelecom.processflow.model.ProcessFlowCommit
import kg.nurtelecom.processflow.model.common.Content
import kg.nurtelecom.processflow.model.component.FlowButton
import kg.nurtelecom.processflow.model.request.FlowResponse
import kg.nurtelecom.processflow.repository.ProcessFlowRepository
import kg.nurtelecom.processflow.ui.main.ProcessFlowActivity
import kg.nurtelecom.processflow.ui.main.ProcessFlowVM
import kg.nurtelecom.processflow.util.TimerUtils.startCountDownTimer

class PersonificationTestProcessFlow : ProcessFlowActivity<PersonificationTestVM>() {

    private var countDownTimer: CountDownTimer? = null

    override fun setupViews() {
        super.setupViews()
        vm.remainingTime.observe(this) {
            setRemainingTime(it)
        }
    }

    override val vm: PersonificationTestVM by lazy {
        PersonificationTestVM(this)
    }
    override val processType: String
        get() = "TEST_TYPE"

    override fun getProcessFlowStartParams(): Map<String, String> {
        return mapOf("identificationNumber" to "123456678")
    }

    override fun resolveButtonClickCommit(button: FlowButton?, additionalContent: List<Content>?) {
        if (button?.buttonId == "EXIT_NAVIGATE_TO_WALLET_MAIN") finish()
        else super.resolveButtonClickCommit(button, additionalContent)
    }

    override fun onDestroy() {
        countDownTimer?.cancel()
        super.onDestroy()
    }

    private fun setRemainingTime(remainingTime: Long?) {
        countDownTimer?.cancel()
        if (remainingTime != null) {
            countDownTimer = startCountDownTimer(
                remainingTime,
                COUNTDOWN_INTERVAL,
                onTick = {
                    vb.chiliToolbar.setAdditionalText(it.toTimeFromMillis)
                }
            )
        }
    }

    companion object {
        private const val COUNTDOWN_INTERVAL = 1000L
    }
}

object MyCommit : ProcessFlowCommit()

sealed class MyEvent : Event() {
    object MySubEvent : MyEvent()
}

class PersonificationTestRepo(context: Context) :
    ProcessFlowRepository(PersonificationFlowApiImpl) {

}


class PersonificationTestVM(context: Context) :
    ProcessFlowVM<ProcessFlowRepository>(PersonificationTestRepo(context)) {

    private val _remainingTime = MutableLiveData<Long?>()
    val remainingTime: LiveData<Long?> get() = _remainingTime

    private fun startPersonificationRemainingTime(timerTime: Long?) {
        _remainingTime.postValue(timerTime)
    }

    override fun dispatchValuesToLiveData(response: FlowResponse): Single<FlowResponse> {
        startPersonificationRemainingTime(1200000L)
        return super.dispatchValuesToLiveData(response)
    }
}
