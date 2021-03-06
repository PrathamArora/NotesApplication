package com.wheelseye.notesapp.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.wheelseye.notesapp.base.workmanager.SyncNotesWorkManager
import com.wheelseye.notesapp.db.entity.Note
import kotlinx.coroutines.selects.select

@Dao
interface NoteDao {

    @Query("select * from UserNotes where isDeleted = 0 order by notesID desc")
    fun getAllNotes(): LiveData<List<Note>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertNote(note: Note)

    @Query("delete from UserNotes")
    suspend fun deleteAllNotes()

    @Query("update UserNotes set isDeleted = 1 where notesID = :notesID")
    suspend fun deleteNoteById(notesID: Long)

    @Query("update UserNotes set label = :labelKey, isEdited = 1 where notesID = :notesID")
    suspend fun updateLabelById(labelKey: Int, notesID: Long)

    @Query(
        "update UserNotes set title = :title, message = :message, date = :currentDate, " +
                "label = :label, isEdited = 1 where notesID = :notesID"
    )
    suspend fun updateNoteById(
        notesID: Long,
        title: String,
        message: String,
        currentDate: String,
        label: Int
    )

    @Query("select * from UserNotes where serverNotesID = -1 and isDeleted = 0")
    fun getAllNewNotes(): List<Note>?

    @Query("update UserNotes set serverNotesID = :serverNotesID, isEdited = 0 where notesID = :appNotesID")
    fun updateNewNotesServerNotesID(appNotesID: Long, serverNotesID: Long)

    @Query("select * from UserNotes where serverNotesID != -1 and isDeleted = 0 and isEdited = 1")
    fun getAllEditedNotes(): List<Note>?

    @Query("update UserNotes set isEdited = 0, serverNotesID = :serverNotesID where notesID = :appNotesID")
    fun updateEditedNotes(appNotesID: Long, serverNotesID: Long)

    @Query("select notesID as appNotesID, serverNotesID as notesID from UserNotes where serverNotesID != -1 and isDeleted = 1")
    fun getAllDeletedNotes(): List<SyncNotesWorkManager.NoteIdMapModel>?

    @Query("delete from UserNotes where notesID = :appNotesID")
    fun updateDeletedNotes(appNotesID: Long)
}