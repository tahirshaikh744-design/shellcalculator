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

class PressureCalculatorActivity : BaseCalculatorActivity() {

    private lateinit var tvUnit1Symbol: TextView
    private lateinit var tvUnit1Value: TextView
    private lateinit var tvUnit1Name: TextView
    private lateinit var tvUnit2Symbol: TextView
    private lateinit var tvUnit2Value: TextView
    private lateinit var tvUnit2Name: TextView

    private var activeUnitField = 1
    private var unit1Factor = 1.0 // Pascal default
    private var unit2Factor = 100000.0 // Bar default

    private val units = arrayOf(
        PressureUnit("Pascal", "Pa", 1.0),
        PressureUnit("Bar", "bar", 100000.0),
        PressureUnit("Pound per square inch", "psi", 6894.75729),
        PressureUnit("Atmosphere", "atm", 101325.0),
        PressureUnit("Millimeters of mercury", "mmHg", 133.322387),
        PressureUnit("Torr", "Torr", 133.322368),
        PressureUnit("Kilopascal", "kPa", 1000.0),
        PressureUnit("Megapascal", "MPa", 1000000.0)
    )

    private var currentInput = "1"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pressure_calculator)

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
                unit1Factor = selected.factor
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
                unit2Factor = selected.factor
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
        val value = currentInput.toDoubleOrNull() ?: 0.0
        val df = DecimalFormat("#,###.########")
        val highlightColor = "#7F00FF".toColorInt()
        val secondaryColor = "#70757A".toColorInt()

        if (activeUnitField == 1) {
            tvUnit1Value.text = currentInput
            tvUnit1Value.setTextColor(highlightColor)
            tvUnit2Value.setTextColor(secondaryColor)
            val converted = (value * unit1Factor) / unit2Factor
            tvUnit2Value.text = df.format(converted)
        } else {
            tvUnit2Value.text = currentInput
            tvUnit2Value.setTextColor(highlightColor)
            tvUnit1Value.setTextColor(secondaryColor)
            val converted = (value * unit2Factor) / unit1Factor
            tvUnit1Value.text = df.format(converted)
        }
    }

    private fun showUnitPicker(onSelected: (PressureUnit) -> Unit) {
        val names = units.map { it.name }.toTypedArray()
        MaterialAlertDialogBuilder(this)
            .setTitle("Select Unit")
            .setItems(names) { _, which -> onSelected(units[which]) }
            .show()
    }

    data class PressureUnit(val name: String, val symbol: String, val factor: Double)
}
