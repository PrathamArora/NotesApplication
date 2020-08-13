package com.wheelseye.notesapp.utility

import com.wheelseye.notesapp.base.activity.BaseActivity
import java.text.SimpleDateFormat
import java.util.*

class Utility {

    companion object {
        fun isEmailValid(email: String): Boolean {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        }

        fun getCurrentDate(): String {
            val currentDate = Calendar.getInstance().time
            val dateFormat = SimpleDateFormat(BaseActivity.DATE_FORMAT, Locale.getDefault())
            return dateFormat.format(currentDate)
        }
    }

}