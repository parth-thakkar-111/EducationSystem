package com.codecx.educationsystem.utils

import android.content.Context
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class FireBaseRefrences(val context: Context) {
    val userRef=FirebaseDatabase.getInstance().getReference("users")
    val coursesRef=FirebaseDatabase.getInstance().getReference("courses")
    val taskRef=FirebaseDatabase.getInstance().getReference("tasks")
    val quizRef=FirebaseDatabase.getInstance().getReference("quiz")
    val assignedRef=FirebaseDatabase.getInstance().getReference("assigned")
    val requestsRef=FirebaseDatabase.getInstance().getReference("requests")
    val scoreRef=FirebaseDatabase.getInstance().getReference("score")
    val taskStorageRef=FirebaseStorage.getInstance().getReference("tasksImages")
    val quizStorageRef=FirebaseStorage.getInstance().getReference("quizMediaFiles")
}