package kg.nurtelecom.processflow.ui.status

import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.core.text.HtmlCompat
import androidx.core.text.parseAsHtml
import androidx.core.view.isVisible
import kg.nurtelecom.processflow.R
import kg.nurtelecom.processflow.base.BaseProcessScreenFragment
import kg.nurtelecom.processflow.base.process.BackPressHandleState
import kg.nurtelecom.processflow.databinding.NurProcessFlowFragmentStatusInfoBinding
import kg.nurtelecom.processflow.extension.getProcessFlowHolder
import kg.nurtelecom.processflow.extension.gone
import kg.nurtelecom.processflow.extension.handleUrlClicks
import kg.nurtelecom.processflow.extension.loadImage
import kg.nurtelecom.processflow.extension.toTimeFromMillis
import kg.nurtelecom.processflow.extension.visible
import kg.nurtelecom.processflow.model.common.ScreenState
import kg.nurtelecom.processflow.model.common.StateScreenStatus
import kg.nurtelecom.processflow.util.AnimationData
import kg.nurtelecom.processflow.util.LottieAnimationHandler

open class ProcessStatusInfoFragment : BaseProcessScreenFragment<NurProcessFlowFragmentStatusInfoBinding>() {

    protected var lottieAnimationHandler: LottieAnimationHandler? = null

    private var isScreenCloseDisabled: Boolean = false

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

    private fun setupScreenClosureAvailability(isScreenCloseDisabled: Boolean) {
        this.isScreenCloseDisabled = isScreenCloseDisabled
        getProcessFlowHolder().setIsNavigationUpEnabled(!isScreenCloseDisabled)
    }

    override fun handleBackPress(): BackPressHandleState {
        return if (isScreenCloseDisabled) BackPressHandleState.HANDLED
        else super.handleBackPress()
    }
}
