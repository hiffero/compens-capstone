package com.bangkit.compends.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bangkit.compends.data.preference.UserPreference
import com.bangkit.compends.data.preference.dataStore
import com.bangkit.compends.data.response.RegisterRequest
import com.bangkit.compends.databinding.ActivityRegisterBinding
import com.bangkit.compends.viewmodel.UserViewModel
import com.bangkit.compends.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pref = UserPreference.getInstance(application.dataStore)
        userViewModel = ViewModelProvider(this, ViewModelFactory(pref))[UserViewModel::class.java]

        binding.registerBtn.setOnClickListener {
            val name = binding.edFullname.text.toString()
            val email = binding.edEmail.text.toString().trim()
            val password = binding.edPassword.text.toString().trim()
            val phone = binding.edPhone.text.toString().trim()

            var updatedPhone = phone

            if (updatedPhone.startsWith("0")) {
                updatedPhone = "+62" + updatedPhone.substring(1)
            }

            if (name.isEmpty() && email.isEmpty() && password.isEmpty() && phone.isEmpty()) {
                Toast.makeText(this, "All fields required", Toast.LENGTH_SHORT).show()
            } else {
                userViewModel.register(
                    request = RegisterRequest(
                        email = email,
                        password = password,
                        name = name,
                        phone = updatedPhone
                    ),
                    onSuccess = { message ->
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()

                    },
                    onFailure = { message ->
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }

        checkTokenAndNavigate()

    }

    private fun checkTokenAndNavigate() {
        val pref = UserPreference.getInstance(applicationContext.dataStore)

        lifecycleScope.launch {
            pref.getUser().collect { token ->
                if (!token.isNullOrEmpty()) {
                    startActivity(Intent(this@RegisterActivity, ListEmployeeActivity::class.java))
                    finish()
                }
            }
        }
    }
}