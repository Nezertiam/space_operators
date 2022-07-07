package com.cci.spaceoperators.users

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.cci.spaceoperators.databinding.FragmentUsernameDialogBinding


class UsernameDialogFragment : DialogFragment() {

    private lateinit var binding: FragmentUsernameDialogBinding

    private val usernameViewModel: UsernameViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = FragmentUsernameDialogBinding.inflate(
            layoutInflater,
            null,
            false
        )

        val builder = AlertDialog.Builder(requireActivity())

        val dialog =
            builder
                .setMessage("Modifier pseudo")
                .setView(binding.root)
                .setPositiveButton("Valider") { dialog, _ -> changeUsername(dialog)}
                .setNegativeButton("Annuler") { dialog, _ -> dialog.cancel()}
                .create()


        return dialog
    }

    private fun changeUsername(dialog: DialogInterface) {

        val hasSucceed = usernameViewModel.changeUsername(
            binding.usernameInput.text.toString()
        )

        if (hasSucceed) {
            val prefs = requireActivity().getPreferences(Context.MODE_PRIVATE)

            prefs.edit()
                .putString("username", binding.usernameInput.text.toString())
                .apply()

            dialog.dismiss()
        } else {
            Toast.makeText(activity, "Le pseudo doit être compris entre 3 et 15 caractères", Toast.LENGTH_LONG).show()
        }
    }

}