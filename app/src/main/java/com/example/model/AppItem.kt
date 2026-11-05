package com.example.model

import androidx.compose.ui.graphics.ImageBitmap

data class AppItem(
    val packageName: String,
    val activityName: String,
    val label: String,
    val iconBitmap: ImageBitmap? = null,
    val isPinned: Boolean = false,
    val firstInstallTime: Long = 0L,
    val isSystemApp: Boolean = false
)
