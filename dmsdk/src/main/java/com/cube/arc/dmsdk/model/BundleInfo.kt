package com.cube.arc.dmsdk.model

import com.google.gson.annotations.SerializedName

/**
 *
 */
data class BundleInfo(
	var status: Int = 0,
	var data: BundleInfo.Data
)
{
	data class Data(
		var id: Int = 0,
		@SerializedName("publish_date") var publishDate: String,
		@SerializedName("download_url") var downloadUrl: String,
		var languages: List<String> = listOf()
	)
}
