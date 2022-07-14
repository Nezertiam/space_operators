package com.cci.spaceoperators.users.dataClasses

import com.google.gson.annotations.SerializedName

data class Player(
    @SerializedName("name")
    var name: String,
    @SerializedName("ip")
    val ip: String,
    @SerializedName("port")
    val port: String,
    @SerializedName("status")
    var isReady: Boolean = false
)