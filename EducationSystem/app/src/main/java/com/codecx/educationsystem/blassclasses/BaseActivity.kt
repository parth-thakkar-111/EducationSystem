package com.codecx.educationsystem.blassclasses

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.codecx.educationsystem.sealdclasses.ProgressStates
import com.codecx.educationsystem.utils.DialogUtil
import com.codecx.educationsystem.utils.FireBaseRefrences
import com.codecx.educationsystem.utils.UserDataHolder
import com.codecx.educationsystem.viewmodels.MainViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.collectLatest
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

open class BaseActivity : AppCompatActivity() {
    protected val firebaseAuth:FirebaseAuth by inject()
    protected val mViewModel:MainViewModel by viewModel()
    protected val mDataHolder:UserDataHolder by inject()
    protected val fireBaseRefrences:FireBaseRefrences by inject()
    protected var mDialogUtil:DialogUtil?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        mDialogUtil=DialogUtil(this)
        lifecycleScope.launchWhenStarted {
            mViewModel.progress.collectLatest {
                when (it) {
                    is ProgressStates.InitialState -> {}
                    is ProgressStates.CloseProgress -> {
                        mDialogUtil?.dismissDialog()
                    }
                    is ProgressStates.ShowProgress -> {
                        if (mDialogUtil?.isShowing == false) {
                            mDialogUtil?.showProgressDialog(it.data,false)
                        }
                    }
                }
            }
        }

    }
}