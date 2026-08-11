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

class PowerCalculatorActivity : BaseCalculatorActivity() {

    private lateinit var tvUnit1Symbol: TextView
    private lateinit var tvUnit1Value: TextView
    private lateinit var tvUnit1Name: TextView
    private lateinit var tvUnit2Symbol: TextView
    private lateinit var tvUnit2Value: TextView
    private lateinit var tvUnit2Name: TextView

    private var activeUnitField = 1
    private var unit1Factor = 1.0 // Watt as base
    private var unit2Factor = 1000.0 // Kilowatt

    private val units = arrayOf(
        PowerUnit("Watt", "W", 1.0),
        PowerUnit("Kilowatt", "kW", 1000.0),
        PowerUnit("Megawatt", "MW", 1000000.0),
        PowerUnit("Horsepower (Imperial)", "hp", 745.7),
        PowerUnit("Horsepower (Metric)", "ps", 735.5),
        PowerUnit("BTU/hour", "BTU/h", 0.29307),
        PowerUnit("Foot-pound/minute", "ft-lb/min", 0.022597)
    )

    private var currentInput = "1"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_power_calculator)

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
            findViewById<Button>(id).setOnClickListener { onKeypadClick((it as Button).text.toString()) }
        }

        findViewById<View>(R.id.btnClear).setOnClickListener {
            currentInput = "0"
            updateValues()
        }

        findViewById<View>(R.id.btnDelete).setOnClickListener {
            currentInput = if (currentInput.length > 1) currentInput.dropLast(1) else "0"
            updateValues()
        }

        tvUnit1Symbol.setOnClickListener { showUnitPicker(true) }
        tvUnit1Value.setOnClickListener { activeUnitField = 1; currentInput = tvUnit1Value.text.toString().replace(",", ""); updateValues() }

        tvUnit2Symbol.setOnClickListener { showUnitPicker(false) }
        tvUnit2Value.setOnClickListener { activeUnitField = 2; currentInput = tvUnit2Value.text.toString().replace(",", ""); updateValues() }

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
        
        val typedValue = TypedValue()
        theme.resolveAttribute(com.google.android.material.R.attr.colorOnSurface, typedValue, true)
        val secondaryColor = typedValue.data

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

    private fun showUnitPicker(isUnit1: Boolean) {
        val names = units.map { "${it.name} (${it.symbol})" }.toTypedArray()
        MaterialAlertDialogBuilder(this)
            .setTitle("Select Unit")
            .setItems(names) { _, which ->
                val selected = units[which]
                if (isUnit1) {
                    tvUnit1Symbol.text = selected.symbol
                    tvUnit1Name.text = selected.name
                    unit1Factor = selected.factor
                } else {
                    tvUnit2Symbol.text = selected.symbol
                    tvUnit2Name.text = selected.name
                    unit2Factor = selected.factor
                }
                updateValues()
            }.show()
    }

    data class PowerUnit(val name: String, val symbol: String, val factor: Double)
}
