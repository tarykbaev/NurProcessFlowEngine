package kg.nurtelecom.processflow.ui.camera.instruction.photo

import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import kg.nurtelecom.processflow.ProcessFlowConfigurator
import kg.nurtelecom.processflow.R
import kg.nurtelecom.processflow.ui.camera.instruction.BasePhotoInstructionFragment

class ForeignPassportInstructionFragment : BasePhotoInstructionFragment() {

    override fun getInstructionTitleRes() =
        R.string.nur_process_flow_photo_instruction_passport_foreigner

    override fun getInstructionSubtitleRes(): Int =
        R.string.nur_process_flow_photo_instruction_passport_foreigner_subtitle

    override fun getInstructionImageRes() = -1
    override fun getInstructionImageUrl() =
        ProcessFlowConfigurator.foreignPassportInstructionUrlResolver.invoke()

    override fun setupImageViewCustomizations(imageView: ImageView) {
        val params = imageView.layoutParams as ConstraintLayout.LayoutParams
        params.width = resources.getDimensionPixelSize(R.dimen.view_240dp)
        params.height = resources.getDimensionPixelSize(R.dimen.view_340dp)
        params.leftMargin = 0
        params.topMargin = 0
        params.rightMargin = 0
        params.bottomMargin = 0
        imageView.layoutParams = params
        imageView.requestLayout()
    }
}