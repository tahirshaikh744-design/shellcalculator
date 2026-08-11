package com.tahir.scical

import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import java.text.DecimalFormat

class DiscountCalculatorActivity : BaseCalculatorActivity() {

    private lateinit var tvOriginalPrice: TextView
    private lateinit var tvDiscountPercent: TextView
    private lateinit var tvFinalPrice: TextView
    private lateinit var tvYouSave: TextView

    private var activeField = 1 // 1 for Original Price, 2 for Discount %
    private var originalPriceInput = "100"
    private var discountPercentInput = "10"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_discount_calculator)

        tvOriginalPrice = findViewById(R.id.tvOriginalPrice)
        tvDiscountPercent = findViewById(R.id.tvDiscountPercent)
        tvFinalPrice = findViewById(R.id.tvFinalPrice)
        tvYouSave = findViewById(R.id.tvYouSave)

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
            if (activeField == 1) originalPriceInput = "0" else discountPercentInput = "0"
            updateUI()
        }

        findViewById<View>(R.id.btnDelete).setOnClickListener {
            if (activeField == 1) {
                originalPriceInput = if (originalPriceInput.length > 1) originalPriceInput.dropLast(1) else "0"
            } else {
                discountPercentInput = if (discountPercentInput.length > 1) discountPercentInput.dropLast(1) else "0"
            }
            updateUI()
        }

        tvOriginalPrice.setOnClickListener {
            activeField = 1
            updateUI()
        }

        tvDiscountPercent.setOnClickListener {
            activeField = 2
            updateUI()
        }

        updateUI()
    }

    private fun onKeypadClick(text: String) {
        if (activeField == 1) {
            if (text == "." && originalPriceInput.contains(".")) return
            originalPriceInput = if (originalPriceInput == "0" && text != ".") text else originalPriceInput + text
        } else {
            if (text == "." && discountPercentInput.contains(".")) return
            if (discountPercentInput == "0" && text != ".") {
                discountPercentInput = text
            } else {
                val newVal = discountPercentInput + text
                if ((newVal.toDoubleOrNull() ?: 0.0) <= 100.0) {
                    discountPercentInput = newVal
                }
            }
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

        tvOriginalPrice.text = originalPriceInput
        tvDiscountPercent.text = discountPercentInput

        if (activeField == 1) {
            tvOriginalPrice.setTextColor(highlightColor)
            tvDiscountPercent.setTextColor(onSurfaceColor)
        } else {
            tvDiscountPercent.setTextColor(highlightColor)
            tvOriginalPrice.setTextColor(onSurfaceColor)
        }
        
        tvFinalPrice.setTextColor(onSurfaceColor)

        calculateDiscount()
    }

    private fun calculateDiscount() {
        val price = originalPriceInput.toDoubleOrNull() ?: 0.0
        val discount = discountPercentInput.toDoubleOrNull() ?: 0.0
        
        val savings = price * (discount / 100.0)
        val finalPrice = price - savings

        val df = DecimalFormat("#,###.##")
        tvFinalPrice.text = df.format(finalPrice)
        tvYouSave.text = "You save ${df.format(savings)}"
    }
}
