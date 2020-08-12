package com.wheelseye.notesapp.home.model.repository

import com.wheelseye.notesapp.crudNotes.model.service.NoteModel
import com.wheelseye.notesapp.db.entity.Note

interface IUpdateNotesCallback {
    fun deleteCurrentNote(note: Note)

    fun updateLabelCurrentNote(note: Note)
}