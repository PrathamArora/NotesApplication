package com.wheelseye.notesapp.base.workmanager.service

import com.wheelseye.notesapp.base.api.GenericAPIModel
import com.wheelseye.notesapp.base.workmanager.model.AddUpdateNoteModelInput
import com.wheelseye.notesapp.base.workmanager.model.AddUpdateNoteModelOutput
import com.wheelseye.notesapp.base.workmanager.model.NoteIdMapModel
import com.wheelseye.notesapp.base.workmanager.model.NoteServerModel
import retrofit2.Call
import retrofit2.http.*

interface ISyncNotesEndPoint {

    @POST("/addAllNotes")
    fun addAllNotes(@Body userNotes: AddUpdateNoteModelInput<ArrayList<NoteServerModel>>):
            Call<GenericAPIModel<AddUpdateNoteModelOutput>>

    @PUT("/updateAllNotes")
    fun updateAllNotes(@Body userNotes: AddUpdateNoteModelInput<ArrayList<NoteServerModel>>):
            Call<GenericAPIModel<AddUpdateNoteModelOutput>>

    @HTTP(method = "DELETE", path = "/deleteAllNotes", hasBody = true)
    fun deleteAllNotes(@Body userNotes: AddUpdateNoteModelInput<ArrayList<NoteIdMapModel>>)
            : Call<GenericAPIModel<AddUpdateNoteModelOutput>>
}