package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface EchoDao {
    // Subject Queries
    @Query("SELECT * FROM subjects")
    fun getAllSubjects(): Flow<List<Subject>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubject(subject: Subject): Long

    @Delete
    suspend fun deleteSubject(subject: Subject)

    @Query("SELECT * FROM subjects WHERE id = :id LIMIT 1")
    suspend fun getSubjectById(id: Int): Subject?

    // Chapter Queries
    @Query("SELECT * FROM chapters")
    fun getAllChapters(): Flow<List<Chapter>>

    @Query("SELECT * FROM chapters WHERE subjectId = :subjectId")
    fun getChaptersBySubject(subjectId: Int): Flow<List<Chapter>>

    @Query("SELECT * FROM chapters WHERE id = :id LIMIT 1")
    suspend fun getChapterById(id: Int): Chapter?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChapter(chapter: Chapter): Long

    @Update
    suspend fun updateChapter(chapter: Chapter)

    @Delete
    suspend fun deleteChapter(chapter: Chapter)

    // Homework Queries
    @Query("SELECT * FROM homeworks")
    fun getAllHomework(): Flow<List<Homework>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHomework(homework: Homework): Long

    @Update
    suspend fun updateHomework(homework: Homework)

    @Delete
    suspend fun deleteHomework(homework: Homework)

    // Exam Queries
    @Query("SELECT * FROM exams")
    fun getAllExams(): Flow<List<Exam>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExam(exam: Exam): Long

    @Update
    suspend fun updateExam(exam: Exam)

    @Delete
    suspend fun deleteExam(exam: Exam)

    // Spaced Repetition Queries
    @Query("SELECT * FROM spaced_repetitions")
    fun getAllSpacedRepetitions(): Flow<List<SpacedRepetition>>

    @Query("SELECT * FROM spaced_repetitions WHERE isCompleted = 0")
    fun getPendingSpacedRepetitions(): Flow<List<SpacedRepetition>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSpacedRepetition(rep: SpacedRepetition): Long

    @Update
    suspend fun updateSpacedRepetition(rep: SpacedRepetition)

    @Delete
    suspend fun deleteSpacedRepetition(rep: SpacedRepetition)

    // Recurrent Skills Queries
    @Query("SELECT * FROM recurrent_skills")
    fun getAllRecurrentSkills(): Flow<List<RecurrentSkill>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecurrentSkill(skill: RecurrentSkill): Long

    @Update
    suspend fun updateRecurrentSkill(skill: RecurrentSkill)

    @Delete
    suspend fun deleteRecurrentSkill(skill: RecurrentSkill)

    // User Profile Queries
    @Query("SELECT * FROM user_profiles WHERE id = 1 LIMIT 1")
    fun getUserProfileFlow(): Flow<UserProfile?>

    @Query("SELECT * FROM user_profiles WHERE id = 1 LIMIT 1")
    suspend fun getUserProfileDirect(): UserProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfile(profile: UserProfile): Long

    // Study Session Log Queries
    @Query("SELECT * FROM study_sessions ORDER BY timestamp DESC")
    fun getAllStudySessions(): Flow<List<StudySessionLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudySessionLog(log: StudySessionLog): Long
}
