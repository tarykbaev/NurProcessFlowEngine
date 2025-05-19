package kg.nurtelecom.processflow.base.process

import kg.nurtelecom.processflow.model.ProcessFlowScreenData

interface ProcessFlowScreen {

    fun setScreenData(data: ProcessFlowScreenData? = null) {}

    fun handleBackPress(): BackPressHandleState = BackPressHandleState.NOT_HANDLE
    fun handleShowLoading(isLoading: Boolean): Boolean = false
    fun handleMultipleFileLoaderContentType(loadedType: String): String? = null
}

enum class BackPressHandleState {
    HANDLED, NOT_HANDLE, CALL_SUPER
}