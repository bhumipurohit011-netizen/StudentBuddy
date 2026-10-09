# 🎓 StudentBuddy — Android Application

StudentBuddy is an all-in-one Android mobile application tailored for students to streamline productivity, organization, and daily academic tasks. Built natively with **Kotlin**, **XML Layouts**, **SQLite**, and **Firebase Realtime Database**.

---

## 📱 Features

- **🔐 Authentication & Splash**: Clean splash screen with login form and form validation.
- **📊 Interactive Dashboard**: Modern grid dashboard with live student registration count retrieved in real-time.
- **📝 Student Registration**: Comprehensive registration supporting multiple input widgets (`EditText`, `RadioGroup`, `CheckBox`, `ToggleButton`). Saves locally to SQLite and syncs to Firebase Realtime Database.
- **👤 Profile Management**: View registered student details with full CRUD support (Update & Delete synced to both SQLite & Firebase).
- **📒 My Notes**: Built-in notepad allowing students to write, store, view, and delete personal study notes stored securely in SQLite.
- **⏱ Study Timer**: Focus/Pomodoro-style timer alternating between **15 minutes of Study** and **5 minutes of Break** with intuitive controls (Start, Pause, Reset) and toast notifications.
- **🧮 Calculator**: Standard arithmetic calculator supporting addition, subtraction, multiplication, and division with error handling.
- **📍 Location / GPS**: Fetches device GPS coordinates (Latitude & Longitude) using Android `LocationManager` and runtime permissions.
- **ℹ About**: Overview screen detailing application metadata and purpose.

---

## 🛠 Tech Stack & Architecture

- **Language**: [Kotlin](https://kotlinlang.org/)
- **UI Framework**: Android XML (ConstraintLayout, GridLayout, LinearLayout, ScrollView)
- **Local Storage**: [SQLite](https://developer.android.com/training/data-storage/sqlite) via `SQLiteOpenHelper`
- **Cloud Backend**: [Firebase Realtime Database](https://firebase.google.com/docs/database)
- **Build System**: Gradle (Kotlin DSL `.gradle.kts`) with Version Catalogs (`libs.versions.toml`)
- **Minimum SDK**: Android API 24 (Android 7.0 Nougat)
- **Target SDK**: Android API 37

---

## 📂 Project Structure

```text
StudentBuddy/
├── app/
│   ├── src/main/
│   │   ├── java/com/example/studentbuddy/
│   │   │   ├── MainActivity.kt        # Splash & Login
│   │   │   ├── DashboardActivity.kt   # Central Dashboard Hub
│   │   │   ├── RegisterActivity.kt    # Dual-storage Registration (SQLite + Firebase)
│   │   │   ├── ProfileActivity.kt     # Profile display, Update & Delete
│   │   │   ├── NotesActivity.kt       # My Notes SQLite CRUD
│   │   │   ├── StudyTimerActivity.kt  # 15 min Study / 5 min Break Timer
│   │   │   ├── CalculatorActivity.kt  # Calculator functionality
│   │   │   ├── LocationActivity.kt    # GPS / Location retrieval
│   │   │   ├── AboutActivity.kt       # About Screen
│   │   │   ├── DatabaseHelper.kt      # SQLite Database Helper (students & notes)
│   │   │   └── ToastUtils.kt          # Custom UI feedback utilities
│   │   ├── res/                       # Layouts, Drawables, Colors, Themes
│   │   └── AndroidManifest.xml        # Permissions and Activity Declarations
│   ├── google-services.json           # Firebase configuration
│   └── build.gradle.kts               # App-level dependencies and plugins
├── gradle/
│   └── libs.versions.toml             # Gradle Version Catalog
├── build.gradle.kts                   # Project-level build script
└── README.md
```

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Ladybug / Meerkat or later
- JDK 11 or higher
- Android SDK with API level 34+ installed

### Setup & Run
1. **Clone the repository:**
   ```bash
   git clone https://github.com/bhumipurohit011-netizen/StudentBuddy.git
   cd StudentBuddy
   ```
2. **Open in Android Studio:**
   - Select `Open an Existing Project` and select the cloned root directory.
3. **Sync Project with Gradle Files:**
   - Ensure `google-services.json` is present in the `app/` directory.
4. **Run on Device / Emulator:**
   - Connect an Android device or start an AVD emulator (API 24+) and click **Run (Shift + F10)**.

---

## 💾 Database Details

### 1. SQLite (`StudentBuddyDB.db`)
- **`students` table**: Stores `id`, `name`, `email`, `phone`, `year`, `interests`, and `study_mode`.
- **`notes` table**: Stores `id`, `title`, and `content`.

### 2. Firebase Realtime Database
- Cloud path: `students/<emailKey>`
- Mirrors registered student details in the cloud for cross-platform availability and real-time count metrics.

---

## 📄 License
This project is licensed under the MIT License - see the LICENSE file for details.
