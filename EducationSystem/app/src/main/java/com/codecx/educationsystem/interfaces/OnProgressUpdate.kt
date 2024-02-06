package com.codecx.zipunzip.interfaces

interface OnProgressUpdate<T> {
    fun updateProgress(value:T)
}