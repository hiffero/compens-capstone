package com.bangkit.compends.data.response

import com.bangkit.compends.adapter.IntDefaultAdapter
import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName

data class EmployeeResponseItem(

	@field:SerializedName("gender")
	val gender: String? = null,

	@field:SerializedName("jobTitle")
	val jobTitle: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("salary")
	@JsonAdapter(IntDefaultAdapter::class)
	val salary: Int? = null,

	@field:SerializedName("age")
	@JsonAdapter(IntDefaultAdapter::class)
	val age: Int? = null
)
