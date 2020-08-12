package com.wheelseye.notesapp.home.view.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.wheelseye.notesapp.R
import com.wheelseye.notesapp.base.activity.BaseActivity
import com.wheelseye.notesapp.crudNotes.model.service.NoteModel
import com.wheelseye.notesapp.crudNotes.view.AlterNoteActivity
import com.wheelseye.notesapp.db.entity.Note
import com.wheelseye.notesapp.home.model.repository.IUpdateNotesCallback
import com.wheelseye.notesapp.home.view.motion.ItemTouchHelperAdapter
import com.wheelseye.notesapp.home.view.motion.StartDragListener
import com.wheelseye.notesapp.home.viewmodel.HomeViewModel
import java.util.*

class NotesAdapter(
    private val mNoteModelList: ArrayList<Note>,
    private val context: Context,
    private val iUpdateNotesCallback: IUpdateNotesCallback,
    private val startDragListener: StartDragListener
) : RecyclerView.Adapter<NotesAdapter.NoteViewHolder>(), ItemTouchHelperAdapter {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        return NoteViewHolder(
            LayoutInflater.from(context).inflate(R.layout.card_note, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return mNoteModelList.size
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.initNoteDetails(
            context,
            mNoteModelList[position],
            iUpdateNotesCallback,
            startDragListener
        )
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {

//        if (fromPosition < 0 || toPosition < 0) {
//            return false
//        }

        try {
//            if (fromPosition == 0 || fromPosition == mNoteModelList.size - 1) {
//                Collections.swap(mNoteModelList, fromPosition, toPosition)
//                notifyItemMoved(fromPosition, toPosition)
//                return true
//            }

            if (fromPosition < toPosition) {
                Log.d(BaseActivity.TAG, "fromPosition < toPosition")
                Log.d(BaseActivity.TAG, "$fromPosition to $toPosition")
                for (i in fromPosition until toPosition step 1) {
                    Log.d(BaseActivity.TAG, "$i")
                    Collections.swap(mNoteModelList, i, i + 1)
                }
            } else {
                Log.d(BaseActivity.TAG, "fromPosition >= toPosition")
                Log.d(BaseActivity.TAG, "$fromPosition to $toPosition")
                for (i in fromPosition downTo toPosition + 1 step 1) {
                    Log.d(BaseActivity.TAG, "$i")
                    Collections.swap(mNoteModelList, i, i - 1)
                }
            }
            this.notifyItemMoved(fromPosition, toPosition)
            return true
        } catch (e: Exception) {
            return false
        }
    }

    override fun onItemDismiss(position: Int) {
        iUpdateNotesCallback.deleteCurrentNote(mNoteModelList[position])
    }


    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var noteTitle: TextView? = null
        private var noteMessage: TextView? = null
        private var noteDate: TextView? = null
        private var noteID: Long? = null
        private var cardNote: CardView? = null
        private var tvPopupMenu: TextView? = null
        private var imgNoteLabel: ImageView? = null
//        private var imgDelete: ImageView? = null

        init {
            noteTitle = itemView.findViewById(R.id.tvNoteTitle)
            noteMessage = itemView.findViewById(R.id.tvNoteMessage)
            noteDate = itemView.findViewById(R.id.tvNoteDate)
            cardNote = itemView.findViewById(R.id.cardNote)
            tvPopupMenu = itemView.findViewById(R.id.tvPopupMenu)
            imgNoteLabel = itemView.findViewById(R.id.imgNoteLabel)
//            imgDelete = itemView.findViewById(R.id.imgDelete)
        }

        fun initNoteDetails(
            context: Context,
            note: Note,
            iUpdateNotesCallback: IUpdateNotesCallback,
            startDragListener: StartDragListener
        ) {
            noteTitle?.text = note.title
            noteMessage?.text = note.message
            noteDate?.text = note.date
            noteID = note.notesID

            manageHeading(note)
            manageLabel(context, note)
            setListeners(context, note, iUpdateNotesCallback, startDragListener)
        }

        private fun manageLabel(context: Context, note: Note) {
            BaseActivity.manageLabel(imgNoteLabel, context, note)
        }

        private fun setListeners(
            context: Context,
            note: Note,
            iUpdateNotesCallback: IUpdateNotesCallback,
            startDragListener: StartDragListener
        ) {
            cardNote?.setOnClickListener {
                val intent = Intent(context, AlterNoteActivity::class.java)
                intent.putExtra(BaseActivity.SINGLE_NOTE_KEY, note)
                context.startActivity(intent)
            }

            cardNote?.setOnLongClickListener {
                startDragListener.requestDrag(this@NoteViewHolder)
                true
            }

            tvPopupMenu?.setOnClickListener {
                val menu = PopupMenu(context, it)
                menu.inflate(R.menu.popup_menu)
                menu.setOnMenuItemClickListener { menuItem ->
                    when (menuItem?.itemId) {
                        R.id.menuItemDelete -> {
                            iUpdateNotesCallback.deleteCurrentNote(note)
                        }
                        R.id.menuItemChangeLabel -> {
                            iUpdateNotesCallback.updateLabelCurrentNote(note)
                        }
                    }
                    false
                }
                menu.show()
            }

//            imgDelete?.setOnClickListener {
//                iDeleteNotesCallback.deleteCurrentNote(noteModel)
//            }
        }

        private fun manageHeading(note: Note) {
            if (note.title.trim().isEmpty()) {
                noteTitle?.visibility = View.GONE
            } else {
                noteTitle?.visibility = View.VISIBLE
            }

            if (note.message.trim().isEmpty()) {
                noteMessage?.visibility = View.GONE
            } else {
                noteMessage?.visibility = View.VISIBLE
            }
        }

    }
}