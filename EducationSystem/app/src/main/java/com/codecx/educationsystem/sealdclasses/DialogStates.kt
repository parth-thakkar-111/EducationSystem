package com.codecx.educationsystem.sealdclasses


sealed class DialogStates {
    object InitialState: DialogStates()
    class ShowDialog<T>(val data: T): DialogStates()
    object CloseDialog: DialogStates()
}