package com.ahmetc.firebase_example_app.ui.auth.signin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.viewpager2.widget.ViewPager2
import com.ahmetc.firebase_example_app.R
import com.ahmetc.firebase_example_app.databinding.DialogResetPasswordBinding
import com.ahmetc.firebase_example_app.databinding.FragmentSigninBinding
import com.ahmetc.firebase_example_app.ui.auth.utils.AuthFragments
import com.ahmetc.firebase_example_app.ui.profile.ProfileActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext

class SignInFragment(private val viewPager: ViewPager2?) : Fragment(R.layout.fragment_signin) {

    private var _binding: FragmentSigninBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SignInViewModel by viewModels()
    private lateinit var mAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSigninBinding.inflate(inflater, container, false)
        mAuth = FirebaseAuth.getInstance()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.signInViewState.observe(viewLifecycleOwner, {
            it?.let { renderSignIn(it) }
        })

        viewModel.forgotPwViewState.observe(viewLifecycleOwner, {
            it?.let { renderForgotPw(it) }
        })

        binding.signInBtnSignIn.setOnClickListener { viewModel.onBtnSignInClick(mAuth) }

        binding.signInToSignUp.setOnClickListener {
            viewPager?.setCurrentItem(
                AuthFragments.SIGN_UP.ordinal,
                true
            )
        }

        binding.signInTextForgotPw.setOnClickListener {
            val builder = MaterialAlertDialogBuilder(requireContext())
            builder.setTitle("Parolanı Sıfırla")

            val dialogBinding = DialogResetPasswordBinding.inflate(layoutInflater)
            viewModel.forgotPwMail = dialogBinding.dialogResetTextMail.text
            builder.setView(dialogBinding.root)

            builder.setNegativeButton("Vazgeç") { dialog, _ ->
                dialog.dismiss()
            }
            builder.setPositiveButton("Gönder") { dialog, _ ->
                viewModel.onForgotPwClick(mAuth)
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

    private fun renderForgotPw(viewState: SignInViewState) {
        if (viewState.isFail) {
            Toast.makeText(context, viewState.errorMsg, Toast.LENGTH_SHORT).show()
        }
        if (viewState.isSuccess) {
            Toast.makeText(context, "Parola sıfırlama e-maili başarıyla gönderildi!",
                Toast.LENGTH_LONG).show()
        }
    }

    private fun renderSignIn(viewState: SignInViewState) {
        if (viewState.isFail) {
            Toast.makeText(context, viewState.errorMsg, Toast.LENGTH_SHORT).show()
        }
        if (viewState.isSuccess) {
            val intent = Intent(context, ProfileActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }
    }

}