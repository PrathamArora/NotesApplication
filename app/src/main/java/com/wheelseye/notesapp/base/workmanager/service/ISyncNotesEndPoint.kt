package com.wheelseye.notesapp.base.workmanager.service

import com.wheelseye.notesapp.base.api.GenericAPIModel
import com.wheelseye.notesapp.base.workmanager.SyncNotesWorkManager
import com.wheelseye.notesapp.db.entity.Note
import retrofit2.Call
import retrofit2.http.*

interface ISyncNotesEndPoint {

    @POST("/addAllNotes")
    fun addAllNotes(@Body userNotes: SyncNotesWorkManager.AddUpdateNoteModelInput<ArrayList<Note>>):
            Call<GenericAPIModel<SyncNotesWorkManager.AddUpdateNoteModelOutput>>

    @PUT("/updateAllNotes")
    fun updateAllNotes(@Body userNotes: SyncNotesWorkManager.AddUpdateNoteModelInput<ArrayList<Note>>):
            Call<GenericAPIModel<SyncNotesWorkManager.AddUpdateNoteModelOutput>>

    @HTTP(method = "DELETE", path = "/deleteAllNotes", hasBody = true)
    fun deleteAllNotes(@Body userNotes: SyncNotesWorkManager.AddUpdateNoteModelInput<ArrayList<SyncNotesWorkManager.NoteIdMapModel>>)
            : Call<GenericAPIModel<SyncNotesWorkManager.AddUpdateNoteModelOutput>>
}