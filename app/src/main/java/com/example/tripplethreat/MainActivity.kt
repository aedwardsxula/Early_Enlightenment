package com.example.tripplethreat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.tripplethreat.ui.theme.TrippleThreatTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            TrippleThreatTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppRoot()
                }
            }
        }
    }
}

enum class AppScreen {
    WELCOME,
    DASHBOARD
}

enum class DashboardTab(val title: String, val icon: ImageVector) {
    HOME("Home", Icons.Default.Home),
    STEPS("Steps", Icons.Default.Checklist),
    LESSONS("Lessons", Icons.AutoMirrored.Filled.MenuBook),
    AI_TUTOR("AI Tutor", Icons.Default.SmartToy)
}

@Composable
fun AppRoot() {
    var currentScreen by remember { mutableStateOf(AppScreen.WELCOME) }

    when (currentScreen) {
        AppScreen.WELCOME -> {
            HomeScreen(onStartLearning = {
                currentScreen = AppScreen.DASHBOARD
            })
        }
        AppScreen.DASHBOARD -> {
            MainDashboardScreen(onNavigateHome = {
                currentScreen = AppScreen.WELCOME
            })
        }
    }
}

@Composable
fun HomeScreen(onStartLearning: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Early Enlightenment",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Your AI Learning Companion",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.secondary
        )

        Spacer(modifier = Modifier.height(32.dp))

        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = "Welcome to Triple Threat!",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Explore new topics, build your knowledge, and learn with AI.",
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onStartLearning,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Start Learning")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainDashboardScreen(onNavigateHome: () -> Unit) {
    var selectedTab by remember { mutableStateOf(DashboardTab.HOME) }
    var currentAffirmation by remember { 
        mutableStateOf("I am capable, resilient, and taking positive steps toward my wellness.") 
    }
    var activeSelfCareGoal by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(selectedTab.title) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ) {
                DashboardTab.entries.forEach { tab ->
                    NavigationBarItem(
                        icon = { Icon(tab.icon, contentDescription = tab.title) },
                        label = { Text(tab.title) },
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                DashboardTab.HOME -> HomeDashboardTab(
                    onNavigateHome = onNavigateHome,
                    activeSelfCareGoal = activeSelfCareGoal
                )
                DashboardTab.STEPS -> StepsScreen(
                    currentAffirmation = currentAffirmation,
                    onAffirmationChange = { currentAffirmation = it },
                    activeGoal = activeSelfCareGoal,
                    onSelectGoal = { activeSelfCareGoal = it }
                )
                DashboardTab.LESSONS -> LessonsScreen()
                DashboardTab.AI_TUTOR -> AiTutorScreen()
            }
        }
    }
}

@Composable
fun HomeDashboardTab(
    onNavigateHome: () -> Unit,
    activeSelfCareGoal: String? = null
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = "Welcome to Triple Threat!",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Explore new topics, build your knowledge, and learn with AI.",
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        activeSelfCareGoal?.let { goal ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Today's Active Self-Care Goal",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = goal,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }

        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Your Learning Progress",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { 0.35f },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "35% Completed - Keep up the great work!",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        OutlinedButton(
            onClick = onNavigateHome,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back to Welcome View")
        }
    }
}

// Helper function to clean Markdown formatting, filter intro/outro lines, and combine Title + Description per card
fun parseAndCleanSelfCareSteps(rawResponse: String): List<String> {
    val lines = rawResponse.lines().map { it.trim() }.filter { it.isNotBlank() }
    val candidates = mutableListOf<String>()

    for (line in lines) {
        // Filter out introductory sentences or closing questions
        if (line.startsWith("I'm glad", ignoreCase = true) ||
            line.startsWith("I'm happy", ignoreCase = true) ||
            line.startsWith("I'd be happy", ignoreCase = true) ||
            line.startsWith("Sure", ignoreCase = true) ||
            line.startsWith("Here are", ignoreCase = true) ||
            line.startsWith("Here's", ignoreCase = true) ||
            line.startsWith("Based on", ignoreCase = true) ||
            line.startsWith("Do you have", ignoreCase = true) ||
            line.endsWith("?")
        ) {
            continue
        }

        // Clean Markdown bold **, list markers (1., -, *, •), and leading quotes
        val cleaned = line
            .replace(Regex("\\*\\*"), "")
            .replace(Regex("^[0-9]+\\.\\s*"), "")
            .replace(Regex("^[-*•]\\s*"), "")
            .replace(Regex("^\"|\"$"), "")
            .trim()

        if (cleaned.length > 5) {
            candidates.add(cleaned)
        }
    }

    // Pair short title headers with their descriptions if split across lines
    val recommendations = mutableListOf<String>()
    var i = 0
    while (i < candidates.size) {
        val current = candidates[i]
        if (i + 1 < candidates.size && (current.startsWith("Step", ignoreCase = true) || current.length < 45) && !current.contains(".")) {
            recommendations.add("$current: ${candidates[i + 1]}")
            i += 2
        } else {
            recommendations.add(current)
            i += 1
        }
    }

    return if (recommendations.isNotEmpty()) {
        recommendations.take(4)
    } else {
        listOf(rawResponse.replace(Regex("\\*\\*"), "").trim())
    }
}

@Composable
fun StepsScreen(
    currentAffirmation: String,
    onAffirmationChange: (String) -> Unit,
    activeGoal: String?,
    onSelectGoal: (String) -> Unit
) {
    var isLoading by remember { mutableStateOf(false) }
    var stepsList by remember { mutableStateOf<List<String>>(emptyList()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Best Small Steps",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Discover personalized self-care solutions based on your current affirmation.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.secondary
        )

        // Current Affirmation Input / Context Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Your Current Affirmation",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = currentAffirmation,
                    onValueChange = onAffirmationChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Affirmation Context") },
                    enabled = !isLoading
                )
            }
        }

        // Action Button: "Best Small Steps"
        Button(
            onClick = {
                val affirmationText = currentAffirmation.trim()
                if (affirmationText.isNotBlank() && !isLoading) {
                    isLoading = true
                    errorMessage = null
                    coroutineScope.launch {
                        try {
                            val prompt = "Based on this mood affirmation: '$affirmationText', generate 4 small, realistic, and practical self-care steps to improve mood. List each step clearly."
                            val response = queryCloudflareAI(prompt)
                            stepsList = parseAndCleanSelfCareSteps(response)
                        } catch (e: Exception) {
                            errorMessage = "Error fetching self-care steps: ${e.localizedMessage ?: "Please check your network."}"
                        } finally {
                            isLoading = false
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading && currentAffirmation.isNotBlank()
        ) {
            Text("Best Small Steps")
        }

        if (isLoading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        errorMessage?.let { err ->
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = err,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        // Display AI-Generated Self-Care Suggestions List
        if (stepsList.isNotEmpty()) {
            Text(
                text = "Recommended Self-Care Solutions:",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(stepsList) { step ->
                    val isSelected = activeGoal == step
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer
                            else MaterialTheme.colorScheme.surfaceVariant
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = step,
                                style = MaterialTheme.typography.bodyLarge,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
                                else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            TextButton(
                                onClick = { onSelectGoal(step) }
                            ) {
                                Text(if (isSelected) "✓ Active Goal Set (Home Screen)" else "+ Set as Active Goal for Home")
                            }
                        }
                    }
                }
            }
        }
    }
}

data class LessonTopic(val title: String, val category: String, val description: String)

@Composable
fun LessonsScreen() {
    val topics = remember {
        listOf(
            LessonTopic("Mathematics & Logic", "STEM", "Algebra, Geometry, and analytical problem solving."),
            LessonTopic("Physical Sciences", "STEM", "Physics, Chemistry, and scientific inquiry fundamentals."),
            LessonTopic("Literature & Composition", "Humanities", "Reading comprehension, essay structure, and critical analysis."),
            LessonTopic("World History", "Humanities", "Key historical eras, civilizations, and global events."),
            LessonTopic("Computer Science & AI", "Technology", "Coding fundamentals, algorithms, and artificial intelligence basics.")
        )
    }

    var selectedTopic by remember { mutableStateOf<LessonTopic?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Educational Topics",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Select a topic below to view lessons and start studying.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.secondary
        )
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(topics) { topic ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedTopic = topic }
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = topic.title,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Badge {
                                Text(topic.category)
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = topic.description,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }

    selectedTopic?.let { topic ->
        AlertDialog(
            onDismissRequest = { selectedTopic = null },
            title = { Text(topic.title) },
            text = {
                Column {
                    Text(text = "Category: ${topic.category}", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Detailed lessons for ${topic.title} will be loaded here. Ready to start studying?")
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedTopic = null }) {
                    Text("Start Lesson")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedTopic = null }) {
                    Text("Close")
                }
            }
        )
    }
}

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

@Composable
fun AiTutorScreen() {
    var promptText by remember { mutableStateOf("") }
    val chatMessages = remember { mutableStateListOf<Pair<String, String>>() }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        errorMessage?.let { err ->
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = err,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(12.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            if (chatMessages.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "Ask your AI Tutor anything about your studies!",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(chatMessages) { (sender, text) ->
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (sender == "You") MaterialTheme.colorScheme.primaryContainer
                                else MaterialTheme.colorScheme.surfaceVariant
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = sender,
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = if (sender == "You") MaterialTheme.colorScheme.onPrimaryContainer
                                    else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = text,
                                    color = if (sender == "You") MaterialTheme.colorScheme.onPrimaryContainer
                                    else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        if (isLoading) {
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = promptText,
                onValueChange = { promptText = it },
                placeholder = { Text("Ask your AI tutor a question...") },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                enabled = !isLoading
            )

            Button(
                onClick = {
                    val prompt = promptText.trim()
                    if (prompt.isNotBlank() && !isLoading) {
                        promptText = ""
                        chatMessages.add("You" to prompt)
                        isLoading = true
                        errorMessage = null

                        coroutineScope.launch {
                            try {
                                val aiReply = queryCloudflareAI(prompt)
                                chatMessages.add("AI Tutor" to aiReply)
                            } catch (e: Exception) {
                                errorMessage = "Network Error: ${e.localizedMessage ?: "Failed to reach AI worker."}"
                                chatMessages.add("System" to "Failed to generate AI response. Please check your connection.")
                            } finally {
                                isLoading = false
                            }
                        }
                    }
                },
                enabled = !isLoading && promptText.isNotBlank()
            ) {
                Text("Send")
            }
        }
    }
}
