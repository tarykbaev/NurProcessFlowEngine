package kg.nurtelecom.processflowengine.online

import android.view.View
import kg.nurtelecom.processflow.base.BaseProcessScreenFragment
import kg.nurtelecom.processflow.base.process.BackPressHandleState
import kg.nurtelecom.processflowengine.databinding.FragmentDocumetsBinding

class DocumetsFragment : BaseProcessScreenFragment<FragmentDocumetsBinding>() {
    override val unclickableMask: View?
        get() = null

    override fun inflateViewBinging(): FragmentDocumetsBinding {
        return FragmentDocumetsBinding.inflate(layoutInflater)
    }

    override fun handleBackPress(): BackPressHandleState {
        return BackPressHandleState.CALL_SUPER
    }
}