package com.bangkit.compends.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bangkit.compends.data.preference.UserPreference
import com.bangkit.compends.data.preference.dataStore
import com.bangkit.compends.data.response.LoginRequest
import com.bangkit.compends.databinding.ActivityLoginBinding
import com.bangkit.compends.viewmodel.UserViewModel
import com.bangkit.compends.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pref = UserPreference.getInstance(application.dataStore)
        userViewModel = ViewModelProvider(this, ViewModelFactory(pref))[UserViewModel::class.java]

        checkTokenAndNavigate()

        binding.loginBtn.setOnClickListener {
            val email = binding.edEmail.text.toString().trim()
            val password = binding.edPassword.text.toString().trim()

            if (!binding.termsCheckbox.isChecked) {
                Toast.makeText(this, "Please check the box first before login", Toast.LENGTH_SHORT).show()
            } else {
                if (email.isEmpty() && password.isEmpty()) {
                    Toast.makeText(this, "All fields required", Toast.LENGTH_SHORT).show()
                } else {
                    userViewModel.login(
                        request = LoginRequest(email = email, password = password),
                        onSuccess = {
                            Toast.makeText(this, "Login berhasil", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, PredictionActivity::class.java))
                            finish()
                        },
                        onFailure = { message ->
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }

        binding.registerBtn.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun checkTokenAndNavigate() {
        val pref = UserPreference.getInstance(applicationContext.dataStore)

        lifecycleScope.launch {
            pref.getUser().collect { token ->
                if (!token.isNullOrEmpty()) {
                    startActivity(Intent(this@LoginActivity, ListEmployeeActivity::class.java))
                    finish()
                }
            }
        }
    }
}