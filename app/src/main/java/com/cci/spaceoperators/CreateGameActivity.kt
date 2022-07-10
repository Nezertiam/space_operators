package com.cci.spaceoperators

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import com.cci.spaceoperators.databinding.ActivityCreateGameBinding
import com.cci.spaceoperators.sockets.SocketViewModel
import com.cci.spaceoperators.users.UsernameViewModel

class CreateGameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateGameBinding

    private val usernameViewModel: UsernameViewModel by viewModels()
    private val socketViewModel: SocketViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_game)
        binding = ActivityCreateGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val prefs = getSharedPreferences("user", Context.MODE_PRIVATE)


        // ------------ DISPLAYING PLAYERS INFO ---------------

        // USERNAME

        val savedUsername = prefs.getString("username", null)

        usernameViewModel.currentUser.observe(this) { username ->
            binding.createGameCurrentUsername.text = username
        }

        if (savedUsername != null) { usernameViewModel.changeUsername(savedUsername) }


        // SOCKET

        socketViewModel.ipAddress.observe(this) { address ->
            binding.createGameAddress.text = address
            if (address != null) {
                Toast.makeText(applicationContext, "Connexion établie sur l'adresse : $address", Toast.LENGTH_LONG).show()
            }
        }

        socketViewModel.port.observe(this) { port ->
            binding.createGamePort.text = port.toString()
        }

        socketViewModel.running.observe(this) { running ->
            if (running == false) {
                Toast.makeText(applicationContext, "Fermeture de la connexion.", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroy() {
        socketViewModel.closeSocket()
        super.onDestroy()
    }

}