package org.duckdns.mancitiss.testapplication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_login.*
import kotlin.concurrent.thread


class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        backLogin.setOnClickListener{
            finish()
        }
        buttonLogIn.setOnClickListener{
            thread {
                val username: String = editTextUsername.text.toString()
                val pw: String = editTextPassword.text.toString()
                if (Connection.LogIn(this, this, username, pw)) {
                    runOnUiThread {
                        startActivity(Intent(this, Account::class.java))
                        finish()
                    }
                }
            }
        }

        textViewSignUp.setOnClickListener{
            startActivity(Intent(this, SignUp::class.java))
            finish()
        }
    }
}