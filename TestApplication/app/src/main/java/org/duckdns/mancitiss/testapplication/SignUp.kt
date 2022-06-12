package org.duckdns.mancitiss.testapplication

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlin.concurrent.thread

class SignUp : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        textViewLogin.setOnClickListener{
            startActivity(Intent(this, Login::class.java))
            finish()
        }

        buttonSignUp.setOnClickListener{
            thread {
                val email = editTextEmail.text.toString()
                val username = editTextUsername2.text.toString()
                val pw = editTextPassword2.text.toString()
                val rpw = editTextConfirmPassword.text.toString()

                if (email.isNotBlank() && username.isNotBlank() && pw.isNotBlank() && rpw.isNotBlank() && email.isEmailValid() && pw == rpw) {
                    if (Connection.SignUp(this, this, username, email, pw)) {
                        if (Connection.LogIn(this, this, username, pw)) {
                            // display account activity, finish this
                            runOnUiThread {
                                val intent = Intent(this@SignUp, Account::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                startActivity(intent)
                                finish()
                            }
                        } else {
                            // display login activity, finish this
                            runOnUiThread {
                                val intent = Intent(this@SignUp, Login::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                startActivity(intent)
                                finish()
                            }
                        }
                    } else {
                        runOnUiThread {
                            // inform user username/password may be wrong
                        }
                    }
                } else {
                    runOnUiThread {
                        // inform user to re-check all information
                    }
                }
            }
        }
    }

    private fun String.isEmailValid(): Boolean {
        return !TextUtils.isEmpty(this) && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
    }
}