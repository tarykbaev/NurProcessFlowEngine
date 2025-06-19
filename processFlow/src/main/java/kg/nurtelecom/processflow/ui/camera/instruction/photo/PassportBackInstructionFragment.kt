package kg.nurtelecom.processflow.ui.camera.instruction.photo

import kg.nurtelecom.processflow.R
import kg.nurtelecom.processflow.ui.camera.instruction.BasePhotoInstructionFragment

class PassportBackInstructionFragment : BasePhotoInstructionFragment() {

    override fun getInstructionTitleRes() = R.string.nur_process_flow_photo_instruction_passport_back
    override fun getInstructionSubtitleRes(): Int =
        R.string.nur_process_flow_photo_instructiin_passports_subtitle

    override fun getInstructionImageRes() = R.drawable.nur_process_flow_ic_instruction_id_card_back

}
