package com.tahir.scical

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import com.google.android.material.button.MaterialButton
import java.text.DecimalFormat

class GstCalculatorActivity : BaseCalculatorActivity() {

    private lateinit var tvOriginalPrice: TextView
    private lateinit var tvFinalPrice: TextView
    private lateinit var tvGstBreakdown: TextView
    private lateinit var btnGst3: MaterialButton
    private lateinit var btnGst5: MaterialButton
    private lateinit var btnGst12: MaterialButton
    private lateinit var btnGst18: MaterialButton
    private lateinit var btnGst28: MaterialButton

    private var currentInput = "100"
    private var currentGstRate = 3.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gst_calculator)

        tvOriginalPrice = findViewById(R.id.tvOriginalPrice)
        tvFinalPrice = findViewById(R.id.tvFinalPrice)
        tvGstBreakdown = findViewById(R.id.tvGstBreakdown)

        btnGst3 = findViewById(R.id.btnGst3)
        btnGst5 = findViewById(R.id.btnGst5)
        btnGst12 = findViewById(R.id.btnGst12)
        btnGst18 = findViewById(R.id.btnGst18)
        btnGst28 = findViewById(R.id.btnGst28)

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
            tvOriginalPrice.setTextSize(TypedValue.COMPLEX_UNIT_SP, 48f)
            updateUI()
        }

        findViewById<View>(R.id.btnDelete).setOnClickListener {
            currentInput = if (currentInput.length > 1) currentInput.dropLast(1) else "0"
            updateUI()
        }

        val gstButtons = mapOf(
            btnGst3 to 3.0,
            btnGst5 to 5.0,
            btnGst12 to 12.0,
            btnGst18 to 18.0,
            btnGst28 to 28.0
        )

        for ((btn, rate) in gstButtons) {
            btn.setOnClickListener {
                currentGstRate = rate
                updateUI()
            }
        }

        updateUI()
    }

    private fun onKeypadClick(text: String) {
        if (text == "." && currentInput.contains(".")) return
        currentInput = if (currentInput == "0" && text != ".") text else currentInput + text
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

        tvOriginalPrice.text = currentInput

        val originalPrice = currentInput.toDoubleOrNull() ?: 0.0
        val gstAmount = originalPrice * (currentGstRate / 100.0)
        val finalPrice = originalPrice + gstAmount
        val halfGst = gstAmount / 2.0

        val df = DecimalFormat("#,###.##")
        tvFinalPrice.text = df.format(finalPrice)
        tvGstBreakdown.text = "CGST/SGST: ${df.format(halfGst)}"

        // Update button states
        val rates = listOf(3.0, 5.0, 12.0, 18.0, 28.0)
        val buttons = listOf(btnGst3, btnGst5, btnGst12, btnGst18, btnGst28)
        
        for (i in buttons.indices) {
            val btn = buttons[i]
            if (rates[i] == currentGstRate) {
                btn.backgroundTintList = ColorStateList.valueOf(highlightColor)
                btn.setTextColor(Color.WHITE)
                btn.strokeWidth = 0
            } else {
                btn.backgroundTintList = ColorStateList.valueOf(Color.TRANSPARENT)
                btn.setTextColor(onSurfaceColor)
                btn.strokeWidth = 2
                btn.strokeColor = ColorStateList.valueOf(onSurfaceColor)
            }
        }
    }
}
