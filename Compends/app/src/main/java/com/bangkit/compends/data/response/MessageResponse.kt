package com.bangkit.compends.data.response

import com.google.gson.annotations.SerializedName

data class MessageResponse(

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class RegisterRequest(
	val email: String,
	val password: String,
	val phone: String,
	val name: String
)
