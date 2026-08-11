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

class SpeedCalculatorActivity : BaseCalculatorActivity() {

    private lateinit var tvUnit1Symbol: TextView
    private lateinit var tvUnit1Value: TextView
    private lateinit var tvUnit1Name: TextView
    private lateinit var tvUnit2Symbol: TextView
    private lateinit var tvUnit2Value: TextView
    private lateinit var tvUnit2Name: TextView

    private var activeUnitField = 1
    private var unit1Factor = 1.0 // m/s default
    private var unit2Factor = 0.2777777778 // km/h default

    private val units = arrayOf(
        SpeedUnit("Light speed", "c", 299792458.0),
        SpeedUnit("Solar system speed", "v☉", 230000.0),
        SpeedUnit("Earth orbital speed", "v⊕", 29780.0),
        SpeedUnit("Kilometer per second", "km/s", 1000.0),
        SpeedUnit("Mile per second", "mi/s", 1609.344),
        SpeedUnit("Mach (STP)", "Ma", 340.29),
        SpeedUnit("Earth rotation (Equator)", "vℯ", 460.0),
        SpeedUnit("Kilometer per minute", "km/min", 16.6666666667),
        SpeedUnit("Mile per minute", "mi/min", 26.8224),
        SpeedUnit("Knot", "kn", 0.5144444444),
        SpeedUnit("Meter per second", "m/s", 1.0),
        SpeedUnit("Kilometer per hour", "km/h", 0.2777777778),
        SpeedUnit("Mile per hour", "mph", 0.44704),
        SpeedUnit("Yard per second", "yd/s", 0.9144),
        SpeedUnit("Foot per second", "ft/s", 0.3048),
        SpeedUnit("Meter per minute", "m/min", 0.0166666667),
        SpeedUnit("Decimeter per second", "dm/s", 0.1),
        SpeedUnit("Inch per second", "in/s", 0.0254),
        SpeedUnit("Centimeter per second", "cm/s", 0.01),
        SpeedUnit("Yard per minute", "yd/min", 0.01524),
        SpeedUnit("Foot per minute", "ft/min", 0.00508),
        SpeedUnit("Millimeter per second", "mm/s", 0.001),
        SpeedUnit("Meter per hour", "m/h", 0.0002777778),
        SpeedUnit("Inch per minute", "in/min", 0.0004233333),
        SpeedUnit("Yard per hour", "yd/h", 0.000254),
        SpeedUnit("Foot per hour", "ft/h", 0.0000846667),
        SpeedUnit("Inch per hour", "in/h", 0.0000070556),
        SpeedUnit("Micrometer per second", "µm/s", 1e-6),
        SpeedUnit("Nanometer per second", "nm/s", 1e-9)
    )

    private var currentInput = "1"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_speed_calculator)

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

    private fun updateValues() {
        val value = currentInput.toDoubleOrNull() ?: 0.0
        val df = DecimalFormat("#,###.########")

        val highlightColor = "#7F00FF".toColorInt() // Violet
        val secondaryColor = "#70757A".toColorInt()

        if (activeUnitField == 1) {
            tvUnit1Value.text = currentInput
            tvUnit1Value.setTextColor(highlightColor)
            tvUnit2Value.setTextColor(secondaryColor)
            
            val baseValue = value * unit1Factor
            val convertedValue = baseValue / unit2Factor
            tvUnit2Value.text = df.format(convertedValue)
        } else {
            tvUnit2Value.text = currentInput
            tvUnit2Value.setTextColor(highlightColor)
            tvUnit1Value.setTextColor(secondaryColor)
            
            val baseValue = value * unit2Factor
            val convertedValue = baseValue / unit1Factor
            tvUnit1Value.text = df.format(convertedValue)
        }
    }

    private fun showUnitPicker(onSelected: (SpeedUnit) -> Unit) {
        val unitNames = units.map { it.name }.toTypedArray()
        MaterialAlertDialogBuilder(this)
            .setTitle("Select Unit")
            .setItems(unitNames) { _, which ->
                onSelected(units[which])
            }
            .show()
    }

    data class SpeedUnit(val name: String, val symbol: String, val factor: Double)
}
