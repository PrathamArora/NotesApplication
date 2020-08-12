package com.wheelseye.notesapp.db.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.wheelseye.notesapp.db.dao.NoteDao
import com.wheelseye.notesapp.db.entity.Note

@Database(entities = [Note::class], version = 1, exportSchema = false)
abstract class NotesRoomDatabase : RoomDatabase() {

    abstract fun noteDao(): NoteDao

    companion object {

        private const val DB_NAME = "user_notes.db"

        @Volatile
        private var notesRoomDatabase: NotesRoomDatabase? = null

        fun getDatabase(context: Context): NotesRoomDatabase {

            if (notesRoomDatabase == null) {
                synchronized(this) {
                    if (notesRoomDatabase == null) {
                        notesRoomDatabase = Room.databaseBuilder(
                            context.applicationContext,
                            NotesRoomDatabase::class.java,
                            DB_NAME
                        ).build()
                    }
                }
            }

            return notesRoomDatabase!!
        }
    }
}