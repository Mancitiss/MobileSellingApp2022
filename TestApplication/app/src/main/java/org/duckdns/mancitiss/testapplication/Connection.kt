package org.duckdns.mancitiss.testapplication

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import java.io.DataInputStream
import java.io.DataOutputStream
import java.nio.charset.StandardCharsets
import javax.net.ssl.SSLSocket
import javax.net.ssl.SSLSocketFactory


/*   how to set preferences
    val editor: SharedPreferences.Editor = pref!!.edit()
    editor.putString("token", newToken)
    editor.apply()
    load()

     how to get preferences
    user.username = pref!!.getString("username", null)
    user.phone = pref!!.getString("phone", null)
    user.email = pref!!.getString("email", null)
 */


class Connection{
    companion object{
        fun load(activity: MainActivity, context: Context, index: Long = 0) {
            try{
                var pref: SharedPreferences = activity.getPreferences(Context.MODE_PRIVATE)

                val ssf: SSLSocketFactory = SSLSocketFactory.getDefault() as SSLSocketFactory
                (ssf.createSocket(context.getString(R.string.serverAddress), context.resources.getInteger(R.integer.port))as SSLSocket).use {
                    val DOS = DataOutputStream(it.getOutputStream())
                    val DIS = DataInputStream(it.getInputStream())
                    DOS.write(Tools.combine("0003".toByteArray(StandardCharsets.UTF_16LE), Tools.data_with_ASCII_byte(index.toString()).toByteArray(StandardCharsets.US_ASCII)))
                    val command = Tools.receive_unicode(DIS, 8);
                    when (command){
                        "0003"->{
                            val json = Tools.receive_Unicode_Automatically(DIS)
                            val gson: Gson = Gson()
                            val productList = gson.fromJson(json, Array<Product>::class.java)
                        }
                        else ->{

                        }
                    }
                }
            }
            catch(e: Exception){
                Log.d("connecting", e.message!!)
            }
        }
    }
}