package com.bangkit.compends.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.bangkit.compends.databinding.FragmentDialogBinding
import java.text.DecimalFormat

class CustomDialogFragment(
    context: Context,
    private val age: Int,
    private val workExperience: Int,
    private val educationLevel: String,
    private val jobTitle: String,
    private val predictedSalary: Int
) : Dialog(context) {

    private lateinit var binding: FragmentDialogBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = FragmentDialogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val numberFormat = DecimalFormat("###,###,###")
        val salary = numberFormat.format(predictedSalary)

        binding.ageTextView.text = "Age: $age"
        binding.workExperienceTextView.text = "Work Experience: $workExperience years"
        binding.educationLevelTextView.text = "Education Level: $educationLevel"
        binding.jobTitleTextView.text = "Job Title: $jobTitle"
        binding.predictedSalaryTextView.text = "Predicted Salary: Rp $salary"

        binding.closeButton.setOnClickListener {
            dismiss()
        }
    }
}
