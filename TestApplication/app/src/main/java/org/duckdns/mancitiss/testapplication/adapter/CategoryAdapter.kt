package org.duckdns.mancitiss.testapplication.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.duckdns.mancitiss.testapplication.entities.Categories
import kotlinx.android.synthetic.main.item_category.view.*
import org.duckdns.mancitiss.testapplication.MainActivity
import org.duckdns.mancitiss.testapplication.R

class CategoryAdapter: RecyclerView.Adapter<CategoryAdapter.RecyclerViewHolder>(){

    private var arrCategory = ArrayList<Categories>()

    lateinit var activity: MainActivity

    class RecyclerViewHolder(view:View):RecyclerView.ViewHolder(view){

    }

    fun setData(arrData:List<Categories>){
        arrCategory = arrData as ArrayList<Categories>
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        return RecyclerViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_category, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        holder.itemView.tv_cateName.text = arrCategory[position].name
        holder.itemView.img_category.setImageResource(arrCategory[position].img)
    }

    override fun getItemCount(): Int {
        return arrCategory.size
    }


}