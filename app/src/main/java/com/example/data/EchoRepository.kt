package com.example.data

import kotlinx.coroutines.flow.Flow

class EchoRepository(private val dao: EchoDao) {
    val subjects: Flow<List<Subject>> = dao.getAllSubjects()
    val chapters: Flow<List<Chapter>> = dao.getAllChapters()
    val homeworks: Flow<List<Homework>> = dao.getAllHomework()
    val exams: Flow<List<Exam>> = dao.getAllExams()
    val spacedRepetitions: Flow<List<SpacedRepetition>> = dao.getAllSpacedRepetitions()
    val pendingSpacedRepetitions: Flow<List<SpacedRepetition>> = dao.getPendingSpacedRepetitions()
    val recurrentSkills: Flow<List<RecurrentSkill>> = dao.getAllRecurrentSkills()
    val userProfile: Flow<UserProfile?> = dao.getUserProfileFlow()
    val studySessions: Flow<List<StudySessionLog>> = dao.getAllStudySessions()

    fun getChaptersBySubject(subjectId: Int): Flow<List<Chapter>> = dao.getChaptersBySubject(subjectId)

    suspend fun getSubjectById(id: Int): Subject? = dao.getSubjectById(id)
    suspend fun getChapterById(id: Int): Chapter? = dao.getChapterById(id)
    suspend fun getUserProfileDirect(): UserProfile? = dao.getUserProfileDirect()

    suspend fun insertSubject(subject: Subject) { dao.insertSubject(subject) }
    suspend fun deleteSubject(subject: Subject) { dao.deleteSubject(subject) }

    suspend fun insertChapter(chapter: Chapter) { dao.insertChapter(chapter) }
    suspend fun updateChapter(chapter: Chapter) { dao.updateChapter(chapter) }
    suspend fun deleteChapter(chapter: Chapter) { dao.deleteChapter(chapter) }

    suspend fun insertHomework(homework: Homework) { dao.insertHomework(homework) }
    suspend fun updateHomework(homework: Homework) { dao.updateHomework(homework) }
    suspend fun deleteHomework(homework: Homework) { dao.deleteHomework(homework) }

    suspend fun insertExam(exam: Exam) { dao.insertExam(exam) }
    suspend fun updateExam(exam: Exam) { dao.updateExam(exam) }
    suspend fun deleteExam(exam: Exam) { dao.deleteExam(exam) }

    suspend fun insertSpacedRepetition(rep: SpacedRepetition) { dao.insertSpacedRepetition(rep) }
    suspend fun updateSpacedRepetition(rep: SpacedRepetition) { dao.updateSpacedRepetition(rep) }
    suspend fun deleteSpacedRepetition(rep: SpacedRepetition) { dao.deleteSpacedRepetition(rep) }

    suspend fun insertRecurrentSkill(skill: RecurrentSkill) { dao.insertRecurrentSkill(skill) }
    suspend fun updateRecurrentSkill(skill: RecurrentSkill) { dao.updateRecurrentSkill(skill) }
    suspend fun deleteRecurrentSkill(skill: RecurrentSkill) { dao.deleteRecurrentSkill(skill) }

    suspend fun insertUserProfile(profile: UserProfile) { dao.insertUserProfile(profile) }
    suspend fun insertStudySessionLog(log: StudySessionLog) { dao.insertStudySessionLog(log) }
}
