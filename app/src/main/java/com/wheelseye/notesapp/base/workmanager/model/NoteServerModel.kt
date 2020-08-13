package com.wheelseye.notesapp.base.workmanager.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class NoteServerModel(
    @Expose
    @SerializedName("notesID")
    val notesID: Long,

    @Expose
    @SerializedName("appNotesID")
    val appNotesID: Long,

    @Expose
    @SerializedName("title")
    var title: String = "",

    @Expose
    @SerializedName("message")
    var message: String = "",

    @Expose
    @SerializedName("date")
    var date: String,

    @Expose
    @SerializedName("label")
    var label: Int
)