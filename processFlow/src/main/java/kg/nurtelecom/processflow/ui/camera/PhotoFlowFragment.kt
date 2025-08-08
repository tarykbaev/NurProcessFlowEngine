package kg.nurtelecom.processflow.ui.camera

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.View
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector.LENS_FACING_FRONT
import androidx.core.os.bundleOf
import androidx.fragment.app.commit
import kg.nurtelecom.nur_text_recognizer.RecognizedMrz
import kg.nurtelecom.nur_text_recognizer.photo_capture.OverlayType
import kg.nurtelecom.nur_text_recognizer.photo_capture.PhotoRecognizerActivity
import kg.nurtelecom.nur_text_recognizer.photo_capture.RecognizePhotoContract
import kg.nurtelecom.nur_text_recognizer.photo_capture.ScreenLabels
import kg.nurtelecom.nur_text_recognizer.photo_capture.TextRecognizerConfig
import kg.nurtelecom.processflow.ProcessFlowConfigurator
import kg.nurtelecom.processflow.R
import kg.nurtelecom.processflow.base.BaseProcessScreenFragment
import kg.nurtelecom.processflow.base.process.BackPressHandleState
import kg.nurtelecom.processflow.databinding.NurProcessFlowFragmentPhotoFlowBinding
import kg.nurtelecom.processflow.extension.getProcessFlowHolder
import kg.nurtelecom.processflow.extension.positiveButton
import kg.nurtelecom.processflow.extension.showDialog
import kg.nurtelecom.processflow.model.ContentTypes
import kg.nurtelecom.processflow.model.ProcessFlowCommit
import kg.nurtelecom.processflow.ui.camera.confirmation.BackPassportPhotoConfirmation
import kg.nurtelecom.processflow.ui.camera.confirmation.FrontPassportPhotoConfirmation
import kg.nurtelecom.processflow.ui.camera.confirmation.PhotoConfirmationFragment
import kg.nurtelecom.processflow.ui.camera.confirmation.SelfiePhotoConfirmation
import kg.nurtelecom.processflow.ui.camera.confirmation.SelfieWithPassportConfirmation
import kg.nurtelecom.processflow.ui.camera.instruction.BasePhotoInstructionFragment
import kg.nurtelecom.processflow.ui.camera.instruction.photo.ForeignPassportInstructionFragment
import kg.nurtelecom.processflow.ui.camera.instruction.photo.ForeignSelfiePhotoInstructionFragment
import kg.nurtelecom.processflow.ui.camera.instruction.photo.PassportBackInstructionFragment
import kg.nurtelecom.processflow.ui.camera.instruction.photo.PassportFrontInstructionFragment
import kg.nurtelecom.processflow.ui.camera.instruction.photo.SelfieOnlyPhotoInstructionFragment
import kg.nurtelecom.processflow.ui.camera.instruction.photo.SelfiePhotoInstructionFragment
import kg.nurtelecom.processflow.ui.camera.instruction.photo.SimpleSelfiePhotoInstructionFragment

class PhotoFlowFragment : BaseProcessScreenFragment<NurProcessFlowFragmentPhotoFlowBinding>() {

    private val cameraType by lazy { (arguments?.getSerializable(ARG_CAMERA_TYPE) as? CameraType) ?: CameraType.SELFIE }
    private val responseId by lazy { (arguments?.getSerializable(ARG_RESPONSE_ID) as? String) ?: "" }

    private var needRecognition = true
    private var recognizedMrz: RecognizedMrz? = null
    private var overlayType: OverlayType = OverlayType.PASSPORT_OVERLAY
    private var timeoutLimit: Int = ProcessFlowConfigurator.recognizerTimeoutLimit
    private var timeoutMills: Long = ProcessFlowConfigurator.recognizerTimeoutMills
    private var showCameraButtonAfterTimeout: Boolean = false

    override val unclickableMask: View?
        get() = vb.unclickableMask

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        if (it.any { !it.value }) onPermissionDeny()
        else openPhotoCapture()
    }

    private val openSettingResult  = registerForActivityResult(object: ActivityResultContract<Unit, Unit>() {
        override fun createIntent(context: Context, input: Unit): Intent {
            return Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + requireActivity().packageName))
        }
        override fun parseResult(resultCode: Int, intent: Intent?) {}
    }) { checkPermission() }

    private fun checkPermission() {
        requestPermissionLauncher.launch(getRequiredPermissionsByVersion())
    }

    private fun getRequiredPermissionsByVersion() =
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                arrayOf(Manifest.permission.CAMERA)
            }
            else -> {
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            }
        }

    private val textRecognizerContract = registerForActivityResult(RecognizePhotoContract()) {
        if (it == null) return@registerForActivityResult
        val filePath = it.getParcelableExtra<Uri>(PhotoRecognizerActivity.EXTRA_PHOTO_URI)?.path ?: ""
        if (needRecognition) recognizedMrz = it.getSerializableExtra(PhotoRecognizerActivity.EXTRA_MRZ_STRING) as? RecognizedMrz
        onPhotoCaptured(filePath)
    }

    private fun openRecognizer(cameraType: CameraType) {
        val label = getString(
            if(cameraType == CameraType.FOREIGN_PASSPORT) R.string.nur_process_flow_photo_instruction_passport_foreigner
            else R.string.nur_process_flow_photo_capture_passport_back_title
        )

        if (cameraType == CameraType.FOREIGN_PASSPORT) {
            overlayType = OverlayType.FOREIGN_PASSPORT_OVERLAY
            timeoutLimit = ProcessFlowConfigurator.foreignRecognizerTimeoutLimit
            timeoutMills = ProcessFlowConfigurator.foreignRecognizerTimeoutMills
            showCameraButtonAfterTimeout = true
        } else {
            overlayType = OverlayType.PASSPORT_OVERLAY
            timeoutLimit = ProcessFlowConfigurator.recognizerTimeoutLimit
            timeoutMills = ProcessFlowConfigurator.recognizerTimeoutMills
            showCameraButtonAfterTimeout = false
        }

        textRecognizerContract.launch(
            TextRecognizerConfig(
                shouldRecognizeOnRetry = false,
                timeoutLimit = timeoutLimit,
                timeoutMills = timeoutMills,
                timeoutMessage = getString(R.string.nur_process_flow_photo_recognizer_timeout_description),
                false,
                photoCaptureLabels = ScreenLabels(label, description = getString(R.string.nur_process_flow_photo_capture_passport_front_description)),
                recognitionLabels = ScreenLabels(label, description = getString(R.string.nur_process_flow_photo_capture_passport_front_description)),
                overlayType = overlayType,
                hasCustomPhotoConfirmation = true,
                needRecognition = recognizedMrz == null,
                isSimplifiedRecognition = true,
                showCameraButtonAfterTimeout = showCameraButtonAfterTimeout
            )
        )
    }

    fun startPhotoFlow(isRetakingPhoto: Boolean = false) {
        needRecognition = ((cameraType == CameraType.BACK_PASSPORT_WITH_RECOGNIZER || cameraType == CameraType.FOREIGN_PASSPORT) && !isRetakingPhoto)
        checkPermission()
    }


    override fun inflateViewBinging() = NurProcessFlowFragmentPhotoFlowBinding.inflate(layoutInflater)

    override fun setupViews() {
        super.setupViews()
        openPhotoInstruction()
    }

    private fun openPhotoInstruction() {
        val fragment = when (cameraType) {
            CameraType.FRONT_PASSPORT -> PassportFrontInstructionFragment()
            CameraType.FOREIGN_PASSPORT -> ForeignPassportInstructionFragment()
            CameraType.BACK_PASSPORT_WITH_RECOGNIZER -> PassportBackInstructionFragment()
            CameraType.SELFIE -> SelfiePhotoInstructionFragment()
            CameraType.FOREIGN_SELFIE -> ForeignSelfiePhotoInstructionFragment()
            CameraType.SIMPLE_SELFIE_PHOTO -> SimpleSelfiePhotoInstructionFragment()
            CameraType.SELFIE_ONLY_PHOTO -> SelfieOnlyPhotoInstructionFragment()
            CameraType.SIMPLE_CAMERA -> { startPhotoFlow(); return }
        }
        childFragmentManager.commit {
            replace(R.id.container, fragment)
        }
    }

    fun onPhotoCaptured(filePath: String?) {
        val targetFragment = when(cameraType) {
            CameraType.FRONT_PASSPORT -> FrontPassportPhotoConfirmation.create(filePath, getScaleType())
            CameraType.FOREIGN_PASSPORT -> BackPassportPhotoConfirmation.create(filePath, getScaleType())
            CameraType.BACK_PASSPORT_WITH_RECOGNIZER -> BackPassportPhotoConfirmation.create(filePath, getScaleType())
            CameraType.SELFIE, CameraType.FOREIGN_SELFIE -> SelfieWithPassportConfirmation.create(filePath, getScaleType())
            CameraType.SIMPLE_SELFIE_PHOTO -> SelfiePhotoConfirmation.create(filePath, getScaleType())
            else -> PhotoConfirmationFragment.create(filePath, getScaleType())
        }
        childFragmentManager.commit {
            replace(R.id.container, targetFragment, PHOTO_CONFIRM_FRAGMENT_TAG)
            addToBackStack(null)
        }
    }

    private fun getScaleType(): ImageView.ScaleType? {
        return if (cameraType in listOf(CameraType.BACK_PASSPORT_WITH_RECOGNIZER,  CameraType.FRONT_PASSPORT, CameraType.FOREIGN_PASSPORT)) ImageView.ScaleType.CENTER_INSIDE
        else null
    }

    private fun onPermissionDeny() {
        showDialog {
            setMessage(R.string.nur_process_flow_permission_denied)
            positiveButton(android.R.string.ok) {
                openSettingResult.launch(Unit)
            }
        }
    }

    fun onPhotoConfirmed(filePath: String) {
        getProcessFlowHolder().commit(ProcessFlowCommit.OnFlowPhotoCaptured(responseId, filePath, getFileType(), recognizedMrz))
    }

    private fun getFileType(): String {
        return when (cameraType) {
            CameraType.FOREIGN_PASSPORT -> ContentTypes.FOREIGN_PASSPORT_PHOTO
            CameraType.FRONT_PASSPORT -> ContentTypes.PASSPORT_FRONT_PHOTO
            CameraType.BACK_PASSPORT_WITH_RECOGNIZER -> ContentTypes.PASSPORT_BACK_PHOTO
            CameraType.SELFIE, CameraType.FOREIGN_SELFIE -> ContentTypes.SELFIE_PHOTO
            CameraType.SIMPLE_SELFIE_PHOTO -> ContentTypes.SIMPLE_SELFIE_PHOTO
            CameraType.SIMPLE_CAMERA -> ContentTypes.SIMPLE_CAMERA
            CameraType.SELFIE_ONLY_PHOTO -> ContentTypes.SELFIE_ONLY_PHOTO
        }
    }

    private fun openPhotoCapture() {
        if (cameraType == CameraType.BACK_PASSPORT_WITH_RECOGNIZER || cameraType == CameraType.FOREIGN_PASSPORT) {
            openRecognizer(cameraType)
            return
        }
        childFragmentManager.commit {
            replace(R.id.container, PhotoCaptureFragment.create(getCameraSettings()))
            addToBackStack(null)
        }
    }

    private fun getCameraSettings(): CameraSettings {
        return when (cameraType) {
            CameraType.SIMPLE_SELFIE_PHOTO -> CameraSettings(lensFacing = LENS_FACING_FRONT, cameraOverlayType = CameraOverlayType.RECTANGLE_FRAME, description = getString(R.string.nur_process_flow_photo_capture_simple_selfie_description))
            CameraType.SELFIE, CameraType.FOREIGN_SELFIE -> CameraSettings(lensFacing = LENS_FACING_FRONT, cameraOverlayType = CameraOverlayType.RECTANGLE_FRAME, description = getString(R.string.nur_process_flow_photo_capture_selfie_passport_description))
            CameraType.SIMPLE_CAMERA -> CameraSettings(cameraOverlayType = CameraOverlayType.RECTANGLE_FRAME)
            CameraType.FOREIGN_PASSPORT -> CameraSettings(description = getString(R.string.nur_process_flow_photo_capture_passport_front_description), cameraOverlayType = CameraOverlayType.RECTANGLE_FRAME)
            CameraType.SELFIE_ONLY_PHOTO -> CameraSettings(lensFacing = LENS_FACING_FRONT, description = getString(R.string.nur_process_flow_photo_capture_selfie_only_description), cameraOverlayType = CameraOverlayType.RECTANGLE_FRAME)
            else -> CameraSettings(description = getString(R.string.nur_process_flow_photo_capture_passport_front_description), headerText = getString(R.string.nur_process_flow_photo_capture_passport_front_title))
        }
    }

    override fun handleShowLoading(isLoading: Boolean): Boolean {
        (childFragmentManager.findFragmentByTag(PHOTO_CONFIRM_FRAGMENT_TAG) as? PhotoConfirmationFragment)?.let {
            it.setIsLoading(isLoading)
            return true
        }
        return false
    }

    override fun handleBackPress(): BackPressHandleState {
        return when(childFragmentManager.findFragmentById(R.id.container)) {
            is BasePhotoInstructionFragment -> BackPressHandleState.NOT_HANDLE
            is SelfiePhotoInstructionFragment -> BackPressHandleState.NOT_HANDLE
            is ForeignSelfiePhotoInstructionFragment -> BackPressHandleState.NOT_HANDLE
            is SimpleSelfiePhotoInstructionFragment -> BackPressHandleState.NOT_HANDLE
            is SelfieOnlyPhotoInstructionFragment -> BackPressHandleState.NOT_HANDLE
            is PhotoConfirmationFragment -> BackPressHandleState.NOT_HANDLE
            else -> {
                if (cameraType == CameraType.SIMPLE_CAMERA) BackPressHandleState.NOT_HANDLE
                else {
                    childFragmentManager.popBackStack()
                    BackPressHandleState.HANDLED
                }
            }
        }
    }

    companion object {
        const val ARG_CAMERA_TYPE = "ARG_CAMERA_TYPE"
        const val ARG_RESPONSE_ID = "ARG_RESPONSE_ID"

        const val PHOTO_CONFIRM_FRAGMENT_TAG = "PHOTO_CONFIRM_FRAGMENT_TAG"

        fun create(cameraType: CameraType, responseId: String): PhotoFlowFragment {
            return PhotoFlowFragment().apply {
                arguments = bundleOf(
                    ARG_CAMERA_TYPE to cameraType,
                    ARG_RESPONSE_ID to responseId
                )
            }
        }
    }
}

enum class CameraType {
    FRONT_PASSPORT,
    BACK_PASSPORT_WITH_RECOGNIZER,
    SELFIE, FOREIGN_SELFIE,
    SIMPLE_CAMERA,
    SIMPLE_SELFIE_PHOTO,
    FOREIGN_PASSPORT,
    SELFIE_ONLY_PHOTO
}