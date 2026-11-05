package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppRepository
import com.example.model.AppItem
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface StartMenuNavigationEvent {
    object Dismiss : StartMenuNavigationEvent
}

data class StartMenuUiState(
    val isLoading: Boolean = true,
    val searchQuery: String = "",
    val allApps: List<AppItem> = emptyList(),
    val filteredApps: List<AppItem> = emptyList(),
    val pinnedApps: List<AppItem> = emptyList(),
    val selectedAppContext: AppItem? = null,
    val showVolumeQuickSlider: Boolean = false
)

class StartMenuViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AppRepository(application.applicationContext)

    private val _rawApps = MutableStateFlow<List<AppItem>>(emptyList())
    private val _searchQuery = MutableStateFlow("")
    private val _selectedAppContext = MutableStateFlow<AppItem?>(null)
    private val _isLoading = MutableStateFlow(true)
    private val _showVolumeQuickSlider = MutableStateFlow(false)

    private val _navigationEvents = MutableSharedFlow<StartMenuNavigationEvent>()
    val navigationEvents: SharedFlow<StartMenuNavigationEvent> = _navigationEvents.asSharedFlow()

    val uiState: StateFlow<StartMenuUiState> = combine(
        _rawApps,
        _searchQuery,
        repository.pinnedPackages,
        _selectedAppContext,
        _isLoading
    ) { apps, query, pinnedPkgs, contextApp, loading ->

        // Update isPinned flag on each app item
        val appsWithPinned = apps.map { app ->
            app.copy(isPinned = pinnedPkgs.contains(app.packageName))
        }

        val filtered = if (query.isBlank()) {
            appsWithPinned
        } else {
            val q = query.trim().lowercase()
            appsWithPinned.filter {
                it.label.lowercase().contains(q) || it.packageName.lowercase().contains(q)
            }
        }

        // Pinned apps that exist on device
        val pinned = appsWithPinned.filter { pinnedPkgs.contains(it.packageName) }

        StartMenuUiState(
            isLoading = loading,
            searchQuery = query,
            allApps = appsWithPinned,
            filteredApps = filtered,
            pinnedApps = pinned,
            selectedAppContext = contextApp,
            showVolumeQuickSlider = _showVolumeQuickSlider.value
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = StartMenuUiState()
    )

    init {
        loadApps()
    }

    fun loadApps() {
        viewModelScope.launch {
            _isLoading.value = true
            val apps = repository.loadInstalledApps()
            _rawApps.value = apps
            _isLoading.value = false
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun clearSearch() {
        _searchQuery.value = ""
    }

    fun onAppClicked(app: AppItem) {
        val launched = repository.launchApp(app.packageName)
        if (launched) {
            viewModelScope.launch {
                _navigationEvents.emit(StartMenuNavigationEvent.Dismiss)
            }
        }
    }

    fun onAppLongClicked(app: AppItem) {
        _selectedAppContext.value = app
    }

    fun dismissAppContext() {
        _selectedAppContext.value = null
    }

    fun togglePin(app: AppItem) {
        repository.togglePin(app.packageName)
        dismissAppContext()
    }

    fun openAppInfo(app: AppItem) {
        repository.openAppInfo(app.packageName)
        dismissAppContext()
        viewModelScope.launch {
            _navigationEvents.emit(StartMenuNavigationEvent.Dismiss)
        }
    }

    fun uninstallApp(app: AppItem) {
        repository.uninstallApp(app.packageName)
        dismissAppContext()
        viewModelScope.launch {
            _navigationEvents.emit(StartMenuNavigationEvent.Dismiss)
        }
    }

    fun openSettings() {
        repository.openSettings()
        viewModelScope.launch {
            _navigationEvents.emit(StartMenuNavigationEvent.Dismiss)
        }
    }

    fun openSoundSettings() {
        repository.openSoundSettings()
        viewModelScope.launch {
            _navigationEvents.emit(StartMenuNavigationEvent.Dismiss)
        }
    }

    fun openDisplaySettings() {
        repository.openDisplaySettings()
        viewModelScope.launch {
            _navigationEvents.emit(StartMenuNavigationEvent.Dismiss)
        }
    }

    fun openWifiSettings() {
        repository.openWifiSettings()
        viewModelScope.launch {
            _navigationEvents.emit(StartMenuNavigationEvent.Dismiss)
        }
    }

    fun dismissMenu() {
        viewModelScope.launch {
            _navigationEvents.emit(StartMenuNavigationEvent.Dismiss)
        }
    }
}
