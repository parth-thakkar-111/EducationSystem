package com.codecx.educationsystem.dataclasses

data class RequestDataClass(var uid: String? = null) {
    var course: CoursesDataClass? = null
    var user: UserDataClass? = null
}