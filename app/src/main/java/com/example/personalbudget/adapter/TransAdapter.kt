package com.example.personalbudget.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.personalbudget.Models.TransModel
import com.example.personalbudget.R
import com.example.personalbudget.listener.ItemTransListener
import java.text.DecimalFormat

class TransAdapter(var context: Context, var arrayItem: ArrayList<TransModel>, var transClicked: ItemTransListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val TYPE_TITLE = 1;
    val TYPE_CONTENT = 2;
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if (viewType == TYPE_TITLE) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_title_trans, null)
            val holder = TitleHolder(view)
            return holder
        }
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_content_trans, null)
        val holder = ContentHolder(view)
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var item = arrayItem.get(position)
        if (holder.itemViewType == TYPE_TITLE){
            var titleHolder = holder as TitleHolder

            var date = item.date.split("/").get(0)
            var mount = item.date.split("/").get(1)
            var year = item.date.split("/").get(2)
            titleHolder.textDateTitle.setText(date)
            titleHolder.textMountTitle.setText(mount+"."+year)
            var decimalFormat = DecimalFormat("###,###,###");
            titleHolder.textCollectTitle.setText(decimalFormat.format(item.collectmoney))
            titleHolder.textSpendingTitle.setText(decimalFormat.format(item.money))
        } else {
            var contentHolder = holder as ContentHolder
            contentHolder.name_content_trans.setText(item.name!!.name)
            contentHolder.text_note_content_trans.setText(item.note)
            contentHolder.text_account_contents_trans.setText(item.account!!.name)
            var decimalFormat = DecimalFormat("###,###,###");
            var money: Long = 0
            if (item.money < 0){
                contentHolder.text_total_content_trans.setTextColor(Color.parseColor("#ff6155"))
                money = -item.money
            } else{
                contentHolder.text_total_content_trans.setTextColor(Color.parseColor("#3987df"))
                money = item.money
            }
            contentHolder.text_total_content_trans.setText(decimalFormat.format(money))

            holder.itemView.setOnClickListener { transClicked.itemClicked(item) }
        }
    }

    override fun getItemCount(): Int {
        return arrayItem.size
    }

    override fun getItemViewType(position: Int): Int {
        if (arrayItem.get(position).isTitle) {
            return TYPE_TITLE
        }
        return TYPE_CONTENT
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    class TitleHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textDateTitle: TextView
        var textMountTitle: TextView
        var textSpendingTitle: TextView
        var textCollectTitle: TextView

        init {
            textDateTitle = itemView.findViewById(R.id.textDateTitle)
            textMountTitle = itemView.findViewById(R.id.textMountTitle)
            textSpendingTitle = itemView.findViewById(R.id.textSpendingTitle)
            textCollectTitle = itemView.findViewById(R.id.textCollectTitle)
        }
    }

    class ContentHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {
        var name_content_trans: TextView
        var text_note_content_trans: TextView
        var text_account_contents_trans: TextView
        var text_total_content_trans: TextView

        init {
            name_content_trans = itemView.findViewById(R.id.name_content_trans)
            text_note_content_trans = itemView.findViewById(R.id.text_note_content_trans)
            text_account_contents_trans = itemView.findViewById(R.id.text_account_contents_trans)
            text_total_content_trans = itemView.findViewById(R.id.text_total_content_trans)
        }
    }
}