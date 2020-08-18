package com.wheelseye.notesapp.db

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.wheelseye.notesapp.base.activity.BaseActivity.Companion.LABEL_WORK_INT
import com.wheelseye.notesapp.db.dao.NoteDao
import com.wheelseye.notesapp.db.database.NotesRoomDatabase
import com.wheelseye.notesapp.db.entity.Note
import com.wheelseye.notesapp.utility.NoteLabel
import com.wheelseye.notesapp.utility.Utility
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.FixMethodOrder
import org.junit.runner.RunWith

import org.junit.jupiter.api.*
import org.junit.runners.MethodSorters

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class DatabaseTest {
    private val title = "Some Title"
    private val message = "Some Message"
    private val notesID: Long = 1
    private val label = NoteLabel.getDefaultKey()
    private val userNote = Note(
        notesID = notesID,
        serverNotesID = -1,
        title = title,
        message = message,
        date = Utility.getCurrentDate(),
        label = label
    )

    companion object {
        private var notesDB: NotesRoomDatabase? = null
        private var noteDao: NoteDao? = null

        @BeforeAll
        fun createDB() {
            if (notesDB == null) {
                val context = InstrumentationRegistry.getInstrumentation().targetContext
                notesDB =
                    Room.inMemoryDatabaseBuilder(context, NotesRoomDatabase::class.java).build()
                noteDao = notesDB?.noteDao()
            }
        }

        @AfterAll
        fun closeDB() {
            notesDB?.close()
        }
    }

    @BeforeEach
    @DisplayName("Flush DB and add a sample note")
    fun setup() = runBlocking {

        if (notesDB == null) {
            val context = InstrumentationRegistry.getInstrumentation().targetContext
            notesDB = Room.inMemoryDatabaseBuilder(context, NotesRoomDatabase::class.java).build()
            noteDao = notesDB?.noteDao()
        }

        noteDao?.deleteAllNotes()
        noteDao?.insertNote(userNote)
        val insertedNote = noteDao?.findNoteById(notesID)
        assertNotNull(insertedNote)
    }


    @Test
    @DisplayName("Check the values of recently added note")
    fun addNoteTest() = runBlocking {
        val insertedNote = noteDao?.findNoteById(notesID)
        assertTrue(userNote.message == insertedNote?.message)
        assertTrue(userNote.title == insertedNote?.title)
        assertTrue(userNote.label == insertedNote?.label)
        assertTrue(userNote.date == insertedNote?.date)
        assertTrue(insertedNote?.isDeleted == 0 && userNote.isDeleted == insertedNote.isDeleted)
        assertTrue(insertedNote?.isEdited == 0 && userNote.isEdited == insertedNote.isEdited)
    }

    @Test
    @DisplayName("Title : Check whether isEdited flag is getting updated")
    fun editNoteTitle() = runBlocking {
        val insertedNote = noteDao?.findNoteById(notesID)
        insertedNote?.title = "Some new title"
        noteDao?.updateNoteById(
            insertedNote?.notesID!!,
            insertedNote.title,
            insertedNote.message,
            Utility.getCurrentDate(),
            insertedNote.label
        )

        val updatedNote = noteDao?.findNoteById(notesID)
        assertEquals(updatedNote?.title, "Some new title")
        assertEquals(updatedNote?.isEdited, 1)
    }

    @Test
    @DisplayName("Message : Check whether isEdited flag is getting updated")
    fun editNoteMessage() = runBlocking {
        val insertedNote = noteDao?.findNoteById(notesID)
        insertedNote?.message = "Some new message"
        noteDao?.updateNoteById(
            insertedNote?.notesID!!,
            insertedNote.title,
            insertedNote.message,
            Utility.getCurrentDate(),
            insertedNote.label
        )

        val updatedNote = noteDao?.findNoteById(notesID)
        assertEquals(updatedNote?.message, "Some new message")
        assertEquals(updatedNote?.isEdited, 1)
    }

    @Test
    @DisplayName("Label : Check whether isEdited flag is getting updated")
    fun editNoteLabel() = runBlocking {
        val insertedNote = noteDao?.findNoteById(notesID)
        insertedNote?.label = LABEL_WORK_INT
        noteDao?.updateNoteById(
            insertedNote?.notesID!!,
            insertedNote.title,
            insertedNote.message,
            Utility.getCurrentDate(),
            insertedNote.label
        )

        val updatedNote = noteDao?.findNoteById(notesID)
        assertEquals(updatedNote?.label, LABEL_WORK_INT)
        assertEquals(updatedNote?.isEdited, 1)
    }

    @Test
    @DisplayName("Delete note in the DB")
    fun deleteNote() = runBlocking {
        val insertedNote = noteDao?.findNoteById(notesID)
        noteDao?.deleteNoteById(insertedNote?.notesID!!)

        val deletedNote = noteDao?.findNoteById(notesID)
        assertEquals(deletedNote?.title, insertedNote?.title)
        assertEquals(deletedNote?.isDeleted, 1)
    }
}