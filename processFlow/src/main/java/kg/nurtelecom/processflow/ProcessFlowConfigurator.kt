package kg.nurtelecom.processflow

object ProcessFlowConfigurator {

    var selfieInstructionUrlResolver: (() -> String) = { "https://minio.o.kg/lkab/personification/selfie_passport.png" }
    val foreignSelfieInstructionUrlResolver: (() -> String) = { "https://minio.o.kg/lkab/personification/foreign_selfie.png" }
    var onlySelfieInstructionUrlResolver: (() -> String) = { "https://minio.o.kg/lkab/personification/selfie.png" }
    var simpleSelfieInstructionUrlResolver: (() -> String) = { "https://minio.o.kg/lkab/personification/myID.png" }
    var foreignPassportInstructionUrlResolver: (() -> String) = { "https://minio.o.kg/lkab/personification/foreign_passport.png" }

    var recognizerTimeoutLimit = 20
    var recognizerTimeoutMills = 20000L

    var foreignRecognizerTimeoutLimit = 1
    var foreignRecognizerTimeoutMills = 7000L
}