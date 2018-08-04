package com.anwesh.uiprojects.linkedmessageboxdownwardview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.anwesh.uiprojects.messageboxdownwardview.MessageBoxDownwardView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MessageBoxDownwardView.create(this)
    }
}
