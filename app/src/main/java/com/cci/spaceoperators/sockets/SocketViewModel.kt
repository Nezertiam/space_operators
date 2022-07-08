package com.cci.spaceoperators.sockets

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.Inet4Address
import java.net.NetworkInterface

class SocketViewModel(application: Application): AndroidViewModel(application)  {

    val ipAddress = MutableLiveData<String>(null)

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val networkInterfaces = NetworkInterface.getNetworkInterfaces()
            val ip =
                networkInterfaces.toList()
                    .find { it.displayName == "wlan0"}
                    ?.inetAddresses?.toList()
                    ?.find { it is Inet4Address }
                    ?.hostAddress ?: "127.0.0.1"
            ipAddress.postValue(ip)
        }
    }

}