package com.wheelseye.notesapp

import com.wheelseye.notesapp.db.DatabaseTest
import com.wheelseye.notesapp.home.HomeActivityTest
import com.wheelseye.notesapp.login.LoginActivityTest
import org.junit.runner.RunWith
import org.junit.runners.Suite


@RunWith(Suite::class)
@Suite.SuiteClasses(
    DatabaseTest::class,
    LoginActivityTest::class,
    HomeActivityTest::class
)
class NotesTestSuite
