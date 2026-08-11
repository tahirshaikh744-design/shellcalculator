package com.tahir.scical

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.core.graphics.toColorInt
import java.io.File
import java.io.FileOutputStream

class BmiDetailsActivity : BaseCalculatorActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bmi_details)

        val bmiValue = intent.getStringExtra("bmi_value") ?: "0.0"
        val bmiCategory = intent.getStringExtra("bmi_category") ?: "Unknown"

        val tvBmiValue = findViewById<TextView>(R.id.tvBmiValue)
        val tvBmiCategory = findViewById<TextView>(R.id.tvBmiCategory)

        tvBmiValue.text = bmiValue
        tvBmiCategory.text = bmiCategory

        // Update category color
        val categoryColor = when (bmiCategory) {
            "Underweight" -> "#4285F4"
            "Normal" -> "#00C09A"
            "Overweight" -> "#FF6D00"
            "Obese" -> "#EA4335"
            else -> "#70757A"
        }.toColorInt()
        
        tvBmiCategory.setTextColor(categoryColor)
        tvBmiValue.setTextColor(if (bmiCategory == "Normal") "#00C09A".toColorInt() else "#FF6D00".toColorInt())

        findViewById<ImageView>(R.id.btnBack).setOnClickListener { finish() }
        
        findViewById<Button>(R.id.btnShare).setOnClickListener {
            shareBmiResults()
        }
    }

    private fun shareBmiResults() {
        val rootView = findViewById<View>(android.R.id.content)
        rootView.post {
            try {
                val bitmap = Bitmap.createBitmap(rootView.width, rootView.height, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(bitmap)
                rootView.draw(canvas)

                val cachePath = File(cacheDir, "images")
                cachePath.mkdirs()
                val imageFile = File(cachePath, "bmi_result.png")
                val stream = FileOutputStream(imageFile)
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                stream.close()

                val contentUri = FileProvider.getUriForFile(this, "$packageName.fileprovider", imageFile)

                if (contentUri != null) {
                    val shareIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        setDataAndType(contentUri, contentResolver.getType(contentUri))
                        putExtra(Intent.EXTRA_STREAM, contentUri)
                        putExtra(Intent.EXTRA_TEXT, "Check out my BMI results calculated using SCI-CAL app. \nDownload now: https://play.google.com/store/apps/details?id=com.tahir.scical")
                        type = "image/png"
                    }
                    startActivity(Intent.createChooser(shareIntent, "Share via"))
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "Failed to share: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
