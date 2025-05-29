package kg.nurtelecom.processflow.ui.camera.instruction.photo

import android.os.Bundle
import android.view.View
import com.design2.chili2.extensions.setOnSingleClickListener
import kg.nurtelecom.processflow.ProcessFlowConfigurator
import kg.nurtelecom.processflow.R
import kg.nurtelecom.processflow.base.BaseFragment
import kg.nurtelecom.processflow.databinding.NurProcessFlowFragmentPhotoSelfeInstrustionBinding
import kg.nurtelecom.processflow.extension.getProcessFlowHolder
import kg.nurtelecom.processflow.extension.loadImage
import kg.nurtelecom.processflow.ui.camera.PhotoFlowFragment

class SimpleSelfiePhotoInstructionFragment : BaseFragment<NurProcessFlowFragmentPhotoSelfeInstrustionBinding>() {

    override fun inflateViewBinging() = NurProcessFlowFragmentPhotoSelfeInstrustionBinding.inflate(layoutInflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupPhotoInstructionView()
        setupActionButton()
        getProcessFlowHolder().setToolbarTitle("")
    }

    private fun setupActionButton() {
        vb.btnAction.setOnSingleClickListener {
            (parentFragment as PhotoFlowFragment).startPhotoFlow()
        }
    }

    private fun setupPhotoInstructionView() {
        with(vb) {
            tvTitle.text = getString(R.string.process_flow_photo_instruction_simple_selfie_title)
            tvSubtitle.text = getString(R.string.process_flow_photo_instruction_simple_selfie_subtitle)
            ivCorrect.loadImage(ProcessFlowConfigurator.simpleSelfieInstructionUrlResolver.invoke())
        }
    }
}