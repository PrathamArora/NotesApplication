package com.wheelseye.notesapp.home.model.callback

interface IAllNotesCallback {
    fun updateAllNotes()

    fun updateSingleNote(position: Int)

    fun updateLogoutInfo(isLoggedOut: Boolean, workManagerTag: String)
}