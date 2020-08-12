package com.wheelseye.notesapp.home.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wheelseye.notesapp.base.activity.BaseActivity
import com.wheelseye.notesapp.db.entity.Note
import com.wheelseye.notesapp.home.model.repository.HomeRepository
import com.wheelseye.notesapp.home.model.repository.IAllNotesCallback
import com.wheelseye.notesapp.utility.NoteLabel

class HomeViewModel : ViewModel(), IAllNotesCallback {
    private val mIsUpdating = MutableLiveData<Pair<Boolean, String>>()
    private val mHomeRepository = HomeRepository()
    private val mUserNotesLiveData = MutableLiveData<List<Note>>()
    private var mSingleNotePosition = MutableLiveData<Int>()
    private val mIsLogoutSuccessful = MutableLiveData<Pair<Boolean, String>>()

    fun init(
        context: Context
    ) {
        mIsUpdating.value = Pair(BaseActivity.SHOW_LOADER, "Initializing...")
        mHomeRepository.init(context)
        mIsUpdating.value = Pair(BaseActivity.HIDE_LOADER, "")
    }


    fun isUpdating(): LiveData<Pair<Boolean, String>> {
        return mIsUpdating
    }

    fun isLogoutSuccessful(): LiveData<Pair<Boolean, String>> {
        return mIsLogoutSuccessful
    }

    fun getAllNotes(): LiveData<List<Note>>? {
        return mHomeRepository.getAllNotes()
    }

    fun getSingleNoteChange(): LiveData<Int> {
        return mSingleNotePosition
    }

    override fun updateAllNotes() {
        mIsUpdating.postValue(Pair(BaseActivity.HIDE_LOADER, ""))
        mUserNotesLiveData.postValue(getAllNotes()?.value)
    }

    override fun updateSingleNote(position: Int) {
        mIsUpdating.postValue(Pair(BaseActivity.HIDE_LOADER, ""))
        mSingleNotePosition.postValue(position)
    }

    override fun updateLogoutInfo(isLoggedOut: Boolean, workManagerTag: String) {
        mIsUpdating.postValue(Pair(BaseActivity.HIDE_LOADER, ""))
        mIsLogoutSuccessful.postValue(Pair(isLoggedOut, workManagerTag))
    }

    fun deleteNote(note: Note) {
        mIsUpdating.value = Pair(BaseActivity.SHOW_LOADER, "Deleting Note")
        mHomeRepository.deleteNote(note, this)
    }

    fun syncNotes(context: Context) {
        mIsUpdating.value = Pair(BaseActivity.SHOW_LOADER, "Updating...")
        mHomeRepository.syncNotes(context, this)
    }

    fun updateLabel(note: Note?, labelKey: Int) {
        if (note == null)
            return
        mIsUpdating.value =
            Pair(BaseActivity.SHOW_LOADER, "Updating Label to " + NoteLabel.getLabel(labelKey))
        mHomeRepository.updateLabel(note, labelKey, this)
    }

    fun logout(context: Context) {
        mIsUpdating.value = Pair(BaseActivity.SHOW_LOADER, "Logging Out")
        mHomeRepository.logout(context, this)
    }

}