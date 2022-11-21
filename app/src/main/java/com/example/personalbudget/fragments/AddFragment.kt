package com.example.personalbudget.fragments

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.DatabaseUtils
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.provider.ContactsContract.Intents.Insert
import android.text.format.DateUtils
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.personalbudget.Models.AddItem
import com.example.personalbudget.R
import com.example.personalbudget.Ultils.MyDatabeseUtils
import com.example.personalbudget.adapter.ItemAddAdapter
import com.example.personalbudget.listener.ItemAddListener
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.android.synthetic.main.fragment_add.*
import org.w3c.dom.Text
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*


class AddFragment : Fragment(), ItemAddListener {

    private lateinit var layoutKeyBoard: View
    private lateinit var recyclerView: RecyclerView
    private lateinit var arrayItem: ArrayList<AddItem>
    private lateinit var itemAddAdapter: ItemAddAdapter
    private lateinit var dialog: DatePickerDialog
    private lateinit var timePickerDialog: TimePickerDialog
    private lateinit var database: SQLiteDatabase
    private lateinit var layout_recyclerview: View
    private lateinit var title_recycler: TextView
    private lateinit var textType: EditText
    private lateinit var textAccount: EditText
    private var clicked = ""
    private lateinit var type: AddItem
    private lateinit var account: AddItem
    private lateinit var textNote: EditText
    private lateinit var textDate: EditText
    private lateinit var textTime: EditText
    private lateinit var textMoney: EditText
    private lateinit var collect_btn: AppCompatButton
    private lateinit var spending_btn: AppCompatButton
    private var tab_select = "spending"

    val DATABASE_NAME: String = "personal_budget.db"

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add, container, false)
        textMoney = view.findViewById(R.id.textMoney)
        textDate = view.findViewById(R.id.textDate)
        textTime = view.findViewById(R.id.textTime)
        textAccount = view.findViewById(R.id.textAccount)
        textType = view.findViewById(R.id.textType)
        val number_close_btn: ImageButton = view.findViewById(R.id.number_close_btn)
        val recycler_close_btn: ImageButton = view.findViewById(R.id.recycler_close_btn)
        textNote = view.findViewById(R.id.textNote)
        collect_btn = view.findViewById(R.id.collect_btn)
        spending_btn = view.findViewById(R.id.spending_btn)
        val save_btn: AppCompatButton = view.findViewById(R.id.save_btn)
//        val delete_btn: AppCompatButton = view.findViewById(R.id.delete_btn)
        layoutKeyBoard = view.findViewById(R.id.layoutKeyBoard)
        recyclerView = view.findViewById(R.id.recyclerview_add_frag)
        layout_recyclerview = view.findViewById(R.id.layout_recyclerview)
        title_recycler = view.findViewById(R.id.title_recycler)
        arrayItem = ArrayList()
        itemAddAdapter = ItemAddAdapter(requireContext(), arrayItem, this)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
        recyclerView.adapter = itemAddAdapter

        collect_btn.setOnClickListener {
            tab_select = "collect"
            collect_btn.setBackgroundResource(R.drawable.button_pressed)
            spending_btn.setBackgroundResource(R.drawable.tab_selected)
            collect_btn.setTextColor(resources.getColor(R.color.primary))
            spending_btn.setTextColor(resources.getColor(R.color.black))
            if (clicked.equals("type")) {
                setDataRecyclerView("type")
            }
        }

        spending_btn.setOnClickListener {
            tab_select = "spending"
            spending_btn.setBackgroundResource(R.drawable.button_pressed)
            spending_btn.setTextColor(resources.getColor(R.color.primary))
            collect_btn.setBackgroundResource(R.drawable.tab_selected)
            collect_btn.setTextColor(resources.getColor(R.color.black))
            if (clicked.equals("type")) {
                setDataRecyclerView("type")
            }
        }

        database = requireContext().openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE, null);
        number_close_btn.setOnClickListener {
            layoutKeyBoard.visibility = View.GONE
        }
        recycler_close_btn.setOnClickListener {
            layout_recyclerview.visibility = View.GONE
            clicked = ""
        }

        textNote.setOnFocusChangeListener { v, hasFocus ->
            view?.let { view.hideKeyboard() }
        }

        textMoney.setOnFocusChangeListener { v, asFocus ->
            if (asFocus) {
                layoutKeyBoard.visibility = View.VISIBLE
                setNumberKeyBoard(view, textMoney)
            } else {
                layoutKeyBoard.visibility = View.GONE
            }
        }
        val date = Date()

        val myDateFormat = "dd/MM/yyyy" // mention the format you need
        val myTimeFormat = "HH:mm"
        val dateFormat = SimpleDateFormat(myDateFormat, Locale.US)
        val timeFormat = SimpleDateFormat(myTimeFormat, Locale.US)
        textDate.setText(dateFormat.format(date).toString())
        textTime.setText(timeFormat.format(date).toString())

        textDate.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                openDatePicker(textDate)
            } else {
                dialog.dismiss()
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

        textTime.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                openTimePicker(textTime)
            } else {
                timePickerDialog.dismiss()
            }
        }

        save_btn.setOnClickListener {
            insertNewTrans()
        }

        return view
    }

    private fun insertNewTrans() {
        if (textAccount.text.isEmpty()) {
            Toast.makeText(requireContext(), "Nhập Tài khoản", Toast.LENGTH_SHORT).show()
            return
        } else if (textType.text.isEmpty()) {
            Toast.makeText(requireContext(), "Nhập Thể loại", Toast.LENGTH_SHORT).show()
            return
        } else if (textMoney.text.isEmpty()) {
            Toast.makeText(requireContext(), "Nhập Số tiền", Toast.LENGTH_SHORT).show()
            return
        }
        val idaccount = account.id
        val idtype = type.id
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
            Log.d("readTitle", "cursor null")
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

        textNote.setText("")
        textAccount.setText("")
        textType.setText("")
        textMoney.setText("")
        val myDate = Date()

        val myDateFormat = "dd/MM/yyyy" // mention the format you need
        val myTimeFormat = "HH:mm"
        val dateFormat = SimpleDateFormat(myDateFormat, Locale.US)
        val timeFormat = SimpleDateFormat(myTimeFormat, Locale.US)
        textDate.setText(dateFormat.format(myDate).toString())
        textTime.setText(timeFormat.format(myDate).toString())

        Toast.makeText(requireContext(), "Thêm thành công", Toast.LENGTH_SHORT).show()

    }

    private fun openTimePicker(textTime: EditText) {
        var cal = Calendar.getInstance()
        val hour = cal.get(Calendar.HOUR_OF_DAY)
        val minute = cal.get(Calendar.MINUTE)

        timePickerDialog = TimePickerDialog(
            requireContext(),
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
            requireContext(), dateSetListener,
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        )

        dialog.show()
    }

    private fun setNumberKeyBoard(view: View, output: TextView) {
        val button0: AppCompatButton = view.findViewById(R.id.button0)
        val button1: AppCompatButton = view.findViewById(R.id.button1)
        val button2: AppCompatButton = view.findViewById(R.id.button2)
        val button3: AppCompatButton = view.findViewById(R.id.button3)
        val button4: AppCompatButton = view.findViewById(R.id.button4)
        val button5: AppCompatButton = view.findViewById(R.id.button5)
        val button6: AppCompatButton = view.findViewById(R.id.button6)
        val button7: AppCompatButton = view.findViewById(R.id.button7)
        val button8: AppCompatButton = view.findViewById(R.id.button8)
        val button9: AppCompatButton = view.findViewById(R.id.button9)
        val buttonDone: AppCompatButton = view.findViewById(R.id.buttondone)
        val buttonClear: ImageButton = view.findViewById(R.id.buttonclear)

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
        buttonClear.setOnClickListener {
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

        buttonDone.setOnClickListener {
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

    fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
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
        val dialog = Dialog(requireContext())
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
                Toast.makeText(requireContext(), "Nhập tên nhãn", Toast.LENGTH_SHORT).show()
            } else {
                var result = upCase(name.toString())
                val query = "INSERT INTO " + type + tab_select + " VALUES(NULL, '" + result + "')"
                database.execSQL(query)
                Toast.makeText(requireContext(), "Thêm thành công", Toast.LENGTH_SHORT).show()
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

    override fun onStop() {
        super.onStop()
        database.close()
    }
}