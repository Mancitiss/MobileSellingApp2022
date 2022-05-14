package org.duckdns.mancitiss.testapplication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.widget.doAfterTextChanged
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import org.duckdns.mancitiss.testapplication.adapter.CategoryAdapter
import org.duckdns.mancitiss.testapplication.adapter.FoodAdapter
import org.duckdns.mancitiss.testapplication.entities.Categories
import org.duckdns.mancitiss.testapplication.entities.Foods
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.concurrent.thread


class MainActivity : AppCompatActivity() {
    companion object{

        private var arrCategory = ArrayList<Categories>()
        private var categoryAdapter = CategoryAdapter()

        private var arrNewestFood = ArrayList<Foods>()
        private var arrRecommendedFood = ArrayList<Foods>()
        private var newestFoodAdapter = FoodAdapter()
        private var recommendedFoodAdapter = FoodAdapter()

        private var newArray: ArrayList<Foods> = ArrayList<Foods>()
        private var recommendedArray: ArrayList<Foods> = ArrayList<Foods>()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val model = Models.getInstance()

        setContentView(R.layout.activity_main)
        arrCategory.add(Categories(1,"Tất cả", R.drawable.food))
        arrCategory.add(Categories(2,"Món ăn", R.drawable.drink))
        arrCategory.add(Categories(3,"Thức uống", R.drawable.chicken))
        arrCategory.add(Categories(4,"Gà rán", R.drawable.hamberger))
        arrCategory.add(Categories(5,"Thức ăn nhanh", R.drawable.noodle))
        arrCategory.add(Categories(6,"Bánh ngọt", R.drawable.cake))

        categoryAdapter.setData(arrCategory)

        rv_category.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rv_category.adapter = categoryAdapter

        //Set New Food Button
        newestFoodAdapter.setData(arrNewestFood)
        rv_suggestfood.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rv_suggestfood.adapter = newestFoodAdapter
        recommendedFoodAdapter.setData(arrRecommendedFood)
        rv_newfood.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rv_newfood.adapter = recommendedFoodAdapter

        tv_search.doAfterTextChanged {
            if (!tv_search.text.isNullOrBlank())
            {
                newArray = ArrayList<Foods>()
                recommendedArray = ArrayList<Foods>()
                for (food: Foods in arrNewestFood){
                    if (food.name.contains(tv_search.text)) newArray.add(food)
                }
                for (food: Foods in arrRecommendedFood){
                    if (food.name.contains(tv_search.text)) recommendedArray.add(food)
                }
                newestFoodAdapter.setData(newArray)
                recommendedFoodAdapter.setData(recommendedArray)
                runOnUiThread{
                    newestFoodAdapter.notifyDataSetChanged()
                    recommendedFoodAdapter.notifyDataSetChanged()
                }
            }
            else {
                newestFoodAdapter.setData(arrNewestFood)
                recommendedFoodAdapter.setData(arrRecommendedFood)
                runOnUiThread {
                    newestFoodAdapter.notifyDataSetChanged()
                    recommendedFoodAdapter.notifyDataSetChanged()
                }
            }
        }

        thread{
            load(arrNewestFood.size.toLong())
        }
    }

    fun addFood(food: Foods){
        arrNewestFood.add(food)
    }

    fun openMenu(view: View) {
        findViewById<DrawerLayout>(R.id.main_drawer_layout).openDrawer(GravityCompat.START)
    }

    fun reload(){
    }

    fun load(index: Long = 0){
        Connection.load(this, this, index)
        arrRecommendedFood = ArrayList(arrNewestFood)
        arrRecommendedFood.sortByDescending { (it.product.averageStars) }
        newestFoodAdapter.setData(arrNewestFood)
        recommendedFoodAdapter.setData(arrRecommendedFood)
        for (food: Foods in arrRecommendedFood){
            Log.d("connecting", food.id)
        }
        runOnUiThread {
            newestFoodAdapter.notifyDataSetChanged()
            recommendedFoodAdapter.notifyDataSetChanged()
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

    fun openCart(view: View) {
        startActivity(Intent(this, CartActivity::class.java))
    }

    fun openNotification(view: View) {
        startActivity(Intent(this, ReceiptActivity::class.java))
    }
}
