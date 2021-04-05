package com.ahmetc.firebase_example_app.ui.auth.signin

import android.text.Editable
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel

class SignInViewModel: ViewModel() {

    var email: Editable? = null;
    var password: Editable? = null;

    fun onForgotPwClick() {
        Toast.makeText(null, "Unuttum", Toast.LENGTH_SHORT).show()
    }

    fun onBtnSignInClick(): String? {
        if (email.isNullOrEmpty() || password.isNullOrEmpty()) {
            return "Lütfen tüm alanları doldurun";
        }
        return null
    }

}