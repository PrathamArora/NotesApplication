package com.wheelseye.notesapp.login

import android.content.Context
import android.content.Intent
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import com.wheelseye.notesapp.R
import com.wheelseye.notesapp.base.activity.BaseActivity
import com.wheelseye.notesapp.login.view.LoginActivity
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginActivityTest {

    private val emailID = "prathamarora@gmail.com"

    @Test
    @DisplayName("Check whether Login activity and its components are visible")
    fun isActivityInView() {

        val openActivity: ActivityTestRule<LoginActivity> =
            ActivityTestRule(LoginActivity::class.java)
        openActivity.launchActivity(Intent())

        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val sharedPref = context.getSharedPreferences(
            BaseActivity.USER_DETAILS_SHARED_PREF,
            Context.MODE_PRIVATE
        )
        if (sharedPref.contains(BaseActivity.USER_ID) && sharedPref.getLong(
                BaseActivity.USER_ID,
                -1
            ) != (-1).toLong()
        ) {
            return
        }

        onView(withId(R.id.container)).check(matches(isDisplayed()))
        onView(withId(R.id.tvWelcome)).check(matches(isDisplayed()))
        onView(withId(R.id.tieEmailID)).check(matches(isDisplayed()))
        onView(withId(R.id.btnLogin)).check(matches(isDisplayed()))
    }

    @Test
    @DisplayName("If the user is logged out, login using 'prathamarora@gmail.com'")
    fun loginIfLoggedOut() {
        val openActivity: ActivityTestRule<LoginActivity> =
            ActivityTestRule(LoginActivity::class.java)
        openActivity.launchActivity(Intent())

        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val sharedPref = context.getSharedPreferences(
            BaseActivity.USER_DETAILS_SHARED_PREF,
            Context.MODE_PRIVATE
        )
        if (!sharedPref.contains(BaseActivity.USER_ID) || sharedPref.getLong(
                BaseActivity.USER_ID,
                -1
            ) == (-1).toLong()
        ) {
            onView(withId(R.id.tieEmailID)).perform(typeText(emailID), closeSoftKeyboard())
            onView(withId(R.id.btnLogin)).perform(click())
            Thread.sleep(10000)
        }

        onView(withId(R.id.fabAddNote)).check(matches(isDisplayed()))
    }
}