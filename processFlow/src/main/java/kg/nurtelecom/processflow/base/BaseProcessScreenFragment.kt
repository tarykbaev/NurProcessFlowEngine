package kg.nurtelecom.processflow.base

import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.viewbinding.ViewBinding
import com.design2.chili2.R
import com.design2.chili2.view.buttons.ChiliButton
import com.design2.chili2.view.input.BaseInputView
import com.design2.chili2.view.input.otp.OtpInputView
import kg.nurtelecom.processflow.base.process.ProcessFlowScreen
import kg.nurtelecom.processflow.extension.getProcessFlowHolder
import kg.nurtelecom.processflow.extension.setMargins
import kg.nurtelecom.processflow.item_creator.FlowButtonCreator
import kg.nurtelecom.processflow.item_creator.InputFieldCreator
import kg.nurtelecom.processflow.item_creator.OtpInputViewCreator
import kg.nurtelecom.processflow.model.ProcessFlowCommit
import kg.nurtelecom.processflow.model.ProcessFlowScreenData
import kg.nurtelecom.processflow.model.common.ScreenState
import kg.nurtelecom.processflow.model.component.ButtonProperties
import kg.nurtelecom.processflow.model.component.FlowButton
import kg.nurtelecom.processflow.model.component.FlowInputField
import kg.nurtelecom.processflow.model.component.FlowMessage
import kg.nurtelecom.processflow.model.component.FlowRetryInfo
import java.util.Date

abstract class BaseProcessScreenFragment<VB: ViewBinding> : BaseFragment<VB>(), ProcessFlowScreen {

    protected var countDownTimers = mutableListOf<CountDownTimer?>()

    protected var selectedButtonId: String? = null

    protected var isAppThemeLight: String = "true"

    protected var appLocale: String = "ru"

    abstract val unclickableMask: View?

    open val buttonsLinearLayout: LinearLayout? = null
    open val inputFieldContainer: FrameLayout? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        clearPrevToolbarState()
    }

    override fun handleShowLoading(isLoading: Boolean): Boolean {
        if (selectedButtonId == null) return false
        try {
            buttonsLinearLayout?.findViewWithTag<ChiliButton>(selectedButtonId)?.setIsLoading(isLoading) ?: return false
            unclickableMask?.isVisible = isLoading
        } catch (ex: Throwable) {
            return false
        }
        if (!isLoading) selectedButtonId = ""
        return true
    }

    override fun setScreenData(data: ProcessFlowScreenData?) {
        resetTimers()
        parseAllowedAnswers(data?.allowedAnswer)
        renderScreenState(data?.state)
        renderMessages(data?.message)
    }

    open fun setThemeAndLocale(isLightTheme: Boolean, appLocale: String) {
        this.isAppThemeLight = isLightTheme.toString()
        this.appLocale = appLocale
    }

    private fun parseAllowedAnswers(allowedAnswers: List<Any?>?) {
        clearPrevState()
        allowedAnswers?.forEach { allowedAnswer ->
            when (allowedAnswer) {
                is FlowButton -> buttonsLinearLayout?.let { renderButton(it, allowedAnswer) }
                is FlowInputField -> {
                    if (allowedAnswer.isOtpView == true) inputFieldContainer?.let { renderOtpInputView(it, allowedAnswer) }
                    else inputFieldContainer?.let { renderInputField(it, allowedAnswer) }
                }
            }
        }
    }

    private fun clearPrevState() {
        selectedButtonId = null
        buttonsLinearLayout?.removeAllViews()
        inputFieldContainer?.removeAllViews()
    }

    protected open fun clearPrevToolbarState() {
        getProcessFlowHolder().apply {
            setToolbarTitle("")
            setupToolbarEndIcon(null, null)
            setToolbarNavIcon(R.drawable.chili_ic_close)
        }
    }

    //InputField handler
    open fun renderInputField(inputFieldContainer: FrameLayout, inputFieldInfo: FlowInputField): BaseInputView {
        val inputFiledView = InputFieldCreator.create(requireContext(), inputFieldInfo, ::inputFieldChanged)
        inputFieldContainer.addView(inputFiledView)
        return inputFiledView.apply {
            requestInputFocus()
            showSystemKeyboard()
        }
    }

    //OtpInputView
    open fun renderOtpInputView(inputFieldContainer: FrameLayout, inputFieldInfo: FlowInputField): OtpInputView {
        val otpView = OtpInputViewCreator.create(requireContext(), inputFieldInfo, ::inputFieldChanged)
        inputFieldContainer.addView(otpView)
        return otpView.apply { requestFocusAndShowKeyboard() }
    }

    open fun inputFieldChanged(result: List<String>, isValid: Boolean) {}

    //Messages handler
    open fun renderMessages(messages: List<FlowMessage?>? = null) {}

    //Screen state handler
    open fun renderScreenState(state: ScreenState? = null) {
        getProcessFlowHolder().setToolbarTitle(state?.appBarText ?: "")
    }

    // Button Handler
    open fun renderButton(buttonsContainer: ViewGroup, buttonInfo: FlowButton) {
        buttonsContainer.addView(
            FlowButtonCreator.create(requireContext(), buttonInfo, ::onButtonClick).apply {
                setMargins(R.dimen.padding_16dp, R.dimen.padding_16dp, R.dimen.padding_16dp, R.dimen.padding_0dp)
                setupTimerFor(
                    buttonInfo.properties?.get(ButtonProperties.ENABLE_AT.propertyName)?.toLongOrNull(),
                    { this.isEnabled = true },
                    { this.isEnabled = false }
                )
            }
        )
    }

    open fun onButtonClick(buttonsInfo: FlowButton) {
        selectedButtonId = buttonsInfo.buttonId
        getProcessFlowHolder().commit(ProcessFlowCommit.OnButtonClick(buttonsInfo))
    }

    open fun onHandleRetry(retry: FlowRetryInfo? = null) {}

    open fun onLinkClick(link: String) {
        getProcessFlowHolder().commit(ProcessFlowCommit.OnLinkClicked(link))
    }

    override fun handleMultipleFileLoaderContentType(loadedType: String): String? {
        return null
    }

    protected fun setupTimerFor(timestamp: Long?, onFinish: () -> Unit, onTick: (Long) -> Unit) {
        if (timestamp == null) return
        val mills = timestamp - Date().time
        setupMillsTimerFor(mills, onFinish, onTick)
    }

    protected fun setupMillsTimerFor(mills: Long?, onFinish: () -> Unit, onTick: (Long) -> Unit) {
        if (mills == null) return
        if (mills <= 0) return onFinish.invoke()
        countDownTimers += object : CountDownTimer(mills, 1000) {
            override fun onTick(millisUntilFinished: Long) { onTick(millisUntilFinished) }
            override fun onFinish() { onFinish() }
        }.apply {
            start()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        resetTimers()
    }

    protected fun resetTimers() {
        countDownTimers.forEach { it?.cancel() }
        countDownTimers.clear()
    }
}