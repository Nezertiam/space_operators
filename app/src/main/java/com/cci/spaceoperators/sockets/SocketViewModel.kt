package com.cci.spaceoperators.sockets

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.cci.spaceoperators.lobby.ConnectionStatus
import com.cci.spaceoperators.sockets.payloads.PlayersPayload
import com.cci.spaceoperators.users.dataClasses.Player
import com.cci.spaceoperators.users.dataClasses.PlayerList
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.lang.Exception
import java.net.*

class SocketViewModel(application: Application): AndroidViewModel(application)  {

    val port = 8888
    val ipAddress = MutableLiveData<String>(null)

    // Player list and status
    val playerList = MutableLiveData<MutableList<Player>>(mutableListOf())
    val isReady = MutableLiveData(false)
    val connectionStatus = MutableLiveData(ConnectionStatus.LOADING)


    private val gson = GsonBuilder().create()
    private val serverSocket = DatagramSocket(port)


    init {
        viewModelScope.launch(Dispatchers.IO) {
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

    private fun listenSocket() {
        viewModelScope.launch(Dispatchers.IO) {
            val buffer = ByteArray(256)
            var packet = DatagramPacket(buffer, buffer.size)

            try {
                serverSocket.receive(packet)

                handlePacketRetrieve(packet) // Packet routing

                listenSocket()
            } catch (ex: Exception) {
                closeSocket()
            }

        }
    }




// ------------------------ UTILS -----------------------------------------------------

    /**
     * Close the socket.
     */
    fun closeSocket() {
        serverSocket.close()
    }

    /**
     * Send a UDP packet to the given IP address and attaching the given payload.
     *
     * @param address The IP address to send the packet.
     * @param port The PORT to send the packet.
     * @param data The payload in JSON type casted into a string.
     */
    fun sendUDPData(address: String, port: Int, data: String) {
        viewModelScope.launch(Dispatchers.IO) {
            DatagramSocket().use {
                val dataBytes = data.toByteArray()
                val inetAddress = InetAddress.getByName(address)
                val packet = DatagramPacket(dataBytes, dataBytes.size, inetAddress, port)
                it.send(packet)
            }
            val type = JSONObject(data).getString("type")
            Log.d("PACKET", "Sending packet with type $type")
        }
    }

    /**
     * Extracts and formats the ip address of the sender from the packet received.
     *
     * @param packet The DatagramPacket received from the request.
     */
    private fun getPacketIp(packet: DatagramPacket): String {
        return packet.address.toString().replace("/", "")
    }

    /**
     * Send a packet with the player list in the payload to all players except the sender.
     */
    private fun sendUpdatedPlayerList() {
        val request = Request(
            RequestTypes.PLAYERS,
            PlayersPayload(playerList.value!!)
        )
        playerList.value!!.map { it ->
            if (it.ip != ipAddress.value) {
                sendUDPData(
                    it.ip,
                    port,
                    request.toJson()
                )
            }
        }
    }

// ------------------------------------------------------------------------------------





// ----------------- RECEIVING PACKET HANDLERS ----------------------------------------


    private fun handlePacketRetrieve(packet: DatagramPacket) {

        Log.d("PACKET", "UDP packet received")

        val data = JSONObject(packet.data.decodeToString())

        Log.d("PACKET", "Type of ${data.getString("type")}")

        when(data.getString("type")) {
            RequestTypes.CONNECT.toString() -> connectPlayer(packet)
            RequestTypes.PLAYERS.toString() -> updatePlayers(packet)
            RequestTypes.STATUS.toString() -> updateStatus(packet)
            RequestTypes.DISCONNECT.toString() -> disconnectPlayer(packet)
            else -> {}
        }

    }


    private fun connectPlayer(packet: DatagramPacket) {

        // Get the payload and deserialize
        val data = JSONObject(packet.data.decodeToString()).getJSONObject("data")

        val newPlayer = Player(
            data.getString("name"),
            getPacketIp(packet),
            port.toString()
        )

        // Update player list with new player
        val list = playerList.value
        list?.add(newPlayer)
        playerList.postValue(list!!)

        // Send the updated list to all players
        sendUpdatedPlayerList()

    }



    private fun updatePlayers(packet: DatagramPacket) {
        val data = JSONObject(packet.data.decodeToString())
            .getJSONObject("data")

        val newList = gson.fromJson(data.toString(), PlayerList::class.java)
        playerList.postValue(newList.players)
    }



    private fun updateStatus(packet: DatagramPacket) {
        // Get the payload
        val data = JSONObject(packet.data.decodeToString())
            .getJSONObject("data")

        // Update the player
        val updatedPlayer = gson.fromJson(data.toString(), Player::class.java)
        val list = playerList.value!!
        val index = list.indexOf(
            list.find { player -> player.ip == getPacketIp(packet) }
        )
        list[index].isReady = updatedPlayer.isReady
        playerList.postValue(list)

        // Resend player list
        sendUpdatedPlayerList()
    }

    private fun disconnectPlayer(packet: DatagramPacket) {
        // Get the player identifier
        val ip = getPacketIp(packet)

        // Update the list by removing the player
        val list = playerList.value!!
        val index = list.indexOf(
            list.find { player -> player.ip == ip }
        )
        list.removeAt(index)
        playerList.postValue(list)

        // Resend the updated list
        sendUpdatedPlayerList()
    }

// ------------------------------------------------------------------------------------

}