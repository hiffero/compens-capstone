package com.bangkit.compends.ui

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bangkit.compends.data.preference.UserPreference
import com.bangkit.compends.data.preference.dataStore
import com.bangkit.compends.data.response.PredictRequest
import com.bangkit.compends.databinding.ActivityPredictionBinding
import com.bangkit.compends.viewmodel.PredictViewModel
import com.bangkit.compends.viewmodel.UserViewModel
import com.bangkit.compends.viewmodel.ViewModelFactory
import java.text.NumberFormat
import java.util.Locale

class PredictionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPredictionBinding
    private lateinit var predictViewModel: PredictViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPredictionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pref = UserPreference.getInstance(application.dataStore)
        predictViewModel = ViewModelProvider(this, ViewModelFactory(pref))[PredictViewModel::class.java]

        setupEducationDropdown()
        setupButton()
    }

    private fun setupButton() {
        binding.btnGenerate.setOnClickListener {
            val education = binding.edEducation.text.toString().lowercase()
            val job = binding.edJobTitle.text.toString()

            if (binding.edExperience.text.toString().isEmpty() || binding.edAge.text.toString().isEmpty() || education.isEmpty() || job.isEmpty()){
                Toast.makeText(this, "All fields required", Toast.LENGTH_SHORT).show()
            } else {
                val experience = binding.edExperience.text.toString().trim().toInt()
                val age = binding.edAge.text.toString().trim().toInt()
                predictViewModel.predict(
                    request = PredictRequest(
                        age = age,
                        educationLevel = education,
                        jobTitle = job,
                        workExperience = experience
                    ),
                    onLoading = {

                    },
                    onSuccess = { response ->
                        val formattedSalary = formatToRupiah(response.predictedSalary ?: 0)
                        binding.btnGenerate.text = formattedSalary
                    },
                    onFailure = { message ->
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }

    fun formatToRupiah(amount: Int): String {
        val localeID = Locale("in", "ID")  // Indonesia Locale
        val numberFormat = NumberFormat.getCurrencyInstance(localeID)
        return numberFormat.format(amount)
    }

    private fun setupEducationDropdown() {
        val educationLevels = listOf("Bachelor", "Master", "Doctorate")

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            educationLevels
        )

        val edEducation = binding.edEducation
        edEducation.setAdapter(adapter)

        edEducation.setOnClickListener {
            edEducation.showDropDown()
        }
    }

}