package org.duckdns.mancitiss.testapplication

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager.getDefaultSharedPreferences
import android.util.Log
import android.view.View
import android.widget.EditText
import com.google.gson.Gson
import org.duckdns.mancitiss.testapplication.entities.Foods
import org.duckdns.mancitiss.testapplication.entities.UserInfo
import java.io.DataInputStream
import java.io.DataOutputStream
import java.nio.charset.StandardCharsets
import javax.net.ssl.SSLSocket
import javax.net.ssl.SSLSocketFactory
import kotlin.concurrent.thread


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
                var pref: SharedPreferences = activity.getSharedPreferences("preferences", Context.MODE_PRIVATE)
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

        fun isLoggedIn(activity: Activity, context: Context) : Boolean{
            try{
                val pref: SharedPreferences = activity.getSharedPreferences("preferences", Context.MODE_PRIVATE)
                val model = Models.getInstance()

                var username = pref!!.getString("username", null)
                var phone = pref!!.getString("phone", null)
                var email = pref!!.getString("email", null)
                var token = pref!!.getString("token", null)
                var newToken = ""
                if (token != null && token.isNotBlank()){
                     newToken = UpdateToken(activity, context, token)
                }
                else {
                    newToken = UpdateToken(activity, context, "00000000000000000000000000000000000000000000000000")
                }
                val editor: SharedPreferences.Editor = pref!!.edit()
                editor.putString("token", newToken)
                editor.commit()
                if (username!=null && email!=null){
                    return true
                }
                return false
            }
            catch(e: Exception){
                Log.d("connecting", e.stackTraceToString())
                return false
            }
        }

        private fun UpdateToken(activity: Activity, context:Context, token: String): String{
            try {
                val pref: SharedPreferences = activity.getSharedPreferences("preferences", Context.MODE_PRIVATE)
                val ssf: SSLSocketFactory = SSLSocketFactory.getDefault() as SSLSocketFactory
                var newToken = ""
                (ssf.createSocket(
                    context.getString(R.string.serverAddress),
                    context.resources.getInteger(R.integer.port)
                ) as SSLSocket).use {
                    val DOS = DataOutputStream(it.getOutputStream())
                    val DIS = DataInputStream(it.getInputStream())
                    DOS.write(Tools.combine("0001".toByteArray(StandardCharsets.UTF_16LE), (token).toByteArray(StandardCharsets.US_ASCII)))
                    when(val instruction = Tools.receive_unicode(DIS, 8)){
                        "0002"->{
                            newToken = Tools.receiveASCII(DIS, 32)
                            val editor: SharedPreferences.Editor = pref!!.edit()

                            editor.putString("token", newToken)
                            editor.commit()
                            Log.d("connecting", "new token received: " + pref.getString("token", ""))
                        }
                        "0004"->{
                            newToken = Tools.receiveASCII(DIS, 32)
                            val editor: SharedPreferences.Editor = pref!!.edit()
                            editor.putString("token", newToken)
                            editor.putString("username", null)
                            editor.putString("phone", null)
                            editor.putString("email", null)
                            editor.commit()
                            Log.d("connecting", "new token received, reset")
                        }
                        "0404"->{
                            newToken = Tools.receiveASCII(DIS, 32)
                            val editor: SharedPreferences.Editor = pref!!.edit()
                            editor.putString("token", newToken)
                            editor.putString("username", null)
                            editor.putString("phone", null)
                            editor.putString("email", null)
                            editor.commit()
                            Log.d("connecting", "new token received, reset (old token not found)")
                        }
                        else -> {
                            Log.d("connecting", "weird message received $instruction")
                        }
                    }
                }
                return newToken
            }
            catch(e: Exception){
                Log.d("connecting", e.stackTraceToString())
                return token
            }
        }

        fun ChangePassword(activity: Activity, context: Context, opw: String, npw: String): Boolean{
            try{
                val pref: SharedPreferences = activity.getSharedPreferences("preferences", Context.MODE_PRIVATE)
                val ssf: SSLSocketFactory = SSLSocketFactory.getDefault() as SSLSocketFactory
                val username = pref!!.getString("username", null)
                if (username != null){
                    (ssf.createSocket(
                        context.getString(R.string.serverAddress),
                        context.resources.getInteger(R.integer.port)
                    ) as SSLSocket).use {
                        val DOS = DataOutputStream(it.getOutputStream())
                        val DIS = DataInputStream(it.getInputStream())
                        DOS.write(("0013"+Tools.data_with_unicode_byte(opw)+Tools.data_with_unicode_byte(npw)+Tools.data_with_unicode_byte(username)).toByteArray(StandardCharsets.UTF_16LE))
                        when(Tools.receive_unicode(DIS, 8)){
                            "0013"->{
                                Log.d("connecting", "true")
                                return true
                            }
                            "0014"->{
                                Log.d("connecting", "false")
                                return false
                            }
                            else -> {
                                Log.d("connecting", "false")
                                return false
                            }
                        }
                    }
                }
                else {
                    // user didn't log in, how come?
                    Log.d("connecting", "false")
                    return false
                }
            }
            catch(e: Exception){
                Log.d("connecting", e.stackTraceToString())
                return false
            }
        }

        fun ChangeInfo(activity: Activity, context: Context, info: UserInfo) : Boolean {
            try {
                val pref: SharedPreferences =
                    activity.getSharedPreferences("preferences", Context.MODE_PRIVATE)
                val ssf: SSLSocketFactory = SSLSocketFactory.getDefault() as SSLSocketFactory
                (ssf.createSocket(
                    context.getString(R.string.serverAddress),
                    context.resources.getInteger(R.integer.port)
                ) as SSLSocket).use {
                    val DOS = DataOutputStream(it.getOutputStream())
                    val DIS = DataInputStream(it.getInputStream())
                    val token: String? = pref.getString("token", null)
                    val username: String? = pref.getString("username", null)
                    if (!token.isNullOrBlank() && !username.isNullOrBlank()) {
                        val gson: Gson = Gson()
                        val json = gson.toJson(info, UserInfo::class.java)
                        DOS.write(
                            Tools.combine(
                                "0014".toByteArray(StandardCharsets.UTF_16LE),
                                Tools.data_with_ASCII_byte(token)
                                    .toByteArray(StandardCharsets.US_ASCII),
                                Tools.data_with_unicode_byte(username)
                                    .toByteArray(StandardCharsets.UTF_16LE),
                                Tools.data_with_unicode_byte(json)
                                    .toByteArray(StandardCharsets.UTF_16LE)
                            )
                        )
                        when (Tools.receive_unicode(DIS, 8)) {
                            "0014" -> {
                                val editor: SharedPreferences.Editor = pref!!.edit()
                                editor.putString("name", info.name)
                                editor.putString("phone", info.phone)
                                editor.putString("email", info.email)
                                editor.commit()
                                Log.d("connecting", "true")
                                return true
                            }
                            else -> {
                                Log.d("connecting", "false")
                                return false
                            }
                        }
                    }
                    Log.d("connecting", "false")
                    return false
                }
            } catch (e: Exception) {
                Log.d("connecting", e.stackTraceToString())
                return false
            }

        }

        fun SignUp(activity: Activity, context: Context, username: String, email: String, pw: String): Boolean{
            try {
                var signUpInfo: SignUpInfo = SignUpInfo()
                signUpInfo.username = username
                signUpInfo.email = email
                signUpInfo.pw = pw
                val gson: Gson = Gson()
                val json: String = gson.toJson(signUpInfo, SignUpInfo::class.java)
                val pref: SharedPreferences = activity.getSharedPreferences("preferences", Context.MODE_PRIVATE)
                val ssf: SSLSocketFactory = SSLSocketFactory.getDefault() as SSLSocketFactory
                (ssf.createSocket(
                    context.getString(R.string.serverAddress),
                    context.resources.getInteger(R.integer.port)
                ) as SSLSocket).use {
                    val DOS = DataOutputStream(it.getOutputStream())
                    val DIS = DataInputStream(it.getInputStream())
                    DOS.write(Tools.combine("0011".toByteArray(StandardCharsets.UTF_16LE), Tools.data_with_unicode_byte(json).toByteArray(StandardCharsets.UTF_16LE)))
                    Log.d("connecting", "0011 sent")
                    when(Tools.receive_unicode(DIS, 8)){
                        "0011"->{
                            Log.d("connecting", "true")
                            return true
                        }
                        else ->{
                            Log.d("connecting", "false")
                            return false
                        }
                    }
                }
            }
            catch(e: Exception){
                Log.d("connecting", e.stackTraceToString())
                return false
            }
        }
        fun LogIn(activity: Activity, context: Context, username: String, pw: String) : Boolean{
            try{
                val pref: SharedPreferences = activity.getSharedPreferences("preferences", Context.MODE_PRIVATE)
                val ssf: SSLSocketFactory = SSLSocketFactory.getDefault() as SSLSocketFactory
                (ssf.createSocket(
                    context.getString(R.string.serverAddress),
                    context.resources.getInteger(R.integer.port)
                ) as SSLSocket).use {
                    val DOS = DataOutputStream(it.getOutputStream())
                    val DIS = DataInputStream(it.getInputStream())
                    DOS.write(Tools.combine("0010".toByteArray(StandardCharsets.UTF_16LE), Tools.data_with_unicode_byte(username).toByteArray(StandardCharsets.UTF_16LE), Tools.data_with_unicode_byte(pw).toByteArray(StandardCharsets.UTF_16LE)))
                    when(val instruction = Tools.receive_unicode(DIS, 8)){
                        "0200"->{
                            val json = Tools.receive_Unicode_Automatically(DIS)
                            val strings: Array<String> = Gson().fromJson(json, Array<String>::class.java)
                            for(i in strings){
                                Log.d("connecting", i.toString())
                            }
                            Log.d("connecting","Strings received")
                            val command = Tools.receive_unicode(DIS, 8)
                            if (command == "0404"){
                                val newToken = Tools.receiveASCII(DIS, 32)
                                val editor: SharedPreferences.Editor = pref!!.edit()
                                editor.putString("token", newToken)
                                editor.putString("username", username)
                                editor.putString("name", strings[0])
                                editor.putString("email", strings[1])
                                editor.putString("phone", strings[2])
                                editor.putString("address", strings[3])
                                editor.commit()
                                Log.d("connecting", "true")
                                return true
                            }
                            Log.d("connecting", "false")
                            return false
                        }
                        "0404"->{
                            val newToken = Tools.receiveASCII(DIS, 32)
                            Log.d("connecting", "token received")
                            val command = Tools.receive_unicode(DIS, 8)
                            if (command == "0200"){
                                val json = Tools.receive_Unicode_Automatically(DIS)
                                Log.d("connecting", json)
                                val strings: Array<String> = Gson().fromJson(json, Array<String>::class.java)
                                val editor: SharedPreferences.Editor = pref!!.edit()
                                editor.putString("token", newToken)
                                editor.putString("username", username)
                                editor.putString("name", strings[0])
                                editor.putString("email", strings[1])
                                editor.putString("phone", strings[2])
                                editor.putString("address", strings[3])
                                editor.commit()
                                Log.d("connecting", "true")
                                return true
                            }
                            else return false
                        }
                        else ->{
                            Log.d("connecting", "false $instruction")
                            return false
                        }
                    }
                }
            }
            catch(e: Exception){
                Log.d("connecting", e.stackTraceToString())
                return false
            }
        }

        fun Logout(activity: Activity, context: Context){
            try{
                val pref: SharedPreferences = activity.getSharedPreferences("preferences", Context.MODE_PRIVATE)
                val editor: SharedPreferences.Editor = pref!!.edit()
                editor.putString("token", null)
                editor.putString("username", null)
                editor.putString("name", null)
                editor.putString("phone", null)
                editor.putString("email", null)
                editor.commit()
            }
            catch(e: Exception){
                Log.d("connecting", e.stackTraceToString())
            }
        }

        fun CheckVisa(activity: Activity, context: Context, visa: String, CVV: String): Boolean{
            // check if visa and CVV are numbers
            if(visa.matches("[0-9]+".toRegex()) && CVV.matches("[0-9]+".toRegex())){
                return true
            }
            return false
        }

        fun PlaceOrderWithAccount(activity: Activity, context: Context): Boolean{
            try {
                Log.d("connecting", "starting to place order")
                val pref: SharedPreferences =
                    activity.getSharedPreferences("preferences", Context.MODE_PRIVATE)
                val token = pref.getString("token", "00000000000000000000000000000000")
                val ssf: SSLSocketFactory = SSLSocketFactory.getDefault() as SSLSocketFactory
                (ssf.createSocket(
                    context.getString(R.string.serverAddress),
                    context.resources.getInteger(R.integer.port)
                ) as SSLSocket).use {
                    val DOS = DataOutputStream(it.getOutputStream())
                    val DIS = DataInputStream(it.getInputStream())
                    val order: Order = Order()
                    order.name = Models.getInstance().currentname
                    order.phoneNumber = Models.getInstance().currentphone
                    order.address = Models.getInstance().currentaddress
                    order.items = Models.getInstance().shoppingCart
                    val gson = Gson()
                    val json = gson.toJson(order, Order::class.java)
                    Log.d("connecting", json)
                    DOS.write(Tools.combine("2610".toByteArray(StandardCharsets.UTF_16LE), token!!.toByteArray(StandardCharsets.US_ASCII), Tools.data_with_unicode_byte(json).toByteArray(StandardCharsets.UTF_16LE)))
                    when(val instruction = Tools.receive_unicode(DIS, 8)){
                        "2611"->{
                            val id = Tools.receiveASCII(DIS, 21)
                            val total = Tools.receive_ASCII_Automatically(DIS).toLong()
                            Log.d("connecting", "order placed" + id)
                            val itemList = ArrayList<Item>()
                            for(i in Models.getInstance().shoppingCart){
                                itemList.add(Item(i.key, i.value, "Đang chờ xử lý"))
                            }
                            val donHang = DonHang(id, order.address, order.phoneNumber, order.name, total, itemList.toArray(arrayOf<Item>()))
                            Models.getInstance().knownOrders[id] = donHang
                            return true
                        }
                        else -> {
                            Log.d("connecting", "order not placed")
                            return false
                        }
                    }
                }
            }
            catch(e: Exception){
                Log.d("connecting", e.stackTraceToString())
                return false
            }
        }

        fun getKnownOrders(activity:Activity, context: Context){
            Log.d("connecting", "starting to place order")
            val pref: SharedPreferences =
                activity.getSharedPreferences("preferences", Context.MODE_PRIVATE)
            val token = pref.getString("token", "00000000000000000000000000000000")
            val ssf: SSLSocketFactory = SSLSocketFactory.getDefault() as SSLSocketFactory
            (ssf.createSocket(
                context.getString(R.string.serverAddress),
                context.resources.getInteger(R.integer.port)
            ) as SSLSocket).use {
                val DOS = DataOutputStream(it.getOutputStream())
                val DIS = DataInputStream(it.getInputStream())
                DOS.write(Tools.combine("6969".toByteArray(StandardCharsets.UTF_16LE), token!!.toByteArray(StandardCharsets.US_ASCII)))
                when (val instruction = Tools.receive_unicode(DIS, 8)) {
                    "6969" -> {
                        val json = Tools.receive_Unicode_Automatically(DIS)
                        Log.d("connecting", json)
                        val orderList = Gson().fromJson(json, Array<DonHang>::class.java)
                        for (i in orderList) {
                            Models.getInstance().knownOrders[i.id] = i
                            Log.d("connecting", i.getItemList().get(0).idsp)
                        }
                    }
                    else -> {
                        Log.d("connecting", "unknown error in get known orders")
                    }
                }
            }
        }
    }
}