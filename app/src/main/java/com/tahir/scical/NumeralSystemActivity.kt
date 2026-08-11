package com.tahir.scical

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toColorInt
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.*

class NumeralSystemActivity : BaseCalculatorActivity() {

    private lateinit var tvUnit1Symbol: TextView
    private lateinit var tvUnit1Value: TextView
    private lateinit var tvUnit1Name: TextView
    private lateinit var tvUnit2Symbol: TextView
    private lateinit var tvUnit2Value: TextView
    private lateinit var tvUnit2Name: TextView

    private var activeUnitField = 1
    private var unit1Base = 10
    private var unit2Base = 2

    private val systems = arrayOf(
        NumeralBase("Decimal", "DEC", 10),
        NumeralBase("Binary", "BIN", 2),
        NumeralBase("Hexadecimal", "HEX", 16),
        NumeralBase("Octal", "OCT", 8)
    )

    private var currentInput = "1"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_numeral_system)

        tvUnit1Symbol = findViewById(R.id.tvUnit1Symbol)
        tvUnit1Value = findViewById(R.id.tvUnit1Value)
        tvUnit1Name = findViewById(R.id.tvUnit1Name)
        tvUnit2Symbol = findViewById(R.id.tvUnit2Symbol)
        tvUnit2Value = findViewById(R.id.tvUnit2Value)
        tvUnit2Name = findViewById(R.id.tvUnit2Name)

        findViewById<ImageView>(R.id.btnBack).setOnClickListener { finish() }

        val keypadIds = listOf(
            R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
            R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9,
            R.id.btnA, R.id.btnB, R.id.btnC, R.id.btnD, R.id.btnE, R.id.btnF
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
            showBasePicker { selected ->
                tvUnit1Symbol.text = selected.symbol
                tvUnit1Name.text = selected.name
                unit1Base = selected.base
                currentInput = "0"
                updateValues()
            }
        }
        tvUnit1Value.setOnClickListener {
            if (activeUnitField != 1) {
                activeUnitField = 1
                currentInput = tvUnit1Value.text.toString()
                updateValues()
            }
        }

        // Unit 2 Selectors
        tvUnit2Symbol.setOnClickListener {
            activeUnitField = 2
            showBasePicker { selected ->
                tvUnit2Symbol.text = selected.symbol
                tvUnit2Name.text = selected.name
                unit2Base = selected.base
                currentInput = "0"
                updateValues()
            }
        }
        tvUnit2Value.setOnClickListener {
            if (activeUnitField != 2) {
                activeUnitField = 2
                currentInput = tvUnit2Value.text.toString()
                updateValues()
            }
        }

        updateValues()
    }

    private fun onKeypadClick(text: String) {
        val base = if (activeUnitField == 1) unit1Base else unit2Base
        val digitValue = text.toIntOrNull(16) ?: -1
        
        if (digitValue >= base) return // Disable keys not valid for current base

        currentInput = if (currentInput == "0") {
            text
        } else {
            currentInput + text
        }
        updateValues()
    }

    private fun updateValues() {
        val highlightColor = "#7F00FF".toColorInt()
        val secondaryColor = "#70757A".toColorInt()

        // Enable/Disable keys based on active base
        updateKeypadState()

        if (activeUnitField == 1) {
            tvUnit1Value.text = currentInput
            tvUnit1Value.setTextColor(highlightColor)
            tvUnit2Value.setTextColor(secondaryColor)
            
            try {
                val decimalValue = currentInput.toLong(unit1Base)
                tvUnit2Value.text = decimalValue.toString(unit2Base).uppercase(Locale.US)
            } catch (e: Exception) {
                tvUnit2Value.text = "0"
            }
        } else {
            tvUnit2Value.text = currentInput
            tvUnit2Value.setTextColor(highlightColor)
            tvUnit1Value.setTextColor(secondaryColor)
            
            try {
                val decimalValue = currentInput.toLong(unit2Base)
                tvUnit1Value.text = decimalValue.toString(unit1Base).uppercase(Locale.US)
            } catch (e: Exception) {
                tvUnit1Value.text = "0"
            }
        }
    }

    private fun updateKeypadState() {
        val base = if (activeUnitField == 1) unit1Base else unit2Base
        val keypadIds = listOf(
            R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
            R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9,
            R.id.btnA, R.id.btnB, R.id.btnC, R.id.btnD, R.id.btnE, R.id.btnF
        )

        for (id in keypadIds) {
            val btn = findViewById<Button>(id)
            val digitValue = btn.text.toString().toIntOrNull(16) ?: -1
            if (digitValue >= base) {
                btn.isEnabled = false
                btn.alpha = 0.3f
            } else {
                btn.isEnabled = true
                btn.alpha = 1.0f
            }
        }
    }

    private fun showBasePicker(onSelected: (NumeralBase) -> Unit) {
        val names = systems.map { it.name }.toTypedArray()
        MaterialAlertDialogBuilder(this)
            .setTitle("Select System")
            .setItems(names) { _, which ->
                onSelected(systems[which])
            }
            .show()
    }

    data class NumeralBase(val name: String, val symbol: String, val base: Int)
}
