# Grameen-Light

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
