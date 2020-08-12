package com.wheelseye.notesapp.base.view.bottomsheet

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.wheelseye.notesapp.R
import com.wheelseye.notesapp.utility.NoteLabel

class LabelAdapter(
    private val labelsList: ArrayList<Int>,
    private val context: Context,
    private val bottomSheetListener: ChooseLabelBottomSheet.BottomSheetListener
) :
    RecyclerView.Adapter<LabelAdapter.LabelViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LabelViewHolder {
        return LabelViewHolder(
            LayoutInflater.from(context).inflate(R.layout.card_select_label, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return labelsList.size
    }

    override fun onBindViewHolder(holder: LabelViewHolder, position: Int) {
        holder.initLabelDetails(context, labelsList[position], bottomSheetListener)
    }

    class LabelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var imgLabelColor: ImageView? = null
        private var tvLabelTag: TextView? = null
        private var cardLabelContainer: CardView? = null

        init {
            imgLabelColor = itemView.findViewById(R.id.imgLabelColor)
            tvLabelTag = itemView.findViewById(R.id.tvLabelTag)
            cardLabelContainer = itemView.findViewById(R.id.cardLabelContainer)
        }

        fun initLabelDetails(
            context: Context,
            labelKey: Int,
            bottomSheetListener: ChooseLabelBottomSheet.BottomSheetListener
        ) {
            tvLabelTag?.text = NoteLabel.getLabel(labelKey)
            val colorDrawablePair = NoteLabel.getColorAndDrawable(labelKey)
            imgLabelColor?.setBackgroundColor(
                context.resources.getColor(
                    colorDrawablePair.first,
                    null
                )
            )

            setListeners(bottomSheetListener, labelKey)
        }

        private fun setListeners(
            bottomSheetListener: ChooseLabelBottomSheet.BottomSheetListener,
            labelKey: Int
        ) {
            cardLabelContainer?.setOnClickListener {
                bottomSheetListener.onLabelSelected(labelKey)
            }
        }
    }


}