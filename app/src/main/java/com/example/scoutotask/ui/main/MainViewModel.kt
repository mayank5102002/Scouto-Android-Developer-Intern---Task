package com.example.scoutotask.ui.main

import UsersDb
import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private var usersDb: UsersDb? = null

    val userLoggedIn = MutableLiveData<Boolean>()
    var userId = -1
    val userRegistered = MutableLiveData<Boolean>()

    fun login(username: String, password: String, context: Context) {
        viewModelScope.launch {
            if(usersDb == null) {
                usersDb = UsersDb(context)
            }

            if(usersDb!!.checkUser(username, password)) {
                userId = usersDb!!.getUserId(username, password)
                userLoggedIn.value = true
            } else {
                userLoggedIn.value = false
            }
        }
    }

    fun register(username: String, password: String, context: Context) {
        viewModelScope.launch {
            if(usersDb == null) {
                usersDb = UsersDb(context)
            }

            if(usersDb!!.checkUser(username, password)) {
                userId = usersDb!!.getUserId(username, password)
                userLoggedIn.value = true
            } else {
                usersDb!!.addUser(username, password)
                userId = usersDb!!.getUserId(username, password)
                userRegistered.value = true
            }
        }
    }

}