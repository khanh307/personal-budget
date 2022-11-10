package com.example.personalbudget.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.personalbudget.Models.AddItem
import com.example.personalbudget.Models.TransModel
import com.example.personalbudget.R
import com.example.personalbudget.activities.UpdateActivity
import com.example.personalbudget.adapter.TransAdapter
import com.example.personalbudget.listener.ItemTransListener
import java.text.DecimalFormat
import java.util.Calendar


class TransFragment : Fragment(), ItemTransListener {

    val DATABASE_NAME: String = "personal_budget.db"
    private lateinit var button1: AppCompatButton
    private lateinit var button2: AppCompatButton
    private lateinit var button3: AppCompatButton
    private lateinit var button4: AppCompatButton
    private lateinit var button5: AppCompatButton
    private lateinit var button6: AppCompatButton
    private lateinit var button7: AppCompatButton
    private lateinit var button8: AppCompatButton
    private lateinit var button9: AppCompatButton
    private lateinit var button10: AppCompatButton
    private lateinit var button11: AppCompatButton
    private lateinit var button12: AppCompatButton

    private lateinit var recyclerView: RecyclerView
    private lateinit var arrayTrans: ArrayList<TransModel>
    private lateinit var adapter: TransAdapter
    private lateinit var month_picker: TextView
    private lateinit var collect: TextView
    private lateinit var spending: TextView
    private lateinit var total: TextView
    var database: SQLiteDatabase? = null
    private var month: String = ""
    private var year: String = ""

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_trans, container, false)
        recyclerView = view.findViewById(R.id.recyclerview_trans_frag)
        month_picker = view.findViewById(R.id.month_picker)
        collect = view.findViewById(R.id.collect)
        spending = view.findViewById(R.id.spending)
        total = view.findViewById(R.id.total)
        arrayTrans = ArrayList()
        adapter = TransAdapter(requireContext(), arrayTrans, this)
        recyclerView.setHasFixedSize(false)
        database = requireContext().openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
        val linearLayoutManager = LinearLayoutManager(requireContext())

        val cal = Calendar.getInstance()
        month = (cal.get(Calendar.MONTH) + 1).toString()
        year = cal.get(Calendar.YEAR).toString()
        month_picker.setText("Th" + month + " " + year)

        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = adapter
        month_picker.setOnClickListener { openDialogPicker() }
        setDate()
        setTotal()
        return view;
    }

    private fun setTotal() {
        val query = "SELECT sum(title.collectmoney), sum(title.spendingmoney) FROM title \n" +
                "WHERE date like '%/" + month + "/" + year + "'"
        var cursor: Cursor = database!!.rawQuery(query, null)
        cursor.moveToFirst()
        while (cursor.count > 0 && cursor.isAfterLast == false){
            val coll = cursor.getString(0).toLong()
            val spend = cursor.getString(1).toLong()
            var decimalFormat = DecimalFormat("###,###,###");
            collect.setText(decimalFormat.format(coll).toString())
            spending.setText(decimalFormat.format(spend).toString())
            var t = decimalFormat.format(coll - spend)
            total.setText(t.toString())
            cursor.moveToNext()
        }
        cursor.close()
    }

    private fun setDate() {
        arrayTrans.clear()

        val title = "SELECT * FROM title \n" +
                "WHERE date like '%/" + month + "/" + year + "'\n" +
                "ORDER BY date DESC"
        var cur: Cursor = database!!.rawQuery(title, null)
        cur.moveToNext()
        while (cur.isAfterLast == false) {
            val title = cur.getString(0)
            val collectmoney = cur.getString(1).toLong()
            val spendingmoney = cur.getString(2).toLong()
            arrayTrans.add(TransModel("", title, "", null, "", spendingmoney, null, collectmoney, true))

            val query =
                "SELECT trans.id, account.id, account.name, typespending.id ,typespending.name, trans.date, trans.time, trans.money, trans.note\n" +
                        "FROM trans, account, typespending\n" +
                        "WHERE trans.idaccount = account.id\n" +
                        "\tand trans.idtype = typespending.id\n" +
                        "\tand trans.date = '" + title + "'" +
                        "UNION\n" +
                        "SELECT trans.id, account.id, account.name, typecollect.id ,typecollect.name, trans.date, trans.time, trans.money, trans.note\n" +
                        "FROM trans, account, typecollect\n" +
                        "WHERE trans.idaccount = account.id\n" +
                        "\tand trans.idtypecollect = typecollect.id\n" +
                        "\tand trans.date = '" + title + "'" +
                        "ORDER BY trans.date DESC , trans.time DESC\n"

            var cursor: Cursor = database!!.rawQuery(query, null)
            cursor.moveToFirst()
            while (cursor.isAfterLast == false) {
                val id = cursor.getString(0)
                val account = AddItem(cursor.getString(1), cursor.getString(2))
                val type = AddItem(cursor.getString(3), cursor.getString(4))
                val date = cursor.getString(5)
                val time = cursor.getString(6)
                val money = cursor.getString(7).toLong()
                val note = cursor.getString(8) + ""
                arrayTrans.add(TransModel(id, date, time, type, note, money, account, 0, false))
                cursor.moveToNext()
            }
            cursor.close()
            cur.moveToNext()
        }
        cur.close()
        adapter.notifyDataSetChanged()
    }


    private fun openDialogPicker() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_pick_month)
        dialog.setCancelable(true)
        val window = dialog.window ?: return
        window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val windowAttributes = window.attributes
        var location = IntArray(2)
        month_picker.getLocationOnScreen(location)
        windowAttributes.gravity = Gravity.TOP or Gravity.LEFT
        windowAttributes.x = location[0]
        windowAttributes.y = location[1]

        window.attributes = windowAttributes

        var cancel_dialog: ImageView = dialog.findViewById(R.id.cancel_dialog)
        var previous_btn: ImageView = dialog.findViewById(R.id.previous_btn)
        var next_btn: ImageView = dialog.findViewById(R.id.next_btn)
        var text_year: TextView = dialog.findViewById(R.id.text_year)
        button1 = dialog.findViewById(R.id.button1)
        button2 = dialog.findViewById(R.id.button2)
        button3 = dialog.findViewById(R.id.button3)
        button4 = dialog.findViewById(R.id.button4)
        button5 = dialog.findViewById(R.id.button5)
        button6 = dialog.findViewById(R.id.button6)
        button7 = dialog.findViewById(R.id.button7)
        button8 = dialog.findViewById(R.id.button8)
        button9 = dialog.findViewById(R.id.button9)
        button10 = dialog.findViewById(R.id.button10)
        button11 = dialog.findViewById(R.id.button11)
        button12 = dialog.findViewById(R.id.button12)

        cancel_dialog.setOnClickListener { dialog.dismiss() }
        text_year.setText(year.toString())
        previous_btn.setOnClickListener {
            val y = text_year.text.toString().toInt()
            text_year.setText((y - 1).toString())
            year = text_year.text.toString()
            month_picker.setText("Th" + month + " " + text_year.text)
            setDate()
            setTotal()
        }
        next_btn.setOnClickListener {
            val y = text_year.text.toString().toInt()
            text_year.setText((y + 1).toString())
            year = text_year.text.toString()
            month_picker.setText("Th" + month + " " + text_year.text)
            setDate()
            setTotal()
        }

        when (month) {
            "1" -> button1.setTextColor(resources.getColor(R.color.primary))
            "2" -> button2.setTextColor(resources.getColor(R.color.primary))
            "3" -> button3.setTextColor(resources.getColor(R.color.primary))
            "4" -> button4.setTextColor(resources.getColor(R.color.primary))
            "5" -> button5.setTextColor(resources.getColor(R.color.primary))
            "6" -> button6.setTextColor(resources.getColor(R.color.primary))
            "7" -> button7.setTextColor(resources.getColor(R.color.primary))
            "8" -> button8.setTextColor(resources.getColor(R.color.primary))
            "9" -> button9.setTextColor(resources.getColor(R.color.primary))
            "10" -> button10.setTextColor(resources.getColor(R.color.primary))
            "11" -> button11.setTextColor(resources.getColor(R.color.primary))
            "12" -> button12.setTextColor(resources.getColor(R.color.primary))
        }

        button1.setOnClickListener {
            month = "1"
            setColor(button1)
            month_picker.setText("Th" + month + " " + text_year.text)
            dialog.dismiss()
            setDate()
            setTotal()
        }
        button2.setOnClickListener {
            month = "2"
            setColor(button2)
            month_picker.setText("Th" + month + " " + text_year.text)
            dialog.dismiss()
            setDate()
            setTotal()
        }
        button3.setOnClickListener {
            month = "3"
            setColor(button3)
            month_picker.setText("Th" + month + " " + text_year.text)
            dialog.dismiss()
            setDate()
            setTotal()
        }
        button4.setOnClickListener {
            month = "4"
            setColor(button4)
            month_picker.setText("Th" + month + " " + text_year.text)
            dialog.dismiss()
            setDate()
            setTotal()
        }
        button5.setOnClickListener {
            month = "5"
            setColor(button5)
            month_picker.setText("Th" + month + " " + text_year.text)
            dialog.dismiss()
            setDate()
            setTotal()
        }
        button6.setOnClickListener {
            month = "6"
            setColor(button6)
            month_picker.setText("Th" + month + " " + text_year.text)
            dialog.dismiss()
            setDate()
            setTotal()
        }
        button7.setOnClickListener {
            month = "7"
            setColor(button7)
            month_picker.setText("Th" + month + " " + text_year.text)
            dialog.dismiss()
            setDate()
            setTotal()
        }
        button8.setOnClickListener {
            month = "8"
            setColor(button8)
            month_picker.setText("Th" + month + " " + text_year.text)
            dialog.dismiss()
            setDate()
            setTotal()
        }
        button9.setOnClickListener {
            month = "9"
            setColor(button9)
            month_picker.setText("Th" + month + " " + text_year.text)
            dialog.dismiss()
            setDate()
            setTotal()
        }
        button10.setOnClickListener {
            month = "10"
            setColor(button10)
            month_picker.setText("Th" + month + " " + text_year.text)
            dialog.dismiss()
            setDate()
            setTotal()
        }
        button11.setOnClickListener {
            month = "11"
            setColor(button11)
            month_picker.setText("Th" + month + " " + text_year.text)
            dialog.dismiss()
            setDate()
            setTotal()
        }
        button12.setOnClickListener {
            month = "12"
            setColor(button12)
            month_picker.setText("Th" + month + " " + text_year.text)
            dialog.dismiss()
            setDate()
            setTotal()
        }

        dialog.show()
    }

    fun setColor(button: AppCompatButton) {
        button1.setTextColor(resources.getColor(R.color.black))
        button2.setTextColor(resources.getColor(R.color.black))
        button3.setTextColor(resources.getColor(R.color.black))
        button4.setTextColor(resources.getColor(R.color.black))
        button5.setTextColor(resources.getColor(R.color.black))
        button6.setTextColor(resources.getColor(R.color.black))
        button7.setTextColor(resources.getColor(R.color.black))
        button8.setTextColor(resources.getColor(R.color.black))
        button9.setTextColor(resources.getColor(R.color.black))
        button10.setTextColor(resources.getColor(R.color.black))
        button11.setTextColor(resources.getColor(R.color.black))
        button12.setTextColor(resources.getColor(R.color.black))
        button.setTextColor(resources.getColor(R.color.primary))
    }

    override fun itemClicked(item: TransModel) {
        var intent: Intent = Intent(requireContext(), UpdateActivity::class.java)
        intent.putExtra("item", item)
        startActivity(intent)
    }


}