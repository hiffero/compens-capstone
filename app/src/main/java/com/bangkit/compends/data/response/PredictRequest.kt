package com.bangkit.compends.data.response

import com.google.gson.annotations.SerializedName

data class PredictRequest(

	@field:SerializedName("educationLevel")
	val educationLevel: String? = null,

	@field:SerializedName("jobTitle")
	val jobTitle: String? = null,

	@field:SerializedName("workExperience")
	val workExperience: Int? = null,

	@field:SerializedName("age")
	val age: Int? = null
)
