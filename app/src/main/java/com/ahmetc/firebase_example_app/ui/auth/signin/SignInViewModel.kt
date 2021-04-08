package com.ahmetc.firebase_example_app.ui.auth.signin

import android.text.Editable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class SignInViewModel : ViewModel() {

    var email: Editable? = null;
    var password: Editable? = null
    var forgotPwMail: Editable? = null

    val signInViewState: MutableLiveData<SignInViewState> = MutableLiveData()
    val forgotPwViewState: MutableLiveData<SignInViewState> = MutableLiveData()

    init {
        forgotPwViewState.value = SignInViewState()
        signInViewState.value = SignInViewState()
    }

    fun onForgotPwClick(firebaseAuth: FirebaseAuth?) {
        if (firebaseAuth != null) {
            firebaseAuth.sendPasswordResetEmail(forgotPwMail.toString())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        successViewState(forgotPwViewState)
                    }
                    else {
                        failViewState(forgotPwViewState, task.exception?.message ?: "Hata")
                    }
                }
        }
        else {
            failViewState(forgotPwViewState, "Bir hata meydana geldi")
        }
    }

    fun onBtnSignInClick(firebaseAuth: FirebaseAuth?) {

        if (email.isNullOrEmpty() || password.isNullOrEmpty()) {
            failViewState(signInViewState, "Lütfen tüm alanları doldurun")
        } else {

            if (firebaseAuth != null) {
                firebaseAuth
                    .signInWithEmailAndPassword(email.toString(), password.toString())
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // CHECK MAIL VERIFICATION
                            if (firebaseAuth.currentUser?.isEmailVerified != true) {
                                firebaseAuth.currentUser?.sendEmailVerification()
                                failViewState(signInViewState,
                                    "Lütfen hesabınızı doğrulayın. (E-Ppostahesabınızı kontrol edin)")
                            }
                            else { // VERIFIED USER
                                successViewState(signInViewState)
                            }
                        } else {
                            failViewState(signInViewState,
                                task.exception?.message ?: "Giriş başarısız")
                        }
                    }
            }
            else {
               failViewState(signInViewState, "Bir hata meydana geldi")
            }

        }
    }

    private fun successViewState(viewState: MutableLiveData<SignInViewState>) {
        viewState.value = SignInViewState(isFail = false, isSuccess = true, errorMsg = "")
    }

    private fun failViewState(viewState: MutableLiveData<SignInViewState>, msg: String) {
        viewState.value = SignInViewState(isFail = true, isSuccess = false, errorMsg = msg)
    }
}