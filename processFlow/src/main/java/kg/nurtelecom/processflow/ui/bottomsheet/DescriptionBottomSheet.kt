package kg.nurtelecom.processflow.ui.bottomsheet

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.core.text.parseAsHtml
import androidx.core.view.isVisible
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kg.nurtelecom.processflow.databinding.NurProcessFlowDescriptionBottomSheetBinding

class DescriptionBottomSheet : BottomSheetDialogFragment() {

    private lateinit var binding: NurProcessFlowDescriptionBottomSheetBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = NurProcessFlowDescriptionBottomSheetBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            btnAction.setOnClickListener { dismiss() }
            ivClose.setOnClickListener { dismiss() }
        }

        setTitle(arguments?.getString(TITLE_ARGS))
        setDescription(arguments?.getString(DESCRIPTION_ARGS))
        setButtonText(arguments?.getString(BUTTON_TEXT_ARGS))
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (view?.parent as View).setBackgroundColor(Color.TRANSPARENT)
    }

    private fun setButtonText(title: String?) = with(binding) {
        title?.let { btnAction.text = it }
    }

    private fun setTitle(title: String?) {
        binding.tvTitle.apply {
            isVisible = title != null
            text = title?.parseAsHtml(HtmlCompat.FROM_HTML_MODE_COMPACT)?.trimEnd()
        }
    }

    private fun setDescription(description: String?) {
        binding.tvDescription.apply {
            isVisible = description != null
            text = description?.parseAsHtml(HtmlCompat.FROM_HTML_MODE_COMPACT)?.trimEnd()
        }
    }

    companion object {

        const val DESCRIPTION_ARGS = "DESCRIPTION_ARGS"
        const val TITLE_ARGS = "TITLE_ARGS"
        const val BUTTON_TEXT_ARGS = "BUTTON_TEXT_ARGS"

        fun newInstance(title: String?, description: String, buttonText: String? = null): DescriptionBottomSheet {
            return DescriptionBottomSheet().apply {
                arguments = Bundle().apply {
                    putString(TITLE_ARGS, title)
                    putString(DESCRIPTION_ARGS, description)
                    putString(BUTTON_TEXT_ARGS, buttonText)
                }
            }
        }
    }
}