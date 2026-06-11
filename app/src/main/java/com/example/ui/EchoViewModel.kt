package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.*
import com.example.gumm.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class EchoViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    val repository = EchoRepository(db.echoDao())

    // Database states
    val subjects: StateFlow<List<Subject>> = repository.subjects
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val chapters: StateFlow<List<Chapter>> = repository.chapters
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val homeworks: StateFlow<List<Homework>> = repository.homeworks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val exams: StateFlow<List<Exam>> = repository.exams
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val spacedRepetitions: StateFlow<List<SpacedRepetition>> = repository.spacedRepetitions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pendingSpacedRepetitions: StateFlow<List<SpacedRepetition>> = repository.pendingSpacedRepetitions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recurrentSkills: StateFlow<List<RecurrentSkill>> = repository.recurrentSkills
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userProfile: StateFlow<UserProfile?> = repository.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val studySessions: StateFlow<List<StudySessionLog>> = repository.studySessions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Interactive UI and Engine states
    val timeBudgetMinutes = MutableStateFlow(45)
    val userEnergyLevel = MutableStateFlow(3) // 1 to 5

    // Timer States
    val activeTimerChapterId = MutableStateFlow<Int?>(null)
    val activeTimerSecondsElapsed = MutableStateFlow(0)
    val isTimerRunning = MutableStateFlow(false)

    // Current Navigation Screen State
    val currentNavigationTab = MutableStateFlow("dashboard") // dashboard, syllabus, homework, analytics, studio

    // Gumm derived knapsack time plan state
    val gummTimePlan: StateFlow<List<GummOptimizedTask>> = combine(
        timeBudgetMinutes,
        chapters,
        homeworks,
        pendingSpacedRepetitions,
        subjects
    ) { textBudget, chaptersVal, hwVal, repsVal, subVal ->
        GummEngine.optimizeTimeBudget(textBudget, chaptersVal, hwVal, repsVal, subVal)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Gumm subject recommender
    val gummRecommendation: StateFlow<GummRecommendation> = combine(
        chapters,
        subjects,
        exams,
        userEnergyLevel
    ) { chaptersVal, subjectsVal, examsVal, energyVal ->
        GummEngine.recommendSubjectNow(chaptersVal, subjectsVal, examsVal, energyVal)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), GummRecommendation("System Setup", "Unconfigured", "#FFD1DC", "...", 20))

    // Gumm circular matrix wheel helper
    val gummMatrixWheel: StateFlow<GummMatrixWheelRecommendation> = combine(
        homeworks,
        pendingSpacedRepetitions,
        subjects
    ) { hwVal, repsVal, subVal ->
        GummEngine.getMatrixWheelRecommendation(hwVal, repsVal, subVal)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), GummMatrixWheelRecommendation("All Caught Up!", "...", "...", 0.5f, 0.5f))

    // Onboarding methods
    fun onboardUser(
        gradeLevel: String,
        availableHoursWeekday: Float,
        availableHoursWeekend: Float,
        peakHours: String,
        attentionSpan: Int,
        mathDiff: Int,
        scienceDiff: Int,
        langDiff: Int,
        humDiff: Int
    ) {
        viewModelScope.launch {
            val profile = UserProfile(
                id = 1,
                gradeLevel = gradeLevel,
                availableHoursWeekday = availableHoursWeekday,
                availableHoursWeekend = availableHoursWeekend,
                peakFocusHours = peakHours,
                attentionSpanMinutes = attentionSpan,
                mathDifficulty = mathDiff,
                scienceDifficulty = scienceDiff,
                languagesDifficulty = langDiff,
                humanitiesDifficulty = humDiff,
                isOnboarded = true,
                currentEnergyLevel = userEnergyLevel.value
            )
            repository.insertUserProfile(profile)

            // Seed default cute subjects on first launch to ensure excellent demo flow
            seedDefaultSubjects()
        }
    }

    private suspend fun seedDefaultSubjects() {
        val existing = subjects.value
        if (existing.isEmpty()) {
            val defaults = listOf(
                Subject(name = "Mathematics AP", color = "#FEF1B5", iconName = "calculate"), // Banana Yellow
                Subject(name = "Physics Kinetics", color = "#B3E5FC", iconName = "science"),  // Sky Blue
                Subject(name = "Chemistry Lab", color = "#CBF3D2", iconName = "vaccines"),     // Mint Green
                Subject(name = "English Poetry", color = "#FFD1DC", iconName = "menu_book")    // Strawberry Pink
            )
            defaults.forEach {
                val subId = db.echoDao().insertSubject(it).toInt()
                // Seed standard chapters
                when (it.name) {
                    "Mathematics AP" -> {
                        db.echoDao().insertChapter(Chapter(subjectId = subId, name = "Arithmetic Progressions", difficultyLevel = 4, state = "Started"))
                        db.echoDao().insertChapter(Chapter(subjectId = subId, name = "Limits and Integrals", difficultyLevel = 5, state = "Started"))
                    }
                    "Physics Kinetics" -> {
                        db.echoDao().insertChapter(Chapter(subjectId = subId, name = "Optics & Reflection", difficultyLevel = 3, state = "Mastered"))
                        db.echoDao().insertChapter(Chapter(subjectId = subId, name = "Rotational Mechanics", difficultyLevel = 5, state = "Started"))
                    }
                    "Chemistry Lab" -> {
                        db.echoDao().insertChapter(Chapter(subjectId = subId, name = "Chemical Kinetics", difficultyLevel = 4, state = "Started"))
                    }
                }
            }
        }
    }

    // Subject operations
    fun addSubject(name: String, colorHex: String, iconName: String) {
        viewModelScope.launch {
            repository.insertSubject(Subject(name = name, color = colorHex, iconName = iconName))
        }
    }

    fun deleteSubject(subject: Subject) {
        viewModelScope.launch {
            repository.deleteSubject(subject)
        }
    }

    // Chapters logic
    fun addChapter(subjectId: Int, name: String, difficulty: Int) {
        viewModelScope.launch {
            val chapterId = db.echoDao().insertChapter(
                Chapter(
                    subjectId = subjectId,
                    name = name,
                    difficultyLevel = difficulty,
                    state = "Started"
                )
            ).toInt()
            
            // Auto schedule spaced reminders for 1, 3, 7, 30, and 90 days!
            scheduleSpacedRepetitions(chapterId)
        }
    }

    private suspend fun scheduleSpacedRepetitions(chapterId: Int) {
        val now = System.currentTimeMillis()
        val oneDayMs = 24 * 60 * 60 * 1000L
        listOf(1, 3, 7, 30, 90).forEach { day ->
            repository.insertSpacedRepetition(
                SpacedRepetition(
                    chapterId = chapterId,
                    dayInterval = day,
                    scheduledDate = now + (day * oneDayMs),
                    isCompleted = false
                )
            )
        }
    }

    fun updateChapterState(chapter: Chapter, newState: String) {
        viewModelScope.launch {
            repository.updateChapter(chapter.copy(state = newState))
        }
    }

    fun updateChapterDetails(chapterId: Int, checklist: String, doubtLedger: String, doubtsResolved: Boolean, confidence: Int) {
        viewModelScope.launch {
            val existing = db.echoDao().getChapterById(chapterId) ?: return@launch
            repository.updateChapter(existing.copy(
                leftToStudyChecklist = checklist,
                activeDoubtLedger = doubtLedger,
                isDoubtResolved = doubtsResolved,
                confidenceStars = confidence
            ))
        }
    }

    // Spaced repetition completions
    fun completeSpacedRepetition(rep: SpacedRepetition) {
        viewModelScope.launch {
            repository.updateSpacedRepetition(rep.copy(isCompleted = true))
            
            val chapter = db.echoDao().getChapterById(rep.chapterId) ?: return@launch
            val nextState = when (rep.dayInterval) {
                1 -> "Started"
                3 -> "Mastered"
                7 -> "Revised"
                30 -> "Revised"
                90 -> "Exam Ready"
                else -> chapter.state
            }
            repository.updateChapter(chapter.copy(
                state = nextState,
                cumulativeTimeMinutes = chapter.cumulativeTimeMinutes + 15,
                lastStudiedTimestamp = System.currentTimeMillis()
            ))

            // Log focus session record
            repository.insertStudySessionLog(
                StudySessionLog(
                    chapterId = rep.chapterId,
                    durationMinutes = 15,
                    timestamp = System.currentTimeMillis(),
                    loadMeterRating = 3
                )
            )
        }
    }

    fun snoozeSpacedRepetition(rep: SpacedRepetition) {
        viewModelScope.launch {
            // Adjust scheduled target date forward in daily steps
            repository.updateSpacedRepetition(
                rep.copy(
                    snoozeCount = rep.snoozeCount + 1,
                    scheduledDate = System.currentTimeMillis() + (12 * 60 * 60 * 1000L) // Snooze to peak afternoon/night (12h)
                )
            )
        }
    }

    // Homework Management
    fun addHomework(subjectId: Int, title: String, description: String, dueDate: Long, estMinutes: Int) {
        viewModelScope.launch {
            repository.insertHomework(
                Homework(
                    subjectId = subjectId,
                    title = title,
                    description = description,
                    dueDate = dueDate,
                    estimatedMinutes = estMinutes,
                    isCompleted = false
                )
            )
        }
    }

    fun toggleHomeworkCompleted(hw: Homework) {
        viewModelScope.launch {
            repository.updateHomework(hw.copy(isCompleted = !hw.isCompleted))
        }
    }

    fun attachWorksheetScanSimulated(hwId: Int) {
        viewModelScope.launch {
            val list = homeworks.value
            val match = list.find { it.id == hwId } ?: return@launch
            // Simulate a beautiful compressed local image path
            val scanPath = "simulated_scanned_work_hw_${hwId}.jpg"
            repository.updateHomework(match.copy(imagePath = scanPath))
        }
    }

    // Exams
    fun addExam(title: String, startDate: Long, chapterIds: List<Int>) {
        viewModelScope.launch {
            repository.insertExam(
                Exam(
                    title = title,
                    startDate = startDate,
                    chapterIdsString = chapterIds.joinToString(",")
                )
            )
        }
    }

    fun deleteExam(exam: Exam) {
        viewModelScope.launch {
            repository.deleteExam(exam)
        }
    }

    // Recurrent routines
    fun addRecurrentSkill(title: String, type: String) {
        viewModelScope.launch {
            val onePeriod = if (type == "Weekly") 7 * 24 * 60 * 60 * 1000L else 30 * 24 * 60 * 60 * 1000L
            repository.insertRecurrentSkill(
                RecurrentSkill(
                    title = title,
                    type = type,
                    nextScheduledDate = System.currentTimeMillis() + onePeriod
                )
            )
        }
    }

    fun recordRecurrentSkillCompliance(skill: RecurrentSkill, completed: Boolean) {
        viewModelScope.launch {
            val onePeriod = if (skill.type == "Weekly") 7 * 24 * 60 * 60 * 1000L else 30 * 24 * 60 * 60 * 1000L
            repository.updateRecurrentSkill(
                skill.copy(
                    nextScheduledDate = System.currentTimeMillis() + onePeriod,
                    lastCompletedDate = if (completed) System.currentTimeMillis() else skill.lastCompletedDate,
                    completedCount = skill.completedCount + if (completed) 1 else 0,
                    skippedCount = skill.skippedCount + if (completed) 0 else 1
                )
            )
        }
    }

    fun deleteRecurrentSkill(skill: RecurrentSkill) {
        viewModelScope.launch {
            repository.deleteRecurrentSkill(skill)
        }
    }

    // Live study Timer Operations
    fun startFocusSession(chapterId: Int) {
        activeTimerChapterId.value = chapterId
        activeTimerSecondsElapsed.value = 0
        isTimerRunning.value = true
    }

    fun pauseFocusSession() {
        isTimerRunning.value = false
    }

    fun resumeFocusSession() {
        isTimerRunning.value = true
    }

    fun tickSeconds() {
        if (isTimerRunning.value) {
            activeTimerSecondsElapsed.value += 1
        }
    }

    fun stopAndSaveFocusSession(loadRating: Int) {
        viewModelScope.launch {
            val chapterId = activeTimerChapterId.value ?: return@launch
            val seconds = activeTimerSecondsElapsed.value
            val minutes = (seconds / 60).coerceAtLeast(1)

            isTimerRunning.value = false
            activeTimerChapterId.value = null
            activeTimerSecondsElapsed.value = 0

            val chapter = db.echoDao().getChapterById(chapterId) ?: return@launch
            repository.updateChapter(chapter.copy(
                cumulativeTimeMinutes = chapter.cumulativeTimeMinutes + minutes,
                lastStudiedTimestamp = System.currentTimeMillis()
            ))

            // Save study session log
            repository.insertStudySessionLog(
                StudySessionLog(
                    chapterId = chapterId,
                    durationMinutes = minutes,
                    timestamp = System.currentTimeMillis(),
                    loadMeterRating = loadRating
                )
            )
        }
    }

    fun cancelFocusSession() {
        isTimerRunning.value = false
        activeTimerChapterId.value = null
        activeTimerSecondsElapsed.value = 0
    }

    fun updateEnergyLevel(level: Int) {
        userEnergyLevel.value = level
        viewModelScope.launch {
            val profile = repository.getUserProfileDirect() ?: return@launch
            repository.insertUserProfile(profile.copy(currentEnergyLevel = level))
        }
    }

    fun toggleAdaptiveColorTheme(enabled: Boolean) {
        viewModelScope.launch {
            val profile = repository.getUserProfileDirect() ?: return@launch
            repository.insertUserProfile(profile.copy(useAdaptiveColor = enabled))
        }
    }
}

class EchoViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EchoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EchoViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
