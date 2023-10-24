package com.lamz.reneapps.response

import com.google.gson.annotations.SerializedName

data class UploadRegisterResponse(

	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String
)
