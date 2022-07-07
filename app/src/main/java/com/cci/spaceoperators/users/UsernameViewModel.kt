package com.cci.spaceoperators.users

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import java.util.UUID

class UsernameViewModel(application: Application): AndroidViewModel(application) {

    private val identifier = UUID.randomUUID().toString().split("-")[0]

    var currentUser = MutableLiveData<String>("Player $identifier")

    fun changeUsername(username: String): Boolean {
        return if (username.length in 4..14) {
            currentUser.postValue(username)
            true
        } else {
            false
        }
    }

}