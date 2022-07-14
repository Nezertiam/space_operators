package com.cci.spaceoperators.sockets.payloads

import com.cci.spaceoperators.sockets.RequestPayload
import com.cci.spaceoperators.users.dataClasses.Player

data class PlayersPayload(
    val players: MutableList<Player>
) : RequestPayload