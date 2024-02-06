package com.codecx.educationsystem.dataclasses

data class ResultDataClass(var resultId: String? = null) {
    var taskId: String? = null
    var quizQuestion: String? = null
    var selectedOption: String? = null
    var isCorrect: Boolean = false
}