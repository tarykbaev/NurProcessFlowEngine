package kg.nurtelecom.processflow.ui.camera.instruction

import android.os.Bundle
import android.view.View
import com.design2.chili2.extensions.setOnSingleClickListener
import kg.nurtelecom.processflow.base.BaseFragment
import kg.nurtelecom.processflow.databinding.NurProcessFlowFragmentPhotoInstructionBinding
import kg.nurtelecom.processflow.extension.getProcessFlowHolder
import kg.nurtelecom.processflow.extension.loadImage
import kg.nurtelecom.processflow.ui.camera.PhotoFlowFragment

abstract class BasePhotoInstructionFragment : BaseFragment<NurProcessFlowFragmentPhotoInstructionBinding>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupPhotoInstructionView()
        vb.btnAction.setOnSingleClickListener {
            (parentFragment as PhotoFlowFragment).startPhotoFlow()
        }
        getProcessFlowHolder().setToolbarTitle("")
    }

    override fun inflateViewBinging() =
        NurProcessFlowFragmentPhotoInstructionBinding.inflate(layoutInflater)

    abstract fun getInstructionTitleRes(): Int
    abstract fun getInstructionSubtitleRes(): Int
    abstract fun getInstructionImageRes(): Int
    open fun getInstructionImageUrl(): String? = null

    private fun setupPhotoInstructionView() {
        with(vb) {
            tvTitle.text = getString(getInstructionTitleRes())
            tvSubtitle.text = getString(getInstructionSubtitleRes())
            ivCorrect.apply {
                getInstructionImageUrl()?.let {
                    loadImage(it)
                } ?: setImageResource(getInstructionImageRes())
            }
        }
    }
}