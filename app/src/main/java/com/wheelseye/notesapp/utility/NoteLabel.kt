package com.wheelseye.notesapp.utility

import com.wheelseye.notesapp.R
import com.wheelseye.notesapp.base.activity.BaseActivity
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

object NoteLabel {
    // Label String versus Color and Drawable
    private val noteLabelMap = HashMap<String, Pair<Int, Int>>()
    private val idLabelMap = HashMap<Int, String>()

    private const val WORK = BaseActivity.LABEL_WORK
    private const val SELF = BaseActivity.LABEL_SELF
    private const val OTHER = BaseActivity.LABEL_OTHER

    private const val SELF_INT = BaseActivity.LABEL_SELF_INT
    private const val WORK_INT = BaseActivity.LABEL_WORK_INT
    private const val OTHER_INT = BaseActivity.LABEL_OTHER_INT
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

    private fun getDefaultLabel(): String {
        return idLabelMap[DEFAULT_INT].toString()
    }

    fun getDefaultKey(): Int {
        return DEFAULT_INT
    }
}