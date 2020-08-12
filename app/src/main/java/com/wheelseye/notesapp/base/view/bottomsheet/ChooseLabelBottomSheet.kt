package com.wheelseye.notesapp.base.view.bottomsheet

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.wheelseye.notesapp.R
import com.wheelseye.notesapp.utility.NoteLabel

class ChooseLabelBottomSheet : BottomSheetDialogFragment() {

    private var bottomSheetListener: BottomSheetListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_choose_label, container, false)
        val rcvAllLabels = view.findViewById<RecyclerView>(R.id.rcvAllLabels)
        val mLabelAdapter =
            LabelAdapter(
                NoteLabel.getAllLabelKeys(),
                context!!,
                bottomSheetListener!!
            )
        rcvAllLabels.layoutManager = LinearLayoutManager(context!!)
        rcvAllLabels.adapter = mLabelAdapter

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (view?.parent as View).setBackgroundColor(Color.TRANSPARENT)
    }

    interface BottomSheetListener {
        fun onLabelSelected(labelKey: Int)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        bottomSheetListener = context as BottomSheetListener
    }


}