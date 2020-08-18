package com.wheelseye.notesapp.home

import android.content.Context
import android.content.Intent
import android.view.Gravity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.contrib.DrawerMatchers.isClosed
import androidx.test.espresso.contrib.NavigationViewActions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import com.wheelseye.notesapp.R
import com.wheelseye.notesapp.base.activity.BaseActivity
import com.wheelseye.notesapp.home.view.activity.HomeActivity
import com.wheelseye.notesapp.home.view.adapter.NotesAdapter
import org.junit.jupiter.api.*
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeActivityTest {

    private var openActivity: ActivityTestRule<HomeActivity>? = null
    private val emailID = "prathamarora@gmail.com"

    @BeforeEach
    fun setup() {
        openActivity =
            ActivityTestRule(HomeActivity::class.java)
        openActivity?.launchActivity(Intent())

        if (!validUser()) {
            onView(withId(R.id.tieEmailID)).perform(
                ViewActions.typeText(emailID),
                ViewActions.closeSoftKeyboard()
            )
            onView(withId(R.id.btnLogin)).perform(click())
            Thread.sleep(10000)
            onView(withId(R.id.fabAddNote)).check(matches(isDisplayed()))
        }
    }

    private fun validUser(): Boolean {
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
            return false
        }
        return true
    }

    @Test
    @DisplayName("Check if user is logged in or not")
    fun isUserLoggedIn() {
        if (!validUser()) {
            fail("User not logged in")
        }
    }

    @Test
    @DisplayName("Check if user has notes")
    fun isUserNotePresent() {
        if (!validUser()) {
            fail("User not logged in")
        } else {
            val notesList = openActivity?.activity?.getFilteredNotesList()
            if (notesList.isNullOrEmpty()) {
                onView(withId(R.id.imgNoNotes)).check(matches(isDisplayed()))
            } else {
                onView(withId(R.id.rvAllNotes)).check(matches(isDisplayed()))
            }
            onView(withId(R.id.fabAddNote)).check(matches(isDisplayed()))
        }
    }

    @Test
    @DisplayName("Navigating to add new note screen")
    fun isNavigatingToNewNoteScreen() {
        if (!validUser()) {
            fail("User not logged in")
        } else {
            onView(withId(R.id.fabAddNote)).perform(click())
            onView(withId(R.id.etTitle)).check(matches(isDisplayed()))
            onView(withId(R.id.imgNoteLabel)).check(matches(isDisplayed()))
            onView(withId(R.id.etMessage)).check(matches(isDisplayed()))
            onView(withId(R.id.btnSave)).check(matches(isDisplayed()))
            onView(withId(R.id.btnSave)).check(matches(withText("Add")))
        }
    }

    @Test
    @DisplayName("Navigating to edit note screen if user notes are present")
    fun isNavigatingToNEditNoteScreen() {
        if (!validUser()) {
            fail("User not logged in")
        } else {
            val notesList = openActivity?.activity?.getFilteredNotesList()
            if (!notesList.isNullOrEmpty()) {
                onView(withId(R.id.rvAllNotes)).perform(
                    RecyclerViewActions.actionOnItemAtPosition<NotesAdapter.NoteViewHolder>(
                        0,
                        click()
                    )
                )

                val note = notesList[0]
                onView(withId(R.id.etTitle)).check(matches(isDisplayed()))
                onView(withId(R.id.imgNoteLabel)).check(matches(isDisplayed()))
                onView(withId(R.id.etMessage)).check(matches(isDisplayed()))
                onView(withId(R.id.btnSave)).check(matches(isDisplayed()))

                onView(withId(R.id.btnSave)).check(matches(withText("Save")))
                onView(withId(R.id.etTitle)).check(matches(withText(note.title)))
                onView(withId(R.id.etMessage)).check(matches(withText(note.message)))
            }
        }
    }

    @Test
    @DisplayName("Check Work label in the filtered results")
    fun isNotesOfWorkLabelOnly() {
        if (!validUser()) {
            fail("User not logged in")
        } else {
            onView(withId(R.id.drawerLayout)).check(matches(isClosed(Gravity.START)))
                .perform(DrawerActions.open())

            onView(withId(R.id.navView)).perform(NavigationViewActions.navigateTo(R.id.menuWorkNotes))

            val filteredNotesList = openActivity?.activity?.getFilteredNotesList()
            filteredNotesList?.let {
                for (singleNote in filteredNotesList) {
                    if (singleNote.label != BaseActivity.LABEL_WORK_INT) {
                        fail("Work Label not found")
                    }
                }
            }
        }
    }

    @Test
    @DisplayName("Check Self label in the filtered results")
    fun isNotesOfSelfLabelOnly() {
        if (!validUser()) {
            fail("User not logged in")
        } else {
            onView(withId(R.id.drawerLayout)).check(matches(isClosed(Gravity.START)))
                .perform(DrawerActions.open())

            onView(withId(R.id.navView)).perform(NavigationViewActions.navigateTo(R.id.menuSelfNotes))

            val filteredNotesList = openActivity?.activity?.getFilteredNotesList()
            filteredNotesList?.let {
                for (singleNote in filteredNotesList) {
                    if (singleNote.label != BaseActivity.LABEL_SELF_INT) {
                        fail("Self Label not found")
                    }
                }
            }
        }
    }

    @Test
    @DisplayName("Check Other label in the filtered results")
    fun isNotesOfOtherLabelOnly() {
        if (!validUser()) {
            fail("User not logged in")
        } else {
            onView(withId(R.id.drawerLayout)).check(matches(isClosed(Gravity.START)))
                .perform(DrawerActions.open())

            onView(withId(R.id.navView)).perform(NavigationViewActions.navigateTo(R.id.menuOtherNotes))

            val filteredNotesList = openActivity?.activity?.getFilteredNotesList()
            filteredNotesList?.let {
                for (singleNote in filteredNotesList) {
                    if (singleNote.label != BaseActivity.LABEL_OTHER_INT) {
                        fail("Other Label not found")
                    }
                }
            }
        }
    }

    @Test
    @DisplayName("Logging out user")
    fun isLoggingOutWorking() {
        if (!validUser()) {
            fail("User not logged in")
        } else {
            onView(withId(R.id.drawerLayout)).check(matches(isClosed(Gravity.START)))
                .perform(DrawerActions.open())

            onView(withId(R.id.navView)).perform(NavigationViewActions.navigateTo(R.id.menuLogout))
            onView(withId(R.id.container)).check(matches(isDisplayed()))
            onView(withId(R.id.tvWelcome)).check(matches(isDisplayed()))
            onView(withId(R.id.tieEmailID)).check(matches(isDisplayed()))
            onView(withId(R.id.btnLogin)).check(matches(isDisplayed()))
        }
    }

}