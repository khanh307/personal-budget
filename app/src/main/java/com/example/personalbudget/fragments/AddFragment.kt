package com.example.personalbudget.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.GridView
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.personalbudget.R
import com.example.personalbudget.adapter.ItemAddAdapter
import kotlinx.android.synthetic.main.fragment_add.*
import java.text.DecimalFormat


class AddFragment : Fragment() {

    private lateinit var layoutKeyBoard: View
    private lateinit var recyclerView: RecyclerView
    private lateinit var arrayItem: ArrayList<String>
    private lateinit var itemAddAdapter: ItemAddAdapter
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add, container, false)
        val textMoney: EditText = view.findViewById(R.id.textMoney)
        layoutKeyBoard = view.findViewById(R.id.layoutKeyBoard)
        recyclerView = view.findViewById(R.id.recyclerview_add_frag)
        arrayItem = ArrayList()
        itemAddAdapter = ItemAddAdapter(requireContext(), arrayItem)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
        recyclerView.adapter = itemAddAdapter
        setDataRecyclerView()
        textMoney.setOnClickListener {
            layoutKeyBoard.visibility = View.VISIBLE
            setNumberKeyBoard(view, textMoney)
        }
        return view
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

    private fun setDataRecyclerView(){
        arrayItem.add("Ăn uống")
        arrayItem.add("Giao thông")
        arrayItem.add("Sinh hoạt")
        arrayItem.add("Giải trí")
        itemAddAdapter.notifyDataSetChanged()
    }
}