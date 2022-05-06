package org.duckdns.mancitiss.testapplication

import android.content.Context
import android.content.Intent
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import org.duckdns.mancitiss.testapplication.adapter.CategoryAdapter
import org.duckdns.mancitiss.testapplication.adapter.FoodAdapter
import org.duckdns.mancitiss.testapplication.entities.Categories
import org.duckdns.mancitiss.testapplication.entities.Foods
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

        private var arrCategory = ArrayList<Categories>()
        private var categoryAdapter = CategoryAdapter()

        private var arrFood = ArrayList<Foods>()
        private var foodAdapter = FoodAdapter()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pref = getPreferences(
            Context.MODE_PRIVATE)
        //setContentView(R.layout.loading)
        /*
        thread{
            connectToServer()
        }*/
        // from now on there will be another thread (the above thread) that runs synchronously with
        // this main thread
        setContentView(R.layout.activity_main)
        //items.add(R.id.item_1)

        //Set Category Button
        arrCategory.add(Categories(1,"Món ăn", R.drawable.food))
        arrCategory.add(Categories(2,"Thức uống", R.drawable.drink))
        arrCategory.add(Categories(3,"Gà rán", R.drawable.chicken))
        arrCategory.add(Categories(4,"Thức ăn nhanh", R.drawable.hamberger))
        arrCategory.add(Categories(5,"Phở/Bún", R.drawable.noodle))
        arrCategory.add(Categories(6,"Bánh ngọt", R.drawable.cake))

        categoryAdapter.setData(arrCategory)

        rv_category.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rv_category.adapter = categoryAdapter



        foodAdapter.setData(arrFood)

        //Set Suggest Food Button
        arrFood.add(Foods(1,"Kimbap", 50000F, R.drawable.food))
        arrFood.add(Foods(2,"Cơm chiên dương châu", 55000F, R.drawable.drink))
        arrFood.add(Foods(3,"Tobokki phô mai", 102000F, R.drawable.chicken))
        arrFood.add(Foods(4,"Trà sữa trân châu", 43000F, R.drawable.hamberger))
        arrFood.add(Foods(5,"Trà chanh", 12000F, R.drawable.noodle))
        arrFood.add(Foods(6,"Mì cay hải sản", 78000F, R.drawable.cake))

        rv_suggestfood.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rv_suggestfood.adapter = foodAdapter

        //Set New Food Button
        rv_newfood.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rv_newfood.adapter = foodAdapter
    }

    fun openMenu(view: View) {
        findViewById<DrawerLayout>(R.id.main_drawer_layout).openDrawer(GravityCompat.START)
    }

    fun reload(){
    }

    fun load(index: Int = 0){

    }

    fun goTo(view: View) {
        when((view as TextView).text){
            getString(R.string.navigation_label_main)->{

            }
            getString(R.string.navigation_label_account)->{
                startActivity(Intent(this, Account::class.java))
                finish()
            }
        }

    }
}
