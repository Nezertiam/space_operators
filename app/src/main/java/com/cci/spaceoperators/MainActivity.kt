package com.cci.spaceoperators

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import com.cci.spaceoperators.databinding.ActivityMainBinding
import com.cci.spaceoperators.users.UsernameDialogFragment
import com.cci.spaceoperators.users.UsernameViewModel
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


        binding.menuPlayerName.setOnClickListener { openUsernameDialog() }

        if (savedUsername != null) {
            usernameViewModel.changeUsername(savedUsername)
        } else {
            val currentUserName = usernameViewModel.currentUser.value
            prefs.edit().putString("username", currentUserName).commit()
        }

        // --------------------------------------------------------------



        // ----------------- BUTTONS SETTINGS ---------------------------

        // Create game button
        binding.menuCreateGameButton.setOnClickListener { goToCreateGame() }

        // Join game button
        // TODO("Not yet implemented")

        // History button
        // TODO("Not yet implemented")

        // Quit app button
        binding.menuQuitApp.setOnClickListener { closeApp() }

        // --------------------------------------------------------------

    }

    private fun goToCreateGame() {
        val intent = Intent(this, CreateGameActivity::class.java)
        startActivity(intent)
    }

    private fun closeApp() {
        moveTaskToBack(true)
        exitProcess(-1)
    }

    private fun openUsernameDialog() {
        UsernameDialogFragment().show(supportFragmentManager, "username-dialog")
    }
}