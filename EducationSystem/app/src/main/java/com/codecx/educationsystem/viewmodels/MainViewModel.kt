package com.codecx.educationsystem.viewmodels

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.codecx.educationsystem.blassclasses.BaseViewModel
import com.codecx.educationsystem.dataclasses.*
import com.codecx.educationsystem.repos.FireBaseRepo
import com.codecx.educationsystem.sealdclasses.DataState
import com.codecx.zipunzip.interfaces.ResultCallBacks
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception

class MainViewModel(val fireBaseRepo: FireBaseRepo) : BaseViewModel() {
    private val _courses: MutableStateFlow<DataState> = MutableStateFlow(DataState.Initial)
    val courses = _courses.asStateFlow()

    private val _coursesForRequest: MutableLiveData<List<CoursesDataClass>> = MutableLiveData()
    val coursesForRequest: LiveData<List<CoursesDataClass>> = _coursesForRequest

    private val _tasks: MutableLiveData<List<TaskDataClass>> = MutableLiveData()
    val tasks: LiveData<List<TaskDataClass>> = _tasks

    private val _quiz: MutableLiveData<List<QuizDataClass>> = MutableLiveData()
    val quiz: LiveData<List<QuizDataClass>> = _quiz

    private val _currentQuiz: MutableLiveData<QuizDataClass> = MutableLiveData()
    val currentQuiz: LiveData<QuizDataClass> = _currentQuiz

    private val _result: MutableLiveData<List<ScoreDataClass>> = MutableLiveData()
    val result: LiveData<List<ScoreDataClass>> = _result

    var quizNo: Int = -1


    private val _assignedLearners: MutableLiveData<List<AssignedDataClass>> = MutableLiveData()
    val assignedLearners: LiveData<List<AssignedDataClass>> = _assignedLearners

    private val _requestedLearners: MutableLiveData<List<RequestDataClass>> = MutableLiveData()
    val requestedLearners: LiveData<List<RequestDataClass>> = _requestedLearners

    var mLearners: List<UserDataClass> = ArrayList()


    fun requestForUserInfo(result: ResultCallBacks<UserDataClass>) = viewModelScope.launch {
        try {
            fireBaseRepo.getUserResponse(result)
        } catch (ex: java.lang.Exception) {
            result.onFail(ex)
        }
    }

    fun signUpUser(userDataClass: UserDataClass, resultCallBacks: ResultCallBacks<String>) =
        viewModelScope.launch(Dispatchers.IO) {
            try {
                fireBaseRepo.createAccount(userDataClass, resultCallBacks)
            } catch (ex: java.lang.Exception) {
                resultCallBacks.onFail(ex)
            }
        }

    fun loadCourses() = viewModelScope.launch(Dispatchers.IO) {
        try {
            _courses.value = DataState.Loading
            fireBaseRepo.loadCourses(object : ResultCallBacks<List<CoursesDataClass>> {
                override fun onFail(message: Exception) {
                    _courses.value = DataState.Error(message)
                }

                override fun onSuccess(result: List<CoursesDataClass>) {
                    _courses.value = DataState.Result(result)
                }

            })
        } catch (ex: java.lang.Exception) {
            _courses.value = DataState.Error(ex)
        }

    }

    fun loadCoursesForRequest() = viewModelScope.launch(Dispatchers.IO) {
        try {
            fireBaseRepo.loadCourses(object : ResultCallBacks<List<CoursesDataClass>> {
                override fun onFail(message: Exception) {
                    _coursesForRequest.postValue(listOf())
                }

                override fun onSuccess(result: List<CoursesDataClass>) {
                    if (_courses.value is DataState.Result<*>) {
                        val list =
                            ((_courses.value as DataState.Result<*>).result as List<CoursesDataClass>)
                        if (list.isEmpty()) {
                            _coursesForRequest.postValue(result)
                        } else {
                            val newList = ArrayList<CoursesDataClass>()
                            result.forEach {
                                if (!list.contains(it)) {
                                    newList.add(it)
                                }
                            }

                            _coursesForRequest.postValue(newList)
                        }
                    } else {
                        _coursesForRequest.postValue(result)
                    }
                }

            })
        } catch (ex: java.lang.Exception) {
            _coursesForRequest.postValue(listOf())
        }

    }

    fun loadAssignedCourses() = viewModelScope.launch(Dispatchers.IO) {
        try {
            _courses.value = DataState.Loading
            fireBaseRepo.loadAssignedCourses(object : ResultCallBacks<List<CoursesDataClass>> {
                override fun onFail(message: Exception) {
                    _courses.value = DataState.Error(message)
                }

                override fun onSuccess(result: List<CoursesDataClass>) {
                    _courses.value = DataState.Result(result)
                }

            })
        } catch (ex: java.lang.Exception) {
            _courses.value = DataState.Error(ex)
        }

    }

    fun addCourse(dd: CoursesDataClass, result: ResultCallBacks<CoursesDataClass>) =
        viewModelScope.launch {
            try {
//                showProgress(ProgressDataClass("Creating course", 0, false))
                withContext(Dispatchers.IO) {

                    fireBaseRepo.addCourse(dd, result)
                }
            } catch (ex: Exception) {
                result.onFail(ex)
            }
        }

    fun loadTasks(courseUid: String?) = viewModelScope.launch(Dispatchers.IO) {
        try {
            fireBaseRepo.loadTaskes(courseUid, object : ResultCallBacks<List<TaskDataClass>> {
                override fun onFail(message: Exception) {
                    _tasks.postValue(listOf())
                }

                override fun onSuccess(result: List<TaskDataClass>) {
                    _tasks.postValue(result)
                }

            })
        } catch (ex: java.lang.Exception) {
            _tasks.postValue(listOf())
        }

    }

    fun addTask(
        dd: TaskDataClass,
        taskImage: Uri,
        resultCallBacks: ResultCallBacks<TaskDataClass>
    ) = viewModelScope.launch(Dispatchers.IO) {
        try {
            showProgress(ProgressDataClass("Creating Task", 0, true))
            delay(1000L)
            withContext(Dispatchers.IO) {

                fireBaseRepo.addTaskIntoCourse(dd, taskImage, resultCallBacks)

            }
        } catch (ex: Exception) {
            resultCallBacks.onFail(ex)
        }
    }

    fun loadQuiz(taskId: String?) = viewModelScope.launch(Dispatchers.IO) {
        try {
            fireBaseRepo.loadQuiz(taskId, object : ResultCallBacks<List<QuizDataClass>> {
                override fun onFail(message: Exception) {
                    _quiz.postValue(listOf())
                }

                override fun onSuccess(result: List<QuizDataClass>) {
                    _quiz.postValue(result)
                }

            })
        } catch (ex: Exception) {
            _quiz.postValue(listOf())
        }
    }

    fun addQuiz(
        quizData: QuizDataClass,
        selectedFileUri: Uri,
        resultCallBacks: ResultCallBacks<QuizDataClass>
    ) = viewModelScope.launch(Dispatchers.IO) {
        try {
            showProgress(ProgressDataClass("Creating quiz...", 0, true))
            fireBaseRepo.createQuiz(quizData, selectedFileUri, resultCallBacks)
        } catch (ex: Exception) {
            resultCallBacks.onFail(ex)
        }

    }

    fun deleteQuiz(it: QuizDataClass) = viewModelScope.launch {
        try {
            fireBaseRepo.deleteQuiz(it)
        } catch (ex: Exception) {

        }
    }

    fun submitCurrentQuiz(value: QuizDataClass) = viewModelScope.launch(Dispatchers.IO) {
        _currentQuiz.postValue(value)
    }

    fun uploadResultInfo(obj: ScoreDataClass) = viewModelScope.launch(Dispatchers.IO) {
        fireBaseRepo.uploadResult(obj)
    }

    fun loadResults() = viewModelScope.launch(Dispatchers.IO) {
        fireBaseRepo.loadResults(object : ResultCallBacks<List<ScoreDataClass>> {
            override fun onFail(message: Exception) {
                _result.postValue(listOf())
            }

            override fun onSuccess(result: List<ScoreDataClass>) {
                _result.postValue(result)
            }

        })
    }

    fun loadAssignedLearners(courseUid: String?) = viewModelScope.launch(Dispatchers.IO) {
        fireBaseRepo.loadAssignedLearners(courseUid!!,
            object : ResultCallBacks<List<AssignedDataClass>> {
                override fun onFail(message: Exception) {
                    _assignedLearners.postValue(listOf())
                }

                override fun onSuccess(result: List<AssignedDataClass>) {
                    _assignedLearners.postValue(result)
                }

            })
    }

    fun loadLearners() = viewModelScope.launch(Dispatchers.IO) {
        fireBaseRepo.loadLearners(object : ResultCallBacks<List<UserDataClass>> {
            override fun onFail(message: Exception) {
                mLearners = listOf()
            }

            override fun onSuccess(result: List<UserDataClass>) {
                mLearners = result
            }

        })
    }

    fun addLearnerIntoAssignedCourse(
        assignDataClass: AssignedDataClass,
        resultCallBacks: ResultCallBacks<AssignedDataClass>
    ) = viewModelScope.launch(Dispatchers.IO) {
        fireBaseRepo.addLearnerIntoAssignCourse(assignDataClass, resultCallBacks)
    }

    fun deleteAssginedLearner(value: AssignedDataClass) = viewModelScope.launch(Dispatchers.IO)
    {
        fireBaseRepo.deleteAssignedLearner(value)

    }

    fun loadRequested(courseUid: String?) = viewModelScope.launch(Dispatchers.IO) {
        fireBaseRepo.loadRequests(courseUid!!,
            object : ResultCallBacks<List<RequestDataClass>> {
                override fun onFail(message: Exception) {
                    _requestedLearners.postValue(listOf())
                }

                override fun onSuccess(result: List<RequestDataClass>) {
                    _requestedLearners.postValue(result)
                }

            })
    }

    fun deleteRequest(value: RequestDataClass) {
        fireBaseRepo.deleteRequest(value)
    }

    fun submitRequest(
        requestDataClass: RequestDataClass,
        resultCallBacks: ResultCallBacks<RequestDataClass>
    ) = viewModelScope.launch(Dispatchers.IO) {
        fireBaseRepo.submitRequest(requestDataClass, resultCallBacks)
    }


}