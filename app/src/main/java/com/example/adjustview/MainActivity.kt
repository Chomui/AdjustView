package com.example.adjustview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<AdjustScrollView>(R.id.adjust).setOnScrollCallback(object : AdjustScrollView.OnScrollCallback {
            override fun onScroll(percent: Float) {
                findViewById<TextView>(R.id.text).text = percent.toString()
            }
        })
    }
}