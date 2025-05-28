package kg.nurtelecom.processflow.ui.camera.instruction.photo

import android.os.Bundle
import android.view.View
import com.design2.chili2.extensions.setOnSingleClickListener
import kg.nurtelecom.processflow.R
import kg.nurtelecom.processflow.extension.visible
import kg.nurtelecom.processflow.ui.camera.instruction.BasePhotoInstructionFragment

class PassportFrontInstructionFragment : BasePhotoInstructionFragment() {

    override fun getInstructionTitleRes() = R.string.process_flow_photo_instruction_passport_front
    override fun getInstructionSubtitleRes(): Int =
        R.string.process_flow_photo_instructiin_passports_subtitle

    override fun getInstructionImageRes() = R.drawable.process_flow_ic_instruction_id_card_front

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vb.btnAdditional.apply {
            visible()
            setOnSingleClickListener {
                // todo commit message to open Tunduk flow
            }
        }
    }
}
