package com.lamz.reneapps.response

import com.google.gson.annotations.SerializedName

data class MapsResponse(

	@field:SerializedName("listStory")
	val listStory: List<ListStoryItems> = emptyList(),

	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String
)

data class ListStoryItems(

	@field:SerializedName("name")
	val name: String   ,

	@field:SerializedName("description")
	val description: String   ,

	@field:SerializedName("lon")
	val lon: Double  ,

	@field:SerializedName("lat")
	val lat: Double
)
