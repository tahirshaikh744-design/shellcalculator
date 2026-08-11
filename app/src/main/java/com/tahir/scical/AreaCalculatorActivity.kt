package com.tahir.scical

import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.toColorInt
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.DecimalFormat

class AreaCalculatorActivity : BaseCalculatorActivity() {

    private lateinit var tvUnit1Symbol: TextView
    private lateinit var tvUnit1Value: TextView
    private lateinit var tvUnit1Name: TextView
    private lateinit var tvUnit2Symbol: TextView
    private lateinit var tvUnit2Value: TextView
    private lateinit var tvUnit2Name: TextView

    private var activeUnitField = 1
    private var unit1Factor = 1.0
    private var unit2Factor = 0.0001 // cm2 default

    private val units = arrayOf(
        AreaUnit("Square kilometer", "km²", 1000000.0),
        AreaUnit("Hectare", "ha", 10000.0),
        AreaUnit("Are", "a", 100.0),
        AreaUnit("Square meter", "m²", 1.0),
        AreaUnit("Square decimeter", "dm²", 0.01),
        AreaUnit("Square centimeter", "cm²", 0.0001),
        AreaUnit("Square millimeter", "mm²", 0.000001),
        AreaUnit("Square micron", "µm²", 1e-12),
        AreaUnit("Square mile", "mi²", 2589988.1103),
        AreaUnit("Acre", "ac", 4046.8564),
        AreaUnit("Rood", "ro", 1011.7141),
        AreaUnit("Square rod", "rd²", 25.2928),
        AreaUnit("Square yard", "yd²", 0.836127),
        AreaUnit("Square foot", "ft²", 0.092903),
        AreaUnit("Square inch", "in²", 0.00064516),
        AreaUnit("Square chain", "ch²", 404.6856),
        AreaUnit("Bigha (India)", "bigha", 1618.7),
        AreaUnit("Katha", "katha", 80.935),
        AreaUnit("Dhur", "dhur", 4.04675),
        AreaUnit("Kanal", "kanal", 505.857),
        AreaUnit("Marla", "marla", 25.2928),
        AreaUnit("Guntha", "guntha", 101.17),
        AreaUnit("Ping (Taiwan/Japan)", "ping", 3.3057),
        AreaUnit("Arpent", "arpent", 3418.89),
        AreaUnit("Township", "twp", 93239571.972),
        AreaUnit("Section", "sec", 2589988.1103)
    )

    private var currentInput = "1"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_area_calculator)

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
                val btn = it as Button
                onKeypadClick(btn.text.toString()) 
            }
        }

        findViewById<View>(R.id.btnClear).setOnClickListener {
            currentInput = "0"
            // Reset text sizes to default (48sp)
            tvUnit1Value.setTextSize(TypedValue.COMPLEX_UNIT_SP, 48f)
            tvUnit2Value.setTextSize(TypedValue.COMPLEX_UNIT_SP, 48f)
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
            showUnitPicker { selectedUnit ->
                tvUnit1Symbol.text = selectedUnit.symbol
                tvUnit1Name.text = selectedUnit.name
                unit1Factor = selectedUnit.factor
                updateValues()
            }
        }
        tvUnit1Name.setOnClickListener {
            activeUnitField = 1
            showUnitPicker { selectedUnit ->
                tvUnit1Symbol.text = selectedUnit.symbol
                tvUnit1Name.text = selectedUnit.name
                unit1Factor = selectedUnit.factor
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
            showUnitPicker { selectedUnit ->
                tvUnit2Symbol.text = selectedUnit.symbol
                tvUnit2Name.text = selectedUnit.name
                unit2Factor = selectedUnit.factor
                updateValues()
            }
        }
        tvUnit2Name.setOnClickListener {
            activeUnitField = 2
            showUnitPicker { selectedUnit ->
                tvUnit2Symbol.text = selectedUnit.symbol
                tvUnit2Name.text = selectedUnit.name
                unit2Factor = selectedUnit.factor
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
        if (currentInput == "0" && text != ".") {
            currentInput = text
        } else {
            currentInput += text
        }
        updateValues()
    }

    private fun formatNumber(input: String): String {
        if (input.isEmpty() || input == "0") return input
        val parts = input.split(".")
        val df = DecimalFormat("#,###")
        val formattedInt = df.format(parts[0].toDoubleOrNull() ?: 0.0)
        return if (parts.size > 1) "$formattedInt.${parts[1]}" else formattedInt
    }

    private fun updateValues() {
        val value = currentInput.toDoubleOrNull() ?: 0.0
        val df = DecimalFormat("#,###.########")

        val highlightColor = "#7F00FF".toColorInt() // Violet
        val secondaryColor = "#70757A".toColorInt()

        if (activeUnitField == 1) {
            tvUnit1Value.text = formatNumber(currentInput)
            tvUnit1Value.setTextColor(highlightColor)
            tvUnit2Value.setTextColor(secondaryColor)
            
            val baseValue = value * unit1Factor
            val convertedValue = baseValue / unit2Factor
            tvUnit2Value.text = df.format(convertedValue)
        } else {
            tvUnit2Value.text = formatNumber(currentInput)
            tvUnit2Value.setTextColor(highlightColor)
            tvUnit1Value.setTextColor(secondaryColor)
            
            val baseValue = value * unit2Factor
            val convertedValue = baseValue / unit1Factor
            tvUnit1Value.text = df.format(convertedValue)
        }
    }

    private fun showUnitPicker(onSelected: (AreaUnit) -> Unit) {
        val unitNames = units.map { it.name }.toTypedArray()
        MaterialAlertDialogBuilder(this)
            .setTitle("Select Unit")
            .setItems(unitNames) { _, which ->
                onSelected(units[which])
            }
            .show()
    }

    data class AreaUnit(val name: String, val symbol: String, val factor: Double)
}
