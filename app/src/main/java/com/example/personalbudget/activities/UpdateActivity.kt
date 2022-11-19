package com.example.personalbudget.activities

import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.session.MediaSession.Token
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.personalbudget.Models.AddItem
import com.example.personalbudget.Models.TransModel
import com.example.personalbudget.R
import com.example.personalbudget.Ultils.MyDatabeseUtils
import com.example.personalbudget.adapter.ItemAddAdapter
import com.example.personalbudget.listener.ItemAddListener
import kotlinx.android.synthetic.main.fragment_add.*
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class UpdateActivity : AppCompatActivity(), ItemAddListener {

    private lateinit var arrayItem: ArrayList<AddItem>
    private lateinit var itemAddAdapter: ItemAddAdapter
    private lateinit var database: SQLiteDatabase
    private var clicked = ""
    private var tab_select = "collect"
    private lateinit var item: TransModel

    private lateinit var dialog: DatePickerDialog
    private lateinit var timePickerDialog: TimePickerDialog

    private var type: AddItem? = null
    private var account: AddItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update)
        item = intent.getSerializableExtra("item") as TransModel
        textDate.setText(item.date)
        textTime.setText(item.time)
        textType.setText(item.name!!.name)
        textAccount.setText(item.account!!.name)
        var decimalFormat = DecimalFormat("###,###,###");
        var money: Long = 0
        if (item.money < 0){
            tab_select = "spending"
            money = -item.money
            spending_btn.setBackgroundResource(R.drawable.button_pressed)
            spending_btn.setTextColor(resources.getColor(R.color.primary))
            collect_btn.visibility = View.GONE
        } else {
            tab_select = "collect"
            money = item.money
            collect_btn.setBackgroundResource(R.drawable.button_pressed)
            collect_btn.setTextColor(resources.getColor(R.color.primary))
            spending_btn.visibility = View.GONE
        }
        textMoney.setText(decimalFormat.format(money).toString())
        textNote.setText(item.note)
        setListener()
    }

    private fun setListener(){
        delete_btn.visibility = View.VISIBLE
        arrayItem = ArrayList()
        itemAddAdapter = ItemAddAdapter(this, arrayItem, this)
        recyclerview_add_frag.setHasFixedSize(true)
        recyclerview_add_frag.layoutManager = GridLayoutManager(this, 3)
        recyclerview_add_frag.adapter = itemAddAdapter
        database = openOrCreateDatabase(MyDatabeseUtils.DATABASE_NAME, Context.MODE_PRIVATE, null);

        number_close_btn.setOnClickListener {
            layoutKeyBoard.visibility = View.GONE
        }
        recycler_close_btn.setOnClickListener {
            layout_recyclerview.visibility = View.GONE
            clicked = ""
        }
        textNote.setOnFocusChangeListener { v, hasFocus ->
            v?.let { v.hideKeyboard() }
        }

        textMoney.setOnFocusChangeListener { v, asFocus ->
            if (asFocus) {
                layoutKeyBoard.visibility = View.VISIBLE
                setNumberKeyBoard(textMoney)
            } else {
                layoutKeyBoard.visibility = View.GONE
            }
        }

        textDate.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                openDatePicker(textDate)
            } else {
                dialog.dismiss()
            }
        }

        textTime.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                openTimePicker(textTime)
            } else {
                timePickerDialog.dismiss()
            }
        }

        textAccount.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                setDataRecyclerView("account")
                clicked = "account"
            } else {
                layout_recyclerview.visibility = View.GONE
                clicked = ""
            }
        }

        textType.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                setDataRecyclerView("type")
                clicked = "type"
            } else {
                layout_recyclerview.visibility = View.GONE
                clicked = ""
            }
        }

        save_btn.setOnClickListener {
            updateTrans()
        }
        delete_btn.setOnClickListener {
            deleteTrans()
        }
    }

    private fun deleteTrans() {
        val query = "DELETE FROM trans WHERE id = " + item.id
        database.execSQL(query)
        var update = ""
        if (item.money < 0){
            update = "UPDATE title \n" +
                    "SET spendingmoney = (SELECT spendingmoney FROM title WHERE date = '"+ item.date + "') - "+ (-item.money) + "\n" +
                    "WHERE date = '" + item.date + "'"
        } else {
            update = "UPDATE title \n" +
                    "SET collectmoney = (SELECT collectmoney FROM title WHERE date = '"+ item.date + "') - "+ (item.money) + "\n" +
                    "WHERE date = '" + item.date + "'"
        }
        database.execSQL(update)
        val queryDelete = "DELETE FROM title\n" +
                "WHERE collectmoney <= 0\n" +
                " and spendingmoney <= 0"
        database.execSQL(queryDelete)
        Toast.makeText(this, "Xóa thành công", Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun updateTrans() {
        if (textAccount.text.isEmpty()) {
            Toast.makeText(this, "Nhập Tài khoản", Toast.LENGTH_SHORT).show()
            return
        } else if (textType.text.isEmpty()) {
            Toast.makeText(this, "Nhập Thể loại", Toast.LENGTH_SHORT).show()
            return
        } else if (textMoney.text.isEmpty()) {
            Toast.makeText(this, "Nhập Số tiền", Toast.LENGTH_SHORT).show()
            return
        }

        var idaccount = ""
        if (account == null) {
            idaccount = item.account!!.id
        } else {
            idaccount = account!!.id
        }
        var idtype = ""
        if (type == null){
            idtype = item.name!!.id
        } else {
            idtype = type!!.id
        }

        val note: String = textNote.text.toString()
        val date = textDate.text.toString()
        val time = textTime.text.toString()
        var money = textMoney.text.toString().replace(".", "")
        var values: ContentValues = ContentValues()

        if (tab_select.equals("spending")) {
            money = "-" + money
        }

        values.put(MyDatabeseUtils.KEY_ACCOUNT_TRANS, idaccount)
        if (tab_select.equals("spending")) {
            values.put(MyDatabeseUtils.KEY_TYPE_TRANS, idtype)
        } else {
            values.put(MyDatabeseUtils.KEY_TYPE_COLL_TRANS, idtype)
        }
        values.put(MyDatabeseUtils.KEY_NOTE_TRANS, note)
        values.put(MyDatabeseUtils.KEY_DATE_TRANS, date)
        values.put(MyDatabeseUtils.KEY_TIME_TRANS, time)
        values.put(MyDatabeseUtils.KEY_MONEY_TRANS, money)

        database.insert(MyDatabeseUtils.TABLE_TRANS, null, values)

        val query = "SELECT  * FROM title\n" +
                "WHERE date = '" + date + "'"
        var cursor: Cursor = database!!.rawQuery(query, null)
        cursor.moveToFirst()
        if (cursor.count <= 0) {
            var para = ContentValues()
            para.put(MyDatabeseUtils.KEY_DATE_TRANS, date)
            if (tab_select.equals("spending")) {
                para.put("collectmoney", "0")
                para.put("spendingmoney", textMoney.text.toString().replace(".", ""))
            } else {
                para.put("collectmoney", textMoney.text.toString().replace(".", ""))
                para.put("spendingmoney", "0")
            }
            database.insert(MyDatabeseUtils.TABLE_TITLE, null, para)
        } else {
            var spendingmoney = cursor.getString(2).toLong()
            var collectmoney = cursor.getString(1).toLong()
            var query: String = ""
            var m = textMoney.text.toString().replace(".", "").toLong()
            if (tab_select.equals("spending")) {
                spendingmoney += m;
                query = "UPDATE title\n" +
                        "SET spendingmoney = '" + spendingmoney + "'\n" +
                        "WHERE date = '" + date + "'"
            } else {
                collectmoney += m;
                query = "UPDATE title\n" +
                        "SET collectmoney = '" + collectmoney + "'\n" +
                        "WHERE date = '" + date + "'"
            }
            database!!.execSQL(query)
        }



        Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show()
        deleteTrans()

    }

    private fun openTimePicker(textTime: EditText) {
        var cal = Calendar.getInstance()
        val hour = cal.get(Calendar.HOUR_OF_DAY)
        val minute = cal.get(Calendar.MINUTE)

        timePickerDialog = TimePickerDialog(
            this,
            TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
                cal.set(Calendar.MINUTE, minute)
                val myFormat = "HH:mm" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                textTime.setText(sdf.format(cal.time).toString())
            },
            hour, minute, true
        )

        timePickerDialog.show()
    }

    private fun openDatePicker(textDate: EditText) {
        var cal = Calendar.getInstance()

        val dateSetListener =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                val myFormat = "dd/MM/yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                textDate.setText(sdf.format(cal.time).toString())
            }

        dialog = DatePickerDialog(
            this, dateSetListener,
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        )

        dialog.show()
    }

    private fun setNumberKeyBoard(output: TextView) {

        button0.setOnClickListener {
            if (output.text.isEmpty()) {
                output.setText("0")
            } else if (!output.text.toString().equals("0")) {
                var decimalFormat = DecimalFormat("###,###,###");
                var money = (output.text.toString() + "0").replace(".", "")
                output.setText(decimalFormat.format(money.toLong()))
            }
        }
        button1.setOnClickListener {
            var decimalFormat = DecimalFormat("###,###,###");
            var money = (output.text.toString() + "1").replace(".", "")
            output.setText(decimalFormat.format(money.toLong()))
        }
        button2.setOnClickListener {
            var decimalFormat = DecimalFormat("###,###,###");
            var money = (output.text.toString() + "2").replace(".", "")
            output.setText(decimalFormat.format(money.toLong()))
        }
        button3.setOnClickListener {
            var decimalFormat = DecimalFormat("###,###,###");
            var money = (output.text.toString() + "3").replace(".", "")
            output.setText(decimalFormat.format(money.toLong()))

        }

        button4.setOnClickListener {
            var decimalFormat = DecimalFormat("###,###,###");
            var money = (output.text.toString() + "4").replace(".", "")
            output.setText(decimalFormat.format(money.toLong()))
        }

        button5.setOnClickListener {
            var decimalFormat = DecimalFormat("###,###,###");
            var money = (output.text.toString() + "5").replace(".", "")
            output.setText(decimalFormat.format(money.toLong()))
        }

        button6.setOnClickListener {
            var decimalFormat = DecimalFormat("###,###,###");
            var money = (output.text.toString() + "6").replace(".", "")

            output.setText(decimalFormat.format(money.toLong()))

        }

        button7.setOnClickListener {
            var decimalFormat = DecimalFormat("###,###,###");
            var money = (output.text.toString() + "7").replace(".", "")

            output.setText(decimalFormat.format(money.toLong()))

        }

        button8.setOnClickListener {
            var decimalFormat = DecimalFormat("###,###,###");
            var money = (output.text.toString() + "8").replace(".", "")

            output.setText(decimalFormat.format(money.toLong()))

        }
        button9.setOnClickListener {
            var decimalFormat = DecimalFormat("###,###,###");
            var money = (output.text.toString() + "9").replace(".", "")
            output.setText(decimalFormat.format(money.toLong()))
        }
        buttonclear.setOnClickListener {
            var decimalFormat = DecimalFormat("###,###,###");
            if (output.text.toString().length > 0) {
                var money = output.text.toString().substring(0, output.text.toString().length - 1)
                    .replace(".", "")
                if (money.length < 1) {
                    output.setText("")
                }
                if (money.length > 0) {
                    output.setText(decimalFormat.format(money.toLong()))
                }

            }
        }

        buttondone.setOnClickListener {
            layoutKeyBoard.visibility = View.GONE
        }
    }

    private fun setDataRecyclerView(type: String) {
        arrayItem.clear()
        title_recycler.setText(type.uppercase())
        var result = ""
        if (type.equals("type")) {
            result = type + tab_select
        }
        if (type.equals("account")) {
            result = type
        }
        val query = "SELECT * FROM " + result
        var cursor: Cursor = database!!.rawQuery(query, null)
        cursor.moveToFirst()
        while (cursor.isAfterLast == false) {
            val id = cursor.getString(0)
            val name = cursor.getString(1)
            arrayItem.add(AddItem(id, name))
            cursor.moveToNext()
        }
        cursor.close()
        arrayItem.add(AddItem("add", "+"))
        itemAddAdapter.notifyDataSetChanged()
        layout_recyclerview.visibility = View.VISIBLE
    }

    override fun clickItem(item: AddItem) {
        if (!item.id.equals("add")) {
            if (clicked.equals("type")) {
                type = item
                textType.setText(item.name)
            } else if (clicked.equals("account")) {
                account = item
                textAccount.setText(item.name)
            }
        } else {
            openDialog(clicked)
        }
    }

    private fun openDialog(type: String) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.layout_dialog)
        dialog.setCancelable(true)
        val window = dialog.window ?: return
        window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val windowAttributes = window.attributes
        windowAttributes.gravity = Gravity.CENTER
        window.attributes = windowAttributes

        val title: TextView = dialog.findViewById(R.id.title)
        val edit_name: EditText = dialog.findViewById(R.id.edit_name)
        val cancel_btn: AppCompatButton = dialog.findViewById(R.id.cancel_btn)
        val save_btn: AppCompatButton = dialog.findViewById(R.id.save_btn)

        cancel_btn.setOnClickListener {
            dialog.dismiss()
        }

        if (type.equals("type")) {
            title.setText("Thêm nhãn Thể loại")
        } else if (type.equals("account")) {
            title.setText("Thêm nhãn Tài khoản")
        }

        save_btn.setOnClickListener {
            var name = edit_name.text
            if (name.isEmpty()) {
                Toast.makeText(this, "Nhập tên nhãn", Toast.LENGTH_SHORT).show()
            } else {
                var result = upCase(name.toString())
                val query = "INSERT INTO " + type + tab_select + " VALUES(NULL, '" + result + "')"
                database.execSQL(query)
                Toast.makeText(this, "Thêm thành công", Toast.LENGTH_SHORT).show()
                setDataRecyclerView(type)
                dialog.dismiss()
            }
        }


        dialog.show()
    }

    private fun upCase(str: String): String {
        str.toLowerCase()
        var arr = str.split(" ")
        var result = ""
        for (x in arr) {
            result = result + (x.substring(0, 1).toUpperCase() + x.substring(1));
            result += " "
        }
        return result.trim()

    }

    fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    override fun onStop() {
        super.onStop()
        database.close()
    }
}