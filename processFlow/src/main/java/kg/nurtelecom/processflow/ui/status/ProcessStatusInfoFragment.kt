package kg.nurtelecom.processflow.ui.status

import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.core.text.HtmlCompat
import androidx.core.text.parseAsHtml
import androidx.core.view.isVisible
import kg.nurtelecom.processflow.R
import kg.nurtelecom.processflow.base.BaseProcessScreenFragment
import kg.nurtelecom.processflow.base.process.BackPressHandleState
import kg.nurtelecom.processflow.custom_view.InputFormGroupButtons
import kg.nurtelecom.processflow.databinding.NurProcessFlowFragmentStatusInfoBinding
import kg.nurtelecom.processflow.extension.getProcessFlowHolder
import kg.nurtelecom.processflow.extension.gone
import kg.nurtelecom.processflow.extension.handleUrlClicks
import kg.nurtelecom.processflow.extension.loadImage
import kg.nurtelecom.processflow.extension.toTimeFromMillis
import kg.nurtelecom.processflow.extension.visible
import kg.nurtelecom.processflow.item_creator.GroupButtonsCreator
import kg.nurtelecom.processflow.model.ProcessFlowCommit
import kg.nurtelecom.processflow.model.common.ScreenState
import kg.nurtelecom.processflow.model.common.StateScreenStatus
import kg.nurtelecom.processflow.model.component.FlowButton
import kg.nurtelecom.processflow.model.input_form.GroupButtonFormItem
import kg.nurtelecom.processflow.model.input_form.Option
import kg.nurtelecom.processflow.model.input_form.Validation
import kg.nurtelecom.processflow.model.input_form.ValidationType
import kg.nurtelecom.processflow.ui.bottomsheet.DescriptionBottomSheet
import kg.nurtelecom.processflow.util.AnimationData
import kg.nurtelecom.processflow.util.LottieAnimationHandler

open class ProcessStatusInfoFragment : BaseProcessScreenFragment<NurProcessFlowFragmentStatusInfoBinding>() {

    protected var lottieAnimationHandler: LottieAnimationHandler? = null

    private var isScreenCloseDisabled: Boolean = false

    private val result = HashMap<String, List<String>?>()

    override val unclickableMask: View?
        get() = vb.unclickableMask

    override val buttonsLinearLayout: LinearLayout?
        get() = vb.llButtons

    override fun onStart() {
        super.onStart()
        getProcessFlowHolder().setIsNavigationUpEnabled(!isScreenCloseDisabled)
    }

    override fun onStop() {
        super.onStop()
        getProcessFlowHolder().setIsNavigationUpEnabled(true)
    }

    override fun renderScreenState(state: ScreenState?) {
        super.renderScreenState(state)
        state?.run {
            vb.tvTitle.text = title ?: ""
            vb.tvTitle.isVisible = title != null
            if (isDescriptionHtml == true) {
                vb.tvSubtitle.text = description?.parseAsHtml(HtmlCompat.FROM_HTML_MODE_COMPACT)?.trimEnd()
                vb.tvSubtitle.handleUrlClicks {
                    vb.tvSubtitle.invalidate()
                    onLinkClick(it)
                }
            } else vb.tvSubtitle.text = description ?: ""
            vb.tvSubtitle.isVisible = description != null
            setupStatusIcon(status, statusImageUrl, animationUrl)
            setupTopImage(topImageUrl)
            setupTimer(state)
            setupScreenClosureAvailability(state.isScreenCloseDisabled ?: false)
            setupBottomAgreement(state.bottomDescriptionHtml)
            setupToolbarEndIcon(state)
        }
    }

    override fun inflateViewBinging() = NurProcessFlowFragmentStatusInfoBinding.inflate(layoutInflater)

    override fun onDestroyView() {
        lottieAnimationHandler?.removeListeners()
        lottieAnimationHandler = null
        super.onDestroyView()
    }

    protected fun getOrCreateLottieAnimationHandler(): LottieAnimationHandler {
        return lottieAnimationHandler ?: LottieAnimationHandler(vb.lavStatus).also {
            lottieAnimationHandler = it
        }
    }

    protected open fun setupTopImage(topImageUrl: String?) = with(vb.ivTopImage) {
        isVisible = !topImageUrl.isNullOrEmpty()
        topImageUrl?.let {
            loadImage(it)
        }
    }

    protected open fun setupStatusIcon(
        stateScreenStatus: StateScreenStatus?,
        statusImageUrl: String?,
        animationUrl: String?
    ): Unit = with(vb) {
        vb.lavStatus.gone()
        vb.ivStatus.gone()
        when {
            animationUrl != null -> {
                getOrCreateLottieAnimationHandler().addToAnimationQueue(
                    AnimationData(
                        animationUrl = animationUrl,
                        isInfiniteRepeat = stateScreenStatus == StateScreenStatus.IN_PROCESS
                    )
                )
                lavStatus.visible()
            }
            statusImageUrl != null -> ivStatus.apply {
                loadImage(statusImageUrl)
                visible()
            }
            stateScreenStatus == StateScreenStatus.IN_PROCESS -> {
                getOrCreateLottieAnimationHandler().addToAnimationQueue(AnimationData(
                    animationRes = R.raw.process_flow_lottie_anim_loop,
                    animationUrl = animationUrl,
                    isInfiniteRepeat = true
                ))
                lavStatus.visible()
            }
            stateScreenStatus == StateScreenStatus.COMPLETE -> {
                getOrCreateLottieAnimationHandler().addToAnimationQueue(AnimationData(
                    animationRes = R.raw.process_flow_lottie_anim_done,
                    animationUrl = animationUrl,
                ))
                lavStatus.visible()
            }
            stateScreenStatus == StateScreenStatus.REJECTED -> {
                getOrCreateLottieAnimationHandler().addToAnimationQueue(AnimationData(
                    animationRes = R.raw.process_flow_lottie_anim_reject,
                    animationUrl = animationUrl,
                ))
                lavStatus.visible()
            }
            stateScreenStatus == StateScreenStatus.WARNING -> {
                getOrCreateLottieAnimationHandler().addToAnimationQueue(AnimationData(
                    animationRes = R.raw.process_flow_lottie_anim_reject,
                    animationUrl = animationUrl,
                ))
                lavStatus.visible()
            }
        }
    }

    private fun setupTimer(state: ScreenState?) {
        val timer = state?.timer

        if (timer != null) {
            val timerText = state.timerText ?: ""
            vb.tvTimer.visible()
            setupTimerFor(
                timer,
                { vb.tvTimer.gone() },
                { vb.tvTimer.text = "$timerText ${it.toTimeFromMillis}" })
        } else {
            vb.tvTimer.gone()
        }
    }

    private fun setupToolbarEndIcon(state: ScreenState?) {
        if (state?.isBottomSheetAvailable() != true) return
        getProcessFlowHolder().setupToolbarEndIcon(R.drawable.nur_process_flow_ic_faq_24dp) {
            DescriptionBottomSheet.newInstance(
                state.infoTitle,
                state.infoDescHtml.orEmpty()
            ).show(childFragmentManager, null)
        }
    }

    private fun setupScreenClosureAvailability(isScreenCloseDisabled: Boolean) {
        this.isScreenCloseDisabled = isScreenCloseDisabled
        getProcessFlowHolder().setIsNavigationUpEnabled(!isScreenCloseDisabled)
    }

    private fun setupBottomAgreement(link: String?) {
        if (link.isNullOrEmpty()) return

        val container = createContainer()

        container.addView(createButtonGroup(createAgreementFormItem(link)))

        vb.bottomContainer.apply {
            removeAllViews()
            addView(container)
        }
    }

    private fun createContainer(): LinearLayout {
        return LinearLayout(context).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            )
            orientation = LinearLayout.VERTICAL
        }
    }

    private fun createAgreementFormItem(link: String): GroupButtonFormItem {
        val option = Option(
            id = "option_id",
            isHtmlText = true,
            label = link,
            isSelected = true
        )
        val validation = Validation(ValidationType.REQUIRED, "true")

        return GroupButtonFormItem(
            fieldId = "field_id",
            options = listOf(option),
            validations = listOf(validation)
        )
    }

    private fun createButtonGroup(groupInfo: GroupButtonFormItem): InputFormGroupButtons {
        result[groupInfo.fieldId] = null
        return GroupButtonsCreator.create(requireContext(), groupInfo, onLinkClick = ::onLinkClick, onSelectedChanged = { values, isValid ->
            result[groupInfo.fieldId] = if (isValid) values else null
        })
    }

    private fun validateInput(): Boolean {
        var isValid = true
        result.forEach {
            if (it.value == null) {
                isValid = false
                val view = vb.bottomContainer.findViewWithTag<View>(it.key)
                if (view is InputFormGroupButtons) view.setupAsError()
            }
        }
        return isValid
    }

    override fun onButtonClick(buttonsInfo: FlowButton) {
        if (!validateInput()) return
        selectedButtonId = buttonsInfo.buttonId
        getProcessFlowHolder().commit(ProcessFlowCommit.OnButtonClick(buttonsInfo))
    }

    override fun onLinkClick(link: String) {
        if (link.startsWith("http") || link.endsWith(".pdf", ignoreCase = true)) {
            super.onLinkClick(link)
        } else {
            getProcessFlowHolder().commit(ProcessFlowCommit.OnButtonClick(FlowButton(link)))
        }
    }

    override fun handleBackPress(): BackPressHandleState {
        return if (isScreenCloseDisabled) BackPressHandleState.HANDLED
        else super.handleBackPress()
    }
}
