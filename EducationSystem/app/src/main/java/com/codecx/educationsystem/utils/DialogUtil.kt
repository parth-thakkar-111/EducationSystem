package com.codecx.educationsystem.utils

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.codecx.educationsystem.databinding.ProgressdialogBinding
import com.codecx.educationsystem.dataclasses.ProgressDataClass

class DialogUtil(val mContext: Context) : Dialog(mContext) {

    private val mProgressView: ProgressdialogBinding by lazy {
        ProgressdialogBinding.inflate(
            LayoutInflater.from(context)
        )
    }

    fun showProgressDialog(data: ProgressDataClass, isCancelable: Boolean = true) {
        mProgressView.mProgress.isIndeterminate = data.isIndeterminate
        if (!data.isIndeterminate) {
            mProgressView.mProgress.progress = data.progress
        }
        mProgressView.tvMessage.text = data.message
        setContentView(mProgressView.root)
        setCancelable(isCancelable)
        setupDialogView()
        show()
    }

    fun showDialog(view: View, isCancelable: Boolean = true) {
        setContentView(view)
        setCancelable(isCancelable)
        setupDialogView()
        show()
    }

    private fun setupDialogView() {
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        window?.setBackgroundDrawable(
            ContextCompat.getDrawable(
                mContext,
                android.R.color.transparent
            )
        )
    }

    fun dismissDialog() {
        if (isShowing) {
            dismiss()
        }
    }

}