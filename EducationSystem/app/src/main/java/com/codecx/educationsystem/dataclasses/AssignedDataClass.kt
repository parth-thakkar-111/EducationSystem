package com.codecx.educationsystem.dataclasses

data class AssignedDataClass(var uid: String? = null) {
    var course: CoursesDataClass? = null
    var user: UserDataClass? = null
}