package com.tahir.scical

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Bundle
import android.provider.CalendarContract
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class AgeCalculatorActivity : BaseCalculatorActivity() {

    private lateinit var tvDob: TextView
    private lateinit var tvToday: TextView
    private lateinit var tvAgeYears: TextView
    private lateinit var tvAgeDetail: TextView
    private lateinit var tvNextBirthdayDay: TextView
    private lateinit var tvNextBirthdayCountdown: TextView
    
    private lateinit var tvSumYears: TextView
    private lateinit var tvSumMonths: TextView
    private lateinit var tvSumWeeks: TextView
    private lateinit var tvSumDays: TextView
    private lateinit var tvSumHours: TextView
    private lateinit var tvSumMinutes: TextView

    private var dobCalendar = Calendar.getInstance().apply { set(2010, Calendar.AUGUST, 16) }
    private var todayCalendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_age_calculator)

        tvDob = findViewById(R.id.tvDob)
        tvToday = findViewById(R.id.tvToday)
        tvAgeYears = findViewById(R.id.tvAgeYears)
        tvAgeDetail = findViewById(R.id.tvAgeDetail)
        tvNextBirthdayDay = findViewById(R.id.tvNextBirthdayDay)
        tvNextBirthdayCountdown = findViewById(R.id.tvNextBirthdayCountdown)
        
        tvSumYears = findViewById(R.id.tvSumYears)
        tvSumMonths = findViewById(R.id.tvSumMonths)
        tvSumWeeks = findViewById(R.id.tvSumWeeks)
        tvSumDays = findViewById(R.id.tvSumDays)
        tvSumHours = findViewById(R.id.tvSumHours)
        tvSumMinutes = findViewById(R.id.tvSumMinutes)

        findViewById<ImageView>(R.id.btnBack).setOnClickListener { finish() }

        tvDob.setOnClickListener { showDatePicker(true) }
        tvToday.setOnClickListener { showDatePicker(false) }

        findViewById<Button>(R.id.btnAddCalendar).setOnClickListener { addToCalendar() }
        findViewById<Button>(R.id.btnShare).setOnClickListener { shareResults() }

        updateUI()
    }

    private fun showDatePicker(isDob: Boolean) {
        val cal = if (isDob) dobCalendar else todayCalendar
        DatePickerDialog(this, { _, y, m, d ->
            cal.set(y, m, d)
            updateUI()
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun addToCalendar() {
        val nextBday = dobCalendar.clone() as Calendar
        nextBday.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR))
        if (nextBday.before(Calendar.getInstance())) {
            nextBday.add(Calendar.YEAR, 1)
        }

        val intent = Intent(Intent.ACTION_INSERT).apply {
            data = CalendarContract.Events.CONTENT_URI
            putExtra(CalendarContract.Events.TITLE, "Birthday Reminder")
            putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, true)
            putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, nextBday.timeInMillis)
            putExtra(CalendarContract.EXTRA_EVENT_END_TIME, nextBday.timeInMillis)
        }
        startActivity(intent)
    }

    private fun shareResults() {
        // Find the content view to screenshot
        val rootView = findViewById<View>(android.R.id.content)
        
        // Wait for layout if necessary
        rootView.post {
            try {
                val bitmap = Bitmap.createBitmap(rootView.width, rootView.height, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(bitmap)
                rootView.draw(canvas)

                val cachePath = File(cacheDir, "images")
                cachePath.mkdirs()
                val imageFile = File(cachePath, "age_result.png")
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
                        putExtra(Intent.EXTRA_TEXT, "Check out my age details calculated using SCI-CAL app. \nDownload now: https://play.google.com/store/apps/details?id=com.tahir.scical")
                        type = "image/png"
                    }
                    startActivity(Intent.createChooser(shareIntent, "Share via"))
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "Failed to share results: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateUI() {
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        tvDob.text = sdf.format(dobCalendar.time)
        tvToday.text = sdf.format(todayCalendar.time)

        val diffMillis = todayCalendar.timeInMillis - dobCalendar.timeInMillis
        if (diffMillis < 0) return

        // Basic Age Calculation
        var years = todayCalendar.get(Calendar.YEAR) - dobCalendar.get(Calendar.YEAR)
        var months = todayCalendar.get(Calendar.MONTH) - dobCalendar.get(Calendar.MONTH)
        var days = todayCalendar.get(Calendar.DAY_OF_MONTH) - dobCalendar.get(Calendar.DAY_OF_MONTH)

        if (days < 0) {
            months--
            val prevMonth = (todayCalendar.clone() as Calendar)
            prevMonth.add(Calendar.MONTH, -1)
            days += prevMonth.getActualMaximum(Calendar.DAY_OF_MONTH)
        }
        if (months < 0) {
            years--
            months += 12
        }

        tvAgeYears.text = years.toString()
        tvAgeDetail.text = "$months months | $days days"

        // Next Birthday
        val nextBday = dobCalendar.clone() as Calendar
        nextBday.set(Calendar.YEAR, todayCalendar.get(Calendar.YEAR))
        if (nextBday.before(todayCalendar) || nextBday.equals(todayCalendar)) {
            nextBday.add(Calendar.YEAR, 1)
        }

        val daySdf = SimpleDateFormat("EEEE", Locale.getDefault())
        tvNextBirthdayDay.text = daySdf.format(nextBday.time)

        var nbMonths = nextBday.get(Calendar.MONTH) - todayCalendar.get(Calendar.MONTH)
        var nbDays = nextBday.get(Calendar.DAY_OF_MONTH) - todayCalendar.get(Calendar.DAY_OF_MONTH)

        if (nbDays < 0) {
            nbMonths--
            nbDays += todayCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        }
        if (nbMonths < 0) nbMonths += 12

        tvNextBirthdayCountdown.text = "$nbMonths months | $nbDays days"

        // Summary
        val totalDays = diffMillis / (24 * 60 * 60 * 1000)
        tvSumYears.text = years.toString()
        tvSumMonths.text = (years * 12 + months).toString()
        tvSumWeeks.text = (totalDays / 7).toString()
        tvSumDays.text = totalDays.toString()
        tvSumHours.text = (totalDays * 24).toString()
        tvSumMinutes.text = (totalDays * 24 * 60).toString()
    }
}
