package org.duckdns.mancitiss.testapplication.adapter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import org.duckdns.mancitiss.testapplication.R
import org.duckdns.mancitiss.testapplication.entities.Foods
import kotlinx.android.synthetic.main.item_food.view.*
import org.duckdns.mancitiss.testapplication.ViewProduct

class FoodAdapter: RecyclerView.Adapter<FoodAdapter.RecyclerViewHolder>(){

    private var arrFood = ArrayList<Foods>()
    private lateinit var mContext:Context

    class RecyclerViewHolder(view:View):RecyclerView.ViewHolder(view){

    }

    fun setData(arrData:ArrayList<Foods>){
        arrFood = arrData
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        return RecyclerViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_food, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        holder.itemView.tv_foodName.text = arrFood[position].name
        holder.itemView.tv_foodPrice.text = arrFood[position].price.toString()
        if (arrFood[position].img != null)
            holder.itemView.img_food.setImageBitmap(arrFood[position].img)
        else holder.itemView.img_food.setImageResource(R.drawable.food)
        holder.itemView.setOnClickListener() {
            onClickGoToViewProduct(arrFood[position])
        }
    }

    private fun onClickGoToViewProduct(food: Foods) {
        var intent:Intent = Intent(this.mContext, ViewProduct::class.java)
        var bundle: Bundle = Bundle()
        bundle.putSerializable("object_food", food)
        intent.putExtras(bundle)
        mContext.startActivity(intent)
    }

    override fun getItemCount(): Int {
        return arrFood.size
    }

}