package com.wheelseye.notesapp.crudNotes.model.service

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class NoteModel(
    @Expose
    @SerializedName("notesID")
    val notesID: Int?,

    @Expose
    @SerializedName("title")
    var title: String?,

    @Expose
    @SerializedName("message")
    var message: String?,

    @Expose
    @SerializedName("date")
    var date: String?,

    @Expose
    @SerializedName("label")
    var label: Int?
) : Serializable