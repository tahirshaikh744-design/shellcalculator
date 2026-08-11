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

class FuelEfficiencyCalculatorActivity : BaseCalculatorActivity() {

    private lateinit var tvUnit1Symbol: TextView
    private lateinit var tvUnit1Value: TextView
    private lateinit var tvUnit1Name: TextView
    private lateinit var tvUnit2Symbol: TextView
    private lateinit var tvUnit2Value: TextView
    private lateinit var tvUnit2Name: TextView

    private var activeUnitField = 1
    private var unit1Type = "km/L"
    private var unit2Type = "L/100km"

    private val units = arrayOf(
        FuelUnit("Kilometer per liter", "km/L"),
        FuelUnit("Liter per 100 kilometers", "L/100km"),
        FuelUnit("Miles per gallon (US)", "MPG (US)"),
        FuelUnit("Miles per gallon (UK)", "MPG (UK)"),
        FuelUnit("Kilometer per gallon (US)", "km/gal (US)"),
        FuelUnit("Miles per liter", "mi/L")
    )

    private var currentInput = "1"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fuel_efficiency_calculator)

        tvUnit1Symbol = findViewById(R.id.tvUnit1Symbol)
        tvUnit1Value = findViewById(R.id.tvUnit1Value)
        tvUnit1Name = findViewById(R.id.tvUnit1Name)
        tvUnit2Symbol = findViewById(R.id.tvUnit2Symbol)
        tvUnit2Value = findViewById(R.id.tvUnit2Value)
        tvUnit2Name = findViewById(R.id.tvUnit2Name)

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
            currentInput = "0"
            updateValues()
        }

        findViewById<View>(R.id.btnDelete).setOnClickListener {
            currentInput = if (currentInput.length > 1) currentInput.dropLast(1) else "0"
            updateValues()
        }

        tvUnit1Symbol.setOnClickListener {
            activeUnitField = 1
            showUnitPicker { selected ->
                tvUnit1Symbol.text = selected.symbol
                tvUnit1Name.text = selected.name
                unit1Type = selected.symbol
                updateValues()
            }
        }
        tvUnit1Value.setOnClickListener {
            activeUnitField = 1
            currentInput = tvUnit1Value.text.toString().replace(",", "")
            updateValues()
        }

        tvUnit2Symbol.setOnClickListener {
            activeUnitField = 2
            showUnitPicker { selected ->
                tvUnit2Symbol.text = selected.symbol
                tvUnit2Name.text = selected.name
                unit2Type = selected.symbol
                updateValues()
            }
        }
        tvUnit2Value.setOnClickListener {
            activeUnitField = 2
            currentInput = tvUnit2Value.text.toString().replace(",", "")
            updateValues()
        }

        updateValues()
    }

    private fun onKeypadClick(text: String) {
        if (text == "." && currentInput.contains(".")) return
        currentInput = if (currentInput == "0" && text != ".") text else currentInput + text
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
            val converted = convertFuel(inputVal, unit1Type, unit2Type)
            tvUnit2Value.text = df.format(converted)
        } else {
            tvUnit2Value.text = currentInput
            tvUnit2Value.setTextColor(highlightColor)
            tvUnit1Value.setTextColor(secondaryColor)
            val converted = convertFuel(inputVal, unit2Type, unit1Type)
            tvUnit1Value.text = df.format(converted)
        }
    }

    private fun convertFuel(value: Double, from: String, to: String): Double {
        if (value <= 0) return 0.0
        // Convert everything to km/L first
        val kmPerL = when (from) {
            "km/L" -> value
            "L/100km" -> 100.0 / value
            "MPG (US)" -> value * 0.425144
            "MPG (UK)" -> value * 0.354006
            "km/gal (US)" -> value / 3.78541
            "mi/L" -> value * 1.60934
            else -> value
        }

        // Convert from km/L to target
        return when (to) {
            "km/L" -> kmPerL
            "L/100km" -> 100.0 / kmPerL
            "MPG (US)" -> kmPerL / 0.425144
            "MPG (UK)" -> kmPerL / 0.354006
            "km/gal (US)" -> kmPerL * 3.78541
            "mi/L" -> kmPerL / 1.60934
            else -> kmPerL
        }
    }

    private fun showUnitPicker(onSelected: (FuelUnit) -> Unit) {
        val unitNames = units.map { it.name }.toTypedArray()
        MaterialAlertDialogBuilder(this)
            .setTitle("Select Unit")
            .setItems(unitNames) { _, which -> onSelected(units[which]) }
            .show()
    }

    data class FuelUnit(val name: String, val symbol: String)
}
