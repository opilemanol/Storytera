package com.example

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.ui.screens.MainAppShell
import com.example.ui.theme.MyApplicationTheme
import com.example.utils.AdHelper
import com.example.worker.StoryReminderWorker
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {

    private val TAG = "MainActivity"

    // Launcher for notification permission request on Android 13+
    private val requestNotificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Log.d(TAG, "Notification permission approved by user")
        } else {
            Log.d(TAG, "Notification permission denied by user")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 1. Initialize AdMob with test IDs and load Interstitial
        AdHelper.initialize(this)

        // 2. Schedule 12-hour story reminder notification worker
        scheduleStoryReminders()

        // 3. Request Notification permissions for Android 13+
        requestNotificationPermission()

        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    MainAppShell()
                }
            }
        }
    }

    private fun scheduleStoryReminders() {
        try {
            val workRequest = PeriodicWorkRequestBuilder<StoryReminderWorker>(
                12, TimeUnit.HOURS
            ).build()

            WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
                "story_reminder_work",
                ExistingPeriodicWorkPolicy.KEEP, // Keep existing to avoid timer resets
                workRequest
            )
            Log.d(TAG, "PeriodicStoryReminder Worker scheduled successfully for 12 hours.")
        } catch (e: Exception) {
            Log.e(TAG, "Error scheduling StoryReminder Worker", e)
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}
