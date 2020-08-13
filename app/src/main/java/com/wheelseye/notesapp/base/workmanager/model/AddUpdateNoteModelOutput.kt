package com.wheelseye.notesapp.base.workmanager.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class AddUpdateNoteModelOutput(
    @Expose
    @SerializedName("userNote")
    val userNotes: ArrayList<NoteIdMapModel>
)
