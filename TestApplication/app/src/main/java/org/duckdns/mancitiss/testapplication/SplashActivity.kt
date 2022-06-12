package org.duckdns.mancitiss.testapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.animation.AnimationUtils
import android.widget.ImageView
import org.duckdns.mancitiss.testapplication.MainActivity
import org.duckdns.mancitiss.testapplication.R

class SplashActivity : AppCompatActivity() {

    private lateinit var img:ImageView
    private lateinit var handler:Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        img = findViewById(R.id.iv_spin)
        rotateAnimation()

        handler = Handler()
        handler.postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, 3000

        )
    }

    private fun rotateAnimation(){
        val rotate = AnimationUtils.loadAnimation(this, R.anim.rotate)
        img.animation = rotate
    }
}
