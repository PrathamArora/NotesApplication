package com.wheelseye.notesapp.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "UserNotes")
class Note(
    @PrimaryKey(autoGenerate = true)
    var notesID: Long = 0,

    @ColumnInfo(name = "serverNotesID")
    var serverNotesID: Long = -1,

    @ColumnInfo(name = "title")
    var title: String = "",

    @ColumnInfo(name = "message")
    var message: String = "",

    @ColumnInfo(name = "date")
    var date: String,

    @ColumnInfo(name = "label")
    var label: Int,

    @ColumnInfo(name = "isDeleted")
    var isDeleted: Int = 0,

    @ColumnInfo(name = "isEdited")
    var isEdited: Int = 0
) : Serializable