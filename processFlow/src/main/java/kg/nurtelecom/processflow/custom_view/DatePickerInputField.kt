package kg.nurtelecom.processflow.custom_view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import kg.nurtelecom.processflow.databinding.NurProcessFlowViewFormItemDatePickerBinding
import kg.nurtelecom.processflow.extension.getThemeColor
import kg.nurtelecom.processflow.item_creator.DatePickerFieldCreator
import kg.nurtelecom.processflow.model.input_form.DatePickerFieldInfo
import java.text.SimpleDateFormat
import java.util.Date

class DatePickerInputField @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null) : LinearLayout(context, attributeSet) {

    private val dateFormat = SimpleDateFormat("dd.MM.yyyy")
    private var onNewValueListener: ((List<String>, Boolean) -> Unit)? = null

    private var datePickerFieldInfo: DatePickerFieldInfo? = null

    private val vb: NurProcessFlowViewFormItemDatePickerBinding = NurProcessFlowViewFormItemDatePickerBinding.inflate(LayoutInflater.from(context), this, true)

    fun setupViews(datePickerFieldInfo: DatePickerFieldInfo, onSetNewValue: (List<String>, Boolean) -> Unit) {
        this.onNewValueListener = onSetNewValue
        datePickerFieldInfo.hint?.let { setHelperText(it) }
        datePickerFieldInfo.placeHolder?.let { setHint(it) }
        datePickerFieldInfo.label?.let { setLabel(it) }
        setDate(datePickerFieldInfo.value)
    }

    fun setHint(hint: String) {
        vb.tvLabel.apply {
            text = hint
            setTextColor(ContextCompat.getColor(context, com.design2.chili2.R.color.gray_1_alpha_50))
        }
    }

    private fun setText(text: String) {
        if (text.isBlank()) return
        vb.tvLabel.apply {
            this.text = text
            setTextColor(context.getThemeColor(com.design2.chili2.R.attr.ChiliPrimaryTextColor))
        }
    }

    fun setDate(dateLong: Long?) {
        val values = dateLong?.toString()?.let { listOf(it) } ?: emptyList()
        val isValid = DatePickerFieldCreator.validateItem(datePickerFieldInfo?.validations, values)
        if (dateLong == null) {
            setText("")
        } else {
            setText(dateFormat.format(Date(dateLong)))
        }
        onNewValueListener?.invoke(values, isValid)
    }

    fun setLabel(label: String) {
        setHint(label)
    }

    fun setHelperText(helperText: String) {
        vb.tvHelper.apply {
            visibility = VISIBLE
            text = helperText
        }
    }

    fun setupAsError() {
        vb.llFieldContainer.setBackgroundResource(com.design2.chili2.R.drawable.chili_bg_input_view_error_rounded)
    }

    fun clearError() {
        vb.llFieldContainer.setBackgroundResource(com.design2.chili2.R.drawable.chili_bg_input_view_rounded)
    }
}