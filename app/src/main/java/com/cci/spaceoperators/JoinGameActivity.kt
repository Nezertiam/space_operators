package com.cci.spaceoperators

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import com.cci.spaceoperators.databinding.ActivityJoinGameBinding
import com.cci.spaceoperators.sockets.Request
import com.cci.spaceoperators.sockets.RequestTypes
import com.cci.spaceoperators.sockets.SocketViewModel
import com.cci.spaceoperators.sockets.payloads.ConnectPayload

class JoinGameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityJoinGameBinding

    private val socketViewModel: SocketViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJoinGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val prefs = getSharedPreferences("user", Context.MODE_PRIVATE)
        val savedUsername = prefs.getString("username", null)
        val username = savedUsername ?: "Invité"


    }

    override fun onDestroy() {
        socketViewModel.closeSocket()
        super.onDestroy()
    }
}