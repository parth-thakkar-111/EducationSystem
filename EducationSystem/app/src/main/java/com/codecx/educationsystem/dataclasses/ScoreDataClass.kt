package com.codecx.educationsystem.dataclasses

data class ScoreDataClass(var scoreId: String? = null) {
    var score: Int = 0
    var userDataClass: UserDataClass? = null
    var task: TaskDataClass? = null
    var answeredQuiz: HashMap<String, ResultDataClass> = HashMap()
}