package com.cci.spaceoperators.users.dataClasses

import com.google.gson.annotations.SerializedName

data class PlayerList(
    @SerializedName("players")
    val players: MutableList<Player>
)