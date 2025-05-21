package kg.nurtelecom.processflow.ui.citizenship

import android.view.View
import kg.nurtelecom.processflow.base.BaseProcessScreenFragment
import kg.nurtelecom.processflow.databinding.FragmentCitizenshipChoosingBinding

class CitizenshipChoosingFragment: BaseProcessScreenFragment<FragmentCitizenshipChoosingBinding>() {

    override val unclickableMask: View = vb.unclickableMask

    override fun inflateViewBinging(): FragmentCitizenshipChoosingBinding =
        FragmentCitizenshipChoosingBinding.inflate(layoutInflater)
}