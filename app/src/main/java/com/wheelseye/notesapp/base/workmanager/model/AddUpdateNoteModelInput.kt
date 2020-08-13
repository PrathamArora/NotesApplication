package com.wheelseye.notesapp.base.workmanager.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class AddUpdateNoteModelInput<T>(
    @Expose
    @SerializedName("userID")
    val userID: Long?,

    @Expose
    @SerializedName("userNote")
    val userNotes: T
)