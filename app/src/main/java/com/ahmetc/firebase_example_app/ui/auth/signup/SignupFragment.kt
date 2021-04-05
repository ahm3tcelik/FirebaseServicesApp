package com.ahmetc.firebase_example_app.ui.auth.signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.ahmetc.firebase_example_app.R
import com.ahmetc.firebase_example_app.databinding.FragmentSignupBinding
import com.ahmetc.firebase_example_app.ui.auth.utils.AuthFragments

class SignUpFragment(private val viewPager: ViewPager2?) : Fragment(R.layout.fragment_signup) {
    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SignUpViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.signUpBtnSignUp.setOnClickListener {
            val result = viewModel.onBtnSignUpClick()
            if (result != null) { // FAIL
                Toast.makeText(context, result, Toast.LENGTH_LONG).show()
            }
            else { // SUCCESS

            }
        }

        binding.signUpToSignIn.setOnClickListener {
            viewPager?.setCurrentItem(AuthFragments.SIGN_IN.ordinal, true)
        }

        viewModel.email = binding.signUpTextMail.text
        viewModel.name = binding.signUpTextName.text
        viewModel.surname = binding.signUpTextSurname.text
        viewModel.password = binding.signUpTextPassword.text
        viewModel.passwordConfirm = binding.signUpTextPasswordConfirm.text

    }
}