package kg.nurtelecom.processflow.ui.status

import kg.nurtelecom.processflow.R
import kg.nurtelecom.processflow.extension.visible
import kg.nurtelecom.processflow.model.common.StateScreenStatus
import kg.nurtelecom.processflow.util.AnimationData

class VideoPromoStatusFragment : ProcessStatusInfoFragment() {

    override fun setupStatusIcon(
        stateScreenStatus: StateScreenStatus?,
        statusImageUrl: String?,
        animationUrl: String?
    ) {
        getOrCreateLottieAnimationHandler().addToAnimationQueue(AnimationData(R.raw.process_flow_lottie_video))
        vb.lavStatus.visible()
    }
}