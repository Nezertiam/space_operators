package com.cci.spaceoperators.sockets

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.Inet4Address
import java.net.NetworkInterface

class SocketViewModel(application: Application): AndroidViewModel(application)  {

    private val serverSocket = DatagramSocket(8008)
    val ipAddress = MutableLiveData<String>(null)
    val running = MutableLiveData<Boolean?>(null)
    val port = MutableLiveData<Int>(null)

    init {
        viewModelScope.launch(Dispatchers.IO) {
            port.postValue(serverSocket.localPort)
            val networkInterfaces = NetworkInterface.getNetworkInterfaces()
            val ip = networkInterfaces.toList()
                .find { it.displayName == "wlan0"}
                ?.inetAddresses?.toList()
                ?.find { it is Inet4Address }
                ?.hostAddress ?: "127.0.0.1"
            ipAddress.postValue(ip)
        }

        listenSocket()
    }

    fun listenSocket() {
        viewModelScope.launch(Dispatchers.IO) {
            val buffer = ByteArray(256)
            var packet = DatagramPacket(buffer, buffer.size)

            try {
                serverSocket.receive(packet)

                handlePacketRetrieve(packet)

                listenSocket()
            } catch (ex: Exception) {
                closeSocket()
                Log.d("Erreur", "Grosse sous merde")
            }

        }
    }

    fun handlePacketRetrieve(packet: DatagramPacket) {
    }

    fun closeSocket() {
        running.postValue(false)
        running.postValue(null)

        serverSocket.close()
    }

}