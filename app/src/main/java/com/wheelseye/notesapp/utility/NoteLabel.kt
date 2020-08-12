package com.wheelseye.notesapp.utility

import com.wheelseye.notesapp.R
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

object NoteLabel {
    // Label String versus Color and Drawable
    private val noteLabelMap = HashMap<String, Pair<Int, Int>>()
    private val idLabelMap = HashMap<Int, String>()

    private const val WORK = "Work"
    private const val SELF = "Self"
    private const val OTHER = "Other"
    private const val DEFAULT_LABEL = OTHER

    private const val SELF_INT = 1
    private const val WORK_INT = SELF_INT + 1
    private const val OTHER_INT = WORK_INT + 1
    private const val DEFAULT_INT = OTHER_INT


    init {
        noteLabelMap[SELF] = Pair(R.color.self_note, R.drawable.self_note)
        noteLabelMap[WORK] = Pair(R.color.work_note, R.drawable.work_note)
        noteLabelMap[OTHER] = Pair(R.color.other_note, R.drawable.other_note)

        idLabelMap[SELF_INT] = SELF
        idLabelMap[WORK_INT] = WORK
        idLabelMap[OTHER_INT] = OTHER
    }

    fun getColorAndDrawable(key: Int?): Pair<Int, Int> {

        val noteKey = key ?: OTHER_INT
        val noteKeyStr = idLabelMap[noteKey]
        return noteLabelMap[noteKeyStr]!!
//        return if (key.isNullOrEmpty() || !noteLabelMap.containsKey(key.toLowerCase(Locale.getDefault()))) {
//            noteLabelMap[getDefaultLabel()]!!
//        } else {
//            noteLabelMap[key.toLowerCase(Locale.getDefault())]!!
//        }
    }

    fun getLabel(key: Int?): String {
        return if (key == null) {
            getDefaultLabel()
        } else {
            idLabelMap[key].toString()
        }
    }

    fun getAllLabelKeys(): ArrayList<Int> {
        val allLabelKeys = ArrayList<Int>()
        allLabelKeys.addAll(idLabelMap.keys)
        return allLabelKeys
    }

    fun outOfLimits(labelKey: Int?): Boolean {
        if (labelKey == null) return true

        val maxLimit = maxOf(SELF_INT, WORK_INT, OTHER_INT)
        val minLimit = minOf(SELF_INT, WORK_INT, OTHER_INT)
        return labelKey !in minLimit..maxLimit
    }

    fun getAllLabels(): ArrayList<String> {
        val allLabels = ArrayList<String>()
        allLabels.addAll(noteLabelMap.keys)
        return allLabels
    }

    fun getDefaultLabel(): String {
        return idLabelMap[DEFAULT_INT].toString()
    }

    fun getDefaultKey(): Int {
        return DEFAULT_INT
    }
}