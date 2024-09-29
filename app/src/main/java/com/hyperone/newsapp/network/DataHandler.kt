package com.paymob.moviesapp.network

sealed class DataHandler<out T> {
    object ShowLoading : DataHandler<Nothing>()
    object HideLoading : DataHandler<Nothing>()
    data class Success<out T>(val data: T) : DataHandler<T>()
    data class Error(val message: String) : DataHandler<Nothing>()
    data class ServerError(val message: String) : DataHandler<Nothing>()
}