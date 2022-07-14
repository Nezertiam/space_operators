package com.cci.spaceoperators.users

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.cci.spaceoperators.databinding.FragmentPlayerBinding
import com.cci.spaceoperators.sockets.SocketViewModel
import com.cci.spaceoperators.users.dataClasses.Player

class PlayerAdapter(
    private var players: MutableList<Player>
) : RecyclerView.Adapter<PlayerAdapter.PlayerViewHolder>() {

    inner class PlayerViewHolder(
        binding: FragmentPlayerBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        val playerName = binding.PlayerName
        val playerState = binding.PlayerState
    }

    fun updatePlayerList(list: MutableList<Player>) {
        players = list
        this!!.notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder =
        PlayerViewHolder(
            FragmentPlayerBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {

        val player = players[position]

        holder.playerName.text = player.name
        holder.playerState.text = if (player.isReady) "Prêt" else "Pas prêt"

    }

    override fun getItemCount(): Int {
        return players.size
    }
}