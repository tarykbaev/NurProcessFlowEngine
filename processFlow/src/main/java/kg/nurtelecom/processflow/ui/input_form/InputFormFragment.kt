package kg.nurtelecom.processflow.ui.input_form

import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentResultListener
import com.design2.chili2.view.input.BaseInputView
import com.design2.chili2.view.modals.picker.DatePickerDialog
import kg.nurtelecom.processflow.R
import kg.nurtelecom.processflow.base.BaseProcessScreenFragment
import kg.nurtelecom.processflow.custom_view.DatePickerInputField
import kg.nurtelecom.processflow.custom_view.drop_down_input_field.DropDownInputField
import kg.nurtelecom.processflow.custom_view.InputFormGroupButtons
import kg.nurtelecom.processflow.databinding.NurProcessFlowFragmentInputFormBinding
import kg.nurtelecom.processflow.extension.getProcessFlowHolder
import kg.nurtelecom.processflow.extension.gone
import kg.nurtelecom.processflow.extension.hideKeyboard
import kg.nurtelecom.processflow.extension.visible
import kg.nurtelecom.processflow.item_creator.DatePickerFieldCreator
import kg.nurtelecom.processflow.item_creator.DisplayOnlyFieldItemCreator
import kg.nurtelecom.processflow.item_creator.DropDownFieldCreator
import kg.nurtelecom.processflow.item_creator.GroupButtonsCreator
import kg.nurtelecom.processflow.item_creator.InputFieldCreator
import kg.nurtelecom.processflow.item_creator.LabelFormItemCreator
import kg.nurtelecom.processflow.item_creator.PairFieldItemCreator
import kg.nurtelecom.processflow.model.ButtonIds.SUBMIT
import kg.nurtelecom.processflow.model.ContentTypes
import kg.nurtelecom.processflow.model.ProcessFlowCommit
import kg.nurtelecom.processflow.model.ProcessFlowScreenData
import kg.nurtelecom.processflow.model.common.Content
import kg.nurtelecom.processflow.model.common.ScreenState
import kg.nurtelecom.processflow.model.component.FlowButton
import kg.nurtelecom.processflow.model.component.FlowInputField
import kg.nurtelecom.processflow.model.input_form.DatePickerFieldInfo
import kg.nurtelecom.processflow.model.input_form.DisplayOnlyFieldItem
import kg.nurtelecom.processflow.model.input_form.DropDownFieldInfo
import kg.nurtelecom.processflow.model.input_form.EnteredValue
import kg.nurtelecom.processflow.model.input_form.FormResponse
import kg.nurtelecom.processflow.model.input_form.GroupButtonFormItem
import kg.nurtelecom.processflow.model.input_form.InputForm
import kg.nurtelecom.processflow.model.input_form.LabelFormItem
import kg.nurtelecom.processflow.model.input_form.Option
import kg.nurtelecom.processflow.model.input_form.PairFieldItem
import java.util.Calendar

class InputFormFragment : BaseProcessScreenFragment<NurProcessFlowFragmentInputFormBinding>(), FragmentResultListener {

    private val optionsRelations = HashSet<OptionFieldParentRelation>()

    private var currentFormId: String = ""

    private val scrollOffset16px: Int by lazy { resources.getDimensionPixelSize(R.dimen.padding_75dp) }

    private var currentOpenedDatePickerId: String? = null

    override val unclickableMask: View
        get() = vb.unclickableMask

    override val buttonsLinearLayout: LinearLayout?
        get() = vb.llAdditionalButtons

    private val result = HashMap<String, List<String>?>()

    private var isContinueClicked = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        childFragmentManager.setFragmentResultListener(DatePickerDialog.PICKER_DIALOG_RESULT, this, this)
    }

    override fun renderScreenState(state: ScreenState?) {
        super.renderScreenState(state)
        vb.tvTitle.gone()
        vb.tvDescription.gone()
        state?.run {
            title?.let { vb.tvTitle.apply {
                text = it
                visible()
            }}
            description?.let { vb.tvDescription.apply {
                text = it
                visible()
            }}
        }
    }


    override fun inflateViewBinging(): NurProcessFlowFragmentInputFormBinding {
        return NurProcessFlowFragmentInputFormBinding.inflate(layoutInflater)
    }

    override fun setupViews(): Unit = with(vb) {}

    override fun onButtonClick(buttonsInfo: FlowButton) {
        selectedButtonId = buttonsInfo.buttonId

        when (buttonsInfo.buttonId) {
            SUBMIT-> setFragmentResultAndClose()
            else -> {
                getProcessFlowHolder().commit(ProcessFlowCommit.OnButtonClick(buttonsInfo))
            }
        }
    }

    fun setAdditionalFetchedOptions(formId: String, options: List<Option>) {
        val showBS = options.isEmpty() || options.size > 1
        setOptionsForDropDownField(fieldId = formId, newOptions = options, showBS = showBS)
    }

    override fun setScreenData(data: ProcessFlowScreenData?) {
        super.setScreenData(data)
        result.clear()
        data?.allowedAnswer?.filterIsInstance<InputForm>()?.firstOrNull()?.let {
            currentFormId = it.formId
            setupInputForm(it)
        }
    }

    private fun setupInputForm(inputForm: InputForm) {
        val container = LinearLayout(context).apply {
            layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT)
            orientation = LinearLayout.VERTICAL
        }
        inputForm.formItems?.forEach {
            val view = when (it.formItem) {
                is FlowInputField -> createInputField(it.formItem).apply {
                    changeInputPositionToStart()
                }
                is GroupButtonFormItem -> createButtonGroup(it.formItem)
                is DropDownFieldInfo -> createDropDownField(it.formItem)
                is DatePickerFieldInfo -> createDatePickerField(it.formItem)
                is LabelFormItem -> createLabelFormItem(it.formItem)
                is PairFieldItem -> createPairFieldItem(it.formItem)
                is DisplayOnlyFieldItem -> createDisplayOnlyFieldItem(it.formItem)
                else -> null
            }
            view?.let { container.addView(it) }
        }
        vb.formContainer.removeAllViews()
        vb.formContainer.addView(container)
    }

    private fun createInputField(inputField: FlowInputField): BaseInputView {
        result[inputField.fieldId] = null
        return InputFieldCreator.create(requireContext(), inputField, { values, isValid ->
            result[inputField.fieldId] = if (isValid) values else null
        }).apply {
            setupOnGetFocusAction { vb.scroll.smoothScrollTo(0, (this.bottom + scrollOffset16px)) }
        }
    }


    private fun createButtonGroup(groupInfo: GroupButtonFormItem): InputFormGroupButtons {
        result[groupInfo.fieldId] = null
        return GroupButtonsCreator.create(requireContext(), groupInfo, onLinkClick = ::onLinkClick, onSelectedChanged = { values, isValid ->
            result[groupInfo.fieldId] = if (isValid) values else null
        })
    }

    private fun createLabelFormItem(labelFormItem: LabelFormItem): View {
        return  LabelFormItemCreator.create(requireContext(), labelFormItem)
    }

    private fun createPairFieldItem(pairFieldItem: PairFieldItem): View {
        return  PairFieldItemCreator.create(requireContext(), pairFieldItem)
    }

    private fun createDisplayOnlyFieldItem(displayOnlyFieldItem: DisplayOnlyFieldItem): View {
        return DisplayOnlyFieldItemCreator.create(requireContext(), displayOnlyFieldItem, ::onFieldClick)
    }

    private fun createDropDownField(dropDownList: DropDownFieldInfo): View {
        result[dropDownList.fieldId] = null
        if (dropDownList.isNeedToFetchOptions == true) {
            optionsRelations.add(OptionFieldParentRelation(currentFieldId = dropDownList.fieldId, parentId = dropDownList.parentFieldId))
        }
        return DropDownFieldCreator.create(requireContext(), dropDownList, { values, isValid ->
            result[dropDownList.fieldId] = if (isValid) values else null
            onDropDownListItemSelectionChanged(dropDownList.fieldId)
        }, ::onRequestOptionsForField)
    }

    private fun createDatePickerField(datePickerFieldInfo: DatePickerFieldInfo): View {
        result[datePickerFieldInfo.fieldId] = null
        return DatePickerFieldCreator.create(requireContext(), datePickerFieldInfo) { values, isValid ->
            result[datePickerFieldInfo.fieldId] = if (isValid) values else null
        }.apply {
            setOnClickListener {
                this.clearError()
                currentOpenedDatePickerId = datePickerFieldInfo.fieldId
                DatePickerDialog.create(
                    getString(R.string.nur_process_flow_next),
                    datePickerFieldInfo.label ?: "",
                    startLimitDate = datePickerFieldInfo.startDateLimit?.let { Calendar.getInstance().apply { timeInMillis = it } },
                    endLimitDate = datePickerFieldInfo.endDateLimit?.let { Calendar.getInstance().apply { timeInMillis = it } }
                ).show(childFragmentManager, null)
            }
        }
    }

    private fun setFragmentResultAndClose() {
        if (!validateInput()) return
        isContinueClicked = true
        requireContext().hideKeyboard()
        getProcessFlowHolder().commit(ProcessFlowCommit.CommitContentFormResponseId(currentFormId, collectResult()))
    }

    private fun onFieldClick(fieldItem: DisplayOnlyFieldItem) {
        getProcessFlowHolder().commit(ProcessFlowCommit.OnButtonClick(FlowButton(fieldItem.fieldId)))
    }

    override fun handleShowLoading(isLoading: Boolean): Boolean {
        if (!isContinueClicked) return false
        isContinueClicked = isLoading
        vb.unclickableMask.isVisible = isLoading
        vb.llAdditionalButtons.isVisible = !isLoading
        return true
    }

    private fun collectResult(): List<Content> {
        val resultValues = mutableListOf<EnteredValue>()
        result.forEach {
            resultValues.add(EnteredValue(it.key, it.value))
        }
        return listOf(Content(FormResponse(resultValues), ContentTypes.INPUT_FORM_DATA))
    }

    private fun validateInput(): Boolean {
        var isValid = true
        result.forEach {
            if (it.value == null) {
                isValid = false
                val view = vb.formContainer.findViewWithTag<View>(it.key)
                when (view) {
                    is BaseInputView -> view.setupFieldAsError(R.string.nur_process_flow_invalid_input)
                    is DropDownInputField -> view.setupAsError()
                    is InputFormGroupButtons -> view.setupAsError()
                    is DatePickerInputField -> view.setupAsError()
                    else -> Toast.makeText(requireContext(), R.string.nur_process_flow_invalid_input, Toast.LENGTH_SHORT).show()
                }
            }
        }
        return isValid
    }

    override fun onDestroyView() {
        super.onDestroyView()
        requireContext().hideKeyboard()
    }

    private fun onDropDownListItemSelectionChanged(fieldId: String) {
        clearChildFieldsOptions(parentFieldId = fieldId)
    }

    private fun clearChildFieldsOptions(parentFieldId: String) {
        optionsRelations.forEach {
            if (it.parentId == parentFieldId) {
                setOptionsForDropDownField(it.currentFieldId, listOf(), false)
            }
        }
    }

    private fun onRequestOptionsForField(fieldId: String) {
        val parentId = optionsRelations.find { it.currentFieldId == fieldId }?.parentId
        val parentSelectedId = parentId?.let { result[it]?.firstOrNull() }
        when {
            parentId == null -> fetchOptions(fieldId)
            parentSelectedId != null -> fetchOptions(fieldId, parentSelectedId)
        }
    }

    private fun fetchOptions(formId: String, parentSelectedOptionId: String = "") {
        getProcessFlowHolder().commit(ProcessFlowCommit.FetchAdditionalOptionsForDropDown(formId, parentSelectedOptionId))
    }

    private fun setOptionsForDropDownField(fieldId: String, newOptions: List<Option>, showBS: Boolean) {
        vb.root.findViewWithTag<DropDownInputField>(fieldId)?.apply {
            options = newOptions
            if (showBS) showOptionsBS()
        }
    }

    override fun onFragmentResult(requestKey: String, result: Bundle) {
        when (requestKey) {
            DatePickerDialog.PICKER_DIALOG_RESULT -> {
                val calendar = result.getSerializable(DatePickerDialog.ARG_SELECTED_DATE) as Calendar
                currentOpenedDatePickerId?.let {
                    vb.formContainer.findViewWithTag<DatePickerInputField>(it).setDate(calendar.timeInMillis)
                }
            }
        }
    }
}

data class OptionFieldParentRelation(val currentFieldId: String, val parentId: String?)