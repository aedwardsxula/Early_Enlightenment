<div align="center">

# Early_Enlightenment

**Mental Wellness Check-In Application**

A project developed by Jayden Brdiges, Peyton Lucas, and Taylor McDile.

</div>

---

## Table of Contents

-[Project Description](#project-description)
-[Instructions To Get Started](instructions-to-get-started)
-[Where To Get Help](where-to-get-help)
-[Team Members](#team-members)
-[Project Tasks](#project-tasks)
-[Contrbutors](#contributors)

---

## Project Description

Early Enlightenment is a mental wellness check-in application designed to help users reflect on their thoughts, receive personalized affirmations, and discover small self-care activities. 

The application uses AI API integration to generate reflections, supportive affirmations, and personalized self-care suggestions based on user input. 


---

## Where To Get Help


---

## Team Members

| Name | Email | GitHub Username |
|:---:|:---:|:---:|
| Peyton Lucas | plucas@xula.edu | Pearlzz2 |
| Taylor McDile | tmcdile@xula.edu | tayjmcdile-alt |
| Jaydan Bridges | jbridg@xula.edu | Jbridg2 |

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

## Contributors

| Who | Date | What |
|:---:|:---:|:---:|

---

## Instructions To Get Started

### 1. Download the Project
1. Open the Early_Enlightenment GitHub repository.
2. Click the green Code button.
3. Copy the repository URL.
4. Open Android Studio.
5. Select Get from VCS or Clone Repository.
6. Paste the GitHub URL and clone the project.
7. Allow Gradle to sync.
8. Select an Android emulator and run the app.

### 2. AI Tutor Setup

The Early Enlightenment AI Tutor uses Cloudflare Workers AI.

The AI service is already deployed at:

https://early-enlightenment-aicopy.jbridg2.workers.dev

The app sends student questions to this endpoint using an HTTP POST request.

The Worker processes the question using Cloudflare Workers AI and returns a generated response.

The Android app displays the response in the AI Tutor screen.

### 3. How the AI Connection Works

1. The student opens the AI Tutor screen.
2. The student types a question.
3. The Android app sends a POST request to the Cloudflare Worker.
4. The request contains JSON in this format:

   {"question": "What is 2 + 2?"}

5. The Cloudflare Worker uses its AI binding to process the question.
6. The Worker returns JSON containing the AI response.
7. The Android app displays the answer.

### 4. Cloudflare AI Configuration

Cloudflare Worker name:
early-enlightenment-aicopy

Worker URL:
https://early-enlightenment-aicopy.jbridg2.workers.dev

AI Binding:
AI

AI Model:
@cf/meta/llama-3.1-8b-instruct-fast

The existing deployed Worker handles AI requests. Teammates do not need to create their own Cloudflare account just to run the application while this endpoint remains available.

If deploying a separate Worker, configure a Workers AI binding named AI, deploy compatible Worker code, and update the endpoint URL in the Android project.

### 5. Testing the AI Tutor

1. Run Early Enlightenment in Android Studio.
2. Open the AI Tutor tab.
3. Enter: What is 2 + 2?
4. Submit the question.
5. Verify that the AI generates a response.

To check whether the hosted Worker is online, open:

https://early-enlightenment-aicopy.jbridg2.workers.dev

The browser should display:

{"status":"Early Enlightenment AI is online"}

Note: The browser test checks the service status. Actual AI responses require a POST request.

### 6. Troubleshooting

If the AI Tutor does not respond:

1. Check that the emulator has internet access.
2. Confirm that the Cloudflare Worker URL is correct.
3. Confirm that the Android app is sending a POST request with JSON.
4. Check Android Studio Logcat for network errors.
5. Confirm that the deployed Cloudflare Worker is online.

### 7. Current Development Status

Completed:
- Android application design and styling.
- Home screen and bottom navigation.
- Educational Topics screen.
- AI Tutor connection to Cloudflare Workers AI.
- Successful AI response testing.

Still in development:
- Full interactive lesson content.
- Additional application testing.
- Production security, authentication, and rate limiting.


