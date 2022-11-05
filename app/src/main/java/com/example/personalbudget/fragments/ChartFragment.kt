package com.example.personalbudget.fragments

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.db.williamchart.view.BarChartView
import com.db.williamchart.view.DonutChartView
import com.example.personalbudget.Models.PieChartItem
import com.example.personalbudget.R
import com.example.personalbudget.adapter.CardAdapter
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.utils.ColorTemplate


class ChartFragment : Fragment() {

    private lateinit var barChartView: BarChart;
    private lateinit var barChart: BarChartView;
    private lateinit var donutChart: DonutChartView;
    private val animationDuration = 1000L
    private lateinit var peiChart: PieChart;
    private lateinit var spinnerChart: Spinner;
    private lateinit var arrayItem: ArrayList<PieChartItem>
    private lateinit var cardAdapter: CardAdapter
    private lateinit var recyclerView: RecyclerView

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chart, container, false)
        barChart = view.findViewById(R.id.barChart2)
        peiChart = view.findViewById(R.id.piechart);
        spinnerChart = view.findViewById(R.id.spinner_chart);
        recyclerView = view.findViewById(R.id.recyclerview_chart_frag)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        arrayItem = ArrayList()
        cardAdapter = CardAdapter(requireContext(), arrayItem)
        recyclerView.adapter = cardAdapter
        setSpinner()
        setUpBarChart()
        setupPieChart()
        setDataReccyclerView()
        return view;
    }

    private fun setDataReccyclerView(){
        var labels = listOf("Ăn uống", "Giao thông", "Giải trí", "Học tập", "Sinh hoạt")
        var colors = peiChart.data.colors
        var persents = listOf<Float>(10f, 20f, 30f, 10f, 30f)


        for (index in 0 until labels.size ){
            val name = labels.get(index)
            val color = colors.get(index)
            val persent = persents.get(index).toInt()
            val total = 1000000 * persent / 100
            arrayItem.add(PieChartItem(persent, name, total, color))
        }

        for (index in 0 until labels.size ){
            val name = labels.get(index)
            val color = colors.get(index)
            val persent = persents.get(index).toInt()
            val total = 1000000 * persent / 100
            arrayItem.add(PieChartItem(persent, name, total, color))

        }

        for (index in 0 until labels.size ){
            val name = labels.get(index)
            val color = colors.get(index)
            val persent = persents.get(index).toInt()
            val total = 1000000 * persent / 100
            arrayItem.add(PieChartItem(persent, name, total, color))

        }

        cardAdapter.notifyDataSetChanged()
    }

    private fun setupPieChart() {
        val NoOfEmp = ArrayList<PieEntry>()

        NoOfEmp.add(PieEntry(10f, 1));
        NoOfEmp.add(PieEntry(20f, 2));
        NoOfEmp.add(PieEntry(30f, 3));
        NoOfEmp.add(PieEntry(10f, 4));
        NoOfEmp.add(PieEntry(30f, 5));

        val dataSet = PieDataSet(NoOfEmp, "Number Of Employees")
        val data = PieData(dataSet)
        peiChart.setData(data)
        peiChart.description.isEnabled = false
        dataSet.setColors(*ColorTemplate.PASTEL_COLORS)
        peiChart.animateXY(2000, 2000)
        setLegend()
    }

    private fun setLegend() {
        val l = peiChart.legend;
        l.setFormSize(10f);
        l.setForm(Legend.LegendForm.SQUARE);
        l.setYOffset(5f);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.CENTER); // set vertical alignment for legend
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT); // set horizontal alignment for legend
        l.setOrientation(Legend.LegendOrientation.VERTICAL); // set orientation for legend
        l.setDrawInside(false);
        l.setTextSize(12f);
        l.setTextColor(Color.BLACK);

        l.setYEntrySpace(1f);

        val entries: MutableList<LegendEntry> = ArrayList()
        val yValues: ArrayList<PieEntry> = ArrayList()
        yValues.add(PieEntry(10f, "Ăn uống"));
        yValues.add(PieEntry(20f, "Giao thông"));
        yValues.add(PieEntry(30f, "Giải trí"));
        yValues.add(PieEntry(10f, "Học tập"));
        yValues.add(PieEntry(30f, "Sinh hoạt"));

        for (i in 0..yValues.size - 1) {
            val entry = LegendEntry()
            entry.formColor = ColorTemplate.PASTEL_COLORS[i]
            entry.label = yValues[i].label
            entries.add(entry)
        }
        l.setCustom(entries)
    }

    private fun setUpBarChart() {
        val barSet = listOf(
            "JAN" to 10F,
            "FEB" to 2F,
            "MAR" to 2F,
            "MAY" to 4F,
            "APR" to 4F
        )
        var colors = ArrayList<Int>()
        for (i in 0..4){
            colors.add(ColorTemplate.PASTEL_COLORS.get(i))
        }
        barChart.barsColorsList = colors
        barChart.labelsSize = 16F
        barChart.animation.duration = animationDuration
        barChart.animate(barSet)
    }

    private fun setSpinner(){
        var items = ArrayList<String>();
        items.add("Tuần")
        items.add("Tháng")
        items.add("Năm")
//        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, items)
        val adapter = ArrayAdapter.createFromResource(requireContext(), R.array.chart_spiner, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerChart.adapter = adapter;
        Toast.makeText(requireContext(), spinnerChart.selectedItem.toString(), Toast.LENGTH_SHORT).show()
        spinnerChart.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                var item: String = parent!!.getItemAtPosition(position).toString()
                Toast.makeText(requireContext(), item, Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

    }


}

