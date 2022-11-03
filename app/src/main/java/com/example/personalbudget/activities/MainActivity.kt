package com.example.personalbudget.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.personalbudget.R
import com.example.personalbudget.fragments.AddFragment
import com.example.personalbudget.fragments.ChartFragment
import com.example.personalbudget.fragments.TransFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setBottomNav()
    }

    private fun setBottomNav(){
        bottom_nav.setOnNavigationItemSelectedListener{
            when(it.itemId){
                R.id.chart ->{
                    loadFragment(ChartFragment())
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.add ->{
                    loadFragment(AddFragment())
                    return@setOnNavigationItemSelectedListener true
                }
                else -> {
                    loadFragment(TransFragment())
                    return@setOnNavigationItemSelectedListener true
                }
            }
        }

    }

    private fun loadFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.content_frag,fragment)
        transaction.commit()
    }

}

