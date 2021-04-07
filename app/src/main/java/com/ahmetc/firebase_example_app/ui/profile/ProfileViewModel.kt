package com.ahmetc.firebase_example_app.ui.profile

import android.text.Editable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ahmetc.firebase_example_app.data.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.File
import java.io.FileInputStream
import java.net.URI


class ProfileViewModel : ViewModel() {
    private val storage = Firebase.storage
    private val db = Firebase.firestore
    val user: MutableLiveData<User> = MutableLiveData()

    var newName: Editable? = null
    var newSurname: Editable? = null
    var newAvatarUrl: String? = null
    var newPassword: Editable? = null

    val deleteAccViewState: MutableLiveData<ProfileViewState> = MutableLiveData()
    val userViewState: MutableLiveData<ProfileViewState> = MutableLiveData()
    val newPasswordViewState: MutableLiveData<ProfileViewState> = MutableLiveData()

    init {
        deleteAccViewState.value = ProfileViewState()
        userViewState.value = ProfileViewState()
        newPasswordViewState.value = ProfileViewState()
    }

    fun loadUser(firebaseAuth: FirebaseAuth) {
        getUserFromDb(firebaseAuth)
    }

    fun uploadAvatar(uri: URI, auth: FirebaseAuth) {
        if (auth.currentUser == null) {
            return
        }
        val file = File(uri.path)
        val stream = FileInputStream(file)
        val uploadTask = storage.reference
            .child("avatars/${auth.currentUser!!.uid}")
            .putStream(stream)

        uploadTask.addOnFailureListener {
            // Handle unsuccessful uploads
        }.addOnSuccessListener { taskSnapshot ->
            taskSnapshot.storage.downloadUrl.addOnCompleteListener { task ->
                if (task.isSuccessful) {

                }
                else {
                    
                }
            }
        }
    }

    fun updateUser(newUser: User?) {
        if (newUser == null) {
            failViewState(userViewState, "Bir hata meydana geldi")
            return
        }

        db.collection("users")
            .document(newUser.uid)
            .set(newUser, SetOptions.merge())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    successViewState(userViewState)
                } else {
                    failViewState(
                        userViewState,
                        task.exception?.message ?: "Bir hata meydana geldi"
                    )
                }
            }
    }

    fun updatePassword(firebaseAuth: FirebaseAuth) {
        if (newPassword.toString().isEmpty()) {
            failViewState(newPasswordViewState, "Bu alan boş bırakılamaz")
            return
        }
        if (firebaseAuth.currentUser == null) {
            failViewState(newPasswordViewState, "Bir hata meydana geldi")
        } else {
            firebaseAuth.currentUser!!.updatePassword(newPassword.toString())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        successViewState(newPasswordViewState)
                    } else {
                        failViewState(
                            newPasswordViewState,
                            task.exception?.message ?: "Bir hata meydana geldi"
                        )
                    }
                }
        }
    }

    fun deleteUser(firebaseAuth: FirebaseAuth) {
        if (firebaseAuth.currentUser == null) {
            failViewState(deleteAccViewState, "Bir hata meydana geldi")
            return
        }
        val userUid = firebaseAuth.currentUser!!.uid
        firebaseAuth.currentUser!!.delete()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    db.collection("users").document(userUid).delete()
                    successViewState(deleteAccViewState)
                } else {
                    failViewState(
                        deleteAccViewState,
                        task.exception?.message ?: "Bir hata meydana geldi"
                    )
                }
            }
    }

    private fun getUserFromDb(firebaseAuth: FirebaseAuth) {
        if (firebaseAuth.currentUser == null) return

        db.collection("users")
            .document(firebaseAuth.currentUser!!.uid)
            .get(Source.DEFAULT).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val dc: DocumentSnapshot = task.result!!
                    val tempUser = User(
                        dc.id,
                        dc["name"].toString(),
                        dc["surname"].toString(),
                        dc["email"].toString(),
                        dc["avatar_url"].toString()
                    )
                    user.value = tempUser
                }
            }
    }


    private fun successViewState(viewState: MutableLiveData<ProfileViewState>) {
        viewState.value = ProfileViewState(isFail = false, isSuccess = true, errorMsg = "")
    }

    private fun failViewState(viewState: MutableLiveData<ProfileViewState>, msg: String) {
        viewState.value = ProfileViewState(isFail = true, isSuccess = false, errorMsg = msg)
    }
}