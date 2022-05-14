package org.duckdns.mancitiss.testapplication

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.duckdns.mancitiss.testapplication.entities.Foods
import java.io.DataInputStream
import java.io.DataOutputStream
import java.lang.reflect.Type
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
                val model = Models.getInstance()

                val ssf: SSLSocketFactory = SSLSocketFactory.getDefault() as SSLSocketFactory
                (ssf.createSocket(context.getString(R.string.serverAddress), context.resources.getInteger(R.integer.port))as SSLSocket).use {
                    val DOS = DataOutputStream(it.getOutputStream())
                    val DIS = DataInputStream(it.getInputStream())
                    DOS.write(Tools.combine("0005".toByteArray(StandardCharsets.UTF_16LE), Tools.data_with_ASCII_byte(index.toString()).toByteArray(StandardCharsets.US_ASCII)))
                    when (val command = Tools.receive_unicode(DIS, 8)){
                        "5555"->{
                            val json = Tools.receive_Unicode_Automatically(DIS)
                            Log.d("connecting", json)
                            val gson: Gson = Gson()
                            val productList = gson.fromJson(json, Array<Product>::class.java)
                            for (product: Product in productList){
                                try
                                {
                                    (ssf.createSocket(context.getString(R.string.serverAddress), context.resources.getInteger(R.integer.port))as SSLSocket).use{ it1 ->
                                        val dos = DataOutputStream(it1.getOutputStream())
                                        val dis = DataInputStream(it1.getInputStream())
                                        dos.write(Tools.combine("1412".toByteArray(StandardCharsets.UTF_16LE), Tools.data_with_ASCII_byte(product.id).toByteArray(StandardCharsets.US_ASCII)))
                                        val avtStr = Tools.receive_ASCII_Automatically(dis)
                                        product.avatar = avtStr
                                    }

                                }
                                catch (e: Exception){
                                    Log.d("connecting", e.stackTraceToString())
                                }
                                if (product.avatar.isNotBlank())
                                    product.image = Tools.BASE64ToImage(product.avatar)
                                if (product.ratingCount != 0L) product.averageStars = product.stars*1.0f / product.ratingCount
                                else product.averageStars = 0f
                                model.getKnownProducts()[product.id] = product
                                activity.runOnUiThread{
                                    Log.d("connecting", "insert product")
                                    activity.addFood(Foods(product))
                                    Log.d("connecting", "insert product finished" + product.id)
                                }
                            }
                        }
                        else ->{
                            Log.d("connecting", command)
                        }
                    }
                }
            }
            catch(e: Exception){
                Log.d("connecting", e.toString())
            }
        }
    }
}