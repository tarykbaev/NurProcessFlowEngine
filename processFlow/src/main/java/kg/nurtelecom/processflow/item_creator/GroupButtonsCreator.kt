package kg.nurtelecom.processflow.item_creator

import android.content.Context
import kg.nurtelecom.processflow.custom_view.InputFormGroupButtons
import kg.nurtelecom.processflow.model.input_form.GroupButtonFormItem

object GroupButtonsCreator : ValidatableItem() {

    fun create(context: Context, groupInfo: GroupButtonFormItem, onSelectedChanged: (selected: List<String>, isValid: Boolean) -> Unit, onLinkClick: ((String) -> Unit)? = null) : InputFormGroupButtons {
        return InputFormGroupButtons(context).apply {
            tag = groupInfo.fieldId
            setSelectedItemChangedListener {
                val isValid = validateItem(groupInfo.validations, it)
                onSelectedChanged(it, isValid)
            }
            groupInfo.buttonType?.let { setButtonType(it) }
            groupInfo.chooseType?.let { setChooseType(it) }
            groupInfo.options?.let { setAllButtons(it) }
            renderButtons(onLinkClick)
        }
    }
}