package com.wheelseye.notesapp.db.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.wheelseye.notesapp.base.workmanager.model.NoteIdMapModel
import com.wheelseye.notesapp.base.workmanager.model.NoteServerModel
import com.wheelseye.notesapp.db.dao.NoteDao
import com.wheelseye.notesapp.db.database.NotesRoomDatabase
import com.wheelseye.notesapp.db.entity.Note

class NotesRepository(context: Context) {
    private var noteDao: NoteDao? = null

    init {
        noteDao = NotesRoomDatabase.getDatabase(context.applicationContext).noteDao()
    }

    var allNotes = noteDao?.getAllNotes()


    suspend fun insert(note: Note) {
        noteDao?.insertNote(note)
    }

    suspend fun delete() {
        noteDao?.deleteAllNotes()
    }

    suspend fun deleteNoteById(id: Long) {
        noteDao?.deleteNoteById(id)
    }

    suspend fun updateLabelById(labelKey: Int, notesID: Long) {
        noteDao?.updateLabelById(labelKey, notesID)
    }

    suspend fun update(
        notesID: Long,
        title: String,
        message: String,
        currentDate: String,
        label: Int
    ) {
        noteDao?.updateNoteById(notesID, title, message, currentDate, label)
    }

    fun getAllNewNotes(): List<NoteServerModel>? {
        return noteDao?.getAllNewNotes()
    }

    fun updateNewNotesServerNotesID(appNotesID: Long, serverNotesID: Long) {
        noteDao?.updateNewNotesServerNotesID(appNotesID, serverNotesID)
    }

    fun getAllEditedNotes(): List<NoteServerModel>? {
        return noteDao?.getAllEditedNotes()
    }

    fun updateEditedNotes(appNotesID: Long, serverNotesID: Long) {
        noteDao?.updateEditedNotes(appNotesID, serverNotesID)
    }

    fun getAllDeletedNotes(): List<NoteIdMapModel>? {
        return noteDao?.getAllDeletedNotes()
    }

    fun updateDeletedNotes(appNotesID: Long) {
        noteDao?.updateDeletedNotes(appNotesID)
    }

    fun getAllNotesList(): List<Note>? {
        return noteDao?.getListNotes()
    }

}