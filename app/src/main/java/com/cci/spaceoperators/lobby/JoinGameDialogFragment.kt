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

        val matchAddress = address.matches("^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$".toRegex()) && address != "255.255.255.255"

        if (matchAddress) {
            val intent = Intent(activity, LobbyActivity::class.java)
                .putExtra("isHost", false)
                .putExtra("ip", address)
            startActivity(intent)
        } else {
            Toast.makeText(activity?.applicationContext, "Adresse IP non valide", Toast.LENGTH_SHORT).show()
        }

    }
}