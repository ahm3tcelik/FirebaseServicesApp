package com.ahmetc.firebase_example_app.ui.auth.signin

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.ahmetc.firebase_example_app.R
import com.ahmetc.firebase_example_app.databinding.DialogResetPasswordBinding
import com.ahmetc.firebase_example_app.databinding.FragmentSigninBinding
import com.ahmetc.firebase_example_app.ui.auth.utils.AuthFragments
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class SignInFragment(private val viewPager: ViewPager2?) : Fragment(R.layout.fragment_signin) {

    private var _binding: FragmentSigninBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SignInViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSigninBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.signInBtnSignIn.setOnClickListener {
            val result = viewModel.onBtnSignInClick()
            if (result != null) {
                Toast.makeText(context, result, Toast.LENGTH_LONG).show()
            }
        }

        binding.signInToSignUp.setOnClickListener {
            viewPager?.setCurrentItem(
                AuthFragments.SIGN_UP.ordinal,
                true
            )
        }

        binding.signInTextForgotPw.setOnClickListener {
            val builder = MaterialAlertDialogBuilder(requireContext())
            builder.setTitle("Reset Password")

            val dialogBinding = DialogResetPasswordBinding.inflate(layoutInflater)
            builder.setView(dialogBinding.root)

            builder.setNegativeButton("Vazgeç") { dialog, _ ->
                dialog.dismiss()
            }
            builder.setPositiveButton("Gönder") { dialog, _ ->
                val dialogBinding = DialogResetPasswordBinding.inflate(layoutInflater)
                Toast.makeText(context, dialogBinding.dialogResetTextMail.text, Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            builder.show()
        }

        viewModel.email = binding.signInTextMail.text
        viewModel.password = binding.signInTextPassword.text

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}