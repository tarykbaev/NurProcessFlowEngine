package kg.nurtelecom.processflow.base.process

import com.design2.chili2.R
import kg.nurtelecom.processflow.model.ProcessFlowCommit

interface ProcessFlowHolder {

    fun setToolbarNavIcon(navIconRes: Int = R.drawable.chili_ic_close)
    fun setToolbarTitle(title: String = "")
    fun setupToolbarEndIcon(iconRes: Int?, onClick: (() -> Unit)?)
    fun setIsToolbarVisible(isVisible: Boolean)
    fun setIsNavigationUpEnabled(isEnabled: Boolean)
    fun setToolbarEndText(text: String?)

    fun commit(commit: ProcessFlowCommit)

    fun setIsActivityLoading(isLoading: Boolean)
    fun setToolbarTitleCentered(isCentered: Boolean)

}