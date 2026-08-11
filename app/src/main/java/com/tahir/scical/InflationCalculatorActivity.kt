package com.tahir.scical

import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import java.text.DecimalFormat
import kotlin.math.pow

class InflationCalculatorActivity : BaseCalculatorActivity() {

    private lateinit var tvCurrentValue: TextView
    private lateinit var tvInflationRate: TextView
    private lateinit var tvDuration: TextView
    private lateinit var tvFutureValue: TextView

    private var activeField = 1 // 1: Value, 2: Rate, 3: Years
    private var amountInput = "1000"
    private var rateInput = "6"
    private var durationInput = "10"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inflation_calculator)

        tvCurrentValue = findViewById(R.id.tvCurrentValue)
        tvInflationRate = findViewById(R.id.tvInflationRate)
        tvDuration = findViewById(R.id.tvDuration)
        tvFutureValue = findViewById(R.id.tvFutureValue)

        findViewById<ImageView>(R.id.btnBack).setOnClickListener { finish() }

        val keypadIds = listOf(
            R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
            R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9, R.id.btnDot
        )

        for (id in keypadIds) {
            findViewById<Button>(id).setOnClickListener { 
                onKeypadClick((it as Button).text.toString()) 
            }
        }

        findViewById<View>(R.id.btnClear).setOnClickListener {
            resetCurrentField()
            updateUI()
        }

        findViewById<View>(R.id.btnDelete).setOnClickListener {
            deleteLastChar()
            updateUI()
        }

        tvCurrentValue.setOnClickListener { activeField = 1; updateUI() }
        tvInflationRate.setOnClickListener { activeField = 2; updateUI() }
        tvDuration.setOnClickListener { activeField = 3; updateUI() }

        updateUI()
    }

    private fun resetCurrentField() {
        when (activeField) {
            1 -> amountInput = "0"
            2 -> rateInput = "0"
            3 -> durationInput = "0"
        }
    }

    private fun deleteLastChar() {
        when (activeField) {
            1 -> amountInput = if (amountInput.length > 1) amountInput.dropLast(1) else "0"
            2 -> rateInput = if (rateInput.length > 1) rateInput.dropLast(1) else "0"
            3 -> durationInput = if (durationInput.length > 1) durationInput.dropLast(1) else "0"
        }
    }

    private fun onKeypadClick(text: String) {
        val currentStr = when (activeField) {
            1 -> amountInput
            2 -> rateInput
            3 -> durationInput
            else -> "0"
        }

        if (text == "." && currentStr.contains(".")) return
        val newStr = if (currentStr == "0" && text != ".") text else currentStr + text
        
        when (activeField) {
            1 -> amountInput = newStr
            2 -> rateInput = newStr
            3 -> durationInput = newStr
        }
        updateUI()
    }

    private fun updateUI() {
        val highlightColor = "#7F00FF".toColorInt()
        val typedValue = TypedValue()
        theme.resolveAttribute(com.google.android.material.R.attr.colorOnSurface, typedValue, true)
        val secondaryColor = if (typedValue.resourceId != 0) ContextCompat.getColor(this, typedValue.resourceId) else typedValue.data

        tvCurrentValue.text = amountInput
        tvInflationRate.text = rateInput
        tvDuration.text = durationInput

        tvCurrentValue.setTextColor(if (activeField == 1) highlightColor else secondaryColor)
        tvInflationRate.setTextColor(if (activeField == 2) highlightColor else secondaryColor)
        tvDuration.setTextColor(if (activeField == 3) highlightColor else secondaryColor)

        calculateInflation()
    }

    private fun calculateInflation() {
        val P = amountInput.toDoubleOrNull() ?: 0.0
        val r = (rateInput.toDoubleOrNull() ?: 0.0) / 100
        val t = durationInput.toDoubleOrNull() ?: 0.0

        val futureValue = P * (1 + r).pow(t)
        val df = DecimalFormat("#,##,###.##")
        tvFutureValue.text = df.format(futureValue)
    }
}
