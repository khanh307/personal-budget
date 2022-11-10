package com.example.personalbudget.adapter

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.res.Resources
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.personalbudget.Models.AddItem
import com.example.personalbudget.Models.TransModel
import com.example.personalbudget.R
import com.example.personalbudget.Ultils.MyDatabeseUtils
import com.example.personalbudget.listener.ItemAddListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class BottomSheetAdapter() : BottomSheetDialogFragment(), ItemAddListener {
    private lateinit var itemReceive: TransModel
    private lateinit var dialogBottom: BottomSheetDialog;
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private lateinit var rootView: View

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

    companion object{
        fun newInstance(item: TransModel): BottomSheetAdapter {
            var bottomSheetAdapter = BottomSheetAdapter()
            var bundle = Bundle()
            bundle.putSerializable("item", item)
            bottomSheetAdapter.setArguments(bundle)
            return bottomSheetAdapter
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundleReceive = arguments
        if (bundleReceive != null) {
           itemReceive = bundleReceive.get("item") as TransModel
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialogBottom = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        return dialogBottom
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_add, container, false)

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        bottomSheetBehavior = BottomSheetBehavior.from(view.parent as View)
//        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
//        var layout: ConstraintLayout? = dialogBottom.findViewById(R.id.layout_add)
//        layout!!.minimumHeight = Resources.getSystem().displayMetrics.heightPixels

        initView()
    }

    private fun initView(){
        textMoney = dialogBottom.findViewById(R.id.textMoney)!!
        textDate = dialogBottom.findViewById(R.id.textDate)!!
        textTime = dialogBottom.findViewById(R.id.textTime)!!
        textAccount = dialogBottom.findViewById(R.id.textAccount)!!
        textType = dialogBottom.findViewById(R.id.textType)!!
        val number_close_btn: ImageButton = dialogBottom.findViewById(R.id.number_close_btn)!!
        val recycler_close_btn: ImageButton = dialogBottom.findViewById(R.id.recycler_close_btn)!!
        textNote = dialogBottom.findViewById(R.id.textNote)!!
        collect_btn = dialogBottom.findViewById(R.id.collect_btn)!!
        spending_btn = dialogBottom.findViewById(R.id.spending_btn)!!
        val save_btn: AppCompatButton = dialogBottom.findViewById(R.id.save_btn)!!
        val delete_btn: AppCompatButton = dialogBottom.findViewById(R.id.delete_btn)!!
        layoutKeyBoard = dialogBottom.findViewById(R.id.layoutKeyBoard)!!
        recyclerView = dialogBottom.findViewById(R.id.recyclerview_add_frag)!!
        layout_recyclerview = dialogBottom.findViewById(R.id.layout_recyclerview)!!
        title_recycler = dialogBottom.findViewById(R.id.title_recycler)!!
        arrayItem = ArrayList()
        itemAddAdapter = ItemAddAdapter(requireContext(), arrayItem, this)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
        recyclerView.adapter = itemAddAdapter
        database = requireContext().openOrCreateDatabase(MyDatabeseUtils.DATABASE_NAME, Context.MODE_PRIVATE, null);

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

        textMoney.setOnFocusChangeListener { v, asFocus ->
            if (asFocus) {
                layoutKeyBoard.visibility = View.VISIBLE
                setNumberKeyBoard(dialogBottom, textMoney)
            } else {
                layoutKeyBoard.visibility = View.GONE
            }
        }

        number_close_btn.setOnClickListener {
            layoutKeyBoard.visibility = View.GONE
        }
        recycler_close_btn.setOnClickListener {
            layout_recyclerview.visibility = View.GONE
            clicked = ""
        }
    }

    private fun setNumberKeyBoard(view: BottomSheetDialog, output: TextView) {
        val button0: AppCompatButton = view.findViewById(R.id.button0)!!
        val button1: AppCompatButton = view.findViewById(R.id.button1)!!
        val button2: AppCompatButton = view.findViewById(R.id.button2)!!
        val button3: AppCompatButton = view.findViewById(R.id.button3)!!
        val button4: AppCompatButton = view.findViewById(R.id.button4)!!
        val button5: AppCompatButton = view.findViewById(R.id.button5)!!
        val button6: AppCompatButton = view.findViewById(R.id.button6)!!
        val button7: AppCompatButton = view.findViewById(R.id.button7)!!
        val button8: AppCompatButton = view.findViewById(R.id.button8)!!
        val button9: AppCompatButton = view.findViewById(R.id.button9)!!
        val buttonDone: AppCompatButton = view.findViewById(R.id.buttondone)!!
        val buttonClear: ImageButton = view.findViewById(R.id.buttonclear)!!

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

    override fun clickItem(item: AddItem) {
        TODO("Not yet implemented")
    }
}