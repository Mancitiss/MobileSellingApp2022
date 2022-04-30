package org.duckdns.mancitiss.testapplication.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.duckdns.mancitiss.testapplication.R
import org.duckdns.mancitiss.testapplication.entities.Foods
import kotlinx.android.synthetic.main.item_food.view.*

class FoodAdapter: RecyclerView.Adapter<FoodAdapter.RecyclerViewHolder>(){

    private var arrFood = ArrayList<Foods>()

    class RecyclerViewHolder(view:View):RecyclerView.ViewHolder(view){

    }

    fun setData(arrData:List<Foods>){
        arrFood = arrData as ArrayList<Foods>
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        return RecyclerViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_food, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        holder.itemView.tv_foodName.text = arrFood[position].name
        holder.itemView.tv_foodPrice.text = arrFood[position].price.toString()
        holder.itemView.img_food.setImageResource(arrFood[position].img)
    }

    override fun getItemCount(): Int {
        return arrFood.size
    }


}