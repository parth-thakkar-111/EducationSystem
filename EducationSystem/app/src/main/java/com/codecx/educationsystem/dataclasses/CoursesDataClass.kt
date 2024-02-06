package com.codecx.educationsystem.dataclasses

data class CoursesDataClass(var courseName: String? = null, var courseDescription: String? = null) {
    var courseUid: String? = null
    var educatorId: String? = null
}