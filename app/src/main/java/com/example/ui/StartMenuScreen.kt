package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.model.AppItem
import com.example.ui.components.AppContextDialog
import com.example.ui.components.AppGridItem
import com.example.ui.components.QuickControlsBar
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.MenuBorderSubtle
import com.example.ui.theme.MenuSurface
import com.example.ui.theme.MenuSurfaceVariant
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VioletAccent

@Composable
fun StartMenuScreen(
    viewModel: StartMenuViewModel,
    onDismiss: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current

    // Listen for navigation events (e.g. app launched or settings opened -> dismiss)
    LaunchedEffect(Unit) {
        viewModel.navigationEvents.collect { event ->
            when (event) {
                StartMenuNavigationEvent.Dismiss -> onDismiss()
            }
        }
    }

    // Handle physical back button or gesture to dismiss start menu
    BackHandler {
        onDismiss()
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .testTag("start_menu_root")
    ) {
        val screenHeight = maxHeight
        val screenWidth = maxWidth

        // 1. Full-screen Translucent Scrim - Dismiss on tap outside
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.40f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {
                        focusManager.clearFocus()
                        onDismiss()
                    }
                )
                .testTag("dismiss_scrim")
        )

        // 2. The Floating Start Menu Window Card
        // Positioned at the bottom center, ergonomically resting above the tablet dock
        Box(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .padding(bottom = 16.dp, start = 20.dp, end = 20.dp, top = 28.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Surface(
                shape = RoundedCornerShape(28.dp),
                color = MenuSurface,
                tonalElevation = 12.dp,
                shadowElevation = 20.dp,
                border = BorderStroke(
                    1.dp,
                    Brush.verticalGradient(
                        listOf(
                            CyanAccent.copy(alpha = 0.45f),
                            MenuBorderSubtle,
                            Color(0x10FFFFFF)
                        )
                    )
                ),
                modifier = Modifier
                    .widthIn(max = 640.dp)
                    .fillMaxWidth()
                    .fillMaxHeight(0.88f)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        // Absorb clicks on the window so it doesn't dismiss when tapping internal elements
                        focusManager.clearFocus()
                    }
                    .testTag("start_menu_floating_window")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp, vertical = 14.dp)
                ) {
                    // Top drag pill
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(bottom = 12.dp)
                            .size(width = 36.dp, height = 4.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.25f))
                    )

                    // Quick Controls & Status Bar
                    QuickControlsBar(
                        totalApps = uiState.allApps.size,
                        onOpenSettings = { viewModel.openSettings() },
                        onOpenSound = { viewModel.openSoundSettings() },
                        onOpenDisplay = { viewModel.openDisplaySettings() },
                        onOpenWifi = { viewModel.openWifiSettings() },
                        onCloseMenu = onDismiss,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // Search Bar
                    SearchBar(
                        query = uiState.searchQuery,
                        onQueryChanged = viewModel::onSearchQueryChanged,
                        onClearQuery = viewModel::clearSearch,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    )

                    // Main Content Area
                    if (uiState.isLoading) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                        ) {
                            CircularProgressIndicator(
                                color = CyanAccent,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    } else if (uiState.searchQuery.isNotBlank()) {
                        // Searching mode: Show search results
                        SearchResultsSection(
                            results = uiState.filteredApps,
                            query = uiState.searchQuery,
                            onAppClick = viewModel::onAppClicked,
                            onAppLongClick = viewModel::onAppLongClicked,
                            onClearSearch = viewModel::clearSearch,
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        // Desktop Start Menu Standard Mode
                        DefaultStartMenuContent(
                            pinnedApps = uiState.pinnedApps,
                            allApps = uiState.allApps,
                            onAppClick = viewModel::onAppClicked,
                            onAppLongClick = viewModel::onAppLongClicked,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // App Context Dialog (Long-press actions: Pin, App Info, Uninstall)
        uiState.selectedAppContext?.let { app ->
            AppContextDialog(
                app = app,
                onDismiss = viewModel::dismissAppContext,
                onLaunch = { viewModel.onAppClicked(app) },
                onTogglePin = { viewModel.togglePin(app) },
                onOpenInfo = { viewModel.openAppInfo(app) },
                onUninstall = { viewModel.uninstallApp(app) }
            )
        }
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChanged: (String) -> Unit,
    onClearQuery: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        value = query,
        onValueChange = onQueryChanged,
        placeholder = {
            Text(
                text = stringResource(R.string.search_placeholder),
                color = TextSecondary,
                fontSize = 14.sp
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = CyanAccent,
                modifier = Modifier.size(20.dp)
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(
                    onClick = onClearQuery,
                    modifier = Modifier.testTag("search_clear_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear search",
                        tint = TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MenuSurfaceVariant,
            unfocusedContainerColor = MenuSurfaceVariant.copy(alpha = 0.6f),
            focusedBorderColor = CyanAccent,
            unfocusedBorderColor = MenuBorderSubtle,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary
        ),
        modifier = modifier.testTag("start_menu_search_input")
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DefaultStartMenuContent(
    pinnedApps: List<AppItem>,
    allApps: List<AppItem>,
    onAppClick: (AppItem) -> Unit,
    onAppLongClick: (AppItem) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(bottom = 12.dp)
    ) {
        // 1. PINNED APPS SECTION
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.PushPin,
                        contentDescription = null,
                        tint = CyanAccent,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = stringResource(R.string.pinned_section),
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "(${pinnedApps.size})",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }
        }

        if (pinnedApps.isEmpty()) {
            item {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(MenuSurfaceVariant.copy(alpha = 0.4f))
                        .padding(16.dp)
                ) {
                    Text(
                        text = "No pinned apps yet. Long-press any app below to pin it here for quick access.",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            item {
                // Responsive grid for pinned apps
                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    maxItemsInEachRow = 5
                ) {
                    pinnedApps.forEach { app ->
                        AppGridItem(
                            app = app,
                            onClick = { onAppClick(app) },
                            onLongClick = { onAppLongClick(app) }
                        )
                    }
                }
            }
        }

        // 2. ALL APPS SECTION
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.GridView,
                    contentDescription = null,
                    tint = VioletAccent,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = stringResource(R.string.all_apps_section),
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "(${allApps.size})",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
        }

        // Group all apps alphabetically
        val grouped = allApps.groupBy {
            val first = it.label.firstOrNull()?.uppercaseChar() ?: '#'
            if (first in 'A'..'Z') first else '#'
        }

        grouped.forEach { (letter, appsInLetter) ->
            item(key = "letter_$letter") {
                Text(
                    text = letter.toString(),
                    color = CyanAccent,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 10.dp, bottom = 4.dp, start = 4.dp)
                )
            }

            item(key = "apps_$letter") {
                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp),
                    horizontalArrangement = Arrangement.Start,
                    maxItemsInEachRow = 5
                ) {
                    appsInLetter.forEach { app ->
                        AppGridItem(
                            app = app,
                            onClick = { onAppClick(app) },
                            onLongClick = { onAppLongClick(app) },
                            compact = true
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchResultsSection(
    results: List<AppItem>,
    query: String,
    onAppClick: (AppItem) -> Unit,
    onAppLongClick: (AppItem) -> Unit,
    onClearSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (results.isEmpty()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = TextSecondary.copy(alpha = 0.5f),
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "No apps found for \"$query\"",
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Check spelling or search for another keyword",
                color = TextSecondary,
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextButton(onClick = onClearSearch) {
                Text("Clear Search", color = CyanAccent)
            }
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 80.dp),
            contentPadding = PaddingValues(vertical = 8.dp),
            modifier = modifier.fillMaxWidth()
        ) {
            items(results, key = { it.packageName }) { app ->
                AppGridItem(
                    app = app,
                    onClick = { onAppClick(app) },
                    onLongClick = { onAppLongClick(app) }
                )
            }
        }
    }
}
