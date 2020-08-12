package com.wheelseye.notesapp.home.model.repository

interface IAllNotesCallback {
    fun updateAllNotes()

    fun updateSingleNote(position: Int)

    fun updateLogoutInfo(isLoggedOut: Boolean, workManagerTag: String)
}