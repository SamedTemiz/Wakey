package com.wakey.app.data.billing

import android.app.Activity
import android.content.Context
import android.util.Log
import com.android.billingclient.api.*
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BillingManager @Inject constructor(
    @ApplicationContext private val context: Context
) : PurchasesUpdatedListener {

    private val _isPremium = MutableStateFlow(false)
    val isPremium = _isPremium.asStateFlow()

    private val billingClient = BillingClient.newBuilder(context)
        .setListener(this)
        .enablePendingPurchases()
        .build()
        
    init {
        try {
            startConnection()
        } catch (e: Exception) {
            Log.e("BillingManager", "Error during init", e)
        }
    }
    
    private fun startConnection() {
        try {
            billingClient.startConnection(object : BillingClientStateListener {
                override fun onBillingSetupFinished(billingResult: BillingResult) {
                    try {
                        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                            checkSubscriptionStatus()
                        } else {
                            Log.w("BillingManager", "Billing setup failed: ${billingResult.debugMessage}")
                        }
                    } catch (e: Exception) {
                        Log.e("BillingManager", "Error in onBillingSetupFinished", e)
                    }
                }
                override fun onBillingServiceDisconnected() {
                    Log.d("BillingManager", "Billing service disconnected")
                }
            })
        } catch (e: Exception) {
            Log.e("BillingManager", "Error starting connection", e)
        }
    }
    
    // Purchase Launch Flow
    fun launchPurchaseFlow(activity: Activity) {
        val productList = listOf(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId("premium_upgrade")
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
        )
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(productList)
            .build()

        billingClient.queryProductDetailsAsync(params) { billingResult, productDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && productDetailsList.isNotEmpty()) {
                val productDetails = productDetailsList[0]
                val productDetailsParamsList = listOf(
                    BillingFlowParams.ProductDetailsParams.newBuilder()
                        .setProductDetails(productDetails)
                        .build()
                )
                val flowParams = BillingFlowParams.newBuilder()
                    .setProductDetailsParamsList(productDetailsParamsList)
                    .build()
                billingClient.launchBillingFlow(activity, flowParams)
            }
        }
    }

    // Handle Purchase Updates
    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: List<Purchase>?) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                handlePurchase(purchase)
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
                         _isPremium.value = true
                    }
                }
            } else {
                _isPremium.value = true
            }
        }
    }
    
    // Restore Purchase Logic
    private fun checkSubscriptionStatus() {
        val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.INAPP)
            .build()
            
        billingClient.queryPurchasesAsync(params) { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                // If purchases list is empty, it means no active purchases found
                for (purchase in purchases) {
                    if (purchase.products.contains("premium_upgrade") && purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                        _isPremium.value = true
                        // If one valid purchase is found, we don't need to check others
                        return@queryPurchasesAsync
                    }
                }
            }
        }
    }
}
