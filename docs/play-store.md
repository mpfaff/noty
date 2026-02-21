# Google Play Store Submission — Noty

📄 For project overview, see /docs/agent.md
📄 For full feature list, see /docs/features.md

All fields below map directly to the Google Play Console. Fields marked `[TODO]` require input before submission.

---

## Store Listing

### App Name
> Max 30 characters

```
Noty - Notification Notes
```
*(25 characters)*

---

### Short Description
> Max 80 characters. Shown in search results and below the app name on the listing page.

```
Pin your notes to notifications — always visible, never forgotten.
```
*(66 characters)*

---

### Full Description
> Max 4000 characters. Supports basic formatting (blank lines between paragraphs; no markdown).

```
Never forget a task again. Noty keeps your notes exactly where you look dozens of times a day — your notification shade.

Write a note, and it instantly appears as a persistent notification in your status bar. It stays there until you're done with it. No need to open any app, set a reminder, or remember to check a list.

KEY FEATURES

📌 Persistent Notifications
Every note you create appears as a notification that stays in your status bar. Your notes are always one swipe away.

🔄 Survives Everything
Notes are restored automatically after a phone restart. The app runs a lightweight background service to keep your notifications alive even after you close the app.

✏️ Add, Edit, Delete — Instantly
Create notes with a title and optional description. Edit or delete any note with a tap. A confirmation step prevents accidental deletions.

🔒 Sticky & Non-Sticky Notes
Mark a note as sticky and it cannot be dismissed from the notification shade — it stays until you delete it yourself. Non-sticky notes can be swiped away directly from the notifications, which removes them automatically.

⚡ Quick Settings Tile
Add a "Quick Note" tile to your Quick Settings panel. Tap it to open the add-note screen without unlocking your phone or navigating to the app.

🎨 Material You Design
Noty uses Material 3 with dynamic color — on Android 12 and above, the app automatically matches your wallpaper's color palette. Light, Dark, and System themes are all supported and persist across sessions.

🔍 Search Your Notes
Quickly filter your notes by title or description with the built-in search bar.

🔒 100% Private
Noty is entirely local. No accounts, no cloud, no network access. Your notes never leave your device.

PERMISSIONS EXPLAINED
• Notifications — required to show your notes as persistent notifications
• Run at Startup — used to restore your notes after your phone restarts
• Foreground Service — allows Noty to keep notifications active in the background

No ads. No in-app purchases. No data collection.
```
*(2,087 characters)*

---

## App Details

| Field | Value |
|-------|-------|
| **Application ID** | `com.example.noty` |
| **Category** | Productivity |
| **Tags** | notes, tasks, notifications, reminder, productivity, sticky notes |
| **Pricing** | Free |
| **Contains Ads** | No |
| **In-App Purchases** | No |

> **Note:** The application ID `com.example.noty` uses an `example` namespace which Google may flag. Rename to a personal or organization domain (e.g. `com.yourname.noty`) before submitting.

---

## Content Rating

Complete the content rating questionnaire in the Play Console. Based on the app's functionality, expected answers:

| Question | Answer |
|----------|--------|
| Violence | None |
| Sexual content | None |
| User-generated content shared online | No |
| Social features (chat, friends, etc.) | No |
| Location sharing | No |
| Personal information collection | No |
| Ads | No |
| **Expected rating** | **Everyone (E)** |

---

## Data Safety Section

Google Play requires a Data Safety declaration. Noty collects and shares no user data.

| Question | Answer |
|----------|--------|
| Does the app collect or share any user data? | **No** |
| Does the app use encryption in transit? | N/A (no network) |
| Does the app allow users to request data deletion? | N/A (all data is local; uninstalling removes all data) |

**Data types collected:** None
**Data types shared with third parties:** None
**Security practices:** Data is not collected or transmitted

> In the Play Console under "Data safety", select "This app doesn't collect or share any user data" and confirm no data types are used.

---

## Permissions Declaration

| Permission | Declared | Reason to disclose to users |
|------------|----------|-----------------------------|
| `POST_NOTIFICATIONS` | Yes (runtime, API 33+) | Required to display note notifications |
| `RECEIVE_BOOT_COMPLETED` | Yes | Restores notifications after device restart |
| `FOREGROUND_SERVICE` | Yes | Keeps notification service running in background |
| `FOREGROUND_SERVICE_DATA_SYNC` | Yes | Background service type required by Android 14+ |

No sensitive permissions (location, camera, microphone, contacts, SMS, etc.) are used.

---

## Graphic Assets

All assets must be prepared before submission. Exact specs below.

### App Icon
| Spec | Requirement |
|------|-------------|
| Format | 32-bit PNG (with alpha) |
| Size | 512 × 512 px |
| Source | Use `ic_launcher_foreground.xml` / `ic_launcher_background.xml` as the basis |
| Status | `[TODO]` — export a 512×512 PNG from the adaptive icon assets |

### Feature Graphic
> Shown at the top of the store listing and in promotional placements.

| Spec | Requirement |
|------|-------------|
| Format | JPEG or 24-bit PNG (no alpha) |
| Size | 1024 × 500 px |
| Content | App name + tagline; avoid placing critical content at edges (may be cropped) |
| Status | `[TODO]` — create graphic |

### Screenshots
> Minimum 2 required. Up to 8 per device type. At least phone screenshots are mandatory.

| Device | Min size | Max size | Min count |
|--------|----------|----------|-----------|
| Phone | 320 px (short side) | 3840 px (long side) | 2 |
| 7" Tablet | 320 px | 3840 px | 0 (optional) |
| 10" Tablet | 320 px | 3840 px | 0 (optional) |

**Suggested screenshot sequence (phone):**

| # | Screen | What to show |
|---|--------|-------------|
| 1 | Notes list (populated) | Several note cards visible, notifications in status bar |
| 2 | Add note sheet | Bottom sheet open with title/description/sticky toggle |
| 3 | Notification shade | Multiple Noty notifications visible |
| 4 | Empty state | Clean empty state screen |
| 5 | Theme picker | Theme selection bottom sheet (dark mode active) |
| 6 | Quick Settings tile | Tile shown in Quick Settings panel |

Status: `[TODO]` — capture screenshots on a physical device or emulator

---

## Contact & Support

| Field | Value |
|-------|-------|
| **Email** | `[TODO]` — add a support email address |
| **Website** | `[TODO]` — optional; add if a landing page exists |
| **Phone** | Leave blank (optional) |
| **Privacy Policy URL** | `[TODO]` — required; see note below |

### Privacy Policy
Google Play **requires** a privacy policy URL for all apps, even those that collect no data. Host a simple policy page stating:
- The app collects no personal data
- No data is shared with third parties
- All data is stored locally on the device
- Uninstalling the app removes all data

A minimal one-page policy hosted on GitHub Pages, Notion, or similar is sufficient.

Status: `[TODO]` — draft and host a privacy policy, then add the URL here and in the Play Console

---

## Release Notes (First Release)

> Shown to users on the "What's new" section. Max 500 characters.

```
Initial release of Noty — pin your notes as persistent notifications so you never forget them. Features include sticky and non-sticky notes, Quick Settings tile, light/dark/system themes with Material You, and automatic restoration after device restart.
```
*(251 characters)*

---

## Pre-Launch Checklist

- [ ] Rename application ID from `com.example.noty` to a personal/org domain
- [ ] Build and test a signed release APK (`./gradlew assembleRelease`)
- [ ] Set up a signing keystore and configure `signingConfigs` in `app/build.gradle`
- [ ] Export 512×512 app icon PNG
- [ ] Create 1024×500 feature graphic
- [ ] Capture at least 2 phone screenshots
- [ ] Draft and host a privacy policy, add URL to Play Console
- [ ] Add a support email address
- [ ] Complete the content rating questionnaire in Play Console
- [ ] Complete the data safety section in Play Console
- [ ] Verify all permissions are declared and justified
- [ ] Test on a clean device (not dev machine) with no debug flags
