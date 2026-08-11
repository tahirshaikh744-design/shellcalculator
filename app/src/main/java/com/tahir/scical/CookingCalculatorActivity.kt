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

class CookingCalculatorActivity : BaseCalculatorActivity() {

    private lateinit var tvUnit1Symbol: TextView
    private lateinit var tvUnit1Value: TextView
    private lateinit var tvUnit1Name: TextView
    private lateinit var tvUnit2Symbol: TextView
    private lateinit var tvUnit2Value: TextView
    private lateinit var tvUnit2Name: TextView
    private lateinit var tvHeaderTitle: TextView

    private var activeUnitField = 1
    private var unit1Factor = 236.588 // Cup (US) default
    private var unit2Factor = 14.7868 // Tablespoon (US) default

    private val units = arrayOf(
        CookingUnit("Cup (US)", "cup", 236.588),
        CookingUnit("Cup (Metric)", "cup", 250.0),
        CookingUnit("Tablespoon (US)", "tbsp", 14.7868),
        CookingUnit("Tablespoon (Metric)", "tbsp", 15.0),
        CookingUnit("Teaspoon (US)", "tsp", 4.9289),
        CookingUnit("Teaspoon (Metric)", "tsp", 5.0),
        CookingUnit("Milliliter", "ml", 1.0),
        CookingUnit("Liter", "l", 1000.0),
        CookingUnit("Fluid Ounce (US)", "fl oz", 29.5735),
        CookingUnit("Fluid Ounce (UK)", "fl oz", 28.4131),
        CookingUnit("Pint (US)", "pt", 473.176),
        CookingUnit("Quart (US)", "qt", 946.353),
        CookingUnit("Gallon (US)", "gal", 3785.41)
    )

    private var currentInput = "1"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generic_converter)

        tvHeaderTitle = findViewById(R.id.tvHeaderTitle)
        tvHeaderTitle.text = "Cooking"

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

        tvUnit2Name.text = units[2].name
        tvUnit2Symbol.text = units[2].symbol
        unit2Factor = units[2].factor

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

    private fun showUnitPicker(onSelected: (CookingUnit) -> Unit) {
        val unitNames = units.map { it.name }.toTypedArray()
        MaterialAlertDialogBuilder(this)
            .setTitle("Select Unit")
            .setItems(unitNames) { _, which ->
                onSelected(units[which])
            }
            .show()
    }

    data class CookingUnit(val name: String, val symbol: String, val factor: Double)
}
