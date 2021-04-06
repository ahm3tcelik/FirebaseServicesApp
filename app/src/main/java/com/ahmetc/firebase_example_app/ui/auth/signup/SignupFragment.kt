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
import com.google.firebase.auth.FirebaseAuth

class SignUpFragment(private val viewPager: ViewPager2?) : Fragment(R.layout.fragment_signup) {

    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SignUpViewModel by viewModels()
    private lateinit var mAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignupBinding.inflate(inflater, container, false)
        mAuth = FirebaseAuth.getInstance()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.signUpViewState.observe(viewLifecycleOwner, {
            it?.let { renderSignUp(it) }
        })

        binding.signUpBtnSignUp.setOnClickListener { viewModel.onBtnSignUpClick(mAuth)}

        binding.signUpToSignIn.setOnClickListener {
            viewPager?.setCurrentItem(AuthFragments.SIGN_IN.ordinal, true)
        }

        viewModel.email = binding.signUpTextMail.text
        viewModel.name = binding.signUpTextName.text
        viewModel.surname = binding.signUpTextSurname.text
        viewModel.password = binding.signUpTextPassword.text
        viewModel.passwordConfirm = binding.signUpTextPasswordConfirm.text
    }

    private fun renderSignUp(viewState: SignUpViewState) {
        if (viewState.isFail) {
            Toast.makeText(context, viewState.errorMsg, Toast.LENGTH_LONG).show()
        }
        else if (viewState.isSuccess) {
            resetForm()
            viewPager?.setCurrentItem(AuthFragments.SIGN_IN.ordinal, true)
            Toast.makeText(context, "Kayıt başarıyla oluşturuldu. E-Mailinizi kontrol edin", Toast.LENGTH_LONG).show()
        }
    }
    private fun resetForm() {
        binding.signUpTextMail.setText("")
        binding.signUpTextName.setText("")
        binding.signUpTextSurname.setText("")
        binding.signUpTextPassword.setText("")
        binding.signUpTextPasswordConfirm.setText("")
    }
}