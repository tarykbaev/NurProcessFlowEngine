package kg.nurtelecom.processflow.network.parser

import com.google.gson.Gson
import kg.nurtelecom.processflow.model.component.FlowFormInfoJson
import kg.nurtelecom.processflow.model.component.FlowInputField
import kg.nurtelecom.processflow.model.input_form.DatePickerFieldInfo
import kg.nurtelecom.processflow.model.input_form.DropDownFieldInfo
import kg.nurtelecom.processflow.model.input_form.FormItem
import kg.nurtelecom.processflow.model.input_form.FormItemType
import kg.nurtelecom.processflow.model.input_form.GroupButtonFormItem
import kg.nurtelecom.processflow.model.input_form.InputForm
import kg.nurtelecom.processflow.model.input_form.LabelFormItem
import kg.nurtelecom.processflow.model.input_form.PairFieldItem

open class InputFormMapper {

    private val gson = Gson()

    open fun map(formJson: FlowFormInfoJson): InputForm {

        val formItem = formJson.formItems.mapNotNull {
            when (it.formItemType) {
                kg.nurtelecom.processflow.model.component.FormItemType.INPUT_FIELD -> FormItem(
                    FormItemType.INPUT_FIELD,
                    gson.fromJson(it.formItem, FlowInputField::class.java)
                )

                kg.nurtelecom.processflow.model.component.FormItemType.GROUP_BUTTON_FORM_ITEM -> FormItem(
                    FormItemType.GROUP_BUTTON_FORM_ITEM,
                    gson.fromJson(it.formItem, GroupButtonFormItem::class.java)
                )

                kg.nurtelecom.processflow.model.component.FormItemType.DROP_DOWN_FORM_ITEM -> FormItem(
                    FormItemType.DROP_DOWN_FORM_ITEM,
                    gson.fromJson(it.formItem, DropDownFieldInfo::class.java)
                )

                kg.nurtelecom.processflow.model.component.FormItemType.DATE_PICKER_FORM_ITEM -> FormItem(
                    FormItemType.DATE_PICKER_FORM_ITEM,
                    gson.fromJson(it.formItem, DatePickerFieldInfo::class.java)
                )

                kg.nurtelecom.processflow.model.component.FormItemType.LABEL -> FormItem(
                    FormItemType.LABEL,
                    gson.fromJson(it.formItem, LabelFormItem::class.java)
                )

                kg.nurtelecom.processflow.model.component.FormItemType.PAIR_FIELD -> FormItem(
                    FormItemType.PAIR_FIELD,
                    gson.fromJson(it.formItem, PairFieldItem::class.java)
                )
                else -> null
            }
        }
        return InputForm(formJson.formId, formJson.title, formItem)
    }
}