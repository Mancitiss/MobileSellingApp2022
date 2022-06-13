package org.duckdns.mancitiss.testapplication

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
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
import kotlin.concurrent.thread


class MainActivity : AppCompatActivity() {
    //companion object {

        private var arrCategory = ArrayList<Categories>()
        private var categoryAdapter = CategoryAdapter()

        private var arrNewestFood = ArrayList<Foods>()
        private var arrRecommendedFood = ArrayList<Foods>()
        private var newestFoodAdapter = FoodAdapter()
        private var recommendedFoodAdapter = FoodAdapter()

        private var newArray: ArrayList<Foods> = ArrayList<Foods>()
        private var recommendedArray: ArrayList<Foods> = ArrayList<Foods>()
    //}

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    val imm: InputMethodManager =
                        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val model = Models.getInstance()
        newestFoodAdapter.mContext = this
        recommendedFoodAdapter.mContext = this
        categoryAdapter.activity = this

        setContentView(R.layout.activity_main)
        arrCategory.add(Categories(1,"Tất cả", R.drawable.noodle))
        arrCategory.add(Categories(2,"Món ăn", R.drawable.food))
        arrCategory.add(Categories(3,"Thức uống", R.drawable.drink))
        arrCategory.add(Categories(4,"Gà rán", R.drawable.chicken))
        arrCategory.add(Categories(5,"Thức ăn nhanh", R.drawable.hamberger))
        arrCategory.add(Categories(6,"Bánh ngọt", R.drawable.cake))

        categoryAdapter.setData(arrCategory)

        rv_category.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rv_category.adapter = categoryAdapter

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

        btn_menu.setOnClickListener{
            openMenu(it)
        }

        btn_noti.setOnClickListener {
            openNotification(it)
        }
        btn_cart.setOnClickListener {
            openCart(it)
        }
        menuGoToMain.setOnClickListener{
            goTo(it)
        }
        menuGoToAccount.setOnClickListener{
            goTo(it)
        }
        reload()
    }

    fun reload(){
        //Set New Food Button
        arrNewestFood = ArrayList<Foods>()
        arrRecommendedFood = ArrayList<Foods>()
        newestFoodAdapter.setData(arrNewestFood)
        rv_newfood.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rv_newfood.adapter = newestFoodAdapter
        recommendedFoodAdapter.setData(arrRecommendedFood)
        rv_suggestfood.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rv_suggestfood.adapter = recommendedFoodAdapter
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
                Log.d("Connecting","main clicked")
                findViewById<DrawerLayout>(R.id.main_drawer_layout).closeDrawers()
            }
            getString(R.string.navigation_label_account)->{
                Log.d("Connecting", "account clicked")
                thread {
                    if (Connection.isLoggedIn(this, this)) {
                        Log.d("Connecting", "Account starting")
                        runOnUiThread {
                            startActivity(Intent(this, Account::class.java))
                        }
                    } else {
                        Log.d("Connecting", "Login starting")
                        runOnUiThread {
                            startActivity(Intent(this, Login::class.java))
                        }
                    }
                }
            }
        }

    }

    fun openCart(view: View) {
        startActivity(Intent(this, CartActivity::class.java))
    }

    fun openNotification(view: View) {
        startActivity(Intent(this, ReceiptActivity::class.java))
    }

    fun categorize(category: String){
        if (category != "Tất cả") {
            newArray = ArrayList<Foods>()
            recommendedArray = ArrayList<Foods>()
            for (food: Foods in arrNewestFood) {
                if (food.product.category == category) newArray.add(food)
            }
            for (food: Foods in arrRecommendedFood) {
                if (food.product.category == category) recommendedArray.add(food)
            }
            newestFoodAdapter.setData(newArray)
            recommendedFoodAdapter.setData(recommendedArray)
            runOnUiThread {
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
}
