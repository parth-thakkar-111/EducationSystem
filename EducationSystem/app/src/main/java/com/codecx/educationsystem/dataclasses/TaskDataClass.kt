package com.codecx.educationsystem.dataclasses

data class TaskDataClass(var taskName: String? = null) {
    var taskId: String? = null
    var taskCourse: CoursesDataClass? = null
    var taskImageUrl: String? = null
}