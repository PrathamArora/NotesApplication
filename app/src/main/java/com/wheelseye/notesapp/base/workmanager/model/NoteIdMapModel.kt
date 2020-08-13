package com.wheelseye.notesapp.base.workmanager.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class NoteIdMapModel(
    @Expose
    @SerializedName("appNotesID")
    val appNotesID: Long,

    @Expose
    @SerializedName("notesID")
    val notesID: Long
)
