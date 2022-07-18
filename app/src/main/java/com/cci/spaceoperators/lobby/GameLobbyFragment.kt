package com.cci.spaceoperators.lobby

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.cci.spaceoperators.databinding.FragmentGameLobbyBinding
import com.cci.spaceoperators.sockets.Request
import com.cci.spaceoperators.sockets.RequestTypes
import com.cci.spaceoperators.sockets.SocketViewModel
import com.cci.spaceoperators.sockets.payloads.ConnectPayload
import com.cci.spaceoperators.sockets.payloads.StatusPayload
import com.cci.spaceoperators.users.dataClasses.Player

class GameLobbyFragment : Fragment() {

    private lateinit var binding: FragmentGameLobbyBinding
    private val socketViewModel: SocketViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGameLobbyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateLayoutInfo()

        if (checkIsHost()) {
            hostGameDisplay()
        } else {
            connectToGameDisplay()
            binding.button.setOnClickListener { updateStatus() }
        }

    }



    /**
     * Checks if the user is the game host and returns the result.
     */
    private fun checkIsHost() : Boolean {
        return activity?.intent!!.getBooleanExtra("isHost", false)
    }

    /**
     * Get the username saved in the Shared Preferences.
     */
    private fun getUsername() : String {
        val prefs = activity?.getSharedPreferences("user", Context.MODE_PRIVATE)
        return prefs!!.getString("username", null) ?: "Inconnu"
    }

    /**
     * Display all game's information on screen.
     * @param isHost To properly display information, the function needs to know if the user is the host of the game.
     * @param username The username of the player
     */
    private fun updateLayoutInfo() {
        val isHost = checkIsHost()
        val username = getUsername()
        binding.tvUsername.text = username
        if (isHost) {
            binding.tvTitle.text = "Héberger une partie"
            binding.tvLobbyStatus.text = "En attente de joueurs"
        } else {
            binding.tvTitle.text = "Rejoindre une partie"
            socketViewModel.isReady.observe(viewLifecycleOwner) { isReady ->
                binding.button.text = if (isReady) "Prêt" else "Pas prêt"
                binding.tvLobbyStatus.text = binding.button.text
            }
        }
    }

    /**
     * Display relative information to a new game.
     */
    private fun hostGameDisplay() {
        val username = getUsername()
        socketViewModel.ipAddress.observe(viewLifecycleOwner) { address ->
            binding.tvAddress.text = address
            if (address != null) {
                Toast.makeText(activity?.applicationContext, "Connexion établie sur l'adresse : $address", Toast.LENGTH_SHORT).show()
                socketViewModel.playerList.postValue(
                    mutableListOf(
                        Player( username, address, socketViewModel.port.toString(),true )
                    )
                )
            }
        }
    }

    /**
     * Display relative information to an existing game.
     */
    private fun connectToGameDisplay() {
        val address = activity?.intent!!.getStringExtra("ip")
        binding.tvAddress.text = address
    }

    private fun updateStatus() {
        val username = getUsername()
        val address = activity?.intent!!.getStringExtra("ip")

        if (address != null) {
            val request = Request(
                RequestTypes.STATUS,
                StatusPayload(
                    username,
                    !socketViewModel.isReady.value!!
                )
            )
            socketViewModel.sendUDPData(address, socketViewModel.port, request.toJson())
            socketViewModel.isReady.postValue(!socketViewModel.isReady.value!!)

        }
    }

}