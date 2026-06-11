package com.example.data

import androidx.room.*

@Entity(tableName = "subjects")
data class Subject(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val color: String, // Hex code (e.g., "#FFD1DC" for Strawberry Pink)
    val iconName: String // e.g., "school", "science", "calculate"
)

@Entity(tableName = "chapters")
data class Chapter(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val subjectId: Int,
    val name: String,
    val state: String = "Started", // "Started", "Mastered", "Revised", "Exam Ready"
    val cumulativeTimeMinutes: Long = 0,
    val leftToStudyChecklist: String = "",
    val activeDoubtLedger: String = "",
    val isDoubtResolved: Boolean = true,
    val difficultyLevel: Int = 3, // 1 to 5
    val lastStudiedTimestamp: Long = 0,
    val confidenceStars: Int = 3
)

@Entity(tableName = "homeworks")
data class Homework(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val subjectId: Int,
    val title: String,
    val description: String = "",
    val dueDate: Long,
    val isCompleted: Boolean = false,
    val imagePath: String? = null, // URI of scanned worksheet
    val estimatedMinutes: Int = 30
)

@Entity(tableName = "exams")
data class Exam(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String, // "Unit Test", "Periodic Test", "Mid-Term", "Final Board"
    val startDate: Long,
    val chapterIdsString: String // comma separated ints
)

@Entity(tableName = "spaced_repetitions")
data class SpacedRepetition(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val chapterId: Int,
    val dayInterval: Int, // 1, 3, 7, 30, 90
    val scheduledDate: Long,
    val isCompleted: Boolean = false,
    val snoozeCount: Int = 0
)

@Entity(tableName = "recurrent_skills")
data class RecurrentSkill(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val type: String, // "Weekly", "Monthly"
    val nextScheduledDate: Long,
    val lastCompletedDate: Long? = null,
    val completedCount: Int = 0,
    val skippedCount: Int = 0
)

@Entity(tableName = "user_profiles")
data class UserProfile(
    @PrimaryKey val id: Int = 1,
    val userName: String = "Student",
    val googleEmail: String? = null,
    val gradeLevel: String = "High School",
    val availableHoursWeekday: Float = 2.0f,
    val availableHoursWeekend: Float = 4.0f,
    val peakFocusHours: String = "Afternoon", // "Morning", "Afternoon", "Evening"
    val attentionSpanMinutes: Int = 30,
    val mathDifficulty: Int = 3,
    val scienceDifficulty: Int = 3,
    val languagesDifficulty: Int = 3,
    val humanitiesDifficulty: Int = 3,
    val isOnboarded: Boolean = false,
    val currentEnergyLevel: Int = 3,
    val useAdaptiveColor: Boolean = false
)

@Entity(tableName = "study_sessions")
data class StudySessionLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val chapterId: Int,
    val durationMinutes: Int,
    val timestamp: Long,
    val loadMeterRating: Int // load rating 1-5
)
