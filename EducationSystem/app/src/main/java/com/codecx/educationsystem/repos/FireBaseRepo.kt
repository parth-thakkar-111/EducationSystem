package com.codecx.educationsystem.repos

import android.content.Context
import android.net.Uri
import com.codecx.educationsystem.dataclasses.*
import com.codecx.educationsystem.enums.AccountType
import com.codecx.educationsystem.utils.FireBaseRefrences
import com.codecx.educationsystem.utils.UserDataHolder
import com.codecx.zipunzip.interfaces.ResultCallBacks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.io.File
import java.lang.Exception

class FireBaseRepo(
    val context: Context,
    val fireBaseRefrences: FireBaseRefrences,
    val firebaseAuth: FirebaseAuth,
    val userDataHolder: UserDataHolder
) {
    fun getUserResponse(result: ResultCallBacks<UserDataClass>) {
        fireBaseRefrences.userRef.child(firebaseAuth.currentUser?.uid!!)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    try {
                        val value = snapshot.getValue(UserDataClass::class.java) as UserDataClass
                        result.onSuccess(value)
                    } catch (ex: Exception) {
                        result.onFail(ex)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })

    }

    fun createAccount(userDataClass: UserDataClass, resultCallBacks: ResultCallBacks<String>) {
        firebaseAuth.createUserWithEmailAndPassword(userDataClass.email!!, userDataClass.password!!)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    userDataClass.uId = it.result.user?.uid
                    fireBaseRefrences.userRef.child(userDataClass.uId!!).setValue(userDataClass)
                        .addOnCompleteListener { refTask ->
                            if (refTask.isSuccessful) {
                                resultCallBacks.onSuccess("Account create Successful")

                            } else {
                                resultCallBacks.onFail(Exception("Fail to add data"))

                            }
                        }.addOnFailureListener {
                            resultCallBacks.onFail(it)
                        }
                } else {
                    resultCallBacks.onFail(Exception("Fail to create account"))
                }
            }.addOnFailureListener {
                resultCallBacks.onFail(it)
            }
    }

    fun loadCourses(resultCallBacks: ResultCallBacks<List<CoursesDataClass>>) {

        fireBaseRefrences.coursesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list: MutableList<CoursesDataClass> = mutableListOf()
                snapshot.children.forEach {
                    list.add(it.getValue(CoursesDataClass::class.java) as CoursesDataClass)
                }
                if (userDataHolder.isLearner()) {
                    resultCallBacks.onSuccess(list)
                } else {
                    resultCallBacks.onSuccess(list.filter { it.educatorId == userDataHolder.getUserData()?.uId })
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    fun addCourse(dd: CoursesDataClass, result: ResultCallBacks<CoursesDataClass>) {
        val pushId = fireBaseRefrences.coursesRef.push().key
        dd.courseUid = pushId
        dd.educatorId=firebaseAuth.currentUser?.uid
        fireBaseRefrences.coursesRef.child(pushId ?: "").setValue(dd).addOnCompleteListener {
            if (it.isSuccessful) {
                result.onSuccess(dd)
            } else {
                result.onFail(Exception("Fail to create"))
            }
        }.addOnFailureListener {
            result.onFail(it)
        }
    }

    fun loadTaskes(courseUid: String?, resultCallBacks: ResultCallBacks<List<TaskDataClass>>) {
        fireBaseRefrences.taskRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list: MutableList<TaskDataClass> = mutableListOf()
                snapshot.children.forEach {
                    list.add(it.getValue(TaskDataClass::class.java) as TaskDataClass)
                }
                val filterList = list.filter { it.taskCourse?.courseUid == courseUid }
                resultCallBacks.onSuccess(filterList)
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    fun addTaskIntoCourse(
        dd: TaskDataClass,
        taskImage: Uri,
        resultCallBacks: ResultCallBacks<TaskDataClass>
    ) {
        val pushId = fireBaseRefrences.taskRef.push().key
        dd.taskId = pushId
        if (taskImage == Uri.EMPTY) {
            addTaskIntoDb(dd, resultCallBacks)
        } else {
            val storageParentPath = "(${dd.taskCourse?.courseName}) ${dd.taskCourse?.courseName}"
            val fileName = "(${dd.taskId}) ${dd.taskName}"
            fireBaseRefrences.taskStorageRef.child(storageParentPath).child(fileName)
                .putFile(taskImage).addOnSuccessListener {
                    it.storage.downloadUrl.addOnCompleteListener { imageUrl ->
                        if (imageUrl.isSuccessful) {
                            dd.taskImageUrl = imageUrl.result.toString()
                            addTaskIntoDb(dd, resultCallBacks)
                        } else {
                            resultCallBacks.onFail(Exception())
                        }
                    }.addOnFailureListener {
                        resultCallBacks.onFail(it)
                    }
                }.addOnFailureListener {
                    resultCallBacks.onFail(it)
                }
        }
    }

    fun addTaskIntoDb(dd: TaskDataClass, resultCallBacks: ResultCallBacks<TaskDataClass>) {
        fireBaseRefrences.taskRef.child(dd.taskId!!).setValue(dd).addOnCompleteListener {
            if (it.isSuccessful) {
                resultCallBacks.onSuccess(dd)
            } else {
                resultCallBacks.onFail(Exception("Fail to add"))
            }
        }.addOnFailureListener {
            resultCallBacks.onFail(it)
        }
    }

    fun loadQuiz(taskId: String?, resultCallBacks: ResultCallBacks<List<QuizDataClass>>) {
        fireBaseRefrences.quizRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list: MutableList<QuizDataClass> = mutableListOf()
                snapshot.children.forEach {
                    list.add(it.getValue(QuizDataClass::class.java) as QuizDataClass)
                }
                if (list.isEmpty()) {
                    resultCallBacks.onSuccess(listOf())
                } else {
                    resultCallBacks.onSuccess(list.filter { it.task?.taskId == taskId })
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    fun loadResults(resultCallBacks: ResultCallBacks<List<ScoreDataClass>>) {
        fireBaseRefrences.scoreRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list: MutableList<ScoreDataClass> = mutableListOf()
                snapshot.children.forEach {
                    list.add(it.getValue(ScoreDataClass::class.java) as ScoreDataClass)
                }
                resultCallBacks.onSuccess(list)
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    fun createQuiz(
        quizData: QuizDataClass,
        selectedFileUri: Uri,
        resultCallBacks: ResultCallBacks<QuizDataClass>
    ) {
        val pushId = fireBaseRefrences.quizRef.push().key
        quizData.quizId = pushId
        if (selectedFileUri == Uri.EMPTY) {
            addQuizIntoDb(quizData, resultCallBacks)
        } else {
            val storageParentPath =
                "(${quizData.task?.taskCourse?.courseUid}) ${quizData.task?.taskCourse?.courseName}"
            // val fileName = "${File(selectedFileUri.toString()).name}"
            val fileName = "${System.currentTimeMillis()}"
            fireBaseRefrences.quizStorageRef.child(storageParentPath).child(fileName)
                .putFile(selectedFileUri).addOnSuccessListener {
                    it.storage.downloadUrl.addOnCompleteListener { fileUrl ->
                        if (fileUrl.isSuccessful) {
                            quizData.fileUrl = fileUrl.result.toString()
                            addQuizIntoDb(quizData, resultCallBacks)
                        } else {
                            resultCallBacks.onFail(Exception())
                        }
                    }.addOnFailureListener {
                        resultCallBacks.onFail(it)
                    }
                }.addOnFailureListener {
                    resultCallBacks.onFail(it)
                }
        }
    }

    fun addQuizIntoDb(dd: QuizDataClass, resultCallBacks: ResultCallBacks<QuizDataClass>) {
        fireBaseRefrences.quizRef.child(dd.quizId!!).setValue(dd).addOnCompleteListener {
            if (it.isSuccessful) {
                resultCallBacks.onSuccess(dd)
            } else {
                resultCallBacks.onFail(Exception("Fail to add"))
            }
        }.addOnFailureListener {
            resultCallBacks.onFail(it)
        }
    }

    fun deleteQuiz(it: QuizDataClass) {
        fireBaseRefrences.quizRef.child(it.quizId!!).removeValue()
    }

    fun uploadResult(obj: ScoreDataClass) {
        val uid = fireBaseRefrences.scoreRef.push().key
        obj.scoreId = uid
        fireBaseRefrences.scoreRef.child(uid!!).setValue(obj)
    }

    fun loadLearners(
        resultCallBacks: ResultCallBacks<List<UserDataClass>>
    ) {
        fireBaseRefrences.userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list: MutableList<UserDataClass> = mutableListOf()
                snapshot.children.forEach {
                    list.add(it.getValue(UserDataClass::class.java) as UserDataClass)
                }
                val fil = list.filter { it.userType == AccountType.Learner.name }
                resultCallBacks.onSuccess(fil)
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    fun loadAssignedLearners(
        courseUid: String,
        resultCallBacks: ResultCallBacks<List<AssignedDataClass>>
    ) {
        fireBaseRefrences.assignedRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list: MutableList<AssignedDataClass> = mutableListOf()
                snapshot.children.forEach {
                    list.add(it.getValue(AssignedDataClass::class.java) as AssignedDataClass)
                }
                val fil = list.filter { it.course?.courseUid == courseUid }
                resultCallBacks.onSuccess(fil)
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    fun addLearnerIntoAssignCourse(
        assignedDataClass: AssignedDataClass,
        resultCallBacks: ResultCallBacks<AssignedDataClass>
    ) {
        val pushKey = fireBaseRefrences.assignedRef.push().key
        assignedDataClass.uid = pushKey
        fireBaseRefrences.assignedRef.child(pushKey!!).setValue(assignedDataClass)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    resultCallBacks.onSuccess(assignedDataClass)
                } else {
                    resultCallBacks.onFail(Exception("Fail to add"))
                }
            }.addOnFailureListener {
                resultCallBacks.onFail(it)
            }
    }

    fun deleteAssignedLearner(value: AssignedDataClass) {
        fireBaseRefrences.assignedRef.child(value.uid!!).removeValue()
    }

    fun loadRequests(courseUid: String, resultCallBacks: ResultCallBacks<List<RequestDataClass>>) {
        fireBaseRefrences.requestsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list: MutableList<RequestDataClass> = mutableListOf()
                snapshot.children.forEach {
                    list.add(it.getValue(RequestDataClass::class.java) as RequestDataClass)
                }
                val fil = list.filter { it.course?.courseUid == courseUid }
                resultCallBacks.onSuccess(fil)
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    fun deleteRequest(value: RequestDataClass) {
        fireBaseRefrences.requestsRef.child(value.uid!!).removeValue()
    }

    fun loadAssignedCourses(resultCallBacks: ResultCallBacks<List<CoursesDataClass>>) {
        fireBaseRefrences.assignedRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list: MutableList<AssignedDataClass> = mutableListOf()
                snapshot.children.forEach {
                    list.add(it.getValue(AssignedDataClass::class.java) as AssignedDataClass)
                }
                val fil = list.filter { it.user?.uId == firebaseAuth.currentUser?.uid }
                val courseList = ArrayList<CoursesDataClass>()
                if (fil.isNotEmpty()) {
                    fil.forEach {
                        courseList.add(it.course ?: CoursesDataClass())
                    }
                }
                resultCallBacks.onSuccess(courseList)
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    fun submitRequest(
        requestDataClass: RequestDataClass,
        resultCallBacks: ResultCallBacks<RequestDataClass>
    ) {
        val pushKey = fireBaseRefrences.requestsRef.push().key
        requestDataClass.uid = pushKey
        fireBaseRefrences.requestsRef.child(pushKey!!).setValue(requestDataClass)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    resultCallBacks.onSuccess(requestDataClass)
                } else {
                    resultCallBacks.onFail(Exception("Fail to add"))
                }
            }.addOnFailureListener {
                resultCallBacks.onFail(it)
            }
    }

}