package com.wheelseye.notesapp.home.model.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.wheelseye.notesapp.base.activity.BaseActivity
import com.wheelseye.notesapp.db.entity.Note
import com.wheelseye.notesapp.db.repository.NotesRepository
import com.wheelseye.notesapp.home.model.callback.IAllNotesCallback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeRepository {

    private var notesRepository: NotesRepository? = null

    fun init(
        context: Context
    ) {
        notesRepository = NotesRepository(context)
    }


    fun getAllNotes(): LiveData<List<Note>>? {
        return notesRepository?.allNotes
    }


    fun deleteNote(note: Note, iAllNotesCallback: IAllNotesCallback) {
        GlobalScope.launch {
            note.isDeleted = 1
            deleteNoteById(note.notesID)
            iAllNotesCallback.updateAllNotes()
        }
    }

    private suspend fun deleteNoteById(notesID: Long) {
        withContext(Dispatchers.IO) {
            notesRepository?.deleteNoteById(notesID)
        }
    }

    fun updateLabel(note: Note, labelKey: Int, iAllNotesCallback: IAllNotesCallback) {
        GlobalScope.launch {
            note.label = labelKey
            val position = notesRepository?.allNotes?.value?.indexOf(note) ?: -1
            if (position != -1) {
                updateLabelById(labelKey, note.notesID)
            }
            iAllNotesCallback.updateSingleNote(position)
        }
    }

    private suspend fun updateLabelById(labelKey: Int, notesID: Long) {
        withContext(Dispatchers.IO) {
            notesRepository?.updateLabelById(labelKey, notesID)
        }
    }


    fun logout(context: Context, iAllNotesCallback: IAllNotesCallback) {
        GlobalScope.launch {
            val userID = context.getSharedPreferences(
                BaseActivity.USER_DETAILS_SHARED_PREF,
                Context.MODE_PRIVATE
            ).getLong(BaseActivity.USER_ID, 1).toString()
            removeUser(context)
            removeUserNotes()
            iAllNotesCallback.updateLogoutInfo(true, userID)
        }
    }

    private suspend fun removeUserNotes() {
        withContext(Dispatchers.IO) {
            notesRepository?.delete()
        }
    }

    private fun removeUser(context: Context) {
        val sharedPreference = context.getSharedPreferences(
            BaseActivity.USER_DETAILS_SHARED_PREF,
            Context.MODE_PRIVATE
        )
        val editor = sharedPreference.edit()
        editor.remove(BaseActivity.USER_ID)
        editor.remove(BaseActivity.USER_EMAIL_ID)
        editor.apply()
    }

}