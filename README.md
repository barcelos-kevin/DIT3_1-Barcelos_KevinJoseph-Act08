# Simple Location Tracker App

## 📱 App Description

The Simple Location Tracker App is an Android mobile application that displays the user's live GPS location on an interactive map in real-time. The app leverages the device's location services to continuously update and track the user's position, displaying it as a marker on a Google Map.

**Key Features:**
- Real-time GPS location tracking
- Interactive Google Map display
- Current location marked with an animated marker
- Detailed location information (latitude and longitude)


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