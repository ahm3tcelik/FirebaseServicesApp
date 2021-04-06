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

    private fun currentSignInViewState(): SignInViewState = signInViewState.value!!
    private fun currentForgotPwViewState(): SignInViewState = signInViewState.value!!

    fun onForgotPwClick(firebaseAuth: FirebaseAuth?) {
        if (firebaseAuth != null) {
            firebaseAuth.sendPasswordResetEmail(forgotPwMail.toString())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        forgotPwViewState.value = currentForgotPwViewState().copy(
                            isSuccess = true,
                            isFail = false,
                            errorMsg = ""
                        )
                    }
                    else {
                        forgotPwViewState.value = currentForgotPwViewState().copy(
                            isSuccess = false,
                            isFail = true,
                            errorMsg = task.exception?.message ?: "Hata!"
                        )
                    }
                }
        }
        else {
            forgotPwViewState.value = currentForgotPwViewState().copy(
                isFail = true,
                isSuccess = false,
                errorMsg = "Bir hata meydana geldi"
            )
        }
    }

    fun onBtnSignInClick(firebaseAuth: FirebaseAuth?) {

        if (email.isNullOrEmpty() || password.isNullOrEmpty()) {
            signInViewState.value = currentSignInViewState().copy(
                isSuccess = false,
                isFail = true,
                errorMsg = "Lütfen tüm alanları doldurun"
            )
        } else {

            if (firebaseAuth != null) {
                firebaseAuth
                    .signInWithEmailAndPassword(email.toString(), password.toString())
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // CHECK MAIL VERIFICATION
                            if (firebaseAuth.currentUser?.isEmailVerified != true) {
                                firebaseAuth.currentUser?.sendEmailVerification()
                                signInViewState.value = currentSignInViewState().copy(
                                    isFail = true,
                                    errorMsg = "Lütfen hesabınızı doğrulayın. (E-Posta hesabınızı kontrol edin)",
                                    isSuccess =  false
                                )
                            }
                            else { // VERIFIED USER
                                signInViewState.value = currentSignInViewState().copy(
                                    isSuccess = true, isFail = false, errorMsg = ""
                                )
                            }
                        } else {
                            signInViewState.value = currentSignInViewState().copy(
                                isSuccess = false, isFail = true,
                                errorMsg = task.exception?.message ?: "Giriş başarısız"
                            )
                        }
                    }
            }
            else {
                signInViewState.value = currentSignInViewState().copy(
                    isSuccess = false, isFail = true, errorMsg = "Bir hata meydana geldi"
                )
            }

        }
    }
}