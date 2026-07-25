package com.example.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.example.MainActivity

class JarvisOverlayService : Service() {

    private var isVoiceActive = true

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        startForegroundServiceNotification()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_TOGGLE_VOICE -> {
                isVoiceActive = !isVoiceActive
                val statusMsg = if (isVoiceActive) "Voice Commands Activated 🎙️" else "Voice Commands Paused 🔇"
                Toast.makeText(this, statusMsg, Toast.LENGTH_SHORT).show()
                startForegroundServiceNotification()
            }
            ACTION_READ_SCREEN -> {
                readAndCopyScreenClipboard()
            }
            ACTION_SAVE_DADA -> {
                saveClipboardToDadaFolder()
            }
        }
        return START_STICKY
    }

    private fun readAndCopyScreenClipboard() {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = clipboard.primaryClip
        if (clipData != null && clipData.itemCount > 0) {
            val text = clipData.getItemAt(0).text.toString()
            Toast.makeText(this, "👁️ Jarvis Inspector Read Screen Text: ${text.take(40)}...", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "👁️ Copy text from Chrome/App, then tap Inspect to let Jarvis read it!", Toast.LENGTH_LONG).show()
        }
    }

    private fun saveClipboardToDadaFolder() {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = clipboard.primaryClip
        if (clipData != null && clipData.itemCount > 0) {
            val text = clipData.getItemAt(0).text.toString()
            Toast.makeText(this, "💾 Saved to Jarvis Dada Folder: '${text.take(30)}...'", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "💾 Copy any text on Chrome/Screen first to save into Dada Folder!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startForegroundServiceNotification() {
        val channelId = "JARVIS_ACTIVE_CHANNEL"
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Jarvis Active Foreground Service",
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(channel)
        }

        val openAppIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            openAppIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        // PendingIntents for Notification Buttons
        val toggleVoiceIntent = PendingIntent.getService(
            this,
            1,
            Intent(this, JarvisOverlayService::class.java).apply { action = ACTION_TOGGLE_VOICE },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val readScreenIntent = PendingIntent.getService(
            this,
            2,
            Intent(this, JarvisOverlayService::class.java).apply { action = ACTION_READ_SCREEN },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val saveDadaIntent = PendingIntent.getService(
            this,
            3,
            Intent(this, JarvisOverlayService::class.java).apply { action = ACTION_SAVE_DADA },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification: Notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("⚡ AI Jarvis Full Phone Control HUD")
            .setContentText(if (isVoiceActive) "🎙️ Voice Active | Tap 'Inspect Screen' in Chrome or any app" else "🔇 Voice Paused | Tap to resume voice control")
            .setSmallIcon(android.R.drawable.ic_menu_call)
            .setContentIntent(pendingIntent)
            .addAction(
                android.R.drawable.ic_btn_speak_now,
                if (isVoiceActive) "VOICE: ON 🎙️" else "VOICE: OFF 🔇",
                toggleVoiceIntent
            )
            .addAction(
                android.R.drawable.ic_menu_view,
                "INSPECT SCREEN 👁️",
                readScreenIntent
            )
            .addAction(
                android.R.drawable.ic_menu_save,
                "SAVE DADA FOLDER 💾",
                saveDadaIntent
            )
            .setOngoing(true)
            .build()

        startForeground(1001, notification)
    }

    companion object {
        const val ACTION_TOGGLE_VOICE = "com.example.action.TOGGLE_VOICE"
        const val ACTION_READ_SCREEN = "com.example.action.READ_SCREEN"
        const val ACTION_SAVE_DADA = "com.example.action.SAVE_DADA"
    }
}
