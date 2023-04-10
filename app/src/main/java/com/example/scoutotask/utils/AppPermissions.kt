package com.example.scoutotask.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager

object AppPermissions {

    const val REQUEST_CODE = 1

    fun checkPermissions(context: Context) : Boolean {
        return context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

    fun requestPermissions(context: Context) {
        if (context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
            (context as Activity).requestPermissions(permissions, REQUEST_CODE)
        }
    }

}