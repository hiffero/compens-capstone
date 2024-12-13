package com.bangkit.compends.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.compends.data.response.EmployeeResponseItem
import com.bangkit.compends.databinding.ItemEmployeeBinding

class EmployeeAdapter(
    private val employeeList: List<EmployeeResponseItem?>?
) : RecyclerView.Adapter<EmployeeAdapter.EmployeeViewHolder>() {

    inner class EmployeeViewHolder(private val binding: ItemEmployeeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(employee: EmployeeResponseItem?, position: Int) {
            employee?.let {
                binding.tvNumber.text = (position + 1).toString()
                binding.tvName.text = it.name?.ifEmpty { "Unknown" } ?: "Unknown"
                binding.tvGender.text = it.gender?.ifEmpty { "Unknown" } ?: "Unknown"
                binding.tvAge.text = if (it.age != null) {
                    "${it.age} years"
                } else {
                    "N/A"
                }
                binding.tvSalary.text = if (it.salary != null) {
                    "Rp. ${it.salary}"
                } else {
                    "Rp. 0"
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmployeeViewHolder {
        val binding = ItemEmployeeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return EmployeeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EmployeeViewHolder, position: Int) {
        holder.bind(employeeList?.get(position), position)
    }

    override fun getItemCount(): Int = employeeList?.size ?: 0
}
