package com.ahmetc.firebase_example_app.ui.profile

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.ahmetc.firebase_example_app.R
import com.ahmetc.firebase_example_app.data.models.User
import com.ahmetc.firebase_example_app.databinding.ActivityProfileBinding
import com.ahmetc.firebase_example_app.ui.auth.AuthActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso

class ProfileActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private val viewModel: ProfileViewModel by viewModels()
    private lateinit var binding: ActivityProfileBinding
    private var avatarUrl: String? = null

    companion object {
        //image pick code
        private const val IMAGE_PICK_CODE = 1000

        //Permission code
        private const val PERMISSION_CODE = 1001
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setSupportActionBar(binding.toolbar)

        mAuth = FirebaseAuth.getInstance()
        viewModel.loadUser(mAuth)

        viewModel.newName = binding.profileTextName.text
        viewModel.newSurname = binding.profileTextSurname.text
        viewModel.newAvatarUrl = viewModel.user.value?.avatar_url
        viewModel.newPassword = binding.profileTextNewPassword.text

        binding.profileBtnSave.setOnClickListener {
            val newUser = viewModel.user.value?.copy(
                name = binding.profileTextName.text.toString(),
                surname = binding.profileTextSurname.text.toString(),
                avatar_url = avatarUrl
            )
            viewModel.updateUser(newUser)
        }
        binding.profileBtnCancel.setOnClickListener {
            handleUser(viewModel.user.value)
        }

        binding.profileBtnNewPassword.setOnClickListener { viewModel.updatePassword(mAuth) }
        binding.profileBtnNewPasswordCancel.setOnClickListener {
            binding.profileTextNewPassword.setText("")
        }

        binding.profileBtnDelete.setOnClickListener {
            val builder = MaterialAlertDialogBuilder(this)
            builder.setTitle("Dikkat")
            builder.setMessage("Hesabınızın silinmesini onaylıyor musunuz?")

            builder.setNegativeButton("Hayır") { dialog, _ ->
                dialog.dismiss()
            }
            builder.setPositiveButton("Evet") { dialog, _ ->
                viewModel.deleteUser(mAuth)
                dialog.dismiss()
            }
            builder.show()
        }

        binding.profileBtnPickAvatar.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_DENIED
                ) {
                    //permission denied
                    val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                    //show popup to request runtime permission
                    requestPermissions(permissions, PERMISSION_CODE)
                } else {
                    //permission already granted
                    pickImageFromGallery()
                }
            } else {
                //system OS is < Marshmallow
                pickImageFromGallery()
            }
        }

        // Handle View states
        viewModel.user.observe(this, {
            it?.let { handleUser(it) }
        })
        viewModel.newPasswordViewState.observe(this, {
            it?.let { handleNewPassword(it) }
        })
        viewModel.deleteAccViewState.observe(this, {
            it?.let { handleDeleteAcc(it) }
        })
        viewModel.userViewState.observe(this, {
            it?.let { handleUserViewState(it) }
        })
        viewModel.avatarViewState.observe(this, {
            it?.let { handleAvatarState(it) }
        })
    }

    private fun handleUser(user: User?) {
        binding.profileTextMail.setText(user?.email)
        binding.profileTextName.setText(user?.name)
        binding.profileTextSurname.setText(user?.surname)
        if (user?.avatar_url.isNullOrEmpty()) {
            binding.profileAvatar.setImageResource(R.drawable.avatar)
        } else {
            Picasso.get()
                .load(user?.avatar_url)
                .error(R.drawable.error)
                .placeholder(R.drawable.loading)
                .into(binding.profileAvatar)
        }
    }

    private fun handleDeleteAcc(viewState: ProfileViewState) {
        if (viewState.isFail) {
            Toast.makeText(this, viewState.errorMsg, Toast.LENGTH_LONG).show()
        } else if (viewState.isSuccess) {
            Toast.makeText(this, "Hesabınız başarıyla silindi!", Toast.LENGTH_LONG).show()
            startActivity(Intent(this, AuthActivity::class.java))
            finish()
        }
    }

    private fun handleNewPassword(viewState: ProfileViewState) {
        if (viewState.isFail) {
            Toast.makeText(this, viewState.errorMsg, Toast.LENGTH_LONG).show()
        } else if (viewState.isSuccess) {
            binding.profileTextNewPassword.setText("")
            Toast.makeText(this, "Parolanız başarıyla değiştirildi", Toast.LENGTH_LONG).show()
        }
    }

    private fun handleUserViewState(viewState: ProfileViewState) {
        if (viewState.isFail) {
            Toast.makeText(this, viewState.errorMsg, Toast.LENGTH_LONG).show()
        } else if (viewState.isSuccess) {
            Toast.makeText(this, "Profiliniz başarıyla güncellendi", Toast.LENGTH_LONG).show()
        }
    }

    private fun handleAvatarState(viewState: ProfileViewState) {
        if (viewState.isFail) {
            Toast.makeText(this, viewState.errorMsg, Toast.LENGTH_LONG).show()
        }
        else if (viewState.isSuccess) {

            Toast.makeText(this, "Avatar başarıyla güncellendi", Toast.LENGTH_LONG).show()
        }
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE){
            val avatarUri = data?.data
            if (avatarUri != null) {
                viewModel.uploadAvatar(avatarUri, mAuth)
                binding.profileAvatar.setImageURI(avatarUri)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.profile_toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.profileToolbarSignOut -> {
            mAuth.signOut()
            startActivity(Intent(this, AuthActivity::class.java))
            finish()
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED
                ) {
                    pickImageFromGallery()
                } else {
                    Toast.makeText(this, "Erişim Engelli", Toast.LENGTH_SHORT).show()
                    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
                }
            }
        }
    }
}