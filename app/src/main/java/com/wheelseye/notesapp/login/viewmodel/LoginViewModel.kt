package com.wheelseye.notesapp.login.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wheelseye.notesapp.base.activity.BaseActivity
import com.wheelseye.notesapp.login.model.callback.ILoginCallback
import com.wheelseye.notesapp.login.model.repository.LoginRepository

class LoginViewModel : ViewModel(),
    ILoginCallback {

    private val mIsUpdating = MutableLiveData<Pair<Boolean, String>>()
    private val mLoginRepository = LoginRepository()
    private val mIsLoginSuccessful = MutableLiveData<Boolean>()

    override fun updateLoginDetails(isSuccessful: Boolean) {
        mIsUpdating.postValue(Pair(BaseActivity.HIDE_LOADER, ""))
        mIsLoginSuccessful.postValue(isSuccessful)
    }

    fun performLogin(context: Context, emailID: String) {
        mIsUpdating.value = Pair(BaseActivity.SHOW_LOADER, BaseActivity.STRING_CHECK_CREDENTIALS)
        mLoginRepository.performLogin(emailID, context, this)
    }

    fun isUpdating(): LiveData<Pair<Boolean, String>> {
        return mIsUpdating
    }

    fun isLoginSuccessful(): LiveData<Boolean> {
        return mIsLoginSuccessful
    }

}