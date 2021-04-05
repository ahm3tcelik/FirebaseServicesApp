package com.ahmetc.firebase_example_app.ui.auth.signup

import android.text.Editable
import android.widget.Toast
import androidx.lifecycle.ViewModel

class SignUpViewModel: ViewModel() {

    var name: Editable? = null
    var surname: Editable? = null
    var email: Editable? = null
    var password: Editable? = null
    var passwordConfirm: Editable? = null

    fun onBtnSignUpClick(): String? {
        if (email.isNullOrEmpty() || password.isNullOrEmpty() || name.isNullOrEmpty()
            || surname.isNullOrEmpty() || passwordConfirm.isNullOrEmpty()) {
            return "Lütfen tüm alanları doldurun"
        }
        else if (password != passwordConfirm) {
            return "Parolalar birbiriyle uyuşmuyor"
        }
        return null
    }
}