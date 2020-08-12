package com.wheelseye.notesapp.crudNotes.model.repository

import android.content.Context
import com.wheelseye.notesapp.crudNotes.view.INoteAlteredCallback
import com.wheelseye.notesapp.db.database.NotesRoomDatabase
import com.wheelseye.notesapp.db.entity.Note
import com.wheelseye.notesapp.db.repository.NotesRepository
import com.wheelseye.notesapp.utility.Utility
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception

class AlterNoteRepository {

    private var notesRepository: NotesRepository? = null

    fun init(context: Context) {
        notesRepository = NotesRepository(context)
    }

    fun addNote(iNoteAlteredCallback: INoteAlteredCallback, note: Note) {
//        val noteForDB = Note(
//            serverNotesID = noteModel.notesID?.toLong()!!,
//            title = noteModel.title!!,
//            message = noteModel.message!!,
//            label = noteModel.label!!,
//            date = Utility.getCurrentDate()
//        )

        GlobalScope.launch {
            try {
                addNoteToDB(note)
                iNoteAlteredCallback.noteAltered(true)
            } catch (e : Exception){
                iNoteAlteredCallback.noteAltered(false)
            }
        }
    }

    private suspend fun addNoteToDB(note: Note) {
        withContext(Dispatchers.IO) {
            notesRepository?.insert(note)
        }
    }

    fun updateNote(iNoteAlteredCallback: INoteAlteredCallback, note: Note) {
        GlobalScope.launch {
            try{
                updateNoteInDB(note)
                iNoteAlteredCallback.noteAltered(true)
            } catch (e : Exception){
                iNoteAlteredCallback.noteAltered(false)
            }
        }
    }

    private suspend fun updateNoteInDB(note: Note){
        withContext(Dispatchers.IO) {
            notesRepository?.update(note.notesID, note.title, note.message, Utility.getCurrentDate(), note.label)
        }
    }
}