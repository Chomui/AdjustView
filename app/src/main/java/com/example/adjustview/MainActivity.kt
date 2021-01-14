package com.example.adjustview

import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<AdjustScrollView>(R.id.adjust).setOnScrollCallback(object : AdjustScrollView.OnScrollCallback {
            override fun onScroll(percent: Float) {
                findViewById<TextView>(R.id.text).text = percent.toString()
            }
        })
        findViewById<TextView>(R.id.text).setOnClickListener {
            findViewById<AdjustScrollView>(R.id.adjust).setProgress(0F)
            findViewById<AdjustScrollView>(R.id.adjust).setColumnsColor(Color.MAGENTA)
            findViewById<AdjustScrollView>(R.id.adjust).setColumnsWidth(resources.getDimension(R.dimen.test))
            findViewById<AdjustScrollView>(R.id.adjust).setColumnsAmount(10)
            findViewById<AdjustScrollView>(R.id.adjust).setColumnsOffset(resources.getDimension(R.dimen.test_2))
        }
    }
}