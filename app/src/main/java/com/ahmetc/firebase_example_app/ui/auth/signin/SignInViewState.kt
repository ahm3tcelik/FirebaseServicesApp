package com.ahmetc.firebase_example_app.ui.auth.signin

data class SignInViewState(
    val isLoading: Boolean = false,
    val isFail: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMsg: String? = null,
)