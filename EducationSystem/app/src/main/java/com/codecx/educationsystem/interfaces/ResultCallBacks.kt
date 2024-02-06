package com.codecx.zipunzip.interfaces

interface ResultCallBacks<T> {
    fun onFail(message:java.lang.Exception)
    fun onSuccess(result:T)
}