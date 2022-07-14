package com.cci.spaceoperators.sockets.payloads

import com.cci.spaceoperators.sockets.RequestPayload

data class StatusPayload(
    val name: String,
    val ip: String,
    val port: Int,
    val status: Boolean
) : RequestPayload