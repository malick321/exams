# Exam Prep — Android app

A native shell around the offline Exam Prep question bank. Everything (all 5,274
questions, every screen) lives in `app/src/main/assets/index.html`. The Java class
only handles what a web page can't do inside a WebView: keeping localStorage alive,
opening the file picker, and writing exports into Downloads.

## Getting the APK without installing anything

1. Create a free GitHub account and make a new **private** repository.
2. Upload this whole folder to it (drag and drop works — use "uploading an existing file").
3. Open the **Actions** tab. The build starts on its own and takes about three minutes.
4. When it turns green, click the run, then download **ExamPrep-apk** at the bottom.
5. Unzip it, move `ExamPrep-debug.apk` to your phone, tap it, allow "install unknown apps".

## Building on a computer instead

Needs Android Studio or the Android SDK:

```
./gradlew assembleDebug
```

Output lands in `app/build/outputs/apk/debug/`.

## Updating the questions later

Replace `app/src/main/assets/index.html` with a newer copy and build again.
Export a backup from inside the app first — installing over the top keeps your
progress, but a backup costs nothing and protects you if anything goes wrong.
