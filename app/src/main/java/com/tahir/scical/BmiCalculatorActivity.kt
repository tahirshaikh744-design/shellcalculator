package com.tahir.scical

import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.DecimalFormat

class BmiCalculatorActivity : BaseCalculatorActivity() {

    private lateinit var tvWeightValue: TextView
    private lateinit var tvWeightUnitName: TextView
    private lateinit var tvHeightValue: TextView
    private lateinit var tvHeightUnitName: TextView
    private lateinit var tvWeightLabel: TextView
    private lateinit var tvHeightLabel: TextView

    private var activeField = 1 // 1 for weight, 2 for height
    private var weightUnit = WeightUnit("Kilograms", 1.0)
    private var heightUnit = HeightUnit("Centimeters", 1.0)

    private var weightInput = "60"
    private var heightInput = "170"

    private val weightUnits = arrayOf(
        WeightUnit("Kilograms", 1.0),
        WeightUnit("Pounds", 0.453592),
        WeightUnit("Stones", 6.35029)
    )

    private val heightUnits = arrayOf(
        HeightUnit("Centimeters", 1.0),
        HeightUnit("Meters", 100.0),
        HeightUnit("Feet", 30.48),
        HeightUnit("Inches", 2.54)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bmi_calculator)

        tvWeightValue = findViewById(R.id.tvWeightValue)
        tvWeightUnitName = findViewById(R.id.tvWeightUnitName)
        tvHeightValue = findViewById(R.id.tvHeightValue)
        tvHeightUnitName = findViewById(R.id.tvHeightUnitName)
        tvWeightLabel = findViewById(R.id.tvWeightLabel)
        tvHeightLabel = findViewById(R.id.tvHeightLabel)

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
            if (activeField == 1) {
                weightInput = "0"
                tvWeightValue.setTextSize(TypedValue.COMPLEX_UNIT_SP, 48f)
            } else {
                heightInput = "0"
                tvHeightValue.setTextSize(TypedValue.COMPLEX_UNIT_SP, 48f)
            }
            updateUI()
        }

        findViewById<View>(R.id.btnDelete).setOnClickListener {
            if (activeField == 1) {
                weightInput = if (weightInput.length > 1) weightInput.dropLast(1) else "0"
            } else {
                heightInput = if (heightInput.length > 1) heightInput.dropLast(1) else "0"
            }
            updateUI()
        }

        findViewById<View>(R.id.btnGo).setOnClickListener { calculateBmi() }

        tvWeightLabel.setOnClickListener { showWeightUnitPicker() }
        tvWeightUnitName.setOnClickListener { showWeightUnitPicker() }
        tvHeightLabel.setOnClickListener { showHeightUnitPicker() }
        tvHeightUnitName.setOnClickListener { showHeightUnitPicker() }

        tvWeightValue.setOnClickListener {
            activeField = 1
            updateUI()
        }

        tvHeightValue.setOnClickListener {
            activeField = 2
            updateUI()
        }

        updateUI()
    }

    private fun onKeypadClick(text: String) {
        if (activeField == 1) {
            if (text == "." && weightInput.contains(".")) return
            weightInput = if (weightInput == "0" && text != ".") text else weightInput + text
        } else {
            if (text == "." && heightInput.contains(".")) return
            heightInput = if (heightInput == "0" && text != ".") text else heightInput + text
        }
        updateUI()
    }

    private fun updateUI() {
        val typedValue = TypedValue()
        theme.resolveAttribute(com.google.android.material.R.attr.colorOnSurface, typedValue, true)
        val onSurfaceColor = if (typedValue.resourceId != 0) {
            ContextCompat.getColor(this, typedValue.resourceId)
        } else {
            typedValue.data
        }
        
        val highlightColor = "#7F00FF".toColorInt() // Violet

        tvWeightValue.text = weightInput
        tvHeightValue.text = heightInput

        if (activeField == 1) {
            tvWeightValue.setTextColor(highlightColor)
            tvHeightValue.setTextColor(onSurfaceColor)
        } else {
            tvHeightValue.setTextColor(highlightColor)
            tvWeightValue.setTextColor(onSurfaceColor)
        }
    }

    private fun showWeightUnitPicker() {
        val names = weightUnits.map { it.name }.toTypedArray()
        MaterialAlertDialogBuilder(this)
            .setTitle("Select Weight Unit")
            .setItems(names) { _, which ->
                weightUnit = weightUnits[which]
                tvWeightUnitName.text = weightUnit.name
            }
            .show()
    }

    private fun showHeightUnitPicker() {
        val names = heightUnits.map { it.name }.toTypedArray()
        MaterialAlertDialogBuilder(this)
            .setTitle("Select Height Unit")
            .setItems(names) { _, which ->
                heightUnit = heightUnits[which]
                tvHeightUnitName.text = heightUnit.name
            }
            .show()
    }

    private fun calculateBmi() {
        val w = (weightInput.toDoubleOrNull() ?: 0.0) * weightUnit.factorKg
        val h = ((heightInput.toDoubleOrNull() ?: 0.0) * heightUnit.factorCm) / 100.0
        
        if (h > 0) {
            val bmi = w / (h * h)
            val df = DecimalFormat("#.#")
            val bmiValue = df.format(bmi)
            val bmiCategory = getBmiCategory(bmi)
            
            val intent = Intent(this, BmiDetailsActivity::class.java).apply {
                putExtra("bmi_value", bmiValue)
                putExtra("bmi_category", bmiCategory)
            }
            startActivity(intent)
        }
    }

    private fun getBmiCategory(bmi: Double): String {
        return when {
            bmi < 18.5 -> "Underweight"
            bmi < 25 -> "Normal"
            bmi < 30 -> "Overweight"
            else -> "Obese"
        }
    }

    data class WeightUnit(val name: String, val factorKg: Double)
    data class HeightUnit(val name: String, val factorCm: Double)
}
