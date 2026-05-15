# Grameen-Light

Grameen-Light is an Android-based smart streetlight monitoring and complaint management system developed for rural communities. The application helps villagers easily report streetlight problems such as fused lights, damaged poles, or streetlights remaining ON during daytime. It also enables Panchayat authorities to monitor complaints, assign repairs, and track maintenance activities efficiently.

The project aims to reduce electricity wastage, improve road safety, and simplify communication between villagers and local authorities through a digital platform.

---

# Problem Statement

In many rural areas, streetlight maintenance is handled manually, which often leads to delayed repairs and poor monitoring. Villagers usually need to report issues physically to local authorities, making the process slow and inefficient.

Common issues include:
- Streetlights remaining fused for long periods
- Lights staying ON during daytime causing energy wastage
- Lack of proper complaint tracking
- Delayed repair management
- Poor visibility and road safety during nighttime

These problems highlight the need for a simple, technology-based reporting and monitoring solution.

---

# Key Features

## 1. Streetlight Pole Mapping
- Displays streetlight poles on Google Maps
- Includes 25 pre-seeded pole locations
- Easy pole identification and selection

## 2. Quick Complaint Reporting
Users can report issues such as:
- Working
- Fused
- Burning During Daytime

Each complaint is recorded instantly through a simplified reporting flow.

## 3. Complaint ID Generation
- Automatically generates complaint IDs in the format:
  `GL-YYYYMMDD-NNN`
- Helps in complaint tracking and management

## 4. Repair Tracking System
- Track complaint status updates
- View maintenance history
- Filter complaints based on repair status

## 5. Panchayat Management Mode
- Secure 4-digit PIN-protected access
- Enables authorities to assign and close repair tasks
- Improves maintenance workflow management

## 6. Energy Saving Summary
- Displays estimated energy savings from resolved daytime-burning issues
- Encourages efficient electricity usage

## 7. Day and Night Audit Themes
- Supports separate themes for day audits and night audits
- Enhances monitoring visibility based on inspection conditions

## 8. Offline-First Functionality
- Reports are stored locally using Room Database
- Data syncs with Firebase when internet becomes available

## 9. Fallback Panchayat Summary
- Local summaries remain available even when cloud services or AI features are unavailable

---

# Technologies Used

- Kotlin
- Android Studio
- Jetpack Compose
- MVVM Architecture
- Google Maps API
- Room Database
- Firebase Firestore
- StateFlow
- Material Design 3

---

# Architecture

The application follows a clean MVVM architecture pattern:

UI → ViewModel → Repository → DAO → Room Database

- Room Database acts as the local source of truth
- Firebase Firestore is used for remote cloud synchronization
- StateFlow is used for reactive state management

---

# Project Structure

```plaintext
app/src/main/java/com/grameenlight/app
│
├── data/local
├── data/model
├── data/repository
├── ui/screens
├── ui/theme
└── ui/viewmodel
```

---

# Application Workflow

1. User opens the Grameen-Light application
2. Streetlight poles are displayed on the map
3. User selects a pole marker
4. User reports the issue status
5. Complaint ID is generated automatically
6. Marker color changes based on complaint type
7. Panchayat users review and manage complaints
8. Complaint status and repair history can be tracked later

---

# Pole Status Indicators

- Green → Working
- Red → Fused
- Yellow → ON During Daytime

These color-coded markers help users quickly identify streetlight conditions.

---

# Firebase Setup

The application can work completely offline without Firebase.

To enable cloud synchronization:

1. Create a Firebase project
2. Download `google-services.json`
3. Place the file inside:
   `app/google-services.json`
4. Rebuild the application


---

# Future Enhancements

Future improvements planned for the project include:

- IoT-based automatic streetlight fault detection
- Smart energy monitoring system
- Push notification alerts
- Advanced admin dashboard
- AI-based fault prediction
- Real-time analytics and reporting

---

# Developed By

Developed by Sushma Acharya as an academic and social-impact project focused on smart rural infrastructure management.

---

# Conclusion

Grameen-Light is a practical and innovative Android application designed to modernize rural streetlight monitoring and complaint management. By combining mobile technology, offline storage, cloud synchronization, and smart reporting features, the system improves maintenance efficiency, reduces energy wastage, and contributes toward building smarter and safer rural communities.
