package com.wheelseye.notesapp.login.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.wheelseye.notesapp.base.activity.BaseActivity
import com.wheelseye.notesapp.R
import com.wheelseye.notesapp.home.view.activity.HomeActivity
import com.wheelseye.notesapp.login.viewmodel.LoginViewModel
import com.wheelseye.notesapp.utility.Utility
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.progress_bar_fullscreen.*

class LoginActivity : BaseActivity() {

    private var mLoginViewModel: LoginViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.hide()

        val sharedPref = getSharedPreferences(USER_DETAILS_SHARED_PREF, Context.MODE_PRIVATE)
        if (sharedPref.contains(USER_ID) && sharedPref.getLong(USER_ID, -1) != (-1).toLong()) {
            moveToHomeScreen()
        }


        initViewModel()
        setObservers()
        initListeners()
    }

    private fun initListeners() {
        btnLogin.setOnClickListener {
            hideKeyboard()
            val emailID = tieEmailID.text.toString().trim()

            if (Utility.isEmailValid(emailID))
                mLoginViewModel?.performLogin(this, emailID)
            else
                Snackbar.make(
                    container,
                    resources.getString(R.string.invalid_email_id),
                    Snackbar.LENGTH_LONG
                ).show()
        }
    }

    override fun initViewModel() {
        mLoginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
    }

    override fun setObservers() {
        mLoginViewModel?.isUpdating()?.observe(this, Observer {
            if (it.first) {
                pbLayout.visibility = View.VISIBLE
                pbText.text = it.second
            } else {
                pbLayout.visibility = View.GONE
            }
        })

        mLoginViewModel?.isLoginSuccessful()?.observe(this, Observer {
            if (!it) {
                Snackbar.make(
                    container,
                    resources.getString(R.string.internal_error_occurred),
                    Snackbar.LENGTH_LONG
                ).show()
            } else {
                startWorkManager(
                    getSharedPreferences(
                        USER_DETAILS_SHARED_PREF,
                        Context.MODE_PRIVATE
                    ).getLong(USER_ID, 1).toString()
                )
                moveToHomeScreen()
            }
        })
    }

    private fun moveToHomeScreen() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}