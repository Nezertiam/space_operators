package com.cci.spaceoperators

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.cci.spaceoperators.databinding.ActivityMainBinding
import com.cci.spaceoperators.users.UsernameDialogFragment
import com.cci.spaceoperators.users.UsernameViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val usernameViewModel: UsernameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        usernameViewModel.currentUser.observe(this) { username ->
            binding.menuPlayerName.text = username
        }

        binding.menuPlayerName.setOnClickListener {
            openUsernameDialog()
        }

        val username = getPreferences(Context.MODE_PRIVATE).getString("username", null)

        if (username == null) {
            openUsernameDialog()
        } else {
            usernameViewModel.changeUsername(username)
        }

    }

    private fun openUsernameDialog() {
        UsernameDialogFragment().show(supportFragmentManager, "username-dialog")
    }
}