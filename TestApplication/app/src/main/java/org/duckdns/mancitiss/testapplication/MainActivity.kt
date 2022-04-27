package org.duckdns.mancitiss.testapplication

import android.app.ActionBar
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.widget.TextViewCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.gson.Gson
import java.io.DataInputStream
import java.io.DataOutputStream
import java.math.BigDecimal
import java.nio.charset.StandardCharsets
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicReference
import javax.net.ssl.SSLSocket
import javax.net.ssl.SSLSocketFactory
import kotlin.concurrent.thread


class MainActivity : AppCompatActivity() {
    var pref: SharedPreferences? = null
    val tools = Tools()
    val ssf: SSLSocketFactory = SSLSocketFactory.getDefault() as SSLSocketFactory
    val gson: Gson = Gson()

    var knownProducts = ConcurrentHashMap<String, Product>()
    var items = mutableListOf<Int>()
    var executor: ExecutorService = Executors.newCachedThreadPool()
    var exec: ScheduledThreadPoolExecutor = ScheduledThreadPoolExecutor(3)
    var user: User? = null
    var workerAdded: AtomicReference<Int> = AtomicReference<Int>(0)
    var commands = ConcurrentLinkedQueue<ByteArray>()

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
    fun addText(text: String?){
        val constraintLayout = findViewById<ConstraintLayout>(R.id.ScrollMenuLayout)
        val dynamicTextview = TextView(this)
        dynamicTextview.text = text
        dynamicTextview.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT)
        dynamicTextview.id = View.generateViewId()
        constraintLayout.addView(dynamicTextview)
        val constraintSet = ConstraintSet()
        constraintSet.clone(constraintLayout)
        constraintSet.connect(
            dynamicTextview.id,
            ConstraintSet.LEFT,
            R.id.ScrollMenuLayout,
            ConstraintSet.RIGHT,
            resources.getDimension(R.dimen.zero_dp).toInt()
        )
        constraintSet.connect(
            dynamicTextview.id,
            ConstraintSet.TOP,
            items.get(items.size-1),
            ConstraintSet.BOTTOM,
            resources.getDimension(R.dimen.min_space).toInt()
        )
        constraintSet.applyTo(constraintLayout)
        items.add(dynamicTextview.id)
    }
    fun addItem(name: String?, description: String?, price: BigDecimal? = BigDecimal(0), count: Int? = 0, image: Bitmap? = null) {
        val constraintLayout = findViewById<ConstraintLayout>(R.id.ScrollMenuLayout)
        val dynamicCardView = CardView(this)
        dynamicCardView.id = View.generateViewId()
        dynamicCardView.layoutParams = ViewGroup.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT)
        constraintLayout.addView(dynamicCardView)

        var constraintSet = ConstraintSet()
        constraintSet.clone(constraintLayout)
        if (items.size > 0){
            constraintSet.connect(
                dynamicCardView.id,
                ConstraintSet.TOP,
                items.get(items.size-1),
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
        items.add(dynamicCardView.id)
        // add layout to cardView
        val dynamicLayout = ConstraintLayout(this)
        dynamicLayout.id = View.generateViewId()
        dynamicLayout.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dynamicCardView.addView(dynamicLayout)

        // add image, texts to the layout created above
        val imageView = ImageView(this)
        imageView.id = View.generateViewId()
        imageView.layoutParams = ViewGroup.LayoutParams(
            resources.getDimension(R.dimen.item_image_size_width).toInt(),
            resources.getDimension(R.dimen.item_image_size_height).toInt()
        )
        imageView.setImageBitmap(image)
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
        nameView.text = name
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
        desView.text = description
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

    fun remove(view: View){ // for debug only
        val constraintLayout = findViewById<ConstraintLayout>(R.id.ScrollMenuLayout)
        val count = constraintLayout.getChildCount() - 1
        if (count > 0) {constraintLayout.removeViews(1, count)}

        items = mutableListOf<Int>()
    }

    fun add(view: View){ // for debug only
        addItem("Gà nướng(?)", "Ngon nhứt nha ngon nhứt nha ngon nhứt nhaa", BigDecimal(0), 0, BitmapFactory.decodeResource(resources, R.drawable.thanksgiving_chicken_96))
        addItem("Khoai tây chiênnn", "Ngon hơn khi dùng lạnh!", BigDecimal(0), 0, BitmapFactory.decodeResource(resources, R.drawable.mcdonald_s_french_fries_96))
        addItem("Bắp nổ", "Dùng để ăn trong khi chờ đến giờ vào rạp", BigDecimal(0), 0, BitmapFactory.decodeResource(resources, R.drawable.popcorn_96))
        addItem("Boba Bola", "Bepis", BigDecimal(0), 0, BitmapFactory.decodeResource(resources, R.drawable.cola_96))
    }

    fun reset(){
        user!!.DOS!!.write(tools.combine(("0003").toByteArray(StandardCharsets.UTF_16LE), tools.data_with_ASCII_byte("0")!!.toByteArray(StandardCharsets.US_ASCII)))
        ping()
    }

    fun goTo(view: View) {
        if ((view as TextView).text == "Main"){
            findViewById<DrawerLayout>(R.id.main_drawer_layout).closeDrawers()
            setContentView(R.layout.activity_main)
            reset()
        }
    }

    fun openMenu(view: View) {
        findViewById<DrawerLayout>(R.id.main_drawer_layout).openDrawer(Gravity.LEFT)
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
        this.user = user
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
                tools.combine(
                    ("0001").toByteArray(
                        StandardCharsets.UTF_16LE
                    ),
                    token.toByteArray(
                        StandardCharsets.US_ASCII)
                )
            )
        }
    }
/*
    private fun clientLoop(){
        try{
            Log.d("connecting", "Looping")
            val user = this.user
            if (user!=null){
                val localDIS = user.DIS
                if (localDIS != null){
                    Log.d("connecting", "Reading")
                    val s: PushbackInputStream = PushbackInputStream(localDIS)
                    val b = s.read()
                    if (b == -1){
                        //Thread.sleep(100)
                    }
                    else {
                        s.unread(b)
                        receiveData(s)
                    }
                }
            }
            Log.d("connecting", "Before calling loop")
            exec.schedule( Runnable(){ @Override fun run(){ Log.d("connecting", "start looping"); clientLoop(); Log.d("connecting", "end looping");}}, 2, TimeUnit.SECONDS)
            Log.d("connecting", "after calling loop")
        }
        catch (e: Exception){
            Log.d("connecting", e.message!!)
        }
    }*/

    private fun clientLoop(){
        try{
            val s = user!!.DIS!!
            var keepReading = true
            do{
                try{
                    // read data from s and process here, in this try block only, do not go outside
                    val instruction = tools.receive_unicode(s, 8)
                    Log.d("connecting", instruction)
                    when (instruction) {
                        "0001" -> { reset() }
                        "0002" -> {
                            val newToken = tools.receiveASCII(s, 32)
                            val editor: SharedPreferences.Editor = pref!!.edit()
                            editor.putString("token", newToken)
                            editor.apply()
                            reset()
                        }
                        "0003" -> {
                            val json = tools.receive_Unicode_Automatically(s)
                            val products: List<Product> = gson.fromJson(json, Array<Product>::class.java).toList()
                            for (product in products){
                                knownProducts.put(product.id!!, product)
                            }

                            // reset menu to 0 products
                            val constraintLayout = findViewById<ConstraintLayout>(R.id.ScrollMenuLayout)
                            val count = constraintLayout.getChildCount() - 1
                            if (count > 0) {constraintLayout.removeViews(1, count)}
                            items = mutableListOf<Int>()
                            for (product in products){
                                runOnUiThread {
                                    // Stuff that updates the UI
                                    addItem(product.name, product.short_description, product.price, product.quantity)
                                }

                            }
                            /*
                            addItem("Gà nướng(?)", "Ngon nhứt nha ngon nhứt nha ngon nhứt nhaa", 0, 0, BitmapFactory.decodeResource(resources, R.drawable.thanksgiving_chicken_96))
                            addItem("Khoai tây chiênnn", "Ngon hơn khi dùng lạnh!", 0, 0, BitmapFactory.decodeResource(resources, R.drawable.mcdonald_s_french_fries_96))
                            addItem("Bắp nổ", "Dùng để ăn trong khi chờ đến giờ vào rạp", 0, 0, BitmapFactory.decodeResource(resources, R.drawable.popcorn_96))
                            addItem("Boba Bola", "Bepis", 0, 0, BitmapFactory.decodeResource(resources, R.drawable.cola_96))
                             */
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
                executor.execute(Runnable(){@Override fun run(){ sendCommands() } })
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
                DOS.write(tools.combine("0012".toByteArray(StandardCharsets.UTF_16LE), pref!!.getString("token", "00000000000000000000000000000000")!!.toByteArray(StandardCharsets.US_ASCII)))
            }
        }
        catch(e: Exception){
            Log.d("connecting", e.message!!)
        }
    }
}
