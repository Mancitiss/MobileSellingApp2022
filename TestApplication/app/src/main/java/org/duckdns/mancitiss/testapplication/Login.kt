package org.duckdns.mancitiss.testapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_sign_up.*

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        backLogin.setOnClickListener{
            finish()
        }
        buttonLogIn.setOnClickListener{
            val username: String = editTextUsername.text.toString()
            val pw: String = editTextPassword.text.toString()
            if (Connection.LogIn(this, this, username, pw)){
                startActivity(Intent(this, Account::class.java))
                finish()
            }
        }

        buttonSignUp.setOnClickListener{
            startActivity(Intent(this, SignUp::class.java))
            finish()
        }
    }
}