package com.wheelseye.notesapp.db.entity

import com.wheelseye.notesapp.crudNotes.model.service.NoteModel
import com.wheelseye.notesapp.login.model.UserNotesModel
import com.wheelseye.notesapp.utility.NoteLabel
import com.wheelseye.notesapp.utility.Utility
import java.io.Serializable

class UserNotes : Serializable {

    var userID: Long? = null
    var userNotes: ArrayList<Note>? = null

    companion object {
        fun getUserNotesFromModel(userNotesModel: UserNotesModel?): UserNotes {
            val userNotes = UserNotes()


            userNotes.userID = userNotesModel?.userID?.toLong() ?: 1
            userNotes.userNotes = getUserNotesList(userNotesModel?.userNotes)
            return userNotes
        }

        private fun getUserNotesList(userNotesModelList: ArrayList<NoteModel>?): ArrayList<Note> {
            val userNotesList = ArrayList<Note>()
            userNotesModelList?.let {
                for (i in 0 until it.size) {
                    userNotesList.add(
                        Note(
                            serverNotesID = it[i].notesID?.toLong() ?: i.toLong(),
                            title = it[i].title ?: "",
                            message = it[i].message ?: "",
                            label = it[i].label ?: NoteLabel.getDefaultKey(),
                            date = it[i].date ?: Utility.getCurrentDate()
                        )
                    )
                }
            }
            return userNotesList
        }
    }
}