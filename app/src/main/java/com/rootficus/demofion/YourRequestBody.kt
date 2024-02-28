package com.rootficus.demofion

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class YourRequestBody(
    @field:SerializedName("amount")
    var amount: Long? = null,

    @field:SerializedName("bank_type")
    var bank_type: String? = null,

    @field:SerializedName("request_type")
    var request_type: String? = null,

    @field:SerializedName("redirect_url")
    var redirect_url: String? = null,

    @field:SerializedName("merchant_payment_id")
    var merchant_payment_id: String? = null,

    @field:SerializedName("currency")
    var currency: String? = "BDT",

    @field:SerializedName("cust_phone")
    var cust_phone: String? = null,
) : Serializable