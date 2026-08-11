package com.tahir.scical

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toColorInt
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.DecimalFormat

class TemperatureCalculatorActivity : BaseCalculatorActivity() {

    private lateinit var tvUnit1Symbol: TextView
    private lateinit var tvUnit1Value: TextView
    private lateinit var tvUnit1Name: TextView
    private lateinit var tvUnit2Symbol: TextView
    private lateinit var tvUnit2Value: TextView
    private lateinit var tvUnit2Name: TextView

    private var activeUnitField = 1
    private var unit1Type = "Celsius"
    private var unit2Type = "Fahrenheit"

    private val units = arrayOf(
        TempUnit("Celsius", "°C"),
        TempUnit("Fahrenheit", "°F"),
        TempUnit("Kelvin", "K"),
        TempUnit("Rankine", "°R"),
        TempUnit("Reaumur", "°Re")
    )

    private var currentInput = "0"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_temperature_calculator)

        tvUnit1Symbol = findViewById(R.id.tvUnit1Symbol)
        tvUnit1Value = findViewById(R.id.tvUnit1Value)
        tvUnit1Name = findViewById(R.id.tvUnit1Name)
        tvUnit2Symbol = findViewById(R.id.tvUnit2Symbol)
        tvUnit2Value = findViewById(R.id.tvUnit2Value)
        tvUnit2Name = findViewById(R.id.tvUnit2Name)

        findViewById<ImageView>(R.id.btnBack).setOnClickListener { finish() }

        val keypadIds = listOf(
            R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
            R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9, R.id.btnDot, R.id.btnMinus
        )

        for (id in keypadIds) {
            findViewById<Button>(id).setOnClickListener { 
                onKeypadClick((it as Button).text.toString()) 
            }
        }

        findViewById<View>(R.id.btnClear).setOnClickListener {
            currentInput = "0"
            updateValues()
        }

        findViewById<View>(R.id.btnDelete).setOnClickListener {
            currentInput = if (currentInput.length > 1) {
                currentInput.substring(0, currentInput.length - 1)
            } else {
                "0"
            }
            updateValues()
        }

        // Unit 1 Selectors
        tvUnit1Symbol.setOnClickListener {
            activeUnitField = 1
            showUnitPicker { selected ->
                tvUnit1Symbol.text = selected.symbol
                tvUnit1Name.text = selected.name
                unit1Type = selected.name
                updateValues()
            }
        }
        tvUnit1Value.setOnClickListener {
            if (activeUnitField != 1) {
                activeUnitField = 1
                currentInput = tvUnit1Value.text.toString().replace(",", "")
                updateValues()
            }
        }

        // Unit 2 Selectors
        tvUnit2Symbol.setOnClickListener {
            activeUnitField = 2
            showUnitPicker { selected ->
                tvUnit2Symbol.text = selected.symbol
                tvUnit2Name.text = selected.name
                unit2Type = selected.name
                updateValues()
            }
        }
        tvUnit2Value.setOnClickListener {
            if (activeUnitField != 2) {
                activeUnitField = 2
                currentInput = tvUnit2Value.text.toString().replace(",", "")
                updateValues()
            }
        }

        updateValues()
    }

    private fun onKeypadClick(text: String) {
        if (text == "." && currentInput.contains(".")) return
        if (text == "-") {
            currentInput = if (currentInput.startsWith("-")) {
                currentInput.substring(1)
            } else {
                if (currentInput == "0") "-0" else "-$currentInput"
            }
        } else {
            currentInput = if (currentInput == "0") text else if (currentInput == "-0") "-$text" else currentInput + text
        }
        updateValues()
    }

    private fun updateValues() {
        val inputVal = currentInput.toDoubleOrNull() ?: 0.0
        val df = DecimalFormat("#,###.########")

        val highlightColor = "#7F00FF".toColorInt()
        val secondaryColor = "#70757A".toColorInt()

        if (activeUnitField == 1) {
            tvUnit1Value.text = currentInput
            tvUnit1Value.setTextColor(highlightColor)
            tvUnit2Value.setTextColor(secondaryColor)
            
            val converted = convertTemperature(inputVal, unit1Type, unit2Type)
            tvUnit2Value.text = df.format(converted)
        } else {
            tvUnit2Value.text = currentInput
            tvUnit2Value.setTextColor(highlightColor)
            tvUnit1Value.setTextColor(secondaryColor)
            
            val converted = convertTemperature(inputVal, unit2Type, unit1Type)
            tvUnit1Value.text = df.format(converted)
        }
    }

    private fun convertTemperature(value: Double, from: String, to: String): Double {
        // Convert to Celsius first
        val celsius = when (from) {
            "Celsius" -> value
            "Fahrenheit" -> (value - 32.0) * 5.0 / 9.0
            "Kelvin" -> value - 273.15
            "Rankine" -> (value - 491.67) * 5.0 / 9.0
            "Reaumur" -> value * 1.25
            else -> value
        }

        // Convert from Celsius to Target
        return when (to) {
            "Celsius" -> celsius
            "Fahrenheit" -> celsius * 9.0 / 5.0 + 32.0
            "Kelvin" -> celsius + 273.15
            "Rankine" -> (celsius + 273.15) * 9.0 / 5.0
            "Reaumur" -> celsius * 0.8
            else -> celsius
        }
    }

    private fun showUnitPicker(onSelected: (TempUnit) -> Unit) {
        val unitNames = units.map { it.name }.toTypedArray()
        MaterialAlertDialogBuilder(this)
            .setTitle("Select Unit")
            .setItems(unitNames) { _, which ->
                onSelected(units[which])
            }
            .show()
    }

    data class TempUnit(val name: String, val symbol: String)
}
