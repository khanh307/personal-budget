package com.example.personalbudget.activities

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.personalbudget.R
import com.example.personalbudget.fragments.AddFragment
import com.example.personalbudget.fragments.ChartFragment
import com.example.personalbudget.fragments.TransFragment
import kotlinx.android.synthetic.main.activity_main.*
import java.io.*


class MainActivity : AppCompatActivity() {

    val DATABASE_NAME: String = "personal_budget.db"
    val DB_PATH_SUFFIX = "/databases/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setBottomNav()
        processCopy()
    }

    private fun setBottomNav() {
        bottom_nav.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.chart -> {
                    loadFragment(ChartFragment())
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.add -> {
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

    private fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.content_frag, fragment)
        transaction.commit()
    }

    private fun processCopy() {
        val dbFile: File = getDatabasePath(DATABASE_NAME)
        Log.d("databaseMain", "processCopy")
        if (!dbFile.exists()) {
            try {
                CopyDataBaseFromAsset()
                Toast.makeText(this, "Copying sucess from Assets folder", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun CopyDataBaseFromAsset() {
        try {
            var myInput: InputStream = assets.open(DATABASE_NAME)
            var outFileName = getDatabasePath()
            var f: File = File(applicationInfo.dataDir + DB_PATH_SUFFIX)
            if (!f.exists()){
                f.mkdir();
            }
            var myOutput: OutputStream = FileOutputStream(outFileName)
            val size = myInput.available()
            val buffer = ByteArray(size)
            myInput.read(buffer);
            myOutput.write(buffer);
            myOutput.flush()
            myOutput.close()
            myInput.close()
        } catch (e: IOException){
            e.printStackTrace()
        }
    }

    private fun getDatabasePath(): String {
        return getApplicationInfo().dataDir + DB_PATH_SUFFIX + DATABASE_NAME;
    }

}

