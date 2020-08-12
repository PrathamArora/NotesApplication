package com.wheelseye.notesapp.login.model.repository

import android.content.Context
import com.wheelseye.notesapp.base.activity.BaseActivity
import com.wheelseye.notesapp.base.api.GenericAPIModel
import com.wheelseye.notesapp.db.database.NotesRoomDatabase
import com.wheelseye.notesapp.db.entity.Note
import com.wheelseye.notesapp.db.entity.UserNotes
import com.wheelseye.notesapp.db.repository.NotesRepository
import com.wheelseye.notesapp.login.model.UserNotesModel
import com.wheelseye.notesapp.login.model.service.IUserNotesEndPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginRepository {

    private var mUserNotes: UserNotes? = null

    fun performLogin(emailID: String, context: Context, iLoginCallback: ILoginCallback) {

        getUserDetailsAndNotes(iLoginCallback, emailID, context)


//        dummyData(iLoginCallback)
    }

    private fun getUserDetailsAndNotes(
        iLoginCallback: ILoginCallback,
        emailID: String,
        context: Context
    ) {
        val retrofit = Retrofit.Builder()
            .baseUrl(BaseActivity.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val iUserNotesEndPoint = retrofit.create(IUserNotesEndPoint::class.java)

        val callUserNotesModel = iUserNotesEndPoint.getUserNotes(emailID)

        callUserNotesModel.enqueue(
            object : Callback<GenericAPIModel<UserNotesModel>> {
                override fun onFailure(call: Call<GenericAPIModel<UserNotesModel>>, t: Throwable) {
                    iLoginCallback.updateLoginDetails(false)
                }

                override fun onResponse(
                    call: Call<GenericAPIModel<UserNotesModel>>,
                    response: Response<GenericAPIModel<UserNotesModel>>
                ) {
                    if (response.body() == null || !response.isSuccessful || response.body()?.status != 200) {
                        iLoginCallback.updateLoginDetails(false)
                        return
                    }
                    mUserNotes = UserNotes.getUserNotesFromModel(response.body()?.data)
                    GlobalScope.launch {
                        saveUser(context)
                        dropAndAddNotes(context)
                        iLoginCallback.updateLoginDetails(true)
                    }
                }

                private fun saveUser(context: Context) {
                    val sharedPreference = context.getSharedPreferences(
                        BaseActivity.USER_DETAILS_SHARED_PREF,
                        Context.MODE_PRIVATE
                    )
                    val editor = sharedPreference.edit()
                    editor.putLong(BaseActivity.USER_ID, mUserNotes?.userID!!)
                    editor.putString(BaseActivity.USER_EMAIL_ID, emailID.trim())
                    editor.apply()
                }

                private suspend fun dropAndAddNotes(context: Context) {
                    withContext(Dispatchers.IO) {

                        val notesRepository = NotesRepository(context)

                        notesRepository.delete()

                        mUserNotes?.userNotes.let {
                            for (i in 0 until it?.size!!) {
                                val noteForDB = Note(
                                    serverNotesID = it[i].serverNotesID,
                                    title = it[i].title,
                                    message = it[i].message,
                                    label = it[i].label,
                                    date = it[i].date
                                )
                                notesRepository.insert(noteForDB)
                            }
                        }

                    }
                }

            }
        )
    }

//    private fun dummyData(iLoginCallback: ILoginCallback) {
//
//        Handler(Looper.getMainLooper()).postDelayed(
//            {
//                val noteModelList = ArrayList<NoteModel>()
//                for (i in 1..5) {
//                    if (i % 2 == 0) {
//                        noteModelList.add(
//                            NoteModel(
//                                1,
//                                "heading 1 heading 1 heading 1 heading 1 heading 1 heading 1 heading 1 ",
//                                "message 1 message 1 message 1 message 1 message 1 message 1 message 1 ",
//                                "29/07/2020"
//                            )
//                        )
//                    }
//                    noteModelList.add(
//                        NoteModel(i, "heading $i", "message $i", "29/07/2020")
//                    )
//                }
//                val tempUserNotesModel =
//                    UserNotesModel(
//                        1,
//                        noteModelList
//                    )
//                iLoginCallback.updateLoginDetails(tempUserNotesModel)
//            },
//            3000
//        )
//    }

}