package com.example.personalbudget.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.personalbudget.Models.PieChartItem
import com.example.personalbudget.R
import java.text.DecimalFormat

class CardAdapter(val context: Context, var arrayItem: ArrayList<PieChartItem>) :
    RecyclerView.Adapter<CardAdapter.CardHolder>() {
    class CardHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var color_item: CardView
        var percent_item: TextView
        var name_item: TextView
        var total_item: TextView

        init {
            color_item = itemView.findViewById(R.id.color_item)
            percent_item = itemView.findViewById(R.id.percent_item)
            name_item = itemView.findViewById(R.id.name_item)
            total_item = itemView.findViewById(R.id.total_item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.layout_pie_item, null);
        var holder = CardHolder(view)
        return holder
    }

    override fun onBindViewHolder(holder: CardHolder, position: Int) {
        var item = arrayItem.get(position)
        holder.color_item.setCardBackgroundColor(item.color)
        holder.name_item.setText(item.name);
        holder.percent_item.setText(item.persent.toString()+"%")
        var decimalFormat = DecimalFormat("###,###,###");
        holder.total_item.setText(decimalFormat.format(item.total)+"đ")
    }

    override fun getItemCount(): Int {
        return arrayItem.size;
    }
}