package com.ahmetc.firebase_example_app.ui.profile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import com.ahmetc.firebase_example_app.R
import com.ahmetc.firebase_example_app.data.models.User
import com.ahmetc.firebase_example_app.databinding.ActivityProfileBinding
import com.ahmetc.firebase_example_app.ui.auth.AuthActivity
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso

class ProfileActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private val viewModel: ProfileViewModel by viewModels()
    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setSupportActionBar(binding.toolbar)

        mAuth = FirebaseAuth.getInstance()
        viewModel.loadUser(mAuth)

        viewModel.user.observe(this, {
            it?.let { handleUser(it) }
        })
    }

    private fun handleUser(user: User) {
        binding.profileTextMail.setText(user.email)
        binding.profileTextName.setText(user.name)
        binding.profileTextSurname.setText(user.surname)
        if (user.avatar_url.isNullOrEmpty()) {
            binding.profileAvatar.setImageResource(R.drawable.avatar)
        }
        Picasso.get()
            .load(user.avatar_url)
            .error(R.drawable.error)
            .placeholder(R.drawable.loading)
            .into(binding.profileAvatar)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.profile_toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when(item.itemId) {
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
}