package kg.nurtelecom.processflow.ui.camera.confirmation

import android.widget.ImageView
import androidx.core.os.bundleOf
import kg.nurtelecom.processflow.R

class BackPassportPhotoConfirmation : PhotoConfirmationFragment() {

    override fun setupViews() {
        super.setupViews()
        vb.tvConfirmTitle.setText(R.string.nur_process_flow_photo_confirmation_passport_back)
    }

    companion object {
        fun create(filePath: String?, scaleType: ImageView.ScaleType? = null): PhotoConfirmationFragment {
            return BackPassportPhotoConfirmation().apply {
                arguments = bundleOf(
                    ARG_FILE_PATH to filePath,
                    ARG_FILE_SCALE_TYPE to scaleType
                )
            }
        }
    }
}