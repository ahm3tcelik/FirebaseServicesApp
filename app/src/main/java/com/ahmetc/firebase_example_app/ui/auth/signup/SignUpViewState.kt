package com.ahmetc.firebase_example_app.ui.auth.signup

data class SignUpViewState(
    val isLoading: Boolean = false,
    val isFail: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMsg: String? = null,
)