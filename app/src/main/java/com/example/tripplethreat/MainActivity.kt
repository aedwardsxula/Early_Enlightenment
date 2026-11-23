package com.example.tripplethreat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.tripplethreat.ui.theme.LightBlueBackground
import com.example.tripplethreat.ui.theme.TrippleThreatTheme
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Task: Set up the database to store user information
@Entity(tableName = "user_info")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 1,
    val userName: String = "Mindful Explorer",
    val primaryGoal: String = "Daily Wellness & Mindfulness",
    val joinDate: String = getCurrentTimestamp("MMM dd, yyyy")
)

@Dao
interface UserDao {
    @Query("SELECT * FROM user_info WHERE id = 1 LIMIT 1")
    suspend fun getUserProfile(): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: UserProfileEntity)
}

@Database(entities = [UserProfileEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}

private var baseTimeMillis: Long = System.currentTimeMillis()
private var timestampStepCounter: Long = 0L

fun getCurrentTimestamp(pattern: String = "h:mm:ss a"): String {
    val timeForStep = baseTimeMillis + (timestampStepCounter * 5 * 60 * 1000L)
    timestampStepCounter++
    return SimpleDateFormat(pattern, Locale.getDefault()).format(Date(timeForStep))
}

class MainActivity : ComponentActivity() {
    companion object {
        lateinit var database: AppDatabase
            private set
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        database = Room.databaseBuilder(
                applicationContext,
                AppDatabase::class.java,
                "early_enlightenment_db"
            ).fallbackToDestructiveMigration(false).build()

        setContent {
            TrippleThreatTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = LightBlueBackground
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
    JOURNAL("Journal", Icons.Default.EditNote),
    FEELINGS("Feelings", Icons.Default.EmojiEmotions),
    LESSONS("Lessons", Icons.AutoMirrored.Filled.MenuBook),
    AI_TUTOR("AI Tutor", Icons.Default.SmartToy)
}

enum class CheckInStep {
    IDLE,
    FORM,
    REFLECTION
}

data class FeelingOption(
    val emoji: String,
    val name: String,
    val category: String
)

data class FeelingRecord(
    val emotions: List<String>,
    val timestamp: String = getCurrentTimestamp("MMM dd, yyyy 'at' h:mm:ss a"),
    val aiGuidance: String? = null
)

val defaultFeelingsList = listOf(
    FeelingOption("😊", "Happy", "Positive"),
    FeelingOption("🌿", "Calm", "Relaxed"),
    FeelingOption("⚡", "Energetic", "Positive"),
    FeelingOption("🧘", "Peaceful", "Relaxed"),
    FeelingOption("💡", "Inspired", "Positive"),
    FeelingOption("😔", "Sad", "Challenging"),
    FeelingOption("😰", "Anxious", "Challenging"),
    FeelingOption("🥱", "Tired", "Physical"),
    FeelingOption("😖", "Stressed", "Challenging"),
    FeelingOption("😶", "Overwhelmed", "Challenging"),
    FeelingOption("🙏", "Grateful", "Positive"),
    FeelingOption("🤔", "Reflective", "Mindful")
)

data class JournalEntry(
    val title: String,
    val text: String,
    val timestamp: String = getCurrentTimestamp("MMM dd, yyyy 'at' h:mm:ss a"),
    val aiSummary: String? = null
)

data class ApiNinjaQuote(
    val quote: String,
    val author: String,
    val category: String = ""
)

data class ChatMessage(
    val sender: String,
    val text: String,
    val timestamp: String = getCurrentTimestamp("h:mm:ss a")
)

data class CheckInRecord(
    val thought: String,
    val reflection: String,
    val keywords: List<String>,
    val quote: ApiNinjaQuote?,
    val timestamp: String = getCurrentTimestamp("MMM dd, yyyy 'at' h:mm:ss a")
)

data class ActiveGoal(
    val text: String,
    val timestamp: String = getCurrentTimestamp("h:mm a")
)

data class LessonActivity(
    val topicTitle: String,
    val category: String,
    val timestamp: String = getCurrentTimestamp("h:mm a")
)

data class EncouragementRecord(
    val message: String,
    val timestamp: String = getCurrentTimestamp("h:mm:ss a"),
    val triggerSource: String = "Manual"
)

val encouragementPool = listOf(
    "You are capable of achieving amazing things!",
    "Every small step brings you closer to your wellness goals.",
    "Believe in yourself and keep pushing forward!",
    "Your dedication and mindfulness are truly inspiring.",
    "Take a deep breath; you are doing fantastic!",
    "Challenges are just stepping stones for your personal growth.",
    "Celebrate your progress today, no matter how small!",
    "You possess the inner strength to overcome any difficulty."
)

suspend fun fetchQuotesFromApiNinjas(apiKey: String): List<ApiNinjaQuote> = withContext(Dispatchers.IO) {
    val quotes = mutableListOf<ApiNinjaQuote>()
    val url = URL("https://api.api-ninjas.com/v1/quotes")

    if (apiKey.isNotBlank()) {
        repeat(5) {
            try {
                val connection = (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "GET"
                    setRequestProperty("X-Api-Key", apiKey)
                    setRequestProperty("Accept", "application/json")
                    connectTimeout = 5000
                    readTimeout = 5000
                }
                if (connection.responseCode in 200..299) {
                    val text = BufferedReader(InputStreamReader(connection.inputStream, Charsets.UTF_8)).use { it.readText() }
                    val jsonArray = JSONArray(text)
                    if (jsonArray.length() > 0) {
                        val obj = jsonArray.getJSONObject(0)
                        quotes.add(
                            ApiNinjaQuote(
                                quote = obj.optString("quote", ""),
                                author = obj.optString("author", "Unknown"),
                                category = obj.optString("category", "")
                            )
                        )
                    }
                }
                connection.disconnect()
            } catch (_: Exception) {
            }
        }
    }

    if (quotes.size < 5) {
        val fallbackPool = listOf(
            ApiNinjaQuote("Success is not final, failure is not fatal: it is the courage to continue that counts.", "Winston Churchill"),
            ApiNinjaQuote("Believe you can and you're halfway there.", "Theodore Roosevelt"),
            ApiNinjaQuote("It is during our darkest moments that we must focus to see the light.", "Aristotle"),
            ApiNinjaQuote("Success usually comes to those who are too busy to be looking for it.", "Henry David Thoreau"),
            ApiNinjaQuote("The only limit to our realization of tomorrow will be our doubts of today.", "Franklin D. Roosevelt"),
            ApiNinjaQuote("Act as if what you do makes a difference. It does.", "William James"),
            ApiNinjaQuote("What you do today can improve all your tomorrows.", "Ralph Marston")
        )
        val missing = 5 - quotes.size
        quotes.addAll(fallbackPool.shuffled().take(missing))
    }
    quotes.take(5)
}

fun extractTop5Keywords(quotes: List<ApiNinjaQuote>): List<String> {
    val stopWords = setOf(
        "the", "and", "a", "of", "to", "in", "is", "it", "you", "that", "was", "for", "on", "are",
        "with", "as", "i", "his", "they", "be", "at", "one", "have", "this", "from", "or", "had",
        "by", "not", "word", "but", "what", "some", "we", "can", "out", "other", "were", "all",
        "there", "when", "up", "use", "your", "how", "said", "an", "each", "she", "which", "do",
        "their", "time", "if", "will", "way", "about", "many", "then", "them", "write", "would",
        "like", "so", "these", "her", "long", "make", "thing", "see", "him", "two", "has", "look",
        "more", "day", "could", "go", "come", "did", "number", "sound", "no", "most", "people",
        "my", "over", "know", "water", "than", "call", "first", "who", "may", "down", "side",
        "been", "now", "find", "any", "new", "work", "part", "take", "get", "place", "made",
        "live", "where", "after", "back", "little", "only", "round", "man", "year", "came",
        "show", "every", "good", "me", "give", "our", "under", "name", "very", "through",
        "just", "form", "sentence", "great", "think", "say", "help", "low", "line", "differ"
    )

    val wordCounts = mutableMapOf<String, Int>()
    for (quoteObj in quotes) {
        val words = quoteObj.quote.lowercase()
            .replace(Regex("[^a-zA-Z0-9\\s]"), "")
            .split(Regex("\\s+"))
            .filter { it.length >= 3 && !stopWords.contains(it) }

        for (word in words) {
            wordCounts[word] = (wordCounts[word] ?: 0) + 1
        }
    }

    val sorted = wordCounts.entries.sortedByDescending { it.value }.map { it.key }
    return if (sorted.size >= 5) {
        sorted.take(5)
    } else {
        val defaultKeywords = listOf("courage", "success", "future", "moments", "doubts")
        (sorted + defaultKeywords).distinct().take(5)
    }
}

fun findBestMatchingQuote(selectedKeywords: Set<String>, quotes: List<ApiNinjaQuote>): ApiNinjaQuote {
    if (quotes.isEmpty()) {
        return ApiNinjaQuote("Believe you can and you're halfway there.", "Theodore Roosevelt")
    }
    var bestQuote = quotes.first()
    var maxMatchCount = -1

    for (quoteObj in quotes) {
        val textLower = quoteObj.quote.lowercase()
        var matches = 0
        for (kw in selectedKeywords) {
            if (textLower.contains(kw.lowercase())) {
                matches++
            }
        }
        if (matches > maxMatchCount) {
            maxMatchCount = matches
            bestQuote = quoteObj
        }
    }
    return bestQuote
}

@Composable
fun AppRoot() {
    var currentScreen by remember { mutableStateOf(AppScreen.WELCOME) }
    val checkInHistory = remember { mutableStateListOf<CheckInRecord>() }
    val journalHistory = remember { mutableStateListOf<JournalEntry>() }
    var activeSelfCareGoal by remember { mutableStateOf<ActiveGoal?>(null) }
    var currentAffirmation by remember {
        mutableStateOf("I am capable, resilient, and taking positive steps toward my wellness.")
    }
    val lessonHistory = remember { mutableStateListOf<LessonActivity>() }

    var currentEncouragement by remember {
        mutableStateOf(
            EncouragementRecord(
                message = encouragementPool.first(),
                timestamp = getCurrentTimestamp("h:mm:ss a"),
                triggerSource = "Initial"
            )
        )
    }
    var encouragementViewCount by remember { mutableIntStateOf(1) }

    fun triggerNewEncouragement(source: String = "Step Completion") {
        val nextMsg = encouragementPool.filter { it != currentEncouragement.message }.random()
        currentEncouragement = EncouragementRecord(
            message = nextMsg,
            timestamp = getCurrentTimestamp("h:mm:ss a"),
            triggerSource = source
        )
        encouragementViewCount++
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = LightBlueBackground
    ) {
        when (currentScreen) {
            AppScreen.WELCOME -> {
                HomeScreen(
                    onStartLearning = { currentScreen = AppScreen.DASHBOARD },
                    checkInHistory = checkInHistory,
                    onAddCheckIn = { record ->
                        checkInHistory.add(0, record)
                        triggerNewEncouragement("Completed Check-in Step")
                    },
                    activeSelfCareGoal = activeSelfCareGoal,
                    currentEncouragement = currentEncouragement,
                    encouragementViewCount = encouragementViewCount,
                    onGetNewEncouragement = { triggerNewEncouragement("Button Click") }
                )
            }
            AppScreen.DASHBOARD -> {
                val feelingHistory = remember { mutableStateListOf<FeelingRecord>() }
                var activeSelectedFeelings by remember { mutableStateOf<List<String>>(emptyList()) }
                MainDashboardScreen(
                    onNavigateHome = { currentScreen = AppScreen.WELCOME },
                    checkInHistory = checkInHistory,
                    journalHistory = journalHistory,
                    onAddJournalEntry = { entry ->
                        journalHistory.add(0, entry)
                        triggerNewEncouragement("Journal Entry Step")
                    },
                    feelingHistory = feelingHistory,
                    activeSelectedFeelings = activeSelectedFeelings,
                    onAddFeelingRecord = { record ->
                        feelingHistory.add(0, record)
                        activeSelectedFeelings = record.emotions
                        triggerNewEncouragement("Feelings Selection Step")
                    },
                    activeSelfCareGoal = activeSelfCareGoal,
                    onSelectGoal = { goal ->
                        activeSelfCareGoal = goal
                        triggerNewEncouragement("Set Active Goal Step")
                    },
                    currentAffirmation = currentAffirmation,
                    onAffirmationChange = { currentAffirmation = it },
                    lessonHistory = lessonHistory,
                    onCompleteLesson = { activity ->
                        lessonHistory.add(0, activity)
                        triggerNewEncouragement("Completed Lesson Step")
                    },
                    currentEncouragement = currentEncouragement,
                    encouragementViewCount = encouragementViewCount,
                    onGetNewEncouragement = { triggerNewEncouragement("Button Click") }
                )
            }
        }
    }
}

@Composable
fun HomeScreen(
    onStartLearning: () -> Unit,
    checkInHistory: List<CheckInRecord> = emptyList(),
    onAddCheckIn: (CheckInRecord) -> Unit = {},
    activeSelfCareGoal: ActiveGoal? = null,
    currentEncouragement: EncouragementRecord? = null,
    encouragementViewCount: Int = 0,
    onGetNewEncouragement: () -> Unit = {}
) {
    var checkInStep by remember { mutableStateOf(CheckInStep.IDLE) }
    var userThought by remember { mutableStateOf("") }
    var reflectionText by remember { mutableStateOf("") }
    var fetchedQuotes by remember { mutableStateOf<List<ApiNinjaQuote>>(emptyList()) }
    var extractedKeywords by remember { mutableStateOf<List<String>>(emptyList()) }
    var selectedKeywords by remember { mutableStateOf(setOf<String>()) }
    var matchingQuote by remember { mutableStateOf<ApiNinjaQuote?>(null) }
    var aiSituationQuote by remember { mutableStateOf<String?>(null) }
    var isLoadingQuotes by remember { mutableStateOf(false) }
    var isLoadingReflection by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBlueBackground)
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Early Enlightenment",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Your Mindful AI Companion",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.secondary
            )

            Spacer(modifier = Modifier.height(24.dp))

            when (checkInStep) {
                CheckInStep.IDLE -> {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Psychology,
                                contentDescription = "Mindfulness",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(56.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Welcome to Early Enlightenment!",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Take a moment for yourself today. Share what's on your mind and receive a supportive reflection with inspiring quotes.",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    currentEncouragement?.let { enc ->
                        Spacer(modifier = Modifier.height(16.dp))
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.AutoAwesome,
                                            contentDescription = "Encouragement",
                                            tint = MaterialTheme.colorScheme.onTertiaryContainer
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Daily Encouragement",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onTertiaryContainer
                                        )
                                    }
                                    Badge(
                                        containerColor = MaterialTheme.colorScheme.primary
                                    ) {
                                        Text("Viewed: $encouragementViewCount")
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "\"${enc.message}\"",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Timestamp: ${enc.timestamp} (${enc.triggerSource})",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f)
                                    )
                                    TextButton(onClick = onGetNewEncouragement) {
                                        Text("New Encouragement")
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    if (isLoadingQuotes) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Fetching quotes & keywords...", style = MaterialTheme.typography.bodyLarge)
                    } else {
                        Button(
                            onClick = {
                                isLoadingQuotes = true
                                coroutineScope.launch {
                                    val apiKey = try {
                                        File("API_KEY.txt").readText().trim()
                                    } catch (_: Exception) {
                                        ""
                                    }
                                    val quotes = fetchQuotesFromApiNinjas(apiKey)
                                    val topWords = extractTop5Keywords(quotes)
                                    fetchedQuotes = quotes
                                    extractedKeywords = topWords
                                    selectedKeywords = emptySet()
                                    isLoadingQuotes = false
                                    checkInStep = CheckInStep.FORM
                                }
                            },
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Begin your check-in",
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                    }

                    activeSelfCareGoal?.let { goal ->
                        Spacer(modifier = Modifier.height(16.dp))
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Active Goal",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                    Badge {
                                        Text(goal.timestamp)
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = goal.text,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }
                    }

                    if (checkInHistory.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(20.dp))
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "My Past Check-Ins History",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                checkInHistory.take(3).forEach { record ->
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = LightBlueBackground),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 8.dp)
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = "Check-in Done",
                                                    style = MaterialTheme.typography.labelMedium,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                                Badge {
                                                    Text(record.timestamp)
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = "\"${record.thought}\"",
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Medium
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = record.reflection,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.secondary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    OutlinedButton(
                        onClick = onStartLearning,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Open Dashboard")
                    }
                }

                CheckInStep.FORM -> {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(24.dp)) {
                            Text(
                                text = "What's been on your mind?",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "Express your thoughts freely below.",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.secondary
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            OutlinedTextField(
                                value = userThought,
                                onValueChange = { userThought = it },
                                placeholder = { Text("Type what you are feeling or thinking...") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(140.dp),
                                enabled = !isLoadingReflection
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "Select any 3 keywords from quotes (${selectedKeywords.size}/3):",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                extractedKeywords.take(3).forEach { keyword ->
                                    val isSelected = selectedKeywords.contains(keyword)
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = {
                                            selectedKeywords = if (isSelected) {
                                                selectedKeywords - keyword
                                            } else {
                                                if (selectedKeywords.size < 3) selectedKeywords + keyword else selectedKeywords
                                            }
                                        },
                                        label = { Text(keyword) }
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                extractedKeywords.drop(3).forEach { keyword ->
                                    val isSelected = selectedKeywords.contains(keyword)
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = {
                                            selectedKeywords = if (isSelected) {
                                                selectedKeywords - keyword
                                            } else {
                                                if (selectedKeywords.size < 3) selectedKeywords + keyword else selectedKeywords
                                            }
                                        },
                                        label = { Text(keyword) }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            if (isLoadingReflection) {
                                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                                Spacer(modifier = Modifier.height(12.dp))
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { checkInStep = CheckInStep.IDLE },
                                    modifier = Modifier.weight(1f),
                                    enabled = !isLoadingReflection
                                ) {
                                    Text("Back")
                                }

                                Button(
                                    onClick = {
                                        if (userThought.isNotBlank() && selectedKeywords.size == 3 && !isLoadingReflection) {
                                            isLoadingReflection = true
                                            coroutineScope.launch {
                                                val bestQuote = findBestMatchingQuote(selectedKeywords, fetchedQuotes)
                                                matchingQuote = bestQuote

                                                val prompt = "Provide a warm, supportive 2-sentence reflection for someone who expressed: '${userThought.trim()}' and selected keywords: ${selectedKeywords.joinToString()}."
                                                val quotePrompt = "Create a short, deeply inspiring, original 1-sentence quote specifically tailored to someone experiencing this situation: '${userThought.trim()}'. Format: \"Quote text\" — AI Mindfulness Companion"
                                                reflectionText = try {
                                                    queryCloudflareAI(prompt)
                                                } catch (_: Exception) {
                                                    "Thank you for sharing your thoughts: '${userThought.trim()}'. It takes self-awareness to express how you feel. Take a gentle breath and acknowledge your efforts today."
                                                }

                                                aiSituationQuote = try {
                                                    queryCloudflareAI(quotePrompt)
                                                } catch (_: Exception) {
                                                    "\"In every moment of reflection lies the seed of wisdom and growth.\" — AI Mindfulness Companion"
                                                } finally {
                                                    isLoadingReflection = false
                                                    checkInStep = CheckInStep.REFLECTION
                                                }
                                            }
                                        }
                                    },
                                    modifier = Modifier.weight(1.5f),
                                    enabled = userThought.isNotBlank() && selectedKeywords.size == 3 && !isLoadingReflection
                                ) {
                                    Text("Prepare Reflection")
                                }
                            }
                        }
                    }
                }

                CheckInStep.REFLECTION -> {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(24.dp)) {
                            Text(
                                text = "Your Personal Reflection",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = LightBlueBackground
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = "What you shared:",
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "\"$userThought\"",
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    if (selectedKeywords.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = "Selected Keywords: ${selectedKeywords.joinToString(", ")}",
                                            style = MaterialTheme.typography.labelLarge,
                                            color = MaterialTheme.colorScheme.secondary
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Task: Get the AI to create a quote based on the user's situation
                            aiSituationQuote?.let { quoteText ->
                                Card(
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.AutoAwesome,
                                                contentDescription = "AI Quote",
                                                tint = MaterialTheme.colorScheme.onTertiaryContainer
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "AI Quote for Your Situation:",
                                                style = MaterialTheme.typography.labelLarge,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onTertiaryContainer
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = quoteText,
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.onTertiaryContainer
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                            }

                            matchingQuote?.let { q ->
                                Card(
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.primaryContainer
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.FormatQuote,
                                                contentDescription = "Quote",
                                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "Matching Quote:",
                                                style = MaterialTheme.typography.labelLarge,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = "\"${q.quote}\"",
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "— ${q.author}",
                                            style = MaterialTheme.typography.labelLarge,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                            }

                            Text(
                                text = "Reflection Insight:",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = reflectionText,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            Button(
                                onClick = {
                                    val record = CheckInRecord(
                                        thought = userThought,
                                        reflection = reflectionText,
                                        keywords = selectedKeywords.toList(),
                                        quote = matchingQuote,
                                        timestamp = getCurrentTimestamp("MMM dd, yyyy 'at' h:mm:ss a")
                                    )
                                    onAddCheckIn(record)
                                    userThought = ""
                                    reflectionText = ""
                                    selectedKeywords = emptySet()
                                    matchingQuote = null
                                    checkInStep = CheckInStep.IDLE
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Complete Check-in")
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainDashboardScreen(
    onNavigateHome: () -> Unit,
    checkInHistory: List<CheckInRecord> = emptyList(),
    journalHistory: List<JournalEntry> = emptyList(),
    onAddJournalEntry: (JournalEntry) -> Unit = {},
    feelingHistory: List<FeelingRecord> = emptyList(),
    activeSelectedFeelings: List<String> = emptyList(),
    onAddFeelingRecord: (FeelingRecord) -> Unit = {},
    activeSelfCareGoal: ActiveGoal? = null,
    onSelectGoal: (ActiveGoal) -> Unit = {},
    currentAffirmation: String = "I am capable, resilient, and taking positive steps toward my wellness.",
    onAffirmationChange: (String) -> Unit = {},
    lessonHistory: List<LessonActivity> = emptyList(),
    onCompleteLesson: (LessonActivity) -> Unit = {},
    currentEncouragement: EncouragementRecord? = null,
    encouragementViewCount: Int = 0,
    onGetNewEncouragement: () -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf(DashboardTab.HOME) }

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
                .background(LightBlueBackground)
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                DashboardTab.HOME -> HomeDashboardTab(
                    onNavigateHome = onNavigateHome,
                    activeSelfCareGoal = activeSelfCareGoal,
                    checkInHistory = checkInHistory,
                    lessonHistory = lessonHistory,
                    currentEncouragement = currentEncouragement,
                    encouragementViewCount = encouragementViewCount,
                    onGetNewEncouragement = onGetNewEncouragement
                )
                DashboardTab.STEPS -> StepsScreen(
                    currentAffirmation = currentAffirmation,
                    onAffirmationChange = onAffirmationChange,
                    activeGoal = activeSelfCareGoal,
                    onSelectGoal = onSelectGoal,
                    activeSelectedFeelings = activeSelectedFeelings
                )
                DashboardTab.JOURNAL -> JournalScreen(
                    journalHistory = journalHistory,
                    onAddJournalEntry = onAddJournalEntry,
                    activeSelectedFeelings = activeSelectedFeelings
                )
                DashboardTab.FEELINGS -> FeelingsScreen(
                    feelingHistory = feelingHistory,
                    onAddFeelingRecord = onAddFeelingRecord
                )
                DashboardTab.LESSONS -> LessonsScreen(
                    lessonHistory = lessonHistory,
                    onCompleteLesson = onCompleteLesson
                )
                DashboardTab.AI_TUTOR -> AiTutorScreen()
            }
        }
    }
}

// Task 14: Create the feelings selection screen with selectable emotions
@Composable
fun FeelingsScreen(
    feelingHistory: List<FeelingRecord>,
    onAddFeelingRecord: (FeelingRecord) -> Unit
) {
    var selectedEmotions by remember { mutableStateOf(setOf<String>()) }
    var isLoadingAi by remember { mutableStateOf(false) }
    var aiGuidance by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Feelings & Emotion Selector",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Select up to 3 emotions that represent how you are feeling right now to receive custom AI mood guidance.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.secondary
        )

        // Task 14 Card: Selectable Emotions Grid
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "How are you feeling?",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Badge {
                        Text("${selectedEmotions.size}/3 selected")
                    }
                }

                // Grid of selectable emotion chips
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    val chunks = defaultFeelingsList.chunked(3)
                    chunks.forEach { rowItems ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            rowItems.forEach { option ->
                                val isSelected = selectedEmotions.contains(option.name)
                                FilterChip(
                                    selected = isSelected,
                                    onClick = {
                                        selectedEmotions = if (isSelected) {
                                            selectedEmotions - option.name
                                        } else {
                                            if (selectedEmotions.size < 3) selectedEmotions + option.name else selectedEmotions
                                        }
                                    },
                                    label = { Text("${option.emoji} ${option.name}") },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }

                Button(
                    onClick = {
                        if (selectedEmotions.isNotEmpty() && !isLoadingAi) {
                            isLoadingAi = true
                            coroutineScope.launch {
                                val emotionsList = selectedEmotions.toList()
                                var guidanceText: String? = null
                                try {
                                    val prompt = "A user is currently feeling: ${emotionsList.joinToString()}. Provide a warm, 2-sentence supportive wellness advice or mindfulness tip tailored to these emotions."
                                    guidanceText = queryCloudflareAI(prompt)
                                } catch (_: Exception) {
                                    guidanceText = "Acknowledge your feelings of ${emotionsList.joinToString()}. Take a slow, deep breath and give yourself grace today."
                                } finally {
                                    val record = FeelingRecord(
                                        emotions = emotionsList,
                                        aiGuidance = guidanceText
                                    )
                                    onAddFeelingRecord(record)
                                    aiGuidance = guidanceText
                                    isLoadingAi = false
                                    selectedEmotions = emptySet()
                                }
                            }
                        }
                    },
                    enabled = selectedEmotions.isNotEmpty() && !isLoadingAi,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (isLoadingAi) "Fetching AI Guidance..." else "Save Feelings & Get AI Advice")
                }
            }
        }

        if (isLoadingAi) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        aiGuidance?.let { advice ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "AI Mood Support Tip:",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = advice,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        // History of past selected feelings
        if (feelingHistory.isNotEmpty()) {
            Text(
                text = "Past Selected Feelings (${feelingHistory.size})",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(feelingHistory) { record ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Emotions: ${record.emotions.joinToString(", ")}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = record.timestamp,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                            record.aiGuidance?.let { advice ->
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "AI Advice: $advice",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// Task 6: Main Journaling Screen with a Text Box (Task 15: AI considers selected feelings)
@Composable
fun JournalScreen(
    journalHistory: List<JournalEntry>,
    onAddJournalEntry: (JournalEntry) -> Unit,
    activeSelectedFeelings: List<String> = emptyList()
) {
    var titleText by remember { mutableStateOf("") }
    var bodyText by remember { mutableStateOf("") }
    var isLoadingAi by remember { mutableStateOf(false) }
    var aiAnalysis by remember { mutableStateOf<String?>(null) }
    
    // Task: Add an option for users to correct the summary & update summaries with AI
    var isEditingCorrection by remember { mutableStateOf(false) }
    var userCorrectionText by remember { mutableStateOf("") }
    var isUpdatingCorrection by remember { mutableStateOf(false) }
    
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Daily Mental Wellness Journal",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Write your thoughts, capture your reflections, and analyze your journal entries with AI.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.secondary
        )

        if (activeSelectedFeelings.isNotEmpty()) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "AI Mood Context: Feeling ${activeSelectedFeelings.joinToString(", ")}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.padding(10.dp)
                )
            }
        }

        // Main Journaling Input Card (Task 6: Main Journaling Screen with a text box)
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "New Journal Entry",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    value = titleText,
                    onValueChange = { titleText = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Entry Title (Optional)") },
                    placeholder = { Text("e.g. Morning Reflection, Grateful Moments") },
                    singleLine = true,
                    enabled = !isLoadingAi
                )

                OutlinedTextField(
                    value = bodyText,
                    onValueChange = { bodyText = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    label = { Text("What's on your mind? (Journal Entry Text Box)") },
                    placeholder = { Text("Write freely about your feelings, experiences, or reflections today...") },
                    maxLines = 10,
                    enabled = !isLoadingAi
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${bodyText.length} characters",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            if (bodyText.isNotBlank()) {
                                val newEntry = JournalEntry(
                                    title = if (titleText.isNotBlank()) titleText.trim() else "Journal Entry",
                                    text = bodyText.trim(),
                                    aiSummary = aiAnalysis
                                )
                                onAddJournalEntry(newEntry)
                                titleText = ""
                                bodyText = ""
                                aiAnalysis = null
                                isEditingCorrection = false
                                userCorrectionText = ""
                            }
                        },
                        enabled = bodyText.isNotBlank() && !isLoadingAi,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Save Entry")
                    }

                    OutlinedButton(
                        onClick = {
                            if (bodyText.isNotBlank() && !isLoadingAi) {
                                isLoadingAi = true
                                coroutineScope.launch {
                                    try {
                                        val feelingsContext = if (activeSelectedFeelings.isNotEmpty()) " taking into account that the user is currently feeling: ${activeSelectedFeelings.joinToString()}" else ""
                                        val prompt = "Summarize this mental wellness journal entry in 2 supportive, encouraging sentences$feelingsContext: '$bodyText'"
                                        aiAnalysis = queryCloudflareAI(prompt)
                                    } catch (e: Exception) {
                                        aiAnalysis = "Could not fetch AI reflection: ${e.localizedMessage ?: "Network error"}"
                                    } finally {
                                        isLoadingAi = false
                                    }
                                }
                            }
                        },
                        enabled = bodyText.isNotBlank() && !isLoadingAi,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(if (isLoadingAi) "Analyzing..." else "AI Reflection")
                    }
                }
            }
        }

        if (isLoadingAi || isUpdatingCorrection) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        aiAnalysis?.let { summary ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "AI Journal Reflection:",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = summary,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                    // Option for users to correct the summary & update summary with AI
                    Spacer(modifier = Modifier.height(8.dp))
                    if (!isEditingCorrection) {
                        TextButton(onClick = { isEditingCorrection = true }) {
                            Text("✍️ Correct / Refine AI Summary")
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            OutlinedTextField(
                                value = userCorrectionText,
                                onValueChange = { userCorrectionText = it },
                                label = { Text("Correct or clarify your summary") },
                                placeholder = { Text("e.g. I was feeling hopeful rather than overwhelmed...") },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !isUpdatingCorrection
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        if (userCorrectionText.isNotBlank() && !isUpdatingCorrection) {
                                            isUpdatingCorrection = true
                                            coroutineScope.launch {
                                                try {
                                                    val prompt = "The original journal entry was: '$bodyText'. The initial AI summary was: '$summary'. The user provided this correction: '$userCorrectionText'. Please generate an updated, accurate, supportive 2-sentence summary incorporating this correction."
                                                    val updatedSummary = queryCloudflareAI(prompt)
                                                    aiAnalysis = updatedSummary
                                                    userCorrectionText = ""
                                                    isEditingCorrection = false
                                                } catch (_: Exception) {
                                                    aiAnalysis = "Updated summary: $userCorrectionText"
                                                } finally {
                                                    isUpdatingCorrection = false
                                                }
                                            }
                                        }
                                    },
                                    enabled = userCorrectionText.isNotBlank() && !isUpdatingCorrection,
                                    modifier = Modifier.weight(1.5f)
                                ) {
                                    Text(if (isUpdatingCorrection) "Updating..." else "Update AI Summary")
                                }
                                TextButton(
                                    onClick = { isEditingCorrection = false },
                                    enabled = !isUpdatingCorrection,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Cancel")
                                }
                            }
                        }
                    }
                }
            }
        }

        // Journal Entry Logs / History List
        if (journalHistory.isNotEmpty()) {
            Text(
                text = "Previous Journal Entries (${journalHistory.size})",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(journalHistory) { entry ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = entry.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = entry.timestamp,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = entry.text,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            entry.aiSummary?.let { summary ->
                                Spacer(modifier = Modifier.height(8.dp))
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "AI Reflection: $summary",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(8.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HomeDashboardTab(
    onNavigateHome: () -> Unit,
    activeSelfCareGoal: ActiveGoal? = null,
    checkInHistory: List<CheckInRecord> = emptyList(),
    lessonHistory: List<LessonActivity> = emptyList(),
    currentEncouragement: EncouragementRecord? = null,
    encouragementViewCount: Int = 0,
    onGetNewEncouragement: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBlueBackground)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = "Welcome to Early Enlightenment!",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Explore new topics, check in on your thoughts, and reflect with AI.",
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        currentEncouragement?.let { enc ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Daily Encouragement",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                        Badge(containerColor = MaterialTheme.colorScheme.primary) {
                            Text("Viewed: $encouragementViewCount")
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "\"${enc.message}\"",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Timestamp: ${enc.timestamp} (${enc.triggerSource})",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f)
                        )
                        TextButton(onClick = onGetNewEncouragement) {
                            Text("New Message")
                        }
                    }
                }
            }
        }

        activeSelfCareGoal?.let { goal ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Today's Active Self-Care Goal",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Badge {
                            Text(goal.timestamp)
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = goal.text,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }

        if (checkInHistory.isNotEmpty()) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "My Recent Check-Ins Activity",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    checkInHistory.take(2).forEach { record ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "\"${record.thought}\"",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Badge {
                                Text(record.timestamp)
                            }
                        }
                    }
                }
            }
        }

        if (lessonHistory.isNotEmpty()) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "My Recent Lesson Activity",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    lessonHistory.take(2).forEach { lesson ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = lesson.topicTitle,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Badge {
                                Text(lesson.timestamp)
                            }
                        }
                    }
                }
            }
        }

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
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
            Text("Back to Home View")
        }
    }
}

fun parseAndCleanSelfCareSteps(rawResponse: String): List<String> {
    val lines = rawResponse.split("\n", "\r").map { it.trim() }.filter { it.isNotBlank() }
    val steps = mutableListOf<String>()

    for (line in lines) {
        val cleaned = line
            .replace(Regex("^(Step\\s*\\d*[:.-]?|\\d+[:.-]|[-*•])\\s*"), "")
            .trim()
        if (cleaned.length > 5 && !cleaned.startsWith("Here are", ignoreCase = true) && !cleaned.startsWith("Based on", ignoreCase = true)) {
            steps.add(cleaned)
        }
    }

    if (steps.isEmpty()) {
        val candidates = mutableListOf<String>()
        for (line in lines) {
            val cleaned = line.replace(Regex("^[*-•]\\s*"), "").trim()
            if (cleaned.isNotBlank()) {
                candidates.add(cleaned)
            }
        }
        var i = 0
        val recommendations = mutableListOf<String>()
        while (i < candidates.size) {
            val current = candidates[i]
            if (i + 1 < candidates.size && (current.startsWith("Step", ignoreCase = true) || current.length < 45)) {
                recommendations.add("$current: ${candidates[i + 1]}")
                i += 2
            } else {
                recommendations.add(current)
                i += 1
            }
        }
        return recommendations.take(4)
    }
    return steps.take(4)
}

// Task 17: Get the AI to suggest a small, realistic step toward the user's goal
@Composable
fun StepsScreen(
    currentAffirmation: String,
    onAffirmationChange: (String) -> Unit,
    activeGoal: ActiveGoal?,
    onSelectGoal: (ActiveGoal) -> Unit,
    activeSelectedFeelings: List<String> = emptyList()
) {
    var isLoading by remember { mutableStateOf(false) }
    var stepsList by remember { mutableStateOf<List<String>>(emptyList()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    // Task 17 state
    var customGoalText by remember { mutableStateOf("") }
    var isGoalStepLoading by remember { mutableStateOf(false) }
    var aiSuggestedStep by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBlueBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Best Small Steps",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Discover personalized self-care solutions based on your current affirmation and specific personal goals.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.secondary
        )

        if (activeSelectedFeelings.isNotEmpty()) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "AI Context: Considering feelings (${activeSelectedFeelings.joinToString(", ")})",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.padding(10.dp)
                )
            }
        }

        // Task 17 Card: AI Goal Step Generator
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Personal Goal Step Suggestion (Task 17)",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Enter a wellness or personal goal you'd like to achieve to get a small, realistic step suggested by AI.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    value = customGoalText,
                    onValueChange = { customGoalText = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Your Personal Goal") },
                    placeholder = { Text("e.g. Reduce daily stress, sleep 8 hours, practice mindfulness...") },
                    singleLine = true,
                    enabled = !isGoalStepLoading
                )

                Button(
                    onClick = {
                        val userGoal = customGoalText.trim().ifEmpty { activeGoal?.text ?: currentAffirmation.trim() }
                        if (userGoal.isNotBlank() && !isGoalStepLoading) {
                            isGoalStepLoading = true
                            coroutineScope.launch {
                                try {
                                    val feelingsContext = if (activeSelectedFeelings.isNotEmpty()) " taking into account current feelings of: ${activeSelectedFeelings.joinToString()}" else ""
                                    val prompt = "The user has set the personal goal: '$userGoal'$feelingsContext. Suggest 1 small, realistic, achievable step they can take today toward this goal. Keep it warm, actionable, and 2 sentences long."
                                    aiSuggestedStep = queryCloudflareAI(prompt)
                                } catch (_: Exception) {
                                    aiSuggestedStep = "Break your goal of '$userGoal' into a tiny 5-minute action today to build momentum."
                                } finally {
                                    isGoalStepLoading = false
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isGoalStepLoading && (customGoalText.isNotBlank() || activeGoal != null || currentAffirmation.isNotBlank())
                ) {
                    Text(if (isGoalStepLoading) "Suggesting Step..." else "Suggest Small Step for Goal")
                }

                if (isGoalStepLoading) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }

                aiSuggestedStep?.let { step ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "AI Suggested Small Step:",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = step,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            val isSelected = activeGoal?.text == step
                            TextButton(
                                onClick = {
                                    val newGoal = ActiveGoal(step, getCurrentTimestamp("h:mm a"))
                                    onSelectGoal(newGoal)
                                }
                            ) {
                                Text(
                                    if (isSelected) "✓ Active Goal Set"
                                    else "+ Set as Active Goal for Home"
                                )
                            }
                        }
                    }
                }
            }
        }

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Your Current Affirmation",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
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

        Button(
            onClick = {
                val affirmationText = currentAffirmation.trim()
                if (affirmationText.isNotBlank() && !isLoading) {
                    isLoading = true
                    errorMessage = null
                    coroutineScope.launch {
                        try {
                            val feelingsContext = if (activeSelectedFeelings.isNotEmpty()) " and current selected feelings: ${activeSelectedFeelings.joinToString()}" else ""
                            val prompt = "Based on this mood affirmation: '$affirmationText'$feelingsContext, generate 4 small, realistic, and practical self-care steps to improve mood. List each step clearly."
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

        if (stepsList.isNotEmpty()) {
            Text(
                text = "Recommended Self-Care Solutions:",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                stepsList.forEach { step ->
                    val goalTimestamp = activeGoal?.timestamp
                    val isSelected = activeGoal != null && activeGoal.text == step
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer
                            else MaterialTheme.colorScheme.surface
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = step,
                                style = MaterialTheme.typography.bodyLarge,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
                                else MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            TextButton(
                                onClick = {
                                    val goal = ActiveGoal(step, getCurrentTimestamp("h:mm a"))
                                    onSelectGoal(goal)
                                }
                            ) {
                                Text(
                                    if (isSelected) "✓ Active Goal Set (Set at $goalTimestamp)"
                                    else "+ Set as Active Goal for Home"
                                )
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
fun LessonsScreen(
    lessonHistory: List<LessonActivity> = emptyList(),
    onCompleteLesson: (LessonActivity) -> Unit = {}
) {
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
            .background(LightBlueBackground)
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

        if (lessonHistory.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "My Studied Lessons History",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    lessonHistory.take(3).forEach { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "• ${item.topicTitle} (${item.category})",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Badge {
                                Text(item.timestamp)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(topics) { topic ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
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
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Badge {
                                Text(topic.category)
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = topic.description,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
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
                TextButton(onClick = {
                    val activity = LessonActivity(
                        topicTitle = topic.title,
                        category = topic.category,
                        timestamp = getCurrentTimestamp("h:mm:ss a")
                    )
                    onCompleteLesson(activity)
                    selectedTopic = null
                }) {
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
    val chatMessages = remember { mutableStateListOf<ChatMessage>() }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBlueBackground)
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
                    items(chatMessages) { msg ->
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (msg.sender == "You") MaterialTheme.colorScheme.primaryContainer
                                else MaterialTheme.colorScheme.surface
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = msg.sender,
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = if (msg.sender == "You") MaterialTheme.colorScheme.onPrimaryContainer
                                        else MaterialTheme.colorScheme.onSurface
                                    )
                                    Badge(
                                        containerColor = if (msg.sender == "You") MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                        else MaterialTheme.colorScheme.secondaryContainer
                                    ) {
                                        Text(
                                            text = msg.timestamp,
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = msg.text,
                                    color = if (msg.sender == "You") MaterialTheme.colorScheme.onPrimaryContainer
                                    else MaterialTheme.colorScheme.onSurface
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
                        val userTimestamp = getCurrentTimestamp("h:mm:ss a")
                        chatMessages.add(ChatMessage("You", prompt, userTimestamp))
                        isLoading = true
                        errorMessage = null

                        coroutineScope.launch {
                            try {
                                val aiReply = queryCloudflareAI(prompt)
                                val aiTimestamp = getCurrentTimestamp("h:mm:ss a")
                                chatMessages.add(ChatMessage("AI Tutor", aiReply, aiTimestamp))
                            } catch (e: Exception) {
                                errorMessage = "Network Error: ${e.localizedMessage ?: "Failed to reach AI worker."}"
                                val sysTimestamp = getCurrentTimestamp("h:mm:ss a")
                                chatMessages.add(ChatMessage("System", "Failed to generate AI response. Please check your connection.", sysTimestamp))
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
