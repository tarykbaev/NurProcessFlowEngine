package kg.nurtelecom.processflow.model

import kg.nurtelecom.processflow.model.input_form.Option


open class Event {

    class Notification(val message: String) : Event()
    class NotificationResId(val messageResId: Int) : Event()

    //Flow
    class ProcessFlowIsExist(val isExist: Boolean, val subProcessFlowType: String? = null): Event()
    object FlowCancelledCloseActivity: Event()


    //InputForm
    class AdditionalOptionsFetched(val formId: String, val options: List<Option>): Event()

}