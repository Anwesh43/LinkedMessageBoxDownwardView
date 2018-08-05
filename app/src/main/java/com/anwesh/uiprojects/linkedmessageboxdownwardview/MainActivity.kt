package com.anwesh.uiprojects.linkedmessageboxdownwardview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import com.anwesh.uiprojects.messageboxdownwardview.MessageBoxDownwardView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view : MessageBoxDownwardView = MessageBoxDownwardView.create(this)
        fullScreen()
        view.addAnimationListener({createCompleteToast(it)}, {createResetToast(it)})
    }
}

fun MainActivity.fullScreen() {
    supportActionBar?.hide()
    window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
}

fun MainActivity.createToast(msg : String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

fun MainActivity.createCompleteToast(i : Int) {
    createToast("animation ${i+1} completed")
}

fun MainActivity.createResetToast(i : Int) {
    createToast("animation ${i+1} is reset")
}