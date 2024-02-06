package com.codecx.educationsystem.utils

import android.app.Dialog
import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.codecx.educationsystem.R

import com.google.android.material.bottomsheet.BottomSheetDialog

class BottomDialogUtil(val mContext: Context) : BottomSheetDialog(mContext, R.style.CustomBottomSheetDialogTheme) {

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