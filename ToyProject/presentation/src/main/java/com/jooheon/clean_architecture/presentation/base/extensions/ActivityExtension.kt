package com.jooheon.clean_architecture.presentation.common

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition

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

fun <A : Activity> Activity.openActivityAndClearStack(activity: Class<A>) {
    Intent(this, activity).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(this)
        finish()
    }
}

fun Context.loadImageBitmap(url: String, onImageLoaded: (Bitmap) -> Unit) {
    Glide.with(this).asBitmap().load(url)
        .into(object : CustomTarget<Bitmap>() {
            override fun onResourceReady(
                resource: Bitmap,
                transition: Transition<in Bitmap>?
            ) {
                onImageLoaded(resource)
            }

            override fun onLoadCleared(placeholder: Drawable?) = Unit
        })
}

fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}