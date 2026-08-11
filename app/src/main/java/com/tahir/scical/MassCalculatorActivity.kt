package com.tahir.scical

import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toColorInt
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.DecimalFormat

class MassCalculatorActivity : BaseCalculatorActivity() {

    private lateinit var tvUnit1Symbol: TextView
    private lateinit var tvUnit1Value: TextView
    private lateinit var tvUnit1Name: TextView
    private lateinit var tvUnit2Symbol: TextView
    private lateinit var tvUnit2Value: TextView
    private lateinit var tvUnit2Name: TextView

    private var activeUnitField = 1
    private var unit1Factor = 1.0 // Kilogram default
    private var unit2Factor = 0.001 // Gram default

    private val units = arrayOf(
        MassUnit("Solar Mass", "M☉", 1.98847e30),
        MassUnit("Earth Mass", "M⊕", 5.9722e24),
        MassUnit("Imperial ton", "imp. t", 1016.0469088),
        MassUnit("US ton", "US t", 907.18474),
        MassUnit("Tonne", "t", 1000.0),
        MassUnit("Quintal", "q", 100.0),
        MassUnit("Picul", "picul", 60.0),
        MassUnit("Maund", "maund", 37.3242),
        MassUnit("Stone", "st", 6.35029318),
        MassUnit("Kilogram", "kg", 1.0),
        MassUnit("Seer", "seer", 0.9331),
        MassUnit("Pound", "lb", 0.45359237),
        MassUnit("Troy pound", "t lb", 0.3732417),
        MassUnit("Catty (Jin)", "catty", 0.5),
        MassUnit("Tael", "tael", 0.05),
        MassUnit("Troy ounce", "t oz", 0.03110348),
        MassUnit("Ounce", "oz", 0.02834952),
        MassUnit("Tola", "tola", 0.0116638),
        MassUnit("Bhori", "bhori", 0.0116638),
        MassUnit("Dram", "dr", 0.0017718),
        MassUnit("Pennyweight", "dwt", 0.00155517),
        MassUnit("Gram", "g", 0.001),
        MassUnit("Carat", "ct", 0.0002),
        MassUnit("Ratti", "ratti", 0.0001215),
        MassUnit("Milligram", "mg", 0.000001),
        MassUnit("Grain", "gr", 0.0000647989),
        MassUnit("Microgram", "µg", 1e-9),
        MassUnit("Atomic mass unit", "u", 1.660539e-27)
    )

    private var currentInput = "1"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mass_calculator)

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
        currentInput = if (currentInput == "0" && text != ".") {
            text
        } else {
            currentInput + text
        }
        updateValues()
    }

    private fun formatNumber(input: String): String {
        if (input.isEmpty() || input == "0") return input
        val parts = input.split(".")
        val df = DecimalFormat("#,###")
        val formattedInt = try {
            df.format(parts[0].toDouble())
        } catch (e: Exception) {
            parts[0]
        }
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

    private fun showUnitPicker(onSelected: (MassUnit) -> Unit) {
        val unitNames = units.map { it.name }.toTypedArray()
        MaterialAlertDialogBuilder(this)
            .setTitle("Select Unit")
            .setItems(unitNames) { _, which ->
                onSelected(units[which])
            }
            .show()
    }

    data class MassUnit(val name: String, val symbol: String, val factor: Double)
}
