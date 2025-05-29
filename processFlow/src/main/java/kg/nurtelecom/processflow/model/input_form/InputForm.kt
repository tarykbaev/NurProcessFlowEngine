package kg.nurtelecom.processflow.model.input_form

import java.io.Serializable

data class InputForm(
    val formId: String,
    val title: String? = null,
    val formItems: List<FormItem>? = null
): Serializable
