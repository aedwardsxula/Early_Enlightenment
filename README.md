# Early_Enlightenment

**Mental Wellness Check-In Application**

A project developed by Jaydan Bridges, Peyton Lucas, and Taylor McDile.

---

## Table of Contents

- [Project Description](#project-description)
- [Instructions to Get Started](#instructions-to-get-started)
- [Cloudflare Workers AI Setup](#cloudflare-workers-ai-setup)
- [Application Features](#application-features)
- [Testing the Application](#testing-the-application)
- [Application Screenshots](#application-screenshots)
- [Troubleshooting](#troubleshooting)
- [Team Members](#team-members)
- [Project Tasks](#project-tasks)
- [Current Development Status](#current-development-status)
- [Contributors](#contributors)

---

## Project Description

Early Enlightenment is a mental wellness check-in application that helps users reflect on their thoughts, receive affirmations, and find small activities that can improve their mood.

Our goal is to create an application that gives users a simple way to check in with themselves and receive helpful suggestions based on how they are feeling.

The project is being developed in Android Studio using Kotlin and Jetpack Compose. We are also using Cloudflare Workers AI to generate responses and personalized self-care recommendations.

Each team member is responsible for a different part of the application. The three main parts of the project are the Home Screen, Affirmation Screen, and Steps Screen.

---

## Instructions to Get Started

These instructions explain how to download the project, open it in Android Studio, and run it on an emulator.

### Step 1: Install Android Studio

1. Go to https://developer.android.com/studio.
2. Download Android Studio for your computer.
3. Run the installer and follow the setup instructions.
4. Open Android Studio once the installation is finished.
5. Complete the setup wizard and allow Android Studio to install the required components.

### Step 2: Install Git

Git is used to download the project from GitHub and keep track of changes.

1. Go to https://git-scm.com/downloads.
2. Download and install Git.
3. Open Command Prompt, PowerShell, or a terminal.
4. Enter the following command:

```bash
git --version
```

If Git is installed correctly, the terminal should display a version number.

### Step 3: Download the Project

Our GitHub repository is located at:

https://github.com/aedwardsxula/Early_Enlightenment

To download the project:

1. Open Android Studio.
2. Select **Clone Repository** or **Get from VCS**.
3. Choose Git.
4. Copy and paste the repository URL:

```text
https://github.com/aedwardsxula/Early_Enlightenment.git
```

5. Choose where you want to save the project.
6. Click **Clone**.
7. Wait for the project to download.
8. Open the project in Android Studio.

If you are working on Windows, I recommend saving the project in a regular folder such as `C:\AndroidProjects` instead of OneDrive. This can help prevent file-locking problems during builds.

Make sure you open the main project folder containing `settings.gradle.kts` and the `app` folder.

If you are reviewing work that has not been merged into `main`, switch to the appropriate feature branch in GitHub or Android Studio.

### Step 4: Check the Project Requirements

The project uses the following configuration:

| Component | Version |
|---|---|
| Gradle Wrapper | 9.6.0 |
| Android Gradle Plugin | 9.4.0 |
| Kotlin | 2.2.10 |
| Java Source Compatibility | 17 |
| Gradle Daemon JDK | 25 |
| Compile SDK | 37 |
| Target SDK | 37 |
| Minimum SDK | 26 |

The Gradle daemon uses Java 25, while the application source code is configured for Java 17 compatibility.

These are two different settings. Java 25 is used to run the Gradle build process, and Java 17 is the compatibility level used when compiling the application's Java code.

The daemon version is configured in:

```text
gradle/gradle-daemon-jvm.properties
```

The Java source compatibility is configured in:

```text
app/build.gradle.kts
```

### Step 5: Configure Gradle

1. Open Android Studio.
2. Go to **File > Settings**.
3. Select **Build, Execution, Deployment > Build Tools > Gradle**.
4. Check the Gradle JDK setting.
5. Make sure the project can access the required Java 25 daemon JDK. The project's Gradle configuration may download the required JDK automatically.
6. Click **Apply**, then **OK**.
7. Select **File > Sync Project with Gradle Files**.
8. Wait for Gradle Sync to finish.

Do not change the Gradle or Kotlin versions unless the team has agreed to update them.

### Step 6: Check the Android SDK

1. Open **Tools > SDK Manager**.
2. Select the **SDK Platforms** tab.
3. Install the SDK platform required by the project, which currently uses API level 37.
4. Open the **SDK Tools** tab.
5. Make sure the following tools are installed:
   - Android SDK Build-Tools
   - Android SDK Platform-Tools
   - Android SDK Command-line Tools
   - Android Emulator
6. Click **Apply** and allow the installations to finish.

### Step 7: Create an Android Emulator

1. Open **Tools > Device Manager**.
2. Click **Create Device**.
3. Select a Pixel phone or another compatible Android device.
4. Click **Next**.
5. Select a supported Android system image with an API level that meets the project's minimum SDK requirement.
6. Download the system image if needed.
7. Finish creating the device.
8. Start the emulator using the Play button in Device Manager.

### Step 8: Build and Run the Application

1. Make sure Gradle Sync has finished.
2. Select the `app` run configuration in Android Studio.
3. Choose the emulator you created.
4. Click **Build > Make Project**.
5. Wait for the build to finish.
6. Click **Run** to open the application.

You can also test the build using the Gradle task:

```bash
./gradlew assembleDebug
```

On Windows, use:

```powershell
.\gradlew.bat assembleDebug
```

If the build completes successfully, Android Studio should be able to install and launch the app on your emulator.

---

## Cloudflare Workers AI Setup

We decided to use Cloudflare Workers AI to handle AI-generated responses in our application.

The Android application sends information to a Cloudflare Worker, and the Worker sends the information to an AI model. The generated response is then returned to the Android application.

### Using Our Existing Cloudflare Worker

Our existing Worker is hosted at:

https://early-enlightenment-aicopy.jbridg2.workers.dev

The current configuration uses:

| Setting | Value |
|---|---|
| Worker Name | early-enlightenment-aicopy |
| AI Binding | AI |
| AI Model | @cf/meta/llama-3.1-8b-instruct-fast |
| Request Method | POST |
| Request Format | JSON |

Team members do not need their own Cloudflare accounts to run the Android application using this existing endpoint, as long as the Worker remains available.

The Android application is already configured to communicate with this Worker.

### How the Connection Works

When a user submits a question or requests self-care recommendations, the Android application sends a POST request to the Cloudflare Worker.

For example, the request may contain:

```json
{
  "question": "What is 2 + 2?"
}
```

The Cloudflare Worker processes the request using Workers AI.

It then returns a response in this format:

```json
{
  "answer": "The answer is 4."
}
```

The text above is an example of the response format. The actual AI response may be worded differently.

The Android application reads the `answer` field and displays the result.

### Where the Cloudflare Connection Is Located

The Android networking code is located in:

```text
app/src/main/java/com/example/tripplethreat/MainActivity.kt
```

The function responsible for communicating with Cloudflare is called:

```kotlin
queryCloudflareAI(question: String)
```

This function sends the user's information to the Worker and returns the AI-generated response.

### Creating Your Own Cloudflare Worker

You do not need to create another Worker to run our application. However, these instructions explain how to set up a separate Worker if you want to test the backend independently.

**Step 1: Create a Cloudflare account**

1. Go to https://dash.cloudflare.com.
2. Sign up or log in.
3. Complete the account setup.

**Step 2: Locate the Worker files**

In the Early Enlightenment project, open the folder:

```text
cloudflare-worker/
```

This folder contains:

| File | Purpose |
|---|---|
| index.js | Contains the JavaScript Worker code |
| wrangler.toml | Contains the Worker configuration |
| README.md | Provides additional Worker setup instructions |

The included JavaScript is a compatible reconstruction of the Worker implementation. It has not been confirmed to be identical to the code currently deployed on Cloudflare.

**Step 3: Install Node.js**

1. Go to https://nodejs.org.
2. Download and install Node.js.
3. Open a terminal.
4. Run:

```bash
node --version
npm --version
```

Both commands should display version numbers.

**Step 4: Open the Worker folder in a terminal**

From the main Early Enlightenment project folder, enter:

```bash
cd cloudflare-worker
```

Make sure the terminal is inside this folder before running the deployment commands.

**Step 5: Choose a different Worker name**

Open `wrangler.toml`.

Change the Worker name to something unique. For example:

```toml
name = "early-enlightenment-yourname"
main = "index.js"
compatibility_date = "2026-11-01"

[ai]
binding = "AI"
```

Do not use `early-enlightenment-aicopy` for your own deployment because that is the name of our existing Worker.

**Step 6: Log in to Cloudflare**

Run:

```bash
npx wrangler login
```

A browser window should open so you can authorize Wrangler to access your Cloudflare account.

**Step 7: Deploy the Worker**

Run:

```bash
npx wrangler deploy
```

Wrangler will use the `wrangler.toml` configuration and the JavaScript Worker code.

If deployment succeeds, Cloudflare will provide a URL for your Worker.

**Step 8: Connect the new Worker to Android Studio**

Open:

```text
app/src/main/java/com/example/tripplethreat/MainActivity.kt
```

Find the URL used by `queryCloudflareAI`.

Replace the existing Worker URL with your new Worker URL.

For example:

```kotlin
val url = URL("https://early-enlightenment-yourname.workers.dev")
```

Save the file and rebuild the application.

**Step 9: Test the connection**

Open your Worker URL in a browser to check whether it responds.

You can also send a POST request to confirm that the AI model is generating a response.

For additional information, see the [Cloudflare Worker Setup Guide](cloudflare-worker/README.md).

---

## Application Features

### Home Screen

The Home Screen is the starting point of the mental wellness application.

The planned check-in feature will allow users to share what has been on their minds and receive a response based on their input.

The Home Screen also contains a section called **Today's Active Self-Care Goal**, which displays a goal selected from the Steps Screen.

### Affirmation Screen

The Affirmation Screen is intended to provide support based on the user's selected keywords.

This feature is part of Taylor's assigned work and will be integrated with the rest of the application as development continues.

### Steps Screen

The Steps Screen is my assigned portion of the project.

I created the **Best Small Steps** feature to give users self-care recommendations based on their current affirmation.

The screen includes an affirmation input field and a button that requests suggestions from Cloudflare Workers AI.

Once the response is received, the recommendations appear as separate cards. Each card contains the recommended activity and an explanation of how to complete it.

The application also removes unnecessary AI formatting so that the recommendations are easier to read.

### Set as Active Goal for Home

I added a feature that connects the Steps Screen to the Home Screen.

Users can select **Set as Active Goal for Home** on one of the recommended activities.

After selecting a goal, they can return to the Home Screen and see that recommendation displayed under **Today's Active Self-Care Goal**.

This allows users to choose one activity they want to focus on instead of only reading a list of suggestions.

### Task 6 — Main Journaling Screen with a Text Box

I implemented Task 6 from the project backlog: **"Create the main journaling screen with a text box."**

- Created a dedicated **Journal** tab (`JournalScreen`) featuring a multi-line text box for writing daily mental wellness reflections and notes.
- Added character counter (`103 characters`), optional entry title input, and automatic date/timestamping.
- Added a **Save Entry** action that stores journal entries in a historical log view (`LazyColumn`).
- Added an **AI Reflection** action that sends journal entries to Cloudflare Workers AI for a supportive 2-sentence summary reflection.

### AI Tutor

The application also includes an AI Tutor screen that connects to the Cloudflare Worker.

Users can enter a question and receive an AI-generated response.

This feature was used to test the Cloudflare connection during development.

---

## Testing the Application

We tested the Android application using an Android emulator.

### Testing the AI Tutor

1. Open Early Enlightenment.
2. Select **Start Learning**.
3. Open the **AI Tutor** tab.
4. Enter a question such as `What is 2 + 2?`.
5. Click **Send**.
6. Confirm that an AI-generated response appears.

The Cloudflare connection was tested using this process.

### Testing the Steps Screen

1. Open the **Steps** tab.
2. Enter or review the current affirmation.
3. Click **Best Small Steps**.
4. Wait for the recommendations to appear.
5. Check that each recommendation is displayed in a separate card.
6. Select **Set as Active Goal for Home**.
7. Return to the Home Screen.
8. Confirm that the selected goal appears.

Both the Steps Screen and its connection to the Home Screen were tested on the emulator.

### Testing the Journaling Screen (Task 6)

1. Open the **Journal** tab.
2. Enter a journal reflection into the multi-line text box (e.g. `Today I am focusing on mindfulness...`).
3. Click **AI Reflection** and confirm Cloudflare Workers AI returns a supportive summary.
4. Click **Save Entry**.
5. Confirm the entry appears in the **Previous Journal Entries** list with timestamp and AI reflection.

### Testing the Cloudflare Endpoint

To check whether the existing Worker is online, open:

https://early-enlightenment-aicopy.jbridg2.workers.dev

The expected response is:

```json
{
  "status": "Early Enlightenment AI is online"
}
```

This checks the Worker status, but it does not test AI generation.

To send an AI request from Windows PowerShell, use:

```powershell
$url = "https://early-enlightenment-aicopy.jbridg2.workers.dev"
$body = @{ question = "What is 2 + 2?" } | ConvertTo-Json

Invoke-RestMethod -Uri $url -Method Post -ContentType "application/json" -Body $body
```

If the request is successful, the response should include an `answer` field.

---

## Application Screenshots

The following screenshots were captured while testing the AI Tutor on the Android emulator.

**Screenshot 1: Question Entered**

[View Screenshot 1](screenshots/screenshot1_ai_question.png)

**Screenshot 2: AI Response**

[View Screenshot 2](screenshots/screenshot2_ai_response.png)

These screenshots show the AI Tutor before and after submitting a test question.

---

## Troubleshooting

### Gradle Sync Problems

If Gradle Sync fails, check the project's Java and Gradle configuration.

The project uses Java 25 for the Gradle daemon and Java 17 source compatibility.

Make sure the required JDK is available and that Android Studio has downloaded the necessary Gradle files.

### Android Emulator Problems

If the emulator does not start:

1. Open Device Manager.
2. Confirm that a virtual device has been created.
3. Check that its system image is installed.
4. Restart the emulator if necessary.

### Cloudflare Connection Problems

If the AI Tutor or Steps Screen does not generate a response:

1. Check the emulator's internet connection.
2. Confirm that the Cloudflare Worker URL is correct.
3. Open the Worker URL in a browser to check its status.
4. Make sure the application is sending a POST request with JSON.
5. Check Android Studio Logcat for error messages.

### Windows File Problems

If the project encounters file-locking errors while building, try moving the project to a local folder outside OneDrive.

After moving the project, reopen it in Android Studio and sync Gradle again.

---

## Team Members

| Name | Email | GitHub Username | Role |
|---|---|---|---|
| Peyton Lucas | plucas@xula.edu | Pearlzz2 | Driver |
| Taylor McDile | tmcdile@xula.edu | tayjmcdile-alt | Tester |
| Jaydan Bridges | jbridg@xula.edu | Jbridg2 | Navigator |

---

## Project Tasks

### 1. Peyton - Home Screen

**1(a).** Create the Home Screen with a "Begin your check-in" button. When the button is clicked, display the "What's been on your mind?" form and prepare a reflection of what the user typed in the form. Use [this API](https://api-ninjas.com) to prepare the reflection by generating 5 random quotes, determining the top 5 most common words in those quotes, display those keyword as pills, let the user select any 3 of those keywords, and then display the quote that includes the most of those keywords.

**1(b).** Add at least one significant feature based on the Affirmation or the Steps Screen.

### 2. Taylor - Affirmation Screen

**2(a).** Create an Affirmation Screen with selectable keywords to avoid and a "Provide Support" button. When the button is clicked, use [this API](https://api-ninjas.com) to generate 5 random jokes, select the joke that has the fewest of the user selected keywords, display that joke and also a picture of either a cat or a dog. The picture is determined by the selected joke. If the joke has any of the keywords, display a cat but if the joke has none of the keywords, display a dog.

**2(b).** Add at least one significant feature based on the Home or the Steps Screen.

### 3. Jaydan - Steps Screen

**3(a).** Create a Steps Screen with a "Best Small Steps" button. When the button is clicked, display a list of self-care solutions to improve the user's mood based on their current affirmation. Because your team decided to use an AI API, use an endpoint that creates this list.

**3(b).** Add at least one significant feature based on the Affirmation or the Home Screen.

---

## Current Development Status

The project is still being developed, and each team member is responsible for completing their assigned section.

| Feature | Status |
|---|---|
| Android Studio project setup | Completed |
| Basic navigation and application layout | Completed |
| Cloudflare Workers AI connection | Completed |
| AI Tutor testing | Completed |
| Task 5: Welcome Screen | Completed |
| Steps Screen | Completed |
| AI-generated self-care recommendations | Completed |
| Selected goal displayed on Home | Completed |
| Task 6: Main Journaling Screen with Text Box | Completed |
| Full Home Screen check-in | Pending team integration |
| Affirmation Screen | Pending team integration |
| Final testing of all three screens together | Pending |

My Steps Screen and Task 6 Journaling Screen are working with the current application setup. Once the other assigned screens are finished, we will need to connect the completed features and test the application as a whole.

---

## Contributors

| Who | When | Contribution |
|---|---|---|
| Jaydan Bridges | Fall 2026 | Steps Screen, Task 6 Journaling Screen with text box, Cloudflare Workers AI integration, selected goal feature, and setup documentation |
| Peyton Lucas | Fall 2026 | Home Screen assignment and team development |
| Taylor McDile | Fall 2026 | Affirmation Screen assignment and team development |

Contributions will be updated as the remaining features are completed.

---

## Saving Changes to GitHub

To save your work:

1. Check which branch you are working on.
2. Review the files you changed.
3. Stage only the files you intend to submit.
4. Commit your changes.
5. Push your feature branch to GitHub.
6. Open a pull request for team review.

For example:

```bash
git status
git add README.md
git commit -m "Update project documentation"
git push origin your-feature-branch
```

Replace `your-feature-branch` with the branch you are actually using.

The team should review changes before they are merged into `main`.

---

## Additional Resources

- [Android Studio](https://developer.android.com/studio)
- [Cloudflare Workers AI Documentation](https://developers.cloudflare.com/workers-ai/)
- [Cloudflare Workers AI Bindings](https://developers.cloudflare.com/workers-ai/configuration/bindings/)
- [Gradle Documentation](https://docs.gradle.org/)
- [Git Documentation](https://git-scm.com/doc)

---

Early Enlightenment  
Team Triple Threat  
Xavier University of Louisiana
