package org.duckdns.mancitiss.testapplication

import android.content.Context
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.GravityCompat
import androidx.core.widget.TextViewCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.gson.Gson
import java.io.DataInputStream
import java.io.DataOutputStream
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicReference
import javax.net.ssl.SSLSocket
import javax.net.ssl.SSLSocketFactory
import kotlin.concurrent.thread


class MainActivity : AppCompatActivity() {
    companion object{
        var pref: SharedPreferences? = null

        val ssf: SSLSocketFactory = SSLSocketFactory.getDefault() as SSLSocketFactory
        val gson: Gson = Gson()

        var backStack = Stack<Int>()
        var knownProducts = ConcurrentHashMap<String, Product>()
        var items = mutableListOf<Product>()
        var executor: ExecutorService = Executors.newCachedThreadPool()
        //var exec: ScheduledThreadPoolExecutor = ScheduledThreadPoolExecutor(3)
        var user: User? = null
        var workerAdded: AtomicReference<Int> = AtomicReference<Int>(0)
        var commands = ConcurrentLinkedQueue<ByteArray>()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pref = getPreferences(
            Context.MODE_PRIVATE)
        setContentView(R.layout.loading)
        thread{
            connectToServer()
        }
        // from now on there will be another thread (the above thread) that runs synchronously with
        // this main thread
        setContentView(R.layout.activity_main)
        //items.add(R.id.item_1)
    }
    fun addItem(product: Product) {
        val dynamicCardViewId = View.generateViewId()
        val imageViewId = View.generateViewId()
        product.cardViewID = dynamicCardViewId
        product.imageViewID = imageViewId
        val constraintLayout = findViewById<ConstraintLayout>(R.id.ScrollMenuLayout)
        val dynamicCardView = CardView(this)
        dynamicCardView.id = product.cardViewID!!
        product.cardViewID = dynamicCardView.id
        dynamicCardView.layoutParams = ViewGroup.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT)
        constraintLayout.addView(dynamicCardView)

        var constraintSet = ConstraintSet()
        constraintSet.clone(constraintLayout)
        if (items.size > 0){
            constraintSet.connect(
                dynamicCardView.id,
                ConstraintSet.TOP,
                items.get(items.size-1).cardViewID!!,
                ConstraintSet.BOTTOM,
                resources.getDimension(R.dimen.cardView_margin_top).toInt()
            )
        } else {
            constraintSet.connect(
                dynamicCardView.id,
                ConstraintSet.TOP,
                R.id.Category,
                ConstraintSet.BOTTOM,
                resources.getDimension(R.dimen.cardView_margin_top).toInt()
            )
        }
        constraintSet.connect(
            dynamicCardView.id,
            ConstraintSet.START,
            R.id.ScrollMenuLayout,
            ConstraintSet.START,
            resources.getDimension(R.dimen.cardView_margin_start).toInt()
        )
        constraintSet.connect(
            dynamicCardView.id,
            ConstraintSet.END,
            R.id.ScrollMenuLayout,
            ConstraintSet.END,
            resources.getDimension(R.dimen.cardView_margin_end).toInt()
        )
        constraintSet.applyTo(constraintLayout)
        items.add(product)
        // add layout to cardView
        val dynamicLayout = ConstraintLayout(this)
        dynamicLayout.id = View.generateViewId()
        dynamicLayout.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dynamicCardView.addView(dynamicLayout)

        // add image, texts to the layout created above
        val imageView = ImageView(this)
        imageView.id = product.imageViewID!!
        imageView.layoutParams = ViewGroup.LayoutParams(
            resources.getDimension(R.dimen.item_image_size_width).toInt(),
            resources.getDimension(R.dimen.item_image_size_height).toInt()
        )
        imageView.setImageBitmap(product.image)
        dynamicLayout.addView(imageView)

        constraintSet = ConstraintSet()
        constraintSet.clone(dynamicLayout)
        constraintSet.connect(
            imageView.id,
            ConstraintSet.START,
            dynamicLayout.id,
            ConstraintSet.START,
            resources.getDimension(R.dimen.item_image_margin_start).toInt()
        )
        constraintSet.connect(
            imageView.id,
            ConstraintSet.TOP,
            dynamicLayout.id,
            ConstraintSet.TOP,
            resources.getDimension(R.dimen.item_image_margin_top).toInt()
        )
        constraintSet.applyTo(dynamicLayout)



        val nameView = TextView(this)
        nameView.id = View.generateViewId()
        nameView.text = product.name
        TextViewCompat.setTextAppearance(nameView, androidx.appcompat.R.style.TextAppearance_AppCompat_Large)
        nameView.layoutParams = ViewGroup.LayoutParams(
            resources.getDimension(R.dimen.item_title_width).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dynamicLayout.addView(nameView)

        constraintSet = ConstraintSet()
        constraintSet.clone(dynamicLayout)
        constraintSet.connect(
            nameView.id,
            ConstraintSet.TOP,
            dynamicLayout.id,
            ConstraintSet.TOP,
            resources.getDimension(R.dimen.item_title_margin_top).toInt()
        )
        constraintSet.connect(
            nameView.id,
            ConstraintSet.START,
            imageView.id,
            ConstraintSet.END,
            resources.getDimension(R.dimen.item_title_margin_start).toInt()
        )
        constraintSet.connect(
            nameView.id,
            ConstraintSet.END,
            dynamicLayout.id,
            ConstraintSet.END,
            resources.getDimension(R.dimen.item_title_margin_end).toInt()
        )
        constraintSet.applyTo(dynamicLayout)



        val desView = TextView(this)
        desView.id = View.generateViewId()
        desView.text = product.short_description
        TextViewCompat.setTextAppearance(desView, androidx.appcompat.R.style.TextAppearance_AppCompat_Medium)
        desView.layoutParams = ViewGroup.LayoutParams(
            resources.getDimension(R.dimen.item_description_width).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dynamicLayout.addView(desView)

        constraintSet = ConstraintSet()
        constraintSet.clone(dynamicLayout)
        constraintSet.connect(
            desView.id,
            ConstraintSet.TOP,
            nameView.id,
            ConstraintSet.BOTTOM,
            resources.getDimension(R.dimen.item_description_margin_top).toInt()
        )
        constraintSet.connect(
            desView.id,
            ConstraintSet.START,
            imageView.id,
            ConstraintSet.END,
            resources.getDimension(R.dimen.item_description_margin_start).toInt()
        )
        constraintSet.connect(
            desView.id,
            ConstraintSet.END,
            dynamicLayout.id,
            ConstraintSet.END,
            resources.getDimension(R.dimen.item_description_margin_end).toInt()
        )
        constraintSet.applyTo(dynamicLayout)
    }

    fun reload(){
        val constraintLayout = findViewById<ConstraintLayout>(R.id.ScrollMenuLayout)
        val count = constraintLayout.getChildCount() - 1
        if (count > 0) {constraintLayout.removeViews(1, count)}
        items = mutableListOf<Product>()
    }

    fun load(index: Int = 0){
        user!!.DOS!!.write(Tools.combine(("0003").toByteArray(StandardCharsets.UTF_16LE), Tools.data_with_ASCII_byte(index.toString()).toByteArray(StandardCharsets.US_ASCII)))
        ping()
    }

    fun goTo(view: View) {
        when((view as TextView).text){
            getString(R.string.navigation_label_main)->{
                val view: View = findViewById(android.R.id.content)
                backStack.push(view.id)
                findViewById<DrawerLayout>(R.id.main_drawer_layout).closeDrawers()
                setContentView(R.layout.activity_main)
                load()
            }
            getString(R.string.navigation_label_account)->{
                val view: View = findViewById(android.R.id.content)
                backStack.push(view.id)
                findViewById<DrawerLayout>(R.id.main_drawer_layout).closeDrawers()
                setContentView(R.layout.activity_login)
                // prepare to show login
                preShowLogin()
            }
        }
    }

    fun goBack(view: View){
        setContentView(backStack.pop())
    }

    private fun preShowLogin(){
        val ss = SpannableString(getString(R.string.sign_up))
        val clickableSpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(textView: View) {
                setContentView(R.layout.activity_sign_up)
                preShowSignUp()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
            }
        }
        ss.setSpan(clickableSpan, 0, ss.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        val textViewSignUp: TextView = findViewById(R.id.textViewSignUp)
        textViewSignUp.setText(ss)
        textViewSignUp.setMovementMethod(LinkMovementMethod.getInstance())
        textViewSignUp.setHighlightColor(Color.TRANSPARENT)
    }

    private fun preShowSignUp() {
        val ss = SpannableString(getString(R.string.login))
        val clickableSpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(textView: View) {
                setContentView(R.layout.activity_login)
                preShowLogin()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
            }
        }
        ss.setSpan(clickableSpan, 0, ss.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        val textViewLogin: TextView = findViewById(R.id.textViewLogin)
        textViewLogin.setText(ss)
        textViewLogin.setMovementMethod(LinkMovementMethod.getInstance());
        textViewLogin.setHighlightColor(Color.TRANSPARENT)
    }

    fun openMenu(view: View) {
        findViewById<DrawerLayout>(R.id.main_drawer_layout).openDrawer(GravityCompat.START)
    }

    private fun connectToServer(){
        Log.d("connecting", "Connecting to server")

        val ss: SSLSocket = ssf.createSocket(getString(R.string.serverAddress), resources.getInteger(R.integer.port)) as SSLSocket
        Log.d("connecting", "Successfully Connected!")
        val DOS = DataOutputStream(ss.outputStream)
        val DIS = DataInputStream(ss.inputStream)
        val user = User()
        user.DIS = DIS
        user.DOS = DOS
        user.username = pref!!.getString("username", null)
        user.phone = pref!!.getString("phone", null)
        user.email = pref!!.getString("email", null)
        MainActivity.user = user
        thread{
            clientLoop()
        }
        val token = pref!!.getString("token", null)
        if (token == "" || token == null) {
            // request server for a new token
            DOS.write("0002".toByteArray(StandardCharsets.UTF_16LE))
        } else {
            //get existing token from preferences
            DOS.write(
                Tools.combine(
                    ("0001").toByteArray(
                        StandardCharsets.UTF_16LE
                    ),
                    token.toByteArray(
                        StandardCharsets.US_ASCII)
                )
            )
        }
    }

    private fun clientLoop(){
        try{
            val s = user!!.DIS!!
            var keepReading = true
            do{
                try{
                    // read data from s and process here, in this try block only, do not go outside
                    val instruction = Tools.receive_unicode(s, 8)
                    Log.d("connecting", instruction)
                    when (instruction) {
                        "0001" -> { load() }
                        "0002" -> {
                            val newToken = Tools.receiveASCII(s, 32)
                            val editor: SharedPreferences.Editor = pref!!.edit()
                            editor.putString("token", newToken)
                            editor.apply()
                            load()
                        }
                        "0003" -> {
                            val json = Tools.receive_Unicode_Automatically(s)
                            val products: List<Product> = gson.fromJson(json, Array<Product>::class.java).toList()
                            for (product in products){
                                knownProducts.put(product.id!!, product)
                            }
                            for (product in products){
                                runOnUiThread {
                                    // Stuff that updates the UI
                                    addItem(product)
                                }
                                thread{
                                    try{
                                        ssf.createSocket(getString(R.string.serverAddress), resources.getInteger(R.integer.port)).use {
                                            val DOS = DataOutputStream(it.getOutputStream())
                                            val DIS = DataInputStream(it.getInputStream())
                                            DOS.write(Tools.combine("1412".toByteArray(StandardCharsets.UTF_16LE), Tools.data_with_ASCII_byte(product.id).toByteArray(StandardCharsets.US_ASCII)))
                                            val base64 = Tools.receive_ASCII_Automatically(DIS)
                                            val bitmapbyte: ByteArray = Base64.decode(base64.toByteArray(), Base64.DEFAULT)
                                            val bitmap = BitmapFactory.decodeByteArray(
                                                bitmapbyte,
                                                0,
                                                bitmapbyte.size
                                            )
                                            Log.d("connecting", "bitmap finished")
                                            bitmap?.let {
                                                Log.d("connecting", "bitmap exists")
                                                product.image = it
                                                runOnUiThread {
                                                    var done = false
                                                    while (!done) {
                                                        product.imageViewID?.let {
                                                            val imageView =
                                                                findViewById<ImageView>(it)
                                                            imageView.setImageBitmap(product.image)
                                                            done = true;
                                                            Log.d("connecting", "imageview updated")
                                                        }
                                                    }
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
                    }
                }
                catch (e: Exception){
                    Log.d("connecting", e.message!!)
                    keepReading = false
                }
            } while (keepReading)
        }
        catch (e: Exception){
            Log.d("connecting", e.message!!)
        }
    }

    fun queueCommand(command: ByteArray){
        commands.add(command)
        ping()
        addWorkerThread()
    }

    private fun addWorkerThread(){
        if (0 == workerAdded.getAndSet(1)){
            try{
                executor.execute({@Override fun run(){ sendCommands() } })
            }
            catch (e: Exception){
                Log.d("connecting", e.message!!)
                try{
                    workerAdded.set(0)
                }
                catch(se: Exception){}
            }
        }
    }

    private fun sendCommands(){
        try{
            while(!commands.isEmpty()){
                val command = commands.poll()
                user!!.DOS!!.write(command)
                ping()
            }
            ping()
            workerAdded.set(0)
        }
        catch(e: Exception){
            Log.d("connecting", e.message!!)
        }
    }

    private fun ping() {
        try{
            ssf.createSocket(getString(R.string.serverAddress), resources.getInteger(R.integer.port)).use {
                val DOS = DataOutputStream(it.getOutputStream())
                DOS.write(Tools.combine("0012".toByteArray(StandardCharsets.UTF_16LE), pref!!.getString("token", "00000000000000000000000000000000")!!.toByteArray(StandardCharsets.US_ASCII)))
            }
        }
        catch(e: Exception){
            Log.d("connecting", e.message!!)
        }
    }
}
