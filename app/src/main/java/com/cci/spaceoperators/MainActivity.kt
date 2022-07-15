package com.cci.spaceoperators

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import com.cci.spaceoperators.databinding.ActivityMainBinding
import com.cci.spaceoperators.lobby.JoinGameDialogFragment
import com.cci.spaceoperators.sockets.SocketViewModel
import com.cci.spaceoperators.users.UsernameDialogFragment
import com.cci.spaceoperators.users.UsernameViewModel
import java.net.Socket
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val usernameViewModel: UsernameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val prefs = getSharedPreferences("user", Context.MODE_PRIVATE)


        // ------------------ USERNAME SETTINGS -------------------------

        val savedUsername = prefs.getString("username", null)

        usernameViewModel.currentUser.observe(this) { username ->
            binding.menuPlayerName.text = username
        }

        if (savedUsername != null) {
            usernameViewModel.changeUsername(savedUsername)
        } else {
            val currentUserName = usernameViewModel.currentUser.value
            prefs.edit().putString("username", currentUserName).commit()
        }

        binding.menuPlayerName.setOnClickListener { openUsernameDialog() }

        // --------------------------------------------------------------



        // ----------------- BUTTONS SETTINGS ---------------------------

        // Create game button
        binding.menuCreateGameButton.setOnClickListener { goToCreateGame() }

        // Join game button
        binding.menuJoinGameButton.setOnClickListener { openJoinGameDialog() }

        // History button
        // TODO("Not yet implemented")

        // Quit app button
        binding.menuQuitApp.setOnClickListener { closeApp() }

        // --------------------------------------------------------------

    }

    private fun goToCreateGame() {
        val intent = Intent(this, LobbyActivity::class.java)
            .putExtra("isHost", true)
        startActivity(intent)
    }

    private fun closeApp() {
        moveTaskToBack(true)
        exitProcess(-1)
    }

    private fun openUsernameDialog() {
        UsernameDialogFragment().show(supportFragmentManager, "username-dialog")
    }

    private fun openJoinGameDialog() {
        JoinGameDialogFragment().show(supportFragmentManager, "join-game-dialog")
    }
}