package com.example.data

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.drawable.toBitmap
import com.example.model.AppItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

class AppRepository(private val context: Context) {

    private val packageManager: PackageManager = context.packageManager
    private val prefs: SharedPreferences =
        context.getSharedPreferences("pad_start_prefs", Context.MODE_PRIVATE)

    private val pinnedKey = "pinned_packages"

    private val _pinnedPackages = MutableStateFlow<Set<String>>(loadPinnedPackages())
    val pinnedPackages: StateFlow<Set<String>> = _pinnedPackages.asStateFlow()

    private fun loadPinnedPackages(): Set<String> {
        val saved = prefs.getStringSet(pinnedKey, null)
        if (saved != null) {
            return saved.toSet()
        }
        // Sensible default suggestions for first launch
        return setOf(
            "com.android.chrome",
            "com.google.android.youtube",
            "com.google.android.apps.photos",
            "com.google.android.gm",
            "com.android.settings",
            "com.android.camera"
        )
    }

    suspend fun loadInstalledApps(): List<AppItem> = withContext(Dispatchers.IO) {
        val intent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        val resolveInfos = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.queryIntentActivities(
                intent,
                PackageManager.ResolveInfoFlags.of(PackageManager.MATCH_ALL.toLong())
            )
        } else {
            @Suppress("DEPRECATION")
            packageManager.queryIntentActivities(intent, PackageManager.MATCH_ALL)
        }

        val myPackageName = context.packageName
        val currentPinned = _pinnedPackages.value

        val items = mutableListOf<AppItem>()

        for (info in resolveInfos) {
            val pkg = info.activityInfo.packageName
            // Skip self to keep start menu clean
            if (pkg == myPackageName) continue

            val label = try {
                info.loadLabel(packageManager).toString()
            } catch (e: Exception) {
                info.activityInfo.name
            }

            val iconBitmap = try {
                val drawable = info.loadIcon(packageManager)
                drawableToBitmap(drawable)
            } catch (e: Exception) {
                null
            }

            val appInfo = try {
                packageManager.getApplicationInfo(pkg, 0)
            } catch (e: Exception) {
                null
            }

            val isSystem = appInfo?.let {
                (it.flags and ApplicationInfo.FLAG_SYSTEM) != 0
            } ?: false

            items.add(
                AppItem(
                    packageName = pkg,
                    activityName = info.activityInfo.name,
                    label = label.ifBlank { pkg },
                    iconBitmap = iconBitmap,
                    isPinned = currentPinned.contains(pkg),
                    isSystemApp = isSystem
                )
            )
        }

        items.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.label })
    }

    private fun drawableToBitmap(drawable: Drawable, sizePx: Int = 128): ImageBitmap {
        return try {
            val bitmap = drawable.toBitmap(
                width = sizePx,
                height = sizePx,
                config = Bitmap.Config.ARGB_8888
            )
            bitmap.asImageBitmap()
        } catch (e: Exception) {
            val bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap.asImageBitmap()
        }
    }

    fun togglePin(packageName: String) {
        val current = _pinnedPackages.value.toMutableSet()
        if (current.contains(packageName)) {
            current.remove(packageName)
        } else {
            current.add(packageName)
        }
        prefs.edit().putStringSet(pinnedKey, current).apply()
        _pinnedPackages.value = current
    }

    fun isPinned(packageName: String): Boolean {
        return _pinnedPackages.value.contains(packageName)
    }

    fun launchApp(packageName: String): Boolean {
        return try {
            val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
            if (launchIntent != null) {
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(launchIntent)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    fun openAppInfo(packageName: String) {
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", packageName, null)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            // Fallback to main settings
            openSettings()
        }
    }

    fun uninstallApp(packageName: String) {
        try {
            val intent = Intent(Intent.ACTION_DELETE).apply {
                data = Uri.fromParts("package", packageName, null)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            openAppInfo(packageName)
        }
    }

    fun openSettings(action: String = Settings.ACTION_SETTINGS) {
        try {
            val intent = Intent(action).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            // Ignore if setting not available
        }
    }

    fun openSoundSettings() {
        openSettings(Settings.ACTION_SOUND_SETTINGS)
    }

    fun openDisplaySettings() {
        openSettings(Settings.ACTION_DISPLAY_SETTINGS)
    }

    fun openWifiSettings() {
        openSettings(Settings.ACTION_WIFI_SETTINGS)
    }
}
