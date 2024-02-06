package com.codecx.educationsystem.sealdclasses

sealed class DataState {
object Initial: DataState()
object Loading: DataState()
class Error(val exception: Exception): DataState()
class Result<T>(val result:T): DataState()
}