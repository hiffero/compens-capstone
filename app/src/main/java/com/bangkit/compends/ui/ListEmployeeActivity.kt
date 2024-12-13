package com.bangkit.compends.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangkit.compends.R
import com.bangkit.compends.adapter.EmployeeAdapter
import com.bangkit.compends.data.preference.UserPreference
import com.bangkit.compends.data.preference.dataStore
import com.bangkit.compends.data.response.EmployeeResponseItem
import com.bangkit.compends.databinding.ActivityEmployeeBinding
import com.bangkit.compends.viewmodel.EmployeeViewModel
import com.bangkit.compends.viewmodel.UserViewModel
import com.bangkit.compends.viewmodel.ViewModelFactory

class ListEmployeeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEmployeeBinding
    private lateinit var viewModel: EmployeeViewModel
    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmployeeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pref = UserPreference.getInstance(application.dataStore)
        viewModel = ViewModelProvider(this, ViewModelFactory(pref))[EmployeeViewModel::class.java]
        userViewModel = ViewModelProvider(this, ViewModelFactory(pref))[UserViewModel::class.java]

        viewModel.getEmployees(
            onSuccess = {},
            onLoading = {},
            onFailure = { message ->
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        )

        viewModel.employees.observe(this) { employee ->
            setupRecycleView(employee)
        }

        binding.btnAddEmploye.setOnClickListener {
            val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_employee, null)
            val dialogBuilder = AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(true)

            val alertDialog = dialogBuilder.create()
            alertDialog.show()

            // Get references to the input fields in the dialog
            val etName = dialogView.findViewById<EditText>(R.id.et_name)
            val etGender = dialogView.findViewById<EditText>(R.id.et_gender)
            val etAge = dialogView.findViewById<EditText>(R.id.et_age)
            val etJobTitle = dialogView.findViewById<EditText>(R.id.et_job_title)
            val etSalary = dialogView.findViewById<EditText>(R.id.et_salary)
            val btnSubmit = dialogView.findViewById<Button>(R.id.btn_submit)

            btnSubmit.setOnClickListener {
                // Get input values
                val name = etName.text.toString()
                val gender = etGender.text.toString()
                val age = etAge.text.toString().toIntOrNull() ?: 0
                val jobTitle = etJobTitle.text.toString()
                val salary = etSalary.text.toString().toIntOrNull() ?: 0

                // Validate inputs
                if (name.isBlank() || gender.isBlank() || age <= 0 || jobTitle.isBlank() || salary <= 0) {
                    Toast.makeText(this, "Please fill in all fields correctly", Toast.LENGTH_SHORT).show()
                } else {
                    // Create a new EmployeeResponseItem
                    val newEmployee = EmployeeResponseItem(
                        name = name,
                        gender = gender,
                        age = age,
                        jobTitle = jobTitle,
                        salary = salary
                    )

                    // Add the new employee to the list (you can also call an API here to persist the data)
                    addEmployeeToList(newEmployee)

                    // Dismiss the dialog
                    alertDialog.dismiss()
                }
            }
        }

        binding.btnDelete.setOnClickListener {
            val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_delete_employee, null)
            val dialogBuilder = AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(true)

            val alertDialog = dialogBuilder.create()
            alertDialog.show()

            // Get references to the dialog views
            val spinnerEmployees = dialogView.findViewById<Spinner>(R.id.spinner_employees)
            val btnConfirmDelete = dialogView.findViewById<Button>(R.id.btn_confirm_delete)

            // Get the employee list from the ViewModel
            val employees = viewModel.employees.value

            // Populate the spinner with employee names
            if (employees != null) {
                val employeeNames = employees.map { it.name ?: "Unknown" }
                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, employeeNames)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerEmployees.adapter = adapter
            }

            // Handle delete button click
            btnConfirmDelete.setOnClickListener {
                // Get the selected employee's position
                val selectedPosition = spinnerEmployees.selectedItemPosition
                val selectedEmployee = employees?.get(selectedPosition)

                if (selectedEmployee != null) {
                    // Call the API to delete the selected employee
                    viewModel.deleteEmployee(
                        selectedEmployee.id.toString(),
                        onSuccess = {
                            Toast.makeText(this, "Employee deleted successfully", Toast.LENGTH_SHORT).show()
                            alertDialog.dismiss()

                        // Optionally, refresh the list or remove the employee from the adapter
                            viewModel.getEmployees(
                                onSuccess = { updatedEmployees ->
                                    setupRecycleView(updatedEmployees)
                                },
                                onFailure = { errorMsg ->
                                    Toast.makeText(this, "Error: $errorMsg", Toast.LENGTH_SHORT).show()
                                },
                                onLoading = { isLoading ->
                                // Show or hide progress indicator if needed
                                }
                            )
                        },
                        onFailure = {
                            Toast.makeText(this, "Failed to delete employee", Toast.LENGTH_SHORT).show()
                        },
                        onLoading = {}
                    )
                }
            }
        }

        binding.btnLogout.setOnClickListener {
            userViewModel.destroyUserToken {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
    }

    private fun addEmployeeToList(newEmployee: EmployeeResponseItem) {
        viewModel.addEmployee(
            request = newEmployee,
            onSuccess = {
                viewModel.getEmployees(
                    onSuccess = {},
                    onLoading = {},
                    onFailure = { message ->
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                    }
                )
            },
            onFailure = {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            },
            onLoading = {}
        )
    }

    private fun setupRecycleView(employeeList: List<EmployeeResponseItem>?) {
        if (!employeeList.isNullOrEmpty()) {
            val adapter = EmployeeAdapter(employeeList)
            binding.rvEmployee.layoutManager = LinearLayoutManager(this)
            binding.rvEmployee.adapter = adapter
        } else {
            // Show empty state or placeholder if the list is null or empty
            Log.d("setupRecycleView", "Employee list is empty or null")
        }
    }

}