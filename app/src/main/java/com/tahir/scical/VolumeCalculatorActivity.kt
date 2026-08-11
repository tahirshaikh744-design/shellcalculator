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

class VolumeCalculatorActivity : BaseCalculatorActivity() {

    private lateinit var tvUnit1Symbol: TextView
    private lateinit var tvUnit1Value: TextView
    private lateinit var tvUnit1Name: TextView
    private lateinit var tvUnit2Symbol: TextView
    private lateinit var tvUnit2Value: TextView
    private lateinit var tvUnit2Name: TextView

    private var activeUnitField = 1
    private var unit1Factor = 1.0 // Cubic meter default
    private var unit2Factor = 0.001 // Liter default

    private val units = arrayOf(
        VolumeUnit("Cubic kilometer", "km³", 1e9),
        VolumeUnit("Cubic meter", "m³", 1.0),
        VolumeUnit("Cubic decimeter", "dm³", 0.001),
        VolumeUnit("Cubic centimeter", "cm³", 1e-6),
        VolumeUnit("Cubic millimeter", "mm³", 1e-9),
        VolumeUnit("Cubic micrometer", "µm³", 1e-18),
        VolumeUnit("Cubic nanometer", "nm³", 1e-27),
        VolumeUnit("Liter", "L", 0.001),
        VolumeUnit("Hectoliter", "hL", 0.1),
        VolumeUnit("Deciliter", "dL", 0.0001),
        VolumeUnit("Centiliter", "cL", 1e-5),
        VolumeUnit("Milliliter", "mL", 1e-6),
        VolumeUnit("Microliter", "µL", 1e-9),
        VolumeUnit("Nanoliter", "nL", 1e-12),
        VolumeUnit("Stere", "st", 1.0),
        VolumeUnit("Cubic mile", "mi³", 4168181825.4405794),
        VolumeUnit("Cubic yard", "yd³", 0.764554857984),
        VolumeUnit("Cubic foot", "ft³", 0.028316846592),
        VolumeUnit("Cubic inch", "in³", 0.000016387064),
        VolumeUnit("Acre-foot", "ac-ft", 1233.4818375475),
        VolumeUnit("US gallon", "gal (US)", 0.003785411784),
        VolumeUnit("US quart", "qt (US)", 0.000946352946),
        VolumeUnit("US pint", "pt (US)", 0.000473176473),
        VolumeUnit("US cup", "cup (US)", 0.0002365882365),
        VolumeUnit("US gill", "gi (US)", 0.00011829411825),
        VolumeUnit("US fluid ounce", "fl oz (US)", 0.0000295735295625),
        VolumeUnit("US tablespoon", "tbsp (US)", 0.00001478676478125),
        VolumeUnit("US teaspoon", "tsp (US)", 4.92892159375e-6),
        VolumeUnit("US minim", "min (US)", 6.1611519921875e-8),
        VolumeUnit("Imperial gallon", "gal (UK)", 0.00454609),
        VolumeUnit("Imperial quart", "qt (UK)", 0.0011365225),
        VolumeUnit("Imperial pint", "pt (UK)", 0.00056826125),
        VolumeUnit("Imperial cup", "cup (UK)", 0.000284130625),
        VolumeUnit("Imperial gill", "gi (UK)", 0.0001420653125),
        VolumeUnit("Imperial fluid ounce", "fl oz (UK)", 0.0000284130625),
        VolumeUnit("Imperial tablespoon", "tbsp (UK)", 0.0000177581640625),
        VolumeUnit("Imperial teaspoon", "tsp (UK)", 5.919388020833333e-6),
        VolumeUnit("Imperial minim", "min (UK)", 5.919388020833333e-8),
        VolumeUnit("US bushel", "bu (US)", 0.03523907),
        VolumeUnit("US peck", "pk (US)", 0.0088097675),
        VolumeUnit("US dry gallon", "gal (US dry)", 0.00440488377086),
        VolumeUnit("US dry quart", "qt (US dry)", 0.001101220942715),
        VolumeUnit("US dry pint", "pt (US dry)", 0.0005506104713575),
        VolumeUnit("Imperial bushel", "bu (UK)", 0.03636872),
        VolumeUnit("Imperial peck", "pk (UK)", 0.00909218),
        VolumeUnit("Oil barrel", "bbl", 0.158987294928),
        VolumeUnit("Standard barrel", "bbl", 0.119240471196),
        VolumeUnit("Beer barrel", "bbl", 0.117347765304),
        VolumeUnit("Board foot", "fbm", 0.002359737),
        VolumeUnit("Cord", "cd", 3.624556),
        VolumeUnit("Drop", "gtt", 5e-8)
    )

    private var currentInput = "1"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_volume_calculator)

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

    private fun showUnitPicker(onSelected: (VolumeUnit) -> Unit) {
        val unitNames = units.map { it.name }.toTypedArray()
        MaterialAlertDialogBuilder(this)
            .setTitle("Select Unit")
            .setItems(unitNames) { _, which ->
                onSelected(units[which])
            }
            .show()
    }

    data class VolumeUnit(val name: String, val symbol: String, val factor: Double)
}
