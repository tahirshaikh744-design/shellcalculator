package com.tahir.scical

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.switchmaterial.SwitchMaterial
import com.android.billingclient.api.*

class SettingsActivity : BaseCalculatorActivity() {

    private lateinit var sharedPref: SharedPreferences
    private lateinit var billingClient: BillingClient
    private var coffeeProductDetails: ProductDetails? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        // Force System Default
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        sharedPref = getSharedPreferences("settings", Context.MODE_PRIVATE)

        setupBilling()

        val toolbar = findViewById<MaterialToolbar>(R.id.settingsToolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        val switchVibration = findViewById<SwitchMaterial>(R.id.switchVibration)
        switchVibration.isChecked = sharedPref.getBoolean("vibration", true)
        switchVibration.setOnCheckedChangeListener { _, isChecked ->
            sharedPref.edit().putBoolean("vibration", isChecked).apply()
        }

        findViewById<TextView>(R.id.btnBuyCoffee).setOnClickListener {
            launchCoffeePurchase()
        }

        findViewById<TextView>(R.id.tvSendFeedback).setOnClickListener {
            sendFeedback()
        }

        val tvFeatures = findViewById<TextView>(R.id.tvFeatures)
        val features = StringBuilder()
            .append("<b>★ Recent Updates:</b>\n")
            .append("  • Added 1,000s separators (commas) for readability\n")
            .append("  • Integrated Country Flags in World Clock & Capitals\n")
            .append("  • Added 15+ new Scientific Unit Converters\n")
            .append("  • Optimized for AMOLED Black & Edge-to-Edge display\n\n")
            .append("<b>1. Core Calculator:</b>\n")
            .append("  • Basic & Scientific operations with History\n\n")
            .append("<b>2. Unit Converters:</b>\n")
            .append("  • 35+ Categories: Age, BMI, Area, Data, Torque, Frequency, Sound, Radiation, Inductance, and more.\n\n")
            .append("<b>3. Financial Tools:</b>\n")
            .append("  • Currency Converter (Live Rates)\n")
            .append("  • Loan, GST, Investment (FD/RD), and Tax Calculators\n\n")
            .append("<b>4. Smart Tools:</b>\n")
            .append("  • World Clock, Scientific Constants, and Capitals Lookup")
            .toString()
        
        tvFeatures.text = androidx.core.text.HtmlCompat.fromHtml(features, androidx.core.text.HtmlCompat.FROM_HTML_MODE_COMPACT)
    }

    private fun setupBilling() {
        val pendingPurchasesParams = PendingPurchasesParams.newBuilder()
            .enableOneTimeProducts()
            .build()

        billingClient = BillingClient.newBuilder(this)
            .setListener { billingResult, purchases ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                    for (purchase in purchases) {
                        handlePurchase(purchase)
                    }
                }
            }
            .enablePendingPurchases(pendingPurchasesParams)
            .build()

        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    queryCoffeeProduct()
                }
            }
            override fun onBillingServiceDisconnected() {}
        })
    }

    private fun queryCoffeeProduct() {
        val queryProductDetailsParams = QueryProductDetailsParams.newBuilder()
            .setProductList(listOf(
                QueryProductDetailsParams.Product.newBuilder()
                    .setProductId("buy_coffee")
                    .setProductType(BillingClient.ProductType.INAPP)
                    .build()
            ))
            .build()

        billingClient.queryProductDetailsAsync(queryProductDetailsParams) { billingResult, productDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                coffeeProductDetails = productDetailsList.firstOrNull()
            }
        }
    }

    private fun launchCoffeePurchase() {
        val details = coffeeProductDetails
        if (details != null) {
            val productDetailsParamsList = listOf(
                BillingFlowParams.ProductDetailsParams.newBuilder()
                    .setProductDetails(details)
                    .build()
            )
            val billingFlowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(productDetailsParamsList)
                .build()
            billingClient.launchBillingFlow(this, billingFlowParams)
        } else {
            Toast.makeText(this, "Item not found on Play Store", Toast.LENGTH_SHORT).show()
            queryCoffeeProduct()
        }
    }

    private fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged) {
                val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                    .build()
                billingClient.acknowledgePurchase(acknowledgePurchaseParams) { billingResult ->
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        Toast.makeText(this, "Thank you for your support! ☕", Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                Toast.makeText(this, "Thank you for your support! ☕", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun sendFeedback() {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf("shaikhtahir3162@gmail.com"))
            putExtra(Intent.EXTRA_SUBJECT, "Feedback for Calculator (SCI-CAL)")
        }
        try {
            startActivity(Intent.createChooser(intent, "Send feedback via..."))
        } catch (e: Exception) {
            Toast.makeText(this, "No email client found", Toast.LENGTH_SHORT).show()
        }
    }
}
