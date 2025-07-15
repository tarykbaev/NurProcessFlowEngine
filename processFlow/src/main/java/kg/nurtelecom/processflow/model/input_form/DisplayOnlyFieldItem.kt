package kg.nurtelecom.processflow.model.input_form

import java.io.Serializable

data class DisplayOnlyFieldItem(
    val fieldId: String,
    val label: String? = null,
    val value: String? = null,
    val isClickable: Boolean? = false,
    val description: String? = null
): Serializable