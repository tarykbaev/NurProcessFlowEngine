package kg.nurtelecom.processflow.item_creator

import kg.nurtelecom.processflow.model.input_form.Validation
import kg.nurtelecom.processflow.model.input_form.ValidationType


open class ValidatableItem {

    fun validateItem(validations: List<Validation>?, values: List<String>): Boolean {
        validations?.forEach {
            when (it.type) {
                ValidationType.REQUIRED -> {
                    if (it.value == "true" && (values.isEmpty() || values.firstOrNull().isNullOrBlank())) {
                        return false
                    }
                }
                ValidationType.REGEX -> {
                    if (it.value != null && (!(values.firstOrNull() ?: "").matches(it.value.toRegex()))) {
                        return false
                    }
                }
                else -> {}
            }
        }
        return true
    }
}