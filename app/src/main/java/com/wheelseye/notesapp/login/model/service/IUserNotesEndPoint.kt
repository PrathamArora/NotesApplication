package com.wheelseye.notesapp.login.model.service

import com.wheelseye.notesapp.base.api.GenericAPIModel
import com.wheelseye.notesapp.login.model.UserNotesModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface IUserNotesEndPoint {

//    @POST("/loginUser")
//    fun getUserNotes(@Body emailID: String): Call<GenericAPIModel<UserNotesModel>>

    @GET("/loginUser")
    fun getUserNotes(@Query("emailid") emailID: String): Call<GenericAPIModel<UserNotesModel>>

}