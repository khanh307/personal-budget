package com.example.personalbudget.fragments

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.db.williamchart.view.BarChartView
import com.db.williamchart.view.DonutChartView
import com.example.personalbudget.Models.PieChartItem
import com.example.personalbudget.R
import com.example.personalbudget.Ultils.MyDatabeseUtils
import com.example.personalbudget.adapter.CardAdapter
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.utils.ColorTemplate
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.abs


class ChartFragment : Fragment() {

    //    private lateinit var barChart: BarChartView;
    private val animationDuration = 1000L
    private lateinit var peiChart: PieChart;
    private lateinit var spinnerChart: Spinner;
    private lateinit var pieItem: ArrayList<PieEntry>
    private lateinit var pieItemLegend: ArrayList<PieEntry>
    private lateinit var arrayItem: ArrayList<PieChartItem>
    private lateinit var cardAdapter: CardAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var text_date: TextView
    private lateinit var noData: TextView
    private lateinit var noDataRecyc: TextView
    private lateinit var dialog: DatePickerDialog
    private lateinit var database: SQLiteDatabase
    private var total: Long = 0

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
    private var month: String = ""
    private var year: String = ""
    private var begin: String = ""
    private var end: String = ""
    private var tab_select = "spending"

    @SuppressLint("MissingInflatedId", "ResourceAsColor")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chart, container, false)
//        barChart = view.findViewById(R.id.barChart2)
        peiChart = view.findViewById(R.id.piechart);
        spinnerChart = view.findViewById(R.id.spinner_chart);
        text_date = view.findViewById(R.id.text_date)
        noData = view.findViewById(R.id.noData)
        noDataRecyc = view.findViewById(R.id.noDataRecyc)
        var collect_btn: AppCompatButton = view.findViewById(R.id.collect_btn)
        var spending_btn: AppCompatButton = view.findViewById(R.id.spending_btn)
        text_date.ellipsize = TextUtils.TruncateAt.END
        recyclerView = view.findViewById(R.id.recyclerview_chart_frag)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        arrayItem = ArrayList()
        cardAdapter = CardAdapter(requireContext(), arrayItem)
        recyclerView.adapter = cardAdapter
        database = requireContext().openOrCreateDatabase(
            MyDatabeseUtils.DATABASE_NAME,
            Context.MODE_PRIVATE,
            null
        );
        setSpinner()
//        val calendar: Calendar = Calendar.getInstance()
//        val dates = getDayOfWeek(
//            calendar.get(Calendar.YEAR),
//            calendar.get(Calendar.DATE),
//            calendar.get(Calendar.MONTH)
//        )
//        begin = "14/11/2022"
//        end = "20/11/2022"

        collect_btn.setOnClickListener {
            tab_select = "collect"
            collect_btn.setBackgroundResource(R.drawable.button_pressed)
            spending_btn.setBackgroundResource(R.drawable.tab_selected)
            collect_btn.setTextColor(resources.getColor(R.color.primary))
            spending_btn.setTextColor(resources.getColor(R.color.black))
            setupPieChart()
        }

        spending_btn.setOnClickListener {
            tab_select = "spending"
            spending_btn.setBackgroundResource(R.drawable.button_pressed)
            spending_btn.setTextColor(resources.getColor(R.color.primary))
            collect_btn.setBackgroundResource(R.drawable.tab_selected)
            collect_btn.setTextColor(resources.getColor(R.color.black))
            setupPieChart()
        }
//        setupPieChart()
        return view;
    }

    private fun setupPieChart() {
        getData()
//        val NoOfEmp = ArrayList<PieEntry>()

//        NoOfEmp.add(PieEntry(10f, 1));
//        NoOfEmp.add(PieEntry(20f, 2));
//        NoOfEmp.add(PieEntry(30f, 3));
//        NoOfEmp.add(PieEntry(10f, 4));
//        NoOfEmp.add(PieEntry(30f, 5));

        val dataSet = PieDataSet(pieItem, "")
        val data = PieData(dataSet)
        peiChart.setData(data)
        peiChart.description.isEnabled = false
        dataSet.setColors(*MyDatabeseUtils.PIE_COLORS)
        peiChart.animateXY(1500, 1500)
        setLegend()
    }

    private fun setLegend() {
        val l = peiChart.legend;
        l.setFormSize(10f);
        l.setForm(Legend.LegendForm.SQUARE);
        l.setYOffset(15f);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.CENTER); // set vertical alignment for legend
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT); // set horizontal alignment for legend
        l.setOrientation(Legend.LegendOrientation.VERTICAL); // set orientation for legend
        l.setDrawInside(false);
        l.setTextSize(15f);
        l.setTextColor(Color.BLACK);

        l.setYEntrySpace(1f);

        val entries: MutableList<LegendEntry> = ArrayList()
//        val yValues: ArrayList<PieEntry> = ArrayList()

        for (i in 0..pieItemLegend.size - 1) {
            val entry = LegendEntry()
            entry.formColor = MyDatabeseUtils.PIE_COLORS[i]
            entry.label = pieItemLegend[i].label
            entries.add(entry)
        }
        l.setCustom(entries)
    }

    private fun setSpinner() {
        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.chart_spiner,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerChart.adapter = adapter;
        Toast.makeText(requireContext(), spinnerChart.selectedItem.toString(), Toast.LENGTH_SHORT)
            .show()

        spinnerChart.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                var item: String = parent!!.getItemAtPosition(position).toString()
                if (item.equals("Hàng Tuần")) {
                    val calendar: Calendar = Calendar.getInstance()
                    val dates = getDayOfWeek(
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.DATE),
                        calendar.get(Calendar.MONTH)
                    )
                    begin = dates[0].toString()
                    end = dates[6].toString()
                    text_date.setText(begin + " ~ " + end)
                    text_date.setOnClickListener {
                        openDatePicker()
                    }
                    setupPieChart()
                } else if (item.equals("Hàng Tháng")) {
                    val calendar: Calendar = Calendar.getInstance()
                    year = calendar.get(Calendar.YEAR).toString()
                    month = (calendar.get(Calendar.MONTH) + 1).toString()
                    text_date.setText("Th" + month + " " + year)
                    text_date.setOnClickListener {
                        openDialogPicker()
                    }
                    setupPieChart()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
    }

    private fun getData() {
        arrayItem.clear()
        pieItem = ArrayList()
        pieItemLegend = ArrayList()
        val type = spinnerChart.selectedItem.toString()
        var query = ""

        if (tab_select.equals("spending")) {
            if (type.equals("Hàng Tuần")) {
                query = "SELECT name, SUM(money) FROM trans, typespending\n" +
                        "WHERE date BETWEEN '" + begin + "' AND '" + end + "'\n" +
                        "\tand idtype = typespending.id\n" +
                        "GROUP BY idtype\n"
            } else if (type.equals("Hàng Tháng")) {
                query = "SELECT name, SUM(money) FROM trans, typespending\n" +
                        "WHERE date like '%/" + month + "/" + year + "'\n" +
                        "\tand idtype = typespending.id\n" +
                        "GROUP BY idtype"
            }
        } else if (tab_select.equals("collect")) {
            if (type.equals("Hàng Tuần")) {
                query = "SELECT name, SUM(money) FROM trans, typecollect\n" +
                        "WHERE date BETWEEN '" + begin + "' AND '" + end + "'\n" +
                        "\tand idtypecollect = typecollect.id\n" +
                        "GROUP BY idtypecollect\n"
            } else if (type.equals("Hàng Tháng")) {
                query = "SELECT name, SUM(money) FROM trans, typecollect\n" +
                        "WHERE date like '%/" + month + "/" + year + "'\n" +
                        "\tand idtypecollect = typecollect.id\n" +
                        "GROUP BY idtypecollect"
            }
        }

        total = 0
        val cursor = database.rawQuery(query, null)
        cursor.moveToFirst()
        if (cursor.count > 0) {
            while (cursor.isAfterLast == false) {
                total += cursor.getString(1).toLong()
                cursor.moveToNext()
            }

            cursor.moveToFirst()
            while (cursor.isAfterLast == false) {
                var name = cursor.getString(0)
                var money = cursor.getString(1).toLong()
                var persent: Long = ((abs(money).toFloat() / abs(total).toFloat()) * 100).toLong()
                pieItem.add(PieEntry(persent.toFloat(), cursor.position + 1))
                pieItemLegend.add(PieEntry(persent.toFloat(), name))
                arrayItem.add(PieChartItem(persent.toInt(), name, abs(money), MyDatabeseUtils.PIE_COLORS[cursor.position]))
                Log.d("typeSpinner", money.toString())
                Log.d("typeSpinner", total.toString())
                Log.d("typeSpinner", persent.toString())
                cursor.moveToNext()
            }
        }
        cardAdapter.notifyDataSetChanged()
        cursor.close()
        if (abs(total) <= "0".toLong()){
            noDataRecyc.visibility = View.VISIBLE
            noData.visibility = View.VISIBLE
        } else {
            noDataRecyc.visibility = View.GONE
            noData.visibility = View.GONE
        }
    }

    private fun openDatePicker() {
        var cal = Calendar.getInstance()
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                val dates = getDayOfWeek(
                    year,
                    dayOfMonth,
                    monthOfYear
                )
                begin = dates[0].toString()
                end = dates[6].toString()
                text_date.setText(begin + " ~ " + end)
                setupPieChart()
            }

        dialog = DatePickerDialog(
            requireContext(), dateSetListener,
            begin.split("/")[2].toInt(),
            begin.split("/")[1].toInt() -1,
            begin.split("/")[0].toInt()
        )
        dialog.show()
    }

    private fun getDayOfWeek(year: Int, date: Int, month: Int): Array<String?> {
        val now = Calendar.getInstance()
        now.set(Calendar.YEAR, year)
        now.set(Calendar.MONTH, month)
        now.set(Calendar.DATE, date - 1)

        val format = SimpleDateFormat("dd/MM/yyyy")
        val days = arrayOfNulls<String>(7)
        val delta = -now[GregorianCalendar.DAY_OF_WEEK] + 2
        now.add(Calendar.DAY_OF_MONTH, delta)
        for (i in 0..6) {
            days[i] = format.format(now.time)
            now.add(Calendar.DAY_OF_MONTH, 1)
        }

        return days
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
        text_date.getLocationOnScreen(location)
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
            text_date.setText("Th" + month + " " + text_year.text)
            setupPieChart()
        }
        next_btn.setOnClickListener {
            val y = text_year.text.toString().toInt()
            text_year.setText((y + 1).toString())
            year = text_year.text.toString()
            text_date.setText("Th" + month + " " + text_year.text)
            setupPieChart()

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
            text_date.setText("Th" + month + " " + text_year.text)
            dialog.dismiss()
            setupPieChart()
        }
        button2.setOnClickListener {
            month = "2"
            setColor(button2)
            text_date.setText("Th" + month + " " + text_year.text)
            dialog.dismiss()
            setupPieChart()
        }
        button3.setOnClickListener {
            month = "3"
            setColor(button3)
            text_date.setText("Th" + month + " " + text_year.text)
            dialog.dismiss()
            setupPieChart()
        }
        button4.setOnClickListener {
            month = "4"
            setColor(button4)
            text_date.setText("Th" + month + " " + text_year.text)
            dialog.dismiss()
            setupPieChart()
        }
        button5.setOnClickListener {
            month = "5"
            setColor(button5)
            text_date.setText("Th" + month + " " + text_year.text)
            dialog.dismiss()
            setupPieChart()
        }
        button6.setOnClickListener {
            month = "6"
            setColor(button6)
            text_date.setText("Th" + month + " " + text_year.text)
            dialog.dismiss()
            setupPieChart()
        }
        button7.setOnClickListener {
            month = "7"
            setColor(button7)
            text_date.setText("Th" + month + " " + text_year.text)
            dialog.dismiss()
            setupPieChart()
        }
        button8.setOnClickListener {
            month = "8"
            setColor(button8)
            text_date.setText("Th" + month + " " + text_year.text)
            dialog.dismiss()
            setupPieChart()
        }
        button9.setOnClickListener {
            month = "9"
            setColor(button9)
            text_date.setText("Th" + month + " " + text_year.text)
            dialog.dismiss()
            setupPieChart()
        }
        button10.setOnClickListener {
            month = "10"
            setColor(button10)
            text_date.setText("Th" + month + " " + text_year.text)
            dialog.dismiss()
            setupPieChart()
        }
        button11.setOnClickListener {
            month = "11"
            setColor(button11)
            text_date.setText("Th" + month + " " + text_year.text)
            dialog.dismiss()
            setupPieChart()
        }
        button12.setOnClickListener {
            month = "12"
            setColor(button12)
            text_date.setText("Th" + month + " " + text_year.text)
            dialog.dismiss()
            setupPieChart()
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

    override fun onStop() {
        super.onStop()
        database.close()
    }
}

