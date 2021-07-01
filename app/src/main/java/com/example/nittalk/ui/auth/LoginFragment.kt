package com.example.nittalk.ui.auth

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.nittalk.R
import com.example.nittalk.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {

    private var _binding: FragmentLoginBinding ?= null
    private val binding get() = _binding!!
    private val authViewModel by viewModels<AuthViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentLoginBinding.bind(view)

        authViewModel.progress.observe(viewLifecycleOwner) {
            binding.progressBar.visibility = it
        }

        authViewModel.enable.observe(viewLifecycleOwner) {
            binding.emailEditText.isEnabled = it
            binding.passwordEditText.isEnabled = it
            binding.signupButton.isEnabled = it
            binding.signinButton.isEnabled = it
            binding.forgotPasswordBtn.isEnabled = it
        }

        binding.signupButton.setOnClickListener {
            val action = LoginFragmentDirections.actionLoginFragmentToSignUpFragment()
            findNavController().navigate(action)
        }

        binding.signinButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            if (email.isEmpty()) {
                binding.emailInputLayout.error = "Email Cannot Be Empty"
            } else if (!isEmailValid(email)) {
                binding.emailInputLayout.error = "Email Is Not Valid"
            } else {
                binding.emailInputLayout.error = null
                if (password.isEmpty()) {
                    binding.passwordInputLayout.error = "Password Cannot Be Empty"
                } else if (password.length < 6) {
                    binding.passwordInputLayout.error =
                        "Password Should Be at-least 6 characters long"
                } else {
                    binding.passwordInputLayout.error = null
                    Log.i("Rohit Login", "Starting Main Activity")
                    authViewModel.signIn(email, password, this)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun isEmailValid(text: String?): Boolean {
        return text != null && android.util.Patterns.EMAIL_ADDRESS.matcher(text).matches()
    }

}