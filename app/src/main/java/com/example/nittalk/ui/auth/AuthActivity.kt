package com.example.nittalk.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.nittalk.databinding.ActivityAuthBinding
import com.example.nittalk.ui.MainActivity
import com.google.firebase.messaging.FirebaseMessaging
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
                FirebaseMessaging.getInstance().token.addOnCompleteListener {
                    if (it.isComplete) {
                        val token = it.result.toString()
//                        Toast.makeText(this, token, Toast.LENGTH_SHORT).show()
//                        FirebaseUtil().updateToken(this, token)
                        authViewModel.updateDeviceToken(token)
                    }
                }

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