package com.wedj.tv
@Deprecated("USE FROM BASE PACKAGE")
open class BaseUseCase {
    val TAG = javaClass.simpleName
    @Deprecated("USE FROM BASE PACKAGE")
    sealed class ResponseType {
        object COMMON : ResponseType()
        object LOGIN : ResponseType()
        object FORGOT_PASSWORD : ResponseType()
        object REGISTER : ResponseType()
        object CREATE_USER : ResponseType()
        object UPDATE_USER : ResponseType()
        object GET_USER_DETAIL : ResponseType()
        object UPLOAD_USER_PROFILE : ResponseType()
        object GET_USERS : ResponseType()
        object GET_RESTAURANTS : ResponseType()
        object UPDATE_USER_STATUS : ResponseType()
        object CREATE_CHECKLIST : ResponseType()
        object GET_CHECKLIST : ResponseType()
        object GET_ASSIGN_CHECKLIST : ResponseType()

    }
}