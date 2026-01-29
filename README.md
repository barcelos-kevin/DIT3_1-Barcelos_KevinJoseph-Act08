# Simple Location Tracker App

## 📱 App Description

The Simple Location Tracker App is an Android mobile application that displays the user's live GPS location on an interactive map in real-time. The app leverages the device's location services to continuously update and track the user's position, displaying it as a marker on a Google Map.

**Key Features:**
- Real-time GPS location tracking
- Interactive Google Map display
- Current location marked with an animated marker
- Automatic camera animation to follow user location
- Detailed location information (latitude and longitude)
- Location updates every 5 seconds

## 🛠 Technical Stack

- **Language:** Kotlin
- **Platform:** Android (API Level 24+)
- **Maps & Location:** Google Play Services (Maps SDK & Fused Location Provider)
- **UI Framework:** AndroidX (AppCompat, Activity, ConstraintLayout)
- **Minimum SDK:** Android 7.0 (API 24)
- **Target SDK:** Android 15 (API 36)

## 📋 Minimum Functional Requirements - COMPLETED ✅

1. ✅ **Location Permission Request and Handling**
   - Runtime permissions requested at app startup
   - `ACCESS_FINE_LOCATION` and `ACCESS_COARSE_LOCATION` permissions declared in AndroidManifest.xml
   - Permission dialog shown to user on Android 6.0+ devices
   - App gracefully handles permission denial with Toast notification

2. ✅ **Map Displayed on Screen**
   - Google Map fragment integrated into MainActivity layout
   - Map fragment initialized via `SupportMapFragment` with async callback
   - Default zoom level and camera position set on map ready

3. ✅ **Marker Showing Current User Location**
   - Blue marker placed at current GPS coordinates
   - Marker includes title ("Current Location") and snippet with lat/lng
   - Previous markers cleared before adding new ones

4. ✅ **Location Updates When Device Moves**
   - Continuous location updates every 5 seconds using `LocationCallback`
   - High accuracy priority set via `PRIORITY_HIGH_ACCURACY`
   - Map camera automatically animates to follow user location
   - Updates handled in `onLocationResult()` callback

5. ✅ **Basic UI**
   - Single-screen application with map occupying full screen
   - Simple and intuitive user interface
   - Toast notifications for permission and location update status

## 📦 Permissions Used

**Required Permissions:**
- `android.permission.ACCESS_FINE_LOCATION` - Access precise GPS location
- `android.permission.ACCESS_COARSE_LOCATION` - Access approximate location via network
- `android.permission.INTERNET` - Required for map tiles and location services

These permissions are declared in `AndroidManifest.xml` and requested at runtime on Android 6.0+ devices.

## 🗺 How GPS Location is Obtained

### Location Flow:

1. **Initialization (onCreate)**
   - `FusedLocationProviderClient` is initialized using Google Play Services
   - This client provides the most accurate location by combining GPS, WiFi, and cellular data

2. **Permission Handling**
   - App checks if `ACCESS_FINE_LOCATION` permission is granted
   - If not granted, user is prompted with a system permission dialog
   - Permission result is handled in `onRequestPermissionsResult()`

3. **Location Updates (startLocationUpdates)**
   - `LocationRequest` created with:
     - `PRIORITY_HIGH_ACCURACY` - Uses GPS with highest accuracy
     - `LOCATION_UPDATE_INTERVAL = 5000L` - Updates every 5 seconds
   - `LocationCallback` registered with `FusedLocationProviderClient`
   - Device starts reporting location updates via `onLocationResult()`

4. **Location Processing (updateMapLocation)**
   - Each location update triggers map refresh:
     - Current marker cleared
     - New marker added at updated coordinates
     - Camera animates to follow user position
     - Zoom level: 15f (street level)

5. **Lifecycle Management**
   - Location updates **start** in `onResume()` when app comes to foreground
   - Location updates **stop** in `onPause()` to save battery
   - Map ready callback initiates first location update
   - Callback properly unregistered on pause to prevent memory leaks

### Location Accuracy:
- **High Accuracy Mode:** Uses GPS, WiFi, and mobile networks
- **Update Frequency:** Every 5 seconds (customizable)
- **Coordinate Format:** Latitude and Longitude in decimal degrees
- **Display Precision:** 4 decimal places (approximately 11 meters accuracy)

## 📁 Project Structure

```
LocationTrackingApp/
│
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── AndroidManifest.xml          # App manifest with permissions
│   │   │   ├── java/com/example/locationtrackingapp/
│   │   │   │   └── MainActivity.kt          # Main activity with location tracking
│   │   │   └── res/
│   │   │       └── layout/
│   │   │           └── activity_main.xml    # Map fragment layout
│   │   ├── test/                            # Unit tests
│   │   └── androidTest/                     # Instrumented tests
│   │
│   └── build.gradle.kts                     # App-level build configuration
│
├── gradle/
│   └── libs.versions.toml                   # Dependency versions
│
├── build.gradle.kts                         # Project-level build configuration
├── settings.gradle.kts                      # Gradle settings
└── README.md                                 # This file
```

## 🚀 Getting Started

### Prerequisites
- Android Studio (Latest version)
- Android SDK with minimum API level 24
- Google Play Services installed on your test device or emulator

### Setup Instructions

1. **Clone/Open Project**
   ```bash
   cd LocationTrackingApp
   ```

2. **Build Project**
   - In Android Studio: Build > Build Project
   - Or via command line: `./gradlew build`

3. **Configure Google Maps API Key**
   - Note: For production, a Google Maps API key is required in AndroidManifest.xml
   - Current build includes basic map functionality for testing

4. **Run Application**
   - Connect Android device (API 24+) or start emulator
   - Android Studio: Run > Run 'app'
   - Or: `./gradlew installDebug`

5. **Grant Permissions**
   - When app launches, grant location permissions
   - App will start tracking location immediately

## ⚠️ Common Pitfalls Avoided

### ✅ Runtime Permission Handling
- App requests permissions at runtime (not just manifest)
- Proper check with `ActivityCompat.checkSelfPermission()` before location access
- User prompted even if permission denied previously

### ✅ Graceful Permission Denial
- App doesn't crash if permission is denied
- Toast notification informs user: "Permission denied"
- Map displays with default location if permissions not granted
- User can manually grant permissions through app settings

### ✅ Lifecycle Management
- Location updates properly stopped in `onPause()`
- Callbacks unregistered to prevent memory leaks
- Updates resume in `onResume()` when app returns to foreground
- No continuous location tracking drains battery

### ✅ Simple Map Implementation
- Only essential map features used (marker, camera animation)
- No complex map overlays or custom styling
- Default Google Map tiles for simplicity
- Clear separation of concerns in code

### ✅ API Key Security
- No hardcoded API keys in source code
- Sensitive information kept out of repository
- Instructions provided for secure key configuration

## 📝 Configuration Notes

### Location Update Interval
To adjust update frequency, modify `LOCATION_UPDATE_INTERVAL` in MainActivity.kt:
```kotlin
private const val LOCATION_UPDATE_INTERVAL = 5000L // 5000 milliseconds = 5 seconds
```

### Zoom Level
To change initial map zoom, adjust the zoom parameter in `onMapReady()`:
```kotlin
mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 12f))
// 12f = zoom level (higher = more zoomed in)
```

## 🔐 Security Considerations

- Location permissions appropriately restricted
- No personal data stored persistently
- Location only accessed when app is in foreground
- Background location access not implemented (not required for this app)

## 🧪 Testing

The app has been tested with:
- Android devices running API 24 and above
- Various location scenarios (GPS, WiFi, cellular)
- Permission grant/deny scenarios
- App background/foreground transitions

## 📄 License

This project is created for educational purposes as part of a laboratory activity.

## 👨‍💻 Author

Created as a Simple Location Tracker laboratory activity for mobile development course.

---

**Status:** ✅ Complete - All minimum requirements met
**Last Updated:** January 29, 2026

#   D I T 3 _ 1 - B a r c e l o s _ K e v i n J o s e p h - A c t 0 8  
 