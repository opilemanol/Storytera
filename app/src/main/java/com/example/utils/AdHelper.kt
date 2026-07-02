package com.example.utils

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

object AdHelper {
    private const val TAG = "AdHelper"
    
    // AdMob standard test ad unit IDs
    const val BANNER_TEST_ID = "ca-app-pub-5927630860510493/6623653632"
    const val INTERSTITIAL_TEST_ID = "ca-app-pub-5927630860510493/4881985103"

    private var mInterstitialAd: InterstitialAd? = null
    var isInterstitialLoading = false
        private set

    fun initialize(context: Context) {
        try {
            MobileAds.initialize(context) { status ->
                Log.d(TAG, "AdMob Initialized: $status")
                loadInterstitial(context)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing AdMob", e)
        }
    }

    fun loadInterstitial(context: Context) {
        if (mInterstitialAd != null || isInterstitialLoading) return
        
        isInterstitialLoading = true
        Log.d(TAG, "Loading Interstitial Ad")
        val adRequest = AdRequest.Builder().build()
        
        InterstitialAd.load(
            context,
            INTERSTITIAL_TEST_ID,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d(TAG, "Interstitial fail to load: ${adError.message}")
                    mInterstitialAd = null
                    isInterstitialLoading = false
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    Log.d(TAG, "Interstitial loaded successfully")
                    mInterstitialAd = interstitialAd
                    isInterstitialLoading = false
                }
            }
        )
    }

    fun showInterstitial(activity: Activity, onAdClosed: () -> Unit = {}) {
        activity.runOnUiThread {
            val interstitial = mInterstitialAd
            if (interstitial != null) {
                interstitial.fullScreenContentCallback = object : com.google.android.gms.ads.FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        Log.d(TAG, "Interstitial dismissed content")
                        mInterstitialAd = null
                        onAdClosed()
                        // Reload immediately for the next transition
                        loadInterstitial(activity)
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: com.google.android.gms.ads.AdError) {
                        Log.d(TAG, "Interstitial failed to show content: ${adError.message}")
                        mInterstitialAd = null
                        onAdClosed()
                    }
                }
                interstitial.show(activity)
            } else {
                Log.d(TAG, "Interstitial was NOT loaded yet")
                onAdClosed()
                loadInterstitial(activity)
            }
        }
    }
}
