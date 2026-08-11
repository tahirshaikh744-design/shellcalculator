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

class LengthCalculatorActivity : BaseCalculatorActivity() {

    private lateinit var tvUnit1Symbol: TextView
    private lateinit var tvUnit1Value: TextView
    private lateinit var tvUnit1Name: TextView
    private lateinit var tvUnit2Symbol: TextView
    private lateinit var tvUnit2Value: TextView
    private lateinit var tvUnit2Name: TextView

    private var activeUnitField = 1
    private var unit1Factor = 1.0 // Meter default
    private var unit2Factor = 0.01 // Centimeter default

    private val units = arrayOf(
        LengthUnit("Gigameter", "Gm", 1e9),
        LengthUnit("Megameter", "Mm", 1e6),
        LengthUnit("Kilometer", "km", 1000.0),
        LengthUnit("Hectometer", "hm", 100.0),
        LengthUnit("Dekameter", "dam", 10.0),
        LengthUnit("Meter", "m", 1.0),
        LengthUnit("Decimeter", "dm", 0.1),
        LengthUnit("Centimeter", "cm", 0.01),
        LengthUnit("Millimeter", "mm", 0.001),
        LengthUnit("Micrometer", "µm", 1e-6),
        LengthUnit("Nanometer", "nm", 1e-9),
        LengthUnit("Picometer", "pm", 1e-12),
        LengthUnit("Femtometer", "fm", 1e-15),
        LengthUnit("Mile", "mi", 1609.344),
        LengthUnit("Yard", "yd", 0.9144),
        LengthUnit("Foot", "ft", 0.3048),
        LengthUnit("Inch", "in", 0.0254),
        LengthUnit("Nautical mile", "NM", 1852.0),
        LengthUnit("League", "lea", 4828.032),
        LengthUnit("Furlong", "fur", 201.168),
        LengthUnit("Chain", "ch", 20.1168),
        LengthUnit("Rod", "rd", 5.0292),
        LengthUnit("Fathom", "ftm", 1.8288),
        LengthUnit("Link", "li", 0.201168),
        LengthUnit("Hand", "h", 0.1016),
        LengthUnit("Span", "span", 0.2286),
        LengthUnit("Cubit", "cubit", 0.4572),
        LengthUnit("Verst (Russian)", "verst", 1066.8),
        LengthUnit("Li (Chinese)", "li", 500.0),
        LengthUnit("Ri (Japanese)", "ri", 3927.27),
        LengthUnit("Shaku (Japanese)", "shaku", 0.303),
        LengthUnit("Sun (Japanese)", "sun", 0.0303),
        LengthUnit("Kosh (Indian)", "kosh", 3000.0),
        LengthUnit("Ganj (Indian)", "ganj", 0.6096),
        LengthUnit("Gaz (Indian)", "gaz", 0.9144),
        LengthUnit("Mil", "mil", 0.0000254),
        LengthUnit("Angstrom", "Å", 1e-10),
        LengthUnit("Light year", "ly", 9.4607304725808e15),
        LengthUnit("Astronomical unit", "AU", 1.495978707e11),
        LengthUnit("Parsec", "pc", 3.085677581e16)
    )

    private var currentInput = "1"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_length_calculator)

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

    private fun showUnitPicker(onSelected: (LengthUnit) -> Unit) {
        val unitNames = units.map { it.name }.toTypedArray()
        MaterialAlertDialogBuilder(this)
            .setTitle("Select Unit")
            .setItems(unitNames) { _, which ->
                onSelected(units[which])
            }
            .show()
    }

    data class LengthUnit(val name: String, val symbol: String, val factor: Double)
}
