package com.cci.spaceoperators.sockets.payloads

import com.cci.spaceoperators.sockets.RequestPayload

data class ConnectPayload(
    val name: String
) : RequestPayload