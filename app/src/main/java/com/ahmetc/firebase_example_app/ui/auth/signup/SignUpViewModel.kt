package com.ahmetc.firebase_example_app.ui.auth.signup

import android.text.Editable
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SignUpViewModel : ViewModel() {

    private val db = Firebase.firestore

    var name: Editable? = null
    var surname: Editable? = null
    var email: Editable? = null
    var password: Editable? = null
    var passwordConfirm: Editable? = null

    val signUpViewState: MutableLiveData<SignUpViewState> = MutableLiveData()

    init {
        signUpViewState.value = SignUpViewState()
    }

    private fun currentSignUpViewState(): SignUpViewState = signUpViewState.value!!

    fun onBtnSignUpClick(firebaseAuth: FirebaseAuth?) {
        if (!formValidate()) {
            return
        }
        if (firebaseAuth != null) {
            handleSignUp(firebaseAuth)
        } else {
            failSignUp("Bir hata meydana geldi")
        }
    }

    private fun formValidate(): Boolean {
        if (email.toString().isEmpty() || password.toString().isEmpty() || surname.toString().isEmpty()
            || passwordConfirm.toString().isEmpty() || name.toString().isEmpty()) {
                    failSignUp("Tüm alanları doldurun")
                    return false
        }
        if (!(password.toString().equals(passwordConfirm.toString()))) {
            failSignUp("Parolalar uyuşmuyor")
            return false
        }
        return true
    }

    private fun handleSignUp(firebaseAuth: FirebaseAuth) {
        val fireUser = firebaseAuth.currentUser

        if (fireUser == null) {
            failSignUp("Bir hata meydana geldi")
            return
        }

        firebaseAuth.createUserWithEmailAndPassword(email.toString(), password.toString())
            .addOnCompleteListener { taskAuth ->
                if (taskAuth.isSuccessful) {
                    saveUserToDb(firebaseAuth)
                } else {
                    failSignUp(taskAuth.exception?.message ?: "Bir hata meydana geldi")
                }
            }
    }

    private fun saveUserToDb(firebaseAuth: FirebaseAuth) {
        db.collection("users")
            .document(firebaseAuth.currentUser!!.uid)
            .set(
                hashMapOf(
                    "name" to name.toString(),
                    "surname" to surname.toString(),
                    "email" to email.toString(),
                    "avatar_url" to ""
                )
            ).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    successSignUp()
                } else {
                    // delete account
                    firebaseAuth.currentUser!!.delete()
                    failSignUp(task.exception?.message ?: "Bir hata meydana geldi")
                }
            }
    }

    private fun successSignUp() {
        signUpViewState.value = currentSignUpViewState().copy(
            isFail = false, isSuccess = true, errorMsg = ""
        )
    }

    private fun failSignUp(message: String) {
        signUpViewState.value = currentSignUpViewState().copy(
            isFail = true, isSuccess = false, errorMsg = message
        )
    }
}