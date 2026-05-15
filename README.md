# Grameen-Light

<<<<<<< HEAD
Grameen-Light (Energy) is an Android app for village streetlight issue reporting and repair tracking. Residents can report pole issues quickly, while Panchayat users can assign and close repairs through a PIN-protected workflow.

## Features

- Pole map with 25 seeded streetlight poles
- Quick report flow for `Working`, `Fused`, and `Burning in Day`
- Complaint ID generation in `GL-YYYYMMDD-NNN` format
- Repair tracker with status filters and update history
- Panchayat mode with 4-digit PIN protection
- Energy-saved summary for resolved daytime-burning issues
- Day audit / night audit theme switching
- Offline-first Room storage with Firebase-ready sync
- Fallback Panchayat summary when AI/cloud is unavailable

## Tech Stack

- Kotlin
- Jetpack Compose
- MVVM
- Room Database
- Firebase Firestore
- StateFlow
- Material Design 3

## Architecture

Data flow is kept strict:

`UI -> ViewModel -> Repository -> DAO -> Room`

Room is the local source of truth. Firebase Firestore is used as a remote sync layer when configuration and internet are available.

## Project Structure

```text
app/src/main/java/com/grameenlight/app
|- data/local
|- data/model
|- data/repository
|- ui/screens
|- ui/theme
|- ui/viewmodel
```

## Open in Android Studio

Open this folder in Android Studio:

## Build

```powershell
.\gradlew.bat :app:assembleDebug
```

## Firebase Setup

The project works offline without Firebase.

To enable cloud sync:

1. Create a Firebase project
2. Download `google-services.json`
3. Place it in `app/google-services.json`
4. Rebuild the app

## Demo Notes

- Panchayat demo PIN default: `1234`
- Android version target: API 28+
- Offline reports save locally first and sync later when possible
=======
Smart streetlight monitoring and complaint reporting system for rural areas.

Grameen-Light is an Android application developed to help villagers report streetlight issues easily. In many rural areas, streetlights remain fused for long periods or stay ON during daytime, causing electricity wastage and safety problems. This app provides a simple platform where users can view streetlights on a map, report issues, and track complaint status.

The application uses Google Maps for pole visualization and stores complaint data using Room Database and Firebase.

## Features

1. View streetlight poles on map  
2. Report streetlight issues  
3. Generate complaint IDs  
4. Track complaint status  
5. Color-coded marker updates  
6. Complaint history  
7. Simple user interface  

## Technologies Used

1. Kotlin  
2. Android Studio  
3. Google Maps API  
4. Room Database  
5. Firebase Realtime Database  
6. MVVM Architecture  

## How the App Works

1. User opens the application  
2. Streetlight poles are displayed on the map  
3. User selects a pole marker  
4. User reports the status  
5. Complaint ID is generated  
6. Marker color changes based on status  
7. Complaint can be tracked later  

## Pole Status Colors

1. Green → Working  
2. Red → Fused  
3. Yellow → ON during Day  

## Project Goal

The main goal of this project is to reduce electricity wastage, improve road safety, and encourage community participation in maintaining rural infrastructure.

## Future Improvements

1. IoT-based monitoring  
2. Admin dashboard  
3. Push notifications  
4. Offline support  
5. AI-based fault detection  

## Developed By

Sushma Acharya
>>>>>>> df2bcbb65a05a00769adfe393f8cb522afced7ca
