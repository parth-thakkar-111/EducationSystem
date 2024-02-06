package com.codecx.educationsystem.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codecx.educationsystem.dataclasses.ResultDataClass
import com.codecx.educationsystem.dataclasses.ScoreDataClass
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SingleSharedViewModel : ViewModel() {
    private var answeredQuizList: MutableList<ResultDataClass> = mutableListOf()

    private val _result: MutableLiveData<List<ResultDataClass>> = MutableLiveData()
    var result: LiveData<List<ResultDataClass>> = _result

    fun addQuizIntoAnsweredList(value: ResultDataClass) {
        answeredQuizList.add(value)
    }

    fun loadResult() = viewModelScope.launch(Dispatchers.IO) {
        _result.postValue(answeredQuizList)
      //  answeredQuizList.clear()
    }



}