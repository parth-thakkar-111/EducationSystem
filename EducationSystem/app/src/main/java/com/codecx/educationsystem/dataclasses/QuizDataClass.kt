package com.codecx.educationsystem.dataclasses


data class QuizDataClass(var quizId: String? = null) {
    var task: TaskDataClass? = null
    var quizTitle: String? = null
    var quizQuestion: String? = null
    var fileUrl: String? = null
    var fileMimeType: String? = null
    var quizOptions: HashMap<String, OptionsDataClass> = HashMap()

}
