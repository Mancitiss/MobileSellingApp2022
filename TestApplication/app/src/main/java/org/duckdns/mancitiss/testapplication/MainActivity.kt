package org.duckdns.mancitiss.testapplication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import org.duckdns.mancitiss.testapplication.adapter.CategoryAdapter
import org.duckdns.mancitiss.testapplication.adapter.FoodAdapter
import org.duckdns.mancitiss.testapplication.entities.Categories
import org.duckdns.mancitiss.testapplication.entities.Foods
import java.util.*
import kotlin.concurrent.thread


class MainActivity : AppCompatActivity() {
    companion object{

        private var arrCategory = ArrayList<Categories>()
        private var categoryAdapter = CategoryAdapter()

        private var arrFood = ArrayList<Foods>()
        private var foodAdapter = FoodAdapter()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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
        /*
        arrFood.add(Foods(1,"Kimbap", 50000F, R.drawable.food))
        arrFood.add(Foods(2,"Cơm chiên dương châu", 55000F, R.drawable.drink))
        arrFood.add(Foods(3,"Tobokki phô mai", 102000F, R.drawable.chicken))
        arrFood.add(Foods(4,"Trà sữa trân châu", 43000F, R.drawable.hamberger))
        arrFood.add(Foods(5,"Trà chanh", 12000F, R.drawable.noodle))
        arrFood.add(Foods(6,"Mì cay hải sản", 78000F, R.drawable.cake))
        */
        /*
        rv_suggestfood.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rv_suggestfood.adapter = foodAdapter

        //Set New Food Button
        rv_newfood.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rv_newfood.adapter = foodAdapter
        */
        thread{
            load(arrFood.size.toLong())
        }
    }

    fun addFood(food: Foods){
        arrFood.add(food)
    }

    fun openMenu(view: View) {
        findViewById<DrawerLayout>(R.id.main_drawer_layout).openDrawer(GravityCompat.START)
    }

    fun reload(){
    }

    fun load(index: Long = 0){
        Connection.load(this, this, index)
        runOnUiThread{
            rv_suggestfood.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            rv_suggestfood.adapter = foodAdapter

            //Set New Food Button
            rv_newfood.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            rv_newfood.adapter = foodAdapter
        }
    }

    fun goTo(view: View) {
        when((view as TextView).text){
            getString(R.string.navigation_label_main)->{
                findViewById<DrawerLayout>(R.id.main_drawer_layout).closeDrawers()
            }
            getString(R.string.navigation_label_account)->{
                startActivity(Intent(this, Account::class.java))
                finish()
            }
        }

    }
}
