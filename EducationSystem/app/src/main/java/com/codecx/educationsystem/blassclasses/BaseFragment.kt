package com.codecx.educationsystem.blassclasses

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.codecx.educationsystem.dataclasses.ProgressDataClass
import com.codecx.educationsystem.dataclasses.UserDataClass
import com.codecx.educationsystem.sealdclasses.ProgressStates
import com.codecx.educationsystem.utils.DialogUtil
import com.codecx.educationsystem.utils.UserDataHolder
import com.codecx.educationsystem.viewmodels.MainViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.collectLatest
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

open class BaseFragment(@LayoutRes layoutId: Int) : Fragment(layoutId) {
    protected val mUserDataHolder: UserDataHolder by inject()
    protected val mSharedViewModel: MainViewModel by sharedViewModel()
    protected val mAuth: FirebaseAuth by inject()
    protected var mDialogUtil: DialogUtil? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mDialogUtil = DialogUtil(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            mSharedViewModel.progress.collectLatest {
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