package com.codecx.educationsystem.blassclasses


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codecx.educationsystem.dataclasses.ProgressDataClass
import com.codecx.educationsystem.sealdclasses.DialogStates
import com.codecx.educationsystem.sealdclasses.ProgressStates
import kotlinx.coroutines.delay

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

open class BaseViewModel : ViewModel() {
    private val _progress: MutableStateFlow<ProgressStates> =
        MutableStateFlow(ProgressStates.InitialState)
    val progress = _progress.asStateFlow()

    fun showProgress(data: ProgressDataClass) = viewModelScope.launch {
        _progress.value = ProgressStates.ShowProgress(data)
    }

    fun closeProgress() = viewModelScope.launch {
        _progress.value = ProgressStates.CloseProgress
        delay(500L)
        _progress.value = ProgressStates.InitialState
    }

    private val _dialog: MutableStateFlow<DialogStates> =
        MutableStateFlow(DialogStates.InitialState)
    val dialog = _dialog.asStateFlow()

    fun showDialog(data: Any) {
        _dialog.value = DialogStates.ShowDialog(data)
    }

    fun closeDialog() {
        _dialog.value = DialogStates.CloseDialog
        _dialog.value = DialogStates.InitialState
    }
}