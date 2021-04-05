package com.ahmetc.firebase_example_app.ui.auth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.ahmetc.firebase_example_app.databinding.ActivityAuthBinding
import com.ahmetc.firebase_example_app.ui.auth.signin.SignInFragment
import com.ahmetc.firebase_example_app.ui.auth.signup.SignUpFragment
import com.google.android.material.tabs.TabLayoutMediator

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val tabNames = arrayListOf("Giriş Yap", "Kayıt Ol")

        val fragments: ArrayList<Fragment> = arrayListOf(
            SignInFragment(binding.authViewPager),
            SignUpFragment(binding.authViewPager)
        )

        val pageAdapter = AuthAdapter(this, fragments)
        binding.authViewPager.adapter = pageAdapter


        TabLayoutMediator(binding.authTabLayout, binding.authViewPager) { tab, position ->
            tab.text = tabNames[position]
        }.attach()
    }
}