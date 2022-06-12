package org.duckdns.mancitiss.testapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_view_product.*
import org.duckdns.mancitiss.testapplication.entities.Foods
import java.util.*

class ViewProduct : AppCompatActivity() {

    var numberOrder = 1
    var idFood: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_product)
        
        var food: Foods = Models.getInstance().currentFood
        idFood = food.id
        if (food.img != null) {
            Glide.with(this).load(food.img).into(imageView2)
        }
        else {
            Glide.with(this).load(R.drawable.food).into(imageView2)
        }
        //Glide.with(this).load(food::img).into(imageView2)
        textViewName.setText(food.name)
        textViewDescription.setText(food.product.description)
        textViewPrice.setText(food.price.toString() + " đ")
        textViewRemain.setText(food.product.quantity.toString())
        textViewRating.setText(food.product.averageStars.toString())
        ratingBar.rating = food.product.averageStars
        // long to date conversion
        val date: Date = Date(food.product.created)
        val sdf = java.text.SimpleDateFormat("dd/MM/yyyy")
        textViewMFG.setText(sdf.format(date))
        textViewNumberOrder.setText("1")

        iButtonBack.setOnClickListener() {
            //startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        buttonPlus.setOnClickListener {
            numberOrder += 1
            textViewNumberOrder.setText(numberOrder.toString())
        }

        buttonMinus.setOnClickListener {
            if(numberOrder > 1) {
                numberOrder -= 1
            }
            textViewNumberOrder.setText(numberOrder.toString())
        }

        buttonAddToCart.setOnClickListener {
            //startActivity(Intent(this, CartActivity::class.java))
            //finish()
            // add key value of idfood and number order to cart
            Models.getInstance().shoppingCart[idFood] = numberOrder
            // inform user that product has been added to cart
            Toast.makeText(this, "Đã thêm vào giỏ", Toast.LENGTH_SHORT).show()
            // go back to main activity
            //startActivity(Intent(this, MainActivity::class.java))
            finish()

        }
    }

}