package com.ahmetc.firebase_example_app.ui.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ahmetc.firebase_example_app.data.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ProfileViewModel: ViewModel() {
    private val db = Firebase.firestore
    val user: MutableLiveData<User> = MutableLiveData()

    fun loadUser(firebaseAuth: FirebaseAuth) {
        getUserFromDb(firebaseAuth)
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
}