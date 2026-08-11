package com.tahir.scical

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.*
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import androidx.core.net.toUri
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.ads.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.android.billingclient.api.*

class MainActivity : BaseCalculatorActivity() {
    private lateinit var viewPager: ViewPager2
    
    private lateinit var navCalculator: LinearLayout
    private lateinit var navConverter: LinearLayout
    private lateinit var navCurrency: LinearLayout
    private lateinit var navSmart: LinearLayout

    private lateinit var iconCalculator: View
    private lateinit var iconConverter: View
    private lateinit var iconCurrency: View
    private lateinit var iconSmart: View

    private lateinit var textCalculator: View
    private lateinit var textConverter: View
    private lateinit var textCurrency: View
    private lateinit var textSmart: View

    private lateinit var layoutHistory: View
    private lateinit var rvHistory: RecyclerView
    private lateinit var billingClient: BillingClient
    private var productDetails: ProductDetails? = null

    private lateinit var adContainer: FrameLayout
    private var adView: AdView? = null

    private val historyList = mutableListOf<HistoryItem>()
    private lateinit var historyAdapter: HistoryAdapter
    private var currentTheme = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        val prefs = getSharedPreferences("settings", Context.MODE_PRIVATE)
        currentTheme = prefs.getInt("theme", 0)
        applyAppTheme(currentTheme)
        
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Handle System Insets
        val mainRoot = findViewById<View>(R.id.mainRoot)
        ViewCompat.setOnApplyWindowInsetsListener(mainRoot) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(
                left = systemBars.left,
                top = systemBars.top,
                right = systemBars.right,
                bottom = systemBars.bottom
            )
            insets
        }

        setupBilling()

        adContainer = findViewById(R.id.adContainer)
        val btnRemoveAds = findViewById<View>(R.id.btnRemoveAds)
        
        val isAdsRemoved = prefs.getBoolean("ads_removed", false)
        if (isAdsRemoved) {
            adContainer.visibility = View.GONE
            btnRemoveAds.visibility = View.GONE
        } else {
            // Force dynamic test device registration for YOUR phone
            val configuration = RequestConfiguration.Builder()
                .setTestDeviceIds(listOf("ba75f5ef-21ec-4f3d-91e3-4c37114e1aae"))
                .build()
            MobileAds.setRequestConfiguration(configuration)

            MobileAds.initialize(this) { status ->
                Log.d("AdMob", "SDK Initialized")
                runOnUiThread {
                    loadAdaptiveBanner()
                }
            }
        }

        handleRatingPopup(prefs)

        viewPager = findViewById(R.id.viewPager)
        navCalculator = findViewById(R.id.navCalculator)
        navConverter = findViewById(R.id.navConverter)
        navCurrency = findViewById(R.id.navCurrency)
        navSmart = findViewById(R.id.navSmart)

        iconCalculator = findViewById(R.id.iconCalculator)
        iconConverter = findViewById(R.id.iconConverter)
        iconCurrency = findViewById(R.id.iconCurrency)
        iconSmart = findViewById(R.id.iconSmart)

        textCalculator = findViewById(R.id.textCalculator)
        textConverter = findViewById(R.id.textConverter)
        textCurrency = findViewById(R.id.textCurrency)
        textSmart = findViewById(R.id.textSmart)

        layoutHistory = findViewById(R.id.layoutHistory)
        rvHistory = findViewById(R.id.rvHistory)

        setupHistory()
        setupViewPager()
        setupNavigation()
        updateNavUI(0)

        findViewById<ImageView>(R.id.btnHistory).setOnClickListener {
            layoutHistory.visibility = View.VISIBLE
            vibrate()
        }
        findViewById<ImageView>(R.id.btnCloseHistory).setOnClickListener {
            layoutHistory.visibility = View.GONE
            vibrate()
        }
        findViewById<Button>(R.id.btnClearHistory).setOnClickListener {
            val size = historyList.size
            historyList.clear()
            historyAdapter.notifyItemRangeRemoved(0, size)
            vibrate()
        }
        
        findViewById<ImageView>(R.id.btnSettings).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
            vibrate()
        }

        btnRemoveAds.setOnClickListener {
            launchPurchaseFlow()
            vibrate()
        }
    }

    private fun loadAdaptiveBanner() {
        adContainer.removeAllViews()
        val adView = AdView(this)
        this.adView = adView
        
        // Swapping back to your REAL Ad Unit ID
        adView.adUnitId = "ca-app-pub-3051165035564130/3423605498" 
        
        val adSize = getAdSize()
        adView.setAdSize(adSize)
        adContainer.addView(adView)

        adView.adListener = object : AdListener() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.e("AdMob", "Ad failed to load: ${adError.message} (Code: ${adError.code})")
            }
            override fun onAdLoaded() {
                Log.d("AdMob", "Live Ad Loaded Successfully!")
            }
        }

        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
    }

    private fun getAdSize(): AdSize {
        val displayMetrics = resources.displayMetrics
        val widthPixels = displayMetrics.widthPixels.toFloat()
        val density = displayMetrics.density
        val adWidth = (widthPixels / density).toInt()
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth)
    }

    override fun onRestart() {
        super.onRestart()
        val prefs = getSharedPreferences("settings", Context.MODE_PRIVATE)
        val newTheme = prefs.getInt("theme", 0)
        if (currentTheme != newTheme) {
            recreate()
        }
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
                    queryProduct()
                    restorePurchases()
                }
            }
            override fun onBillingServiceDisconnected() {}
        })
    }

    private fun queryProduct() {
        val queryProductDetailsParams = QueryProductDetailsParams.newBuilder()
            .setProductList(listOf(
                QueryProductDetailsParams.Product.newBuilder()
                    .setProductId("remove_ads")
                    .setProductType(BillingClient.ProductType.INAPP)
                    .build()
            ))
            .build()

        billingClient.queryProductDetailsAsync(queryProductDetailsParams) { billingResult, productDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                productDetails = productDetailsList.firstOrNull()
            }
        }
    }

    private fun restorePurchases() {
        val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.INAPP)
            .build()

        billingClient.queryPurchasesAsync(params) { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                for (purchase in purchases) {
                    if (purchase.products.contains("remove_ads") && purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                        applyAdRemoval()
                    }
                }
            }
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
                        applyAdRemoval()
                    }
                }
            } else {
                applyAdRemoval()
            }
        }
    }

    private fun applyAdRemoval() {
        val prefs = getSharedPreferences("settings", Context.MODE_PRIVATE)
        prefs.edit { putBoolean("ads_removed", true) }
        runOnUiThread {
            adContainer.visibility = View.GONE
            findViewById<View>(R.id.btnRemoveAds).visibility = View.GONE
        }
    }

    private fun launchPurchaseFlow() {
        val details = productDetails
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
            Toast.makeText(this, "Item not found on Play Store", Toast.LENGTH_LONG).show()
            queryProduct()
        }
    }

    private fun handleRatingPopup(prefs: android.content.SharedPreferences) {
        val hasRated = prefs.getBoolean("has_rated", false)
        if (hasRated) return

        val openCount = prefs.getInt("open_count", 0) + 1
        prefs.edit { putInt("open_count", openCount) }

        if (openCount % 3 == 0) {
            Handler(Looper.getMainLooper()).postDelayed({
                if (!isFinishing && !isDestroyed) {
                    showRatingDialog(prefs)
                }
            }, 1000)
        }
    }

    private fun showRatingDialog(prefs: android.content.SharedPreferences) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_rating, null)
        val alertDialog = MaterialAlertDialogBuilder(this)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        val btnClose = dialogView.findViewById<ImageView>(R.id.btnCloseRating)
        val btnRate = dialogView.findViewById<Button>(R.id.btnRateNow)
        val ratingBar = dialogView.findViewById<RatingBar>(R.id.ratingBar)

        btnClose.setOnClickListener { alertDialog.dismiss() }

        btnRate.setOnClickListener {
            if (ratingBar.rating > 0) {
                prefs.edit { putBoolean("has_rated", true) }
                try {
                    val uri = "market://details?id=$packageName".toUri()
                    val goToMarket = Intent(Intent.ACTION_VIEW, uri)
                    goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_NEW_DOCUMENT or Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
                    startActivity(goToMarket)
                } catch (e: Exception) {
                    startActivity(Intent(Intent.ACTION_VIEW, "https://play.google.com/store/apps/details?id=$packageName".toUri()))
                }
                alertDialog.dismiss()
            } else {
                Toast.makeText(this, "Please select stars to rate", Toast.LENGTH_SHORT).show()
            }
        }

        alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        alertDialog.show()
    }

    private fun setupViewPager() {
        val adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int = 4
            override fun createFragment(position: Int): Fragment {
                return when (position) {
                    0 -> CalculatorFragment()
                    1 -> ConverterFragment()
                    2 -> FinanceFragment()
                    3 -> SmartFragment()
                    else -> CalculatorFragment()
                }
            }
        }
        viewPager.adapter = adapter
        
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updateNavUI(position)
            }
        })
    }

    private fun setupNavigation() {
        navCalculator.setOnClickListener { viewPager.currentItem = 0; vibrate() }
        navConverter.setOnClickListener { viewPager.currentItem = 1; vibrate() }
        navCurrency.setOnClickListener { viewPager.currentItem = 2; vibrate() }
        navSmart.setOnClickListener { viewPager.currentItem = 3; vibrate() }
    }

    private fun updateNavUI(position: Int) {
        textCalculator.visibility = if (position == 0) View.VISIBLE else View.GONE
        iconCalculator.visibility = if (position == 0) View.GONE else View.VISIBLE
        navCalculator.setBackgroundResource(if (position == 0) R.drawable.bg_nav_item_selected else android.R.color.transparent)

        textConverter.visibility = if (position == 1) View.VISIBLE else View.GONE
        iconConverter.visibility = if (position == 1) View.GONE else View.VISIBLE
        navConverter.setBackgroundResource(if (position == 1) R.drawable.bg_nav_item_selected else android.R.color.transparent)

        textCurrency.visibility = if (position == 2) View.VISIBLE else View.GONE
        iconCurrency.visibility = if (position == 2) View.GONE else View.VISIBLE
        navCurrency.setBackgroundResource(if (position == 2) R.drawable.bg_nav_item_selected else android.R.color.transparent)

        textSmart.visibility = if (position == 3) View.VISIBLE else View.GONE
        iconSmart.visibility = if (position == 3) View.GONE else View.VISIBLE
        navSmart.setBackgroundResource(if (position == 3) R.drawable.bg_nav_item_selected else android.R.color.transparent)
    }

    fun addToHistory(formula: String, result: String) {
        historyList.add(0, HistoryItem(formula, result))
        historyAdapter.notifyItemInserted(0)
    }

    fun vibrate() {
        VibrationHelper.vibrate(this)
    }

    override fun applyAppTheme(themeIndex: Int) {
        when (themeIndex) {
            0 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            1 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            2 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }

    private fun setupHistory() {
        historyAdapter = HistoryAdapter(historyList)
        rvHistory.layoutManager = LinearLayoutManager(this)
        rvHistory.adapter = historyAdapter
        val swipeHandler = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(r: RecyclerView, v: RecyclerView.ViewHolder, t: RecyclerView.ViewHolder) = false
            override fun onSwiped(vh: RecyclerView.ViewHolder, d: Int) = historyAdapter.removeItem(vh.adapterPosition)
        }
        ItemTouchHelper(swipeHandler).attachToRecyclerView(rvHistory)
    }
}
