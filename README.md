<div align="center">

# Early_Enlightenment

**Mental Wellness Check-In Application**

A project developed by Jayden Bridges, Peyton Lucas, and Taylor McDile.

</div>

---

## Table of Contents

- [Project Description](#project-description)
- [Complete Team Installation and Setup Instructions](#complete-team-installation-and-setup-instructions)
- [Cloudflare Workers AI Architecture & Code Breakdown](#cloudflare-workers-ai-architecture--code-breakdown)
- [Team Members](#team-members)
- [Project Tasks & Current Development Status](#project-tasks--current-development-status)
- [Contributors](#contributors)

---

## Project Description

Early Enlightenment is a mental wellness check-in application designed to help users reflect on their thoughts, receive personalized affirmations, and discover small self-care activities. 

The application uses AI API integration to generate reflections, supportive affirmations, and personalized self-care suggestions based on user input. 

---

## Complete Team Installation and Setup Instructions

**Project:** Early Enlightenment — Triple Threat  
**GitHub Repository:** https://github.com/aedwardsxula/Early_Enlightenment  
**Cloudflare AI:** https://early-enlightenment-aicopy.jbridg2.workers.dev

These instructions are for all team members. Complete each step before moving to the next.

---

### Step 1 — Install Android Studio

1. Open https://developer.android.com/studio.
2. Download Android Studio for your computer.
3. Open the installer.
4. Follow the installation instructions.
5. Open Android Studio after installation.
6. Complete the setup wizard.
7. Allow Android Studio to download its required components.

### Step 2 — Install Git

1. Open https://git-scm.com/downloads.
2. Download Git for your operating system.
3. Install Git using the recommended settings.
4. Open Command Prompt or PowerShell.
5. Enter:

```bash
git --version
```

6. Confirm that a Git version number appears.

### Step 3 — Download the Team Project

1. Open Android Studio.
2. On the welcome screen, click **Clone Repository** or **Get from VCS**.
3. Select Git.
4. Paste this repository URL:

```text
https://github.com/aedwardsxula/Early_Enlightenment.git
```

5. Choose a location on your computer to save the project.
6. Prefer a local folder such as `C:\AndroidProjects` rather than OneDrive.
7. Click **Clone**.
8. Sign in to GitHub if requested.
9. Wait for the repository to download.
10. Open the project.

Make sure you open the folder containing `settings.gradle.kts` and the `app` directory.

### Step 4 — Check the Project Versions & JDK Differences

The project files specify these versions:

| Component | Version | Notes |
|---|---|---|
| Gradle Wrapper | 9.6.0 | Managed automatically via `gradle-wrapper.properties` |
| Android Gradle Plugin | 9.4.0 | Configured in `gradle/libs.versions.toml` |
| Kotlin | 2.2.10 | Plugin `org.jetbrains.kotlin.plugin.compose` |
| Java Source/Target Compatibility | 17 | Configured in `app/build.gradle.kts` (`JavaVersion.VERSION_17`) |
| Gradle Runtime / Daemon JDK | Java 25 (or JDK 17+) | Auto-detected / Embedded JDK running Gradle 9.6.0 |
| Compile SDK | 37 | Android 16 (API Level 37) |
| Target SDK | 37 | Android 16 (API Level 37) |
| Minimum SDK | 26 | Android 8.0 (Oreo) |

**Understanding JDK vs. Java Source Compatibility:**
- **Gradle Runtime / Daemon JDK (Java 25 / JDK 17+):** This is the Java Virtual Machine that executes Gradle build tasks inside Android Studio. Gradle 9.6.0 supports JDK 17 through JDK 25.
- **Java Source & Target Compatibility (`17`):** This dictates the Java bytecode language level emitted by the Kotlin and Java compilers (`JavaVersion.VERSION_17`).
- **Rule for Teammates:** You do not need to force a specific JDK version if your Android Studio Gradle JDK is set to **Embedded JDK** or any compatible **JDK 17+**. Do not change plugin or version numbers in build files simply because Android Studio displays an upgrade notification.

### Step 5 — Configure Gradle

1. In Android Studio, click **File → Settings** (or **Android Studio → Settings** on macOS).
2. Open **Build, Execution, Deployment**.
3. Select **Build Tools → Gradle**.
4. Under **Gradle JDK**, select **Embedded JDK** or an installed **JDK 17+** (such as Java 17, 21, or 25).
5. Click **Apply**, then **OK**.
6. In the Project panel, open:

   `gradle → wrapper → gradle-wrapper.properties`

7. Confirm that the file contains:

```properties
distributionUrl=https\://services.gradle.org/distributions/gradle-9.6.0-bin.zip
```

8. Select **File → Sync Project with Gradle Files**.
9. Allow Gradle to download its required files.
10. Wait until synchronization finishes without errors.

### Step 6 — Check the Build Directory Configuration

1. Open `app/build.gradle.kts`.
2. Find the build directory line:
   ```kotlin
   layout.buildDirectory.set(file(System.getProperty("java.io.tmpdir") + "/Early_Enlightenment_build/app"))
   ```
3. Make sure this line does not reference another team member's personal Windows directory, such as `C:/Users/jbridg2/...`.
4. Using `System.getProperty("java.io.tmpdir")` provides a portable, cross-platform build directory that works on every computer without OneDrive file locking errors.
5. Sync the project again after confirming this setting.

### Step 7 — Install the Required Android SDK

1. Click **Tools → SDK Manager**.
2. Select the **SDK Platforms** tab.
3. Install the Android SDK platform required by `compileSdk = 37` (**Android 16 API Level 37**).
4. Select the **SDK Tools** tab.
5. Install or verify:
   - Android SDK Build-Tools
   - Android SDK Platform-Tools
   - Android SDK Command-line Tools
   - Android Emulator
6. Click **Apply**.
7. Accept the licenses.
8. Wait for downloads to finish, then click **OK**.

### Step 8 — Create an Android Emulator

1. Click **Tools → Device Manager**.
2. Click **Create Device**.
3. Choose a supported Pixel phone (e.g., Pixel 8 or Pixel 7).
4. Click **Next**.
5. Choose a system image with API level 26 or higher (e.g., API Level 34+).
6. Download the image if necessary.
7. Click **Next**, then **Finish**.
8. Click the green **Play** button next to the emulator in Device Manager.

### Step 9 — Build and Run Early Enlightenment

1. Return to Android Studio.
2. Check that Gradle Sync completed successfully.
3. At the top toolbar, select the `app` (or `Tripple_Threat.app`) run configuration.
4. Select your booted emulator.
5. Click **Build → Make Project**.
6. Confirm the build succeeds.
7. Click the green **Run** button (or press `Shift+F10`).

If there is no application run configuration:
1. Click **Run → Edit Configurations**.
2. Click `+` ➔ **Android App**.
3. Set **Module** to `Early_Enlightenment.app` (or `Tripple_Threat.app`).
4. Select **Default Activity** under Deploy.
5. Click **Apply** ➔ **OK**.

### Step 10 — Locate the Application Source Code

In Android Studio, switch the left panel to **Project** view.

Open:
`app/src/main/java/com/example/tripplethreat/MainActivity.kt`

This file contains the UI screens and Cloudflare Workers AI integration.

### Step 11 — How Cloudflare Workers AI Integration Works (Code Breakdown)

Our AI service uses **Cloudflare Workers AI**:

- **Worker Endpoint URL:** `https://early-enlightenment-aicopy.jbridg2.workers.dev`
- **Workers AI Binding Name:** `AI`
- **AI Model:** `@cf/meta/llama-3.1-8b-instruct-fast`

#### Code Breakdown in `MainActivity.kt`

The networking call is implemented using a Kotlin suspend function running on `Dispatchers.IO`:

```kotlin
// Networking function to query Cloudflare Workers AI backend
suspend fun queryCloudflareAI(question: String): String = withContext(Dispatchers.IO) {
    val url = URL("https://early-enlightenment-aicopy.jbridg2.workers.dev")
    val connection = (url.openConnection() as HttpURLConnection).apply {
        requestMethod = "POST"
        setRequestProperty("Content-Type", "application/json; charset=UTF-8")
        setRequestProperty("Accept", "application/json")
        doOutput = true
        connectTimeout = 15000
        readTimeout = 15000
    }

    try {
        val jsonInput = JSONObject().apply {
            put("question", question)
        }.toString()

        connection.outputStream.use { os ->
            os.write(jsonInput.toByteArray(Charsets.UTF_8))
        }

        val responseCode = connection.responseCode
        if (responseCode in 200..299) {
            val responseString = BufferedReader(InputStreamReader(connection.inputStream, Charsets.UTF_8)).use { reader ->
                reader.readText()
            }
            val jsonResponse = JSONObject(responseString)
            if (jsonResponse.has("answer")) {
                jsonResponse.getString("answer")
            } else {
                throw Exception("Response missing 'answer' field.")
            }
        } else {
            val errorString = connection.errorStream?.let { stream ->
                BufferedReader(InputStreamReader(stream, Charsets.UTF_8)).use { it.readText() }
            } ?: "HTTP error code: $responseCode"
            throw Exception(errorString)
        }
    } finally {
        connection.disconnect()
    }
}
```

#### How the Data Flows:
1. The user types a question in `AiTutorScreen` and clicks **Send**.
2. `queryCloudflareAI(prompt)` opens an HTTPS `POST` connection.
3. The app serializes a JSON payload: `{"question": "What is 2 + 2?"}`.
4. The Cloudflare Worker receives the request, calls `env.AI.run('@cf/meta/llama-3.1-8b-instruct-fast', { prompt: question })`, and returns a JSON payload `{"answer": "..."}`.
5. The Android app parses `jsonResponse.getString("answer")` and displays it in the Jetpack Compose chat view.

### Step 12 — Check Internet Permission

Open `app/src/main/AndroidManifest.xml` and verify:
```xml
<uses-permission android:name="android.permission.INTERNET" />
```

### Step 13 — Cloudflare Account & API Key Requirements
- **Do Teammates Need Cloudflare Accounts?** **No.**
- **Are Client API Keys Required?** **No.** Model execution and authentication are managed on the server side by the Cloudflare Worker via the `AI` binding. Teammates do not need individual API keys or accounts.

### Step 14 — Test the Hosted Worker in Browser
1. Open your web browser.
2. Visit: `https://early-enlightenment-aicopy.jbridg2.workers.dev`
3. Confirm status output:
   ```json
   {"status":"Early Enlightenment AI is online"}
   ```

### Step 15 — Send a Test AI Question (Command Line)
- **PowerShell (Windows):**
  ```powershell
  $url = "https://early-enlightenment-aicopy.jbridg2.workers.dev"
  $body = @{ question = "What is 2 + 2?" } | ConvertTo-Json
  Invoke-RestMethod -Uri $url -Method Post -ContentType "application/json" -Body $body
  ```
- **cURL (macOS/Linux):**
  ```bash
  curl -X POST https://early-enlightenment-aicopy.jbridg2.workers.dev \
       -H "Content-Type: application/json" \
       -d '{"question": "What is 2 + 2?"}'
  ```

### Step 16 — Test AI Tutor in the Android App
1. Open the app on the emulator.
2. Click **Start Learning** ➔ Select **AI Tutor** tab.
3. Type: `What is 2 + 2?`
4. Capture **Screenshot 1** (question typed).
5. Click **Send** and wait for the response.
6. Capture **Screenshot 2** (AI response displayed).

### Step 17 — Troubleshooting Guide

#### A. Gradle Sync Fails
- **Fix:** Verify Embedded JDK / JDK 25 selection in **Settings → Build Tools → Gradle**. Ensure Java 17 compatibility remains set in `app/build.gradle.kts`. Do not change plugin version numbers.

#### B. Windows File Locking (`AccessDeniedException`)
- **Fix:** Ensure `app/build.gradle.kts` uses `System.getProperty("java.io.tmpdir")`. Pause OneDrive sync if needed.

#### C. AI Tutor Network Error
- **Fix:** Verify emulator Wi-Fi/internet access and test the Worker URL in a browser.

### Step 18 — How to Save Work & Submit a Pull Request

Follow these exact steps when contributing features or documentation:

1. **Pull Latest Team Changes:**
   ```bash
   git pull origin main
   ```
2. **Create Your Feature Branch:**
   ```bash
   git checkout -b feature/your-feature-name
   ```
3. **Review Your Local Changes:**
   ```bash
   git status
   ```
4. **Stage Your Modified Files:**
   ```bash
   git add README.md app/build.gradle.kts .gitignore build.gradle.kts gradle/libs.versions.toml
   ```
5. **Commit Your Changes:**
   ```bash
   git commit -m "Descriptive commit message"
   ```
6. **Push Your Branch to GitHub:**
   ```bash
   git push -u origin feature/your-feature-name
   ```
7. **Open a Pull Request:**
   Go to the [GitHub Repository](https://github.com/aedwardsxula/Early_Enlightenment), click **Compare & pull request**, and request a team review before merging into `main`.

### Step 19 — Final Team Verification Checklist
- [ ] Android Studio & Git installed
- [ ] Repository cloned from `https://github.com/aedwardsxula/Early_Enlightenment.git`
- [ ] Gradle Sync completed (Gradle Daemon Java 25 / Source Java 17)
- [ ] Android SDK 37 installed
- [ ] Emulator launched
- [ ] Project builds & launches `MainActivity`
- [ ] AI Tutor answers test question `What is 2 + 2?`

---

## Cloudflare Workers AI Architecture & Documentation

Official Reference Documentation:
- 🔗 **Cloudflare Workers AI Get Started:** https://developers.cloudflare.com/workers-ai/get-started/
- 🔗 **Cloudflare Workers AI Bindings:** https://developers.cloudflare.com/workers-ai/configuration/bindings/
- 🔗 **Android Studio Manual:** https://developer.android.com/studio
- 🔗 **Gradle User Guide:** https://docs.gradle.org/
- 🔗 **Git Manual:** https://git-scm.com/doc

---

## Team Members

| Name | Email | GitHub Username | Role |
|:---:|:---:|:---:|:---:|
| Peyton Lucas | plucas@xula.edu | Pearlzz2 | Driver |
| Taylor McDile | tmcdile@xula.edu | tayjmcdile-alt | Tester |
| Jaydan Bridges | jbridg@xula.edu | Jbridg2 | Navigator |

---

## Project Tasks

### 1. Peyton - Home Screen

**1(a).** Create the Home Screen with a "Begin your check-in" button.  When the button is clicked, display the "What's been on your mind?" form and prepare a reflection of what the user typed in the form.  Use [this API](https://api-ninjas.com) to prepare the reflection by generating 5 random quotes, determining the top 5 most common words in those quotes, display those keyword as pills, let the user select any 3 of those keywords, and then display the quote that includes the most of those keywords.

**1(b).** Add at least one significant feature based on the Affirmation or the Steps Screen.

### 2. Taylor - Affirmation Screen

**2(a).** Create an Affirmation Screen with selectable keywords to avoid and a "Provide Support" button.  When the button is clicked, use [this API](https://api-ninjas.com) to generate 5 random jokes, select the joke that has the fewest of the user selected keywords, display that joke and also a picture of either a cat or a dog.  The picture is determined by the selected joke.  If the joke has any of the keywords, display a cat but if the joke has none of the keywords, display a dog.

**2(b).** Add at least one significant feature based on the Home or the Steps Screen.

### 3. Jaydan - Steps Screen

**3(a).** Create a Steps Screen with a "Best Small Steps" button.  When the button is clicked, display a list of self-care solutions to improve the user's mood based on their current affirmation.  Because your team decided to use an AI API, use an endpoint that creates this list.

**3(b).** Add at least one significant feature based on the Affirmation or the Home Screen.

---

## Contributors

| Who | Date | What |
|:---:|:---:|:---:|
