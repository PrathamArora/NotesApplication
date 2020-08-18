package com.wheelseye.notesapp.home

import android.view.View
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import com.wheelseye.notesapp.home.view.activity.HomeActivity
import org.hamcrest.Matcher

class HomeActivityCustomAction : ViewAction {
    override fun getDescription(): String {
        return "Custom action for testing labels on notes list"
    }

    override fun getConstraints(): Matcher<View> {
        return isEnabled()
    }

    override fun perform(uiController: UiController?, view: View?) {
        val homeActivity = view as HomeActivity



    }
}