package com.example.services.billing

import android.content.Context

/**
 * Service package interface for Billing and In-App Purchases.
 */
class BillingService(private val context: Context) {
    fun isProSubscriber(): Boolean = true
}
