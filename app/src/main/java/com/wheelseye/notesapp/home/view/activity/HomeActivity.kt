package com.wheelseye.notesapp.home.view.activity

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.navigation.NavigationView
import com.wheelseye.notesapp.R
import com.wheelseye.notesapp.base.activity.BaseActivity
import com.wheelseye.notesapp.base.view.bottomsheet.ChooseLabelBottomSheet
import com.wheelseye.notesapp.crudNotes.view.AlterNoteActivity
import com.wheelseye.notesapp.db.entity.Note
import com.wheelseye.notesapp.home.model.callback.IUpdateNotesCallback
import com.wheelseye.notesapp.home.view.adapter.NotesAdapter
import com.wheelseye.notesapp.home.view.motion.ItemTouchHelperCallback
import com.wheelseye.notesapp.home.view.motion.StartDragListener
import com.wheelseye.notesapp.home.viewmodel.HomeViewModel
import com.wheelseye.notesapp.login.view.LoginActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.progress_bar_fullscreen.*

class HomeActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener,
    IUpdateNotesCallback, StartDragListener, ChooseLabelBottomSheet.BottomSheetListener {

    private var navTvEmailId: TextView? = null
    private var mHomeViewModel: HomeViewModel? = null
    private var mNotesAdapter: NotesAdapter? = null
    private var itemTouchHelper: ItemTouchHelper? = null
    private val allNotesList = ArrayList<Note>()
    private val filteredNotesList = ArrayList<Note>()
    private var chooseLabelBottomSheet: ChooseLabelBottomSheet? = null
    private var toBeEditedNote: Note? = null
    private var currentFilterLabel = LABEL_ALL_INT


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        supportActionBar?.title = resources.getString(R.string.home)
        checkForValidUser(this)

        initViews()
        initRecyclerView()
        initViewModel()
        setObservers()
        setUserNavData()
        initListeners()
    }

    private fun initListeners() {
        fabAddNote.setOnClickListener {
            val intent = Intent(this, AlterNoteActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initRecyclerView() {

        manageVisibility()

        mNotesAdapter = NotesAdapter(filteredNotesList, this, this, this)
        var itemsPerRow = PORTRAIT_NOTES
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)
            itemsPerRow = LANDSCAPE_NOTES

        rvAllNotes.layoutManager =
            StaggeredGridLayoutManager(itemsPerRow, StaggeredGridLayoutManager.VERTICAL)

        val itemTouchHelperCallback = ItemTouchHelperCallback(mNotesAdapter!!)
        itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper?.attachToRecyclerView(rvAllNotes)
        rvAllNotes.adapter = mNotesAdapter
    }

    private fun manageVisibility() {
        if (filteredNotesList.isNullOrEmpty()) {
            imgNoNotes.visibility = View.VISIBLE
            rvAllNotes.visibility = View.GONE
        } else {
            imgNoNotes.visibility = View.GONE
            rvAllNotes.visibility = View.VISIBLE
        }
    }

    private fun initViews() {
        navView.setNavigationItemSelectedListener(this)
        navView.bringToFront()
        navView.itemIconTintList = null
        val toggle =
            ActionBarDrawerToggle(this, drawerLayout!!, toolbar!!, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.isDrawerIndicatorEnabled = true
        toggle.syncState()

        val headerView = navView.getHeaderView(0)
        navTvEmailId = headerView.findViewById(R.id.tvEmailID)
        navView.menu.getItem(1).subMenu.getItem(0).isChecked = true
    }

    private fun setUserNavData() {
        val sharedPreference = getSharedPreferences(USER_DETAILS_SHARED_PREF, Context.MODE_PRIVATE)
        navTvEmailId?.text = sharedPreference.getString(USER_EMAIL_ID, GUEST_USER)
    }


    override fun initViewModel() {
        mHomeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
    }

    override fun setObservers() {
        mHomeViewModel?.isUpdating()?.observe(this, Observer {
            if (it.first) {
                pbLayout.visibility = View.VISIBLE
                pbText.text = it.second
            } else {
                pbLayout.visibility = View.GONE
                pbText.text = it.second
            }
        })

        mHomeViewModel?.init(this)

        mHomeViewModel?.getAllNotes()
            ?.observe(this, Observer {
                allNotesList.clear()
                allNotesList.addAll(it)
                filterData()
                mNotesAdapter?.notifyDataSetChanged()
            })


        mHomeViewModel?.getSingleNoteChange()?.observe(this, Observer {
            val editedNote = allNotesList[it]
            val indexInFilteredData = filteredNotesList.indexOf(editedNote)
            if (indexInFilteredData != -1) {
                mNotesAdapter?.notifyItemChanged(it)
                filterData()
            }
        })

        mHomeViewModel?.isLogoutSuccessful()?.observe(this, Observer {
            if (it.first) {
                stopWorkManager(it.second)
            }
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        })
    }

    private fun filterData() {
        filteredNotesList.clear()
        if (currentFilterLabel == LABEL_ALL_INT) {
            filteredNotesList.addAll(allNotesList)
        } else {
            for (i in 0 until allNotesList.size) {
                if (allNotesList[i].label == currentFilterLabel) {
                    filteredNotesList.add(allNotesList[i])
                }
            }
        }
        mNotesAdapter?.notifyDataSetChanged()
        manageVisibility()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menuAddNotes -> {
                val intent = Intent(this, AlterNoteActivity::class.java)
                startActivity(intent)
            }
            R.id.menuAllNotes -> {
                currentFilterLabel = LABEL_ALL_INT
                filterData()
            }
            R.id.menuSelfNotes -> {
                navView.menu.getItem(1).subMenu.getItem(0).isChecked = false
                currentFilterLabel = LABEL_SELF_INT
                filterData()
            }
            R.id.menuWorkNotes -> {
                navView.menu.getItem(1).subMenu.getItem(0).isChecked = false
                currentFilterLabel = LABEL_WORK_INT
                filterData()
            }
            R.id.menuOtherNotes -> {
                navView.menu.getItem(1).subMenu.getItem(0).isChecked = false
                currentFilterLabel = LABEL_OTHER_INT
                filterData()
            }
            R.id.menuLogout -> {
                mHomeViewModel?.logout(this)
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun deleteCurrentNote(note: Note) {

        val builder = AlertDialog.Builder(this)

        builder.setTitle("Delete")
            .setMessage("Do you want to delete this note from " + note.date + "?")
            .setCancelable(false)
            .setPositiveButton(
                STRING_YES
            ) { _, _ ->
                mHomeViewModel?.deleteNote(note)
            }
            .setNegativeButton(
                STRING_NO
            ) { _, _ -> }
            .show()
    }

    override fun updateLabelCurrentNote(note: Note) {
        toBeEditedNote = note
        chooseLabelBottomSheet =
            ChooseLabelBottomSheet()
        chooseLabelBottomSheet?.show(supportFragmentManager, CHOOSE_LABEL_TAG)
    }

    override fun onLabelSelected(labelKey: Int) {
        chooseLabelBottomSheet?.dismiss()
        mHomeViewModel?.updateLabel(toBeEditedNote, labelKey)
    }

    override fun requestDrag(viewHolder: RecyclerView.ViewHolder) {
        itemTouchHelper?.startDrag(viewHolder)
    }
}