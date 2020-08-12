package com.wheelseye.notesapp.base.api

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class GenericAPIModel<T>(
    @Expose
    @SerializedName("status")
    val status: Int?,

    @Expose
    @SerializedName("message")
    val message: String?,

    @Expose
    @SerializedName("data")
    val data: T?
)