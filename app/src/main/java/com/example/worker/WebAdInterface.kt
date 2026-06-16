package com.example.webkit

import android.app.Activity
import android.util.Log
import android.webkit.JavascriptInterface
import com.example.utils.AdHelper

class WebAdInterface(private val activity: Activity) {

    private val TAG = "WebAdInterface"

    /**
     * Call this from JavaScript whenever a page transition occurs.
     * Web code example:
     *      if (window.AndroidAdInterface) {
     *          window.AndroidAdInterface.onPageTransition();
     *      }
     */
    @JavascriptInterface
    fun onPageTransition() {
        Log.d(TAG, "onPageTransition triggered from JS!")
        // Run on UI Thread to display the Interstitial Ad properly
        activity.runOnUiThread {
            AdHelper.showInterstitial(activity)
        }
    }

    /**
     * Alias for showInterstitial to make it easy for the developer.
     * Web code example:
     *      if (window.AndroidAdInterface) {
     *          window.AndroidAdInterface.showInterstitial();
     *      }
     */
    @JavascriptInterface
    fun showInterstitial() {
        Log.d(TAG, "showInterstitial triggered from JS!")
        activity.runOnUiThread {
            AdHelper.showInterstitial(activity)
        }
    }
}
