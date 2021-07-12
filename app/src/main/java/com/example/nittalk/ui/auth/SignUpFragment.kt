package com.example.nittalk.ui.auth

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.nittalk.R
import com.example.nittalk.databinding.FragmentSignUpBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpFragment : Fragment(R.layout.fragment_sign_up) {

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!
    private val authViewModel by viewModels<AuthViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSignUpBinding.bind(view)

        authViewModel.progress.observe(viewLifecycleOwner) {
            binding.progressBar.visibility = it
        }

        authViewModel.enable.observe(viewLifecycleOwner) {
            binding.apply {
                emailEditText.isEnabled = it
                passwordEditText.isEnabled = it
                rePasswordEditText.isEnabled = it
            }
        }

        binding.apply {
            signinButton.setOnClickListener {
                val action = SignUpFragmentDirections.actionSignUpFragmentToLoginFragment()
                findNavController().navigate(action)
            }

            backBtn.setOnClickListener {
                findNavController().navigateUp()
            }

            signupButton.setOnClickListener {
                val email = emailEditText.text.toString()
                val password = passwordEditText.text.toString()
                val passwordCheck = rePasswordEditText.text.toString()

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
                        if (password == passwordCheck) {
                            passwordInputLayout.error = null
                            authViewModel.createUser(email, password, requireActivity())
                        } else {
                            rePasswordInputLayout.error = "Password Doesn't Match"
                        }
                    }
                }
            }
        }
    }

    private fun isEmailValid(text: String?): Boolean {
        return text != null && android.util.Patterns.EMAIL_ADDRESS.matcher(text).matches()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
