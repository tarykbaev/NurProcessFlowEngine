package kg.nurtelecom.processflow.model

import kg.nurtelecom.processflow.model.common.ScreenState
import kg.nurtelecom.processflow.model.component.FlowMessage
import kg.nurtelecom.processflow.model.request.ProcessVariable

data class ProcessFlowScreenData(
    val screenKey: String? = null,
    val state: ScreenState? = null,
    val allowedAnswer: List<Any?>? = null,
    val message: List<FlowMessage?>? = null,
    val processVariable: ProcessVariable? = null
)