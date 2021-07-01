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
            binding.emailEditText.isEnabled = it
            binding.passwordEditText.isEnabled = it
            binding.rePasswordEditText.isEnabled = it
        }

        binding.signinButton.setOnClickListener {
            val action = SignUpFragmentDirections.actionSignUpFragmentToLoginFragment()
            findNavController().navigate(action)
        }

        binding.backBtn.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.signupButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            val passwordCheck = binding.rePasswordEditText.text.toString()

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
                    if (password == passwordCheck) {
                        binding.passwordInputLayout.error = null
                        authViewModel.createUser(email, password, requireActivity())
                    } else {
                        binding.rePasswordInputLayout.error = "Password Doesn't Match"
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
