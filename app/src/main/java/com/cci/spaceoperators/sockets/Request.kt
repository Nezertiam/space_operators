package com.cci.spaceoperators.sockets

import com.google.gson.GsonBuilder

data class Request(
    val type: RequestTypes,
    val data: RequestPayload? = null
) {
    fun toJson(): String {
        return GsonBuilder().create().toJson(this)
    }
}