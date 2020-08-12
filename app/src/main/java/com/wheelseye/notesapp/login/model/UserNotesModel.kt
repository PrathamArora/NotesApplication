package com.wheelseye.notesapp.login.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.wheelseye.notesapp.crudNotes.model.service.NoteModel
import java.io.Serializable

data class UserNotesModel(
    @Expose
    @SerializedName("userID")
    val userID: Int?,

    @Expose
    @SerializedName("userNotes")
    val userNotes: ArrayList<NoteModel>?
) : Serializable
