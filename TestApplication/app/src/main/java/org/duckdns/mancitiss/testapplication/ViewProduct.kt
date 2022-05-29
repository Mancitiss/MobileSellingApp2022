package org.duckdns.mancitiss.testapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_view_product.*
import org.duckdns.mancitiss.testapplication.entities.Foods

class ViewProduct : AppCompatActivity() {

    var numberOrder = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_product)
        
        var food: Foods = intent.getSerializableExtra("object_food") as Foods

        Glide.with(this).load(food::img).into(imageView2)
        textViewName.setText((food::name).toString())
        textViewDescription.setText((Product::description).toString())
        textViewPrice.setText((food::price).toString())
        textViewNumberOrder.setText("1")

        iButtonBack.setOnClickListener() {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        buttonPlus.setOnClickListener {
            numberOrder += 1
            textViewNumberOrder.setText(numberOrder)
        }

        buttonMinus.setOnClickListener {
            if(numberOrder > 1) {
                numberOrder -= 1
            }
            textViewNumberOrder.setText(numberOrder)
        }

        buttonAddToCart.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
            finish()
        }
    }

}