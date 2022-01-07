package com.jooheon.clean_architecture.presentation.common

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

fun AppCompatActivity.replaceFragment(
    savedInstanceState: Bundle?, @IdRes where: Int,
    fragment: Fragment, tag: String
) {
    if(savedInstanceState == null) {
        supportFragmentManager.beginTransaction()
            .replace(where, fragment, tag)
            .commit()
    }
}

fun AppCompatActivity.startActivityWithFinish(context: Context, activity: Class<*>) {
    startActivity(Intent(context, activity).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
    this.finish()
}