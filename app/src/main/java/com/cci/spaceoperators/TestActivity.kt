package com.cci.spaceoperators

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.cci.spaceoperators.databinding.ActivityTestBinding
import com.cci.spaceoperators.sockets.SocketViewModel

class TestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTestBinding

    private val socketViewModel: SocketViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}