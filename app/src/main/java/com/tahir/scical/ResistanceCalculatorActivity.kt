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

class ResistanceCalculatorActivity : BaseCalculatorActivity() {

    private lateinit var tvUnit1Symbol: TextView
    private lateinit var tvUnit1Value: TextView
    private lateinit var tvUnit1Name: TextView
    private lateinit var tvUnit2Symbol: TextView
    private lateinit var tvUnit2Value: TextView
    private lateinit var tvUnit2Name: TextView
    private lateinit var tvHeaderTitle: TextView

    private var activeUnitField = 1
    private var unit1Factor = 1.0 // Ohm default
    private var unit2Factor = 1000.0 // Kilohm default

    private val units = arrayOf(
        ResistanceUnit("Ohm", "Ω", 1.0),
        ResistanceUnit("Kilohm", "kΩ", 1000.0),
        ResistanceUnit("Megohm", "MΩ", 1e6),
        ResistanceUnit("Gigaohm", "GΩ", 1e9),
        ResistanceUnit("Milliohm", "mΩ", 0.001),
        ResistanceUnit("Microohm", "µΩ", 1e-6),
        ResistanceUnit("Abohm", "abΩ", 1e-9),
        ResistanceUnit("Statohm", "statΩ", 8.987552e11)
    )

    private var currentInput = "1"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generic_converter)

        tvHeaderTitle = findViewById(R.id.tvHeaderTitle)
        tvHeaderTitle.text = "Resistance"

        tvUnit1Symbol = findViewById(R.id.tvUnit1Symbol)
        tvUnit1Value = findViewById(R.id.tvUnit1Value)
        tvUnit1Name = findViewById(R.id.tvUnit1Name)
        tvUnit2Symbol = findViewById(R.id.tvUnit2Symbol)
        tvUnit2Value = findViewById(R.id.tvUnit2Value)
        tvUnit2Name = findViewById(R.id.tvUnit2Name)

        findViewById<ImageView>(R.id.btnBack).setOnClickListener { finish() }

        tvUnit1Name.text = units[0].name
        tvUnit1Symbol.text = units[0].symbol
        unit1Factor = units[0].factor

        tvUnit2Name.text = units[1].name
        tvUnit2Symbol.text = units[1].symbol
        unit2Factor = units[1].factor

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

        val highlightColor = "#7F00FF".toColorInt()
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

    private fun showUnitPicker(onSelected: (ResistanceUnit) -> Unit) {
        val unitNames = units.map { it.name }.toTypedArray()
        MaterialAlertDialogBuilder(this)
            .setTitle("Select Unit")
            .setItems(unitNames) { _, which ->
                onSelected(units[which])
            }
            .show()
    }

    data class ResistanceUnit(val name: String, val symbol: String, val factor: Double)
}
