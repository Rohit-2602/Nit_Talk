package com.example.nittalk.ui.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import com.example.nittalk.ui.MainActivity
import com.example.nittalk.databinding.ActivityAuthBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding
    private val authViewModel by viewModels<AuthViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()
        authViewModel.loginState.observe(this) { state ->
            if (state == true) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            else {
                Toast.makeText(this, "User Not", Toast.LENGTH_SHORT).show()
                Log.i("Auth Activity Login", state.toString())
            }
        }
    }

}