package org.duckdns.mancitiss.testapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.loading)
        Thread.sleep(3000)
        setContentView(R.layout.activity_main)

    }
}
