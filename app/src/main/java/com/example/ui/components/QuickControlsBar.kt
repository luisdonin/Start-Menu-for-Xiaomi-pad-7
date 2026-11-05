package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrightnessMedium
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.MenuBorderSubtle
import com.example.ui.theme.MenuSurfaceVariant
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun QuickControlsBar(
    totalApps: Int,
    onOpenSettings: () -> Unit,
    onOpenSound: () -> Unit,
    onOpenDisplay: () -> Unit,
    onOpenWifi: () -> Unit,
    onCloseMenu: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MenuSurfaceVariant.copy(alpha = 0.7f))
            .border(1.dp, MenuBorderSubtle, RoundedCornerShape(16.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Tablet status indicator
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(CyanAccent)
            )
            Text(
                text = " Xiaomi Pad 7",
                color = TextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = " • $totalApps apps",
                color = TextSecondary,
                fontSize = 11.sp
            )
        }

        // Quick action icons
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            QuickControlButton(
                icon = Icons.Default.VolumeUp,
                contentDescription = "Sound Settings",
                onClick = onOpenSound,
                testTag = "quick_sound_btn"
            )
            QuickControlButton(
                icon = Icons.Default.BrightnessMedium,
                contentDescription = "Display Settings",
                onClick = onOpenDisplay,
                testTag = "quick_display_btn"
            )
            QuickControlButton(
                icon = Icons.Default.Wifi,
                contentDescription = "Wi-Fi Settings",
                onClick = onOpenWifi,
                testTag = "quick_wifi_btn"
            )
            QuickControlButton(
                icon = Icons.Default.Settings,
                contentDescription = "Tablet Settings",
                onClick = onOpenSettings,
                testTag = "quick_settings_btn"
            )
            QuickControlButton(
                icon = Icons.Default.Close,
                contentDescription = "Dismiss Start Menu",
                onClick = onCloseMenu,
                tint = Color(0xFFEF4444),
                testTag = "quick_close_btn"
            )
        }
    }
}

@Composable
private fun QuickControlButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = TextSecondary,
    testTag: String = ""
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .size(36.dp)
            .testTag(testTag)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(18.dp)
        )
    }
}
