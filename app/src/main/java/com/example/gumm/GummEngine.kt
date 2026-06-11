package com.example.gumm

import com.example.data.*
import java.util.*

data class GummOptimizedTask(
    val title: String,
    val durationMinutes: Int,
    val type: String, // "REVISION", "HOMEWORK", "CRITICAL_EXAM"
    val subtitle: String,
    val reason: String
)

data class GummRecommendation(
    val subjectName: String,
    val chapterName: String,
    val color: String,
    val reason: String,
    val estimatedMinutes: Int
)

data class GummMatrixWheelRecommendation(
    val heading: String,
    val directive: String,
    val actionText: String,
    val proportionHomework: Float, // 0.0 to 1.0
    val proportionRevision: Float
)

data class HabitCorrelationResult(
    val hourOfDay: Int,
    val frequency: Int,
    val avgDuration: Float,
    val qualityScore: Int, // 1 to 5
    val recommendation: String
)

data class ClusterResult(
    val clusterName: String,
    val chapterNames: List<String>,
    val explanation: String
)

object GummEngine {

    // Knapsack optimization sequence
    fun optimizeTimeBudget(
        availableMinutes: Int,
        chapters: List<Chapter>,
        homeworks: List<Homework>,
        reps: List<SpacedRepetition>,
        subjects: List<Subject>
    ): List<GummOptimizedTask> {
        val subjectMap = subjects.associateBy { it.id }
        val chapterMap = chapters.associateBy { it.id }

        // Compile potential items to fit in our time budget
        val candidates = mutableListOf<GummOptimizedTask>()

        // 1. Spaced Repetition Due (Weight = High, Estimate ~ 15 mins)
        val pendingReps = reps.filter { !it.isCompleted }
        for (rep in pendingReps) {
            val chapter = chapterMap[rep.chapterId] ?: continue
            val subName = subjectMap[chapter.subjectId]?.name ?: "Subject"
            candidates.add(
                GummOptimizedTask(
                    title = "Revise: ${chapter.name}",
                    durationMinutes = 15,
                    type = "REVISION",
                    subtitle = "$subName · Day ${rep.dayInterval}",
                    reason = "Overdue interval needs immediate Day ${rep.dayInterval} memory reinforcement."
                )
            )
        }

        // 2. Pending Homework (Weight = Medium/High, Estimate = Homework.estimatedMinutes)
        val pendingHomework = homeworks.filter { !it.isCompleted }
        for (hw in pendingHomework) {
            val subName = subjectMap[hw.subjectId]?.name ?: "Subject"
            candidates.add(
                GummOptimizedTask(
                    title = "Homework: ${hw.title}",
                    durationMinutes = hw.estimatedMinutes.coerceAtLeast(10).coerceAtMost(60),
                    type = "HOMEWORK",
                    subtitle = "$subName · Due soon",
                    reason = "Urgent school worksheet requires completion."
                )
            )
        }

        // 3. Chapters Started but not Mastered / Revised (Weight = Medium, Estimate ~ 20 mins)
        val examChapters = chapters.filter { it.state == "Started" || it.state == "Mastered" }
        for (ch in examChapters) {
            val subName = subjectMap[ch.subjectId]?.name ?: "Subject"
            candidates.add(
                GummOptimizedTask(
                    title = "Review: ${ch.name}",
                    durationMinutes = 20,
                    type = "CRITICAL_EXAM",
                    subtitle = "$subName · State: ${ch.state}",
                    reason = "Unmastered syllabus chapter with oncoming exam targets."
                )
            )
        }

        // Simple Greedy Knapsack-like scheduling:
        // Sort candidates by custom importance priority
        val sortedCandidates = candidates.sortedByDescending {
            when (it.type) {
                "REVISION" -> 300 // Revisions are critical to prevent memory drop-off
                "HOMEWORK" -> 200 // Homework has deadlines
                else -> 100 // General exam prep is medium-term
            }
        }

        val result = mutableListOf<GummOptimizedTask>()
        var timeRemaining = availableMinutes

        for (candidate in sortedCandidates) {
            if (timeRemaining >= candidate.durationMinutes) {
                result.add(candidate)
                timeRemaining -= candidate.durationMinutes
            } else if (timeRemaining >= 10 && candidate.type == "REVISION") {
                // Compress revision if possible
                result.add(candidate.copy(durationMinutes = timeRemaining, reason = "COMPRESSED: ${candidate.reason}"))
                timeRemaining = 0
            }
            if (timeRemaining <= 0) break
        }

        return result
    }

    // Recommendation Processor: "What Subject to Study Now?"
    fun recommendSubjectNow(
        chapters: List<Chapter>,
        subjects: List<Subject>,
        exams: List<Exam>,
        energyLevel: Int
    ): GummRecommendation {
        if (subjects.isEmpty() || chapters.isEmpty()) {
            return GummRecommendation(
                subjectName = "System Setup",
                chapterName = "Set up your Syllabus Match",
                color = "#FFD1DC",
                reason = "Please add custom subjects and chapters first inside your Syllabus Matrix!",
                estimatedMinutes = 20
            )
        }

        val subjectMap = subjects.associateBy { it.id }

        // Strategy: Look for chapters that have the lowest state index ("Started" or "Mastered") linked to upcoming exams,
        // or look for subjects with maximum unrevised units.
        val targetDifficulty = if (energyLevel >= 4) 4 else if (energyLevel <= 2) 2 else 3

        val chapterWeights = chapters.map { chapter ->
            var weight = 100
            if (chapter.state == "Started") weight += 300
            if (chapter.state == "Mastered") weight += 150
            if (chapter.state == "Revised") weight += 50

            // Match difficulty
            if (chapter.difficultyLevel >= targetDifficulty) weight += 100
            if (!chapter.isDoubtResolved) weight += 200

            chapter to weight
        }

        val bestMatch = chapterWeights.maxByOrNull { it.second }?.first ?: chapters.first()
        val matchSubject = subjectMap[bestMatch.subjectId] ?: subjects.first()

        val reason = when {
            !bestMatch.isDoubtResolved -> "This topic contains unresolved active doubts with teachers. Perfect opportunity to work on clarification!"
            bestMatch.state == "Started" && energyLevel >= 4 -> "Your energy profile is high. Take advantage by initiating deep analytical mastery on ${bestMatch.name}."
            bestMatch.state == "Started" && energyLevel <= 2 -> "Your energy is low. Let's do a light reading of ${bestMatch.name} to advance it slowly without fatigue."
            else -> "Spaced-repetition analysis indicates high subject neglect. Revise this topic to secure retention."
        }

        return GummRecommendation(
            subjectName = matchSubject.name,
            chapterName = bestMatch.name,
            color = matchSubject.color,
            reason = reason,
            estimatedMinutes = if (energyLevel >= 4) 45 else 20
        )
    }

    // Matrix Wheel Generator: "Study / Homework / Revise" Matrix Wheel Carousel Assistant
    fun getMatrixWheelRecommendation(
        homeworks: List<Homework>,
        reps: List<SpacedRepetition>,
        subjects: List<Subject>
    ): GummMatrixWheelRecommendation {
        val pendingHomeworkCount = homeworks.count { !it.isCompleted }
        val pendingRepCount = reps.count { !it.isCompleted }

        val total = pendingHomeworkCount + pendingRepCount
        if (total == 0) {
            return GummMatrixWheelRecommendation(
                heading = "All Caught Up! 🌟",
                directive = "Maintain your current streak!",
                actionText = "Add a new syllabus chapter or design custom subjects in the Studio.",
                proportionHomework = 0.5f,
                proportionRevision = 0.5f
            )
        }

        val pHw = pendingHomeworkCount.toFloat() / total
        val pRep = pendingRepCount.toFloat() / total

        return if (pHw >= pRep) {
            GummMatrixWheelRecommendation(
                heading = "Homework Priority Ledger 📝",
                directive = "Your unresolved homework loads are slipping!",
                actionText = "Prioritize Homework items. Grab a scan of physical worksheets and submit first.",
                proportionHomework = pHw,
                proportionRevision = pRep
            )
        } else {
            GummMatrixWheelRecommendation(
                heading = "Review Interval Matrix 🔁",
                directive = "Keep the memory curve active!",
                actionText = "Focus on the overdue Day 3 / Day 7 Spaced Repetitions immediately.",
                proportionHomework = pHw,
                proportionRevision = pRep
            )
        }
    }

    // AI-Driven Habit Correlation Engine & Peak Efficiency Window Profiler
    fun getHabitCorrelations(logs: List<StudySessionLog>): List<HabitCorrelationResult> {
        if (logs.isEmpty()) {
            return listOf(
                HabitCorrelationResult(
                    hourOfDay = 16,
                    frequency = 0,
                    avgDuration = 30f,
                    qualityScore = 3,
                    recommendation = "No study logs recorded yet. Complete focus sessions to map habit analytics!"
                )
            )
        }

        val calendar = Calendar.getInstance()
        val groups = logs.groupBy {
            calendar.timeInMillis = it.timestamp
            calendar.get(Calendar.HOUR_OF_DAY)
        }

        return groups.map { (hour, groupLogs) ->
            val freq = groupLogs.size
            val avgDur = groupLogs.map { it.durationMinutes }.average().toFloat()
            val avgQuality = groupLogs.map { it.loadMeterRating }.average().toFloat()

            // Map comfort score: low cognitive load rating (meaning low frustration) + decent duration = higher score
            val qualityScore = when {
                avgQuality >= 4.0 -> 2 // High frustration
                avgQuality <= 2.0 -> 5 // Low frustration / flow state
                else -> 4
            }

            val hourStr = when {
                hour == 0 -> "12 AM"
                hour < 12 -> "$hour AM"
                hour == 12 -> "12 PM"
                else -> "${hour - 12} PM"
            }

            val rec = when {
                avgDur >= 40f && qualityScore >= 4 -> "Spectacular peak performance observed at $hourStr. Highly recommended for intense analytical work!"
                qualityScore <= 2 -> "Friction detected at $hourStr (high fatigue/frustration). Suggest switching to lighter readings during this interval."
                else -> "Balanced efficiency observed at $hourStr. Suitable for regular revisions."
            }

            HabitCorrelationResult(
                hourOfDay = hour,
                frequency = freq,
                avgDuration = avgDur,
                qualityScore = qualityScore,
                recommendation = rec
            )
        }.sortedByDescending { it.frequency }
    }

    // Cross-Subject Context Clustering
    fun clusterTopics(chapters: List<Chapter>, subjects: List<Subject>): List<ClusterResult> {
        if (chapters.size < 2) return emptyList()
        val subjectMap = subjects.associateBy { it.id }

        // Clean cluster matching algorithm based on semantic keyword match
        val clusters = mutableListOf<ClusterResult>()

        val physicsChapters = chapters.filter { subjectMap[it.subjectId]?.name?.contains("Physics", true) == true }
        val mathChapters = chapters.filter { subjectMap[it.subjectId]?.name?.contains("Math", true) == true }
        val scienceChapters = chapters.filter { subjectMap[it.subjectId]?.name?.contains("Science", true) == true || subjectMap[it.subjectId]?.name?.contains("Chemistry", true) == true }

        if (physicsChapters.isNotEmpty() && mathChapters.isNotEmpty()) {
            clusters.add(
                ClusterResult(
                    clusterName = "Optics & Trigonometric Geometry",
                    chapterNames = (physicsChapters.take(2) + mathChapters.take(2)).map { it.name },
                    explanation = "Reinforce conceptual equations by studying optical trigonometry and geometry formulas simultaneously."
                )
            )
        }

        if (scienceChapters.isNotEmpty() && mathChapters.isNotEmpty()) {
            clusters.add(
                ClusterResult(
                    clusterName = "Physical Kinetics & Algebraic Formulas",
                    chapterNames = (scienceChapters.take(2) + mathChapters.take(2)).map { it.name },
                    explanation = "Optimize chemistry rate constants or physical kinetics by linking standard algebraic calculus modules together."
                )
            )
        }

        return clusters
    }
}
