package com.wheelseye.notesapp.home.model.callback

import com.wheelseye.notesapp.db.entity.Note

interface IUpdateNotesCallback {
    fun deleteCurrentNote(note: Note)

    fun updateLabelCurrentNote(note: Note)
}