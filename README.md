# PainTracker

![Build Status](https://github.com/devoctomy/PainTracker/actions/workflows/android.yml/badge.svg)

Simple pain tracking app for Android written in Kotlin.

I created this app to help track pain symptoms whilst receiving physiotherapy over an extended period of time.  Usually when asked where I have felt discomfort over the previous week my answer would be quite vague, using an app like this I wanted to just dump out a PDF report for the previous week containing all the necessary information.

## Requirements

* Android Studio (Created using 2024.2.1 Patch 3)
* OpenJDK

I am using the following,

openjdk 21.0.4 2024-07-16
OpenJDK Runtime Environment OpenLogic-OpenJDK (build 21.0.4+7-adhoc.Administrator.jdk21u)
OpenJDK 64-Bit Server VM OpenLogic-OpenJDK (build 21.0.4+7-adhoc.Administrator.jdk21u, mixed mode, sharing)

* Android SDK 14 (API Level 34)
* Android SDK Build-Tools 36-rc3
* Android SDK Platform-Tools 35.0.2

### Output from Android Studio

Android Studio Ladybug | 2024.2.1 Patch 3
Build #AI-242.23339.11.2421.12700392, built on November 22, 2024
Runtime version: 21.0.3+-12282718-b509.11 amd64
VM: OpenJDK 64-Bit Server VM by JetBrains s.r.o.
Toolkit: sun.awt.windows.WToolkit
Windows 11.0
GC: G1 Young Generation, G1 Concurrent GC, G1 Old Generation
Memory: 4096M
Cores: 32
Registry:
  ide.experimental.ui=true
  i18n.locale=

## Progress

- [x] Date Selection, default to today
- [x] Pain input via a number of preset categories
- [x] Notes input
- [x] Ability to edit previously entered data
- [x] Simple export to PDF functionality
- [x] Ability to see overview / listing of all data
- [ ] Ability to add custom pain categories
- [ ] Google Drive backup / storage support

## Screenshots

![Front of body pain](https://github.com/devoctomy/PainTracker/raw/main/Documentation/Screenshots/Screenshot-2024-12-25-122152.png)

![Pain notes](https://github.com/devoctomy/PainTracker/raw/main/Documentation/Screenshots/Screenshot-2024-12-25-122254.png)

![Back of body pain](https://github.com/devoctomy/PainTracker/raw/main/Documentation/Screenshots/Screenshot-2024-12-25-122336.png)

![Date Picker](https://github.com/devoctomy/PainTracker/raw/main/Documentation/Screenshots/Screenshot-2024-12-25-122359.png)

![PDF Pain Report](https://github.com/devoctomy/PainTracker/raw/main/Documentation/Screenshots/Screenshot-2024-12-29-165151.png)

