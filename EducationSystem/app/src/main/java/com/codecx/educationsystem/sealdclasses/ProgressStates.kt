package com.codecx.educationsystem.sealdclasses

import com.codecx.educationsystem.dataclasses.ProgressDataClass


sealed class ProgressStates {
    object InitialState: ProgressStates()
    class ShowProgress(val data: ProgressDataClass): ProgressStates()
    object CloseProgress: ProgressStates()
}