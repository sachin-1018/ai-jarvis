package com.example.data.device

import android.content.Context
import android.content.Intent
import android.hardware.camera2.CameraManager
import android.media.AudioManager
import android.net.Uri
import android.os.BatteryManager
import android.provider.Settings
import android.widget.Toast

data class PhoneControlResult(
    val isSuccess: Boolean,
    val feedbackMessage: String,
    val actionTaken: String
)

class PhoneControlManager(private val context: Context) {

    fun executeCommand(rawCommand: String): PhoneControlResult {
        val cmd = rawCommand.lowercase().trim()

        return when {
            cmd.contains("call") || cmd.contains("phone karo") || cmd.contains("dial") -> {
                val number = extractPhoneNumber(cmd)
                if (number.isNotEmpty()) {
                    dialNumber(number)
                    PhoneControlResult(true, "Dialing $number...", "CALL")
                } else {
                    val name = cmd.replace("call", "").replace("ko", "").replace("phone", "").replace("karo", "").trim()
                    dialName(name)
                    PhoneControlResult(true, "Opening Phone Dialer for $name", "CALL")
                }
            }

            cmd.contains("message") || cmd.contains("sms") || cmd.contains("msg") -> {
                val number = extractPhoneNumber(cmd)
                val msg = "Hello from AI Jarvis!"
                sendSmsIntent(if (number.isNotEmpty()) number else "9876543210", msg)
                PhoneControlResult(true, "Opening SMS Composer...", "SMS")
            }

            cmd.contains("flashlight") || cmd.contains("torch") -> {
                if (cmd.contains("off") || cmd.contains("band")) {
                    setTorch(false)
                    PhoneControlResult(true, "Flashlight turned OFF.", "TORCH_OFF")
                } else {
                    setTorch(true)
                    PhoneControlResult(true, "Flashlight turned ON.", "TORCH_ON")
                }
            }

            cmd.contains("volume") || cmd.contains("aawaz") || cmd.contains("sound") -> {
                val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
                val maxVol = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
                when {
                    cmd.contains("max") || cmd.contains("full") -> {
                        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVol, AudioManager.FLAG_SHOW_UI)
                        PhoneControlResult(true, "Volume set to MAXIMUM", "VOLUME")
                    }
                    cmd.contains("mute") || cmd.contains("zero") || cmd.contains("band") -> {
                        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, AudioManager.FLAG_SHOW_UI)
                        PhoneControlResult(true, "Volume MUTED", "VOLUME")
                    }
                    else -> {
                        val target = (maxVol * 0.7).toInt()
                        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, target, AudioManager.FLAG_SHOW_UI)
                        PhoneControlResult(true, "Volume adjusted to 70%", "VOLUME")
                    }
                }
            }

            cmd.contains("battery") || cmd.contains("charging") -> {
                val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
                val level = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
                PhoneControlResult(true, "Current battery level is $level%", "BATTERY")
            }

            cmd.contains("setting") || cmd.contains("settings") -> {
                if (cmd.contains("accessibility") || cmd.contains("access")) {
                    openAccessibilitySettings()
                    PhoneControlResult(true, "Opening Accessibility Settings...", "ACCESSIBILITY")
                } else {
                    openSettings()
                    PhoneControlResult(true, "Opening System Settings...", "SETTINGS")
                }
            }

            cmd.contains("accessibility") || cmd.contains("accessability") -> {
                openAccessibilitySettings()
                PhoneControlResult(true, "Opening Device Accessibility Settings...", "ACCESSIBILITY")
            }

            cmd.contains("camera") -> {
                openApp("com.android.camera") || launchCameraIntent()
                PhoneControlResult(true, "Opening Camera...", "OPEN_APP")
            }

            cmd.contains("youtube") -> {
                openApp("com.google.android.youtube")
                PhoneControlResult(true, "Opening YouTube...", "OPEN_APP")
            }

            cmd.contains("whatsapp") -> {
                openApp("com.whatsapp")
                PhoneControlResult(true, "Opening WhatsApp...", "OPEN_APP")
            }

            cmd.contains("chrome") || cmd.contains("browser") -> {
                openApp("com.android.chrome")
                PhoneControlResult(true, "Opening Chrome...", "OPEN_APP")
            }

            cmd.contains("calculator") || cmd.contains("calc") -> {
                openApp("com.google.android.calculator") || openApp("com.android.calculator2")
                PhoneControlResult(true, "Opening Calculator...", "OPEN_APP")
            }

            cmd.contains("open") || cmd.contains("chalu karo") || cmd.contains("kholo") -> {
                val appName = cmd.replace("open", "").replace("chalu karo", "").replace("kholo", "").trim()
                val opened = openAppByName(appName)
                if (opened) {
                    PhoneControlResult(true, "Opened $appName successfully", "OPEN_APP")
                } else {
                    PhoneControlResult(false, "Could not find app matching '$appName' on device.", "OPEN_APP")
                }
            }

            else -> {
                PhoneControlResult(false, "Unknown device control command.", "UNKNOWN")
            }
        }
    }

    private fun extractPhoneNumber(cmd: String): String {
        val regex = Regex("\\d{10,12}")
        return regex.find(cmd)?.value ?: ""
    }

    private fun dialNumber(number: String) {
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$number")).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }

    private fun dialName(name: String) {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }

    private fun sendSmsIntent(number: String, message: String) {
        val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:$number")).apply {
            putExtra("sms_body", message)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }

    private fun setTorch(enabled: Boolean) {
        try {
            val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
            val cameraId = cameraManager.cameraIdList.firstOrNull() ?: return
            cameraManager.setTorchMode(cameraId, enabled)
        } catch (e: Exception) {
            Toast.makeText(context, "Flashlight error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openSettings() {
        val intent = Intent(Settings.ACTION_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }

    fun openAccessibilitySettings() {
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }

    private fun launchCameraIntent(): Boolean {
        return try {
            val intent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun openApp(packageName: String): Boolean {
        return try {
            val launchIntent = context.packageManager.getLaunchIntentForPackage(packageName)
            if (launchIntent != null) {
                launchIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(launchIntent)
                true
            } else false
        } catch (e: Exception) {
            false
        }
    }

    private fun openAppByName(appName: String): Boolean {
        val pm = context.packageManager
        val mainIntent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        val apps = pm.queryIntentActivities(mainIntent, 0)
        for (app in apps) {
            val label = app.loadLabel(pm).toString().lowercase()
            if (label.contains(appName.lowercase())) {
                val launchIntent = pm.getLaunchIntentForPackage(app.activityInfo.packageName)
                if (launchIntent != null) {
                    launchIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    context.startActivity(launchIntent)
                    return true
                }
            }
        }
        return false
    }
}
