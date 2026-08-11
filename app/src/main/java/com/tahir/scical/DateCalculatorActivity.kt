package com.tahir.scical

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toColorInt
import java.text.SimpleDateFormat
import java.util.*

class DateCalculatorActivity : BaseCalculatorActivity() {

    private lateinit var tvFromDate: TextView
    private lateinit var tvToDate: TextView
    private lateinit var tvDiffYears: TextView
    private lateinit var tvDiffMonths: TextView
    private lateinit var tvDiffDays: TextView
    private lateinit var tvSummaryFrom: TextView
    private lateinit var tvSummaryTo: TextView

    private var fromCalendar = Calendar.getInstance()
    private var toCalendar = Calendar.getInstance()
    private var activeField = 1 // 1 for From, 2 for To

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_date_calculator)

        tvFromDate = findViewById(R.id.tvFromDate)
        tvToDate = findViewById(R.id.tvToDate)
        tvDiffYears = findViewById(R.id.tvDiffYears)
        tvDiffMonths = findViewById(R.id.tvDiffMonths)
        tvDiffDays = findViewById(R.id.tvDiffDays)
        tvSummaryFrom = findViewById(R.id.tvSummaryFrom)
        tvSummaryTo = findViewById(R.id.tvSummaryTo)

        findViewById<ImageView>(R.id.btnBack).setOnClickListener { finish() }

        findViewById<View>(R.id.btnFromDate).setOnClickListener { 
            activeField = 1
            showDatePicker(true) 
        }
        findViewById<View>(R.id.btnToDate).setOnClickListener { 
            activeField = 2
            showDatePicker(false) 
        }

        updateUI()
    }

    private fun showDatePicker(isFrom: Boolean) {
        val cal = if (isFrom) fromCalendar else toCalendar
        DatePickerDialog(this, { _, y, m, d ->
            cal.set(y, m, d)
            // Reset time to midnight for accurate date comparison
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            updateUI()
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun updateUI() {
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        
        val highlightColor = "#7F00FF".toColorInt()
        val normalColor = "#70757A".toColorInt()

        tvFromDate.text = sdf.format(fromCalendar.time)
        tvToDate.text = sdf.format(toCalendar.time)
        tvSummaryFrom.text = sdf.format(fromCalendar.time)
        tvSummaryTo.text = sdf.format(toCalendar.time)

        if (activeField == 1) {
            tvFromDate.setTextColor(highlightColor)
            tvToDate.setTextColor(normalColor)
        } else {
            tvToDate.setTextColor(highlightColor)
            tvFromDate.setTextColor(normalColor)
        }

        val start = fromCalendar.clone() as Calendar
        val end = toCalendar.clone() as Calendar

        // Ensure we calculate from smaller to larger date
        val isReverse = start.after(end)
        val (calcStart, calcEnd) = if (isReverse) end to start else start to end

        var years = calcEnd.get(Calendar.YEAR) - calcStart.get(Calendar.YEAR)
        var months = calcEnd.get(Calendar.MONTH) - calcStart.get(Calendar.MONTH)
        var days = calcEnd.get(Calendar.DAY_OF_MONTH) - calcStart.get(Calendar.DAY_OF_MONTH)

        if (days < 0) {
            months--
            val temp = calcEnd.clone() as Calendar
            temp.add(Calendar.MONTH, -1)
            days += temp.getActualMaximum(Calendar.DAY_OF_MONTH)
        }
        if (months < 0) {
            years--
            months += 12
        }

        // If it was reversed, we might want to show 0 or negative. 
        // Based on the requirement "difference", showing the absolute difference is common, 
        // but often calculators show 0 if start > end.
        if (isReverse) {
            tvDiffYears.text = "0"
            tvDiffMonths.text = "0"
            tvDiffDays.text = "0"
        } else {
            tvDiffYears.text = years.toString()
            tvDiffMonths.text = months.toString()
            tvDiffDays.text = days.toString()
        }
    }
}
