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
            binding.apply {
                emailEditText.isEnabled = it
                passwordEditText.isEnabled = it
                signupButton.isEnabled = it
                signinButton.isEnabled = it
                forgotPasswordBtn.isEnabled = it
            }
        }

        binding.apply {
            signupButton.setOnClickListener {
                val action = LoginFragmentDirections.actionLoginFragmentToSignUpFragment()
                findNavController().navigate(action)
            }
            signinButton.setOnClickListener {
                val email = emailEditText.text.toString()
                val password = passwordEditText.text.toString()

                if (email.isEmpty()) {
                    emailInputLayout.error = "Email Cannot Be Empty"
                } else if (!isEmailValid(email)) {
                    emailInputLayout.error = "Email Is Not Valid"
                } else {
                    emailInputLayout.error = null
                    if (password.isEmpty()) {
                        passwordInputLayout.error = "Password Cannot Be Empty"
                    } else if (password.length < 6) {
                        passwordInputLayout.error =
                            "Password Should Be at-least 6 characters long"
                    } else {
                        passwordInputLayout.error = null
                        Log.i("Rohit Login", "Starting Main Activity")
                        authViewModel.signIn(email, password, this@LoginFragment)
                    }
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