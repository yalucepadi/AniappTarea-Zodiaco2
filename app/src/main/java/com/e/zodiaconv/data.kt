package com.e.zodiaconv

import com.google.gson.annotations.SerializedName

data class UserInfo (
    @SerializedName("sign") val sign: String?,
    @SerializedName("day") val day: String?

)