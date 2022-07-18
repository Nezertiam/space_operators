package com.cci.spaceoperators

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import com.cci.spaceoperators.databinding.ActivityLobbyBinding
import com.cci.spaceoperators.lobby.ConnectionStatus
import com.cci.spaceoperators.lobby.GameLobbyFragment
import com.cci.spaceoperators.lobby.LoadingFragment
import com.cci.spaceoperators.sockets.Request
import com.cci.spaceoperators.sockets.RequestTypes
import com.cci.spaceoperators.sockets.SocketViewModel
import com.cci.spaceoperators.sockets.payloads.ConnectPayload
import com.cci.spaceoperators.sockets.payloads.StatusPayload

class LobbyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLobbyBinding

    private val socketViewModel: SocketViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLobbyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val isHost = checkIsHost()
        if (isHost) {
            socketViewModel.connectionStatus.postValue(ConnectionStatus.CONNECTED)
            displayLobby(savedInstanceState)
        }
        else {
            displayLoading(savedInstanceState)
            tryConnectToExistingGame(savedInstanceState)
        }

    }

    /**
     * Checks if the user is the game host and returns the result.
     */
    private fun checkIsHost() : Boolean {
        return intent!!.getBooleanExtra("isHost", false)
    }

    /**
     * Get the username saved in the Shared Preferences.
     */
    private fun getUsername() : String {
        val prefs = getSharedPreferences("user", Context.MODE_PRIVATE)
        return prefs!!.getString("username", null) ?: "Inconnu"
    }

    /**
     * Return the address sent by the Main Activity.
     */
    private fun getAddress() : String? {
        return intent.getStringExtra("ip")
    }

    private fun displayLoading(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fgmGameLayout, LoadingFragment())
                .commit()
        }
    }

    private fun displayLobby(savedInstanceState: Bundle?) {
        socketViewModel.connectionStatus.postValue(ConnectionStatus.CONNECTED)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fgmGameLayout, GameLobbyFragment())
                .commit()
        }
    }

    private fun tryConnectToExistingGame(savedInstanceState: Bundle?) {
        val username = getUsername()
        val address = getAddress()

        if (address != null) {
            socketViewModel.ipAddress.postValue(address)

            val timer = object: CountDownTimer(5000, 1000) {
                override fun onTick(p0: Long) {}

                override fun onFinish() {
                    Toast.makeText(applicationContext, "Le serveur ne répond pas...", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            timer.start()

            socketViewModel.playerList.observe(this@LobbyActivity) {
                if (it.size > 0) {
                    timer.cancel()
                    displayLobby(savedInstanceState)
                }
            }

            val request = Request(
                RequestTypes.CONNECT,
                ConnectPayload(username)
            )

            socketViewModel.sendUDPData(
                address,
                socketViewModel.port,
                request.toJson(),
            )



        } else {
            Toast.makeText(applicationContext, "Impossible d'émettre une connexion à l'hôte...", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onStop() {
        super.onStop()

        val isHost = checkIsHost()
        val address = getAddress()
        val name = getUsername()

        if (!isHost && address != null) {
            val request = Request(
                RequestTypes.DISCONNECT
            )
            socketViewModel.sendUDPData(
                address,
                socketViewModel.port,
                request.toJson()
            )
        }

        socketViewModel.closeSocket()

        finish()
    }
}