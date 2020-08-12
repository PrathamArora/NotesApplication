package com.wheelseye.notesapp.crudNotes.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wheelseye.notesapp.base.activity.BaseActivity
import com.wheelseye.notesapp.crudNotes.model.repository.AlterNoteRepository
import com.wheelseye.notesapp.crudNotes.model.service.NoteModel
import com.wheelseye.notesapp.crudNotes.view.INoteAlteredCallback
import com.wheelseye.notesapp.db.entity.Note

class AlterNoteViewModel : ViewModel(), INoteAlteredCallback {

    private val mAlterNoteRepository = AlterNoteRepository()
    private val mIsUpdating = MutableLiveData<Pair<Boolean, String>>()
    private val mIsNoteAlteredSuccessfully = MutableLiveData<Boolean>()

    fun init(context: Context) {
        mIsUpdating.value = Pair(BaseActivity.SHOW_LOADER, "Initializing...")
        mAlterNoteRepository.init(context)
        mIsUpdating.value = Pair(BaseActivity.HIDE_LOADER, "")
    }

    fun addNote(note: Note) {
        mIsUpdating.value = Pair(BaseActivity.SHOW_LOADER, "Adding Note...")
        mAlterNoteRepository.addNote(this, note)
    }

    fun updateNote(note: Note) {
        mIsUpdating.value = Pair(BaseActivity.SHOW_LOADER, "Updating Note...")
        mAlterNoteRepository.updateNote(this, note)
    }

    override fun noteAltered(isSuccessful: Boolean) {
        mIsUpdating.postValue(Pair(BaseActivity.HIDE_LOADER, ""))
        mIsNoteAlteredSuccessfully.postValue(isSuccessful)
    }

    fun isUpdating(): LiveData<Pair<Boolean, String>> {
        return mIsUpdating
    }

    fun isNoteAlteredSuccessfully(): LiveData<Boolean> {
        return mIsNoteAlteredSuccessfully
    }
}