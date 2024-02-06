package com.codecx.educationsystem.dataclasses

data class ProgressDataClass(
    var message: String,
    var progress: Int = 0,
    var isIndeterminate: Boolean = false
)