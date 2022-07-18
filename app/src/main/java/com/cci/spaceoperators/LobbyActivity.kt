package com.cci.spaceoperators

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import com.cci.spaceoperators.databinding.ActivityLobbyBinding
import com.cci.spaceoperators.sockets.Request
import com.cci.spaceoperators.sockets.RequestTypes
import com.cci.spaceoperators.sockets.SocketViewModel
import com.cci.spaceoperators.sockets.payloads.ConnectPayload
import com.cci.spaceoperators.sockets.payloads.StatusPayload
import com.cci.spaceoperators.users.dataClasses.Player

class LobbyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLobbyBinding

    private val socketViewModel: SocketViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLobbyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val prefs = getSharedPreferences("user", Context.MODE_PRIVATE)
        val username = prefs.getString("username", null) ?: "Inconnu"
        val isHost = intent.getBooleanExtra("isHost", false)


        // ------------ DISPLAYING INITIAL INFO ---------------

        updateLayoutInfo(isHost, username)


        // ----------------------------------------------------


        // ------------ UPDATING INFOS ------------------------

        // IP ADDRESS
        if (isHost) hostGame(username) else connectToGame(username)

        // BUTTON LISTENER
        if (isHost) {
            //
        } else {
            binding.button.setOnClickListener { updateStatus(username) }
        }

        // ----------------------------------------------------



    }

    private fun updateLayoutInfo(isHost: Boolean, username: String) {
        binding.tvUsername.text = username
        if (isHost) {
            binding.tvTitle.text = "Héberger une partie"
            binding.tvLobbyStatus.text = "En attente de joueurs"
        } else {
            binding.tvTitle.text = "Rejoindre une partie"
            socketViewModel.isReady.observe(this) { bool ->
                binding.button.text = if (bool) "Prêt" else "Pas prêt"
                binding.tvLobbyStatus.text = binding.button.text
            }
        }
    }

    private fun hostGame(username: String) {
        socketViewModel.ipAddress.observe(this) { address ->
            binding.tvAddress.text = address
            if (address != null) {
                Toast.makeText(applicationContext, "Connexion établie sur l'adresse : $address", Toast.LENGTH_SHORT).show()
                socketViewModel.playerList.postValue(
                    mutableListOf(
                        Player( username, address, socketViewModel.port.toString(),true )
                    )
                )
            }
        }
    }

    private fun connectToGame(username: String) {
        val address = intent.getStringExtra("ip")

        if (address != null) {
            val request = Request(
                RequestTypes.connect,
                ConnectPayload(username)
            )

            binding.tvAddress.text = address

            socketViewModel.sendUDPData(
                address,
                socketViewModel.port,
                request.toJson(),
            )
            Toast.makeText(applicationContext, "Connexion établie sur l'adresse : $address", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(applicationContext, "Impossible d'émettre une connexion à l'hôte...", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateStatus(username: String) {
        val address = intent.getStringExtra("ip")

        if (address != null) {
            val request = Request(
                RequestTypes.status,
                StatusPayload(
                    username,
                    address,
                    socketViewModel.port,
                    !socketViewModel.isReady.value!!
                )
            )
            socketViewModel.sendUDPData(address, socketViewModel.port, request.toJson())
            socketViewModel.isReady.postValue(!socketViewModel.isReady.value!!)

        }

    }


    override fun onDestroy() {
        socketViewModel.closeSocket()
        super.onDestroy()
    }

}