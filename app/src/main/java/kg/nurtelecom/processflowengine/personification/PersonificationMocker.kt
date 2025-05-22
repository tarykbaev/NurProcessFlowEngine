package kg.nurtelecom.processflowengine.personification

import java.util.Date

object PersonificationMocker {

    val mock = mutableMapOf<String, String>().apply {

        put(
            "start", """
                {
                   "process_id":"werq-rqwew-rwer-fser",
                   "process_status":"RUNNING",
                   "screen_code":"STATUS_INFO",
                   "screen_state":{
                      "status":"COMPLETE",
                      "statusImageUrl": "https://minio.o.kg/lkab/personification/passport.png",
                      "title":"Пройдите персонификацию, подготовьте паспорт",
                      "description":"Для заключения договора(ов) и/или предоставления услуг связи и/или финансовых сервисов и использования приложения необходимо пройти персонификацию и/или идентификацию."
                   },
                   "messages":[],
                   "allowed_answers":[
                      {
                         "responseType":"BUTTON",
                         "responseItem":{
                            "buttonId":"start_ident_foreign",
                            "text":"Гражданин другой страны",
                            "style":"SECONDARY"
                         }
                      },
                      {
                         "responseType":"BUTTON",
                         "responseItem":{
                            "buttonId":"start_ident",
                            "text":"Гражданин Кыргызстана",
                            "style":"ACCENT",
                            "properties":{
                               "isEnabled":true
                            }
                         }
                      }   
                   ]
                }        
            """.trimIndent()
        )

        put(
            "start_ident", """
            {
               "process_id":"werq-rqwew-rwer-fser",
               "process_status":"RUNNING",
               "screen_code":"PASSPORT_FRONT_PHOTO",
               "screen_state":{},
               "messages":[],
               "allowed_answers":[
                  {
                     "responseType":"BUTTON",
                     "responseItem":{
                        "buttonId":"PASSPORT_FRONT_PHOTO2",
                        "text":"далее",
                        "style":"ACCENT"
                     }
                  }   
               ]
            }
        """.trimIndent()
        )

        put(
            "PASSPORT_FRONT_PHOTO2", """
            {
               "process_id":"werq-rqwew-rwer-fser",
               "process_status":"RUNNING",
               "screen_code":"PASSPORT_BACK_PHOTO",
               "messages":[],
               "allowed_answers":[
                  {
                     "responseType":"BUTTON",
                     "responseItem":{
                        "buttonId":"PASSPORT_BACK_PHOTO",
                        "text":"Далее",
                        "style":"ACCENT"
                     }
                  } 
               ]
            }
        """.trimIndent()
        )

        put(
            "PASSPORT_BACK_PHOTO", """
            {
               "process_id":"werq-rqwew-rwer-fser",
               "process_status":"RUNNING",
               "screen_code":"INPUT_FORM",
               "screen_state":{
                    "app_bar_text":"Passport data"
               },
               "messages":[],
               "allowed_answers":[
                  {
                    "responseType":"BUTTON",
                    "responseItem":{
                        "style":"SECONDARY",
                        "properties":null,
                        "buttonId":"PASSPORT_MANUAL_INPUT",
                        "text":"Ввести данные вручную"
                    }
                  },
                  {
                    "responseType":"INPUT_FORM",
                    "responseItem":{
                        "formId":"passport_data_confirm",
                        "title": "Подтвердите ваши паспортные данные",
                        "formItems":[
                            {
                                "formItemType":"PAIR_FIELD",
                                "formItem":{
                                    "fieldId":"PAIR_FIELD_1",
                                    "startText":"ФИО",
                                    "endText":"Асаналиев Асан Асаналиевич"
                                }
                            },
                            {
                                "formItemType":"PAIR_FIELD",
                                "formItem":{
                                    "fieldId":"PAIR_FIELD_2",
                                    "startText":"Дата рождения",
                                    "endText":"01.01.1990"
                                }
                            },
                            {
                                "formItemType":"PAIR_FIELD",
                                "formItem":{
                                    "fieldId":"PAIR_FIELD_3",
                                    "startText":"ИНН",
                                    "endText":"2261119970000"
                                }
                            },
                            {
                                "formItemType":"PAIR_FIELD",
                                "formItem":{
                                    "fieldId":"PAIR_FIELD_4",
                                    "startText":"Паспорт",
                                    "endText":"ID1234567 выдан МКК-120"
                                }
                            }
                        ]
                    }
                  }
               ]
            }
        """.trimIndent()
        )

        put(
            "passport_data_confirm", """
            {
               "process_id":"werq-rqwew-rwer-fser",
               "process_status":"RUNNING",
               "screen_code":"SELFIE_PHOTO",
               "screen_state":{},
               "messages":[],
               "allowed_answers":[
                  {
                     "responseType":"BUTTON",
                     "responseItem":{
                        "buttonId":"SELFIE_PHOTO",
                        "text":"Далее",
                        "style":"ACCENT"
                     }
                  }
               ]
            }
        """.trimIndent()
        )


        put(
            "SELFIE_PHOTO", """
            {
               "process_id":"werq-rqwew-rwer-fser",
               "process_status":"RUNNING",
               "screen_code":"STATUS_INFO",
               "screen_state":{
                  "status":"IN_PROCESS",
                  "title":"Проверяем Ваши данные",
                  "description":"Это займёт всего пару минут. Пожалуйста, дождитесь завершения — не закрывайте страницу."
               },
               "messages":[],
               "allowed_answers":[
                  {
                     "responseType":"RETRY",
                     "responseItem":{
                        "id":"RETRY_1",
                        "properties": {
                            "showLoader":false,
                            "enableAt":${Date().time + 2000}
                        }
                     }
                  }  
               ]
            }
        """.trimIndent()
        )

        put(
            "FLOW_STATE", """
            {
               "process_id":"werq-rqwew-rwer-fser",
               "process_status":"RUNNING",
               "screen_code":"STATUS_INFO",
               "screen_state":{
                  "status":"COMPLETE",
                  "title":"Персонификация пройдена успешно!",
                  "description":"Обратите внимание! Если возникнут проблемы с интернет-соединением, рекомендуем перезагрузить телефон, чтобы восстановить связь"
               },
               "messages":[],
               "allowed_answers":[
                  {
                     "responseType":"BUTTON",
                     "responseItem":{
                        "buttonId":"EXIT_NAVIGATE_TO_WALLET_MAIN",
                        "text":"Войти в Мой О!",
                        "style":"ACCENT",
                        "properties":{
                           "isEnabled":true
                        }
                     }
                  }   
               ]
            }
        """.trimIndent()
        )


        put(
            "start_ident_foreign", """
            {
               "process_id":"werq-rqwew-rwer-fser",
               "process_status":"RUNNING",
               "screen_code":"FOREIGN_PASSPORT_PHOTO",
               "screen_state":{},
               "messages":[],
               "allowed_answers":[
                  {
                     "responseType":"BUTTON",
                     "responseItem":{
                        "buttonId":"FOREIGN_PASSPORT_PHOTO",
                        "text":"далее",
                        "style":"ACCENT"
                     }
                  }   
               ]
            }
        """.trimIndent()
        )

        put(
            "FOREIGN_PASSPORT_PHOTO", """
            {
               "process_id":"werq-rqwew-rwer-fser",
               "process_status":"RUNNING",
               "screen_code":"INPUT_FORM",
               "screen_state":{
                    "app_bar_text":"Passport data"
               },
               "messages":[],
               "allowed_answers":[
                  {
                    "responseType":"BUTTON",
                    "responseItem":{
                        "style":"SECONDARY",
                        "properties":null,
                        "buttonId":"PASSPORT_MANUAL_INPUT",
                        "text":"Ввести данные вручную"
                    }
                  },
                  {
                    "responseType":"INPUT_FORM",
                    "responseItem":{
                        "formId":"passport_data_confirm",
                        "title": "Подтвердите ваши паспортные данные",
                        "formItems":[
                            {
                                "formItemType":"PAIR_FIELD",
                                "formItem":{
                                    "fieldId":"PAIR_FIELD_1",
                                    "startText":"ФИО",
                                    "endText":"Асаналиев Асан Асаналиевич"
                                }
                            },
                            {
                                "formItemType":"PAIR_FIELD",
                                "formItem":{
                                    "fieldId":"PAIR_FIELD_2",
                                    "startText":"Дата рождения",
                                    "endText":"01.01.1990"
                                }
                            },
                            {
                                "formItemType":"PAIR_FIELD",
                                "formItem":{
                                    "fieldId":"PAIR_FIELD_2",
                                    "startText":"Пол",
                                    "endText":"Муж."
                                }
                            },
                            {
                                "formItemType":"PAIR_FIELD",
                                "formItem":{
                                    "fieldId":"PAIR_FIELD_3",
                                    "startText":"ИНН",
                                    "endText":"2261119970000"
                                }
                            },
                            {
                                "formItemType":"PAIR_FIELD",
                                "formItem":{
                                    "fieldId":"PAIR_FIELD_4",
                                    "startText":"Паспорт",
                                    "endText":"ID1234567 выдан МКК-120"
                                }
                            }
                        ]
                    }
                  }
               ]
            }
        """.trimIndent()
        )


        put(
            "PASSPORT_MANUAL_INPUT", """
            {
               "process_id":"werq-rqwew-rwer-fser",
               "process_status":"RUNNING",
               "screen_code":"INPUT_FORM",
               "screen_state":{
                    "app_bar_text":"Ввести данные вручную"
               },
               "messages":[],
               "allowed_answers":[
                  {
                    "responseType":"INPUT_FORM",
                    "responseItem":{
                        "formId":"passport_data_confirm",
                        "formItems":[
                            {
                                "formItemType":"INPUT_FIELD",
                                "formItem":{
                                    "fieldId":"SURNAME",
                                    "label":"Фамилия",
                                    "placeholder":"Фамилия",
                                    "validations":[
                                        {
                                            "type":"REQUIRED",
                                            "value":"true"
                                        }
                                    ],
                                    "value":"Prefilled value"
                                }
                            },
                            {
                                "formItemType":"INPUT_FIELD",
                                "formItem":{
                                    "fieldId":"NAME",
                                    "label":"Имя",
                                    "placeholder":"Имя",
                                    "validations":[
                                        {
                                            "type":"REQUIRED",
                                            "value":"true"
                                        }
                                    ],
                                    "value":"Prefilled value"
                                }
                            },
                            {
                                "formItemType":"INPUT_FIELD",
                                "formItem":{
                                    "fieldId":"PATRONYMIC",
                                    "label":"Отчество",
                                    "placeholder":"Отчество",
                                    "validations":[
                                        {
                                            "type":"REQUIRED",
                                            "value":"true"
                                        }
                                    ],
                                    "value":"Prefilled value"
                                }
                            },
                            {
                                "formItemType":"DATE_PICKER_FORM_ITEM",
                                "formItem":{
                                    "fieldId":"BIRTHDATE",
                                    "label":"Дата рождения"
                                }
                            },
                            {
                                "formItemType":"INPUT_FIELD",
                                "formItem":{
                                    "fieldId":"INN",
                                    "label":"Персональный номер, состоящий из 14 цифр",
                                    "placeholder":"ИНН",
                                    "inputType":"NUMBER",
                                    "validations":[
                                        {
                                            "type":"REGEX",
                                            "value":"^[0-9]*"
                                        },
                                        {
                                            "type":"REQUIRED",
                                            "value":"true"
                                        }
                                    ],
                                    "value":"123123123"
                                }
                            },
                            {
                                "formItemType":"INPUT_FIELD",
                                "formItem":{
                                    "fieldId":"PASSPORT",
                                    "label":"Номер паспорта",
                                    "placeholder":"Номер паспорта",
                                    "validations":[
                                        {
                                            "type":"REQUIRED",
                                            "value":"true"
                                        }
                                    ],
                                    "value":"ID1234567"
                                }
                            },
                            {
                                "formItemType":"DATE_PICKER_FORM_ITEM",
                                "formItem":{
                                    "fieldId":"PASSPORT_date",
                                    "label":"Дата выдачи"
                                }
                            },
                            {
                                "formItemType":"INPUT_FIELD",
                                "formItem":{
                                    "fieldId":"PASSPORT_ISSUER",
                                    "label":"Кем выдан",
                                    "placeholder":"Кем выдан",
                                    "validations":[
                                        {
                                            "type":"REQUIRED",
                                            "value":"true"
                                        }
                                    ],
                                    "value":"ID1234567"
                                }
                            }
                        ]
                    }
                  }
               ]
            }
        """.trimIndent()
        )

    }

}