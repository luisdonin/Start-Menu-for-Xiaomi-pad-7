# Xiaomi Pad 7 Floating Start Menu

A sleek, desktop-style floating start menu application tailored for the Xiaomi Pad 7 tablet. Tapping the start menu icon from the tablet dock or home screen presents an elegant, translucent, floating window launcher positioned ergonomically over the current screen, featuring rapid search, pinned favorite apps, an all-apps list, quick system controls, and instant tap-outside dismissal.

## User Review & Critical Decisions

> [!IMPORTANT]
> The floating start menu is configured specifically based on your preferences for the Xiaomi Pad 7 tablet dock experience. Review the confirmed decisions below before approving execution.

- **Confirmed: Launch from Dock as Floating Window**: Clicking the app icon from the Xiaomi Pad 7 dock immediately opens a non-fullscreen floating card with a translucent background dimming scrim. Whatever you were doing underneath remains visible.
- **Confirmed: Instant Dismissal**: Tapping anywhere outside the floating menu, pressing Back, or launching any app immediately dismisses the start menu window.
- **Confirmed: Quick Actions Included**:
  - Instant live search bar at the top with auto-focus option.
  - Pinned & favorite applications grid with one-tap toggle.
  - Context menu on long-press with App Info (`Settings`) and Quick Uninstall actions.
  - Quick system shortcuts for Volume control, Settings, Wi-Fi, and Display.

---

## 1. Overview & Core Concept

- **What It Does**: Serves as a desktop-grade Start Menu for the Xiaomi Pad 7 (HyperOS). When opened from the dock, it appears as an elevated, rounded, frosted-glass floating card (floating window) on the screen. It discovers all launchable applications on the tablet, allows searching, pinning favorites, accessing system tools, and launching apps seamlessly.
- **Target Audience / Persona**: Xiaomi Pad 7 tablet users utilizing workstation mode or keyboard/mouse accessories who want a desktop-style Start Menu experience without having to return to the full-screen home launcher.
- **Key Value**: Instant multi-tasking access to all apps and controls in a non-intrusive floating window that never takes over the full display and dismisses effortlessly.

---

## 2. User Experience & Visual Design

### Key User Flows
1. **Launch from Dock**: User taps "Start" icon on the tablet dock.
2. **Floating Window Entrance**: The menu smoothly scales and fades in as a centered/bottom-aligned floating panel (approx. 580dp wide, 620dp high) with a soft translucent backdrop scrim.
3. **App Discovery & Launch**:
   - Pinned favorites are highlighted in a prominent top grid.
   - An alphabetical "All Apps" section provides categorized or smooth-scrolling access to all installed apps with real icons and labels.
   - Live search input at the top filters apps instantly by name or package.
4. **App Context Actions**: Long-pressing or tapping the action menu on any app opens an options sheet with:
   - "Pin to Start / Unpin"
   - "App Info" (system application details)
   - "Uninstall" (direct package uninstaller dialog)
5. **Quick System Shortcuts**: Quick access bar at the top/bottom for Volume level slider/preset, System Settings, Display settings, and Start Menu customization.
6. **Effortless Dismissal**: Tapping outside the card, tapping the close button, or swiping down immediately finishes the activity with a smooth exit animation.

### Visual Identity & Theme
- **Aesthetic Direction**: HyperOS & modern workstation aesthetics—dark glassmorphism, refined rounded corners (`28.dp`), micro-borders with subtle glow, and clean high-contrast iconography.
- **Color Palette**:
  - *Backdrop Dim*: Deep translucent slate overlay (`#50000000` to `#80090D16`)
  - *Window Surface*: Frosted midnight dark surface (`#1E222D` with subtle gradient and `#323846` border)
  - *Accent & Highlights*: HyperOS electric cyan (`#00C2FF`) and vibrant coral violet accents for active pills and search focus
  - *Typography*: Clean, legible sans-serif with distinct hierarchical weights for section titles, app titles, and package metadata.
- **Motion & Feedback**:
  - Spring-based entrance scaling (`spring(stiffness = Spring.StiffnessMediumLow)`)
  - Haptic touch feedback and ripple on all clickable icons
  - Instant transition to background when an app is launched

---

## 3. Key Product Decisions & Trade-Offs

- **Translucent Activity Window vs. System Overlay Service**:
  - *Chosen Approach*: Translucent Activity configured with `android:windowIsTranslucent="true"`, transparent background, and an outer dismissible scrim.
  - *Why*: This allows direct launch from the Xiaomi Pad 7 dock/launcher without requiring users to grant sensitive `SYSTEM_ALERT_WINDOW` ("Draw over other apps") permissions or having a battery-draining foreground service running in the background. It feels native, opens instantly, and closes cleanly upon launch or tap-outside.
- **Package Visibility (`<queries>`)**:
  - *Chosen Approach*: Declare `<queries>` for `ACTION_MAIN` with `CATEGORY_LAUNCHER` in `AndroidManifest.xml`.
  - *Why*: Android 11+ restricts package visibility; this declaration guarantees full visibility into all installed launcher apps without requiring dangerous permissions.
- **Local Persistence for Pinned Apps**:
  - *Chosen Approach*: Android `SharedPreferences` / DataStore for saved pinned package names and layout preferences.
  - *Why*: Instant, zero-latency loading on start menu open, perfectly suited for device-local app preferences.

---

## 4. Technical Architecture & Data Strategy

### System & Component Diagram

```
┌─────────────────────────────────────────────────────────────┐
│                 Tablet Screen / Current App                 │
│  ┌───────────────────────────────────────────────────────┐  │
│  │         Translucent Scrim (Tap Outside -> Dismiss)     │  │
│  │                                                       │  │
│  │   ┌───────────────────────────────────────────────┐   │  │
│  │   │          Floating Start Menu Window           │   │  │
│  │   │  ┌─────────────────────────────────────────┐  │   │  │
│  │   │  │ 🔍 Search Bar & Quick System Controls   │  │   │  │
│  │   │  ├─────────────────────────────────────────┤  │   │  │
│  │   │  │ 📌 Pinned Apps Grid (Favorites)         │  │   │  │
│  │   │  ├─────────────────────────────────────────┤  │   │  │
│  │   │  │ 📱 All Apps (Alphabetical / Filtered)   │  │   │  │
│  │   │  ├─────────────────────────────────────────┤  │   │  │
│  │   │  │ ⚙️ Bottom Action Strip (Settings/Power) │  │   │  │
│  │   │  └─────────────────────────────────────────┘  │   │  │
│  │                                                       │  │
│  └───────────────────────────────────────────────────────┘  │
└──────────────────────────┬──────────────────────────────────┘
                           │ Launches Apps / Queries
                           ▼
      ┌──────────────────────────────────────────┐
      │  PackageManager & Local App Repository  │
      │  - queryIntentActivities()               │
      │  - loadLabel() & loadIcon()              │
      │  - SharedPreferences (Pinned Apps)       │
      │  - AudioManager / Intent Launcher        │
      └──────────────────────────────────────────┘
```

### State Management & Handlers
- **StartMenuViewModel**:
  - `appsState`: `StateFlow<List<AppItem>>` sorted alphabetically with cached drawable icons.
  - `pinnedState`: `StateFlow<Set<String>>` tracking user-pinned package names.
  - `searchQuery`: Live text filter matching app titles and package identifiers.
  - `activeContextApp`: Currently selected app for the action modal (Pin, App Info, Uninstall).
- **Interactive Handlers**:
  - `onAppClick(app)`: Triggers `packageManager.getLaunchIntentForPackage(app.packageName)`, launches intent, and calls `finish()` with no delay.
  - `onAppLongClick(app)`: Opens the options dialog/sheet.
  - `onDismissRequest()`: Triggers `activity.finish()` smoothly.
  - `onTogglePin(packageName)`: Updates pinned list and persists to preferences immediately.
  - `onOpenAppInfo(packageName)`: Dispatches `ACTION_APPLICATION_DETAILS_SETTINGS`.
  - `onUninstallApp(packageName)`: Dispatches `ACTION_DELETE` / `ACTION_UNINSTALL_PACKAGE`.
