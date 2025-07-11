# Attendance Tracker Android App

A modern Android application for employee attendance tracking with GPS-based validation, built with Jetpack Compose and following clean architecture principles.

## Features

### Core Functionality
- **Employee Authentication** - Secure login with employee ID and password
- **GPS-Based Attendance** - Mark attendance only when within configured office radius
- **Real-time Location Tracking** - Live GPS status and distance from office
- **Check-in/Check-out** - Simple attendance marking with location validation
- **Attendance History** - View daily and monthly attendance records
- **Dashboard** - Overview of current status and today's attendance
- **Profile Management** - User profile and app settings

### Technical Features
- **Offline-first Architecture** - Secure local data storage
- **JWT Authentication** - Token-based security with automatic refresh
- **Material 3 Design** - Modern UI with dark/light theme support
- **Real-time Updates** - Live GPS status and attendance data
- **Location Permissions** - Seamless permission handling
- **Error Handling** - Comprehensive error states and user feedback

## Architecture

### Clean Architecture
The app follows clean architecture principles with clear separation of concerns:

```
app/
├── data/           # Data layer (API, DTOs, repositories)
├── domain/         # Business logic (models, use cases)
├── presentation/   # UI layer (screens, ViewModels)
├── di/            # Dependency injection
└── utils/         # Utilities and helpers
```

### Tech Stack
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose with Material 3
- **Architecture**: MVVM (Model-View-ViewModel)
- **Dependency Injection**: Hilt
- **Networking**: Retrofit + OkHttp
- **Location Services**: FusedLocationProviderClient
- **Local Storage**: DataStore (Preferences)
- **Async Programming**: Coroutines + Flow
- **Navigation**: Navigation Compose

## Requirements

### System Requirements
- **Android API Level**: 26+ (Android 8.0)
- **Target SDK**: 34 (Android 14)
- **Kotlin Version**: 1.9.0
- **Compile SDK**: 34

### Permissions
- `ACCESS_FINE_LOCATION` - GPS-based attendance tracking
- `ACCESS_COARSE_LOCATION` - Location services
- `INTERNET` - API communication
- `ACCESS_NETWORK_STATE` - Network connectivity
- `POST_NOTIFICATIONS` - Push notifications (Android 13+)

## Setup & Installation

### Prerequisites
1. Android Studio Arctic Fox or later
2. JDK 11 or later
3. Android SDK with API level 26+

### Build Configuration
1. Clone the repository
2. Open in Android Studio
3. Sync Gradle files
4. Configure backend API endpoint in `NetworkModule.kt`
5. Build and run on device/emulator

### Backend Integration
The app integrates with a Spring Boot backend. Configure the base URL in:
```kotlin
// NetworkModule.kt
private const val BASE_URL = "http://your-backend-url:8080/"
```

## Key Dependencies

### Core Libraries
- **Jetpack Compose BOM**: 2024.02.00
- **Hilt**: 2.49
- **Retrofit**: 2.9.0
- **OkHttp**: 4.12.0
- **Navigation Compose**: 2.7.6
- **DataStore**: 1.0.0
- **Play Services Location**: 21.0.1
- **Coroutines**: 1.7.3

### Development Tools
- **Kotlin Symbol Processing (KSP)**: 1.9.0-1.0.13
- **Android Gradle Plugin**: 8.5.1
- **Accompanist Permissions**: 0.32.0

## Project Structure

### Data Layer
- **API**: REST client definitions and authentication
- **DTOs**: Data transfer objects for API communication
- **Repositories**: Data access implementations
- **Local**: DataStore preferences management

### Domain Layer
- **Models**: Core business entities
- **Repositories**: Abstract data access contracts
- **Use Cases**: Business logic implementations

### Presentation Layer
- **Screens**: Compose UI screens (Login, Dashboard, History, Profile)
- **ViewModels**: State management and business logic
- **Navigation**: Navigation graph and routing
- **Components**: Reusable UI components
- **Theme**: Material 3 theming and colors

## Features Overview

### Authentication
- Employee ID and password login
- JWT token storage and management
- Automatic session validation
- Secure logout functionality

### GPS Attendance
- Real-time location tracking
- Distance calculation from office
- Visual GPS status indicators
- Location accuracy validation
- Check-in/check-out with coordinates

### Dashboard
- Current attendance status
- Today's working hours
- Quick access to attendance marking
- GPS status overview

### History
- Daily attendance records
- Monthly view with calendar
- Date range filtering
- Working hours calculation
- Pull-to-refresh updates

### Profile
- User information display
- App settings and preferences
- Logout functionality
- App version information

## Contributing

### Code Style
- Follow Kotlin coding conventions
- Use meaningful variable and function names
- Add KDoc for public APIs
- Maintain consistent formatting

### Architecture Guidelines
- Maintain separation of concerns
- Use dependency injection
- Follow MVVM pattern
- Implement proper error handling
- Write unit tests for business logic

### Git Workflow
1. Create feature branch from main
2. Make changes with descriptive commits
3. Test thoroughly on device
4. Create pull request with description
5. Code review before merge

## Testing

### Unit Tests
- Use JUnit 4 for unit testing
- Mock dependencies with Mockito
- Test use cases and ViewModels
- Maintain high test coverage

### Integration Tests
- Use Espresso for UI testing
- Test complete user flows
- Verify API integration
- Test location services

## Build & Release

### Debug Build
```bash
./gradlew assembleDebug
```

### Release Build
```bash
./gradlew assembleRelease
```

### APK Location
Built APKs are located in:
```
app/build/outputs/apk/debug/
app/build/outputs/apk/release/
```

## License

This project is proprietary software for internal company use.

## Support

For technical issues or questions:
1. Check existing GitHub issues
2. Create new issue with detailed description
3. Include device information and logs
4. Provide steps to reproduce 