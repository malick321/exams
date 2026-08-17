# Turning Exam Prep into a real app

Three routes are in this folder. Pick one. You don't need to code for any of them.

---

## Route 1 — Android APK, built for you online (recommended)

You get a proper `.apk` file with its own icon. No computer software to install.

1. Sign up at **github.com** (free).
2. Click **+** at the top right → **New repository**. Name it `exam-prep`, tick **Private**, create it.
3. On the empty repo page click **uploading an existing file**. Drag in *everything* from this
   folder. Wait for the upload to finish, then click **Commit changes**.
4. Go to the **Actions** tab. A job called *Build the APK* starts by itself. Give it 3–5 minutes
   until there's a green tick.
5. Click that finished run. At the bottom under **Artifacts**, download **ExamPrep-apk**.
6. Unzip it. Move `ExamPrep-debug.apk` onto your phone and tap it.
   Android will ask permission to install from unknown sources — allow it, then install.

You now have Exam Prep in your app drawer. It opens fullscreen with no address bar, works with
mobile data off, and the back button moves through the app instead of closing it.

**To update later:** replace `app/src/main/assets/index.html` in the repo with a newer file.
The build reruns on its own and you download a fresh APK. Installing it over the old one keeps
all your progress.

---

## Route 2 — iPhone (and Android too, if you'd rather skip the APK)

An `.apk` can never be installed on an iPhone; Apple doesn't permit it. This route gives you the
same app-like result on iOS, and it works on Android as well.

1. Do steps 1–3 of Route 1 (upload this folder to a GitHub repo).
2. In the repo go to **Settings → Pages**.
3. Under *Build and deployment*, set **Source** to `Deploy from a branch`, pick branch **main**
   and folder **/docs**, then Save.
4. Wait about a minute, refresh, and copy the address it shows you
   (`https://yourname.github.io/exam-prep/`).
5. Open that address on your phone.
   - **iPhone:** Safari → Share button → **Add to Home Screen**.
   - **Android:** Chrome → ⋮ menu → **Install app** (or *Add to Home screen*).

Same result: real icon, fullscreen, no browser bar, its own window when you switch apps.
After the first open it runs completely offline.

If you'd rather nobody could stumble across the address, keep the repo private and use Route 1
instead — Pages needs the repo to be public on a free account.

---

## Route 3 — APK in two minutes, no GitHub

Quickest, but it goes through someone else's website and free tiers sometimes add a splash screen.

1. Open **webintoapp.com/app-maker** (or **median.co**).
2. Choose the option to build from an **HTML file / offline website**, not a URL.
3. Upload `ExamPrep.html` from this folder.
4. Set the app name to `Exam Prep` and upload `docs/icon-512.png` as the icon.
5. Build, then download the APK and install it on your phone.

I'd use Route 1 over this — it's yours, it's private, and nothing gets added to your app.

---

## What each folder is

| Item | What it's for |
|---|---|
| `ExamPrep.html` | The app as one file. Works on its own in any browser. |
| `docs/` | Same app plus the bits that make it installable from a web address (Routes 1 & 2). |
| `app/`, `gradle*`, `settings.gradle` | The Android project the APK is built from. |
| `.github/workflows/` | The recipe GitHub follows to build your APK. |

---

## Two things worth knowing

**Your progress lives on the phone, not in the file.** Swapping in a newer version of the app
keeps everything. Clearing Chrome's data, or uninstalling the APK, wipes it. Before any big
change, open **Settings → Export backup** and put that file in your Drive.

**The APK is a debug build.** That only means it's signed with Android's standard test key
instead of a paid developer key. It installs and runs exactly the same. It just can't go on the
Play Store, which you don't need since this is for you.
