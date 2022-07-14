package com.cci.spaceoperators.users

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.cci.spaceoperators.databinding.FragmentPlayerListBinding
import com.cci.spaceoperators.sockets.SocketViewModel

class PlayerListFragment : Fragment() {

    private lateinit var binding: FragmentPlayerListBinding
    private lateinit var playerAdapter: PlayerAdapter
    private val socketViewModel: SocketViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentPlayerListBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playerAdapter = PlayerAdapter(mutableListOf())
        binding.rvPlayerList.adapter = playerAdapter
        binding.rvPlayerList.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        socketViewModel.playerList.observe(viewLifecycleOwner) {
            val playerList = socketViewModel.playerList.value ?: mutableListOf()
            playerAdapter = PlayerAdapter(playerList)
            binding.rvPlayerList.adapter = playerAdapter
        }

    }

}