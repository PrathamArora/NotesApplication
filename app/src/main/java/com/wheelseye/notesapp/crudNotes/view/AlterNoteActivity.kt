package com.wheelseye.notesapp.crudNotes.view

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.wheelseye.notesapp.R
import com.wheelseye.notesapp.base.activity.BaseActivity
import com.wheelseye.notesapp.base.view.bottomsheet.ChooseLabelBottomSheet
import com.wheelseye.notesapp.crudNotes.model.service.NoteModel
import com.wheelseye.notesapp.crudNotes.viewmodel.AlterNoteViewModel
import com.wheelseye.notesapp.db.entity.Note
import com.wheelseye.notesapp.utility.NoteLabel
import com.wheelseye.notesapp.utility.Utility
import kotlinx.android.synthetic.main.activity_alter_note.*
import kotlinx.android.synthetic.main.progress_bar_fullscreen.*

class AlterNoteActivity : BaseActivity(),
    ChooseLabelBottomSheet.BottomSheetListener {

    private var isEditMode = false
    private var mAlterNoteViewModel: AlterNoteViewModel? = null
    private var noteID: Long = -1
    private var noteLabel = NoteLabel.getDefaultKey()
    private var note: Note? = null
    private var chooseLabelBottomSheet: ChooseLabelBottomSheet? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alter_note)
        setSupportActionBar(toolbar)

        initView()
        setData()
        initViewModel()
        initListeners()
        setObservers()
    }

    private fun initView() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        if (intent.hasExtra(SINGLE_NOTE_KEY) && intent.getSerializableExtra(SINGLE_NOTE_KEY) != null) {
            isEditMode = true
            supportActionBar?.title = "Edit Note"
            btnSave.text = "Save"
            note = intent.getSerializableExtra(SINGLE_NOTE_KEY) as Note
        } else {
            supportActionBar?.title = "Add Note"
            btnSave.text = "Add"
            note = Note(
                serverNotesID = noteID,
                title = "",
                message = "",
                date = Utility.getCurrentDate(),
                label = noteLabel
            )
        }
    }

    private fun initListeners() {
        btnSave.setOnClickListener {
            hideKeyboard()
            checkAndSave()
        }

        imgNoteLabel.setOnClickListener {
            chooseLabelBottomSheet =
                ChooseLabelBottomSheet()
            chooseLabelBottomSheet?.show(supportFragmentManager, "Choose Label")
        }
    }

    private fun checkAndSave() {
        val title = etTitle.text.toString().trim()
        val message = etMessage.text.toString().trim()

        if (title.isEmpty() && message.isEmpty()) {
            Snackbar.make(container, "Unable to save empty note.", Snackbar.LENGTH_SHORT).show()
            return
        }

        note?.title = title
        note?.message = message

        if (isEditMode) {
            mAlterNoteViewModel?.updateNote(note!!)
        } else {
            mAlterNoteViewModel?.addNote(note!!)
        }
    }

    private fun setData() {
        etTitle.setText(note?.title.toString())
        etMessage.setText(note?.message.toString())
        noteID = note?.notesID ?: noteID
        noteLabel = note?.label ?: noteLabel
        manageLabel(imgNoteLabel, this, note!!)
    }

    override fun initViewModel() {
        mAlterNoteViewModel = ViewModelProvider(this).get(AlterNoteViewModel::class.java)
        mAlterNoteViewModel?.init(this)
    }

    override fun setObservers() {
        mAlterNoteViewModel?.isUpdating()?.observe(this, Observer {
            if (it.first) {
                pbLayout.visibility = View.VISIBLE
                pbText.text = it.second
            } else {
                pbLayout.visibility = View.GONE
                pbText.text = it.second
            }
        })

        mAlterNoteViewModel?.isNoteAlteredSuccessfully()?.observe(this, Observer {
            if (it) {
                Toast.makeText(this, "Note Saved.", Toast.LENGTH_LONG).show()
                finish()
            } else {
                if (isEditMode)
                    Snackbar.make(container, "Unable to update note.", Snackbar.LENGTH_LONG).show()
                else
                    Snackbar.make(container, "Unable to add note.", Snackbar.LENGTH_LONG).show()
            }
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onLabelSelected(labelKey: Int) {
        chooseLabelBottomSheet?.dismiss()
        updateLabel(labelKey)
    }

    private fun updateLabel(newLabelKey: Int) {
        note?.label = newLabelKey
        manageLabel(imgNoteLabel, this, note!!)
    }

}