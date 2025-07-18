package kg.nurtelecom.processflow.item_creator

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import androidx.core.text.parseAsHtml
import com.design2.chili2.extensions.setIsSurfaceClickable
import com.design2.chili2.extensions.setOnSingleClickListener
import com.design2.chili2.view.cells.BaseCellView
import kg.nurtelecom.processflow.R
import kg.nurtelecom.processflow.model.input_form.DisplayOnlyFieldItem
import com.design2.chili2.R as Chilli_R

object DisplayOnlyFieldItemCreator {

    fun create(
        context: Context,
        displayOnlyFieldItem: DisplayOnlyFieldItem,
        onClick: (item: DisplayOnlyFieldItem) -> Unit
    ): View {
        val padding0 = context.resources.getDimensionPixelSize(Chilli_R.dimen.padding_0dp)
        val padding4 = context.resources.getDimensionPixelSize(Chilli_R.dimen.padding_4dp)
        val padding8 = context.resources.getDimensionPixelSize(Chilli_R.dimen.padding_8dp)
        val padding16 = context.resources.getDimensionPixelSize(Chilli_R.dimen.padding_16dp)
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply { setMargins(padding16, padding8, padding16, padding8) }

        return BaseCellView(context, null, 0, Chilli_R.style.Chili_InputViewStyle).apply {
            tag = displayOnlyFieldItem.fieldId
            setTitleTextAppearance(Chilli_R.style.Chili_H7_Primary_Bold)
            setPadding(padding0, padding0, padding0, padding4)
            updateTitleMargin(padding16, padding16, padding16, padding8)
            setTitleMaxLines(Int.MAX_VALUE)
            displayOnlyFieldItem.label?.let { setTitle(it) }

            setSubtitleTextAppearance(Chilli_R.style.Chili_H7_Primary)
            setSubtitleMaxLines(Int.MAX_VALUE)
            displayOnlyFieldItem.value?.let { setSubtitle(it) }
            if (displayOnlyFieldItem.isDescriptionHtml == true) {
                displayOnlyFieldItem.description?.let { setSubtitle(it.parseAsHtml()) }
            } else displayOnlyFieldItem.description?.let { setSubtitle(it) }
            setBackgroundResource(R.drawable.ic_label_cell_rounded_bg)
            this.layoutParams = layoutParams
            setDividerVisibility(false)
            if (displayOnlyFieldItem.isClickable == true) {
                setIsChevronVisible(true)
                setIsSurfaceClickable(true)
                setOnSingleClickListener { onClick(displayOnlyFieldItem) }
            } else {
                setIsChevronVisible(false)
                setIsSurfaceClickable(false)
            }
        }
    }
}