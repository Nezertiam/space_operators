package com.cci.spaceoperators.lobby

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.cci.spaceoperators.LobbyActivity
import com.cci.spaceoperators.databinding.FragmentJoinGameDialogBinding


class JoinGameDialogFragment : DialogFragment() {

    private lateinit var binding: FragmentJoinGameDialogBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = FragmentJoinGameDialogBinding.inflate(layoutInflater, null, false)

        val builder = AlertDialog.Builder(requireActivity())

        return builder
            .setMessage("Rejoindre une partie")
            .setView(binding.root)
            .setPositiveButton("Valider") { _, _ -> joinGame() }
            .setNegativeButton("Annuler") { dialog, _ -> dialog.dismiss() }
            .create()
    }


    private fun joinGame() {

        val address = binding.etAddress.text.toString()
        val port = binding.etPort.text.toString()
        val maxPortNumber = 65535

        val matchAddress = address.matches("^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$".toRegex()) && address != "255.255.255.255"
        val matchPort = port.matches("-?\\d+(\\.\\d+)?".toRegex()) && port.length <= 5 && port.toInt() >= 0 && port.toInt() <= maxPortNumber

        if (matchAddress && matchPort) {
            val intent = Intent(activity, LobbyActivity::class.java)
                .putExtra("isHost", false)
                .putExtra("ip", address)
                .putExtra("port", port.toInt())
            startActivity(intent)
        } else {
            if (!matchAddress) {
                Toast.makeText(activity?.applicationContext, "Adresse IP non valide", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(activity?.applicationContext, "Port non valide", Toast.LENGTH_SHORT).show()
            }
        }

    }
}