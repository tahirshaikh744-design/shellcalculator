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
import java.io.File
import java.io.FileOutputStream

class ResultDetailsActivity : BaseCalculatorActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result_details)

        val mainLabel = intent.getStringExtra("mainLabel") ?: ""
        val duration = intent.getStringExtra("duration") ?: ""
        val mainValue = intent.getStringExtra("mainValue") ?: ""
        val secondaryLabel = intent.getStringExtra("secondaryLabel") ?: ""
        val secondaryValue = intent.getStringExtra("secondaryValue") ?: ""
        val bottomLabel1 = intent.getStringExtra("bottomLabel1") ?: ""
        val bottomValue1 = intent.getStringExtra("bottomValue1") ?: ""
        val bottomLabel2 = intent.getStringExtra("bottomLabel2") ?: ""
        val bottomValue2 = intent.getStringExtra("bottomValue2") ?: ""
        val weight1 = intent.getFloatExtra("weight1", 50f)
        val weight2 = intent.getFloatExtra("weight2", 50f)

        findViewById<TextView>(R.id.tvMainLabel).text = mainLabel
        findViewById<TextView>(R.id.tvDurationDisplay).text = duration
        findViewById<TextView>(R.id.tvMainValue).text = mainValue
        findViewById<TextView>(R.id.tvSecondaryLabel).text = secondaryLabel
        findViewById<TextView>(R.id.tvSecondaryValue).text = secondaryValue
        findViewById<TextView>(R.id.tvBottomLabel1).text = bottomLabel1
        findViewById<TextView>(R.id.tvBottomValue1).text = bottomValue1
        findViewById<TextView>(R.id.tvBottomLabel2).text = bottomLabel2
        findViewById<TextView>(R.id.tvBottomValue2).text = bottomValue2

        findViewById<View>(R.id.viewPrincipal).layoutParams = (findViewById<View>(R.id.viewPrincipal).layoutParams as android.widget.LinearLayout.LayoutParams).apply {
            weight = weight1
        }
        findViewById<View>(R.id.viewInterest).layoutParams = (findViewById<View>(R.id.viewInterest).layoutParams as android.widget.LinearLayout.LayoutParams).apply {
            weight = weight2
        }

        findViewById<ImageView>(R.id.btnBack).setOnClickListener { finish() }
        
        findViewById<Button>(R.id.btnShare).setOnClickListener {
            shareResults()
        }

        findViewById<Button>(R.id.btnStats).setOnClickListener {
            val statsIntent = Intent(this, StatsActivity::class.java).apply {
                // Pass raw numeric data using standard keys for StatsActivity
                putExtra("rawPrincipal", intent.getDoubleExtra("rawPrincipal", 0.0))
                putExtra("rawRate", intent.getDoubleExtra("rawRate", 0.0))
                putExtra("rawMonths", intent.getIntExtra("rawMonths", 0))
                putExtra("type", mainLabel) // "EMI" or "Maturity Value"
                putExtra("isRecurring", intent.getBooleanExtra("isRecurring", false))
            }
            startActivity(statsIntent)
        }
    }

    private fun shareResults() {
        val rootView = findViewById<View>(android.R.id.content)
        rootView.post {
            try {
                val bitmap = Bitmap.createBitmap(rootView.width, rootView.height, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(bitmap)
                rootView.draw(canvas)

                val cachePath = File(cacheDir, "images")
                cachePath.mkdirs()
                val imageFile = File(cachePath, "result_details.png")
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
                        putExtra(Intent.EXTRA_TEXT, "Check out my calculation results from SCI-CAL app! \nDownload now: https://play.google.com/store/apps/details?id=com.tahir.scical")
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
