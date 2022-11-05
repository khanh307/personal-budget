package com.example.personalbudget.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.example.personalbudget.R

class ItemAddAdapter(var context: Context, var arrayItem: ArrayList<String>) :
    RecyclerView.Adapter<ItemAddAdapter.ItemHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemAddAdapter.ItemHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_item_recycler_view_add_frag, null)
        val holder = ItemHolder(view)
        return holder
    }

    override fun onBindViewHolder(holder: ItemAddAdapter.ItemHolder, position: Int) {
        holder.textView.setText(arrayItem.get(position))
    }

    override fun getItemCount(): Int {
       return arrayItem.size
    }

    class ItemHolder(@NonNull itemView: View): RecyclerView.ViewHolder(itemView){
        var textView: TextView

        init {
            textView = itemView.findViewById(R.id.name_item);
        }
    }
}