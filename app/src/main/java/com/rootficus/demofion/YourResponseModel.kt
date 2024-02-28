package com.rootficus.demofion

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class YourResponseModel (
    @field:SerializedName("status")
    var status: Int? = null,

    @field:SerializedName("msg")
    var msg: String? = null,

    @field:SerializedName("currency")
    var currency: String? = null,

    @field:SerializedName("redirect_url")
    var redirect_url: String? = null,

    @field:SerializedName("request_id")
    var request_id: String? = null,

    @field:SerializedName("callback_url")
    var callback_url: String? = null,
) : Serializable
