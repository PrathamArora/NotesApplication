package com.wheelseye.notesapp.login.model.repository

import com.wheelseye.notesapp.db.entity.UserNotes

interface ILoginCallback {
    fun updateLoginDetails(isSuccessful: Boolean)
}