package com.wheelseye.notesapp.home.model.repository

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import com.wheelseye.notesapp.base.activity.BaseActivity
import com.wheelseye.notesapp.base.api.GenericAPIModel
import com.wheelseye.notesapp.crudNotes.model.service.NoteModel
import com.wheelseye.notesapp.db.database.NotesRoomDatabase
import com.wheelseye.notesapp.db.entity.Note
import com.wheelseye.notesapp.db.entity.UserNotes
import com.wheelseye.notesapp.db.repository.NotesRepository
import com.wheelseye.notesapp.login.model.UserNotesModel
import com.wheelseye.notesapp.login.model.service.IUserNotesEndPoint
import com.wheelseye.notesapp.utility.NoteLabel
import com.wheelseye.notesapp.utility.Utility
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HomeRepository {

    private var allNotesList: ArrayList<Note>? = null
    private var notesRepository: NotesRepository? = null

    fun getAllNotes(context: Context, userID: Int, iAllNotesCallback: IAllNotesCallback) {
        // Retrofit call
        // input - ID
        // output - All notes
    }

    fun init(
        context: Context
    ) {
        notesRepository = NotesRepository(context)
    }


    //    private suspend fun dropAndAddNotes() {
//        withContext(Dispatchers.IO) {
//            notesRepository?.delete()
//
//            for (i in 0 until allNotesList?.size!!) {
//                val noteForDB = Note(
//                    serverNotesID = allNotesList!![i].notesID,
//                    title = allNotesList!![i].title,
//                    message = allNotesList!![i].message,
//                    label = allNotesList!![i].label,
//                    date = allNotesList!![i].date
//                )
//                notesRepository?.insert(noteForDB)
//            }
//        }
//    }
//
    fun getAllNotes(): LiveData<List<Note>>? {
        return notesRepository?.allNotes
    }


    fun deleteNote(note: Note, iAllNotesCallback: IAllNotesCallback) {
        GlobalScope.launch {
            note.isDeleted = 1
            deleteNoteById(note.notesID)
            iAllNotesCallback.updateAllNotes()
        }
    }

    private suspend fun deleteNoteById(notesID: Long) {
        withContext(Dispatchers.IO) {
            notesRepository?.deleteNoteById(notesID)
        }
    }

    fun updateLabel(note: Note, labelKey: Int, iAllNotesCallback: IAllNotesCallback) {
        GlobalScope.launch {
            note.label = labelKey
            val position = notesRepository?.allNotes?.value?.indexOf(note) ?: -1
            if (position != -1) {
                updateLabelById(labelKey, note.notesID)
            }
            iAllNotesCallback.updateSingleNote(position)
        }

//        Handler(Looper.getMainLooper()).postDelayed(
//            {
//                val position = allNotesList?.indexOf(note) ?: -1
//                if (position != -1) {
//                    note.label = labelKey
////                    iAllNotesCallback.updateAllNotes()
//                }
//                iAllNotesCallback.updateSingleNote(position)
//            },
//            2000
//        )
    }

    private suspend fun updateLabelById(labelKey: Int, notesID: Long) {
        withContext(Dispatchers.IO) {
            notesRepository?.updateLabelById(labelKey, notesID)
        }
    }


    fun syncNotes(context: Context, iAllNotesCallback: IAllNotesCallback) {
        val emailID = context.getSharedPreferences(
            BaseActivity.USER_DETAILS_SHARED_PREF,
            Context.MODE_PRIVATE
        ).getString(
            BaseActivity.USER_EMAIL_ID, "guest"
        )!!

        val retrofit = Retrofit.Builder()
            .baseUrl(BaseActivity.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val iUserNotesEndPoint = retrofit.create(IUserNotesEndPoint::class.java)

        val callUserNotesModel = iUserNotesEndPoint.getUserNotes(emailID)

        callUserNotesModel.enqueue(
            object : Callback<GenericAPIModel<UserNotesModel>> {
                override fun onFailure(call: Call<GenericAPIModel<UserNotesModel>>, t: Throwable) {
                    iAllNotesCallback.updateAllNotes()
                }

                override fun onResponse(
                    call: Call<GenericAPIModel<UserNotesModel>>,
                    response: Response<GenericAPIModel<UserNotesModel>>
                ) {
                    if (response.body() == null || !response.isSuccessful || response.body()?.status != 200) {
                        iAllNotesCallback.updateAllNotes()
                        return
                    }
                    if (response.body()?.data != null && !response.body()?.data?.userNotes.isNullOrEmpty()) {
                        allNotesList?.clear()
                        allNotesList?.addAll(UserNotes.getUserNotesList(response.body()?.data?.userNotes))
                    } else {
                        allNotesList = null
                    }
                    iAllNotesCallback.updateAllNotes()
                }
            }
        )

    }


    fun updateNotesPosition(iAllNotesCallback: IAllNotesCallback) {
        Handler(Looper.getMainLooper()).postDelayed(
            {
                iAllNotesCallback.updateAllNotes()
            },
            2000
        )
    }

    fun logout(context: Context, iAllNotesCallback: IAllNotesCallback) {
        GlobalScope.launch {
            val userID = context.getSharedPreferences(
                BaseActivity.USER_DETAILS_SHARED_PREF,
                Context.MODE_PRIVATE
            ).getLong(BaseActivity.USER_ID, 1).toString()
            removeUser(context)
            removeUserNotes()
            iAllNotesCallback.updateLogoutInfo(true, userID)
        }
    }

    private suspend fun removeUserNotes() {
        withContext(Dispatchers.IO) {
            notesRepository?.delete()
        }
    }

    private fun removeUser(context: Context) {
        val sharedPreference = context.getSharedPreferences(
            BaseActivity.USER_DETAILS_SHARED_PREF,
            Context.MODE_PRIVATE
        )
        val editor = sharedPreference.edit()
        editor.remove(BaseActivity.USER_ID)
        editor.remove(BaseActivity.USER_EMAIL_ID)
        editor.apply()
    }

}