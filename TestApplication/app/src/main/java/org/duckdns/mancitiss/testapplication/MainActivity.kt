package org.duckdns.mancitiss.testapplication

import android.app.ActionBar
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.marginTop
import androidx.core.widget.TextViewCompat
import com.google.android.material.resources.TextAppearance


class MainActivity : AppCompatActivity() {

    var items = mutableListOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.loading)
        Thread.sleep(3000)
        setContentView(R.layout.activity_main)
        //items.add(R.id.item_1)
        addItem("Món ăn thứ nhất", "Ngon nhứt nhaa")
        addItem("Món ăn thứ hai", "Nghon nhì nhee")
        addItem("Món ăn thứ ba", "Ngon lém nhoo")
        addItem("Ulatr", "ahola")
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
    fun addItem(name: String?, description: String?, price: Int? = 0, image: Bitmap? = null) {
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
}
